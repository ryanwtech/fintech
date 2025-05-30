package com.fintech.integration;

import com.fintech.domain.Transaction;
import com.fintech.dto.CreateTransactionRequest;
import com.fintech.dto.TransactionDto;
import com.fintech.dto.UpdateTransactionRequest;
import com.fintech.repo.TransactionRepository;
import com.fintech.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TransactionServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepository;

    private UUID testAccountId;
    private UUID testCategoryId;

    @BeforeEach
    void setUp() {
        testAccountId = UUID.fromString("33333333-3333-3333-3333-333333333333");
        testCategoryId = UUID.fromString("66666666-6666-6666-6666-666666666666");
    }

    @Test
    void shouldCreateTransaction() {
        // Given
        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setAmount(new BigDecimal("-50.00"));
        request.setDescription("Test transaction");
        request.setMerchant("Test Merchant");
        request.setPostedAt(LocalDateTime.now());
        request.setCategoryId(testCategoryId);

        // When
        TransactionDto result = transactionService.createTransaction(testAccountId, request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("-50.00"));
        assertThat(result.getDescription()).isEqualTo("Test transaction");
        assertThat(result.getMerchant()).isEqualTo("Test Merchant");
        assertThat(result.getCategoryId()).isEqualTo(testCategoryId);
        assertThat(result.getTransactionType()).isEqualTo(Transaction.TransactionType.DEBIT);
        assertThat(result.getStatus()).isEqualTo(Transaction.TransactionStatus.PENDING);
        assertThat(result.getExternalId()).isNotNull();

        // Verify it was saved to database
        Optional<Transaction> savedTransaction = transactionRepository.findByExternalId(result.getExternalId());
        assertThat(savedTransaction).isPresent();
    }

    @Test
    void shouldUpdateTransaction() {
        // Given
        UUID transactionId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        UpdateTransactionRequest request = new UpdateTransactionRequest();
        request.setDescription("Updated description");
        request.setMerchant("Updated merchant");

        // When
        TransactionDto result = transactionService.updateTransaction(transactionId, request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isEqualTo("Updated description");
        assertThat(result.getMerchant()).isEqualTo("Updated merchant");
        assertThat(result.getId()).isEqualTo(transactionId);

        // Verify it was updated in database
        Optional<Transaction> updatedTransaction = transactionRepository.findById(transactionId);
        assertThat(updatedTransaction).isPresent();
        assertThat(updatedTransaction.get().getDescription()).isEqualTo("Updated description");
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentTransaction() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        UpdateTransactionRequest request = new UpdateTransactionRequest();
        request.setDescription("Updated description");

        // When & Then
        assertThatThrownBy(() -> transactionService.updateTransaction(nonExistentId, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Transaction not found");
    }

    @Test
    void shouldGetTransactionsByAccount() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<TransactionDto> result = transactionService.getTransactionsByAccount(
                testAccountId, null, null, null, null, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3); // 3 test transactions
        assertThat(result.getContent().get(0).getAccountId()).isEqualTo(testAccountId);
    }

    @Test
    void shouldGetTransactionsWithFilters() {
        // Given
        LocalDateTime from = LocalDateTime.now().minusDays(5);
        LocalDateTime to = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<TransactionDto> result = transactionService.getTransactionsByAccount(
                testAccountId, from, to, testCategoryId, "coffee", pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getDescription()).containsIgnoringCase("coffee");
    }

    @Test
    void shouldImportTransactionsFromCsv() {
        // Given
        String csvContent = "postedAt,amount,description,merchant\n" +
                "2024-01-15T09:30:00,-25.50,Coffee purchase,Starbucks\n" +
                "2024-01-15T10:30:00,-45.00,Gas station,Shell\n" +
                "2024-01-15T11:30:00,1000.00,Salary deposit,Employer";
        
        MultipartFile file = new MockMultipartFile(
                "file", "transactions.csv", "text/csv", csvContent.getBytes());

        // When
        var result = transactionService.importTransactionsFromCsv(testAccountId, file);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalRows()).isEqualTo(3);
        assertThat(result.getSuccessfulImports()).isEqualTo(3);
        assertThat(result.getFailedImports()).isEqualTo(0);
        assertThat(result.getImportedTransactions()).hasSize(3);
        assertThat(result.getErrors()).isEmpty();
    }

    @Test
    void shouldHandleCsvImportWithErrors() {
        // Given
        String csvContent = "postedAt,amount,description,merchant\n" +
                "invalid-date,-25.50,Coffee purchase,Starbucks\n" +
                "2024-01-15T10:30:00,invalid-amount,Gas station,Shell\n" +
                "2024-01-15T11:30:00,1000.00,Salary deposit,Employer";
        
        MultipartFile file = new MockMultipartFile(
                "file", "transactions.csv", "text/csv", csvContent.getBytes());

        // When
        var result = transactionService.importTransactionsFromCsv(testAccountId, file);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalRows()).isEqualTo(3);
        assertThat(result.getSuccessfulImports()).isEqualTo(1);
        assertThat(result.getFailedImports()).isEqualTo(2);
        assertThat(result.getImportedTransactions()).hasSize(1);
        assertThat(result.getErrors()).hasSize(2);
    }

    @Test
    void shouldHandleEmptyCsvFile() {
        // Given
        String csvContent = "postedAt,amount,description,merchant\n";
        
        MultipartFile file = new MockMultipartFile(
                "file", "empty.csv", "text/csv", csvContent.getBytes());

        // When
        var result = transactionService.importTransactionsFromCsv(testAccountId, file);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalRows()).isEqualTo(0);
        assertThat(result.getSuccessfulImports()).isEqualTo(0);
        assertThat(result.getFailedImports()).isEqualTo(0);
        assertThat(result.getImportedTransactions()).isEmpty();
        assertThat(result.getErrors()).isEmpty();
    }

    @Test
    void shouldApplyRulesDuringTransactionCreation() {
        // Given
        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setAmount(new BigDecimal("-15.00"));
        request.setDescription("Coffee at Starbucks");
        request.setMerchant("Starbucks");
        request.setPostedAt(LocalDateTime.now());

        // When
        TransactionDto result = transactionService.createTransaction(testAccountId, request);

        // Then
        assertThat(result).isNotNull();
        // The rule should have automatically assigned the Food category
        assertThat(result.getCategoryId()).isEqualTo(testCategoryId);
    }

    @Test
    void shouldHandleDuplicateTransactions() {
        // Given
        String csvContent = "postedAt,amount,description,merchant\n" +
                "2024-01-15T09:30:00,-25.50,Coffee shop,Starbucks\n" + // This is a duplicate
                "2024-01-15T10:30:00,-45.00,New transaction,New Merchant";
        
        MultipartFile file = new MockMultipartFile(
                "file", "transactions.csv", "text/csv", csvContent.getBytes());

        // When
        var result = transactionService.importTransactionsFromCsv(testAccountId, file);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalRows()).isEqualTo(2);
        assertThat(result.getSuccessfulImports()).isEqualTo(1);
        assertThat(result.getFailedImports()).isEqualTo(1);
        assertThat(result.getErrors()).hasSize(1);
        assertThat(result.getErrors().get(0)).contains("Duplicate transaction found");
    }
}
