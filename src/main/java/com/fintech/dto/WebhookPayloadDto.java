package com.fintech.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class WebhookPayloadDto {
    private String eventType;
    private String accountId;
    private List<TransactionData> transactions;

    // Getters and Setters
    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public List<TransactionData> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionData> transactions) {
        this.transactions = transactions;
    }

    public static class TransactionData {
        private String transactionId;
        private BigDecimal amount;
        private String description;
        private String merchant;
        private LocalDateTime postedAt;
        private String currency;
        private String category;
        private String status;

        // Getters and Setters
        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getMerchant() {
            return merchant;
        }

        public void setMerchant(String merchant) {
            this.merchant = merchant;
        }

        public LocalDateTime getPostedAt() {
            return postedAt;
        }

        public void setPostedAt(LocalDateTime postedAt) {
            this.postedAt = postedAt;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
