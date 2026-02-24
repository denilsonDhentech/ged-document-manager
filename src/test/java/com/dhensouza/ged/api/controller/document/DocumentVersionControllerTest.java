package com.dhensouza.ged.api.controller.document;

import com.dhensouza.ged.BaseIntegrationTest;
import com.dhensouza.ged.application.auth.service.TokenService;
import com.dhensouza.ged.application.document.dto.DocumentVersionWebDTO;
import com.dhensouza.ged.domain.entity.Account;
import com.dhensouza.ged.domain.entity.Document;
import com.dhensouza.ged.domain.repository.AccountRepository;
import com.dhensouza.ged.domain.repository.AuditLogRepository;
import com.dhensouza.ged.domain.repository.DocumentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DocumentVersionControllerTest extends BaseIntegrationTest {

    @Autowired private DocumentRepository documentRepository;
    @Autowired private AuditLogRepository auditLogRepository;
    @Autowired private TokenService tokenService;
    @Autowired private AccountRepository accountRepository;

    @Test
    @DisplayName("Should upload new version and create audit log")
    void shouldUploadNewVersionSuccessfully() throws Exception {
        Account user = accountRepository.save(Account.create("editor", "hash", "USER", "TENANT_1"));
        Document doc = documentRepository.save(new Document("Manual.pdf", "D", user, "TENANT_1", null));
        String token = tokenService.generateToken(user);

        DocumentVersionWebDTO request = new DocumentVersionWebDTO(
                "s3://bucket/manual-v2.pdf", "new-hash-789", 2048L, "application/pdf"
        );

        mockMvc.perform(post("/api/documents/{id}/versions", doc.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        var logs = auditLogRepository.findAll();
        assertTrue(logs.stream().anyMatch(log ->
                log.getAction().name().contains("FILE_UPLOADED") &&
                        log.getDocumentId().equals(doc.getId())
        ));
    }
}
