package com.dhensouza.ged.application.audit.service;

import com.dhensouza.ged.api.controller.audit.dto.response.AuditLogResponse;
import com.dhensouza.ged.domain.entity.AuditLog;
import com.dhensouza.ged.domain.repository.AuditLogRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional()
    public Page<AuditLogResponse> findAll(Pageable pageable) {
        return auditLogRepository.findAllWithAccount(pageable)
                .map(this::toResponse);
    }

    private AuditLogResponse toResponse(AuditLog log) {
        return new AuditLogResponse(
                log.getId(),
                log.getTimestamp(),
                log.getAccount() != null ? log.getAccount().getUsername() : "SYSTEM/DELETED_USER",
                log.getAction().name(),
                log.getDocumentId(),
                log.getMetadata()
        );
    }

    @Transactional()
    public List<AuditLogResponse> findByDocument(UUID documentId) {
        return auditLogRepository.findByDocumentIdOrderByTimestampDesc(documentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }
}
