package com.dhensouza.ged.application.document.dto.response;

import java.time.LocalDateTime;

public record DocumentVersionResponse(
        Integer versionNumber,
        String fileKey,
        Long size,
        String contentType,
        String uploadedBy,
        LocalDateTime uploadedAt,
        String checksum
) {}
