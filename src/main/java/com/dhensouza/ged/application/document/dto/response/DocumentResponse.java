package com.dhensouza.ged.application.document.dto.response;

import java.util.UUID;

public record DocumentResponse(
        UUID id,
        String title,
        String status,
        Integer currentVersion
) {}
