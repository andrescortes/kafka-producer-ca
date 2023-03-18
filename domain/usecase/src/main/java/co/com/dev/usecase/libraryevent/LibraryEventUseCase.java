package co.com.dev.usecase.libraryevent;

import co.com.dev.model.libraryevent.LibraryEvent;
import co.com.dev.model.libraryevent.gateways.LibraryEventRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LibraryEventUseCase {
    private final LibraryEventRepository libraryEventRepository;

    public void sendToKafka(LibraryEvent data) {
        libraryEventRepository.sendLibraryEvent(data);
    }
}
