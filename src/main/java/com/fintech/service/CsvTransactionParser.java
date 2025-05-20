package com.fintech.service;

import com.fintech.domain.Transaction;
import com.fintech.dto.CreateTransactionRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CsvTransactionParser {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public List<CsvTransactionRow> parseCsv(MultipartFile file) throws IOException {
        List<CsvTransactionRow> rows = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            int lineNumber = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                try {
                    CsvTransactionRow row = parseCsvLine(line, lineNumber);
                    rows.add(row);
                } catch (Exception e) {
                    CsvTransactionRow errorRow = new CsvTransactionRow();
                    errorRow.setLineNumber(lineNumber);
                    errorRow.setError("Error parsing line " + lineNumber + ": " + e.getMessage());
                    rows.add(errorRow);
                }
            }
        }
        
        return rows;
    }

    private CsvTransactionRow parseCsvLine(String line, int lineNumber) {
        String[] fields = parseCsvFields(line);
        
        if (fields.length < 4) {
            throw new IllegalArgumentException("Invalid CSV format. Expected at least 4 fields: postedAt, amount, merchant, description");
        }
        
        CsvTransactionRow row = new CsvTransactionRow();
        row.setLineNumber(lineNumber);
        
        try {
            // Parse postedAt
            String dateStr = fields[0].trim();
            LocalDateTime postedAt = parseDateTime(dateStr);
            row.setPostedAt(postedAt);
            
            // Parse amount
            String amountStr = fields[1].trim();
            BigDecimal amount = new BigDecimal(amountStr);
            row.setAmount(amount);
            
            // Parse merchant
            String merchant = fields[2].trim();
            row.setMerchant(merchant.isEmpty() ? null : merchant);
            
            // Parse description
            String description = fields[3].trim();
            row.setDescription(description.isEmpty() ? null : description);
            
            // Parse optional fields
            if (fields.length > 4) {
                String categoryIdStr = fields[4].trim();
                if (!categoryIdStr.isEmpty()) {
                    try {
                        row.setCategoryId(UUID.fromString(categoryIdStr));
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Invalid category ID format: " + categoryIdStr);
                    }
                }
            }
            
            if (fields.length > 5) {
                String notes = fields[5].trim();
                row.setNotes(notes.isEmpty() ? null : notes);
            }
            
        } catch (Exception e) {
            row.setError("Error parsing line " + lineNumber + ": " + e.getMessage());
        }
        
        return row;
    }

    private String[] parseCsvFields(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(currentField.toString());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }
        
        fields.add(currentField.toString());
        return fields.toArray(new String[0]);
    }

    private LocalDateTime parseDateTime(String dateStr) {
        try {
            return LocalDateTime.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e1) {
            try {
                return LocalDateTime.parse(dateStr + " 00:00:00", DATE_FORMATTER);
            } catch (DateTimeParseException e2) {
                throw new IllegalArgumentException("Invalid date format: " + dateStr + ". Expected yyyy-MM-dd or yyyy-MM-dd HH:mm:ss");
            }
        }
    }

    public static class CsvTransactionRow {
        private int lineNumber;
        private LocalDateTime postedAt;
        private BigDecimal amount;
        private String merchant;
        private String description;
        private UUID categoryId;
        private String notes;
        private String error;

        // Getters and Setters
        public int getLineNumber() {
            return lineNumber;
        }

        public void setLineNumber(int lineNumber) {
            this.lineNumber = lineNumber;
        }

        public LocalDateTime getPostedAt() {
            return postedAt;
        }

        public void setPostedAt(LocalDateTime postedAt) {
            this.postedAt = postedAt;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public String getMerchant() {
            return merchant;
        }

        public void setMerchant(String merchant) {
            this.merchant = merchant;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public UUID getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(UUID categoryId) {
            this.categoryId = categoryId;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public boolean hasError() {
            return error != null && !error.isEmpty();
        }

        public CreateTransactionRequest toCreateRequest() {
            CreateTransactionRequest request = new CreateTransactionRequest();
            request.setPostedAt(this.postedAt);
            request.setAmount(this.amount);
            request.setMerchant(this.merchant);
            request.setDescription(this.description);
            request.setCategoryId(this.categoryId);
            request.setNotes(this.notes);
            return request;
        }
    }
}
