package com.fintech.service;

import com.fintech.domain.Budget;
import com.fintech.domain.BudgetItem;
import com.fintech.domain.Category;
import com.fintech.dto.*;
import com.fintech.repo.BudgetItemRepository;
import com.fintech.repo.BudgetRepository;
import com.fintech.repo.CategoryRepository;
import com.fintech.repo.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private BudgetItemRepository budgetItemRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AuditService auditService;

    public BudgetDto getBudgetByMonth(UUID userId, String month) {
        YearMonth yearMonth = YearMonth.parse(month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        Optional<Budget> budgetOpt = budgetRepository.findByUserIdAndMonth(
                userId, yearMonth.getYear(), yearMonth.getMonthValue());

        if (budgetOpt.isPresent()) {
            return enrichBudgetWithData(budgetOpt.get());
        }

        // Create default budget if none exists
        return createDefaultBudget(userId, startDate, endDate);
    }

    public BudgetDto createBudget(UUID userId, CreateBudgetRequest request) {
        // Check if budget already exists for the month
        YearMonth yearMonth = YearMonth.from(request.getStartDate());
        Optional<Budget> existingBudget = budgetRepository.findByUserIdAndMonth(
                userId, yearMonth.getYear(), yearMonth.getMonthValue());

        if (existingBudget.isPresent()) {
            throw new RuntimeException("Budget already exists for this month");
        }

        // Create budget
        Budget budget = new Budget();
        budget.setUserId(userId);
        budget.setName(request.getName());
        budget.setDescription(request.getDescription());
        budget.setStartDate(request.getStartDate());
        budget.setEndDate(request.getEndDate());
        budget.setTotalAmount(request.getTotalAmount());
        budget.setIsActive(true);

        Budget savedBudget = budgetRepository.save(budget);

        // Create budget items
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            for (CreateBudgetRequest.BudgetItemRequest itemRequest : request.getItems()) {
                BudgetItem budgetItem = new BudgetItem();
                budgetItem.setBudgetId(savedBudget.getId());
                budgetItem.setCategoryId(itemRequest.getCategoryId());
                budgetItem.setPlannedAmount(itemRequest.getPlannedAmount());
                budgetItem.setActualAmount(BigDecimal.ZERO);
                budgetItemRepository.save(budgetItem);
            }
        }

        // Log audit
        auditService.logBudgetAction(com.fintech.domain.AuditLog.AuditAction.CREATE, savedBudget, null);

        return enrichBudgetWithData(savedBudget);
    }

    public BudgetDto getBudgetById(UUID userId, UUID budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        if (!budget.getUserId().equals(userId)) {
            throw new RuntimeException("Budget not found");
        }

        return enrichBudgetWithData(budget);
    }

    public BudgetItemDto updateBudgetItem(UUID userId, UUID budgetId, UUID categoryId, UpdateBudgetItemRequest request) {
        // Verify budget belongs to user
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        if (!budget.getUserId().equals(userId)) {
            throw new RuntimeException("Budget not found");
        }

        // Find or create budget item
        Optional<BudgetItem> budgetItemOpt = budgetItemRepository.findByBudgetIdAndCategoryId(budgetId, categoryId);
        BudgetItem budgetItem;

        if (budgetItemOpt.isPresent()) {
            budgetItem = budgetItemOpt.get();
            budgetItem.setPlannedAmount(request.getPlannedAmount());
        } else {
            budgetItem = new BudgetItem();
            budgetItem.setBudgetId(budgetId);
            budgetItem.setCategoryId(categoryId);
            budgetItem.setPlannedAmount(request.getPlannedAmount());
            budgetItem.setActualAmount(BigDecimal.ZERO);
        }

        BudgetItem savedItem = budgetItemRepository.save(budgetItem);

        // Log audit
        auditService.logBudgetItemAction(com.fintech.domain.AuditLog.AuditAction.UPDATE, savedItem, null);

        return enrichBudgetItemWithData(savedItem);
    }

    public void deleteBudget(UUID userId, UUID budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        if (!budget.getUserId().equals(userId)) {
            throw new RuntimeException("Budget not found");
        }

        // Delete budget items first
        budgetItemRepository.deleteByBudgetId(budgetId);

        // Log audit before deletion
        auditService.logBudgetAction(com.fintech.domain.AuditLog.AuditAction.DELETE, budget, null);

        budgetRepository.delete(budget);
    }

    private BudgetDto createDefaultBudget(UUID userId, LocalDate startDate, LocalDate endDate) {
        Budget budget = new Budget();
        budget.setUserId(userId);
        budget.setName("Budget for " + YearMonth.from(startDate).toString());
        budget.setDescription("Default budget for " + YearMonth.from(startDate).toString());
        budget.setStartDate(startDate);
        budget.setEndDate(endDate);
        budget.setTotalAmount(BigDecimal.ZERO);
        budget.setIsActive(true);

        Budget savedBudget = budgetRepository.save(budget);
        return enrichBudgetWithData(savedBudget);
    }

    private BudgetDto enrichBudgetWithData(Budget budget) {
        BudgetDto dto = BudgetDto.fromEntity(budget);

        // Get budget items with category information
        List<BudgetItem> items = budgetItemRepository.findByBudgetIdOrderByPlannedAmountDesc(budget.getId());
        List<BudgetItemDto> itemDtos = items.stream()
                .map(this::enrichBudgetItemWithData)
                .collect(Collectors.toList());

        dto.setItems(itemDtos);

        // Calculate spent amount from transactions
        BigDecimal spentAmount = calculateSpentAmount(budget.getUserId(), budget.getStartDate(), budget.getEndDate());
        dto.setSpentAmount(spentAmount);

        // Calculate remaining amount
        BigDecimal remainingAmount = budget.getTotalAmount().subtract(spentAmount);
        dto.setRemainingAmount(remainingAmount);

        return dto;
    }

    private BudgetItemDto enrichBudgetItemWithData(BudgetItem budgetItem) {
        BudgetItemDto dto = BudgetItemDto.fromEntity(budgetItem);

        // Get category information
        Optional<Category> categoryOpt = categoryRepository.findById(budgetItem.getCategoryId());
        if (categoryOpt.isPresent()) {
            Category category = categoryOpt.get();
            dto.setCategoryName(category.getName());
        }

        // Calculate actual amount from transactions
        BigDecimal actualAmount = calculateCategorySpentAmount(
                budgetItem.getCategoryId(), 
                budgetItem.getBudgetId()
        );
        dto.setActualAmount(actualAmount);

        // Calculate remaining amount
        BigDecimal remainingAmount = budgetItem.getPlannedAmount().subtract(actualAmount);
        dto.setRemainingAmount(remainingAmount);

        // Calculate spent percentage
        if (budgetItem.getPlannedAmount().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal percentage = actualAmount.divide(budgetItem.getPlannedAmount(), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
            dto.setSpentPercentage(percentage);
        } else {
            dto.setSpentPercentage(BigDecimal.ZERO);
        }

        return dto;
    }

    private BigDecimal calculateSpentAmount(UUID userId, LocalDate startDate, LocalDate endDate) {
        return transactionRepository.calculateSpentAmountByUserAndDateRange(userId, startDate, endDate);
    }

    private BigDecimal calculateCategorySpentAmount(UUID categoryId, UUID budgetId) {
        // Get budget to determine date range
        Budget budget = budgetRepository.findById(budgetId).orElse(null);
        if (budget == null) {
            return BigDecimal.ZERO;
        }

        return transactionRepository.calculateSpentAmountByCategoryAndDateRange(
                categoryId, budget.getStartDate(), budget.getEndDate());
    }
}
