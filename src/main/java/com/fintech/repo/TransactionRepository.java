package com.fintech.repo;

import com.fintech.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    @Query("SELECT COALESCE(SUM(CASE WHEN t.transactionType = 'CREDIT' THEN t.amount ELSE -t.amount END), 0) " +
           "FROM Transaction t WHERE t.accountId = :accountId")
    BigDecimal calculateAccountBalance(@Param("accountId") UUID accountId);
}
