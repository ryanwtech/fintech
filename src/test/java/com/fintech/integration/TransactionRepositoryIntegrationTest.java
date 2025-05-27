package com.fintech.integration;

import com.fintech.domain.Transaction;
import com.fintech.repo.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionRepositoryIntegrationTest extends BaseIntegrationTest {

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
    void shouldFindTransactionsByAccountId() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Transaction> transactions = transactionRepository.findByAccountId(testAccountId, pageable);

        // Then
        assertThat(transactions).isNotNull();
        assertThat(transactions.getContent()).hasSize(3); // 3 test transactions for this account
        assertThat(transactions.getContent().get(0).getAccountId()).isEqualTo(testAccountId);
    }

    @Test
    void shouldFindTransactionsWithFilters() {
        // Given
        LocalDateTime from = LocalDateTime.now().minusDays(5);
        LocalDateTime to = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Transaction> transactions = transactionRepository.findByAccountIdWithFilters(
                testAccountId, from, to, testCategoryId, "coffee", pageable);

        // Then
        assertThat(transactions).isNotNull();
        assertThat(transactions.getContent()).hasSize(1);
        assertThat(transactions.getContent().get(0).getDescription()).containsIgnoringCase("coffee");
    }

    @Test
    void shouldFindTransactionByExternalId() {
        // Given
        String externalId = "ext_txn_001";

        // When
        Optional<Transaction> transaction = transactionRepository.findByExternalId(externalId);

        // Then
        assertThat(transaction).isPresent();
        assertThat(transaction.get().getExternalId()).isEqualTo(externalId);
        assertThat(transaction.get().getDescription()).isEqualTo("Coffee shop");
    }

    @Test
    void shouldCalculateAccountBalance() {
        // Given
        UUID accountId = UUID.fromString("33333333-3333-3333-3333-333333333333");

        // When
        BigDecimal balance = transactionRepository.calculateAccountBalance(accountId);

        // Then
        // Expected: 3000.00 (salary) - 25.50 (coffee) - 45.00 (gas) = 2929.50
        assertThat(balance).isEqualByComparingTo(new BigDecimal("2929.50"));
    }

    @Test
    void shouldFindDuplicates() {
        // Given
        LocalDateTime postedAt = LocalDateTime.now().minusDays(1);
        BigDecimal amount = new BigDecimal("-25.50");
        String merchant = "Starbucks";
        String description = "Coffee shop";

        // When
        List<Transaction> duplicates = transactionRepository.findDuplicates(
                testAccountId, postedAt, amount, merchant, description);

        // Then
        assertThat(duplicates).hasSize(1);
        assertThat(duplicates.get(0).getMerchant()).isEqualTo(merchant);
    }

    @Test
    void shouldCalculateSpentAmountByUserAndDateRange() {
        // Given
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        LocalDate startDate = LocalDate.now().minusDays(5);
        LocalDate endDate = LocalDate.now();

        // When
        BigDecimal spentAmount = transactionRepository.calculateSpentAmountByUserAndDateRange(
                userId, startDate, endDate);

        // Then
        // Expected: 25.50 (coffee) + 45.00 (gas) = 70.50
        assertThat(spentAmount).isEqualByComparingTo(new BigDecimal("70.50"));
    }

    @Test
    void shouldCalculateSpentAmountByCategoryAndDateRange() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(5);
        LocalDate endDate = LocalDate.now();

        // When
        BigDecimal spentAmount = transactionRepository.calculateSpentAmountByCategoryAndDateRange(
                testCategoryId, startDate, endDate);

        // Then
        // Expected: 25.50 (coffee) + 85.50 (groceries) = 111.00
        assertThat(spentAmount).isEqualByComparingTo(new BigDecimal("111.00"));
    }

    @Test
    void shouldGetDailyCashflowData() {
        // Given
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        LocalDate startDate = LocalDate.now().minusDays(5);
        LocalDate endDate = LocalDate.now();

        // When
        List<Object[]> rawData = transactionRepository.getDailyCashflowDataRaw(userId, startDate, endDate);

        // Then
        assertThat(rawData).isNotEmpty();
        // Verify data structure
        Object[] firstRow = rawData.get(0);
        assertThat(firstRow).hasSize(3); // date, income, expenses
    }

    @Test
    void shouldGetSpendByCategoryData() {
        // Given
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        LocalDate startDate = LocalDate.now().minusDays(5);
        LocalDate endDate = LocalDate.now();

        // When
        List<Object[]> rawData = transactionRepository.getSpendByCategoryDataRaw(userId, startDate, endDate);

        // Then
        assertThat(rawData).isNotEmpty();
        // Verify data structure
        Object[] firstRow = rawData.get(0);
        assertThat(firstRow).hasSize(5); // categoryId, categoryName, categoryColor, amount, transactionCount
    }

    @Test
    void shouldGetMonthlyTrendData() {
        // Given
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();

        // When
        List<Object[]> rawData = transactionRepository.getMonthlyTrendDataRaw(userId, startDate, endDate);

        // Then
        assertThat(rawData).isNotEmpty();
        // Verify data structure
        Object[] firstRow = rawData.get(0);
        assertThat(firstRow).hasSize(5); // year, month, totalIncome, totalExpenses, transactionCount
    }
}
