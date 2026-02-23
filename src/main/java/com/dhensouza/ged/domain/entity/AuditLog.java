package com.dhensouza.ged.domain.entity;

import com.dhensouza.ged.domain.enums.AuditAction;
import jakarta.persistence.*;
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

    @Column(columnDefinition = "jsonb")
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
}
