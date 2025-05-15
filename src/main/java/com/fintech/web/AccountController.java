package com.fintech.web;

import com.fintech.dto.AccountDto;
import com.fintech.dto.CreateAccountRequest;
import com.fintech.service.AccountService;
import com.fintech.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AuthService authService;

    @GetMapping
    public ResponseEntity<List<AccountDto>> getUserAccounts(Authentication authentication) {
        try {
            String email = authentication.getName();
            UUID userId = authService.getUserByEmail(email).getId();
            List<AccountDto> accounts = accountService.getUserAccounts(userId);
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getAccountById(@PathVariable UUID id, Authentication authentication) {
        try {
            String email = authentication.getName();
            UUID userId = authService.getUserByEmail(email).getId();
            AccountDto account = accountService.getAccountById(userId, id);
            return ResponseEntity.ok(account);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<AccountDto> createAccount(@Valid @RequestBody CreateAccountRequest request, 
                                                  Authentication authentication) {
        try {
            String email = authentication.getName();
            UUID userId = authService.getUserByEmail(email).getId();
            AccountDto account = accountService.createAccount(userId, request);
            return ResponseEntity.ok(account);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
