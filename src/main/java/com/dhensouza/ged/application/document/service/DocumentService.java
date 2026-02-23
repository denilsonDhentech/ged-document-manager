package com.dhensouza.ged.application.document.service;

import com.dhensouza.ged.application.document.dto.request.CreateDocumentRequest;
import com.dhensouza.ged.application.document.dto.response.DocumentResponse;
import com.dhensouza.ged.domain.entity.Account;
import com.dhensouza.ged.domain.entity.Document;
import com.dhensouza.ged.domain.entity.DocumentVersion;
import com.dhensouza.ged.domain.exception.EntityNotFoundException;
import com.dhensouza.ged.domain.repository.AccountRepository;
import com.dhensouza.ged.domain.repository.DocumentRepository;
import com.dhensouza.ged.domain.repository.DocumentVersionRepository;

public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentVersionRepository versionRepository;
    private final AccountRepository accountRepository;

    public DocumentService(
            DocumentRepository documentRepository,
            DocumentVersionRepository versionRepository,
            AccountRepository accountRepository) {
        this.documentRepository = documentRepository;
        this.versionRepository = versionRepository;
        this.accountRepository = accountRepository;
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
}
