package com.dhensouza.ged.domain.repository;

import com.dhensouza.ged.domain.entity.AuditLog;
import java.util.List;
import java.util.UUID;

public interface AuditLogRepository {
    void save(AuditLog log);
    List<AuditLog> findAllByDocumentId(UUID documentId);
    List<AuditLog> findAllByAccountId(UUID accountId);
}
