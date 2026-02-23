package com.dhensouza.ged.domain.entity;

import com.dhensouza.ged.domain.enums.DocumentStatus;
import com.dhensouza.ged.domain.exception.BusinessRuleException;
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

    public Document(String title, String description, Account owner, String tenantId, List<String> tags) {
        this.id = UUID.randomUUID();
        this.setTitle(title);
        this.description = description;
        this.owner = owner;
        this.tenantId = tenantId;
        this.status = DocumentStatus.DRAFT;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        this.tags = new ArrayList<>();

        if (tags != null) {
            this.addTags(tags);
        }
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

    public void addTags(List<String> tags) {
        if (tags == null) return;

        tags.stream()
                .filter(tag -> tag != null && !tag.isBlank())
                .forEach(tag -> {
                    if (!this.tags.contains(tag)) {
                        this.tags.add(tag.trim());
                    }
                });
    }

    public void updateMetadata(String title, String description, List<String> tags) {
        this.setTitle(title);
        this.description = description;

        if (tags != null) {
            this.tags.clear();
            this.addTags(tags);
        }

        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void changeStatus(DocumentStatus newStatus) {
        if (this.status == newStatus) return;

        if (!this.status.canTransitionTo(newStatus)) {
            throw new BusinessRuleException(
                    String.format("Invalid transition from %s to %s", this.status, newStatus)
            );
        }

        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();

    }
}
