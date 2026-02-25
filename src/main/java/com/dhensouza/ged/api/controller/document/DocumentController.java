package com.dhensouza.ged.api.controller.document;

import com.dhensouza.ged.api.controller.document.dto.DocumentCreateWebDTO;
import com.dhensouza.ged.application.document.dto.request.CreateDocumentRequest;
import com.dhensouza.ged.application.document.dto.request.DocumentFilter;
import com.dhensouza.ged.application.document.dto.response.DocumentResponse;
import com.dhensouza.ged.application.document.service.DocumentSearchService;
import com.dhensouza.ged.application.document.service.DocumentService;
import com.dhensouza.ged.domain.entity.Document;
import com.dhensouza.ged.domain.enums.DocumentStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponse> create(
            @RequestPart("data") String webDtoRaw,
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal Jwt jwt) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        DocumentCreateWebDTO webDto = objectMapper.readValue(webDtoRaw, DocumentCreateWebDTO.class);

        UUID userId = UUID.fromString(jwt.getSubject());
        String tenantId = jwt.getClaimAsString("tenantId");

        CreateDocumentRequest serviceRequest = webDto.toServiceRequest(userId, tenantId);

        DocumentResponse response = documentService.createDocument(serviceRequest, file);
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
            @RequestParam UUID uploaderId,
            @RequestPart("file") MultipartFile file) throws Exception {

        documentService.uploadNewVersion(id, uploaderId, file);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<Void> changeStatus(
            @PathVariable UUID id,
            @RequestParam DocumentStatus newStatus,
            @AuthenticationPrincipal Jwt jwt
    ) {
        documentService.changeStatus(id, newStatus);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/versions/{versionNumber}/download")
    public ResponseEntity<Map<String, String>> getDownloadUrl(
            @PathVariable UUID id,
            @PathVariable int versionNumber,
            @AuthenticationPrincipal Jwt jwt) {

        UUID userId = UUID.fromString(jwt.getSubject());

        String downloadUrl = documentService.generateDownloadUrl(id, versionNumber, userId);

        return ResponseEntity.ok(Map.of("url", downloadUrl));
    }
}
