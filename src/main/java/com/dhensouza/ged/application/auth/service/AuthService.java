package com.dhensouza.ged.application.auth.service;

import com.dhensouza.ged.application.auth.dto.request.LoginRequest;
import com.dhensouza.ged.application.auth.dto.response.LoginResponse;
import com.dhensouza.ged.domain.entity.Account;
import com.dhensouza.ged.domain.exception.BusinessRuleException;
import com.dhensouza.ged.domain.repository.AccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class AuthService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    public AuthService(AccountRepository accountRepository,
                       PasswordEncoder passwordEncoder,
                       TokenService tokenService) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public LoginResponse authenticate(LoginRequest request) {
        log.info("Iniciando autenticação para o usuário: {}", request.username());

        Account account = accountRepository.findByUsername(request.username())
                .orElseThrow(() -> {
                    log.warn("Tentativa de login falhou: usuário {} não encontrado", request.username());
                    return new BusinessRuleException("User not found");
                });

        if (!passwordEncoder.matches(request.password(), account.getPassword())) {
            log.warn("Senha incorreta para o usuário: {}", request.username());
            throw new BusinessRuleException("Invalid credentials");
        }

        try {
            log.debug("Gerando token JWT para o usuário: {}", account.getUsername());
            String token = tokenService.generateToken(account);

            log.info("Usuário {} autenticado com sucesso", account.getUsername());
            return new LoginResponse(
                    token,
                    account.getUsername(),
                    account.getRole(),
                    account.getTenantId()
            );
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Erro crítico ao gerar token para {}: {}", account.getUsername(), e.getMessage(), e);
            throw new BusinessRuleException("Error generating token: " + e.getMessage());
        }
    }
}
