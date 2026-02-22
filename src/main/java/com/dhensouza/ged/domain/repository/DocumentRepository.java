package com.dhensouza.ged.domain.repository;

import com.dhensouza.ged.domain.entity.Document;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
public interface DocumentRepository {
    Document save(Document document);
    Optional<Document> findById(UUID id);
    List<Document> findAllByTenantId(String tenantId);
    void deleteById(UUID id);
}
