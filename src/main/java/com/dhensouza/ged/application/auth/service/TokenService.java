package com.dhensouza.ged.application.auth.service;

import com.dhensouza.ged.domain.entity.Account;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Instant;

public class TokenService {

    private final JwtEncoder encoder;

    public TokenService(JwtEncoder encoder) {
        this.encoder = encoder;
    }

    public String generateToken(Account account) {
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

        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
