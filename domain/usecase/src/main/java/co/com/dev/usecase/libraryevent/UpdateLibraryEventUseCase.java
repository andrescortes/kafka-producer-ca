package co.com.dev.usecase.libraryevent;

import co.com.dev.model.libraryevent.LibraryEvent;
import co.com.dev.model.libraryevent.gateways.LibraryEventUpdateRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UpdateLibraryEventUseCase {

    private final LibraryEventUpdateRepository libraryEventUpdateRepository;

    public void updateLibraryEvent(LibraryEvent libraryEvent) {
        libraryEventUpdateRepository.updateEventRepository(libraryEvent);
    }
}
