package com.Customer.management.system.Customer.management.system.controller;

import com.Customer.management.system.Customer.management.system.DTO.BulkImportResultDTO;
import com.Customer.management.system.Customer.management.system.service.BulkImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/bulk")
public class BulkImportController {

    private final BulkImportService bulkImportService;

    @Autowired
    public BulkImportController(BulkImportService bulkImportService) {
        this.bulkImportService = bulkImportService;
    }


    @PostMapping("/upload")
    public ResponseEntity<BulkImportResultDTO> upload(
            @RequestParam("file") MultipartFile file) {
        BulkImportResultDTO result = bulkImportService.importExcel(file);
        return ResponseEntity.ok(result);
    }


    @PostMapping("/upload-async")
    public DeferredResult<ResponseEntity<BulkImportResultDTO>> uploadAsync(
            @RequestParam("file") MultipartFile file) {

        // Timeout: 5 minutes (300,000 ms)
        DeferredResult<ResponseEntity<BulkImportResultDTO>> deferredResult =
                new DeferredResult<>(300_000L,
                        ResponseEntity.status(408).body(timeoutResult()));

        CompletableFuture<BulkImportResultDTO> future = bulkImportService.importExcelAsync(file);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                BulkImportResultDTO errResult = new BulkImportResultDTO();
                errResult.setStatus("FAILED");
                errResult.addError("Async processing error: " + ex.getMessage());
                deferredResult.setResult(ResponseEntity.status(500).body(errResult));
            } else {
                deferredResult.setResult(ResponseEntity.ok(result));
            }
        });

        return deferredResult;
    }

    private BulkImportResultDTO timeoutResult() {
        BulkImportResultDTO r = new BulkImportResultDTO();
        r.setStatus("TIMEOUT");
        r.addError("Processing timed out after 5 minutes.");
        return r;
    }
}