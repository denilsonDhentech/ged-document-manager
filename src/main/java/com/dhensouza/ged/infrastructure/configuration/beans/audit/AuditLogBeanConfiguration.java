package com.dhensouza.ged.infrastructure.configuration.beans.audit;

import com.dhensouza.ged.application.audit.service.AuditLogService;
import com.dhensouza.ged.domain.repository.AuditLogRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditLogBeanConfiguration {

    @Bean
    @Transactional
    public AuditLogService auditLogService(AuditLogRepository auditLogRepository) {
        return new AuditLogService(auditLogRepository);
    }
}
