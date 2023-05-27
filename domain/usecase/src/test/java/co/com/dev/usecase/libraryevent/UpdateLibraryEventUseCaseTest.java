package co.com.dev.usecase.libraryevent;

import co.com.dev.model.libraryevent.Book;
import co.com.dev.model.libraryevent.LibraryEvent;
import co.com.dev.model.libraryevent.gateways.LibraryEventUpdateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateLibraryEventUseCaseTest {

    @Mock
    private LibraryEventUpdateRepository libraryEventUpdateRepository;

    @InjectMocks
    private UpdateLibraryEventUseCase updateLibraryEventUseCase;

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
    void updateLibraryEvent() {
        Mockito.doNothing().when(libraryEventUpdateRepository).updateEventRepository(Mockito.any(LibraryEvent.class));
        updateLibraryEventUseCase.updateLibraryEvent(libraryEvent);
        Mockito.verify(libraryEventUpdateRepository, Mockito.times(1)).updateEventRepository(Mockito.any(LibraryEvent.class));
    }
}
