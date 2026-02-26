package com.dhensouza.ged.api.controller.audit;
import com.dhensouza.ged.application.audit.dto.response.AuditLogResponse;
import com.dhensouza.ged.application.audit.service.AuditLogService;
import com.dhensouza.ged.domain.entity.Account;
import com.dhensouza.ged.domain.entity.AuditLog;
import com.dhensouza.ged.domain.enums.AuditAction;
import com.dhensouza.ged.domain.repository.AuditLogRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditLogService auditLogService;

    @Test
    @DisplayName("Should return a paged list of audit logs with mapped usernames")
    void shouldReturnPagedAuditLogs() {
        Pageable pageable = PageRequest.of(0, 10);
        Account account = mock(Account.class);
        when(account.getUsername()).thenReturn("dhensouza");

        AuditLog log = new AuditLog(account, AuditAction.DOCUMENT_CREATED, UUID.randomUUID(), "{}");
        Page<AuditLog> logPage = new PageImpl<>(List.of(log));

        when(auditLogRepository.findAllWithAccount(pageable)).thenReturn(logPage);

        Page<AuditLogResponse> result = auditLogService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("dhensouza", result.getContent().get(0).username());
        assertEquals(AuditAction.DOCUMENT_CREATED.name(), result.getContent().get(0).action());

        verify(auditLogRepository).findAllWithAccount(pageable);
    }

    @Test
    @DisplayName("Should return SYSTEM/DELETED_USER when account is null in log")
    void shouldReturnDeletedUserLabelWhenAccountIsNull() {
        Pageable pageable = PageRequest.of(0, 10);
        AuditLog log = new AuditLog(null, AuditAction.FILE_DOWNLOADED, UUID.randomUUID(), "{}");
        Page<AuditLog> logPage = new PageImpl<>(List.of(log));

        when(auditLogRepository.findAllWithAccount(pageable)).thenReturn(logPage);

        Page<AuditLogResponse> result = auditLogService.findAll(pageable);

        assertEquals("SYSTEM/DELETED_USER", result.getContent().get(0).username());
    }

    @Test
    @DisplayName("Should return list of logs for a specific document ordered by timestamp")
    void shouldReturnLogsByDocument() {
        UUID docId = UUID.randomUUID();
        AuditLog log = new AuditLog(mock(Account.class), AuditAction.FILE_UPLOADED, docId, "{}");

        when(auditLogRepository.findByDocumentIdOrderByTimestampDesc(docId))
                .thenReturn(List.of(log));

        List<AuditLogResponse> result = auditLogService.findByDocument(docId);

        assertFalse(result.isEmpty());
        assertEquals(docId, result.get(0).documentId());
        verify(auditLogRepository).findByDocumentIdOrderByTimestampDesc(docId);
    }
}
