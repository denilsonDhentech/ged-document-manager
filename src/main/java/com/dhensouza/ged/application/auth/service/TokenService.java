package com.dhensouza.ged.application.auth.service;

import com.dhensouza.ged.domain.entity.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Instant;

public class TokenService {
    private static final Logger log = LoggerFactory.getLogger(TokenService.class);
    private final JwtEncoder encoder;

    public TokenService(JwtEncoder encoder) {
        this.encoder = encoder;
    }

    public String generateToken(Account account) {
        log.debug("Iniciando geração de claims para o usuário: {}", account.getUsername());

        Instant now = Instant.now();
        long expiry = 3600L;

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("ged-document-manager")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(account.getId().toString())
                .claim("username", account.getUsername())
                .claim("role", account.getRole())
                .claim("tenantId", account.getTenantId())
                .build();

        String token = encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        log.info("Token JWT gerado com sucesso para o usuário: {}", account.getUsername());

        return token;
    }
}
