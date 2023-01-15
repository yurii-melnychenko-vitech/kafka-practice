package org.example;

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
public class KafkaTopicConfig {
    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

//    @Bean
//    public NewTopic topic1() {
//        return TopicBuilder.name("first-topic")
//                .partitions(1)
//                .replicas(1)
//                .build();
////        return new NewTopic("first_topic", 1, (short) 1);
//    }
//
//    @Bean
//    public NewTopic topic2() {
//        return TopicBuilder.name("second-topic")
//                .partitions(1)
//                .replicas(1)
//                .build();
////        return new NewTopic("second_topic", 1, (short) 1);
//    }

//    @Bean
//    public NewTopic deadLetterTopic(AppKafkaProperties properties) {
//        // https://docs.spring.io/spring-kafka/docs/2.8.2/reference/html/#configuring-topics
//        return TopicBuilder.name(ORDERS + properties.deadletter().suffix())
//                // Use only one partition for infrequently used Dead Letter Topic
//                .partitions(1)
//                // Use longer retention for Dead Letter Topic, allowing for more time to troubleshoot
//                .config(TopicConfig.RETENTION_MS_CONFIG, "" + properties.deadletter().retention().toMillis())
//                .build();
//    }
}
