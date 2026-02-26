package com.dhensouza.ged.domain.repository;

import com.dhensouza.ged.domain.entity.DocumentVersion;
import io.micrometer.common.KeyValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentVersionRepository extends JpaRepository<DocumentVersion, UUID> {
    DocumentVersion save(DocumentVersion version);

    @Query("SELECT MAX(v.versionNumber) FROM DocumentVersion v WHERE v.document.id = :documentId")
    Optional<Integer> findMaxVersionByDocumentId(@Param("documentId") UUID documentId);

    Optional<DocumentVersion> findByDocumentIdAndVersionNumber(UUID docId, int versionNumber);

    long countByDocumentId(UUID documentId);

    @Query("SELECT v FROM DocumentVersion v " +
            "JOIN FETCH v.uploader " +
            "WHERE v.document.id = :documentId " +
            "ORDER BY v.versionNumber DESC")
    List<DocumentVersion> findByDocumentIdWithUploader(@Param("documentId") UUID documentId);
}
