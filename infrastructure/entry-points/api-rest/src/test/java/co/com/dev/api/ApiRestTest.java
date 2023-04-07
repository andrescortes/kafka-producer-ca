package co.com.dev.api;

import co.com.dev.kafkahelper.KafkaFactoryProducer;
import co.com.dev.model.libraryevent.Book;
import co.com.dev.model.libraryevent.LibraryEvent;
import co.com.dev.model.libraryevent.LibraryEventType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {ApiRest.class})
class ApiRestTest {
    @Autowired
    private ApiRest apiRest;

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
    @DisplayName("Test POST /api/libraryevent with MockMvc")
    void testPostLibraryEventWithMockMvc() {
        ResponseEntity<LibraryEvent> response = apiRest.postLibraryEvent(libraryEvent);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals(libraryEvent, response.getBody());

        Mockito.verify(kafkaFactoryProducer, Mockito.times(1)).emitWithTopic(any());
    }

    @Test
    @DisplayName("Test POST /api/libraryevent returns BAD_REQUEST when library event ID is missing")
    void testPostLibraryEventReturnsBadRequestWhenLibraryEventIdIsMissing() {
        // Given
        libraryEvent.setLibraryEventId(null);

        // When
        ResponseEntity<LibraryEvent> response = apiRest.postLibraryEvent(libraryEvent);

        // Then
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertNull(response.getBody());
        Mockito.verifyNoInteractions(kafkaFactoryProducer);
    }
}
