package com.fintech.service;

import com.fintech.domain.Account;
import com.fintech.domain.AuditLog;
import com.fintech.domain.Transaction;
import com.fintech.dto.*;
import com.fintech.repo.AccountRepository;
import com.fintech.repo.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CsvTransactionParser csvParser;

    @Autowired
    private AuditService auditService;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private RuleService ruleService;

    public Page<TransactionDto> getTransactionsByAccount(UUID accountId, LocalDateTime from, LocalDateTime to, 
                                                         UUID categoryId, String searchQuery, Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findByAccountIdWithFilters(
                accountId, from, to, categoryId, searchQuery, pageable);
        
        return transactions.map(TransactionDto::fromEntity);
    }

    public TransactionDto createTransaction(UUID accountId, CreateTransactionRequest request) {
        // Verify account exists
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Apply rules to determine category if not provided
        UUID categoryId = request.getCategoryId();
        if (categoryId == null) {
            RuleService.RuleMatchResult ruleMatch = ruleService.applyRulesToTransaction(
                    account.getUserId(), request.getMerchant(), request.getDescription());
            if (ruleMatch.isMatch()) {
                categoryId = ruleMatch.getTargetCategoryId();
            }
        }

        // Create transaction
        Transaction transaction = new Transaction();
        transaction.setAccountId(accountId);
        transaction.setCategoryId(categoryId);
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setMerchant(request.getMerchant());
        transaction.setPostedAt(request.getPostedAt());
        transaction.setTransactionType(determineTransactionType(request.getAmount()));
        transaction.setStatus(Transaction.TransactionStatus.PENDING);
        transaction.setExternalId(generateExternalId());

        // Save transaction
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Log audit
        auditLogService.logTransactionAction(AuditLog.AuditAction.CREATE, savedTransaction, null);

        return TransactionDto.fromEntity(savedTransaction);
    }

    public TransactionDto updateTransaction(UUID transactionId, UpdateTransactionRequest request) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        // Store old values for audit
        Transaction oldTransaction = createTransactionCopy(transaction);

        // Update fields
        if (request.getPostedAt() != null) {
            transaction.setPostedAt(request.getPostedAt());
        }
        if (request.getAmount() != null) {
            transaction.setAmount(request.getAmount());
            transaction.setTransactionType(determineTransactionType(request.getAmount()));
        }
        if (request.getMerchant() != null) {
            transaction.setMerchant(request.getMerchant());
        }
        if (request.getDescription() != null) {
            transaction.setDescription(request.getDescription());
        }
        if (request.getCategoryId() != null) {
            transaction.setCategoryId(request.getCategoryId());
        }

        // Save updated transaction
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Log audit
        auditLogService.logTransactionAction(AuditLog.AuditAction.UPDATE, savedTransaction, oldTransaction);

        return TransactionDto.fromEntity(savedTransaction);
    }

    public CsvImportResult importTransactionsFromCsv(UUID accountId, MultipartFile file) {
        // Verify account exists
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        try {
            // Parse CSV
            List<CsvTransactionParser.CsvTransactionRow> csvRows = csvParser.parseCsv(file);
            
            List<TransactionDto> importedTransactions = new ArrayList<>();
            List<String> errors = new ArrayList<>();
            int successfulImports = 0;
            int failedImports = 0;

            for (CsvTransactionParser.CsvTransactionRow row : csvRows) {
                if (row.hasError()) {
                    errors.add("Line " + row.getLineNumber() + ": " + row.getError());
                    failedImports++;
                    continue;
                }

                try {
                    // Check for duplicates
                    List<Transaction> duplicates = transactionRepository.findDuplicates(
                            accountId, row.getPostedAt(), row.getAmount(), row.getMerchant(), row.getDescription());
                    
                    if (!duplicates.isEmpty()) {
                        errors.add("Line " + row.getLineNumber() + ": Duplicate transaction found");
                        failedImports++;
                        continue;
                    }

                    // Create transaction with rule application
                    CreateTransactionRequest createRequest = row.toCreateRequest();
                    TransactionDto createdTransaction = createTransaction(accountId, createRequest);
                    importedTransactions.add(createdTransaction);
                    successfulImports++;

                } catch (Exception e) {
                    errors.add("Line " + row.getLineNumber() + ": " + e.getMessage());
                    failedImports++;
                }
            }

            // Log import audit
            auditLogService.logImportAction("Transaction", accountId, file.getOriginalFilename(), successfulImports, failedImports);

            return new CsvImportResult(csvRows.size(), successfulImports, failedImports, errors, importedTransactions);

        } catch (IOException e) {
            throw new RuntimeException("Error reading CSV file: " + e.getMessage());
        }
    }

    public void deleteTransaction(UUID transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        // Log audit before deletion
        auditService.logTransactionAction(AuditLog.AuditAction.DELETE, transaction, null);

        transactionRepository.delete(transaction);
    }

    private Transaction.TransactionType determineTransactionType(BigDecimal amount) {
        return amount.compareTo(BigDecimal.ZERO) >= 0 ? 
                Transaction.TransactionType.CREDIT : Transaction.TransactionType.DEBIT;
    }

    private String generateExternalId() {
        return "TXN_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
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
}
