package com.dhensouza.ged.application.document.dto.response;

import com.dhensouza.ged.domain.entity.Document;

import java.time.LocalDateTime;
import java.util.UUID;

public record DocumentResponse(
        UUID id,
        String title,
        String status,
        Integer versionCount,
        LocalDateTime createdAt
) {
    public static DocumentResponse fromEntity(Document doc) {
        return new DocumentResponse(
                doc.getId(),
                doc.getTitle(),
                doc.getStatus().name(),
                0,
                doc.getCreatedAt()
        );
    }
}
