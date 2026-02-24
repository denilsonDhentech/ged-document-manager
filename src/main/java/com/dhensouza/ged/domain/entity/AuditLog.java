package com.dhensouza.ged.domain.entity;

import com.dhensouza.ged.domain.enums.AuditAction;
import com.dhensouza.ged.domain.enums.DocumentStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    private UUID id;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AuditAction action;

    @Column(name = "document_id")
    private UUID documentId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata;

    protected AuditLog() {
    }

    public AuditLog(Account account, AuditAction action, UUID documentId, String metadata) {
        this.id = UUID.randomUUID();
        this.timestamp = LocalDateTime.now();
        this.account = account;
        this.action = action;
        this.documentId = documentId;
        this.metadata = metadata;
    }

    public UUID getId() {
        return id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Account getAccount() {
        return account;
    }

    public AuditAction getAction() {
        return action;
    }

    public UUID getDocumentId() {
        return documentId;
    }

    public String getMetadata() {
        return metadata;
    }

    public static AuditLog logFileUpload(Account account, UUID documentId, int version) {
        String metadata = String.format("{\"version\": %d}", version);
        return new AuditLog(account, AuditAction.FILE_UPLOADED, documentId, metadata);
    }

    // Dentro de AuditLog.java

    public static AuditLog logStatusChange(Account account, UUID documentId, DocumentStatus oldStatus, DocumentStatus newStatus) {
        String metadata = String.format(
                "{\"previousStatus\": \"%s\", \"newStatus\": \"%s\", \"message\": \"Status changed to %s\"}",
                oldStatus, newStatus, newStatus
        );
        return new AuditLog(account, newStatus.getAuditAction(), documentId, metadata);
    }

    public static AuditLog logMetadataUpdate(Account account, UUID documentId) {
        String metadata = "{\"info\": \"Metadata updated by user\"}";
        return new AuditLog(account, AuditAction.UPDATE_DOCUMENT, documentId, metadata);
    }
}
