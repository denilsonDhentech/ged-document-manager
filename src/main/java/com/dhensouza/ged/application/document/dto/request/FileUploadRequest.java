package com.dhensouza.ged.application.document.dto.request;

import java.util.UUID;

public record FileUploadRequest(
        UUID documentId,
        UUID uploaderId,
        String fileKey,
        String checksum,
        Long fileSize,
        String fileType
) {}
