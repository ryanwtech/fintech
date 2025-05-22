package com.fintech.repo;

import com.fintech.domain.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, UUID> {

    @Query("SELECT b FROM Budget b WHERE b.userId = :userId AND " +
           "EXTRACT(YEAR FROM b.startDate) = :year AND " +
           "EXTRACT(MONTH FROM b.startDate) = :month")
    Optional<Budget> findByUserIdAndMonth(@Param("userId") UUID userId, 
                                         @Param("year") int year, 
                                         @Param("month") int month);

    @Query("SELECT b FROM Budget b WHERE b.userId = :userId AND " +
           "b.startDate >= :startDate AND b.startDate <= :endDate " +
           "ORDER BY b.startDate DESC")
    List<Budget> findByUserIdAndDateRange(@Param("userId") UUID userId,
                                          @Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);

    @Query("SELECT b FROM Budget b WHERE b.userId = :userId AND b.isActive = true " +
           "ORDER BY b.startDate DESC")
    List<Budget> findActiveBudgetsByUserId(@Param("userId") UUID userId);

    @Query("SELECT COUNT(b) FROM Budget b WHERE b.userId = :userId")
    long countByUserId(@Param("userId") UUID userId);
}
