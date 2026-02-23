package com.dhensouza.ged.application.document.service;

import com.dhensouza.ged.application.document.dto.request.CreateDocumentRequest;
import com.dhensouza.ged.application.document.dto.request.FileUploadRequest;
import com.dhensouza.ged.application.document.dto.request.UpdateDocumentMetadataRequest;
import com.dhensouza.ged.domain.entity.Account;
import com.dhensouza.ged.domain.entity.Document;
import com.dhensouza.ged.domain.enums.AuditAction;
import com.dhensouza.ged.domain.enums.DocumentStatus;
import com.dhensouza.ged.domain.exception.BusinessRuleException;
import com.dhensouza.ged.domain.exception.EntityNotFoundException;
import com.dhensouza.ged.domain.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock private DocumentRepository documentRepository;
    @Mock private DocumentVersionRepository versionRepository;
    @Mock private AccountRepository accountRepository;
    @Mock private AuditLogRepository auditLogRepository;

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

    @Test
    @DisplayName("Should successfully update document metadata and record audit log")
    void shouldUpdateDocumentMetadataSuccessfully() {
        UUID docId = UUID.randomUUID();
        UpdateDocumentMetadataRequest updateRequest = new UpdateDocumentMetadataRequest(
                "New Title",
                "New Description",
                List.of("Urgent", "Financial")
        );

        Document existingDoc = new Document("New Title I", "New Description I", mock(Account.class), "tenant-1", null);

        when(documentRepository.findById(docId)).thenReturn(Optional.of(existingDoc));

        documentService.updateMetadata(docId, updateRequest);

        assertEquals("New Title", existingDoc.getTitle());
        assertEquals("New Description", existingDoc.getDescription());
        assertTrue(existingDoc.getTags().contains("Urgent"));

        verify(documentRepository, times(1)).save(existingDoc);

        verify(auditLogRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when updating metadata of non-existent document")
    void shouldThrowExceptionWhenDocumentNotFoundOnUpdate() {
        UUID docId = UUID.randomUUID();
        when(documentRepository.findById(docId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                documentService.updateMetadata(docId, new UpdateDocumentMetadataRequest("T", "D", null))
        );
    }

    @Test
    @DisplayName("Should successfully publish a draft document and record audit log")
    void shouldPublishDocumentSuccessfully() {
        UUID docId = UUID.randomUUID();
        Document existingDoc = new Document("Title", "Desc", mock(Account.class), "t1", null);
        assertEquals(DocumentStatus.DRAFT, existingDoc.getStatus());

        when(documentRepository.findById(docId)).thenReturn(Optional.of(existingDoc));

        documentService.changeStatus(docId, DocumentStatus.PUBLISHED);

        assertEquals(DocumentStatus.PUBLISHED, existingDoc.getStatus());
        verify(documentRepository, times(1)).save(existingDoc);
        verify(auditLogRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Should throw BusinessRuleException when trying to publish an archived document")
    void shouldThrowExceptionWhenPublishingArchivedDocument() {
        UUID docId = UUID.randomUUID();
        Document archivedDoc = new Document("Title", "Desc", mock(Account.class), "t1", null);

        archivedDoc.changeStatus(DocumentStatus.ARCHIVED);

        when(documentRepository.findById(docId)).thenReturn(Optional.of(archivedDoc));

        assertThrows(BusinessRuleException.class, () ->
                documentService.changeStatus(docId, DocumentStatus.PUBLISHED)
        );
    }

    @Test
    @DisplayName("Should create a new version with incremental number when document already has versions")
    void shouldCreateNewIncrementalVersionSuccessfully() {
        Account uploader = mock(Account.class);
        Document existingDoc = new Document("Title", "Desc", uploader, "t1", null);

        UUID realDocId = existingDoc.getId();
        UUID uploaderId = UUID.randomUUID();

        FileUploadRequest uploadRequest = new FileUploadRequest(
                realDocId, uploaderId, "storage-key-v2", "hash-v2", 200L, "pdf"
        );

        when(documentRepository.findById(realDocId)).thenReturn(Optional.of(existingDoc));
        when(accountRepository.findById(uploaderId)).thenReturn(Optional.of(uploader));
        when(versionRepository.findMaxVersionByDocumentId(realDocId)).thenReturn(Optional.of(1));

        documentService.uploadNewVersion(uploadRequest);

        verify(versionRepository).save(argThat(version -> version.getVersionNumber() == 2));
        verify(auditLogRepository).save(argThat(log ->
                log.getAction() == AuditAction.FILE_UPLOADED &&
                        log.getDocumentId().equals(realDocId)
        ));
    }

    @Test
    @DisplayName("Should throw BusinessRuleException when uploading to an archived document")
    void shouldThrowExceptionWhenUploadingToArchivedDocument() {
        Account uploader = mock(Account.class);
        Document archivedDoc = new Document("Title", "Desc", uploader, "t1", null);
        archivedDoc.changeStatus(DocumentStatus.PUBLISHED);
        archivedDoc.changeStatus(DocumentStatus.ARCHIVED);

        UUID realDocId = archivedDoc.getId();
        FileUploadRequest request = new FileUploadRequest(realDocId, UUID.randomUUID(), "k", "h", 1L, "p");

        when(documentRepository.findById(realDocId)).thenReturn(Optional.of(archivedDoc));
        when(accountRepository.findById(any())).thenReturn(Optional.of(uploader));

        assertThrows(BusinessRuleException.class, () -> documentService.uploadNewVersion(request));

        verify(versionRepository, never()).save(any());
    }
}
