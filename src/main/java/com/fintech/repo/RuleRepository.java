package com.fintech.repo;

import com.fintech.domain.Rule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RuleRepository extends JpaRepository<Rule, UUID> {

    List<Rule> findByUserIdAndEnabledTrueOrderByPriorityAsc(UUID userId);

    List<Rule> findByUserIdOrderByPriorityAsc(UUID userId);

    @Query("SELECT r FROM Rule r WHERE r.userId = :userId AND r.enabled = true ORDER BY r.priority ASC")
    List<Rule> findEnabledRulesByUserIdOrderByPriority(@Param("userId") UUID userId);
}
