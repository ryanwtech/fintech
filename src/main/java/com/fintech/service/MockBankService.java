package com.fintech.service;

import com.fintech.domain.BankConnection;
import com.fintech.dto.BankConnectionDto;
import com.fintech.dto.LinkBankRequest;
import com.fintech.repo.BankConnectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class MockBankService {

    @Autowired
    private BankConnectionRepository bankConnectionRepository;

    @Autowired
    private AuditService auditService;

    public BankConnectionDto linkBank(UUID userId, LinkBankRequest request) {
        // Check if user already has a connection for this account
        Optional<BankConnection> existingConnection = bankConnectionRepository
                .findByUserIdAndExternalConnectionId(userId, request.getAccountNumber());

        if (existingConnection.isPresent()) {
            throw new RuntimeException("Bank account already linked");
        }

        // Generate fake access token
        String accessToken = generateFakeAccessToken();

        // Create bank connection
        BankConnection connection = new BankConnection();
        connection.setUserId(userId);
        connection.setBankName(request.getBankName());
        connection.setExternalConnectionId(accessToken);
        connection.setAccountNumberMasked(maskAccountNumber(request.getAccountNumber()));
        connection.setConnectionStatus(BankConnection.ConnectionStatus.ACTIVE);
        connection.setLastSyncAt(LocalDateTime.now());

        BankConnection savedConnection = bankConnectionRepository.save(connection);

        // Log audit
        auditService.logBankConnectionAction(com.fintech.domain.AuditLog.AuditAction.CREATE, savedConnection, null);

        return BankConnectionDto.fromEntity(savedConnection);
    }

    public List<BankConnectionDto> getUserConnections(UUID userId) {
        List<BankConnection> connections = bankConnectionRepository.findActiveConnectionsByUserId(userId);
        return connections.stream()
                .map(BankConnectionDto::fromEntity)
                .collect(Collectors.toList());
    }

    public BankConnectionDto getConnectionById(UUID userId, UUID connectionId) {
        BankConnection connection = bankConnectionRepository.findById(connectionId)
                .orElseThrow(() -> new RuntimeException("Bank connection not found"));

        if (!connection.getUserId().equals(userId)) {
            throw new RuntimeException("Bank connection not found");
        }

        return BankConnectionDto.fromEntity(connection);
    }

    public void unlinkBank(UUID userId, UUID connectionId) {
        BankConnection connection = bankConnectionRepository.findById(connectionId)
                .orElseThrow(() -> new RuntimeException("Bank connection not found"));

        if (!connection.getUserId().equals(userId)) {
            throw new RuntimeException("Bank connection not found");
        }

        // Update status to inactive
        connection.setConnectionStatus(BankConnection.ConnectionStatus.INACTIVE);
        bankConnectionRepository.save(connection);

        // Log audit
        auditService.logBankConnectionAction(com.fintech.domain.AuditLog.AuditAction.UPDATE, connection, null);
    }

    public BankConnection getConnectionByAccessToken(String accessToken) {
        return bankConnectionRepository.findByExternalConnectionId(accessToken)
                .orElseThrow(() -> new RuntimeException("Invalid access token"));
    }

    private String generateFakeAccessToken() {
        // Generate a fake access token for testing
        return "mock_token_" + UUID.randomUUID().toString().replace("-", "");
    }

    private String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 4) {
            return accountNumber;
        }
        return "****" + accountNumber.substring(accountNumber.length() - 4);
    }
}
