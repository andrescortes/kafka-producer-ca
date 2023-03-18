package co.com.dev.model.libraryevent.gateways;

import co.com.dev.model.libraryevent.LibraryEvent;

public interface LibraryEventRepository {
    void sendLibraryEvent(LibraryEvent libraryEvent);
}
