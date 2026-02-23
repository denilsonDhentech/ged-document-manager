package com.dhensouza.ged.application.document.dto.request;

import com.dhensouza.ged.domain.enums.DocumentStatus;

public record DocumentFilter(
        String title,
        DocumentStatus status,
        String tag
) {}
