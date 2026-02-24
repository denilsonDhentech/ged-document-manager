package com.dhensouza.ged.api.controller.document;

import com.dhensouza.ged.api.controller.document.dto.DocumentCreateWebDTO;
import com.dhensouza.ged.application.document.dto.DocumentVersionWebDTO;
import com.dhensouza.ged.application.document.dto.request.CreateDocumentRequest;
import com.dhensouza.ged.application.document.dto.request.DocumentFilter;
import com.dhensouza.ged.application.document.dto.request.FileUploadRequest;
import com.dhensouza.ged.application.document.dto.response.DocumentResponse;
import com.dhensouza.ged.application.document.service.DocumentSearchService;
import com.dhensouza.ged.application.document.service.DocumentService;
import com.dhensouza.ged.domain.entity.Document;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;
    private final DocumentSearchService searchService;

    public DocumentController(DocumentService documentService, DocumentSearchService searchService) {
        this.documentService = documentService;
        this.searchService = searchService;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<DocumentResponse> create(
            @RequestBody @Valid DocumentCreateWebDTO webDto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        String tenantId = jwt.getClaim("tenantId");

        CreateDocumentRequest serviceRequest = webDto.toServiceRequest(userId, tenantId);
        DocumentResponse response = documentService.createDocument(serviceRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER', 'VIEWER')")
    public ResponseEntity<Page<DocumentResponse>> search(
            DocumentFilter filter,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String tenantId = jwt.getClaim("tenantId");

        Page<Document> result = searchService.search(filter, tenantId, pageable);

        Page<DocumentResponse> response = result.map(DocumentResponse::fromEntity);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/versions")
    public ResponseEntity<Void> uploadVersion(
            @PathVariable UUID id,
            @RequestBody @Valid DocumentVersionWebDTO webDto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID uploaderId = UUID.fromString(jwt.getSubject());

        FileUploadRequest serviceRequest = new FileUploadRequest(
                id,
                uploaderId,
                webDto.fileKey(),
                webDto.checksum(),
                webDto.fileSize(),
                webDto.fileType()
        );

        documentService.uploadNewVersion(serviceRequest);

        return ResponseEntity.noContent().build();
    }
}
