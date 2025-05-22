package com.fintech.dto;

import com.fintech.domain.BudgetItem;

import java.math.BigDecimal;
import java.util.UUID;

public class BudgetItemDto {
    private UUID id;
    private UUID budgetId;
    private UUID categoryId;
    private String categoryName;
    private BigDecimal plannedAmount;
    private BigDecimal actualAmount;
    private BigDecimal remainingAmount;
    private BigDecimal spentPercentage;

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(UUID budgetId) {
        this.budgetId = budgetId;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BigDecimal getPlannedAmount() {
        return plannedAmount;
    }

    public void setPlannedAmount(BigDecimal plannedAmount) {
        this.plannedAmount = plannedAmount;
    }

    public BigDecimal getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(BigDecimal actualAmount) {
        this.actualAmount = actualAmount;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public BigDecimal getSpentPercentage() {
        return spentPercentage;
    }

    public void setSpentPercentage(BigDecimal spentPercentage) {
        this.spentPercentage = spentPercentage;
    }

    public static BudgetItemDto fromEntity(BudgetItem budgetItem) {
        BudgetItemDto dto = new BudgetItemDto();
        dto.setId(budgetItem.getId());
        dto.setBudgetId(budgetItem.getBudgetId());
        dto.setCategoryId(budgetItem.getCategoryId());
        dto.setPlannedAmount(budgetItem.getPlannedAmount());
        dto.setActualAmount(budgetItem.getActualAmount());
        return dto;
    }
}
