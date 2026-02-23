package com.dhensouza.ged.domain.repository;

import com.dhensouza.ged.domain.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    List<AuditLog> findAllByDocumentId(UUID documentId);
    List<AuditLog> findAllByAccountId(UUID accountId);
}
