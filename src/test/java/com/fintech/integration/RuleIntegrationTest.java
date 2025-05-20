package com.fintech.integration;

import com.fintech.domain.Account;
import com.fintech.domain.Category;
import com.fintech.domain.Rule;
import com.fintech.domain.User;
import com.fintech.dto.CreateRuleRequest;
import com.fintech.dto.CreateTransactionRequest;
import com.fintech.dto.TransactionDto;
import com.fintech.repo.*;
import com.fintech.service.RuleService;
import com.fintech.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class RuleIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private RuleService ruleService;
    private TransactionService transactionService;
    private AuditService auditService;

    private User user;
    private Account account;
    private Category foodCategory;
    private Category coffeeCategory;

    @BeforeEach
    void setUp() {
        // Create audit service
        auditService = new AuditService();
        
        // Create rule service
        ruleService = new RuleService();
        try {
            var ruleRepositoryField = RuleService.class.getDeclaredField("ruleRepository");
            ruleRepositoryField.setAccessible(true);
            ruleRepositoryField.set(ruleService, ruleRepository);
            
            var auditField = RuleService.class.getDeclaredField("auditService");
            auditField.setAccessible(true);
            auditField.set(ruleService, auditService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject dependencies", e);
        }

        // Create transaction service
        transactionService = new TransactionService();
        try {
            var accountRepositoryField = TransactionService.class.getDeclaredField("accountRepository");
            accountRepositoryField.setAccessible(true);
            accountRepositoryField.set(transactionService, accountRepository);
            
            var transactionRepositoryField = TransactionService.class.getDeclaredField("transactionRepository");
            transactionRepositoryField.setAccessible(true);
            transactionRepositoryField.set(transactionService, transactionRepository);
            
            var ruleServiceField = TransactionService.class.getDeclaredField("ruleService");
            ruleServiceField.setAccessible(true);
            ruleServiceField.set(transactionService, ruleService);
            
            var auditServiceField = TransactionService.class.getDeclaredField("auditService");
            auditServiceField.setAccessible(true);
            auditServiceField.set(transactionService, auditService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject dependencies", e);
        }

        // Create test data
        user = createTestUser();
        account = createTestAccount();
        foodCategory = createTestCategory("Food", false);
        coffeeCategory = createTestCategory("Coffee", false);
    }

    @Test
    void testTransactionCreationWithRuleMatching() {
        // Given - Create a rule that matches Starbucks transactions
        CreateRuleRequest ruleRequest = new CreateRuleRequest();
        ruleRequest.setName("Starbucks Rule");
        ruleRequest.setDescription("Auto-categorize Starbucks transactions");
        ruleRequest.setConditions("{\"merchantPattern\": \".*starbucks.*\", \"descriptionPattern\": \".*coffee.*\", \"logic\": \"OR\"}");
        ruleRequest.setActions("{\"targetCategoryId\": \"" + coffeeCategory.getId() + "\"}");
        ruleRequest.setPriority(1);
        ruleRequest.setEnabled(true);

        ruleService.createRule(user.getId(), ruleRequest);

        // When - Create a transaction that should match the rule
        CreateTransactionRequest transactionRequest = new CreateTransactionRequest();
        transactionRequest.setPostedAt(LocalDateTime.now());
        transactionRequest.setAmount(new BigDecimal("5.50"));
        transactionRequest.setMerchant("Starbucks Coffee");
        transactionRequest.setDescription("Morning coffee");
        // Note: categoryId is not set, should be auto-assigned by rule

        TransactionDto result = transactionService.createTransaction(account.getId(), transactionRequest);

        // Then - Transaction should be created with the category from the rule
        assertThat(result).isNotNull();
        assertThat(result.getCategoryId()).isEqualTo(coffeeCategory.getId());
        assertThat(result.getMerchant()).isEqualTo("Starbucks Coffee");
        assertThat(result.getDescription()).isEqualTo("Morning coffee");
    }

    @Test
    void testTransactionCreationWithMultipleRules() {
        // Given - Create multiple rules with different priorities
        CreateRuleRequest rule1Request = new CreateRuleRequest();
        rule1Request.setName("General Coffee Rule");
        rule1Request.setConditions("{\"descriptionPattern\": \".*coffee.*\"}");
        rule1Request.setActions("{\"targetCategoryId\": \"" + coffeeCategory.getId() + "\"}");
        rule1Request.setPriority(10);
        rule1Request.setEnabled(true);

        CreateRuleRequest rule2Request = new CreateRuleRequest();
        rule2Request.setName("Starbucks Specific Rule");
        rule2Request.setConditions("{\"merchantPattern\": \".*starbucks.*\"}");
        rule2Request.setActions("{\"targetCategoryId\": \"" + foodCategory.getId() + "\"}");
        rule2Request.setPriority(1);
        rule2Request.setEnabled(true);

        ruleService.createRule(user.getId(), rule1Request);
        ruleService.createRule(user.getId(), rule2Request);

        // When - Create a transaction that matches both rules
        CreateTransactionRequest transactionRequest = new CreateTransactionRequest();
        transactionRequest.setPostedAt(LocalDateTime.now());
        transactionRequest.setAmount(new BigDecimal("5.50"));
        transactionRequest.setMerchant("Starbucks Coffee");
        transactionRequest.setDescription("Morning coffee");

        TransactionDto result = transactionService.createTransaction(account.getId(), transactionRequest);

        // Then - Should match the higher priority rule (lower number)
        assertThat(result).isNotNull();
        assertThat(result.getCategoryId()).isEqualTo(foodCategory.getId()); // Starbucks specific rule
    }

    @Test
    void testTransactionCreationWithNoMatchingRules() {
        // Given - Create a rule that doesn't match
        CreateRuleRequest ruleRequest = new CreateRuleRequest();
        ruleRequest.setName("Pizza Rule");
        ruleRequest.setConditions("{\"merchantPattern\": \".*pizza.*\"}");
        ruleRequest.setActions("{\"targetCategoryId\": \"" + foodCategory.getId() + "\"}");
        ruleRequest.setPriority(1);
        ruleRequest.setEnabled(true);

        ruleService.createRule(user.getId(), ruleRequest);

        // When - Create a transaction that doesn't match any rules
        CreateTransactionRequest transactionRequest = new CreateTransactionRequest();
        transactionRequest.setPostedAt(LocalDateTime.now());
        transactionRequest.setAmount(new BigDecimal("5.50"));
        transactionRequest.setMerchant("Starbucks Coffee");
        transactionRequest.setDescription("Morning coffee");

        TransactionDto result = transactionService.createTransaction(account.getId(), transactionRequest);

        // Then - Transaction should be created without a category
        assertThat(result).isNotNull();
        assertThat(result.getCategoryId()).isNull();
    }

    @Test
    void testTransactionCreationWithDisabledRules() {
        // Given - Create a disabled rule
        CreateRuleRequest ruleRequest = new CreateRuleRequest();
        ruleRequest.setName("Disabled Starbucks Rule");
        ruleRequest.setConditions("{\"merchantPattern\": \".*starbucks.*\"}");
        ruleRequest.setActions("{\"targetCategoryId\": \"" + coffeeCategory.getId() + "\"}");
        ruleRequest.setPriority(1);
        ruleRequest.setEnabled(false);

        ruleService.createRule(user.getId(), ruleRequest);

        // When - Create a transaction that would match the disabled rule
        CreateTransactionRequest transactionRequest = new CreateTransactionRequest();
        transactionRequest.setPostedAt(LocalDateTime.now());
        transactionRequest.setAmount(new BigDecimal("5.50"));
        transactionRequest.setMerchant("Starbucks Coffee");
        transactionRequest.setDescription("Morning coffee");

        TransactionDto result = transactionService.createTransaction(account.getId(), transactionRequest);

        // Then - Transaction should be created without a category (rule is disabled)
        assertThat(result).isNotNull();
        assertThat(result.getCategoryId()).isNull();
    }

    @Test
    void testTransactionCreationWithExplicitCategory() {
        // Given - Create a rule
        CreateRuleRequest ruleRequest = new CreateRuleRequest();
        ruleRequest.setName("Starbucks Rule");
        ruleRequest.setConditions("{\"merchantPattern\": \".*starbucks.*\"}");
        ruleRequest.setActions("{\"targetCategoryId\": \"" + coffeeCategory.getId() + "\"}");
        ruleRequest.setPriority(1);
        ruleRequest.setEnabled(true);

        ruleService.createRule(user.getId(), ruleRequest);

        // When - Create a transaction with an explicit category
        CreateTransactionRequest transactionRequest = new CreateTransactionRequest();
        transactionRequest.setPostedAt(LocalDateTime.now());
        transactionRequest.setAmount(new BigDecimal("5.50"));
        transactionRequest.setMerchant("Starbucks Coffee");
        transactionRequest.setDescription("Morning coffee");
        transactionRequest.setCategoryId(foodCategory.getId()); // Explicit category

        TransactionDto result = transactionService.createTransaction(account.getId(), transactionRequest);

        // Then - Should use the explicit category, not the rule
        assertThat(result).isNotNull();
        assertThat(result.getCategoryId()).isEqualTo(foodCategory.getId());
    }

    private User createTestUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRole(User.UserRole.USER);
        user.setIsActive(true);
        return entityManager.persistAndFlush(user);
    }

    private Account createTestAccount() {
        Account account = new Account();
        account.setUserId(user.getId());
        account.setName("Test Account");
        account.setAccountType(Account.AccountType.CHECKING);
        account.setBalance(BigDecimal.ZERO);
        account.setCurrency("USD");
        account.setIsActive(true);
        return entityManager.persistAndFlush(account);
    }

    private Category createTestCategory(String name, boolean isIncome) {
        Category category = new Category();
        category.setUserId(user.getId());
        category.setName(name);
        category.setDescription("Test " + name + " category");
        category.setColor("#FF0000");
        category.setIcon("icon");
        category.setIsIncome(isIncome);
        category.setIsActive(true);
        return entityManager.persistAndFlush(category);
    }
}
