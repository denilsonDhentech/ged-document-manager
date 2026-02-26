package com.dhensouza.ged.application.account.service;

import com.dhensouza.ged.api.controller.account.dto.request.CreateAccountRequest;
import com.dhensouza.ged.application.account.dto.response.AccountResponse;
import com.dhensouza.ged.domain.entity.Account;
import com.dhensouza.ged.domain.exception.BusinessRuleException;
import com.dhensouza.ged.domain.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    private AccountRepository repository;
    private AccountService service;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        repository = mock(AccountRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        service = new AccountService(repository, passwordEncoder);
    }

    @Test
    @DisplayName("Should create account successfully when username is unique")
    void shouldCreateAccountSuccessfully() {
        CreateAccountRequest request = new CreateAccountRequest("dhensouza", "pass123", "ADMIN", "tenant-alpha");

        when(passwordEncoder.encode("pass123")).thenReturn("encoded_pass");
        when(repository.findByUsername("dhensouza")).thenReturn(Optional.empty());
        when(repository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AccountResponse response = service.create(request);

        assertNotNull(response);
        assertEquals("dhensouza", response.username());
        verify(passwordEncoder, times(1)).encode("pass123");
        verify(repository, times(1)).save(any(Account.class));
    }

    @Test
    @DisplayName("Should throw BusinessRuleException when attempting to register a username that already exists")
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        CreateAccountRequest request = new CreateAccountRequest("dhen", "123", "USER", "t1");

        Account existingAccount = Account.create("dhen", "pwd", "USER", "t1");
        when(repository.findByUsername("dhen")).thenReturn(Optional.of(existingAccount));

        assertThrows(BusinessRuleException.class, () -> service.create(request));

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should return a list of AccountResponse when multiple accounts exist")
    void shouldReturnListOfAccounts() {
        // GIVEN
        Account account1 = Account.create("user.one", "pwd1", "ADMIN", "T1");
        Account account2 = Account.create("user.two", "pwd2", "USER", "T1");

        when(repository.findAll()).thenReturn(java.util.List.of(account1, account2));

        java.util.List<AccountResponse> result = service.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("user.one", result.get(0).username());
        assertEquals("user.two", result.get(1).username());

        verify(repository, times(1)).findAll();
        verifyNoMoreInteractions(passwordEncoder);
    }
}
