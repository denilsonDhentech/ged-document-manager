package com.dhensouza.ged.domain.repository;

import com.dhensouza.ged.domain.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID>, JpaSpecificationExecutor<Document> {
    void deleteById(UUID id);
}
