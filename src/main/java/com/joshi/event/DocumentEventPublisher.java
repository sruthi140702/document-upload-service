package com.joshi.event;

import com.joshi.dto.DocumentUploadedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DocumentEventPublisher {

    @Autowired
    private KafkaTemplate<String, DocumentUploadedEvent> kafkaTemplate;

    public void publish(DocumentUploadedEvent event) {
        log.info("Publishing kafka event ={}",event);
        kafkaTemplate.send("document.uploaded",event.getFileName(), event);
    }
}
