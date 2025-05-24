package com.fintech.service;

import com.fintech.domain.Transaction;
import com.fintech.domain.WebhookEvent;
import com.fintech.dto.WebhookEventDto;
import com.fintech.dto.WebhookPayloadDto;
import com.fintech.repo.AccountRepository;
import com.fintech.repo.CategoryRepository;
import com.fintech.repo.TransactionRepository;
import com.fintech.repo.WebhookEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class WebhookEventService {

    @Autowired
    private WebhookEventRepository webhookEventRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AuditService auditService;

    @Autowired
    private ObjectMapper objectMapper;

    public WebhookEvent createWebhookEvent(String eventType, String payload, String source) {
        WebhookEvent event = new WebhookEvent();
        event.setEventType(eventType);
        event.setPayload(payload);
        event.setSource(source);
        event.setStatus(WebhookEvent.EventStatus.PENDING);
        event.setCreatedAt(LocalDateTime.now());

        return webhookEventRepository.save(event);
    }

    @Async
    public void processWebhookEvent(UUID eventId) {
        WebhookEvent event = webhookEventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Webhook event not found"));

        try {
            event.setStatus(WebhookEvent.EventStatus.PROCESSED);
            webhookEventRepository.save(event);

            // Parse payload
            WebhookPayloadDto payload = objectMapper.readValue(event.getPayload(), WebhookPayloadDto.class);

            // Process transactions
            if ("transactions.new".equals(event.getEventType()) && payload.getTransactions() != null) {
                processNewTransactions(payload);
            }

            // Mark as completed
            event.setStatus(WebhookEvent.EventStatus.PROCESSED);
            event.setProcessedAt(LocalDateTime.now());
            webhookEventRepository.save(event);

        } catch (Exception e) {
            // Mark as failed
            event.setStatus(WebhookEvent.EventStatus.FAILED);
            event.setErrorMessage(e.getMessage());
            event.setProcessedAt(LocalDateTime.now());
            webhookEventRepository.save(event);

            // Log error
            System.err.println("Failed to process webhook event " + eventId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void processNewTransactions(WebhookPayloadDto payload) {
        // Find the account by external account ID
        String externalAccountId = payload.getAccountId();
        
        // For mock bank, we'll use the external account ID to find the internal account
        // In a real implementation, you'd have a mapping table
        List<com.fintech.domain.Account> accounts = accountRepository.findAll();
        
        com.fintech.domain.Account account = accounts.stream()
                .filter(acc -> externalAccountId.equals(acc.getName())) // Simple mapping for demo
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Account not found for external ID: " + externalAccountId));

        // Process each transaction
        for (WebhookPayloadDto.TransactionData transactionData : payload.getTransactions()) {
            try {
                processTransaction(account, transactionData);
            } catch (Exception e) {
                System.err.println("Failed to process transaction " + transactionData.getTransactionId() + ": " + e.getMessage());
                // Continue processing other transactions
            }
        }
    }

    private void processTransaction(com.fintech.domain.Account account, WebhookPayloadDto.TransactionData transactionData) {
        // Check if transaction already exists
        Optional<Transaction> existingTransaction = transactionRepository
                .findByExternalId(transactionData.getTransactionId());

        if (existingTransaction.isPresent()) {
            // Update existing transaction
            updateTransaction(existingTransaction.get(), transactionData);
        } else {
            // Create new transaction
            createTransaction(account, transactionData);
        }
    }

    private void createTransaction(com.fintech.domain.Account account, WebhookPayloadDto.TransactionData transactionData) {
        Transaction transaction = new Transaction();
        transaction.setAccountId(account.getId());
        transaction.setExternalId(transactionData.getTransactionId());
        transaction.setAmount(transactionData.getAmount());
        transaction.setDescription(transactionData.getDescription());
        transaction.setMerchant(transactionData.getMerchant());
        transaction.setPostedAt(transactionData.getPostedAt());
        transaction.setTransactionType(transactionData.getAmount().compareTo(BigDecimal.ZERO) > 0 ? 
                Transaction.TransactionType.CREDIT : Transaction.TransactionType.DEBIT);
        transaction.setStatus(Transaction.TransactionStatus.CLEARED);

        // Try to find category by name
        if (transactionData.getCategory() != null) {
            Optional<com.fintech.domain.Category> category = categoryRepository
                    .findByUserIdAndName(account.getUserId(), transactionData.getCategory());
            if (category.isPresent()) {
                transaction.setCategoryId(category.get().getId());
            }
        }

        Transaction savedTransaction = transactionRepository.save(transaction);

        // Log audit
        auditService.logTransactionAction(com.fintech.domain.AuditLog.AuditAction.CREATE, savedTransaction, null);
    }

    private void updateTransaction(Transaction existingTransaction, WebhookPayloadDto.TransactionData transactionData) {
        // Store old values for audit
        Transaction oldTransaction = createTransactionCopy(existingTransaction);

        // Update fields
        existingTransaction.setAmount(transactionData.getAmount());
        existingTransaction.setDescription(transactionData.getDescription());
        existingTransaction.setMerchant(transactionData.getMerchant());
        existingTransaction.setPostedAt(transactionData.getPostedAt());
        existingTransaction.setTransactionType(transactionData.getAmount().compareTo(BigDecimal.ZERO) > 0 ? 
                Transaction.TransactionType.CREDIT : Transaction.TransactionType.DEBIT);

        // Update category if provided
        if (transactionData.getCategory() != null) {
            Optional<com.fintech.domain.Category> category = categoryRepository
                    .findByUserIdAndName(existingTransaction.getAccountId(), transactionData.getCategory());
            if (category.isPresent()) {
                existingTransaction.setCategoryId(category.get().getId());
            }
        }

        Transaction savedTransaction = transactionRepository.save(existingTransaction);

        // Log audit
        auditService.logTransactionAction(com.fintech.domain.AuditLog.AuditAction.UPDATE, savedTransaction, oldTransaction);
    }

    private Transaction createTransactionCopy(Transaction original) {
        Transaction copy = new Transaction();
        copy.setId(original.getId());
        copy.setAccountId(original.getAccountId());
        copy.setCategoryId(original.getCategoryId());
        copy.setAmount(original.getAmount());
        copy.setDescription(original.getDescription());
        copy.setMerchant(original.getMerchant());
        copy.setPostedAt(original.getPostedAt());
        copy.setTransactionType(original.getTransactionType());
        copy.setStatus(original.getStatus());
        copy.setExternalId(original.getExternalId());
        copy.setMetadata(original.getMetadata());
        copy.setCreatedAt(original.getCreatedAt());
        copy.setUpdatedAt(original.getUpdatedAt());
        return copy;
    }

    public List<WebhookEvent> getPendingEvents() {
        return webhookEventRepository.findByStatusOrderByCreatedAtAsc(WebhookEvent.EventStatus.PENDING);
    }

    public List<WebhookEvent> getFailedEvents() {
        return webhookEventRepository.findFailedEventsSince(LocalDateTime.now().minusHours(24));
    }

    public List<WebhookEventDto> getPendingEventsAsDto() {
        return getPendingEvents().stream()
                .map(WebhookEventDto::fromEntity)
                .collect(Collectors.toList());
    }
}
