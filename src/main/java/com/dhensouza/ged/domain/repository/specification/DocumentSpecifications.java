package com.dhensouza.ged.domain.repository.specification;

import com.dhensouza.ged.domain.entity.Document;
import com.dhensouza.ged.domain.enums.DocumentStatus;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate; // IMPORT CORRETO AQUI
import java.util.ArrayList;
import java.util.List;

public class DocumentSpecifications {

    public static Specification<Document> hasTenant(String tenantId) {
        return (root, query, cb) -> cb.equal(root.get("tenantId"), tenantId);
    }

    public static Specification<Document> titleLike(String title) {
        return (root, query, cb) -> (title == null || title.isBlank()) ? null :
                cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Document> hasStatus(DocumentStatus status) {
        return (root, query, cb) -> (status == null) ? null :
                cb.equal(root.get("status"), status);
    }

    public static Specification<Document> hasTag(String tag) {
        return (root, query, cb) -> {
            if (tag == null || tag.isBlank()) return null;
            query.distinct(true);
            return cb.equal(root.join("tags"), tag);
        };
    }
}