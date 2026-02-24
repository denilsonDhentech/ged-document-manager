package com.dhensouza.ged.api.controller.account;

import com.dhensouza.ged.application.auth.service.TokenService;
import com.dhensouza.ged.domain.entity.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AccountAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenService tokenService;

    @Test
    @DisplayName("Should allow ADMIN to access the list accounts endpoint")
    void shouldAllowAdminAccess() throws Exception {
        Account admin = Account.create("admin", "123", "ADMIN", "t1");
        String token = tokenService.generateToken(admin);

        mockMvc.perform(get("/api/accounts")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should forbid USER from accessing the list accounts endpoint")
    void shouldForbidUserAccess() throws Exception {
        Account user = Account.create("user", "123", "USER", "t1");
        String token = tokenService.generateToken(user);

        mockMvc.perform(get("/api/accounts")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}