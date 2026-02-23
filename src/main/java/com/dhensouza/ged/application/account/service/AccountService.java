package com.dhensouza.ged.application.account.service;

import com.dhensouza.ged.application.account.dto.request.CreateAccountRequest;
import com.dhensouza.ged.application.account.dto.response.AccountResponse;
import com.dhensouza.ged.domain.entity.Account;
import com.dhensouza.ged.domain.exception.BusinessRuleException;
import com.dhensouza.ged.domain.repository.AccountRepository;

public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    public AccountResponse create(CreateAccountRequest request) {
        accountRepository.findByUsername(request.username()).ifPresent(account -> {
            throw new BusinessRuleException("The username '" + request.username() + "' is already in use.");
        });

        Account account = Account.create(
                request.username(),
                request.password(),
                request.role(),
                request.tenantId()
        );

        Account savedAccount = accountRepository.save(account);

        return AccountResponse.fromEntity(savedAccount);
    }
}
