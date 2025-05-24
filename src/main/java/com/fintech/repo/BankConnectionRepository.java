package com.fintech.repo;

import com.fintech.domain.BankConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BankConnectionRepository extends JpaRepository<BankConnection, UUID> {

    List<BankConnection> findByUserIdAndConnectionStatus(UUID userId, BankConnection.ConnectionStatus connectionStatus);

    @Query("SELECT bc FROM BankConnection bc WHERE bc.userId = :userId AND bc.connectionStatus = 'ACTIVE'")
    List<BankConnection> findActiveConnectionsByUserId(@Param("userId") UUID userId);

    Optional<BankConnection> findByUserIdAndExternalConnectionId(UUID userId, String externalConnectionId);

    @Query("SELECT bc FROM BankConnection bc WHERE bc.externalConnectionId = :externalConnectionId")
    Optional<BankConnection> findByExternalConnectionId(@Param("externalConnectionId") String externalConnectionId);

    @Query("SELECT COUNT(bc) FROM BankConnection bc WHERE bc.userId = :userId")
    long countByUserId(@Param("userId") UUID userId);
}
