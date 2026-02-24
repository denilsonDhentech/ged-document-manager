package com.dhensouza.ged.api.controller.document.dto;

import com.dhensouza.ged.application.document.dto.request.CreateDocumentRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record DocumentCreateWebDTO(
        @NotBlank(message = "Title is required")
        String title,

        String description,

        List<String> tags, // Corrigido o tipo aqui

        @NotBlank(message = "File key (S3/Storage) is required")
        String fileKey,

        @NotBlank(message = "Checksum is required for integrity")
        String checksum,

        @NotNull(message = "File size is required")
        Long fileSize,

        @NotBlank(message = "File type is required")
        String fileType
) {
    public CreateDocumentRequest toServiceRequest(UUID uploaderId, String tenantId) {

        return new CreateDocumentRequest(
                this.title(),
                this.description(),
                this.tags(),
                uploaderId,
                tenantId,
                this.fileKey(),
                this.checksum(),
                this.fileSize(),
                this.fileType()
        );
    }
}