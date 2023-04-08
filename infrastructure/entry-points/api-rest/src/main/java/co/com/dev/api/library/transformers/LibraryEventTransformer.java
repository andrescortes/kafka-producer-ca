package co.com.dev.api.library.transformers;

import co.com.dev.api.library.dto.LibraryEventDTO;
import co.com.dev.model.libraryevent.LibraryEvent;
import org.mapstruct.Mapper;

@Mapper
public interface LibraryEventTransformer {
    LibraryEvent toEntity(LibraryEventDTO libraryEventDTO);

    LibraryEventDTO toDTO(LibraryEvent libraryEvent);
}
