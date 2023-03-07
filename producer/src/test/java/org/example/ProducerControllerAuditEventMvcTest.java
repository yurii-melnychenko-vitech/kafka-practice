package org.example;

import org.example.model.AuditEvent;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest
public class ProducerControllerAuditEventMvcTest {
    @Container
    public static KafkaContainer kafkaContainer =
            new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.3.0"));
    @Autowired
    private MockMvc mvc;
    @MockBean
    private KafkaProducerService kafkaProducerService;

    static {
        kafkaContainer.start();
    }

//    @Container
//    private static KafkaContainer kafkaContainer = new KafkaContainer(
//            DockerImageName.parse("confluentinc/cp-kafka:5.3.0")
//    );

//    @Autowired
//    private KafkaProducerController kafkaProducerController;
//
//    @MockBean
//    private KafkaProducerService kafkaProducerService;
    @Test
    void testCallSendAuditEventMessageMethod() throws Exception {
        kafkaContainer.getBootstrapServers();

        AuditEvent auditEvent = new AuditEvent();
        auditEvent.setResourceType("type");
        auditEvent.setId("id");
        auditEvent.setEvent("message");
        auditEvent.setParticipant(List.of("value1", "value2"));
        auditEvent.setSource("source");
        auditEvent.setObject(List.of("value3", "value4"));

        String auditEventJson = "{\n" +
                "\t\"resourceType\":\"type\",\n" +
                "\t\"id\":\"id\",\n" +
                "\t\"event\":\"message\",\n" +
                "\t\"participant\":[\"value1\", \"value2\"],\n" +
                "\t\"source\":\"source\",\n" +
                "    \"object\":[\"value3\", \"value4\"]\n" +
                "}";

        mvc.perform(
                        post("http://localhost:8080/kafka/auditEventPublish?topicName=avro-topic")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(auditEventJson)
                )
                .andExpect(status().isOk());

        Mockito.verify(kafkaProducerService, times(1))
                .sendAuditEventMessage("avro-topic", auditEvent);
    }
}
