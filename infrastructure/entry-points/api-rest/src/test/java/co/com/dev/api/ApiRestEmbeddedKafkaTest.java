package co.com.dev.api;

import co.com.dev.kafkahelper.KafkaFactoryProducer;
import co.com.dev.model.libraryevent.Book;
import co.com.dev.model.libraryevent.LibraryEvent;
import co.com.dev.model.libraryevent.LibraryEventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class ApiRestEmbeddedKafkaTest {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @MockBean
    private KafkaFactoryProducer kafkaFactoryProducer;

    private LibraryEvent libraryEvent;


    @BeforeEach
    void setUp() {
        libraryEvent = new LibraryEvent();
        libraryEvent.setLibraryEventId(1);
        libraryEvent.setLibraryEventType(LibraryEventType.NEW);
        libraryEvent.setBook(new Book(1, "Cien años de soledad", "Gabriel García Márquez"));
    }

    @Test
    @DisplayName("should create a LibraryEvent")
    void postLibraryEvent() {
        // given
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<LibraryEvent> requestEntity = new HttpEntity<>(libraryEvent, headers);

        // when
        ResponseEntity<LibraryEvent> responseEntity = testRestTemplate.postForEntity("/api/libraryevent", requestEntity, LibraryEvent.class);

        // then
        assertThat(responseEntity).isNotNull();
        assertThat(Objects.requireNonNull(responseEntity.getBody()).getLibraryEventId()).isNotNull();
        LibraryEvent requestEntityBody = requestEntity.getBody();
        assert requestEntityBody != null;
        assertThat(requestEntityBody.getLibraryEventId()).isEqualTo(libraryEvent.getLibraryEventId());
        assertThat(requestEntityBody.getLibraryEventType()).isEqualTo(libraryEvent.getLibraryEventType());
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        verify(kafkaFactoryProducer, times(1)).emitWithTopic(any());
    }

    @Test
    @DisplayName("should return bad request")
    void postLibraryEventError() {
        // given
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        libraryEvent.setLibraryEventId(null);
        HttpEntity<LibraryEvent> requestEntity = new HttpEntity<>(libraryEvent, headers);

        // when
        ResponseEntity<LibraryEvent> responseEntity = testRestTemplate.postForEntity("/api/libraryevent", requestEntity, LibraryEvent.class);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        verify(kafkaFactoryProducer, times(0)).emitWithTopic(any());
    }
}
