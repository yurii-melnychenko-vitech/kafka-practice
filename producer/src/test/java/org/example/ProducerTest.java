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
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

@AutoConfigureMockMvc
@SpringBootTest
public class ProducerTest {
    private static final String AUDIT_EVENT_JSON = "{\n" +
            "\t\"resourceType\":\"type\",\n" +
            "\t\"event\":\"message\",\n" +
            "\t\"participant\":[\"value1\", \"value2\"],\n" +
            "\t\"source\":\"source\",\n" +
            "    \"object\":[\"value3\", \"value4\"]\n" +
            "}";
    private static final AuditEvent AUDIT_EVENT_OBJECT = AuditEvent.newBuilder()
            .setResourceType("type")
            .setEvent("message")
            .setParticipant(List.of("value1", "value2"))
            .setSource("source")
            .setObject(List.of("value3", "value4")).build();
    @Autowired
    private MockMvc mvc;
    @MockBean
    private KafkaProducerService kafkaProducerService;

    @Test
    void sendAuditEventControllerTest() throws Exception {
        mvc.perform(
                    post("http://localhost:8080/kafka/auditEventPublish?topicName=avro-topic")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(AUDIT_EVENT_JSON)
                ).andExpect(status().isOk());

        Mockito.verify(kafkaProducerService, times(1))
                .sendAuditEventMessage("avro-topic", AUDIT_EVENT_OBJECT);
    }
}
