package com.fintech.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "budget_items")
@Data
@EqualsAndHashCode(callSuper = true)
public class BudgetItem extends BaseEntity {

    @Column(name = "budget_id", nullable = false)
    private UUID budgetId;

    @Column(name = "category_id", nullable = false)
    private UUID categoryId;

    @Column(name = "planned_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal plannedAmount;

    @Column(name = "actual_amount", precision = 15, scale = 2)
    private BigDecimal actualAmount = BigDecimal.ZERO;
}
