package co.com.dev.api;

import co.com.dev.api.library.UpdateLibraryEventService;
import co.com.dev.api.library.dto.BookDTO;
import co.com.dev.api.library.dto.LibraryEventDTO;
import co.com.dev.api.library.dto.LibraryEventTypeDTO;
import co.com.dev.api.library.transformers.LibraryEventTransformer;
import co.com.dev.model.libraryevent.Book;
import co.com.dev.model.libraryevent.LibraryEvent;
import co.com.dev.model.libraryevent.LibraryEventType;
import co.com.dev.usecase.libraryevent.UpdateLibraryEventUseCase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@EmbeddedKafka(topics = {"library-events"}, partitions = 3)
@TestPropertySource(properties = {
        "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap-servers=${spring.embedded.kafka.brokers}",

})
@ContextConfiguration(classes = {TestApp.class})
class ApiRestEmbeddedKafkaReadValueTest {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @MockBean
    private UpdateLibraryEventService updateLibraryEventService;

    @MockBean
    private LibraryEventTransformer libraryEventTransformer;

    private LibraryEvent libraryEvent;
    private LibraryEventDTO libraryEventDTO;

    private BookDTO bookDTO;
    private Consumer<Integer, String> consumer;

    @BeforeEach
    void setUp() {
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("group1", "true", embeddedKafkaBroker));
        consumer = new DefaultKafkaConsumerFactory<>(configs, new IntegerDeserializer(), new StringDeserializer()).createConsumer();
        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);

        Book book = new Book(1, "Wolf rain", "Jeff Besos");
        bookDTO = new BookDTO(1, "Wolf rain", "Jeff Besos");
        libraryEvent = LibraryEvent.builder()
                .libraryEventType(LibraryEventType.NEW)
                .libraryEventId(1)
                .book(book)
                .build();
        libraryEventDTO = LibraryEventDTO.builder()
                .libraryEventType(LibraryEventTypeDTO.NEW)
                .libraryEventId(1)
                .book(bookDTO)
                .build();
    }

    @AfterEach
    void tearDown() {
        consumer.close();
    }

    @Test
    @DisplayName("should create a LibraryEventDTO")
    @Timeout(1)
    void postLibraryEvent() {
        // given
        String expectedValue = "{\"libraryEventId\":1,\"libraryEventType\":\"NEW\",\"book\":{\"bookId\":1,\"bookName\":\"Wolf rain\",\"bookAuthor\":\"Jeff Besos\"}}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LibraryEventDTO> requestEntity = new HttpEntity<>(libraryEventDTO, headers);


        // when
        doReturn(libraryEvent).when(libraryEventTransformer).toEntity(any());
        doReturn(libraryEventDTO).when(libraryEventTransformer).toDTO(any());
        ResponseEntity<LibraryEventDTO> responseEntity = testRestTemplate.postForEntity("/api/libraryevent", requestEntity, LibraryEventDTO.class);

        // then
        assertThat(responseEntity).isNotNull();
        assertThat(Objects.requireNonNull(responseEntity.getBody()).getLibraryEventId()).isNotNull();
        LibraryEventDTO requestEntityBody = requestEntity.getBody();
        assert requestEntityBody != null;
        assertThat(requestEntityBody.getLibraryEventId()).isEqualTo(libraryEventDTO.getLibraryEventId());
        assertThat(requestEntityBody.getLibraryEventType()).isEqualTo(libraryEventDTO.getLibraryEventType());
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // get record from topic
        ConsumerRecord<Integer, String> singleRecord = KafkaTestUtils.getSingleRecord(consumer, "library-events");
        assertThat(expectedValue).isEqualTo(singleRecord.value());
    }

    @Test
    @DisplayName("should return bad request")
    void postLibraryEventError() {
        // given
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        libraryEvent.setLibraryEventId(null);
        HttpEntity<LibraryEventDTO> requestEntity = new HttpEntity<>(libraryEventDTO, headers);

        // when
        doReturn(libraryEvent).when(libraryEventTransformer).toEntity(any());
        doReturn(libraryEventDTO).when(libraryEventTransformer).toDTO(any());
        ResponseEntity<LibraryEventDTO> responseEntity = testRestTemplate.postForEntity("/api/libraryevent", requestEntity, LibraryEventDTO.class);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
