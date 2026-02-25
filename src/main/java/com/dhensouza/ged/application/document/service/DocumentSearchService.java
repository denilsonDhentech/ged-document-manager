package com.dhensouza.ged.application.document.service;

import com.dhensouza.ged.application.document.dto.request.DocumentFilter;
import com.dhensouza.ged.application.document.dto.response.DocumentResponse;
import com.dhensouza.ged.domain.entity.Document;
import com.dhensouza.ged.domain.repository.DocumentRepository;
import com.dhensouza.ged.domain.repository.DocumentVersionRepository;
import com.dhensouza.ged.domain.repository.specification.DocumentSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class DocumentSearchService {

    private final DocumentRepository documentRepository;
    private final DocumentVersionRepository versionRepository;

    public DocumentSearchService(DocumentRepository documentRepository, DocumentVersionRepository versionRepository) {
        this.documentRepository = documentRepository;
        this.versionRepository = versionRepository;
    }

    public Page<DocumentResponse> search(DocumentFilter filter, String tenantId, Pageable pageable) {
        Specification<Document> spec = Specification
                .where(DocumentSpecifications.hasTenant(tenantId))
                .and(DocumentSpecifications.titleLike(filter.title()))
                .and(DocumentSpecifications.hasStatus(filter.status()))
                .and(DocumentSpecifications.hasTag(filter.tag()));

        Page<Document> documents = documentRepository.findAll(spec, pageable);

        return documents.map(doc -> {
            long versionCount = versionRepository.countByDocumentId(doc.getId());
            return new DocumentResponse(
                    doc.getId(),
                    doc.getTitle(),
                    doc.getStatus().name(),
                    (int) versionCount,
                    doc.getCreatedAt()
            );
        });
    }
}