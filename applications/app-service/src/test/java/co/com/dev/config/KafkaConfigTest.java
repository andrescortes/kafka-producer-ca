package co.com.dev.config;

import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class KafkaConfigTest {
    private static final String TOPIC = "library-events";
    @InjectMocks
    private KafkaConfig kafkaConfig;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(kafkaConfig, "topic", "library-events");
    }

    @Test
    void producerFactoryNotBeNull() {
        assertNotNull(kafkaConfig.producerFactory());
        assertEquals(IntegerSerializer.class, kafkaConfig.producerFactory().getConfigurationProperties().get("key.serializer"));
        assertEquals(StringSerializer.class, kafkaConfig.producerFactory().getConfigurationProperties().get("value.serializer"));
    }

    @Test
    void kafkaTemplateShouldNotBeNull() {
        assertNotNull(kafkaConfig.kafkaTemplate());
        assertEquals(TOPIC, kafkaConfig.kafkaTemplate().getDefaultTopic());
    }

    @Test
    void topicShouldBeSetFromProperties() {
        ReflectionTestUtils.setField(kafkaConfig, "topic", "");
        assertEquals("", kafkaConfig.kafkaTemplate().getDefaultTopic());
        ReflectionTestUtils.setField(kafkaConfig, "topic", " ");
        assertEquals(" ", kafkaConfig.kafkaTemplate().getDefaultTopic());
    }

    @Test
    void producerFactoryShouldContainBootstrapServersConfig() {
        assertNotNull(kafkaConfig.producerFactory().getConfigurationProperties().get("bootstrap.servers"));
    }

    @Test
    void producerFactoryShouldContainConfiguredBootstrapServers() {
        String expectedBootstrapServers = "localhost:8097, localhost:8098, localhost:8099";
        String actualBootstrapServers = (String) kafkaConfig.producerFactory().getConfigurationProperties().get("bootstrap.servers");
        assertEquals(expectedBootstrapServers, actualBootstrapServers);
    }

    @Test
    void producerFactoryShouldContainKeyAndValueSerializerClasses() {
        assertEquals(IntegerSerializer.class, kafkaConfig.producerFactory().getConfigurationProperties().get("key.serializer"));
        assertEquals(StringSerializer.class, kafkaConfig.producerFactory().getConfigurationProperties().get("value.serializer"));
    }
}
