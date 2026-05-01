package com.Customer.management.system.Customer.management.system.service;
import com.Customer.management.system.Customer.management.system.DTO.BulkImportResultDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

public interface BulkImportService {

    /**
     * Synchronous import — suitable for smaller files or when the caller can tolerate waiting.
     */
    BulkImportResultDTO importExcel(MultipartFile file);

    /**
     * Asynchronous import — returns immediately; processes file in background.
     * Suitable for very large files (1M+ rows).
     */
    CompletableFuture<BulkImportResultDTO> importExcelAsync(MultipartFile file);
}