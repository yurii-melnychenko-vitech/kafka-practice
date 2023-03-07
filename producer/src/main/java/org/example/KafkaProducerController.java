package org.example;

import org.example.model.AuditEvent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kafka")
public class KafkaProducerController {
    private final KafkaProducerService service;

    public KafkaProducerController(KafkaProducerService service) {
        this.service = service;
    }

    @PostMapping("/publish")
    public ResponseEntity<String> publish(
            @RequestParam("topicName") String topicName,
            @RequestParam("message") String message
    ) {
        service.sendStringMessage(topicName, message);
        return ResponseEntity.ok("Message sent to kafka topic");
    }

    @PostMapping("/auditEventPublish")
    public ResponseEntity<String> auditEventPublish(
            @RequestParam("topicName") String topicName,
            @RequestBody AuditEvent auditEvent
    ) {
        service.sendAuditEventMessage(topicName, auditEvent);
        return ResponseEntity.ok("Message sent to kafka topic");
    }
}
