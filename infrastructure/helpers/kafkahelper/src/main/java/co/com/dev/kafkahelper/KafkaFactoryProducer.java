package co.com.dev.kafkahelper;

import co.com.dev.model.common.DomainEvent;
import co.com.dev.model.common.gateway.DomainEventBus;
import co.com.dev.model.ex.ErrorConvertToString;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.List;
import java.util.logging.Level;

/**
 * The type Kafka factory producer.
 */
@Log
@Component
public class KafkaFactoryProducer implements DomainEventBus {
    @Autowired
    private KafkaTemplate<Integer, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public <I, T> Void emit(DomainEvent<I, T> event) {
        Integer key = (Integer) event.getEventId();
        String value;
        try {
            value = objectMapper.writeValueAsString(event.getData());
        } catch (JsonProcessingException e) {
            throw new ErrorConvertToString(e.getMessage());
        }

        ListenableFuture<SendResult<Integer, String>> sendResultListenableFuture = kafkaTemplate.sendDefault(key, value);
        sendResultListenableFuture.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable ex) {
                handlerFailure(key, value, ex);
            }

            @Override
            public void onSuccess(SendResult<Integer, String> result) {
                handlerSuccess(key, value, result);
                System.out.println("number partition = " + result.getRecordMetadata().partition());
            }
        });
        return null;
    }

    private void handlerFailure(Integer key, String value, Throwable ex) {
        log.log(Level.SEVERE, "Error Sending the Message and the exception is {0}", ex.getMessage());
        try {
            throw ex;
        } catch (Throwable e) {
            log.log(Level.SEVERE, "Error in OnFailure", e.getMessage());
        }
    }

    private void handlerSuccess(Integer key, String value, SendResult<Integer, String> result) {
        List<Object> key1 = List.of(key, value, result.getRecordMetadata().partition());
        System.out.println("handlerSuccess = " + key1);
        log.log(Level.SEVERE, "Message Send SuccessFully for the Key: {0} and the value is {1}, partition is {2}", key1);
        // Do nothing because of X and Y.
    }
}
