package com.dhensouza.ged.infrastructure.configuration.document;

import com.dhensouza.ged.application.document.service.DocumentSearchService;
import com.dhensouza.ged.domain.repository.DocumentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DocumentBeanConfig {

    @Bean
    public DocumentSearchService documentSearchService(DocumentRepository repository) {
        return new DocumentSearchService(repository);
    }

}
