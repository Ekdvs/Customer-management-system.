package com.Customer.management.system.Customer.management.system.serviceimpl;
import com.Customer.management.system.Customer.management.system.DTO.BulkImportResultDTO;
import com.Customer.management.system.Customer.management.system.entiry.Customer;
import com.Customer.management.system.Customer.management.system.repository.CustomerRepository;
import com.Customer.management.system.Customer.management.system.service.BulkImportService;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;


@Service
public class BulkImportServiceImpl implements BulkImportService {

    private static final Logger log = LoggerFactory.getLogger(BulkImportServiceImpl.class);

    private static final int BATCH_SIZE = 500;
    private static final int MAX_ERRORS_REPORTED = 500;

    private static final DateTimeFormatter[] DATE_FORMATTERS = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
    };

    private final CustomerRepository customerRepository;

    @Autowired
    public BulkImportServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    // ----------------------------------------------------------------
    // SYNCHRONOUS IMPORT
    // ----------------------------------------------------------------
    @Override
    public BulkImportResultDTO importExcel(MultipartFile file) {
        long start = System.currentTimeMillis();
        BulkImportResultDTO result = new BulkImportResultDTO();

        validateFile(file);

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            processSheet(sheet, result);

        } catch (Exception e) {
            log.error("Bulk import failed", e);
            result.setStatus("FAILED");
            result.addError("Fatal error: " + e.getMessage());
        }

        result.setProcessingTimeMs(System.currentTimeMillis() - start);
        if (result.getStatus() == null) {
            result.setStatus(result.getFailureCount() == 0 ? "SUCCESS" : "PARTIAL_SUCCESS");
        }
        log.info("Bulk import completed: {} success, {} failed in {}ms",
                result.getSuccessCount(), result.getFailureCount(), result.getProcessingTimeMs());
        return result;
    }

    // ----------------------------------------------------------------
    // ASYNCHRONOUS IMPORT
    // ----------------------------------------------------------------
    @Override
    @Async("bulkImportExecutor")
    public CompletableFuture<BulkImportResultDTO> importExcelAsync(MultipartFile file) {
        BulkImportResultDTO result = importExcel(file);
        return CompletableFuture.completedFuture(result);
    }

    // ----------------------------------------------------------------
    // CORE PROCESSING LOGIC
    // ----------------------------------------------------------------

    private void processSheet(Sheet sheet, BulkImportResultDTO result) {
        List<Customer> batch = new ArrayList<>(BATCH_SIZE);

        // Track NICs seen in this file to skip file-level duplicates
        Set<String> seenNics = new HashSet<>();

        int totalRows = 0;
        int rowNum = 0;

        for (Row row : sheet) {
            rowNum++;
            if (rowNum == 1) continue; // Skip header row

            totalRows++;

            try {
                String name = getCellStringValue(row, 0);
                String dobStr = getCellStringValue(row, 1);
                String nic = getCellStringValue(row, 2);

                // --- Validate mandatory fields ---
                List<String> validationErrors = new ArrayList<>();

                if (name == null || name.trim().isEmpty()) {
                    validationErrors.add("Name is missing");
                }
                if (dobStr == null || dobStr.trim().isEmpty()) {
                    validationErrors.add("Date of birth is missing");
                }
                if (nic == null || nic.trim().isEmpty()) {
                    validationErrors.add("NIC is missing");
                }

                if (!validationErrors.isEmpty()) {
                    addRowError(result, rowNum, String.join("; ", validationErrors));
                    continue;
                }

                name = name.trim();
                nic = nic.trim();
                dobStr = dobStr.trim();

                // --- Parse date ---
                LocalDate dob = parseDate(dobStr);
                if (dob == null) {
                    addRowError(result, rowNum, "Invalid date format: '" + dobStr + "'. Expected: yyyy-MM-dd");
                    continue;
                }

                // --- Check in-file duplicate NIC ---
                if (seenNics.contains(nic)) {
                    addRowError(result, rowNum, "Duplicate NIC in file: " + nic);
                    continue;
                }
                seenNics.add(nic);

                // --- Build entity ---
                Customer customer = new Customer();
                customer.setName(name);
                customer.setDob(dob);
                customer.setNic(nic);

                batch.add(customer);

                // --- Flush batch ---
                if (batch.size() >= BATCH_SIZE) {
                    saveBatch(batch, result, rowNum);
                    batch.clear();
                }

            } catch (Exception e) {
                addRowError(result, rowNum, "Unexpected error: " + e.getMessage());
            }
        }

        // Flush remaining
        if (!batch.isEmpty()) {
            saveBatch(batch, result, rowNum);
        }

        result.setTotalRows(totalRows);
    }

    /**
     * Saves a batch of customers, skipping any that violate the unique NIC constraint.
     * Uses individual saves inside the batch to capture per-record errors gracefully.
     */
    @Transactional
    public void saveBatch(List<Customer> batch, BulkImportResultDTO result, int approximateRowNum) {
        for (Customer customer : batch) {
            try {
                // Check DB-level NIC uniqueness before attempting insert
                if (customerRepository.existsByNic(customer.getNic())) {
                    addRowError(result, -1, "NIC already exists in DB: " + customer.getNic());
                } else {
                    customerRepository.save(customer);
                    result.setSuccessCount(result.getSuccessCount() + 1);
                }
            } catch (Exception e) {
                result.setFailureCount(result.getFailureCount() + 1);
                addRowError(result, -1, "Failed to save NIC " + customer.getNic() + ": " + e.getMessage());
            }
        }
    }

    // ----------------------------------------------------------------
    // HELPERS
    // ----------------------------------------------------------------

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Upload file is empty or missing.");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null ||
                (!originalFilename.endsWith(".xlsx") && !originalFilename.endsWith(".xls"))) {
            throw new IllegalArgumentException("Only .xlsx and .xls files are supported.");
        }
    }

    private String getCellStringValue(Row row, int colIndex) {
        Cell cell = row.getCell(colIndex);
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    // Date cell — convert to LocalDate string
                    return cell.getLocalDateTimeCellValue().toLocalDate().toString();
                }
                // Plain number — format without scientific notation
                double d = cell.getNumericCellValue();
                if (d == Math.floor(d)) {
                    return String.valueOf((long) d);
                }
                return String.valueOf(d);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            default:
                return null;
        }
    }

    private LocalDate parseDate(String dateStr) {
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(dateStr, formatter);
            } catch (DateTimeParseException ignored) {
                // Try next formatter
            }
        }
        return null;
    }

    private void addRowError(BulkImportResultDTO result, int rowNum, String message) {
        result.setFailureCount(result.getFailureCount() + 1);
        if (result.getErrors().size() < MAX_ERRORS_REPORTED) {
            String prefix = rowNum > 0 ? "Row " + rowNum + ": " : "";
            result.addError(prefix + message);
        }
    }
}