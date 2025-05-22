package com.fintech.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class CreateBudgetRequest {

    @NotBlank(message = "Budget name is required")
    @Size(min = 1, max = 100, message = "Budget name must be between 1 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description must be 500 characters or less")
    private String description;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Total amount is required")
    private BigDecimal totalAmount;

    private List<BudgetItemRequest> items;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<BudgetItemRequest> getItems() {
        return items;
    }

    public void setItems(List<BudgetItemRequest> items) {
        this.items = items;
    }

    public static class BudgetItemRequest {
        @NotNull(message = "Category ID is required")
        private java.util.UUID categoryId;

        @NotNull(message = "Planned amount is required")
        private BigDecimal plannedAmount;

        // Getters and Setters
        public java.util.UUID getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(java.util.UUID categoryId) {
            this.categoryId = categoryId;
        }

        public BigDecimal getPlannedAmount() {
            return plannedAmount;
        }

        public void setPlannedAmount(BigDecimal plannedAmount) {
            this.plannedAmount = plannedAmount;
        }
    }
}
