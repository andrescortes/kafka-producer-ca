package co.com.dev.asyncmessagingsenders.library;

import co.com.dev.kafkahelper.KafkaFactoryProducer;
import co.com.dev.model.common.DomainEvent;
import co.com.dev.model.libraryevent.LibraryEvent;
import co.com.dev.model.libraryevent.gateways.LibraryEventUpdateRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class LibraryEventUpdatePublisher implements LibraryEventUpdateRepository {

    private final KafkaFactoryProducer kafkaFactoryProducer;

    @Override
    public void updateEventRepository(LibraryEvent libraryEvent) {
        kafkaFactoryProducer.emitWithTopic(new DomainEvent<>(libraryEvent.getLibraryEventId(),libraryEvent));
    }
}
