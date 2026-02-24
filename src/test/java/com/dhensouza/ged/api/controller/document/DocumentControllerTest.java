package com.dhensouza.ged.api.controller.document;

import com.dhensouza.ged.BaseIntegrationTest;
import com.dhensouza.ged.application.auth.service.TokenService;
import com.dhensouza.ged.domain.entity.Account;
import com.dhensouza.ged.domain.repository.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DocumentControllerTest extends BaseIntegrationTest {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    @DisplayName("Should create document and associate with the authenticated user's tenant")
    void shouldCreateDocumentSuccessfully() throws Exception {
        Account account = Account.create("john_doe", "hashed_pass", "USER", "tenant-123");
        accountRepository.save(account);

        String token = tokenService.generateToken(account);

        Map<String, Object> request = Map.of(
                "title", "Monthly Report",
                "description", "Billing PDF",
                "fileKey", "s3://bucket/file.pdf",
                "checksum", "a1b2c3d4",
                "fileSize", 1024L,
                "fileType", "application/pdf"
        );

        mockMvc.perform(post("/api/documents")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Monthly Report"));
    }
}
