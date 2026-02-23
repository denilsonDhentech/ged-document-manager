package com.dhensouza.ged.application.document.service;

import com.dhensouza.ged.application.document.dto.request.DocumentFilter;
import com.dhensouza.ged.domain.entity.Document;
import com.dhensouza.ged.domain.repository.DocumentRepository;
import com.dhensouza.ged.domain.repository.specification.DocumentSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;


public class DocumentSearchService {

    private final DocumentRepository documentRepository;

    public DocumentSearchService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public Page<Document> search(DocumentFilter filter, String tenantId, Pageable pageable) {
        Specification<Document> spec = Specification
                .where(DocumentSpecifications.hasTenant(tenantId))
                .and(DocumentSpecifications.titleLike(filter.title()))
                .and(DocumentSpecifications.hasStatus(filter.status()))
                .and(DocumentSpecifications.hasTag(filter.tag()));

        return documentRepository.findAll(spec, pageable);
    }
}
