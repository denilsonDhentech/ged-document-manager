package com.dhensouza.ged.api.controller.document;

import com.dhensouza.ged.BaseIntegrationTest;
import com.dhensouza.ged.application.auth.service.TokenService;
import com.dhensouza.ged.domain.entity.Account;
import com.dhensouza.ged.domain.entity.Document;
import com.dhensouza.ged.domain.repository.AccountRepository;
import com.dhensouza.ged.domain.repository.AuditLogRepository;
import com.dhensouza.ged.domain.repository.DocumentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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

        MockMultipartFile filePart = new MockMultipartFile("file", "v2.pdf",
                MediaType.APPLICATION_PDF_VALUE, "New Content".getBytes());

        mockMvc.perform(multipart("/api/documents/{id}/versions", doc.getId())
                        .file(filePart)
                        .queryParam("uploaderId", user.getId().toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        var logs = auditLogRepository.findAll();
        assertTrue(logs.stream().anyMatch(log ->
                log.getAction().name().contains("FILE_UPLOADED") &&
                        log.getDocumentId().equals(doc.getId())
        ));
    }
}
