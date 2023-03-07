package org.example;

import org.example.model.AuditEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaTemplate<String, AuditEvent> auditEventKafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate, KafkaTemplate<String, AuditEvent> auditEventKafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.auditEventKafkaTemplate = auditEventKafkaTemplate;
    }

    public void sendStringMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }

    public void sendAuditEventMessage(String topic, AuditEvent message) {
        auditEventKafkaTemplate.send(topic, message);
    }
}
