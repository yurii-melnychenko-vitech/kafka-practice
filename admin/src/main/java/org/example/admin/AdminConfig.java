package org.example.admin;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class AdminConfig {
    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic avroTopic() {
        return TopicBuilder.name("avro-topic")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic avroTopicDlt() {
        return TopicBuilder.name("avro-topic-dlt")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic avroTopicRetry0() {
        return TopicBuilder.name("avro-topic-retry-0")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic avroTopicRetry1() {
        return TopicBuilder.name("avro-topic-retry-1")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic firstTopic() {
        return TopicBuilder.name("first-topic")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic secondTopic() {
        return TopicBuilder.name("second-topic")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
