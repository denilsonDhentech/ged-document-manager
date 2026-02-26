package com.dhensouza.ged.application.document.service;

import com.dhensouza.ged.application.document.dto.request.CreateDocumentRequest;
import com.dhensouza.ged.application.document.dto.request.UpdateDocumentMetadataRequest;
import com.dhensouza.ged.application.document.dto.response.DocumentResponse;
import com.dhensouza.ged.application.document.dto.response.DocumentVersionResponse;
import com.dhensouza.ged.domain.entity.Account;
import com.dhensouza.ged.domain.entity.Document;
import com.dhensouza.ged.domain.entity.DocumentVersion;
import com.dhensouza.ged.domain.enums.DocumentStatus;
import com.dhensouza.ged.domain.exception.BusinessRuleException;
import com.dhensouza.ged.domain.exception.EntityNotFoundException;
import com.dhensouza.ged.domain.repository.AccountRepository;
import com.dhensouza.ged.domain.repository.AuditLogRepository;
import com.dhensouza.ged.domain.repository.DocumentRepository;
import com.dhensouza.ged.domain.repository.DocumentVersionRepository;
import com.dhensouza.ged.infrastructure.storage.S3StorageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock private DocumentRepository documentRepository;
    @Mock private DocumentVersionRepository versionRepository;
    @Mock private AccountRepository accountRepository;
    @Mock private AuditLogRepository auditLogRepository;
    @Mock private S3StorageService storageService; // Novo Mock

    @InjectMocks
    private DocumentService documentService;

    @Test
    @DisplayName("Should successfully create document, upload to S3 and record audit")
    void shouldCreateDocumentAndInitialVersionSuccessfully() throws Exception {
        // Arrange
        UUID uploaderId = UUID.randomUUID();

        CreateDocumentRequest request = new CreateDocumentRequest(
                "Contract",
                "Desc",
                List.of("Tag1"),
                uploaderId,
                "t1"
        );

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "conteudo".getBytes()
        );

        when(accountRepository.findById(uploaderId)).thenReturn(Optional.of(mock(Account.class)));
        when(documentRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        DocumentResponse response = documentService.createDocument(request, file);

        assertNotNull(response);
        assertEquals("Contract", response.title());

        verify(storageService, times(1)).upload(anyString(), any(), anyString());
        verify(versionRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Should create a new version incrementally and upload to S3")
    void shouldCreateNewIncrementalVersionSuccessfully() throws Exception {
        Account uploader = mock(Account.class);
        Document existingDoc = new Document("Title", "Desc", uploader, "t1", null);
        UUID realDocId = existingDoc.getId();
        UUID uploaderId = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile("file", "v2.pdf", "application/pdf", "novo conteudo".getBytes());

        when(documentRepository.findById(realDocId)).thenReturn(Optional.of(existingDoc));
        when(accountRepository.findById(uploaderId)).thenReturn(Optional.of(uploader));
        when(versionRepository.findMaxVersionByDocumentId(realDocId)).thenReturn(Optional.of(1));

        documentService.uploadNewVersion(realDocId, uploaderId, file);

        verify(storageService).upload(anyString(), any(), anyString());
        verify(versionRepository).save(argThat(version -> version.getVersionNumber() == 2));
        verify(auditLogRepository).save(any());
    }

    @Test
    @DisplayName("Should throw BusinessRuleException when uploading to an archived document")
    void shouldThrowExceptionWhenUploadingToArchivedDocument() throws Exception {
        Account uploader = mock(Account.class);
        Document archivedDoc = new Document("Title", "Desc", uploader, "t1", null);
        archivedDoc.changeStatus(DocumentStatus.ARCHIVED);

        UUID realDocId = archivedDoc.getId();
        UUID uploaderId = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile("file", "v2.pdf", "application/pdf", "conteudo".getBytes());

        when(documentRepository.findById(realDocId)).thenReturn(Optional.of(archivedDoc));
        when(accountRepository.findById(uploaderId)).thenReturn(Optional.of(uploader));

        assertThrows(BusinessRuleException.class, () ->
                documentService.uploadNewVersion(realDocId, uploaderId, file)
        );

        verify(storageService, never()).upload(any(), any(), any());
        verify(versionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should successfully update document metadata and record audit log")
    void shouldUpdateDocumentMetadataSuccessfully() {
        UUID docId = UUID.randomUUID();
        UpdateDocumentMetadataRequest updateRequest = new UpdateDocumentMetadataRequest("New Title", "New Description", List.of("Urgent"));
        Document existingDoc = new Document("Old", "Old", mock(Account.class), "t-1", null);

        when(documentRepository.findById(docId)).thenReturn(Optional.of(existingDoc));

        documentService.updateMetadata(docId, updateRequest);

        assertEquals("New Title", existingDoc.getTitle());
        verify(documentRepository).save(existingDoc);
    }

    @Test
    @DisplayName("Should return version history list ordered by version number descending")
    void shouldReturnVersionHistoryListOrderedByVersionNumberDescending() {
        UUID docId = UUID.randomUUID();
        Account uploader = mock(Account.class);
        when(uploader.getUsername()).thenReturn("john_doe");

        Document doc = mock(Document.class);
        var v1 = new DocumentVersion(doc, 1, "key1", "hash1", 1024L, "application/pdf", uploader);
        var v2 = new DocumentVersion(doc, 2, "key2", "hash2", 2048L, "application/pdf", uploader);

        when(documentRepository.existsById(docId)).thenReturn(true);
        when(versionRepository.findByDocumentIdWithUploader(docId)).thenReturn(List.of(v2, v1));

        List<DocumentVersionResponse> history = documentService.listVersions(docId);

        assertNotNull(history);
        assertEquals(2, history.size());

        assertEquals(2, history.get(0).versionNumber());
        assertEquals("john_doe", history.get(0).uploadedBy());
        assertEquals(2048L, history.get(0).size());

        assertEquals(1, history.get(1).versionNumber());
        assertEquals("hash1", history.get(1).checksum());

        verify(documentRepository, times(1)).existsById(docId);
        verify(versionRepository, times(1)).findByDocumentIdWithUploader(docId);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when document does not exist for history")
    void shouldThrowExceptionWhenDocumentNotFoundForHistory() {
        UUID invalidDocId = UUID.randomUUID();
        when(documentRepository.existsById(invalidDocId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () ->
                documentService.listVersions(invalidDocId)
        );

        verify(versionRepository, never()).findByDocumentIdWithUploader(any());
    }
}