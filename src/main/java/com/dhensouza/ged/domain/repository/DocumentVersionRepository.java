package com.dhensouza.ged.domain.repository;

import com.dhensouza.ged.domain.entity.DocumentVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface DocumentVersionRepository extends JpaRepository<DocumentVersion, UUID> {
    DocumentVersion save(DocumentVersion version);

    @Query("SELECT MAX(v.versionNumber) FROM DocumentVersion v WHERE v.document.id = :documentId")
    Optional<Integer> findMaxVersionByDocumentId(@Param("documentId") UUID documentId);

    Optional<DocumentVersion> findByDocumentIdAndVersionNumber(UUID docId, int versionNumber);

    long countByDocumentId(UUID documentId);
}
