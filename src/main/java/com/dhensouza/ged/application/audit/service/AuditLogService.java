package com.dhensouza.ged.application.audit.service;

import com.dhensouza.ged.application.audit.dto.response.AuditLogResponse;
import com.dhensouza.ged.domain.entity.AuditLog;
import com.dhensouza.ged.domain.repository.AuditLogRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

public class AuditLogService {

    private static final Logger log = LoggerFactory.getLogger(AuditLogService.class);

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public Page<AuditLogResponse> findAll(Pageable pageable) {
        log.info("Consulta ao log de auditoria completo - Página: {}, Tamanho: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        return auditLogRepository.findAllWithAccount(pageable)
                .map(this::toResponse);
    }

    private AuditLogResponse toResponse(AuditLog auditLog) {
        return new AuditLogResponse(
                auditLog.getId(),
                auditLog.getTimestamp(),
                auditLog.getAccount() != null ? auditLog.getAccount().getUsername() : "SYSTEM/DELETED_USER",
                auditLog.getAction().name(),
                auditLog.getDocumentId(),
                auditLog.getMetadata()
        );
    }

    @Transactional
    public List<AuditLogResponse> findByDocument(UUID documentId) {
        log.info("Buscando histórico de auditoria para o documento ID: {}", documentId);

        List<AuditLog> auditLogs = auditLogRepository.findByDocumentIdOrderByTimestampDesc(documentId);

        log.debug("Encontrados {} registros de auditoria para o documento {}", auditLogs.size(), documentId);

        return auditLogs.stream()
                .map(this::toResponse)
                .toList();
    }
}
