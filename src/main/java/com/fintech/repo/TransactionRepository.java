package com.fintech.repo;

import com.fintech.domain.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
}