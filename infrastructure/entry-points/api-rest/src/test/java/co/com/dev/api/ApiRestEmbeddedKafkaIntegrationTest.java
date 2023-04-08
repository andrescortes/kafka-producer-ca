package co.com.dev.api;

import co.com.dev.api.library.dto.BookDTO;
import co.com.dev.api.library.dto.LibraryEventDTO;
import co.com.dev.api.library.dto.LibraryEventTypeDTO;
import co.com.dev.api.library.transformers.LibraryEventTransformer;
import co.com.dev.kafkahelper.KafkaFactoryProducer;
import co.com.dev.model.common.DomainEvent;
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
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class ApiRestEmbeddedKafkaIntegrationTest {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @MockBean
    private KafkaFactoryProducer kafkaFactoryProducer;

    @MockBean
    private LibraryEventTransformer libraryEventTransformer;

    private LibraryEvent libraryEvent;

    private LibraryEventDTO libraryEventDTO;

    @BeforeEach
    void setUp() {
        Book book = new Book(1, "Cien años de soledad", "Gabriel García Márquez");
        BookDTO bookDTO = new BookDTO(1, "Cien años de soledad", "Gabriel García Márquez");
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

    @Test
    @DisplayName("should create a LibraryEventDTO")
    void postLibraryEvent() {
        // given
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<LibraryEventDTO> requestEntity = new HttpEntity<>(libraryEventDTO, headers);

        // when
        doNothing().when(kafkaFactoryProducer).emitWithTopic(isA(DomainEvent.class));
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

        verify(kafkaFactoryProducer, times(1)).emitWithTopic(any());
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
        doNothing().when(kafkaFactoryProducer).emitWithTopic(isA(DomainEvent.class));
        doReturn(libraryEvent).when(libraryEventTransformer).toEntity(any());
        doReturn(libraryEventDTO).when(libraryEventTransformer).toDTO(any());
        ResponseEntity<LibraryEventDTO> responseEntity = testRestTemplate.postForEntity("/api/libraryevent", requestEntity, LibraryEventDTO.class);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        verify(kafkaFactoryProducer, times(0)).emitWithTopic(any());
    }
}
