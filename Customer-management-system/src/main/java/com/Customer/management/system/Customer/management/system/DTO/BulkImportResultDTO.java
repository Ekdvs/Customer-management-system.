package com.Customer.management.system.Customer.management.system.DTO;

import java.util.ArrayList;
import java.util.List;


public class BulkImportResultDTO {

    private int totalRows;
    private int successCount;
    private int failureCount;
    private List<String> errors = new ArrayList<>();
    private String status;
    private long processingTimeMs;

    public BulkImportResultDTO() {}

    // ---- Getters & Setters ----

    public int getTotalRows() { return totalRows; }
    public void setTotalRows(int totalRows) { this.totalRows = totalRows; }

    public int getSuccessCount() { return successCount; }
    public void setSuccessCount(int successCount) { this.successCount = successCount; }

    public int getFailureCount() { return failureCount; }
    public void setFailureCount(int failureCount) { this.failureCount = failureCount; }

    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getProcessingTimeMs() { return processingTimeMs; }
    public void setProcessingTimeMs(long processingTimeMs) { this.processingTimeMs = processingTimeMs; }

    public void addError(String error) { this.errors.add(error); }
}
