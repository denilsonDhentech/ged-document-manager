package com.dhensouza.ged.application.auth.service;

import com.dhensouza.ged.application.auth.dto.request.LoginRequest;
import com.dhensouza.ged.application.auth.dto.response.LoginResponse;
import com.dhensouza.ged.domain.entity.Account;
import com.dhensouza.ged.domain.exception.BusinessRuleException;
import com.dhensouza.ged.domain.repository.AccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;


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
        System.out.println("Iniciando autenticação para: " + request.username());

        Account account = accountRepository.findByUsername(request.username())
                .orElseThrow(() -> new BusinessRuleException("Usuário não encontrado"));

        if (!passwordEncoder.matches(request.password(), account.getPassword())) {
            System.out.println("Senha incorreta!");
            throw new BusinessRuleException("Credenciais inválidas");
        }

        try {
            System.out.println("Gerando token...");
            String token = tokenService.generateToken(account);
            return new LoginResponse(token);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessRuleException("Erro ao gerar token: " + e.getMessage());
        }
    }
}
