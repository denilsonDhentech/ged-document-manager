package com.dhensouza.ged.application.account.dto.response;

import java.util.UUID;

public record AccountResponse(
        UUID id,
        String username,
        String role,
        String tenantId
) {}
