package co.com.dev.api;

import co.com.dev.api.library.CreateLibraryEventSevice;
import co.com.dev.api.library.dto.BookDTO;
import co.com.dev.api.library.dto.LibraryEventDTO;
import co.com.dev.api.library.dto.LibraryEventTypeDTO;
import co.com.dev.api.library.transformers.LibraryEventTransformer;
import co.com.dev.kafkahelper.KafkaFactoryProducer;
import co.com.dev.model.common.DomainEvent;
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
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {CreateLibraryEventSevice.class})
class ApiRestTest {
    @Autowired
    private CreateLibraryEventSevice createLibraryEventSevice;

    @MockBean
    private KafkaFactoryProducer kafkaFactoryProducer;

    @MockBean
    private LibraryEventTransformer libraryEventTransformer;


    private LibraryEvent libraryEvent;
    private LibraryEventDTO libraryEventDTO;
    private BookDTO bookDTO;

    @BeforeEach
    void setUp() {
        Book book = new Book(1, "Cien años de soledad", "Gabriel García Márquez");
        bookDTO = new BookDTO(1, "Cien años de soledad", "Gabriel García Márquez");
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
    @DisplayName("Test POST /api/libraryevent with MockMvc")
    void testPostLibraryEventWithMockMvc() {
        //given
        //when
        doNothing().when(kafkaFactoryProducer).emitWithTopic(isA(DomainEvent.class));
        doReturn(libraryEvent).when(libraryEventTransformer).toEntity(any());
        doReturn(libraryEventDTO).when(libraryEventTransformer).toDTO(any());

        ResponseEntity<LibraryEventDTO> response = createLibraryEventSevice.postLibraryEvent(libraryEventDTO);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals(libraryEventDTO, response.getBody());

        Mockito.verify(kafkaFactoryProducer, Mockito.times(1)).emitWithTopic(any());
    }

    @Test
    @DisplayName("Test POST /api/libraryevent returns BAD_REQUEST when library event ID is missing")
    void testPostLibraryEventReturnsBadRequestWhenLibraryEventIdIsMissing() {
        // Given
        libraryEvent.setLibraryEventId(null);

        // When
        doNothing().when(kafkaFactoryProducer).emitWithTopic(isA(DomainEvent.class));
        doReturn(libraryEvent).when(libraryEventTransformer).toEntity(any());
        doReturn(libraryEventDTO).when(libraryEventTransformer).toDTO(any());
        ResponseEntity<LibraryEventDTO> response = createLibraryEventSevice.postLibraryEvent(libraryEventDTO);

        // Then
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertNull(response.getBody());
        Mockito.verifyNoInteractions(kafkaFactoryProducer);
    }
}
