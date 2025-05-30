package com.fintech.service;

import com.fintech.domain.AuditLog;
import com.fintech.repo.AuditLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    private final ObjectMapper objectMapper;

    public AuditLogService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    /**
     * Log a generic action with entity details
     */
    public void logAction(AuditLog.AuditAction action, String entityType, UUID entityId, Object payload) {
        logAction(action, entityType, entityId, payload, null);
    }

    /**
     * Log a generic action with entity details and old values
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAction(AuditLog.AuditAction action, String entityType, UUID entityId, Object newPayload, Object oldPayload) {
        AuditLog auditLog = new AuditLog();
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setAction(action);
        auditLog.setCreatedAt(LocalDateTime.now());

        try {
            // Convert payloads to JSON
            if (oldPayload != null) {
                auditLog.setOldValues(objectMapper.writeValueAsString(oldPayload));
            }
            if (newPayload != null) {
                auditLog.setNewValues(objectMapper.writeValueAsString(newPayload));
            }
        } catch (Exception e) {
            // Fallback to toString if JSON conversion fails
            if (oldPayload != null) {
                auditLog.setOldValues(oldPayload.toString());
            }
            if (newPayload != null) {
                auditLog.setNewValues(newPayload.toString());
            }
        }

        auditLogRepository.save(auditLog);
    }

    /**
     * Log transaction actions
     */
    public void logTransactionAction(AuditLog.AuditAction action, Object transaction, Object oldTransaction) {
        UUID entityId = extractId(transaction);
        logAction(action, "Transaction", entityId, transaction, oldTransaction);
    }

    /**
     * Log category actions
     */
    public void logCategoryAction(AuditLog.AuditAction action, Object category, Object oldCategory) {
        UUID entityId = extractId(category);
        logAction(action, "Category", entityId, category, oldCategory);
    }

    /**
     * Log budget actions
     */
    public void logBudgetAction(AuditLog.AuditAction action, Object budget, Object oldBudget) {
        UUID entityId = extractId(budget);
        logAction(action, "Budget", entityId, budget, oldBudget);
    }

    /**
     * Log budget item actions
     */
    public void logBudgetItemAction(AuditLog.AuditAction action, Object budgetItem, Object oldBudgetItem) {
        UUID entityId = extractId(budgetItem);
        logAction(action, "BudgetItem", entityId, budgetItem, oldBudgetItem);
    }

    /**
     * Log account actions
     */
    public void logAccountAction(AuditLog.AuditAction action, Object account, Object oldAccount) {
        UUID entityId = extractId(account);
        logAction(action, "Account", entityId, account, oldAccount);
    }

    /**
     * Log user actions
     */
    public void logUserAction(AuditLog.AuditAction action, Object user, Object oldUser) {
        UUID entityId = extractId(user);
        logAction(action, "User", entityId, user, oldUser);
    }

    /**
     * Log rule actions
     */
    public void logRuleAction(AuditLog.AuditAction action, Object rule, Object oldRule) {
        UUID entityId = extractId(rule);
        logAction(action, "Rule", entityId, rule, oldRule);
    }

    /**
     * Log bank connection actions
     */
    public void logBankConnectionAction(AuditLog.AuditAction action, Object bankConnection, Object oldBankConnection) {
        UUID entityId = extractId(bankConnection);
        logAction(action, "BankConnection", entityId, bankConnection, oldBankConnection);
    }

    /**
     * Log webhook event actions
     */
    public void logWebhookEventAction(AuditLog.AuditAction action, Object webhookEvent, Object oldWebhookEvent) {
        UUID entityId = extractId(webhookEvent);
        logAction(action, "WebhookEvent", entityId, webhookEvent, oldWebhookEvent);
    }

    /**
     * Log import actions with custom details
     */
    public void logImportAction(String entityType, UUID entityId, String fileName, int successfulImports, int failedImports) {
        AuditLog auditLog = new AuditLog();
        auditLog.setEntityType(entityType + "Import");
        auditLog.setEntityId(entityId);
        auditLog.setAction(AuditLog.AuditAction.CREATE);
        auditLog.setCreatedAt(LocalDateTime.now());

        String importDetails = String.format(
            "{\"fileName\":\"%s\",\"successfulImports\":%d,\"failedImports\":%d,\"timestamp\":\"%s\"}",
            fileName, successfulImports, failedImports, LocalDateTime.now().toString()
        );

        auditLog.setNewValues(importDetails);
        auditLogRepository.save(auditLog);
    }

    /**
     * Log system events
     */
    public void logSystemEvent(String eventType, String description, Object details) {
        AuditLog auditLog = new AuditLog();
        auditLog.setEntityType("System");
        auditLog.setEntityId(UUID.randomUUID()); // Generate a random ID for system events
        auditLog.setAction(AuditLog.AuditAction.CREATE);
        auditLog.setCreatedAt(LocalDateTime.now());

        try {
            String eventDetails = String.format(
                "{\"eventType\":\"%s\",\"description\":\"%s\",\"details\":%s}",
                eventType, description, objectMapper.writeValueAsString(details)
            );
            auditLog.setNewValues(eventDetails);
        } catch (Exception e) {
            auditLog.setNewValues(String.format(
                "{\"eventType\":\"%s\",\"description\":\"%s\",\"details\":\"%s\"}",
                eventType, description, details.toString()
            ));
        }

        auditLogRepository.save(auditLog);
    }

    /**
     * Extract ID from an object using reflection
     */
    private UUID extractId(Object obj) {
        if (obj == null) {
            return null;
        }

        try {
            // Try to get getId() method
            java.lang.reflect.Method getIdMethod = obj.getClass().getMethod("getId");
            Object id = getIdMethod.invoke(obj);
            return id instanceof UUID ? (UUID) id : null;
        } catch (Exception e) {
            // If getId() method doesn't exist or fails, return null
            return null;
        }
    }
}
