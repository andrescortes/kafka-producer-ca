package co.com.dev.api;

import co.com.dev.kafkahelper.KafkaFactoryProducer;
import co.com.dev.model.common.DomainEvent;
import co.com.dev.model.libraryevent.LibraryEvent;
import co.com.dev.model.libraryevent.LibraryEventType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ApiRest {
    private final KafkaFactoryProducer kafkaFactoryProducer;


    @PostMapping("/libraryevent")
    public ResponseEntity<LibraryEvent> postLibraryEvent(@RequestBody LibraryEvent libraryEvent) {
        if (libraryEvent.getLibraryEventId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        libraryEvent.setLibraryEventType(LibraryEventType.NEW);
        kafkaFactoryProducer.emitWithTopic(new DomainEvent<>(libraryEvent.getLibraryEventId(), libraryEvent));
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }
}
