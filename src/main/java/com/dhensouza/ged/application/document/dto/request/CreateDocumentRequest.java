package com.dhensouza.ged.application.document.dto.request;

import java.util.List;
import java.util.UUID;

public record CreateDocumentRequest(
        String title,
        String description,
        List<String> tags,
        UUID uploaderId,
        String tenantId,
        String fileKey,
        String checksum,
        Long fileSize,
        String fileType
) {}
