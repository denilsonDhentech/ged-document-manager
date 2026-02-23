package com.dhensouza.ged.application.account.dto.response;

import com.dhensouza.ged.domain.entity.Account;

import java.util.UUID;

public record AccountResponse(
        UUID id,
        String username,
        String role,
        String tenantId
) {
    public static AccountResponse fromEntity(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getUsername(),
                account.getRole(),
                account.getTenantId()
        );
    }
}
