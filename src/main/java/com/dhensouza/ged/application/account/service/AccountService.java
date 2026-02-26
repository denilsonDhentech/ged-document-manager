package com.dhensouza.ged.application.account.service;

import com.dhensouza.ged.api.controller.account.dto.request.CreateAccountRequest;
import com.dhensouza.ged.api.controller.account.dto.request.UpdateAccountRequest;
import com.dhensouza.ged.application.account.dto.response.AccountResponse;
import com.dhensouza.ged.domain.entity.Account;
import com.dhensouza.ged.domain.exception.BusinessRuleException;
import com.dhensouza.ged.domain.exception.EntityNotFoundException;
import com.dhensouza.ged.domain.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;


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

    public List<AccountResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(AccountResponse::fromEntity)
                .toList();
    }

    @Transactional
    public AccountResponse update(java.util.UUID id, UpdateAccountRequest request) {
        Account account = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!account.getUsername().equals(request.username())) {
            repository.findByUsername(request.username()).ifPresent(u -> {
                throw new BusinessRuleException("Username already exists.");
            });
            account.setUsername(request.username());
        }

        account.changeRole(request.role());
        account.changeTenantId(request.tenantId());

        if (request.password() != null && !request.password().isBlank()) {
            account.setPassword(passwordEncoder.encode(request.password()));
        }

        Account updated = repository.save(account);
        return AccountResponse.fromEntity(updated);
    }

    public void delete(java.util.UUID id) {
        if (!repository.existsById(id)) {
            throw new com.dhensouza.ged.domain.exception.EntityNotFoundException("User not found with ID: " + id);
        }

        repository.deleteById(id);
    }
}
