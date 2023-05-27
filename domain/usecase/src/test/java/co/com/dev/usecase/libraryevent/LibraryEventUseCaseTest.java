package co.com.dev.usecase.libraryevent;

import co.com.dev.model.libraryevent.Book;
import co.com.dev.model.libraryevent.LibraryEvent;
import co.com.dev.model.libraryevent.LibraryEventType;
import co.com.dev.model.libraryevent.gateways.LibraryEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class CreateLibraryEventUseCaseTest {

    @Mock
    private LibraryEventRepository libraryEventRepository;

    @InjectMocks
    private CreateLibraryEventUseCase createLibraryEventUseCase;

    private LibraryEvent libraryEvent;
    @BeforeEach
    void setUp() {
        libraryEvent = LibraryEvent.builder()
                .libraryEventId(1)
                .book(Book.builder()
                        .bookId(1)
                        .bookName("Spring")
                        .bookAuthor("John")
                        .build())
                .build();
    }

    @Test
    void shouldBeSendToKafkaSuccess() {
        Mockito.doNothing().when(libraryEventRepository).sendLibraryEvent(Mockito.any(LibraryEvent.class));
        createLibraryEventUseCase.sendToKafka(libraryEvent);
        Mockito.verify(libraryEventRepository, Mockito.times(1)).sendLibraryEvent(Mockito.any(LibraryEvent.class));
    }
}
