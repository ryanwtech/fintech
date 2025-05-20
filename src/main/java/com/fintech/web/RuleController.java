package com.fintech.web;

import com.fintech.dto.*;
import com.fintech.service.RuleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class RuleController {

    @Autowired
    private RuleService ruleService;

    @GetMapping("/rules")
    public ResponseEntity<List<RuleDto>> getUserRules(@RequestParam UUID userId) {
        List<RuleDto> rules = ruleService.getUserRules(userId);
        return ResponseEntity.ok(rules);
    }

    @GetMapping("/rules/{ruleId}")
    public ResponseEntity<RuleDto> getRuleById(
            @RequestParam UUID userId,
            @PathVariable UUID ruleId) {
        RuleDto rule = ruleService.getRuleById(userId, ruleId);
        return ResponseEntity.ok(rule);
    }

    @PostMapping("/rules")
    public ResponseEntity<RuleDto> createRule(
            @RequestParam UUID userId,
            @Valid @RequestBody CreateRuleRequest request) {
        RuleDto rule = ruleService.createRule(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(rule);
    }

    @PatchMapping("/rules/{ruleId}")
    public ResponseEntity<RuleDto> updateRule(
            @RequestParam UUID userId,
            @PathVariable UUID ruleId,
            @Valid @RequestBody UpdateRuleRequest request) {
        RuleDto rule = ruleService.updateRule(userId, ruleId, request);
        return ResponseEntity.ok(rule);
    }

    @DeleteMapping("/rules/{ruleId}")
    public ResponseEntity<Void> deleteRule(
            @RequestParam UUID userId,
            @PathVariable UUID ruleId) {
        ruleService.deleteRule(userId, ruleId);
        return ResponseEntity.noContent().build();
    }
}
