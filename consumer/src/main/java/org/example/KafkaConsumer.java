package org.example;

import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {
    @KafkaListener(topics = {"${first-topic.name.consumer}", "${second-topic.name.consumer}"}, groupId = "${spring.kafka.consumer.group-id}")
    public void listenGroupFoo(String message) {
        System.out.println("Received Message in group foo: " + message);
    }

    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            autoCreateTopics = "false",
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE)
    @KafkaListener(topics = "${audit-event-topic.name.consumer}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenAuditTopic(String auditEvent) {
        if (auditEvent.contains("error")) {
            throw new RuntimeException("test kafka exception");
        }
        System.out.println("Received Message in group foo: " + auditEvent);
    }

    @DltHandler
    public void dlt(String auditEvent) {
        System.out.println("dlt: " + auditEvent);
    }
}
