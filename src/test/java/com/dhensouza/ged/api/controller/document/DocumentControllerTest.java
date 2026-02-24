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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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
        Account account = accountRepository.save(Account.create("john_doe", "hashed_pass", "USER", "tenant-123"));
        String token = tokenService.generateToken(account);

        Map<String, Object> metadata = Map.of(
                "title", "Monthly Report",
                "description", "Billing PDF",
                "tags", List.of("Finance")
        );

        MockMultipartFile metadataPart = new MockMultipartFile("data", "",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(metadata));

        MockMultipartFile filePart = new MockMultipartFile("file", "test.pdf",
                MediaType.APPLICATION_PDF_VALUE, "PDF Content".getBytes());

        mockMvc.perform(multipart("/api/documents")
                        .file(metadataPart)
                        .file(filePart)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Monthly Report"));
    }
}
