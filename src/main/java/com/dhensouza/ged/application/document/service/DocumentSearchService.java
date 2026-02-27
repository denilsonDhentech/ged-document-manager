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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class DocumentSearchService {
    private static final Logger log = LoggerFactory.getLogger(DocumentSearchService.class);

    private final DocumentRepository documentRepository;
    private final DocumentVersionRepository versionRepository;

    public DocumentSearchService(DocumentRepository documentRepository, DocumentVersionRepository versionRepository) {
        this.documentRepository = documentRepository;
        this.versionRepository = versionRepository;
    }

    public Page<DocumentResponse> search(DocumentFilter filter, String tenantId, Pageable pageable) {
        log.info("Iniciando busca de documentos para o Tenant: {}. Filtros: title='{}', status='{}', tag='{}'",
                tenantId, filter.title(), filter.status(), filter.tag());

        Specification<Document> spec = Specification
                .where(DocumentSpecifications.hasTenant(tenantId))
                .and(DocumentSpecifications.titleLike(filter.title()))
                .and(DocumentSpecifications.hasStatus(filter.status()))
                .and(DocumentSpecifications.hasTag(filter.tag()));

        log.debug("Executando query com paginação: página {}, tamanho {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<Document> documents = documentRepository.findAll(spec, pageable);

        log.info("Busca finalizada. Encontrados {} documentos para o tenant {}",
                documents.getTotalElements(), tenantId);

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