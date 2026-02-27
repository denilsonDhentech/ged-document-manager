package com.dhensouza.ged.application.document.service;

import com.dhensouza.ged.application.document.dto.request.CreateDocumentRequest;
import com.dhensouza.ged.application.document.dto.request.UpdateDocumentMetadataRequest;
import com.dhensouza.ged.application.document.dto.response.DocumentResponse;
import com.dhensouza.ged.application.document.dto.response.DocumentVersionResponse;
import com.dhensouza.ged.domain.entity.*;
import com.dhensouza.ged.domain.enums.DocumentStatus;
import com.dhensouza.ged.domain.exception.BusinessRuleException;
import com.dhensouza.ged.domain.exception.EntityNotFoundException;
import com.dhensouza.ged.domain.repository.*;
import com.dhensouza.ged.infrastructure.storage.S3StorageService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

public class DocumentService {

    private static final Logger log = LoggerFactory.getLogger(DocumentService.class);

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
        log.info("Iniciando criação de documento: '{}' para uploader: {}", request.title(), request.uploaderId());

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

        log.info("Documento criado com sucesso. ID: {}", savedDocument.getId());
        return DocumentResponse.fromEntity(savedDocument, 1);
    }

    @Transactional
    public void updateMetadata(UUID docId, UpdateDocumentMetadataRequest request) {
        log.info("Iniciando atualização de metadados para o doc: {}", docId);

        Document document = documentRepository.findById(docId)
                .orElseThrow(() -> new EntityNotFoundException("Document not found"));

        document.updateMetadata(request.title(), request.description(), request.tags());
        documentRepository.save(document);

        AuditLog auditLog = AuditLog.logMetadataUpdate(document.getOwner(), document.getId());
        auditLogRepository.save(auditLog);

        log.debug("Metadados do documento {} atualizados com sucesso", docId);
    }

    @Transactional
    public void changeStatus(UUID docId, DocumentStatus newStatus) {
        log.info("Alterando status do documento {} para {}", docId, newStatus);
        Document document = documentRepository.findById(docId)
                .orElseThrow(() -> new EntityNotFoundException("Document not found"));

        DocumentStatus oldStatus = document.getStatus();
        document.changeStatus(newStatus);
        documentRepository.save(document);

        auditLogRepository.save(AuditLog.logStatusChange(document.getOwner(), document.getId(), oldStatus, newStatus));
    }

    @Transactional
    public void uploadNewVersion(UUID documentId, UUID uploaderId, MultipartFile file) throws Exception {
        log.info("Iniciando upload de nova versão para o documento: {}", documentId);
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Document not found"));

        Account uploader = accountRepository.findById(uploaderId)
                .orElseThrow(() -> new EntityNotFoundException("Uploader not found"));

        if (document.getStatus() == DocumentStatus.ARCHIVED) {
            log.warn("Falha ao atualizar documento {}: status arquivado", documentId);
            throw new BusinessRuleException("Cannot upload new version to an archived document");
        }

        String checksum = calculateChecksum(file.getBytes());
        String fileKey = UUID.randomUUID() + "-" + file.getOriginalFilename();
        int nextVersion = versionRepository.findMaxVersionByDocumentId(document.getId()).map(v -> v + 1).orElse(1);

        DocumentVersion newVersion = document.createNewVersion(nextVersion, fileKey, checksum, file.getSize(), file.getContentType(), uploader);
        versionRepository.save(newVersion);

        storageService.upload(fileKey, file.getBytes(), file.getContentType());

        auditLogRepository.save(AuditLog.logFileUpload(uploader, document.getId(), nextVersion));
        log.info("Nova versão {} criada para o documento {}", nextVersion, documentId);
    }

    @Transactional
    public String generateDownloadUrl(UUID docId, int versionNumber, UUID userId) {
        log.info("Gerando URL de download para doc: {}, v: {}", docId, versionNumber);
        Account currentUser = accountRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        DocumentVersion version = versionRepository.findByDocumentIdAndVersionNumber(docId, versionNumber)
                .orElseThrow(() -> new EntityNotFoundException("Version not found"));

        if (!version.getDocument().getTenantId().equals(currentUser.getTenantId())) {
            log.error("Tentativa de download ilegal. User: {}, Doc: {}", userId, docId);
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
        log.debug("Listando histórico de versões para o documento: {}", documentId);
        if (!documentRepository.existsById(documentId)) {
            throw new EntityNotFoundException("Document not found");
        }
        return versionRepository.findByDocumentIdWithUploader(documentId).stream().map(v ->
                new DocumentVersionResponse(v.getVersionNumber(), v.getFileKey(), v.getFileSize(),
                        v.getFileType(), v.getUploader().getUsername(), v.getUploadedAt(), v.getChecksum())).toList();
    }
}
