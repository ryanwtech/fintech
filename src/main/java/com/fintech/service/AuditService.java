package com.fintech.service;

import com.fintech.domain.AuditLog;
import com.fintech.domain.BankConnection;
import com.fintech.domain.Budget;
import com.fintech.domain.BudgetItem;
import com.fintech.domain.Rule;
import com.fintech.domain.Transaction;
import com.fintech.repo.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuditService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    public void logTransactionAction(AuditLog.AuditAction action, Transaction transaction, Transaction oldTransaction) {
        AuditLog auditLog = new AuditLog();
        auditLog.setEntityType("Transaction");
        auditLog.setEntityId(transaction.getId());
        auditLog.setAction(action);
        
        // Set old values for updates
        if (action == AuditLog.AuditAction.UPDATE && oldTransaction != null) {
            auditLog.setOldValues(createTransactionJson(oldTransaction));
        }
        
        // Set new values
        auditLog.setNewValues(createTransactionJson(transaction));
        
        auditLog.setCreatedAt(LocalDateTime.now());
        
        auditLogRepository.save(auditLog);
    }

    public void logImportAction(UUID accountId, String fileName, int successfulImports, int failedImports) {
        AuditLog auditLog = new AuditLog();
        auditLog.setEntityType("TransactionImport");
        auditLog.setEntityId(accountId);
        auditLog.setAction(AuditLog.AuditAction.CREATE);
        
        String importDetails = String.format(
            "{\"fileName\":\"%s\",\"successfulImports\":%d,\"failedImports\":%d,\"timestamp\":\"%s\"}",
            fileName, successfulImports, failedImports, LocalDateTime.now().toString()
        );
        
        auditLog.setNewValues(importDetails);
        auditLog.setCreatedAt(LocalDateTime.now());
        
        auditLogRepository.save(auditLog);
    }

    public void logRuleAction(AuditLog.AuditAction action, Rule rule, Rule oldRule) {
        AuditLog auditLog = new AuditLog();
        auditLog.setEntityType("Rule");
        auditLog.setEntityId(rule.getId());
        auditLog.setAction(action);
        
        // Set old values for updates
        if (action == AuditLog.AuditAction.UPDATE && oldRule != null) {
            auditLog.setOldValues(createRuleJson(oldRule));
        }
        
        // Set new values
        auditLog.setNewValues(createRuleJson(rule));
        
        auditLog.setCreatedAt(LocalDateTime.now());
        
        auditLogRepository.save(auditLog);
    }

    private String createRuleJson(Rule rule) {
        return String.format(
            "{\"id\":\"%s\",\"name\":\"%s\",\"description\":\"%s\",\"conditions\":\"%s\",\"actions\":\"%s\",\"priority\":%d,\"enabled\":%s}",
            rule.getId(),
            rule.getName(),
            rule.getDescription() != null ? rule.getDescription() : "",
            rule.getConditions(),
            rule.getActions(),
            rule.getPriority(),
            rule.getEnabled()
        );
    }

    private String createTransactionJson(Transaction transaction) {
        return String.format(
            "{\"id\":\"%s\",\"accountId\":\"%s\",\"amount\":%s,\"description\":\"%s\",\"merchant\":\"%s\",\"postedAt\":\"%s\",\"transactionType\":\"%s\",\"status\":\"%s\"}",
            transaction.getId(),
            transaction.getAccountId(),
            transaction.getAmount(),
            transaction.getDescription() != null ? transaction.getDescription() : "",
            transaction.getMerchant() != null ? transaction.getMerchant() : "",
            transaction.getPostedAt(),
            transaction.getTransactionType(),
            transaction.getStatus()
        );
    }

    public void logBudgetAction(AuditLog.AuditAction action, Budget budget, Budget oldBudget) {
        AuditLog auditLog = new AuditLog();
        auditLog.setEntityType("Budget");
        auditLog.setEntityId(budget.getId());
        auditLog.setAction(action);
        
        // Set old values for updates
        if (action == AuditLog.AuditAction.UPDATE && oldBudget != null) {
            auditLog.setOldValues(createBudgetJson(oldBudget));
        }
        
        // Set new values
        auditLog.setNewValues(createBudgetJson(budget));
        
        auditLog.setCreatedAt(LocalDateTime.now());
        
        auditLogRepository.save(auditLog);
    }

    public void logBudgetItemAction(AuditLog.AuditAction action, BudgetItem budgetItem, BudgetItem oldBudgetItem) {
        AuditLog auditLog = new AuditLog();
        auditLog.setEntityType("BudgetItem");
        auditLog.setEntityId(budgetItem.getId());
        auditLog.setAction(action);
        
        // Set old values for updates
        if (action == AuditLog.AuditAction.UPDATE && oldBudgetItem != null) {
            auditLog.setOldValues(createBudgetItemJson(oldBudgetItem));
        }
        
        // Set new values
        auditLog.setNewValues(createBudgetItemJson(budgetItem));
        
        auditLog.setCreatedAt(LocalDateTime.now());
        
        auditLogRepository.save(auditLog);
    }

    private String createBudgetJson(Budget budget) {
        return String.format(
            "{\"id\":\"%s\",\"name\":\"%s\",\"description\":\"%s\",\"startDate\":\"%s\",\"endDate\":\"%s\",\"totalAmount\":%s,\"isActive\":%s}",
            budget.getId(),
            budget.getName(),
            budget.getDescription() != null ? budget.getDescription() : "",
            budget.getStartDate(),
            budget.getEndDate(),
            budget.getTotalAmount(),
            budget.getIsActive()
        );
    }

    private String createBudgetItemJson(BudgetItem budgetItem) {
        return String.format(
            "{\"id\":\"%s\",\"budgetId\":\"%s\",\"categoryId\":\"%s\",\"plannedAmount\":%s,\"actualAmount\":%s}",
            budgetItem.getId(),
            budgetItem.getBudgetId(),
            budgetItem.getCategoryId(),
            budgetItem.getPlannedAmount(),
            budgetItem.getActualAmount()
        );
    }

    public void logBankConnectionAction(AuditLog.AuditAction action, BankConnection connection, BankConnection oldConnection) {
        AuditLog auditLog = new AuditLog();
        auditLog.setEntityType("BankConnection");
        auditLog.setEntityId(connection.getId());
        auditLog.setAction(action);
        
        // Set old values for updates
        if (action == AuditLog.AuditAction.UPDATE && oldConnection != null) {
            auditLog.setOldValues(createBankConnectionJson(oldConnection));
        }
        
        // Set new values
        auditLog.setNewValues(createBankConnectionJson(connection));
        
        auditLog.setCreatedAt(LocalDateTime.now());
        
        auditLogRepository.save(auditLog);
    }

    private String createBankConnectionJson(BankConnection connection) {
        return String.format(
            "{\"id\":\"%s\",\"bankName\":\"%s\",\"accountNumberMasked\":\"%s\",\"connectionStatus\":\"%s\"}",
            connection.getId(),
            connection.getBankName(),
            connection.getAccountNumberMasked(),
            connection.getConnectionStatus()
        );
    }
}
