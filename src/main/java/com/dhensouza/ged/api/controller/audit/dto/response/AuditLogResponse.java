package com.dhensouza.ged.api.controller.audit.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record AuditLogResponse(
        UUID id,
        LocalDateTime timestamp,
        String username,
        String action,
        UUID documentId,
        String metadata
) {}
