package co.com.dev.model.common.generic;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EventPublish<K, V> {
    private K id;
    private V body;
}
