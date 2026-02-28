package com.joshi.controller;

import com.joshi.dto.DocumentUploadedEvent;
import com.joshi.event.DocumentEventPublisher;
import com.joshi.model.DocumentMetadata;
import com.joshi.repository.DocumentMetadataRepository;
import com.joshi.service.S3Service;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class FileUploadControllerTest {

    @Mock
    private S3Service s3Service;

    @Mock
    private DocumentMetadataRepository repository;

    @Mock
    private DocumentEventPublisher eventPublisher;

    @InjectMocks
    private FileUploadController fileUploadController;

    public FileUploadControllerTest(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUploadFile_Success()throws IOException{
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "This is test content".getBytes()
        );
       when(s3Service.uploadFile(file)).thenReturn("s3://bucket/raw/test.txt");

        ResponseEntity<String> response = fileUploadController.uploadFile(file);

        assertEquals(200,response.getStatusCode().value());
        assertEquals("File uploaded Succesfully",response.getBody());

        verify(s3Service,times(1)).uploadFile(file);
        verify(repository,times(1)).save(any(DocumentMetadata.class));
        verify(eventPublisher,times(1)).publish(any(DocumentUploadedEvent.class));
    }

    @Test
    void testUploadFile_Failure()throws Exception{

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "content".getBytes()
        );

        when(s3Service.uploadFile(file)).thenThrow(new IOException("S3 Error"));

        ResponseEntity<String> response = fileUploadController.uploadFile(file);

        assertEquals(500,response.getStatusCode().value());
        assertEquals("Upload failed : S3 Error", response.getBody());

        verify(s3Service,times(1)).uploadFile(file);
        verifyNoInteractions(repository);
        verifyNoInteractions(eventPublisher);
    }

}
