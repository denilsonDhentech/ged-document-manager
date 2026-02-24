package com.dhensouza.ged.infrastructure.configuration;

import com.dhensouza.ged.application.account.service.AccountService;
import com.dhensouza.ged.application.document.service.DocumentService;
import com.dhensouza.ged.domain.repository.AccountRepository;
import com.dhensouza.ged.domain.repository.AuditLogRepository;
import com.dhensouza.ged.domain.repository.DocumentRepository;
import com.dhensouza.ged.domain.repository.DocumentVersionRepository;
import com.dhensouza.ged.infrastructure.storage.S3StorageService;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class BeanConfiguration {

    @Bean
    @Transactional
    public AccountService accountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        return new AccountService(accountRepository, passwordEncoder);
    }

    @Bean
    @Transactional
    public DocumentService documentService(
            DocumentRepository docRepo,
            DocumentVersionRepository verRepo,
            AccountRepository accRepo,
            AuditLogRepository auditRepo,
            S3StorageService storageService) {
        return new DocumentService(docRepo, verRepo, accRepo, auditRepo, storageService);
    }
}
