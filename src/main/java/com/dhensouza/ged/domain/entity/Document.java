package com.dhensouza.ged.domain.entity;

import com.dhensouza.ged.domain.enums.DocumentStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "documents")
public class Document {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(name = "tags")
    private List<String> tags;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Account owner;

    @Column(name = "tenant_id", nullable = false, length = 50)
    private String tenantId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Document() {
    }

    public Document(String title, String description, Account owner, String tenantId) {
        this.id = UUID.randomUUID();
        this.setTitle(title);
        this.description = description;
        this.owner = owner;
        this.tenantId = tenantId;
        this.status = DocumentStatus.DRAFT;
        this.tags = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void publish() {
        if (this.status == DocumentStatus.PUBLISHED) {
            throw new IllegalStateException("Document is already published.");
        }
        this.status = DocumentStatus.PUBLISHED;
        this.updatedAt = LocalDateTime.now();
    }

    public void archive() {
        this.status = DocumentStatus.ARCHIVED;
        this.updatedAt = LocalDateTime.now();
    }

    public void addTag(String tag) {
        if (tag == null || tag.trim().isEmpty()) {
            throw new IllegalArgumentException("Tag cannot be empty.");
        }
        if (this.tags == null) {
            this.tags = new ArrayList<>();
        }
        this.tags.add(tag.trim().toLowerCase());
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required and cannot be empty.");
        }
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getTags() {
        return tags != null ? Collections.unmodifiableList(tags) : Collections.emptyList();
    }

    public DocumentStatus getStatus() {
        return status;
    }

    public Account getOwner() {
        return owner;
    }

    public String getTenantId() {
        return tenantId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
