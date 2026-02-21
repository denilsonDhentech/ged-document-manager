package com.dhensouza.ged.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "document_versions")
public class DocumentVersion {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @Column(name = "file_key", nullable = false)
    private String fileKey;

    @Column(nullable = false, length = 64)
    private String checksum;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "file_type", nullable = false, length = 50)
    private String fileType;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private Account uploader;

    protected DocumentVersion() {
    }

    public DocumentVersion(Document document, Integer versionNumber, String fileKey,
                           String checksum, Long fileSize, String fileType, Account uploader) {
        this.id = UUID.randomUUID();
        this.document = document;
        this.versionNumber = versionNumber;
        this.fileKey = fileKey;
        this.checksum = checksum;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.uploader = uploader;
        this.uploadedAt = LocalDateTime.now();
    }

    // --- Getters ---

    public UUID getId() {
        return id;
    }

    public Document getDocument() {
        return document;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public String getFileKey() {
        return fileKey;
    }

    public String getChecksum() {
        return checksum;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public Account getUploader() {
        return uploader;
    }
}
