package com.fintech.repo;

import com.fintech.domain.BudgetItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BudgetItemRepository extends JpaRepository<BudgetItem, UUID> {

    List<BudgetItem> findByBudgetIdOrderByPlannedAmountDesc(UUID budgetId);

    @Query("SELECT bi FROM BudgetItem bi WHERE bi.budgetId = :budgetId AND bi.categoryId = :categoryId")
    Optional<BudgetItem> findByBudgetIdAndCategoryId(@Param("budgetId") UUID budgetId, 
                                                     @Param("categoryId") UUID categoryId);

    @Query("SELECT bi FROM BudgetItem bi WHERE bi.budgetId = :budgetId " +
           "ORDER BY bi.plannedAmount DESC")
    List<BudgetItem> findByBudgetIdOrderByPlannedAmount(@Param("budgetId") UUID budgetId);

    @Query("SELECT SUM(bi.plannedAmount) FROM BudgetItem bi WHERE bi.budgetId = :budgetId")
    java.math.BigDecimal sumPlannedAmountByBudgetId(@Param("budgetId") UUID budgetId);

    void deleteByBudgetId(UUID budgetId);
}
