package com.dhensouza.ged.application.service;

import com.dhensouza.ged.domain.entity.Account;
import com.dhensouza.ged.domain.exception.BusinessRuleException;
import com.dhensouza.ged.domain.repository.AccountRepository;

public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account createAccount(String username, String password, String role, String tenantId) {
        accountRepository.findByUsername(username).ifPresent(account -> {
            throw new BusinessRuleException("Username already exists: " + username);
        });

        Account newAccount = new Account(username, password, role, tenantId);
        return accountRepository.save(newAccount);
    }
}
