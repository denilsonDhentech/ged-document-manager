package com.dhensouza.ged.infrastructure.configuration;

import com.dhensouza.ged.application.account.service.AccountService;
import com.dhensouza.ged.domain.repository.AccountRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public AccountService accountService(AccountRepository accountRepository) {
        return new AccountService(accountRepository);
    }
}
