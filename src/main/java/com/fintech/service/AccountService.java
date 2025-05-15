package com.fintech.service;

import com.fintech.domain.Account;
import com.fintech.dto.AccountDto;
import com.fintech.dto.CreateAccountRequest;
import com.fintech.repo.AccountRepository;
import com.fintech.repo.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public List<AccountDto> getUserAccounts(UUID userId) {
        List<Account> accounts = accountRepository.findByUserIdAndIsActiveTrue(userId);
        return accounts.stream()
                .map(this::enrichAccountWithBalance)
                .collect(Collectors.toList());
    }

    public AccountDto getAccountById(UUID userId, UUID accountId) {
        Account account = accountRepository.findByUserIdAndId(userId, accountId);
        if (account == null) {
            throw new RuntimeException("Account not found");
        }
        return enrichAccountWithBalance(account);
    }

    public AccountDto createAccount(UUID userId, CreateAccountRequest request) {
        Account account = new Account();
        account.setUserId(userId);
        account.setName(request.getName());
        account.setAccountType(request.getAccountType());
        account.setCurrency(request.getCurrency());
        account.setBalance(BigDecimal.ZERO);
        account.setIsActive(true);

        Account savedAccount = accountRepository.save(account);
        return enrichAccountWithBalance(savedAccount);
    }

    private AccountDto enrichAccountWithBalance(Account account) {
        AccountDto dto = AccountDto.fromEntity(account);
        
        // Calculate actual balance from transactions
        BigDecimal calculatedBalance = transactionRepository.calculateAccountBalance(account.getId());
        if (calculatedBalance == null) {
            calculatedBalance = BigDecimal.ZERO;
        }
        
        dto.setBalance(calculatedBalance);
        return dto;
    }
}
