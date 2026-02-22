package com.dhensouza.ged.domain.repository;

import com.dhensouza.ged.domain.entity.Account;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {
    Account save(Account account);
    Optional<Account> findById(UUID id);
    Optional<Account> findByUsername(String username);
}
