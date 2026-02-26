package com.dhensouza.ged.api.controller.account.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateAccountRequest(
        @Schema(description = "Unique username for login", example = "dhensouza")
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50)
        String username,

        @Schema(description = "Strong password with at least 6 characters", example = "P@ssw0rd123")
        @NotBlank(message = "Password is required")
        @Size(min = 6)
        String password,

        @Schema(description = "User access level", allowableValues = {"ADMIN", "USER", "VIEWER"})
        @NotBlank(message = "Access role is required")
        @Pattern(regexp = "^(ADMIN|USER|VIEWER)$")
        String role,

        @Schema(description = "Organization identifier", example = "T1")
        @NotBlank(message = "Tenant identifier is required")
        String tenantId
) {}