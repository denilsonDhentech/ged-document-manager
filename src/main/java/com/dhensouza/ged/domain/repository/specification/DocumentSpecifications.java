package com.dhensouza.ged.domain.repository.specification;

import com.dhensouza.ged.domain.entity.Document;
import com.dhensouza.ged.domain.enums.DocumentStatus;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate; // IMPORT CORRETO AQUI
import java.util.ArrayList;
import java.util.List;

public class DocumentSpecifications {

    public static Specification<Document> withFilters(String title, DocumentStatus status, String tag, String tenantId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("tenantId"), tenantId));

            if (title != null && !title.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (tag != null && !tag.isBlank()) {
                predicates.add(cb.equal(root.join("tags"), tag));
            }

            query.distinct(true);

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}