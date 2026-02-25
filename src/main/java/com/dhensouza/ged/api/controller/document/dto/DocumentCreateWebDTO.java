package com.dhensouza.ged.api.controller.document.dto;

import com.dhensouza.ged.application.document.dto.request.CreateDocumentRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

@Schema(description = "Request body for creating a new document")
public record DocumentCreateWebDTO(
        @NotBlank(message = "Title is required")
        @Size(min = 3, max = 150, message = "Title must be between 3 and 150 characters")
        @Schema(description = "The document title", example = "Quarterly Financial Report")
        String title,

        @Size(max = 500, message = "Description cannot exceed 500 characters")
        @Schema(description = "Brief description of the document", example = "Contains financial data for Q1 2026")
        String description,

        @Schema(description = "List of tags for categorization", example = "[\"finance\", \"reports\", \"2026\"]")
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