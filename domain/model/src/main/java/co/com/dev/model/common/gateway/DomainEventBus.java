package co.com.dev.model.common.gateway;

import co.com.dev.model.common.DomainEvent;

/**
 * The interface Domain event bus.
 */
public interface DomainEventBus {
    /**
     * Emit void.
     *
     * @param <I>   the type parameter
     * @param <T>   the type parameter
     * @param event the event
     */
    <I,T> void emitDefault(DomainEvent<I,T> event);
//    <I,T> void emitSync(DomainEvent<I,T> event);
}
