package co.com.dev.asyncmessagingsenders.common;

import co.com.dev.model.common.generic.EventPublishGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;


@Component
public class KafkaPublisher<K, V, T> implements EventPublishGateway<K, V, T> {
    private final KafkaTemplate<K, V> kafkaTemplate;

    @Value("${app.kafka.default-topic}")
    private String topic;

    public KafkaPublisher(KafkaTemplate<K, V> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;

    }

    @Override
    public T emit(K key, V value) {
        T send = (T) kafkaTemplate.send(topic, value);
        KafkaLog.showResult((ListenableFuture<SendResult<K, V>>) send);
        return (T) kafkaTemplate.send(topic, value);
    }
}
