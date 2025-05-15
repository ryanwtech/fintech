package com.fintech.repo;

import com.fintech.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    List<Account> findByUserIdAndIsActiveTrue(UUID userId);
    
    List<Account> findByUserId(UUID userId);
    
    @Query("SELECT a FROM Account a WHERE a.userId = :userId AND a.id = :accountId")
    Account findByUserIdAndId(@Param("userId") UUID userId, @Param("accountId") UUID accountId);
    
    boolean existsByUserIdAndId(UUID userId, UUID accountId);
}
