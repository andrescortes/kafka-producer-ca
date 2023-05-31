package co.com.dev.asyncmessagingsenders.library;

import co.com.dev.kafkahelper.KafkaFactoryProducer;
import co.com.dev.model.libraryevent.Book;
import co.com.dev.model.libraryevent.LibraryEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LibraryEventUpdatePublisherTest {

    @Mock
    private KafkaFactoryProducer kafkaFactoryProducer;

    @InjectMocks
    private LibraryEventUpdatePublisher publisher;

    @Test
    void updateEventRepositorySuccess() {
        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(1)
                .book(Book.builder()
                        .bookId(1)
                        .build())
                .build();

        Mockito.doNothing().when(kafkaFactoryProducer).emitWithTopic(Mockito.any());
        publisher.updateEventRepository(libraryEvent);
        Mockito.verify(kafkaFactoryProducer, Mockito.times(1)).emitWithTopic(Mockito.any());
    }
}
