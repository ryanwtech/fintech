package com.fintech.dto;

import com.fintech.domain.BankConnection;

import java.time.LocalDateTime;
import java.util.UUID;

public class BankConnectionDto {
    private UUID id;
    private UUID userId;
    private String bankName;
    private String externalConnectionId;
    private String accountNumberMasked;
    private BankConnection.ConnectionStatus connectionStatus;
    private LocalDateTime lastSyncAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

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

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getExternalConnectionId() {
        return externalConnectionId;
    }

    public void setExternalConnectionId(String externalConnectionId) {
        this.externalConnectionId = externalConnectionId;
    }

    public String getAccountNumberMasked() {
        return accountNumberMasked;
    }

    public void setAccountNumberMasked(String accountNumberMasked) {
        this.accountNumberMasked = accountNumberMasked;
    }

    public BankConnection.ConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(BankConnection.ConnectionStatus connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public LocalDateTime getLastSyncAt() {
        return lastSyncAt;
    }

    public void setLastSyncAt(LocalDateTime lastSyncAt) {
        this.lastSyncAt = lastSyncAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static BankConnectionDto fromEntity(BankConnection connection) {
        BankConnectionDto dto = new BankConnectionDto();
        dto.setId(connection.getId());
        dto.setUserId(connection.getUserId());
        dto.setBankName(connection.getBankName());
        dto.setExternalConnectionId(connection.getExternalConnectionId());
        dto.setAccountNumberMasked(connection.getAccountNumberMasked());
        dto.setConnectionStatus(connection.getConnectionStatus());
        dto.setLastSyncAt(connection.getLastSyncAt());
        dto.setCreatedAt(connection.getCreatedAt());
        dto.setUpdatedAt(connection.getUpdatedAt());
        return dto;
    }
}
