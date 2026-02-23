package com.dhensouza.ged.domain.account.entity;

import com.dhensouza.ged.domain.entity.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Test
    @DisplayName("Should create account with valid data")
    void shouldCreateAccountSuccessfully() {
        Account account = new Account("dhensouza", "password123", "ADMIN", "tenant-1");

        assertNotNull(account.getId());
        assertEquals("dhensouza", account.getUsername());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    @DisplayName("Should throw IllegalArgumentException when username is empty or blank")
    void shouldThrowExceptionWhenUsernameIsInvalid(String invalidUsername) {
        assertThrows(IllegalArgumentException.class, () ->
                new Account(invalidUsername, "password", "USER", "t1")
        );
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when username is null")
    void shouldThrowExceptionWhenUsernameIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new Account(null, "password", "USER", "t1")
        );
    }
}
