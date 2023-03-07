package org.example;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.example.model.AuditEvent;
import org.example.model.AvroAuditEventSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.kafka.clients.admin.AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG;

//@Testcontainers
//@SpringBootTest
public class KafkaProducerSendAuditEventToTopicTest {
//    KafkaTemplate<String, AuditEvent> testAuditEventKafkaTemplate;
//
//    @Container
//    public static KafkaContainer kafkaContainer =
//            new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.3.0"));
//
//    static {
//        kafkaContainer.start();
//        createTopics("avro-topic");
//    }
//
////    @AfterAll
////    static void tearDown() {
////        kafkaContainer.stop();
////    }
//
//    @Test
//    void testSendMessageToKafkaTopic() throws Exception {
//        AuditEvent auditEvent = new AuditEvent();
//        auditEvent.setResourceType("type");
//        auditEvent.setId("id");
//        auditEvent.setEvent("message");
//        auditEvent.setParticipant(List.of("value1", "value2"));
//        auditEvent.setSource("source");
//        auditEvent.setObject(List.of("value3", "value4"));
//
//        testAuditEventKafkaTemplate = testAuditEventKafkaTemplate(kafkaContainer.getBootstrapServers());
//
////        testAuditEventKafkaTemplate.send("first-topic", "test");
//    }
//
//    @KafkaListener(topics = "first-topic", groupId = "first-group-id")
//    private void getMessage(String message) {
//        System.out.println(message);
//    }
//
//    private static void createTopics(String... topics) {
//        var newTopics =
//                Arrays.stream(topics)
//                        .map(topic -> new NewTopic(topic, 1, (short) 1))
//                        .collect(Collectors.toList());
//        try (var admin = AdminClient.create(Map.of(BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers()))) {
//            admin.createTopics(newTopics);
//        }
//    }
//    private static String getKafkaBrokers() {
//        Integer mappedPort = kafkaContainer.getFirstMappedPort();
//        return String.format("%s:%d", "localhost", 9093);
//    }
//
//    public ProducerFactory<String, AuditEvent> testAuditEventProducerFactory(String bootstrapServer) {
//        Map<String, Object> configProps = new HashMap<>();
//        configProps.put(
//                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
//                bootstrapServer
//        );
//        configProps.put(
//                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
//                StringSerializer.class
//        );
//        configProps.put(
//                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
//                AvroAuditEventSerializer.class
//        );
//        return new DefaultKafkaProducerFactory<>(configProps);
//    }
//
//    public KafkaTemplate<String, AuditEvent> testAuditEventKafkaTemplate(String bootstrapServer) {
//        return new KafkaTemplate<>(testAuditEventProducerFactory(bootstrapServer));
//    }
}
