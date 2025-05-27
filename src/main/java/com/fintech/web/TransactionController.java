package com.fintech.web;

import com.fintech.dto.*;
import com.fintech.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@Tag(name = "Transactions", description = "Transaction management operations")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/accounts/{accountId}/transactions")
    @Operation(summary = "Get transactions for an account", description = "Retrieve paginated transactions for a specific account with optional filtering")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved transactions"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public ResponseEntity<Page<TransactionDto>> getTransactions(
            @Parameter(description = "Account ID") @PathVariable UUID accountId,
            @Parameter(description = "Start date filter") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @Parameter(description = "End date filter") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @Parameter(description = "Search query") @RequestParam(required = false) String q,
            @Parameter(description = "Category ID filter") @RequestParam(required = false) UUID categoryId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "postedAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<TransactionDto> transactions = transactionService.getTransactionsByAccount(
                accountId, from, to, categoryId, q, pageable);
        
        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/accounts/{accountId}/transactions")
    public ResponseEntity<TransactionDto> createTransaction(
            @PathVariable UUID accountId,
            @Valid @RequestBody CreateTransactionRequest request) {
        
        TransactionDto transaction = transactionService.createTransaction(accountId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }

    @PatchMapping("/transactions/{transactionId}")
    public ResponseEntity<TransactionDto> updateTransaction(
            @PathVariable UUID transactionId,
            @Valid @RequestBody UpdateTransactionRequest request) {
        
        TransactionDto transaction = transactionService.updateTransaction(transactionId, request);
        return ResponseEntity.ok(transaction);
    }

    @DeleteMapping("/transactions/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable UUID transactionId) {
        transactionService.deleteTransaction(transactionId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/transactions/import")
    public ResponseEntity<CsvImportResult> importTransactions(
            @RequestParam("accountId") UUID accountId,
            @RequestParam("file") MultipartFile file) {
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        if (!file.getContentType().equals("text/csv") && 
            !file.getOriginalFilename().toLowerCase().endsWith(".csv")) {
            return ResponseEntity.badRequest().build();
        }
        
        CsvImportResult result = transactionService.importTransactionsFromCsv(accountId, file);
        return ResponseEntity.ok(result);
    }
}
