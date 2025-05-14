package com.fintech.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bank_connections")
@Data
@EqualsAndHashCode(callSuper = true)
public class BankConnection extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "bank_name", nullable = false, length = 100)
    private String bankName;

    @Column(name = "account_number_masked", length = 20)
    private String accountNumberMasked;

    @Enumerated(EnumType.STRING)
    @Column(name = "connection_status", length = 20)
    private ConnectionStatus connectionStatus = ConnectionStatus.ACTIVE;

    @Column(name = "last_sync_at")
    private LocalDateTime lastSyncAt;

    @Column(name = "external_connection_id", length = 255)
    private String externalConnectionId;

    @Column(name = "credentials_encrypted", columnDefinition = "TEXT")
    private String credentialsEncrypted;

    public enum ConnectionStatus {
        ACTIVE, INACTIVE, ERROR
    }
}
