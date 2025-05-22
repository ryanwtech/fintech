package com.fintech.service;

import com.fintech.domain.*;
import com.fintech.dto.*;
import com.fintech.repo.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ReportsServiceTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransactionRepository transactionRepository;

    private ReportsService reportsService;

    private UUID userId;
    private Account account;
    private Category foodCategory;
    private Category transportCategory;

    @BeforeEach
    void setUp() {
        // Create reports service
        reportsService = new ReportsService();
        try {
            var transactionRepositoryField = ReportsService.class.getDeclaredField("transactionRepository");
            transactionRepositoryField.setAccessible(true);
            transactionRepositoryField.set(reportsService, transactionRepository);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject dependencies", e);
        }

        // Create test data
        userId = UUID.randomUUID();
        account = createTestAccount();
        foodCategory = createTestCategory("Food", false);
        transportCategory = createTestCategory("Transport", false);
    }

    @Test
    void testGetCashflowReport() {
        // Given - Create test transactions
        createTestTransaction(account.getId(), foodCategory.getId(), new BigDecimal("-50.00"), LocalDate.of(2024, 1, 15));
        createTestTransaction(account.getId(), transportCategory.getId(), new BigDecimal("-30.00"), LocalDate.of(2024, 1, 16));
        createTestTransaction(account.getId(), null, new BigDecimal("2000.00"), LocalDate.of(2024, 1, 1)); // Income

        // When
        CashflowReportDto report = reportsService.getCashflowReport(
                userId, 
                LocalDate.of(2024, 1, 1), 
                LocalDate.of(2024, 1, 31)
        );

        // Then
        assertThat(report).isNotNull();
        assertThat(report.getFromDate()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(report.getToDate()).isEqualTo(LocalDate.of(2024, 1, 31));
        assertThat(report.getTotalIncome()).isEqualTo(new BigDecimal("2000.00"));
        assertThat(report.getTotalExpenses()).isEqualTo(new BigDecimal("80.00"));
        assertThat(report.getNetCashflow()).isEqualTo(new BigDecimal("1920.00"));
        assertThat(report.getDataPoints()).isNotEmpty();
    }

    @Test
    void testGetSpendByCategoryReport() {
        // Given - Create test transactions
        createTestTransaction(account.getId(), foodCategory.getId(), new BigDecimal("-100.00"), LocalDate.of(2024, 1, 15));
        createTestTransaction(account.getId(), foodCategory.getId(), new BigDecimal("-50.00"), LocalDate.of(2024, 1, 20));
        createTestTransaction(account.getId(), transportCategory.getId(), new BigDecimal("-30.00"), LocalDate.of(2024, 1, 16));

        // When
        SpendByCategoryReportDto report = reportsService.getSpendByCategoryReport(
                userId,
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 31)
        );

        // Then
        assertThat(report).isNotNull();
        assertThat(report.getTotalSpent()).isEqualTo(new BigDecimal("180.00"));
        assertThat(report.getCategoryData()).hasSize(2);
        
        // Check food category data
        SpendByCategoryReportDto.CategorySpendData foodData = report.getCategoryData().stream()
                .filter(data -> data.getCategoryName().equals("Food"))
                .findFirst()
                .orElse(null);
        assertThat(foodData).isNotNull();
        assertThat(foodData.getAmount()).isEqualTo(new BigDecimal("150.00"));
        assertThat(foodData.getTransactionCount()).isEqualTo(2);
        assertThat(foodData.getPercentage()).isEqualTo(new BigDecimal("83.33"));
    }

    @Test
    void testGetTrendReport() {
        // Given - Create test transactions for multiple months
        createTestTransaction(account.getId(), foodCategory.getId(), new BigDecimal("-100.00"), LocalDate.of(2024, 1, 15));
        createTestTransaction(account.getId(), null, new BigDecimal("2000.00"), LocalDate.of(2024, 1, 1));
        
        createTestTransaction(account.getId(), foodCategory.getId(), new BigDecimal("-120.00"), LocalDate.of(2024, 2, 15));
        createTestTransaction(account.getId(), null, new BigDecimal("2200.00"), LocalDate.of(2024, 2, 1));

        // When
        TrendReportDto report = reportsService.getTrendReport(userId, 6);

        // Then
        assertThat(report).isNotNull();
        assertThat(report.getMonths()).isEqualTo(6);
        assertThat(report.getMonthlyData()).hasSize(2);
        
        // Check summary
        assertThat(report.getSummary()).isNotNull();
        assertThat(report.getSummary().getTotalIncome()).isEqualTo(new BigDecimal("4200.00"));
        assertThat(report.getSummary().getTotalExpenses()).isEqualTo(new BigDecimal("220.00"));
        assertThat(report.getSummary().getTotalNetCashflow()).isEqualTo(new BigDecimal("3980.00"));
    }

    @Test
    void testGetCashflowReport_NoTransactions() {
        // When
        CashflowReportDto report = reportsService.getCashflowReport(
                userId,
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 31)
        );

        // Then
        assertThat(report).isNotNull();
        assertThat(report.getTotalIncome()).isEqualTo(BigDecimal.ZERO);
        assertThat(report.getTotalExpenses()).isEqualTo(BigDecimal.ZERO);
        assertThat(report.getNetCashflow()).isEqualTo(BigDecimal.ZERO);
        assertThat(report.getDataPoints()).isEmpty();
    }

    @Test
    void testGetSpendByCategoryReport_NoTransactions() {
        // When
        SpendByCategoryReportDto report = reportsService.getSpendByCategoryReport(
                userId,
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 31)
        );

        // Then
        assertThat(report).isNotNull();
        assertThat(report.getTotalSpent()).isEqualTo(BigDecimal.ZERO);
        assertThat(report.getCategoryData()).isEmpty();
    }

    @Test
    void testGetTrendReport_NoTransactions() {
        // When
        TrendReportDto report = reportsService.getTrendReport(userId, 6);

        // Then
        assertThat(report).isNotNull();
        assertThat(report.getMonthlyData()).isEmpty();
        assertThat(report.getSummary()).isNotNull();
        assertThat(report.getSummary().getTotalIncome()).isEqualTo(BigDecimal.ZERO);
        assertThat(report.getSummary().getTotalExpenses()).isEqualTo(BigDecimal.ZERO);
    }

    private Account createTestAccount() {
        Account account = new Account();
        account.setUserId(userId);
        account.setName("Test Account");
        account.setAccountType(Account.AccountType.CHECKING);
        account.setBalance(BigDecimal.ZERO);
        account.setCurrency("USD");
        account.setIsActive(true);
        return entityManager.persistAndFlush(account);
    }

    private Category createTestCategory(String name, boolean isIncome) {
        Category category = new Category();
        category.setUserId(userId);
        category.setName(name);
        category.setDescription("Test " + name + " category");
        category.setColor("#FF0000");
        category.setIcon("icon");
        category.setIsIncome(isIncome);
        category.setIsActive(true);
        return entityManager.persistAndFlush(category);
    }

    private Transaction createTestTransaction(UUID accountId, UUID categoryId, BigDecimal amount, LocalDate date) {
        Transaction transaction = new Transaction();
        transaction.setAccountId(accountId);
        transaction.setCategoryId(categoryId);
        transaction.setAmount(amount);
        transaction.setDescription("Test transaction");
        transaction.setMerchant("Test Merchant");
        transaction.setPostedAt(date.atStartOfDay());
        transaction.setTransactionType(amount.compareTo(BigDecimal.ZERO) > 0 ? 
                Transaction.TransactionType.CREDIT : Transaction.TransactionType.DEBIT);
        transaction.setStatus(Transaction.TransactionStatus.CLEARED);
        return entityManager.persistAndFlush(transaction);
    }
}
