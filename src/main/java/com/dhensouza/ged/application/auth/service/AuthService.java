package com.dhensouza.ged.application.auth.service;

import com.dhensouza.ged.application.auth.dto.request.LoginRequest;
import com.dhensouza.ged.application.auth.dto.response.LoginResponse;
import com.dhensouza.ged.domain.entity.Account;
import com.dhensouza.ged.domain.exception.BusinessRuleException;
import com.dhensouza.ged.domain.repository.AccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthService(AccountRepository accountRepository,
                       PasswordEncoder passwordEncoder,
                       TokenService tokenService) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public LoginResponse authenticate(LoginRequest request) {
        Account account = accountRepository.findByUsername(request.username())
                .orElseThrow(() -> new BusinessRuleException("Invalid username or password."));

        if (!passwordEncoder.matches(request.password(), account.getPassword())) {
            throw new BusinessRuleException("Invalid username or password.");
        }

        String token = tokenService.generateToken(account);

        return new LoginResponse(token);
    }
}
