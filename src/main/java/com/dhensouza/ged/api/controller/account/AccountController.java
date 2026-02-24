package com.dhensouza.ged.api.controller.account;

import com.dhensouza.ged.application.account.dto.request.CreateAccountRequest;
import com.dhensouza.ged.application.account.dto.response.AccountResponse;
import com.dhensouza.ged.application.account.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> register(@RequestBody @Valid CreateAccountRequest request) {
        AccountResponse response = accountService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}