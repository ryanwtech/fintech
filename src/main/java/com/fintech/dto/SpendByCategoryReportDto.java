package com.fintech.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class SpendByCategoryReportDto {
    private LocalDate fromDate;
    private LocalDate toDate;
    private BigDecimal totalSpent;
    private List<CategorySpendData> categoryData;

    // Getters and Setters
    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
    }

    public List<CategorySpendData> getCategoryData() {
        return categoryData;
    }

    public void setCategoryData(List<CategorySpendData> categoryData) {
        this.categoryData = categoryData;
    }

    public static class CategorySpendData {
        private String categoryId;
        private String categoryName;
        private String categoryColor;
        private BigDecimal amount;
        private BigDecimal percentage;
        private int transactionCount;

        // Getters and Setters
        public String getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public String getCategoryColor() {
            return categoryColor;
        }

        public void setCategoryColor(String categoryColor) {
            this.categoryColor = categoryColor;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public BigDecimal getPercentage() {
            return percentage;
        }

        public void setPercentage(BigDecimal percentage) {
            this.percentage = percentage;
        }

        public int getTransactionCount() {
            return transactionCount;
        }

        public void setTransactionCount(int transactionCount) {
            this.transactionCount = transactionCount;
        }
    }
}
