package com.fintech.dto;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;
import java.util.UUID;

public class UpdateBudgetItemRequest {

    private UUID categoryId;

    @DecimalMin(value = "0.00", message = "Planned amount must be greater than or equal to 0")
    private BigDecimal plannedAmount;

    // Getters and Setters
    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getPlannedAmount() {
        return plannedAmount;
    }

    public void setPlannedAmount(BigDecimal plannedAmount) {
        this.plannedAmount = plannedAmount;
    }
}
