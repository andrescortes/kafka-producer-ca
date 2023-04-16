package co.com.dev.model.libraryevent.gateways;

import co.com.dev.model.libraryevent.LibraryEvent;

public interface LibraryEventUpdateRepository<T> {
    T updateEventRepository(LibraryEvent libraryEvent);
}
