package com.fintech.service;

import com.fintech.domain.Rule;
import com.fintech.dto.*;
import com.fintech.repo.RuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Transactional
public class RuleService {

    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private AuditLogService auditLogService;

    public List<RuleDto> getUserRules(UUID userId) {
        List<Rule> rules = ruleRepository.findByUserIdOrderByPriorityAsc(userId);
        return rules.stream()
                .map(RuleDto::fromEntity)
                .collect(Collectors.toList());
    }

    public RuleDto getRuleById(UUID userId, UUID ruleId) {
        Rule rule = ruleRepository.findById(ruleId)
                .orElseThrow(() -> new RuntimeException("Rule not found"));
        
        if (!rule.getUserId().equals(userId)) {
            throw new RuntimeException("Rule not found");
        }
        
        return RuleDto.fromEntity(rule);
    }

    public RuleDto createRule(UUID userId, CreateRuleRequest request) {
        Rule rule = new Rule();
        rule.setUserId(userId);
        rule.setName(request.getName());
        rule.setDescription(request.getDescription());
        rule.setConditions(request.getConditions());
        rule.setActions(request.getActions());
        rule.setPriority(request.getPriority());
        rule.setEnabled(request.getEnabled());

        // Validate rule conditions and actions
        validateRuleConditions(request.getConditions());
        validateRuleActions(request.getActions());

        Rule savedRule = ruleRepository.save(rule);

        // Log audit
        auditLogService.logRuleAction(com.fintech.domain.AuditLog.AuditAction.CREATE, savedRule, null);

        return RuleDto.fromEntity(savedRule);
    }

    public RuleDto updateRule(UUID userId, UUID ruleId, UpdateRuleRequest request) {
        Rule rule = ruleRepository.findById(ruleId)
                .orElseThrow(() -> new RuntimeException("Rule not found"));
        
        if (!rule.getUserId().equals(userId)) {
            throw new RuntimeException("Rule not found");
        }

        // Store old values for audit
        Rule oldRule = createRuleCopy(rule);

        // Update fields
        if (request.getName() != null) {
            rule.setName(request.getName());
        }
        if (request.getDescription() != null) {
            rule.setDescription(request.getDescription());
        }
        if (request.getConditions() != null) {
            validateRuleConditions(request.getConditions());
            rule.setConditions(request.getConditions());
        }
        if (request.getActions() != null) {
            validateRuleActions(request.getActions());
            rule.setActions(request.getActions());
        }
        if (request.getPriority() != null) {
            rule.setPriority(request.getPriority());
        }
        if (request.getEnabled() != null) {
            rule.setEnabled(request.getEnabled());
        }

        Rule savedRule = ruleRepository.save(rule);

        // Log audit
        auditLogService.logRuleAction(com.fintech.domain.AuditLog.AuditAction.UPDATE, savedRule, oldRule);

        return RuleDto.fromEntity(savedRule);
    }

    public void deleteRule(UUID userId, UUID ruleId) {
        Rule rule = ruleRepository.findById(ruleId)
                .orElseThrow(() -> new RuntimeException("Rule not found"));
        
        if (!rule.getUserId().equals(userId)) {
            throw new RuntimeException("Rule not found");
        }

        // Log audit before deletion
        auditLogService.logRuleAction(com.fintech.domain.AuditLog.AuditAction.DELETE, rule, null);

        ruleRepository.delete(rule);
    }

    public List<Rule> getEnabledRulesForUser(UUID userId) {
        return ruleRepository.findEnabledRulesByUserIdOrderByPriority(userId);
    }

    public RuleMatchResult applyRulesToTransaction(UUID userId, String merchant, String description) {
        List<Rule> rules = getEnabledRulesForUser(userId);
        
        for (Rule rule : rules) {
            RuleMatchResult match = evaluateRule(rule, merchant, description);
            if (match.isMatch()) {
                return match;
            }
        }
        
        return RuleMatchResult.noMatch();
    }

    private RuleMatchResult evaluateRule(Rule rule, String merchant, String description) {
        try {
            RuleConditions conditions = parseRuleConditions(rule.getConditions());
            RuleActions actions = parseRuleActions(rule.getActions());
            
            boolean merchantMatch = false;
            boolean descriptionMatch = false;
            
            // Check merchant pattern
            if (conditions.getMerchantPattern() != null) {
                Pattern pattern = Pattern.compile(conditions.getMerchantPattern(), Pattern.CASE_INSENSITIVE);
                merchantMatch = merchant != null && pattern.matcher(merchant).find();
            }
            
            // Check description pattern
            if (conditions.getDescriptionPattern() != null) {
                Pattern pattern = Pattern.compile(conditions.getDescriptionPattern(), Pattern.CASE_INSENSITIVE);
                descriptionMatch = description != null && pattern.matcher(description).find();
            }
            
            // Apply logic (AND or OR)
            boolean matches = false;
            if (conditions.getLogic().equals("AND")) {
                matches = merchantMatch && descriptionMatch;
            } else if (conditions.getLogic().equals("OR")) {
                matches = merchantMatch || descriptionMatch;
            } else {
                // Default to OR if logic is not specified
                matches = merchantMatch || descriptionMatch;
            }
            
            if (matches) {
                return RuleMatchResult.match(rule.getId(), rule.getName(), actions.getTargetCategoryId());
            }
            
        } catch (Exception e) {
            // Log error but don't fail the transaction
            System.err.println("Error evaluating rule " + rule.getId() + ": " + e.getMessage());
        }
        
        return RuleMatchResult.noMatch();
    }

