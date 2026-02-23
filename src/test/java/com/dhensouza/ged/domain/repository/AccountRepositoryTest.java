package com.dhensouza.ged.domain.repository;

import com.dhensouza.ged.domain.entity.Account;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Should persist and find account by username")
    void shouldPersistAndFindAccountByUsername() {
        Account account = new Account("dhensouza", "secure_hash", "ADMIN", "tenant-1");
        entityManager.persist(account);
        entityManager.flush();

        Optional<Account> found = accountRepository.findByUsername("dhensouza");

        assertTrue(found.isPresent());
        assertEquals("dhensouza", found.get().getUsername());
    }

    @Test
    @DisplayName("Should throw exception when username is already taken")
    void shouldThrowExceptionOnDuplicateUsername() {
        Account first = new Account("user1", "pass", "USER", "t-1");
        entityManager.persist(first);
        entityManager.flush();

        Account second = new Account("user1", "pass2", "USER", "t-2");

        assertThrows(PersistenceException.class, () -> {
            entityManager.persist(second);
            entityManager.flush();
        });
    }
}
