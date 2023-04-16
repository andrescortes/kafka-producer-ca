package co.com.dev.usecase.libraryevent;

import co.com.dev.model.libraryevent.LibraryEvent;
import co.com.dev.model.libraryevent.gateways.LibraryEventUpdateRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UpdateLibraryEventUseCase<T> {
    private final LibraryEventUpdateRepository<T> libraryEventUpdateRepository;

    public T updateLibraryEvent(LibraryEvent libraryEvent) {
         return libraryEventUpdateRepository.updateEventRepository(libraryEvent);
    }
}
