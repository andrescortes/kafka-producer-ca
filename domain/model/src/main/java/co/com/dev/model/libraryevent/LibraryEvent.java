package co.com.dev.model.libraryevent;

import co.com.dev.model.common.DomainEvent;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LibraryEvent{

    private Integer libraryEventId;
    private Book book;
}
