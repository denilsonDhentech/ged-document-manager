package com.dhensouza.ged.application.auth.dto.response;


public record LoginResponse(
        String token,
        String username,
        String role,
        String tenantId
) {}