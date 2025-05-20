package com.fintech.web;

import com.fintech.dto.*;
import com.fintech.service.TransactionService;
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
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/accounts/{accountId}/transactions")
    public ResponseEntity<Page<TransactionDto>> getTransactions(
            @PathVariable UUID accountId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "postedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

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
