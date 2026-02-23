package com.dhensouza.ged.application.document.service;

import com.dhensouza.ged.application.document.dto.request.DocumentFilter;
import com.dhensouza.ged.domain.enums.DocumentStatus;
import com.dhensouza.ged.domain.repository.DocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentSearchServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    private DocumentSearchService searchService;

    @BeforeEach
    void setUp() {
        searchService = new DocumentSearchService(documentRepository);
    }

    @Test
    @DisplayName("Should call repository with combined specification when filter is provided")
    void shouldCallRepositoryWithFilters() {
        String tenantId = "tenant-123";
        DocumentFilter filter = new DocumentFilter("Contract", DocumentStatus.PUBLISHED, "Legal");
        Pageable pageable = PageRequest.of(0, 10);

        when(documentRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(Page.empty());

        searchService.search(filter, tenantId, pageable);

        verify(documentRepository, times(1)).findAll(any(Specification.class), eq(pageable));
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
