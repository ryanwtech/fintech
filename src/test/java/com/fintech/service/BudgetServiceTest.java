package com.fintech.service;

import com.fintech.domain.Budget;
import com.fintech.domain.BudgetItem;
import com.fintech.domain.Category;
import com.fintech.dto.*;
import com.fintech.repo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
class BudgetServiceTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private BudgetItemRepository budgetItemRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private BudgetService budgetService;
    private AuditService auditService;

    private UUID userId;
    private Category foodCategory;
    private Category transportCategory;

    @BeforeEach
    void setUp() {
        // Create audit service
        auditService = new AuditService();
        
        // Create budget service
        budgetService = new BudgetService();
        try {
            var budgetRepositoryField = BudgetService.class.getDeclaredField("budgetRepository");
            budgetRepositoryField.setAccessible(true);
            budgetRepositoryField.set(budgetService, budgetRepository);
            
            var budgetItemRepositoryField = BudgetService.class.getDeclaredField("budgetItemRepository");
            budgetItemRepositoryField.setAccessible(true);
            budgetItemRepositoryField.set(budgetService, budgetItemRepository);
            
            var categoryRepositoryField = BudgetService.class.getDeclaredField("categoryRepository");
            categoryRepositoryField.setAccessible(true);
            categoryRepositoryField.set(budgetService, categoryRepository);
            
            var transactionRepositoryField = BudgetService.class.getDeclaredField("transactionRepository");
            transactionRepositoryField.setAccessible(true);
            transactionRepositoryField.set(budgetService, transactionRepository);
            
            var auditServiceField = BudgetService.class.getDeclaredField("auditService");
            auditServiceField.setAccessible(true);
            auditServiceField.set(budgetService, auditService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject dependencies", e);
        }

        // Create test data
        userId = UUID.randomUUID();
        foodCategory = createTestCategory("Food", false);
        transportCategory = createTestCategory("Transport", false);
    }

    @Test
    void testGetBudgetByMonth_CreatesDefaultBudget() {
        // When
        BudgetDto budget = budgetService.getBudgetByMonth(userId, "2024-01");

        // Then
        assertThat(budget).isNotNull();
        assertThat(budget.getName()).isEqualTo("Budget for 2024-01");
        assertThat(budget.getStartDate()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(budget.getEndDate()).isEqualTo(LocalDate.of(2024, 1, 31));
        assertThat(budget.getTotalAmount()).isEqualTo(BigDecimal.ZERO);
        assertThat(budget.getIsActive()).isTrue();
    }

    @Test
    void testCreateBudget() {
        // Given
        CreateBudgetRequest request = new CreateBudgetRequest();
        request.setName("January 2024 Budget");
        request.setDescription("Monthly budget for January");
        request.setStartDate(LocalDate.of(2024, 1, 1));
        request.setEndDate(LocalDate.of(2024, 1, 31));
        request.setTotalAmount(new BigDecimal("3000.00"));

        CreateBudgetRequest.BudgetItemRequest foodItem = new CreateBudgetRequest.BudgetItemRequest();
        foodItem.setCategoryId(foodCategory.getId());
        foodItem.setPlannedAmount(new BigDecimal("800.00"));

        CreateBudgetRequest.BudgetItemRequest transportItem = new CreateBudgetRequest.BudgetItemRequest();
        transportItem.setCategoryId(transportCategory.getId());
        transportItem.setPlannedAmount(new BigDecimal("200.00"));

        request.setItems(List.of(foodItem, transportItem));

        // When
        BudgetDto result = budgetService.createBudget(userId, request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("January 2024 Budget");
        assertThat(result.getDescription()).isEqualTo("Monthly budget for January");
        assertThat(result.getTotalAmount()).isEqualTo(new BigDecimal("3000.00"));
        assertThat(result.getItems()).hasSize(2);
        assertThat(result.getItems().get(0).getCategoryId()).isEqualTo(foodCategory.getId());
        assertThat(result.getItems().get(0).getPlannedAmount()).isEqualTo(new BigDecimal("800.00"));
    }

    @Test
    void testCreateBudget_DuplicateMonth() {
        // Given - Create first budget
        CreateBudgetRequest request1 = new CreateBudgetRequest();
        request1.setName("January 2024 Budget");
        request1.setStartDate(LocalDate.of(2024, 1, 1));
        request1.setEndDate(LocalDate.of(2024, 1, 31));
        request1.setTotalAmount(new BigDecimal("3000.00"));

        budgetService.createBudget(userId, request1);

        // When - Try to create another budget for the same month
        CreateBudgetRequest request2 = new CreateBudgetRequest();
        request2.setName("Another January Budget");
        request2.setStartDate(LocalDate.of(2024, 1, 1));
        request2.setEndDate(LocalDate.of(2024, 1, 31));
        request2.setTotalAmount(new BigDecimal("2000.00"));

        // Then
        assertThatThrownBy(() -> budgetService.createBudget(userId, request2))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Budget already exists for this month");
    }

    @Test
    void testUpdateBudgetItem() {
        // Given - Create budget first
        Budget budget = createTestBudget();
        UpdateBudgetItemRequest request = new UpdateBudgetItemRequest();
        request.setCategoryId(foodCategory.getId());
        request.setPlannedAmount(new BigDecimal("500.00"));

        // When
        BudgetItemDto result = budgetService.updateBudgetItem(userId, budget.getId(), foodCategory.getId(), request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCategoryId()).isEqualTo(foodCategory.getId());
        assertThat(result.getPlannedAmount()).isEqualTo(new BigDecimal("500.00"));
        assertThat(result.getCategoryName()).isEqualTo("Food");
    }

    @Test
    void testGetBudgetById() {
        // Given
        Budget budget = createTestBudget();

        // When
        BudgetDto result = budgetService.getBudgetById(userId, budget.getId());

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(budget.getId());
        assertThat(result.getName()).isEqualTo("Test Budget");
    }

    @Test
    void testGetBudgetById_NotFound() {
        // When & Then
        assertThatThrownBy(() -> budgetService.getBudgetById(userId, UUID.randomUUID()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Budget not found");
    }

    @Test
    void testGetBudgetById_WrongUser() {
        // Given
        Budget budget = createTestBudget();
        UUID otherUserId = UUID.randomUUID();

        // When & Then
        assertThatThrownBy(() -> budgetService.getBudgetById(otherUserId, budget.getId()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Budget not found");
    }

    @Test
    void testDeleteBudget() {
        // Given
        Budget budget = createTestBudget();

        // When
        budgetService.deleteBudget(userId, budget.getId());

        // Then
        assertThat(budgetRepository.findById(budget.getId())).isEmpty();
    }

    @Test
    void testEnrichBudgetWithData() {
        // Given
        Budget budget = createTestBudget();
        BudgetItem budgetItem = createTestBudgetItem(budget.getId(), foodCategory.getId(), new BigDecimal("500.00"));

        // When
        BudgetDto result = budgetService.getBudgetById(userId, budget.getId());

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getCategoryName()).isEqualTo("Food");
        assertThat(result.getItems().get(0).getPlannedAmount()).isEqualTo(new BigDecimal("500.00"));
        assertThat(result.getSpentAmount()).isNotNull();
        assertThat(result.getRemainingAmount()).isNotNull();
    }

    private Budget createTestBudget() {
        Budget budget = new Budget();
        budget.setUserId(userId);
        budget.setName("Test Budget");
        budget.setDescription("Test budget description");
        budget.setStartDate(LocalDate.of(2024, 1, 1));
        budget.setEndDate(LocalDate.of(2024, 1, 31));
        budget.setTotalAmount(new BigDecimal("1000.00"));
        budget.setIsActive(true);
        return entityManager.persistAndFlush(budget);
    }

    private BudgetItem createTestBudgetItem(UUID budgetId, UUID categoryId, BigDecimal plannedAmount) {
        BudgetItem budgetItem = new BudgetItem();
        budgetItem.setBudgetId(budgetId);
        budgetItem.setCategoryId(categoryId);
        budgetItem.setPlannedAmount(plannedAmount);
        budgetItem.setActualAmount(BigDecimal.ZERO);
        return entityManager.persistAndFlush(budgetItem);
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
}
