package org.example;

import org.example.model.AuditEvent;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kafka")
public class KafkaProducerController {
    private final KafkaTemplate<String, String> kafkaTemplate;

    private final KafkaTemplate<String, AuditEvent> auditEventKafkaTemplate;

    public KafkaProducerController(
            KafkaTemplate<String, String> kafkaTemplate,
            KafkaTemplate<String, AuditEvent> auditEventKafkaTemplate
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.auditEventKafkaTemplate = auditEventKafkaTemplate;
    }

    @PostMapping("/publish")
    public ResponseEntity<String> publish(
            @RequestParam("topicName") String topicName,
            @RequestParam("message") String message
    ) {
        kafkaTemplate.send(topicName, message);
        return ResponseEntity.ok("Message sent to kafka topic");
    }

    @PostMapping("/auditEventPublish")
    public ResponseEntity<String> auditEventPublish(
            @RequestParam("topicName") String topicName,
            @RequestBody AuditEvent auditEvent
    ) {
        auditEventKafkaTemplate.send(topicName, auditEvent);
        return ResponseEntity.ok("Message sent to kafka topic");
    }
}
