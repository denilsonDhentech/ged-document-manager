package com.dhensouza.ged.application.document.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DocumentVersionWebDTO(
        @NotBlank String fileKey,
        @NotBlank String checksum,
        @NotNull Long fileSize,
        @NotBlank String fileType
) {}
