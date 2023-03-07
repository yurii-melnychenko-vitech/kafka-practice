package org.example;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
//import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.example.AuditEventRepository;
import org.example.AuditEventRepositoryImpl;
import org.example.KafkaConsumer;
import org.example.model.AuditEvent;
import org.example.model.AvroAuditEventSerializer;
import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.apache.kafka.clients.admin.AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.then;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;
import static org.testcontainers.shaded.org.awaitility.Awaitility.waitAtMost;

@Testcontainers
@SpringBootTest
public class ConsumerReceiveMessageTest {
//    @Autowired
//    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    @MockBean
    private AuditEventRepository auditEventRepository;
    private KafkaConsumer kafkaConsumer;
    private KafkaTemplate<String, AuditEvent> testAuditEventKafkaTemplate;

    @Container
    public static KafkaContainer kafkaContainer =
            new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.3.0"));


//    @DynamicPropertySource
//    static void dataSourceProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
//        registry.add("spring.kafka.consumer.bootstrap-servers", kafkaContainer::getBootstrapServers);
////        registry.add("spring.kafka.producer.bootstrap-servers", kafkaContainer::getBootstrapServers);
////        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
//    }

    @DynamicPropertySource
    static void dataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", () -> "127.0.0.1:" + kafkaContainer.getBootstrapServers().split(":")[2]);
        registry.add("spring.kafka.consumer.bootstrap-servers", () -> "127.0.0.1:" + kafkaContainer.getBootstrapServers().split(":")[2]);
//        registry.add("spring.kafka.producer.bootstrap-servers", kafkaContainer::getBootstrapServers);
//        registry.add("spring.kafka.producer.bootstrap-servers", kafkaContainer::getBootstrapServers);
//        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    static {
        kafkaContainer.start();
        createTopics("avro-topic");
    }

//    @BeforeTestClass
//    public void beforeTest() {
//
//        kafkaListenerEndpointRegistry.getListenerContainers().forEach(
//                messageListenerContainer -> {
//                    ContainerTestUtils
//                            .waitForAssignment(messageListenerContainer, 1);
//
//                }
//        );
//    }

    @Test
    public void consumerReceiveMessageTest() {
        kafkaConsumer = new KafkaConsumer(auditEventRepository);
        AuditEvent auditEvent = new AuditEvent();
        auditEvent.setResourceType("type");
        auditEvent.setId("id");
        auditEvent.setEvent("message");
        auditEvent.setParticipant(List.of("value1", "value2"));
        auditEvent.setSource("source");
        auditEvent.setObject(List.of("value3", "value4"));
        testAuditEventKafkaTemplate = testAuditEventKafkaTemplate("127.0.0.1:" + kafkaContainer.getBootstrapServers().split(":")[2]);
        testAuditEventKafkaTemplate.send("avro-topic", auditEvent);

//        System.out.println(kafkaConsumer.getAuditEvent());

//        while (kafkaConsumer.getData() == null) {
//            System.out.println("null");
//        }

        await().until(() -> kafkaConsumer.getData() != null);

//        waitAtMost(10, TimeUnit.SECONDS)
//                .untilAsserted(() -> {
////                    var receivedMessage = kafkaConsumer.receivedMessage;
//                    then(kafkaConsumer.getData()).isEqualTo(auditEvent);
//                });
        assertThat(kafkaConsumer.getData()).isEqualTo(auditEvent);
    }

    public ProducerFactory<String, AuditEvent> testAuditEventProducerFactory(String bootstrapServer) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServer
        );
        configProps.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class
        );
        configProps.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                AvroAuditEventSerializer.class
        );
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    public KafkaTemplate<String, AuditEvent> testAuditEventKafkaTemplate(String bootstrapServer) {
        return new KafkaTemplate<>(testAuditEventProducerFactory(bootstrapServer));
    }

    private static void createTopics(String... topics) {
        var newTopics =
                Arrays.stream(topics)
                        .map(topic -> new NewTopic(topic, 1, (short) 1))
                        .collect(Collectors.toList());
        try (var admin = AdminClient.create(Map.of(BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:" + kafkaContainer.getBootstrapServers().split(":")[2]))) {
            admin.createTopics(newTopics);
        }
    }

//    @Bean
//    public Map<String, Object> consumerConfigs() {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
//        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, "first-group-id");
//        // more standard configuration
//        return props;
//    }
}
