package com.dhensouza.ged.api.controller.account;

import com.dhensouza.ged.api.controller.account.dto.request.CreateAccountRequest;
import com.dhensouza.ged.api.controller.account.dto.request.UpdateAccountRequest;
import com.dhensouza.ged.application.account.dto.response.AccountResponse;
import com.dhensouza.ged.application.account.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<AccountResponse>> listAll() {
        List<AccountResponse> accounts = accountService.findAll();
        return ResponseEntity.ok(accounts);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<AccountResponse> update(
            @PathVariable java.util.UUID id,
            @RequestBody @Valid UpdateAccountRequest request) {
        return ResponseEntity.ok(accountService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable java.util.UUID id) {
        accountService.delete(id);
        return ResponseEntity.noContent().build();
    }
}