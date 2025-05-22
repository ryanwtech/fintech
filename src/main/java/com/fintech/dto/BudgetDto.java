package com.fintech.dto;

import com.fintech.domain.Budget;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class BudgetDto {
    private UUID id;
    private UUID userId;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalAmount;
    private BigDecimal spentAmount;
    private BigDecimal remainingAmount;
    private Boolean isActive;
    private List<BudgetItemDto> items;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

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

    public BigDecimal getSpentAmount() {
        return spentAmount;
    }

    public void setSpentAmount(BigDecimal spentAmount) {
        this.spentAmount = spentAmount;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public List<BudgetItemDto> getItems() {
        return items;
    }

    public void setItems(List<BudgetItemDto> items) {
        this.items = items;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static BudgetDto fromEntity(Budget budget) {
        BudgetDto dto = new BudgetDto();
        dto.setId(budget.getId());
        dto.setUserId(budget.getUserId());
        dto.setName(budget.getName());
        dto.setDescription(budget.getDescription());
        dto.setStartDate(budget.getStartDate());
        dto.setEndDate(budget.getEndDate());
        dto.setTotalAmount(budget.getTotalAmount());
        dto.setIsActive(budget.getIsActive());
        dto.setCreatedAt(budget.getCreatedAt().toLocalDate());
        dto.setUpdatedAt(budget.getUpdatedAt().toLocalDate());
        return dto;
    }
}
