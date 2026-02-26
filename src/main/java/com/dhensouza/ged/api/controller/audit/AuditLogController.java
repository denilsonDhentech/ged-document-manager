package com.dhensouza.ged.api.controller.audit;

import com.dhensouza.ged.api.controller.audit.dto.response.AuditLogResponse;
import com.dhensouza.ged.application.audit.service.AuditLogService;
import com.dhensouza.ged.domain.repository.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/audit")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<AuditLogResponse>> getLogs(
            @PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(auditLogService.findAll(pageable));
    }

    @GetMapping("/document/{documentId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<List<AuditLogResponse>> getLogsByDocument(@PathVariable UUID documentId) {
        return ResponseEntity.ok(auditLogService.findByDocument(documentId));
    }
}
