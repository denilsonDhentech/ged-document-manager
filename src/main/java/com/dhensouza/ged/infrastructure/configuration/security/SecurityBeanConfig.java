package com.dhensouza.ged.infrastructure.configuration.security;

import com.dhensouza.ged.application.auth.service.AuthService;
import com.dhensouza.ged.application.auth.service.TokenService;
import com.dhensouza.ged.domain.repository.AccountRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;

@Configuration
public class SecurityBeanConfig {

    @Bean
    public TokenService tokenService(JwtEncoder jwtEncoder) {
        return new TokenService(jwtEncoder);
    }

    @Bean
    public AuthService authService(
            AccountRepository accountRepository,
            PasswordEncoder passwordEncoder,
            TokenService tokenService) {
        return new AuthService(accountRepository, passwordEncoder, tokenService);
    }
}
