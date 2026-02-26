package com.dhensouza.ged.domain.repository;

import com.dhensouza.ged.domain.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    @Query(value = "SELECT l FROM AuditLog l LEFT JOIN FETCH l.account",
            countQuery = "SELECT count(l) FROM AuditLog l")
    Page<AuditLog> findAllWithAccount(Pageable pageable);

    List<AuditLog> findByDocumentIdOrderByTimestampDesc(UUID documentId);
}
