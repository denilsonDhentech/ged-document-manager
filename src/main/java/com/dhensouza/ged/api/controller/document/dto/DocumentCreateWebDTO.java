package com.dhensouza.ged.api.controller.document.dto;

import com.dhensouza.ged.application.document.dto.request.CreateDocumentRequest;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.UUID;

public record DocumentCreateWebDTO(
        @NotBlank(message = "Title is required")
        String title,

        String description,

        List<String> tags
) {
    public CreateDocumentRequest toServiceRequest(UUID uploaderId, String tenantId) {
        return new CreateDocumentRequest(
                this.title(),
                this.description(),
                this.tags(),
                uploaderId,
                tenantId
        );
    }
}