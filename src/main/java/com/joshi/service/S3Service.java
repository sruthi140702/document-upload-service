package com.joshi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    @Autowired
    private final S3Client s3Client;

    private final String bucketName ="joshi-learning-bucket-2025";

    public String uploadFile(MultipartFile file) throws IOException {
        log.debug("Starting S3 Upload for file : {}",file.getOriginalFilename());
        String key = "raw/"+file.getOriginalFilename();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
        log.info("Successfully uploaded file to S3 at path: {}",key);
        return key;

    }
}
