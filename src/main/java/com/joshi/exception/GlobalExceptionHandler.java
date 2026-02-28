package com.joshi.exception;

import jakarta.persistence.Entity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<Map<String,Object>> handleFileUploadException(FileUploadException ex){
        return new ResponseEntity<>(buildResponse("UPLOAD_ERROR",ex.getMessage()),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String,Object>> handleMaxSizeException(MaxUploadSizeExceededException ex){
        return new ResponseEntity<>(buildResponse("FILE_TOO_LARGE","Uploaded file is too large."),HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> handleGenericException(Exception ex){
        return new ResponseEntity<>(buildResponse("INTERNAL_ERROR","Something went wrong"),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Map<String,Object> buildResponse(String errorCode,String message){
        Map<String,Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("message",errorCode);
        return error;
    }
}
