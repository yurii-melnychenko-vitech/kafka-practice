package org.example;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.google.gson.Gson;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.example.model.AuditEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.stream.StreamSupport;

import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@Testcontainers
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ConsumerTest {
    private static final String FIRST_GROUP_ID = "first-group-id";
    private static final String AVRO_TOPIC = "avro-topic";
    private static final int CONTAINER_PORT = 5432;
    private static final int LOCAL_PORT = 54389;
    private static final KafkaContainer kafkaContainer;
    private static final PostgreSQLContainer postgresqlContainer;
    private static AdminClient admin;

    static {
        kafkaContainer
                = new org.testcontainers.containers.KafkaContainer(DockerImageName
                .parse("confluentinc/cp-kafka:6.2.1"));
        kafkaContainer.start();

        DockerImageName postgres = DockerImageName.parse("postgres:13.1");
        postgresqlContainer = new PostgreSQLContainer<>(postgres)
                .withDatabaseName("kafka_practice")
                .withUsername("postgres")
                .withPassword("postgres")
                .withReuse(true)
                .withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
                        new HostConfig().withPortBindings(
                                new PortBinding(
                                        Ports.Binding.bindPort(LOCAL_PORT),
                                        new ExposedPort(CONTAINER_PORT)
                                )
                        )
                ));
        postgresqlContainer.start();
    }

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
        registry.add("kafka.bootstrap.servers.config", kafkaContainer::getBootstrapServers);
        registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
    }

    private static final AuditEvent VALID_AUDIT_EVENT = AuditEvent.newBuilder()
            .setResourceType("type")
            .setEvent("message test")
            .setParticipant(List.of("value1", "value2"))
            .setSource("source test")
            .setObject(List.of("value3", "value4")).build();

    private static final Event EVENT = Event.builder()
            .resourceType(VALID_AUDIT_EVENT.getClass().getTypeName())
            .resource(new Gson().toJson(VALID_AUDIT_EVENT))
            .build();

    private static final AuditEvent INVALID_AUDIT_EVENT = AuditEvent.newBuilder()
            .setResourceType("type")
            .setEvent("error")
            .setParticipant(List.of("value1", "value2"))
            .setSource("source")
            .setObject(List.of("value3", "value4")).build();

    @Autowired
    KafkaTemplate<String, AuditEvent> kafkaTemplate;
    @Autowired
    KafkaConsumer consumer;

    @Autowired
    private EventRepository eventRepository;

    @BeforeAll
    public static void setUp() {
        Properties config = new Properties();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        admin = AdminClient.create(config);
    }

    @Test
    public void consumerReceiveValidMessageTest() {
        await().until(() -> checkIfConsumerReady(FIRST_GROUP_ID));

        kafkaTemplate.send(AVRO_TOPIC, VALID_AUDIT_EVENT);

        await().until(() -> StreamSupport.stream(
                eventRepository.findAll().spliterator(), false)
                .anyMatch(event -> checkIfEventObjectsEquals(event, EVENT)));

        List<Event> events = new ArrayList<>();
        eventRepository.findAll().iterator().forEachRemaining(events::add);

        assertThat(events.stream().anyMatch(event -> checkIfEventObjectsEquals(event, EVENT))).isTrue();
        assertThat(KafkaConsumer.dltAuditEvents.contains(VALID_AUDIT_EVENT)).isFalse();
    }

    @Test
    public void consumerReceiveInvalidMessageTest() {
        await().until(() -> checkIfConsumerReady(FIRST_GROUP_ID));

        kafkaTemplate.send(AVRO_TOPIC, INVALID_AUDIT_EVENT);

        await().until(() -> KafkaConsumer.dltAuditEvents.contains(INVALID_AUDIT_EVENT));

        assertThat(KafkaConsumer.dltAuditEvents.contains(INVALID_AUDIT_EVENT)).isTrue();
    }

    private static boolean checkIfConsumerReady(String groupId) {
        try {
            return admin.listConsumerGroups()
                    .all()
                    .get()
                    .stream()
                    .anyMatch(cg -> cg.groupId().equals(groupId));
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean checkIfEventObjectsEquals(Event event1, Event event2) {
        return event1.getResourceType().equals(event2.getResourceType()) &&
                event1.getResource().equals(event2.getResource());
    }
}