    private void validateRuleConditions(String conditions) {
        try {
            parseRuleConditions(conditions);
        } catch (Exception e) {
            throw new RuntimeException("Invalid rule conditions: " + e.getMessage());
        }
    }

    private void validateRuleActions(String actions) {
        try {
            parseRuleActions(actions);
        } catch (Exception e) {
            throw new RuntimeException("Invalid rule actions: " + e.getMessage());
        }
    }

    private RuleConditions parseRuleConditions(String conditions) {
        // Simple JSON parsing for rule conditions
        // Expected format: {"merchantPattern": ".*starbucks.*", "descriptionPattern": ".*coffee.*", "logic": "OR"}
        try {
            RuleConditions ruleConditions = new RuleConditions();
            
            // Remove JSON braces and quotes
            String cleanConditions = conditions.trim();
            if (cleanConditions.startsWith("{") && cleanConditions.endsWith("}")) {
                cleanConditions = cleanConditions.substring(1, cleanConditions.length() - 1);
            }
            
            String[] pairs = cleanConditions.split(",");
            for (String pair : pairs) {
                String[] keyValue = pair.split(":");
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim().replaceAll("\"", "");
                    String value = keyValue[1].trim().replaceAll("\"", "");
                    
                    switch (key) {
                        case "merchantPattern":
                            ruleConditions.setMerchantPattern(value);
                            break;
                        case "descriptionPattern":
                            ruleConditions.setDescriptionPattern(value);
                            break;
                        case "logic":
                            ruleConditions.setLogic(value);
                            break;
                    }
                }
            }
            
            return ruleConditions;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse rule conditions: " + e.getMessage());
        }
    }

    private RuleActions parseRuleActions(String actions) {
        // Simple JSON parsing for rule actions
        // Expected format: {"targetCategoryId": "123e4567-e89b-12d3-a456-426614174000"}
        try {
            RuleActions ruleActions = new RuleActions();
            
            // Remove JSON braces and quotes
            String cleanActions = actions.trim();
            if (cleanActions.startsWith("{") && cleanActions.endsWith("}")) {
                cleanActions = cleanActions.substring(1, cleanActions.length() - 1);
            }
            
            String[] pairs = cleanActions.split(",");
            for (String pair : pairs) {
                String[] keyValue = pair.split(":");
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim().replaceAll("\"", "");
                    String value = keyValue[1].trim().replaceAll("\"", "");
                    
                    if (key.equals("targetCategoryId")) {
                        ruleActions.setTargetCategoryId(UUID.fromString(value));
                    }
                }
            }
            
            return ruleActions;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse rule actions: " + e.getMessage());
        }
    }

    private Rule createRuleCopy(Rule original) {
        Rule copy = new Rule();
        copy.setId(original.getId());
        copy.setUserId(original.getUserId());
        copy.setName(original.getName());
        copy.setDescription(original.getDescription());
        copy.setConditions(original.getConditions());
        copy.setActions(original.getActions());
        copy.setPriority(original.getPriority());
        copy.setEnabled(original.getEnabled());
        copy.setCreatedAt(original.getCreatedAt());
        copy.setUpdatedAt(original.getUpdatedAt());
        return copy;
    }

    // Inner classes for rule parsing
    private static class RuleConditions {
        private String merchantPattern;
        private String descriptionPattern;
        private String logic = "OR";

        public String getMerchantPattern() { return merchantPattern; }
        public void setMerchantPattern(String merchantPattern) { this.merchantPattern = merchantPattern; }
        public String getDescriptionPattern() { return descriptionPattern; }
        public void setDescriptionPattern(String descriptionPattern) { this.descriptionPattern = descriptionPattern; }
        public String getLogic() { return logic; }
        public void setLogic(String logic) { this.logic = logic; }
    }

    private static class RuleActions {
        private UUID targetCategoryId;

        public UUID getTargetCategoryId() { return targetCategoryId; }
        public void setTargetCategoryId(UUID targetCategoryId) { this.targetCategoryId = targetCategoryId; }
    }

    public static class RuleMatchResult {
        private boolean match;
        private UUID ruleId;
        private String ruleName;
        private UUID targetCategoryId;

        private RuleMatchResult(boolean match, UUID ruleId, String ruleName, UUID targetCategoryId) {
            this.match = match;
            this.ruleId = ruleId;
            this.ruleName = ruleName;
            this.targetCategoryId = targetCategoryId;
        }

        public static RuleMatchResult match(UUID ruleId, String ruleName, UUID targetCategoryId) {
            return new RuleMatchResult(true, ruleId, ruleName, targetCategoryId);
        }

        public static RuleMatchResult noMatch() {
            return new RuleMatchResult(false, null, null, null);
        }

        public boolean isMatch() { return match; }
        public UUID getRuleId() { return ruleId; }
        public String getRuleName() { return ruleName; }
        public UUID getTargetCategoryId() { return targetCategoryId; }
    }
}
