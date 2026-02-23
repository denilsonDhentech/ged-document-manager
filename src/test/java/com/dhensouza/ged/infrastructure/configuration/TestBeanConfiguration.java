package com.dhensouza.ged.infrastructure.configuration;

import com.dhensouza.ged.domain.repository.AccountRepository;
import com.dhensouza.ged.domain.repository.AuditLogRepository;
import com.dhensouza.ged.domain.repository.DocumentRepository;
import com.dhensouza.ged.domain.repository.DocumentVersionRepository;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestBeanConfiguration {

    @Bean
    @Primary
    public AccountRepository accountRepository() {
        return Mockito.mock(AccountRepository.class);
    }

    @Bean
    @Primary
    public DocumentRepository documentRepository() {
        return Mockito.mock(DocumentRepository.class);
    }

    @Bean
    @Primary
    public DocumentVersionRepository documentVersionRepository() {
        return Mockito.mock(DocumentVersionRepository.class);
    }

    @Bean
    @Primary
    public AuditLogRepository auditLogRepository() {
        return Mockito.mock(AuditLogRepository.class);
    }
}
