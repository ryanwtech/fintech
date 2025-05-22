package com.fintech.web;

import com.fintech.dto.*;
import com.fintech.service.BudgetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @GetMapping("/budgets")
    public ResponseEntity<BudgetDto> getBudgetByMonth(
            @RequestParam UUID userId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") String month) {
        BudgetDto budget = budgetService.getBudgetByMonth(userId, month);
        return ResponseEntity.ok(budget);
    }

    @PostMapping("/budgets")
    public ResponseEntity<BudgetDto> createBudget(
            @RequestParam UUID userId,
            @Valid @RequestBody CreateBudgetRequest request) {
        BudgetDto budget = budgetService.createBudget(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(budget);
    }

    @GetMapping("/budgets/{budgetId}")
    public ResponseEntity<BudgetDto> getBudgetById(
            @RequestParam UUID userId,
            @PathVariable UUID budgetId) {
        BudgetDto budget = budgetService.getBudgetById(userId, budgetId);
        return ResponseEntity.ok(budget);
    }

    @PatchMapping("/budgets/{budgetId}/items/{categoryId}")
    public ResponseEntity<BudgetItemDto> updateBudgetItem(
            @RequestParam UUID userId,
            @PathVariable UUID budgetId,
            @PathVariable UUID categoryId,
            @Valid @RequestBody UpdateBudgetItemRequest request) {
        BudgetItemDto budgetItem = budgetService.updateBudgetItem(userId, budgetId, categoryId, request);
        return ResponseEntity.ok(budgetItem);
    }

    @DeleteMapping("/budgets/{budgetId}")
    public ResponseEntity<Void> deleteBudget(
            @RequestParam UUID userId,
            @PathVariable UUID budgetId) {
        budgetService.deleteBudget(userId, budgetId);
        return ResponseEntity.noContent().build();
    }
}
