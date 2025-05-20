package com.fintech.dto;

import java.util.List;

public class CsvImportResult {
    private int totalRows;
    private int successfulImports;
    private int failedImports;
    private List<String> errors;
    private List<TransactionDto> importedTransactions;

    // Constructors
    public CsvImportResult() {}

    public CsvImportResult(int totalRows, int successfulImports, int failedImports, List<String> errors, List<TransactionDto> importedTransactions) {
        this.totalRows = totalRows;
        this.successfulImports = successfulImports;
        this.failedImports = failedImports;
        this.errors = errors;
        this.importedTransactions = importedTransactions;
    }

    // Getters and Setters
    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public int getSuccessfulImports() {
        return successfulImports;
    }

    public void setSuccessfulImports(int successfulImports) {
        this.successfulImports = successfulImports;
    }

    public int getFailedImports() {
        return failedImports;
    }

    public void setFailedImports(int failedImports) {
        this.failedImports = failedImports;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public List<TransactionDto> getImportedTransactions() {
        return importedTransactions;
    }

    public void setImportedTransactions(List<TransactionDto> importedTransactions) {
        this.importedTransactions = importedTransactions;
    }
}
