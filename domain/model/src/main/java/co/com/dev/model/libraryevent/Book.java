package co.com.dev.model.libraryevent;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Book {
    private Integer bookId;
    private String bookName;
    private String bookAuthor;
}
