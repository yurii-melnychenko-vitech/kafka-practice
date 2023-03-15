package org.example;

import org.example.model.AuditEvent;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class KafkaConsumer {
    private final AuditEventRepository auditEventRepository;
    public static List<AuditEvent> dltAuditEvents = new ArrayList<>();

    public KafkaConsumer(AuditEventRepository auditEventRepository) {
        this.auditEventRepository = auditEventRepository;
    }
    @KafkaListener(
            topics = {"${first-topic.name.consumer}", "${second-topic.name.consumer}"},
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaStringListenerContainerFactory"
    )
    public void listenStringTopics(String message) {
        System.out.println("Received Message: " + message);
    }

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE)
    @KafkaListener(topics = "${audit-event-topic.name.consumer}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenAuditTopic(AuditEvent auditEvent) {
        if (auditEvent.getEvent().contains("error")) {
            throw new RuntimeException("test kafka exception");
        }

        this.auditEventRepository.save(auditEvent);
        System.out.println("Received Message: " + auditEvent);
    }

    @DltHandler
    public void dlt(AuditEvent auditEvent) {
        dltAuditEvents.add(auditEvent);
        System.out.println("dlt: " + auditEvent);
    }
}
