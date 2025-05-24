package com.fintech.repo;

import com.fintech.domain.Transaction;
import com.fintech.dto.CashflowReportDto;
import com.fintech.dto.SpendByCategoryReportDto;
import com.fintech.dto.TrendReportDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    Page<Transaction> findByAccountId(UUID accountId, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.accountId = :accountId " +
           "AND (:from IS NULL OR t.postedAt >= :from) " +
           "AND (:to IS NULL OR t.postedAt <= :to) " +
           "AND (:categoryId IS NULL OR t.categoryId = :categoryId) " +
           "AND (:q IS NULL OR LOWER(t.description) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(t.merchant) LIKE LOWER(CONCAT('%', :q, '%')))")
    Page<Transaction> findByAccountIdWithFilters(
            @Param("accountId") UUID accountId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("categoryId") UUID categoryId,
            @Param("q") String searchQuery,
            Pageable pageable);

    List<Transaction> findByAccountIdAndExternalId(UUID accountId, String externalId);

    @Query("SELECT t FROM Transaction t WHERE t.accountId = :accountId " +
           "AND t.postedAt = :postedAt " +
           "AND t.amount = :amount " +
           "AND (:merchant IS NULL OR t.merchant = :merchant) " +
           "AND (:description IS NULL OR t.description = :description)")
    List<Transaction> findDuplicates(
            @Param("accountId") UUID accountId,
            @Param("postedAt") LocalDateTime postedAt,
            @Param("amount") java.math.BigDecimal amount,
            @Param("merchant") String merchant,
            @Param("description") String description);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.accountId = :accountId")
    long countByAccountId(UUID accountId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.accountId = :accountId")
    BigDecimal calculateAccountBalance(UUID accountId);

    // Budget and Reports queries
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "JOIN Account a ON t.accountId = a.id " +
           "WHERE a.userId = :userId AND t.postedAt >= :startDate AND t.postedAt <= :endDate " +
           "AND t.amount < 0")
    BigDecimal calculateSpentAmountByUserAndDateRange(@Param("userId") UUID userId, 
                                                     @Param("startDate") LocalDate startDate, 
                                                     @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.categoryId = :categoryId AND t.postedAt >= :startDate AND t.postedAt <= :endDate " +
           "AND t.amount < 0")
    BigDecimal calculateSpentAmountByCategoryAndDateRange(@Param("categoryId") UUID categoryId,
                                                         @Param("startDate") LocalDate startDate,
                                                         @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "JOIN Account a ON t.accountId = a.id " +
           "WHERE a.userId = :userId AND t.postedAt >= :startDate AND t.postedAt <= :endDate " +
           "AND t.amount > 0")
    BigDecimal calculateTotalIncomeByUserAndDateRange(@Param("userId") UUID userId,
                                                     @Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "JOIN Account a ON t.accountId = a.id " +
           "WHERE a.userId = :userId AND t.postedAt >= :startDate AND t.postedAt <= :endDate " +
           "AND t.amount < 0")
    BigDecimal calculateTotalExpensesByUserAndDateRange(@Param("userId") UUID userId,
                                                       @Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate);

    @Query("SELECT DATE(t.postedAt) as date, " +
           "COALESCE(SUM(CASE WHEN t.amount > 0 THEN t.amount ELSE 0 END), 0) as income, " +
           "COALESCE(SUM(CASE WHEN t.amount < 0 THEN t.amount ELSE 0 END), 0) as expenses " +
           "FROM Transaction t " +
           "JOIN Account a ON t.accountId = a.id " +
           "WHERE a.userId = :userId AND t.postedAt >= :startDate AND t.postedAt <= :endDate " +
           "GROUP BY DATE(t.postedAt) " +
           "ORDER BY DATE(t.postedAt)")
    List<Object[]> getDailyCashflowDataRaw(@Param("userId") UUID userId,
                                           @Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);

    @Query("SELECT c.id as categoryId, c.name as categoryName, c.color as categoryColor, " +
           "COALESCE(SUM(ABS(t.amount)), 0) as amount, COUNT(t.id) as transactionCount " +
           "FROM Transaction t " +
           "JOIN Account a ON t.accountId = a.id " +
           "JOIN Category c ON t.categoryId = c.id " +
           "WHERE a.userId = :userId AND t.postedAt >= :startDate AND t.postedAt <= :endDate " +
           "AND t.amount < 0 " +
           "GROUP BY c.id, c.name, c.color " +
           "ORDER BY amount DESC")
    List<Object[]> getSpendByCategoryDataRaw(@Param("userId") UUID userId,
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);

    @Query("SELECT EXTRACT(YEAR FROM t.postedAt) as year, EXTRACT(MONTH FROM t.postedAt) as month, " +
           "COALESCE(SUM(CASE WHEN t.amount > 0 THEN t.amount ELSE 0 END), 0) as totalIncome, " +
           "COALESCE(SUM(CASE WHEN t.amount < 0 THEN t.amount ELSE 0 END), 0) as totalExpenses, " +
           "COUNT(t.id) as transactionCount " +
           "FROM Transaction t " +
           "JOIN Account a ON t.accountId = a.id " +
           "WHERE a.userId = :userId AND t.postedAt >= :startDate AND t.postedAt <= :endDate " +
           "GROUP BY EXTRACT(YEAR FROM t.postedAt), EXTRACT(MONTH FROM t.postedAt) " +
           "ORDER BY year, month")
    List<Object[]> getMonthlyTrendDataRaw(@Param("userId") UUID userId,
                                         @Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);

    // Helper methods for data transformation
    default List<CashflowReportDto.CashflowDataPoint> getDailyCashflowData(UUID userId, LocalDate startDate, LocalDate endDate) {
        List<Object[]> rawData = getDailyCashflowDataRaw(userId, startDate, endDate);
        List<CashflowReportDto.CashflowDataPoint> dataPoints = new ArrayList<>();
        
        for (Object[] row : rawData) {
            CashflowReportDto.CashflowDataPoint point = new CashflowReportDto.CashflowDataPoint();
            point.setDate(((java.sql.Date) row[0]).toLocalDate());
            point.setIncome((BigDecimal) row[1]);
            point.setExpenses(((BigDecimal) row[2]).abs());
            point.setNetCashflow(point.getIncome().add(point.getExpenses()));
            dataPoints.add(point);
        }
        
        return dataPoints;
    }

    default List<SpendByCategoryReportDto.CategorySpendData> getSpendByCategoryData(UUID userId, LocalDate startDate, LocalDate endDate) {
        List<Object[]> rawData = getSpendByCategoryDataRaw(userId, startDate, endDate);
        List<SpendByCategoryReportDto.CategorySpendData> categoryData = new ArrayList<>();
        
        for (Object[] row : rawData) {
            SpendByCategoryReportDto.CategorySpendData data = new SpendByCategoryReportDto.CategorySpendData();
            data.setCategoryId(row[0].toString());
            data.setCategoryName((String) row[1]);
            data.setCategoryColor((String) row[2]);
            data.setAmount((BigDecimal) row[3]);
            data.setTransactionCount(((Number) row[4]).intValue());
            categoryData.add(data);
        }
        
        return categoryData;
    }

    default List<TrendReportDto.MonthlyTrendData> getMonthlyTrendData(UUID userId, LocalDate startDate, LocalDate endDate) {
        List<Object[]> rawData = getMonthlyTrendDataRaw(userId, startDate, endDate);
        List<TrendReportDto.MonthlyTrendData> monthlyData = new ArrayList<>();
        
        for (Object[] row : rawData) {
            TrendReportDto.MonthlyTrendData data = new TrendReportDto.MonthlyTrendData();
            int year = ((Number) row[0]).intValue();
            int month = ((Number) row[1]).intValue();
            
            data.setMonth(String.format("%04d-%02d", year, month));
            data.setMonthStart(LocalDate.of(year, month, 1));
            data.setMonthEnd(LocalDate.of(year, month, 1).withDayOfMonth(LocalDate.of(year, month, 1).lengthOfMonth()));
            data.setTotalIncome((BigDecimal) row[2]);
            data.setTotalExpenses(((BigDecimal) row[3]).abs());
            data.setNetCashflow(data.getTotalIncome().add(data.getTotalExpenses()));
            data.setTransactionCount(((Number) row[4]).intValue());
            monthlyData.add(data);
        }
        
        return monthlyData;
    }

    Optional<Transaction> findByExternalId(String externalId);
}