package com.dhensouza.ged.domain.document.entity;

import com.dhensouza.ged.domain.entity.Account;
import com.dhensouza.ged.domain.entity.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DocumentTest {

    @Test
    @DisplayName("You must create a document with valid tags and ignore null/empty ones.")
    void shouldCreateDocumentWithValidTags() {
        Account owner = new Account("owner", "pass", "ADMIN", "tenant-1");
        List<String> tags = List.of("PDF", "Invoice", "", "  ");

        Document doc = new Document("Title", "Desc", owner, "tenant-1", tags);

        assertTrue(doc.getTags().contains("PDF"));
        assertTrue(doc.getTags().contains("Invoice"));
        assertEquals(2, doc.getTags().size());
    }

    @Test
    @DisplayName("Should initialize an empty list of tags when null is passed to the constructor")
    void shouldInitializeEmptyTagsWhenNullPassed() {
        Account owner = new Account("owner", "pass", "ADMIN", "tenant-1");

        Document doc = new Document("Title", "Desc", owner, "tenant-1", null);

        assertNotNull(doc.getTags());
        assertTrue(doc.getTags().isEmpty());
    }
}
