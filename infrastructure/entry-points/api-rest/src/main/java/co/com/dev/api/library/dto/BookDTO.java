package co.com.dev.api.library.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class BookDTO {
    private Integer bookId;
    @NotNull
    private String bookName;
    @NotNull
    private String bookAuthor;
}
