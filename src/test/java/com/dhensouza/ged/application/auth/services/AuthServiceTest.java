package com.dhensouza.ged.application.auth.services;

import com.dhensouza.ged.application.auth.dto.request.LoginRequest;
import com.dhensouza.ged.application.auth.dto.response.LoginResponse;
import com.dhensouza.ged.application.auth.service.AuthService;
import com.dhensouza.ged.application.auth.service.TokenService;
import com.dhensouza.ged.domain.entity.Account;
import com.dhensouza.ged.domain.exception.BusinessRuleException;
import com.dhensouza.ged.domain.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private TokenService tokenService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(accountRepository, passwordEncoder, tokenService);
    }

    @Test
    @DisplayName("Should return token when credentials are valid")
    void shouldAuthenticateSuccessfully() {
        LoginRequest request = new LoginRequest("dhen", "password123");
        Account account = Account.create("dhen", "hashed_password", "USER", "t1");

        when(accountRepository.findByUsername("dhen")).thenReturn(Optional.of(account));
        when(passwordEncoder.matches("password123", "hashed_password")).thenReturn(true);
        when(tokenService.generateToken(account)).thenReturn("valid-jwt-token");

        LoginResponse response = authService.authenticate(request);

        assertEquals("valid-jwt-token", response.token());
    }

    @Test
    @DisplayName("Should throw exception when password does not match")
    void shouldThrowExceptionWhenPasswordInvalid() {
        LoginRequest request = new LoginRequest("dhen", "wrong_pass");
        Account account = Account.create("dhen", "hashed_password", "USER", "t1");

        when(accountRepository.findByUsername("dhen")).thenReturn(Optional.of(account));
        when(passwordEncoder.matches("wrong_pass", "hashed_password")).thenReturn(false);

        assertThrows(BusinessRuleException.class, () -> authService.authenticate(request));
    }
}
