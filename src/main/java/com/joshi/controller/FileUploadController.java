package com.joshi.controller;

import com.joshi.dto.DocumentUploadedEvent;
import com.joshi.event.DocumentEventPublisher;
import com.joshi.exception.FileUploadException;
import com.joshi.model.DocumentMetadata;
import com.joshi.repository.DocumentMetadataRepository;
import com.joshi.service.S3Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    @Autowired
    private S3Service s3Service;

    @Autowired
    private DocumentMetadataRepository repository;

    @Autowired
    private DocumentEventPublisher eventPublisher;

    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            log.warn("Rejected file :File is empty");
            throw new FileUploadException("Upload File is empty");
        }


        long maxSizeInBytes = 5 * 1024 * 1024;
        if (file.getSize() > maxSizeInBytes) {
            log.warn("Rejected file: Exceeds size limit. Filename={}, size={} bytes",file.getOriginalFilename(),file.getSize());
            return ResponseEntity.badRequest().body("File Exceeds 2MB limit");
        }
        String contentType = file.getContentType();
        if (contentType == null || !isSupportedType(contentType)) {
            log.warn("Rejected file: Unsupported content type. Filename={}, size={} bytes",file.getOriginalFilename(),file.getSize());
            return ResponseEntity.badRequest().body("Unsupported file Type.");
        }

        try {
            String s3Path = s3Service.uploadFile(file);
            log.info("Uploaded file to S3. Path={}",s3Path);

            //Save metadata in DB
            DocumentMetadata metadata = new DocumentMetadata();
            metadata.setFileName(file.getOriginalFilename());
            metadata.setFileType(file.getContentType());
            metadata.setSize(file.getSize());
            metadata.setS3Path(s3Path);
            metadata.setUploadedAt(LocalDateTime.now());
            repository.save(metadata);
            log.info("Saved file metadata to DB. Filename ={}",file.getOriginalFilename());

            //Publish Kafka Event
            DocumentUploadedEvent event = new DocumentUploadedEvent(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    LocalDateTime.now().toString(),
                    s3Path
            );
            eventPublisher.publish(event);
            log.info("Published document.uploaded event for file={}",file.getOriginalFilename());

            return ResponseEntity.ok("File uploaded Succesfully");
        } catch (IOException e) {
            log.error("Upload failed for file={} due to {}",file.getOriginalFilename(),e.getMessage(),e);
            return ResponseEntity.status(500).body("Upload failed : " + e.getMessage());
        }
    }

    private boolean isSupportedType(String contentType) {
        return contentType.equals("application/pdf") ||
                contentType.equals("application/msword") ||
                contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
                contentType.equals("text/plain") ||
                contentType.equals("text/csv") ||
                contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
                contentType.equals("image/png") ||
                contentType.equals("image/jpeg");
    }


}
