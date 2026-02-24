package com.dhensouza.ged.api.controller.document;

import com.dhensouza.ged.BaseIntegrationTest;
import com.dhensouza.ged.application.auth.service.TokenService;
import com.dhensouza.ged.domain.entity.Account;
import com.dhensouza.ged.domain.entity.Document;
import com.dhensouza.ged.domain.enums.DocumentStatus;
import com.dhensouza.ged.domain.repository.AccountRepository;
import com.dhensouza.ged.domain.repository.AuditLogRepository;
import com.dhensouza.ged.domain.repository.DocumentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DocumentStatusControllerTest extends BaseIntegrationTest {

    @Autowired private DocumentRepository documentRepository;
    @Autowired private TokenService tokenService;
    @Autowired private AccountRepository accountRepository;
    @Autowired private AuditLogRepository auditLogRepository;

    @Test
    @DisplayName("Should block new version upload if document is ARCHIVED")
    void shouldBlockVersionUploadOnArchivedDocument() throws Exception {
        Account user = accountRepository.save(Account.create("admin", "hash", "ADMIN", "T1"));
        Document doc = new Document("Old Doc", "D", user, "T1", null);
        doc.changeStatus(DocumentStatus.PUBLISHED);
        doc.changeStatus(DocumentStatus.ARCHIVED);
        documentRepository.save(doc);

        String token = tokenService.generateToken(user);
        MockMultipartFile filePart = new MockMultipartFile("file", "test.pdf", "application/pdf", "bytes".getBytes());

        mockMvc.perform(multipart("/api/documents/{id}/versions", doc.getId())
                        .file(filePart)
                        .queryParam("uploaderId", user.getId().toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("Should create an audit log when document status is updated")
    void shouldCreateAuditLogOnStatusChange() throws Exception {
        Account admin = accountRepository.save(Account.create("admin_audit", "hash", "ADMIN", "T1"));
        Document doc = documentRepository.save(new Document("Audit Test", "D", admin, "T1", null));
        String token = tokenService.generateToken(admin);

        mockMvc.perform(patch("/api/documents/{id}/status", doc.getId())
                        .header("Authorization", "Bearer " + token)
                        .param("newStatus", "PUBLISHED"))
                .andExpect(status().isNoContent());

        var logs = auditLogRepository.findAll();

        boolean hasAudit = logs.stream().anyMatch(log ->
                log.getDocumentId().equals(doc.getId()) &&
                        log.getAction().name().contains("PUBLISHED")
        );

        assertTrue(hasAudit, "Audit log for PUBLISHED status should have been created");
    }
}
