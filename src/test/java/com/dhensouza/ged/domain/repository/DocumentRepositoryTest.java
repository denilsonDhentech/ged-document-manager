package com.dhensouza.ged.domain.repository;

import com.dhensouza.ged.domain.entity.Account;
import com.dhensouza.ged.domain.entity.Document;
import com.dhensouza.ged.domain.enums.DocumentStatus;
import com.dhensouza.ged.domain.repository.specification.DocumentSpecifications;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;


import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class DocumentRepositoryTest {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Account globalOwner;
    private String defaultTenant = "tenant-a";

    @BeforeEach
    void setUp() {
        globalOwner = new Account("dhensouza", "password", "ADMIN", defaultTenant);
        entityManager.persist(globalOwner);

        Document docBase = new Document("Document Base", "Desc", globalOwner, defaultTenant, List.of("General"));
        entityManager.persist(docBase);

        entityManager.flush();
    }

    @Test
    @DisplayName("Should filter documents by title and status using global setup")
    void shouldFilterDocumentsByTitleAndStatus() {
        Document docManual = new Document("Manual PDF", "Manual Description", globalOwner, defaultTenant, List.of("Guide"));
        docManual.changeStatus(DocumentStatus.PUBLISHED);
        entityManager.persist(docManual);
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);

        Specification<Document> spec = Specification
                .where(DocumentSpecifications.hasTenant(defaultTenant))
                .and(DocumentSpecifications.titleLike("Manual"))
                .and(DocumentSpecifications.hasStatus(DocumentStatus.PUBLISHED));

        Page<Document> result = documentRepository.findAll(spec, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Manual PDF", result.getContent().get(0).getTitle());
    }

    @Test
    @DisplayName("Should filter documents by specific tag")
    void shouldFilterDocumentsByTag() {
        Document docJava = new Document("Spring Guide", "Desc", globalOwner, defaultTenant, List.of("Java", "Backend"));
        Document docCsharp = new Document("DotNet Guide", "Desc", globalOwner, defaultTenant, List.of("C#", "Backend"));

        entityManager.persist(docJava);
        entityManager.persist(docCsharp);
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);

        Specification<Document> spec = Specification
                .where(DocumentSpecifications.hasTenant(defaultTenant))
                .and(DocumentSpecifications.hasTag("Java"));

        Page<Document> result = documentRepository.findAll(spec, pageable);

        assertEquals(1, result.getTotalElements());
        assertTrue(result.getContent().get(0).getTags().contains("Java"));
        assertEquals("Spring Guide", result.getContent().get(0).getTitle());
    }
}
