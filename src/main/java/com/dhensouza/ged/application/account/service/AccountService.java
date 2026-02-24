package com.dhensouza.ged.application.account.service;

import com.dhensouza.ged.application.account.dto.request.CreateAccountRequest;
import com.dhensouza.ged.application.account.dto.response.AccountResponse;
import com.dhensouza.ged.domain.entity.Account;
import com.dhensouza.ged.domain.exception.BusinessRuleException;
import com.dhensouza.ged.domain.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;


public class AccountService {

    private final AccountRepository repository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(AccountRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AccountResponse create(CreateAccountRequest request) {
        if (repository.findByUsername(request.username()).isPresent()) {
            throw new BusinessRuleException("Username already exists.");
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        Account account = Account.create(
                request.username(),
                encodedPassword,
                request.role(),
                request.tenantId()
        );

        Account savedAccount = repository.save(account);
        return AccountResponse.fromEntity(savedAccount);
    }
}
