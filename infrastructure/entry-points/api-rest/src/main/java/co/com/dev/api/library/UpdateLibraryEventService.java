package co.com.dev.api.library;

import co.com.dev.api.library.dto.LibraryEventDTO;
import co.com.dev.api.library.transformers.LibraryEventTransformer;
import co.com.dev.model.common.ex.GlobalException;
import co.com.dev.model.libraryevent.LibraryEvent;
import co.com.dev.model.libraryevent.LibraryEventType;
import co.com.dev.usecase.libraryevent.UpdateLibraryEventUseCase;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class UpdateLibraryEventService {
    private final UpdateLibraryEventUseCase<ListenableFuture<SendResult<Integer, String>>> updateLibraryEventUseCase;
    private final LibraryEventTransformer libraryEventTransformer;

    private static void showResult(ListenableFuture<SendResult<Integer, String>> sendResultListenableFuture) {
        try {
            SendResult<Integer, String> sendResult = sendResultListenableFuture.get();
            log.info("key: {}, Value: {}, topic: {}, partition: {}, extras: {}", sendResult.getProducerRecord().key(), sendResult.getProducerRecord().value(), sendResult.getProducerRecord().topic(), sendResult.getProducerRecord().partition(), sendResult.getRecordMetadata().toString());

        } catch (InterruptedException | ExecutionException e) {
            log.info("Interrupted! {}", e.getMessage(), e);
            // Restore interrupted state...
            Thread.currentThread().interrupt();
            throw new GlobalException(e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateLibraryEvent(@RequestBody @Validated LibraryEventDTO libraryEventDTO) {
        LibraryEvent libraryEvent = libraryEventTransformer.toEntity(libraryEventDTO);

        if (libraryEvent.getLibraryEventId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please pass the LibrayEventId");
        }

        libraryEvent.setLibraryEventType(LibraryEventType.UPDATE);
        showResult(updateLibraryEventUseCase.updateLibraryEvent(libraryEvent));
        return ResponseEntity.status(HttpStatus.OK).body(this.libraryEventTransformer.toDTO(libraryEvent));
    }
}
