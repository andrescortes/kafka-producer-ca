package co.com.dev.asyncmessagingsenders.library;

import co.com.dev.asyncmessagingsenders.common.KafkaPublisher;
import co.com.dev.model.common.ex.ErrorConvertToString;
import co.com.dev.model.libraryevent.LibraryEvent;
import co.com.dev.model.libraryevent.gateways.LibraryEventUpdateRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class LibraryEventPublisher<T> implements LibraryEventUpdateRepository<T> {
    private final KafkaPublisher<Integer, String, T> publisher;
    private final ObjectMapper objectMapper;

    public LibraryEventPublisher(KafkaPublisher<Integer, String, T> publisher, ObjectMapper objectMapper) {
        this.publisher = publisher;
        this.objectMapper = objectMapper;
    }

    @Override
    public T updateEventRepository(LibraryEvent libraryEvent) {
        try {
            String valueAsString = objectMapper.writeValueAsString(libraryEvent);
            return publisher.emit(libraryEvent.getLibraryEventId(), valueAsString);
        } catch (JsonProcessingException e) {
            throw new ErrorConvertToString(e.getMessage());
        }
    }
}
