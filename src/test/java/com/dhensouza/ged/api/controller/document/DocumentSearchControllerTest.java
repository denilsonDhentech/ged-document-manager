package com.dhensouza.ged.api.controller.document;

import com.dhensouza.ged.application.auth.service.TokenService;
import com.dhensouza.ged.domain.entity.Account;
import com.dhensouza.ged.domain.entity.Document;
import com.dhensouza.ged.domain.repository.AccountRepository;
import com.dhensouza.ged.domain.repository.DocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
@ActiveProfiles("test")
class DocumentSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @BeforeEach
    void setUp() {
        documentRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("Should list only documents belonging to the authenticated user's tenant")
    void shouldListOnlyTenantDocuments() throws Exception {
        Account userA = Account.create("user_a", "hash", "USER", "TENANT_A");
        Account userB = Account.create("user_b", "hash", "USER", "TENANT_B");
        accountRepository.saveAll(List.of(userA, userB));

        // Documentos para cada tenant
        Document docA = new Document("Financial Report", "Content A", userA, "TENANT_A", List.of("finance"));
        Document docB = new Document("HR Policies", "Content B", userB, "TENANT_B", List.of("hr"));
        documentRepository.saveAll(List.of(docA, docB));

        String tokenA = tokenService.generateToken(userA);

        mockMvc.perform(get("/api/documents")
                        .header("Authorization", "Bearer " + tokenA)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Financial Report"))
                .andExpect(jsonPath("$.content[0].tenantId").doesNotExist());
    }

    @Test
    @DisplayName("Should filter documents by tag and respect tenant isolation")
    void shouldFilterByTagAndTenant() throws Exception {
        Account userA = accountRepository.save(Account.create("user_a", "hash", "USER", "TENANT_A"));
        Account userB = accountRepository.save(Account.create("user_b", "hash", "USER", "TENANT_B"));

        Document doc1 = new Document("Invoice 01", "D", userA, "TENANT_A", List.of("finance", "urgent"));
        Document doc2 = new Document("Report 01", "D", userA, "TENANT_A", List.of("hr"));
        Document doc3 = new Document("Other Tenant Finance", "D", userB, "TENANT_B", List.of("finance"));

        documentRepository.saveAll(List.of(doc1, doc2, doc3));

        String tokenA = tokenService.generateToken(userA);

        mockMvc.perform(get("/api/documents")
                        .header("Authorization", "Bearer " + tokenA)
                        .param("tag", "finance")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Invoice 01"));
    }
}