package co.com.dev.model.common.generic;

public interface EventPublishGateway<K, V, T> {

    T emit(K id, V body);
}
