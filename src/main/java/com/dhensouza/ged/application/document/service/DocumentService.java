package com.dhensouza.ged.application.document.service;

import com.dhensouza.ged.application.document.dto.request.CreateDocumentRequest;
import com.dhensouza.ged.application.document.dto.request.UpdateDocumentMetadataRequest;
import com.dhensouza.ged.application.document.dto.response.DocumentResponse;
import com.dhensouza.ged.domain.entity.Account;
import com.dhensouza.ged.domain.entity.AuditLog;
import com.dhensouza.ged.domain.entity.Document;
import com.dhensouza.ged.domain.entity.DocumentVersion;
import com.dhensouza.ged.domain.enums.AuditAction;
import com.dhensouza.ged.domain.exception.EntityNotFoundException;
import com.dhensouza.ged.domain.repository.AccountRepository;
import com.dhensouza.ged.domain.repository.AuditLogRepository;
import com.dhensouza.ged.domain.repository.DocumentRepository;
import com.dhensouza.ged.domain.repository.DocumentVersionRepository;

import java.util.UUID;

public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentVersionRepository versionRepository;
    private final AccountRepository accountRepository;
    private final AuditLogRepository auditLogRepository;

    public DocumentService(
            DocumentRepository documentRepository,
            DocumentVersionRepository versionRepository,
            AccountRepository accountRepository,
            AuditLogRepository auditLogRepository) {
        this.documentRepository = documentRepository;
        this.versionRepository = versionRepository;
        this.accountRepository = accountRepository;
        this.auditLogRepository = auditLogRepository;
    }

    public DocumentResponse createDocument(CreateDocumentRequest request) {
        Account uploader = accountRepository.findById(request.uploaderId())
                .orElseThrow(() -> new EntityNotFoundException("Uploader account not found"));

        Document document = new Document(
                request.title(),
                request.description(),
                uploader,
                request.tenantId(),
                request.tags()
        );

        Document savedDocument = documentRepository.save(document);

        DocumentVersion initialVersion = new DocumentVersion(
                savedDocument,
                1,
                request.fileKey(),
                request.checksum(),
                request.fileSize(),
                request.fileType(),
                uploader
        );
        versionRepository.save(initialVersion);

        return new DocumentResponse(
                savedDocument.getId(),
                savedDocument.getTitle(),
                savedDocument.getStatus().name(),
                1
        );
    }

// No DocumentService.java

    public void updateMetadata(UUID docId, UpdateDocumentMetadataRequest request) {
        Document document = documentRepository.findById(docId)
                .orElseThrow(() -> new EntityNotFoundException("Document not found"));

        document.updateMetadata(request.title(), request.description(), request.tags());

        documentRepository.save(document);

        AuditLog log = new AuditLog(
                document.getOwner(),
                AuditAction.UPDATE_DOCUMENT,
                document.getId(),
                "{ \"info\": \"Metadata updated by Service\" }"
        );
        auditLogRepository.save(log);
    }
}
