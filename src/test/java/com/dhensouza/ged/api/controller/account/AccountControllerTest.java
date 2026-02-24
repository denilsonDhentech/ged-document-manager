package com.dhensouza.ged.api.controller.account;

import com.dhensouza.ged.BaseIntegrationTest;
import com.dhensouza.ged.application.account.dto.request.CreateAccountRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AccountControllerTest extends BaseIntegrationTest {

    @Test
    @DisplayName("Should register a USER account and return 201 Created")
    void shouldRegisterUserAccount() throws Exception {
        CreateAccountRequest request = new CreateAccountRequest("operator1", "pass123", "USER", "tenant-1");

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("operator1"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @DisplayName("Should register a VIEWER account and return 201 Created")
    void shouldRegisterViewerAccount() throws Exception {
        CreateAccountRequest request = new CreateAccountRequest("viewer1", "pass123", "VIEWER", "tenant-1");

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("VIEWER"));
    }
}
