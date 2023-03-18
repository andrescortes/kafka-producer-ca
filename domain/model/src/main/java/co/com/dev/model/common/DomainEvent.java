package co.com.dev.model.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class DomainEvent<I, T> {
    private final I eventId;
    private T data;
}
