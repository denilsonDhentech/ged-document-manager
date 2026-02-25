package com.dhensouza.ged.application.document.service;

import com.dhensouza.ged.application.document.dto.request.DocumentFilter;
import com.dhensouza.ged.domain.entity.Document;
import com.dhensouza.ged.domain.enums.DocumentStatus;
import com.dhensouza.ged.domain.repository.DocumentRepository;
import com.dhensouza.ged.domain.repository.DocumentVersionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentSearchServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    private DocumentSearchService searchService;

    @Mock
    private DocumentVersionRepository documentVersionRepository;

    @BeforeEach
    void setUp() {
        searchService = new DocumentSearchService(documentRepository, documentVersionRepository);
    }

    @Test
    @DisplayName("Should call repository with combined specification and fetch version count")
    void shouldCallRepositoryWithFilters() {
        String tenantId = "tenant-123";
        DocumentFilter filter = new DocumentFilter("Contract", DocumentStatus.PUBLISHED, "Legal");
        Pageable pageable = PageRequest.of(0, 10);

        Document mockDocument = mock(Document.class);
        UUID docId = UUID.randomUUID();
        when(mockDocument.getId()).thenReturn(docId);
        when(mockDocument.getStatus()).thenReturn(DocumentStatus.PUBLISHED);

        Page<Document> page = new PageImpl<>(List.of(mockDocument));

        when(documentRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);

        when(documentVersionRepository.countByDocumentId(docId)).thenReturn(5L);

        var result = searchService.search(filter, tenantId, pageable);

        verify(documentRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        verify(documentVersionRepository, times(1)).countByDocumentId(docId);

        assertEquals(5, result.getContent().get(0).versionCount());
    }

    @Test
    @DisplayName("Should handle empty filter and still provide tenant specification")
    void shouldHandleEmptyFilter() {
        DocumentFilter emptyFilter = new DocumentFilter(null, null, null);
        String tenantId = "tenant-123";
        Pageable pageable = PageRequest.of(0, 10);

        when(documentRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(Page.empty());

        searchService.search(emptyFilter, tenantId, pageable);

        verify(documentRepository).findAll(any(Specification.class), eq(pageable));
    }
}
