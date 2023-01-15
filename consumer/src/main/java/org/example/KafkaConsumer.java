package org.example;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {
    @KafkaListener(topics = {"${first-topic.name.consumer}", "${second-topic.name.consumer}"}, groupId = "${spring.kafka.consumer.group-id}")
    public void listenGroupFoo(String message) {
        System.out.println("Received Message in group foo: " + message);
    }

    @KafkaListener(topics = "${audit-event-topic.name.consumer}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenAuditTopic(String auditEvent) {
        System.out.println("Received Message in group foo: " + auditEvent);
    }
}
