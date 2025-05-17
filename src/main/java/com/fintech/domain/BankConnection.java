package com.fintech.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bank_connections")
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

    // Getters and Setters
    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountNumberMasked() {
        return accountNumberMasked;
    }

    public void setAccountNumberMasked(String accountNumberMasked) {
        this.accountNumberMasked = accountNumberMasked;
    }

    public ConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(ConnectionStatus connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public LocalDateTime getLastSyncAt() {
        return lastSyncAt;
    }

    public void setLastSyncAt(LocalDateTime lastSyncAt) {
        this.lastSyncAt = lastSyncAt;
    }

    public String getExternalConnectionId() {
        return externalConnectionId;
    }

    public void setExternalConnectionId(String externalConnectionId) {
        this.externalConnectionId = externalConnectionId;
    }

    public String getCredentialsEncrypted() {
        return credentialsEncrypted;
    }

    public void setCredentialsEncrypted(String credentialsEncrypted) {
        this.credentialsEncrypted = credentialsEncrypted;
    }
}
