package com.fintech.service;

import com.fintech.domain.Rule;
import com.fintech.dto.CreateRuleRequest;
import com.fintech.dto.UpdateRuleRequest;
import com.fintech.repo.RuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
class RuleServiceTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RuleRepository ruleRepository;

    private RuleService ruleService;
    private AuditService auditService;

    private UUID userId;

    @BeforeEach
    void setUp() {
        auditService = new AuditService();
        ruleService = new RuleService();
        
        // Use reflection to set the repository
        try {
            var repositoryField = RuleService.class.getDeclaredField("ruleRepository");
            repositoryField.setAccessible(true);
            repositoryField.set(ruleService, ruleRepository);
            
            var auditField = RuleService.class.getDeclaredField("auditService");
            auditField.setAccessible(true);
            auditField.set(ruleService, auditService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject dependencies", e);
        }
        
        userId = UUID.randomUUID();
    }

    @Test
    void testCreateRule() {
        // Given
        CreateRuleRequest request = new CreateRuleRequest();
        request.setName("Coffee Rule");
        request.setDescription("Auto-categorize coffee transactions");
        request.setConditions("{\"merchantPattern\": \".*starbucks.*\", \"descriptionPattern\": \".*coffee.*\", \"logic\": \"OR\"}");
        request.setActions("{\"targetCategoryId\": \"" + UUID.randomUUID() + "\"}");
        request.setPriority(1);
        request.setEnabled(true);

        // When
        var result = ruleService.createRule(userId, request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Coffee Rule");
        assertThat(result.getDescription()).isEqualTo("Auto-categorize coffee transactions");
        assertThat(result.getConditions()).isEqualTo("{\"merchantPattern\": \".*starbucks.*\", \"descriptionPattern\": \".*coffee.*\", \"logic\": \"OR\"}");
        assertThat(result.getActions()).isEqualTo("{\"targetCategoryId\": \"" + request.getActions().split("\"")[3] + "\"}");
        assertThat(result.getPriority()).isEqualTo(1);
        assertThat(result.getEnabled()).isTrue();
    }

    @Test
    void testCreateRuleWithInvalidConditions() {
        // Given
        CreateRuleRequest request = new CreateRuleRequest();
        request.setName("Invalid Rule");
        request.setConditions("invalid json");
        request.setActions("{\"targetCategoryId\": \"" + UUID.randomUUID() + "\"}");

        // When & Then
        assertThatThrownBy(() -> ruleService.createRule(userId, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid rule conditions");
    }

    @Test
    void testCreateRuleWithInvalidActions() {
        // Given
        CreateRuleRequest request = new CreateRuleRequest();
        request.setName("Invalid Rule");
        request.setConditions("{\"merchantPattern\": \".*test.*\"}");
        request.setActions("invalid json");

        // When & Then
        assertThatThrownBy(() -> ruleService.createRule(userId, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid rule actions");
    }

    @Test
    void testGetUserRules() {
        // Given
        createTestRule("Rule 1", 1);
        createTestRule("Rule 2", 2);
        createTestRule("Rule 3", 0);

        // When
        var rules = ruleService.getUserRules(userId);

        // Then
        assertThat(rules).hasSize(3);
        assertThat(rules.get(0).getName()).isEqualTo("Rule 3"); // Priority 0 first
        assertThat(rules.get(1).getName()).isEqualTo("Rule 1"); // Priority 1 second
        assertThat(rules.get(2).getName()).isEqualTo("Rule 2"); // Priority 2 last
    }

    @Test
    void testUpdateRule() {
        // Given
        var rule = createTestRule("Original Rule", 1);
        UpdateRuleRequest request = new UpdateRuleRequest();
        request.setName("Updated Rule");
        request.setDescription("Updated description");
        request.setPriority(5);

        // When
        var result = ruleService.updateRule(userId, rule.getId(), request);

        // Then
        assertThat(result.getName()).isEqualTo("Updated Rule");
        assertThat(result.getDescription()).isEqualTo("Updated description");
        assertThat(result.getPriority()).isEqualTo(5);
    }

    @Test
    void testDeleteRule() {
        // Given
        var rule = createTestRule("Rule to Delete", 1);

        // When
        ruleService.deleteRule(userId, rule.getId());

        // Then
        assertThat(ruleRepository.findById(rule.getId())).isEmpty();
    }

    @Test
    void testRuleMatching_MerchantPattern() {
        // Given
        createTestRule("Starbucks Rule", "{\"merchantPattern\": \".*starbucks.*\"}", 
                      "{\"targetCategoryId\": \"" + UUID.randomUUID() + "\"}");

        // When
        var match = ruleService.applyRulesToTransaction(userId, "Starbucks Coffee", "Morning coffee");

        // Then
        assertThat(match.isMatch()).isTrue();
        assertThat(match.getRuleName()).isEqualTo("Starbucks Rule");
    }

    @Test
    void testRuleMatching_DescriptionPattern() {
        // Given
        createTestRule("Coffee Rule", "{\"descriptionPattern\": \".*coffee.*\"}", 
                      "{\"targetCategoryId\": \"" + UUID.randomUUID() + "\"}");

        // When
        var match = ruleService.applyRulesToTransaction(userId, "Local Cafe", "Morning coffee");

        // Then
        assertThat(match.isMatch()).isTrue();
        assertThat(match.getRuleName()).isEqualTo("Coffee Rule");
    }

    @Test
    void testRuleMatching_AndLogic() {
        // Given
        createTestRule("Strict Rule", "{\"merchantPattern\": \".*starbucks.*\", \"descriptionPattern\": \".*coffee.*\", \"logic\": \"AND\"}", 
                      "{\"targetCategoryId\": \"" + UUID.randomUUID() + "\"}");

        // When - Both conditions match
        var match1 = ruleService.applyRulesToTransaction(userId, "Starbucks Coffee", "Morning coffee");
        // When - Only merchant matches
        var match2 = ruleService.applyRulesToTransaction(userId, "Starbucks Coffee", "Sandwich");
        // When - Only description matches
        var match3 = ruleService.applyRulesToTransaction(userId, "Local Cafe", "Morning coffee");

        // Then
        assertThat(match1.isMatch()).isTrue();
        assertThat(match2.isMatch()).isFalse();
        assertThat(match3.isMatch()).isFalse();
    }

    @Test
    void testRuleMatching_OrLogic() {
        // Given
        createTestRule("Flexible Rule", "{\"merchantPattern\": \".*starbucks.*\", \"descriptionPattern\": \".*coffee.*\", \"logic\": \"OR\"}", 
                      "{\"targetCategoryId\": \"" + UUID.randomUUID() + "\"}");

        // When - Both conditions match
        var match1 = ruleService.applyRulesToTransaction(userId, "Starbucks Coffee", "Morning coffee");
        // When - Only merchant matches
        var match2 = ruleService.applyRulesToTransaction(userId, "Starbucks Coffee", "Sandwich");
        // When - Only description matches
        var match3 = ruleService.applyRulesToTransaction(userId, "Local Cafe", "Morning coffee");
        // When - Neither matches
        var match4 = ruleService.applyRulesToTransaction(userId, "McDonald's", "Burger");

        // Then
        assertThat(match1.isMatch()).isTrue();
        assertThat(match2.isMatch()).isTrue();
        assertThat(match3.isMatch()).isTrue();
        assertThat(match4.isMatch()).isFalse();
    }

    @Test
    void testRuleMatching_PriorityOrder() {
        // Given
        UUID categoryId1 = UUID.randomUUID();
        UUID categoryId2 = UUID.randomUUID();
        
        createTestRule("Low Priority Rule", "{\"merchantPattern\": \".*coffee.*\"}", 
                      "{\"targetCategoryId\": \"" + categoryId1 + "\"}", 10);
        createTestRule("High Priority Rule", "{\"merchantPattern\": \".*starbucks.*\"}", 
                      "{\"targetCategoryId\": \"" + categoryId2 + "\"}", 1);

        // When
        var match = ruleService.applyRulesToTransaction(userId, "Starbucks Coffee", "Morning coffee");

        // Then - Should match the higher priority rule (lower number)
        assertThat(match.isMatch()).isTrue();
        assertThat(match.getRuleName()).isEqualTo("High Priority Rule");
        assertThat(match.getTargetCategoryId()).isEqualTo(categoryId2);
    }

    @Test
    void testRuleMatching_DisabledRules() {
        // Given
        createTestRule("Disabled Rule", "{\"merchantPattern\": \".*starbucks.*\"}", 
                      "{\"targetCategoryId\": \"" + UUID.randomUUID() + "\"}", 1, false);

        // When
        var match = ruleService.applyRulesToTransaction(userId, "Starbucks Coffee", "Morning coffee");

        // Then
        assertThat(match.isMatch()).isFalse();
    }

    @Test
    void testRuleMatching_NoRules() {
        // When
        var match = ruleService.applyRulesToTransaction(userId, "Any Merchant", "Any Description");

        // Then
        assertThat(match.isMatch()).isFalse();
    }

    @Test
    void testRuleMatching_CaseInsensitive() {
        // Given
        createTestRule("Case Insensitive Rule", "{\"merchantPattern\": \".*STARBUCKS.*\"}", 
                      "{\"targetCategoryId\": \"" + UUID.randomUUID() + "\"}");

        // When
        var match = ruleService.applyRulesToTransaction(userId, "starbucks coffee", "Morning coffee");

        // Then
        assertThat(match.isMatch()).isTrue();
    }

    private Rule createTestRule(String name, int priority) {
        return createTestRule(name, "{\"merchantPattern\": \".*test.*\"}", 
                            "{\"targetCategoryId\": \"" + UUID.randomUUID() + "\"}", priority, true);
    }

    private Rule createTestRule(String name, String conditions, String actions) {
        return createTestRule(name, conditions, actions, 1, true);
    }

    private Rule createTestRule(String name, String conditions, String actions, int priority) {
        return createTestRule(name, conditions, actions, priority, true);
    }

    private Rule createTestRule(String name, String conditions, String actions, int priority, boolean enabled) {
        Rule rule = new Rule();
        rule.setUserId(userId);
        rule.setName(name);
        rule.setDescription("Test rule");
        rule.setConditions(conditions);
        rule.setActions(actions);
        rule.setPriority(priority);
        rule.setEnabled(enabled);
        
        return entityManager.persistAndFlush(rule);
    }
}
