package com.dhensouza.ged.application.account.service;

import com.dhensouza.ged.application.account.dto.request.CreateAccountRequest;
import com.dhensouza.ged.domain.entity.Account;
import com.dhensouza.ged.domain.exception.BusinessRuleException;
import com.dhensouza.ged.domain.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    private AccountRepository repository;
    private AccountService service;

    @BeforeEach
    void setUp() {
        repository = mock(AccountRepository.class);
        service = new AccountService(repository);
    }

    @Test
    @DisplayName("Should throw BusinessRuleException when attempting to register a username that already exists")
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        CreateAccountRequest request = new CreateAccountRequest("dhen", "123", "USER", "t1");

        when(repository.findByUsername("dhen")).thenReturn(Optional.of(mock(Account.class)));

        assertThrows(BusinessRuleException.class, () -> service.create(request));
        verify(repository, never()).save(any());
    }
}
