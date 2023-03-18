package co.com.dev.asynckafkaproducer.libraryevent;

import co.com.dev.model.common.DomainEvent;
import co.com.dev.model.common.gateway.DomainEventBus;
import co.com.dev.model.libraryevent.LibraryEvent;
import co.com.dev.model.libraryevent.gateways.LibraryEventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * The type Library event producer.
 */
@Component
@AllArgsConstructor
public class LibraryEventProducer implements LibraryEventRepository {
    private final DomainEventBus domainEventBus;

    @Override
    public void sendLibraryEvent(LibraryEvent libraryEvent) {
        domainEventBus.emit(new DomainEvent<>(libraryEvent.getLibraryEventId(), libraryEvent));
    }
}
