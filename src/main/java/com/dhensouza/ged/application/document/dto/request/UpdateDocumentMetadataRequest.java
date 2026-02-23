package com.dhensouza.ged.application.document.dto.request;

import java.util.List;

public record UpdateDocumentMetadataRequest(
        String title,
        String description,
        List<String> tags
) {}
