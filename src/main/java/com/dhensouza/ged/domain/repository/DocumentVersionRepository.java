package com.dhensouza.ged.domain.repository;

import com.dhensouza.ged.domain.entity.DocumentVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentVersionRepository extends JpaRepository<DocumentVersion, UUID> {
    DocumentVersion save(DocumentVersion version);
    Optional<Integer> findMaxVersionByDocumentId(UUID documentId);
}
