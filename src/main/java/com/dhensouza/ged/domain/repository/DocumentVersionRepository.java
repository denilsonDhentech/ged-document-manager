package com.dhensouza.ged.domain.repository;

import com.dhensouza.ged.domain.entity.DocumentVersion;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentVersionRepository {
    DocumentVersion save(DocumentVersion version);
    Optional<Integer> findMaxVersionByDocumentId(UUID documentId);
}
