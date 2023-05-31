package co.com.dev.api.library;

import co.com.dev.api.library.dto.LibraryEventDTO;
import co.com.dev.api.library.transformers.LibraryEventTransformer;
import co.com.dev.model.libraryevent.LibraryEvent;
import co.com.dev.model.libraryevent.LibraryEventType;
import co.com.dev.usecase.libraryevent.UpdateLibraryEventUseCase;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class UpdateLibraryEventService {

    private final UpdateLibraryEventUseCase updateLibraryEventUseCase;
    private final LibraryEventTransformer libraryEventTransformer;

    @PutMapping("/libraryevent")
    public ResponseEntity<LibraryEventDTO> updateLibraryEvent(@RequestBody @Validated LibraryEventDTO libraryEventDTO) {
        LibraryEvent libraryEvent = libraryEventTransformer.toEntity(libraryEventDTO);
        if (Objects.isNull(libraryEvent.getLibraryEventId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        libraryEvent.setLibraryEventType(LibraryEventType.UPDATE);
        updateLibraryEventUseCase.updateLibraryEvent(libraryEvent);
        return ResponseEntity.status(HttpStatus.OK).body(libraryEventTransformer.toDTO(libraryEvent));
    }
}
