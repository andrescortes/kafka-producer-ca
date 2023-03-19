package co.com.dev.api;

import co.com.dev.kafkahelper.KafkaFactoryProducer;
import co.com.dev.model.common.DomainEvent;
import co.com.dev.model.libraryevent.LibraryEvent;
import co.com.dev.usecase.libraryevent.LibraryEventUseCase;
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

    private final LibraryEventUseCase useCase;
    private KafkaFactoryProducer kafkaFactoryProducer;


    @PostMapping("/libraryevent")
    public ResponseEntity<LibraryEvent> postLibraryEvent(@RequestBody LibraryEvent libraryEvent) {
        //invoke kafka producer
//        useCase.sendToKafka(libraryEvent);
        log.info("before sendEvent");
        /*SendResult<Integer, String> sendResult = kafkaFactoryProducer.emitSync(new DomainEvent<>(libraryEvent.getLibraryEventId(), libraryEvent));
        log.info("SendResult is {}",sendResult.toString());*/
        kafkaFactoryProducer.emitWithTopic(new DomainEvent<>(libraryEvent.getLibraryEventId(), libraryEvent));
        log.info("after sendEvent");


        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }
}
