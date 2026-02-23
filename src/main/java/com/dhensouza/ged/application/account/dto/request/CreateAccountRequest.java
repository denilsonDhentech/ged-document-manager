package com.dhensouza.ged.application.account.dto.request;

public record CreateAccountRequest(
        String username,
        String password,
        String role,
        String tenantId
) {}
