package com.fintech.repo;

import com.fintech.domain.WebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface WebhookEventRepository extends JpaRepository<WebhookEvent, UUID> {

    List<WebhookEvent> findByStatusOrderByCreatedAtAsc(WebhookEvent.EventStatus status);

    @Query("SELECT we FROM WebhookEvent we WHERE we.status = 'PENDING' AND we.createdAt < :cutoffTime ORDER BY we.createdAt ASC")
    List<WebhookEvent> findPendingEventsBefore(@Param("cutoffTime") LocalDateTime cutoffTime);

    @Query("SELECT we FROM WebhookEvent we WHERE we.status = 'FAILED' AND we.createdAt >= :since ORDER BY we.createdAt DESC")
    List<WebhookEvent> findFailedEventsSince(@Param("since") LocalDateTime since);

    @Query("SELECT COUNT(we) FROM WebhookEvent we WHERE we.status = :status")
    long countByStatus(@Param("status") WebhookEvent.EventStatus status);
}
