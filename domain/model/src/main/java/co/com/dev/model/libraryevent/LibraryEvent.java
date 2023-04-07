package co.com.dev.model.libraryevent;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LibraryEvent{

    private Integer libraryEventId;
    private LibraryEventType libraryEventType;
    private Book book;
}
