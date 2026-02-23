package com.dhensouza.ged.domain.enums;

public enum DocumentStatus {
    DRAFT(null) {
        @Override
        public boolean canTransitionTo(DocumentStatus next) {
            return next == PUBLISHED || next == ARCHIVED;
        }
    },
    PUBLISHED(AuditAction.DOCUMENT_PUBLISHED) {
        @Override
        public boolean canTransitionTo(DocumentStatus next) {
            return next == ARCHIVED;
        }
    },
    ARCHIVED(AuditAction.DOCUMENT_ARCHIVED) {
        @Override
        public boolean canTransitionTo(DocumentStatus next) {
            return false;
        }
    };

    private final AuditAction auditAction;

    DocumentStatus(AuditAction auditAction) {
        this.auditAction = auditAction;
    }

    public AuditAction getAuditAction() {
        return auditAction;
    }

    public abstract boolean canTransitionTo(DocumentStatus next);
}
