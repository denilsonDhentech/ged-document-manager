package com.dhensouza.ged.application.document.service;

import com.dhensouza.ged.application.document.dto.request.CreateDocumentRequest;
import com.dhensouza.ged.application.document.dto.request.UpdateDocumentMetadataRequest;
import com.dhensouza.ged.application.document.dto.response.DocumentResponse;
import com.dhensouza.ged.application.document.dto.response.DocumentVersionResponse;
import com.dhensouza.ged.domain.entity.Account;
import com.dhensouza.ged.domain.entity.AuditLog;
import com.dhensouza.ged.domain.entity.Document;
import com.dhensouza.ged.domain.entity.DocumentVersion;
import com.dhensouza.ged.domain.enums.DocumentStatus;
import com.dhensouza.ged.domain.exception.BusinessRuleException;
import com.dhensouza.ged.domain.exception.EntityNotFoundException;
import com.dhensouza.ged.domain.repository.AccountRepository;
import com.dhensouza.ged.domain.repository.AuditLogRepository;
import com.dhensouza.ged.domain.repository.DocumentRepository;
import com.dhensouza.ged.domain.repository.DocumentVersionRepository;
import com.dhensouza.ged.infrastructure.storage.S3StorageService;
import jakarta.transaction.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentVersionRepository versionRepository;
    private final AccountRepository accountRepository;
    private final AuditLogRepository auditLogRepository;
    private final S3StorageService storageService;


    public DocumentService(
            DocumentRepository documentRepository,
            DocumentVersionRepository versionRepository,
            AccountRepository accountRepository,
            AuditLogRepository auditLogRepository,
            S3StorageService storageService) {
        this.documentRepository = documentRepository;
        this.versionRepository = versionRepository;
        this.accountRepository = accountRepository;
        this.auditLogRepository = auditLogRepository;
        this.storageService = storageService;
    }

    @Transactional
    public DocumentResponse createDocument(CreateDocumentRequest request, MultipartFile file) throws Exception {
        Account uploader = accountRepository.findById(request.uploaderId())
                .orElseThrow(() -> new EntityNotFoundException("Uploader account not found"));

        String checksum = calculateChecksum(file.getBytes());
        String fileKey = UUID.randomUUID() + "-" + file.getOriginalFilename();

        Document document = new Document(request.title(), request.description(), uploader, request.tenantId(), request.tags());
        Document savedDocument = documentRepository.save(document);

        DocumentVersion initialVersion = new DocumentVersion(
                savedDocument, 1, fileKey, checksum, file.getSize(), file.getContentType(), uploader
        );
        versionRepository.save(initialVersion);

        storageService.upload(fileKey, file.getBytes(), file.getContentType());

        auditLogRepository.save(AuditLog.logDocumentCreation(uploader, savedDocument.getId()));

        return DocumentResponse.fromEntity(savedDocument, 1);
    }

    @Transactional
    public void updateMetadata(UUID docId, UpdateDocumentMetadataRequest request) {
        Document document = documentRepository.findById(docId)
                .orElseThrow(() -> new EntityNotFoundException("Document not found"));

        document.updateMetadata(request.title(), request.description(), request.tags());

        documentRepository.save(document);

        AuditLog log = AuditLog.logMetadataUpdate(document.getOwner(), document.getId());
        auditLogRepository.save(log);
    }
    @Transactional
    public void changeStatus(UUID docId, DocumentStatus newStatus) {
        Document document = documentRepository.findById(docId)
                .orElseThrow(() -> new EntityNotFoundException("Document not found"));

        DocumentStatus oldStatus = document.getStatus();
        document.changeStatus(newStatus);
        documentRepository.save(document);

        AuditLog log = AuditLog.logStatusChange(document.getOwner(), document.getId(), oldStatus, newStatus);

        auditLogRepository.save(log);
    }
    @Transactional
    public void uploadNewVersion(UUID documentId, UUID uploaderId, MultipartFile file) throws Exception {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Document not found"));

        Account uploader = accountRepository.findById(uploaderId)
                .orElseThrow(() -> new EntityNotFoundException("Uploader not found"));

        if (document.getStatus() == DocumentStatus.ARCHIVED) {
            throw new BusinessRuleException("Cannot upload new version to an archived document");
        }

        String checksum = calculateChecksum(file.getBytes());
        String fileKey = UUID.randomUUID() + "-" + file.getOriginalFilename();

        int nextVersion = versionRepository.findMaxVersionByDocumentId(document.getId())
                .map(v -> v + 1).orElse(1);

        DocumentVersion newVersion = document.createNewVersion(
                nextVersion, fileKey, checksum, file.getSize(), file.getContentType(), uploader
        );

        versionRepository.save(newVersion);

        storageService.upload(fileKey, file.getBytes(), file.getContentType());

        auditLogRepository.save(AuditLog.logFileUpload(uploader, document.getId(), nextVersion));
    }

    @Transactional
    public String generateDownloadUrl(UUID docId, int versionNumber, UUID userId) {
        Account currentUser = accountRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        DocumentVersion version = versionRepository.findByDocumentIdAndVersionNumber(docId, versionNumber)
                .orElseThrow(() -> new EntityNotFoundException("Version not found"));

        if (!version.getDocument().getTenantId().equals(currentUser.getTenantId())) {
            throw new BusinessRuleException("Access denied: Document belongs to another tenant");
        }

        String url = storageService.generatePresignedUrl(version.getFileKey());

        auditLogRepository.save(AuditLog.logFileDownload(currentUser, docId, versionNumber));

        return url;
    }

    private String calculateChecksum(byte[] bytes) throws Exception {
        byte[] hash = MessageDigest.getInstance("SHA-256").digest(bytes);
        return HexFormat.of().formatHex(hash);
    }

    public List<DocumentVersionResponse> listVersions(UUID documentId) {
        if (!documentRepository.existsById(documentId)) {
            throw new EntityNotFoundException("Document not found");
        }

        return versionRepository.findByDocumentIdWithUploader(documentId)
                .stream()
                .map(v -> new DocumentVersionResponse(
                        v.getVersionNumber(),
                        v.getFileKey(),
                        v.getFileSize(),
                        v.getFileType(),
                        v.getUploader().getUsername(),
                        v.getUploadedAt(),
                        v.getChecksum()
                )).toList();
    }
}
