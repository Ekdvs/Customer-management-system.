package com.Customer.management.system.Customer.management.system;


import com.Customer.management.system.Customer.management.system.DTO.BulkImportResultDTO;
import com.Customer.management.system.Customer.management.system.repository.CustomerRepository;
import com.Customer.management.system.Customer.management.system.serviceimpl.BulkImportServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BulkImportServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private BulkImportServiceImpl bulkImportService;

    @Test
    void testImportExcel_InvalidFile_ShouldFail() {
        MockMultipartFile file =
                new MockMultipartFile("file", "test.txt", "text/plain", "data".getBytes());

        assertThrows(IllegalArgumentException.class, () -> {
            bulkImportService.importExcel(file);
        });
    }

    @Test
    void testImportExcel_EmptyFile_ShouldFail() {
        MockMultipartFile file =
                new MockMultipartFile("file", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[0]);

        assertThrows(IllegalArgumentException.class, () -> {
            bulkImportService.importExcel(file);
        });
    }
}
