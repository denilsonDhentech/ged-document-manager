package com.dhensouza.ged.application.document.service;

import com.dhensouza.ged.application.document.dto.request.CreateDocumentRequest;
import com.dhensouza.ged.domain.entity.Account;
import com.dhensouza.ged.domain.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock private DocumentRepository documentRepository;
    @Mock private DocumentVersionRepository versionRepository;
    @Mock private AccountRepository accountRepository;

    @InjectMocks
    private DocumentService documentService;

    @Test
    @DisplayName("Should successfully orchestrate the creation of a document and its initial version")
    void shouldCreateDocumentAndInitialVersionSuccessfully() {
        UUID uploaderId = UUID.randomUUID();
        CreateDocumentRequest request = new CreateDocumentRequest(
                "Contract", "Desc", null, uploaderId, "t1", "key", "hash", 100L, "pdf"
        );

        when(accountRepository.findById(uploaderId)).thenReturn(Optional.of(mock(Account.class)));
        when(documentRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        documentService.createDocument(request);

        verify(documentRepository, times(1)).save(any());
        verify(versionRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when the document uploader does not exist")
    void shouldThrowExceptionWhenUploaderNotFound() {
        UUID uploaderId = UUID.randomUUID();
        CreateDocumentRequest request = new CreateDocumentRequest(
                "Contract", "Desc", null, uploaderId, "t1", "key", "hash", 100L, "pdf"
        );


        when(accountRepository.findById(uploaderId)).thenReturn(Optional.empty());

        assertThrows(com.dhensouza.ged.domain.exception.EntityNotFoundException.class,
                () -> documentService.createDocument(request));

        verify(documentRepository, never()).save(any());
        verify(versionRepository, never()).save(any());
    }
}
