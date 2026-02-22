package com.dhensouza.ged.domain.repository;

import com.dhensouza.ged.domain.entity.DocumentVersion;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentVersionRepository {
    DocumentVersion save(DocumentVersion version);
    List<DocumentVersion> findAllByDocumentId(UUID documentId);
    Optional<DocumentVersion> findLatestByDocumentId(UUID documentId);
}
