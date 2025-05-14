package com.fintech.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Data
@EqualsAndHashCode(callSuper = true)
public class Transaction extends BaseEntity {

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Column(name = "category_id")
    private UUID categoryId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 255)
    private String merchant;

    @Column(name = "posted_at", nullable = false)
    private LocalDateTime postedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 20)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TransactionStatus status = TransactionStatus.PENDING;

    @Column(name = "external_id", length = 255)
    private String externalId;

    @Column(columnDefinition = "jsonb")
    private String metadata;

    public enum TransactionType {
        DEBIT, CREDIT
    }

    public enum TransactionStatus {
        PENDING, CLEARED, RECONCILED
    }
}
