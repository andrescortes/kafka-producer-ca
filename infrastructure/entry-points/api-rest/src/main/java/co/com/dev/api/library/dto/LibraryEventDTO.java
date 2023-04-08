package co.com.dev.api.library.dto;

import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LibraryEventDTO {

    private Integer libraryEventId;
    private LibraryEventTypeDTO libraryEventType;
    @NotNull
    @Valid
    private BookDTO book;
}
