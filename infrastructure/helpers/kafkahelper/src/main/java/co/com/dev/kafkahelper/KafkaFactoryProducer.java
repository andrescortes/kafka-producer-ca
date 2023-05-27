package co.com.dev.kafkahelper;

import co.com.dev.model.common.DomainEvent;
import co.com.dev.model.common.ex.ErrorConvertToString;
import co.com.dev.model.common.ex.ErrorSendKafka;
import co.com.dev.model.common.ex.GlobalException;
import co.com.dev.model.common.domainevent.gateway.DomainEventBus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

/**
 * The type Kafka factory producer.
 */
@Log
@Component
public class KafkaFactoryProducer implements DomainEventBus {

    @Value("${app.kafka.default-topic}")
    private String topic;

    @Autowired
    private KafkaTemplate<Integer, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public <I, T> void emitDefault(DomainEvent<I, T> event) {
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
                handlerFailure(ex);
            }

            @Override
            public void onSuccess(SendResult<Integer, String> result) {
                handlerSuccess(key, value, result);
            }
        });
    }

    public <I, T> SendResult<Integer, String> emitSync(DomainEvent<I, T> event) {
        Integer key = (Integer) event.getEventId();
        String value;
        SendResult<Integer, String> sendResult;

        try {
            value = objectMapper.writeValueAsString(event.getData());
        } catch (JsonProcessingException e) {
            throw new ErrorConvertToString(e.getMessage());
        }

        try {
            sendResult = kafkaTemplate.sendDefault(key, value).get();
        } catch (ExecutionException | InterruptedException e) {
            KafkaFactoryProducer.log.log(Level.SEVERE, "ExecutionException/InterruptedException, Error sending message and the exception is {}", e.getMessage());
            Thread.currentThread().interrupt();
            throw new ErrorSendKafka(e.getMessage());
        } catch (Exception e) {
            KafkaFactoryProducer.log.log(Level.SEVERE, "Exception, Error sending message and the exception is {}", e.getMessage());
            throw new GlobalException(e.getMessage());
        }

        return sendResult;
    }
    @Override
    public <I, T> void emitWithTopic(DomainEvent<I, T> event) {
        Integer key = (Integer) event.getEventId();
        String value;
        try {
            value = objectMapper.writeValueAsString(event.getData());
        } catch (JsonProcessingException e) {
            throw new ErrorConvertToString(e.getMessage());
        }
        ProducerRecord<Integer, String> producerRecord = this.buildProducerRecord(topic, key, value);

        ListenableFuture<SendResult<Integer, String>> sendResultListenableFuture = kafkaTemplate.send(producerRecord);
        sendResultListenableFuture.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable ex) {
                handlerFailure(ex);
            }

            @Override
            public void onSuccess(SendResult<Integer, String> result) {
                handlerSuccess(key, value, result);
            }
        });
    }

    public <I, T> ListenableFuture<SendResult<Integer, String>> emitWithTopicApproach2(DomainEvent<I, T> event) {
        Integer key = (Integer) event.getEventId();
        String value;
        try {
            value = objectMapper.writeValueAsString(event.getData());
        } catch (JsonProcessingException e) {
            throw new ErrorConvertToString(e.getMessage());
        }
        ProducerRecord<Integer, String> producerRecord;
        producerRecord = this.buildProducerRecord(topic, key, value);

        ListenableFuture<SendResult<Integer, String>> sendResultListenableFuture = null;

        sendResultListenableFuture = kafkaTemplate.send(producerRecord);
        sendResultListenableFuture.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable ex) {
                handlerFailure(ex);
            }

            @Override
            public void onSuccess(SendResult<Integer, String> result) {
                handlerSuccess(key, value, result);
            }
        });
        return sendResultListenableFuture;
    }

    private ProducerRecord<Integer, String> buildProducerRecord(String topic, Integer key, String value) {
        List<Header> headers = List.of(new RecordHeader("event-source", "scanner".getBytes()));
        return new ProducerRecord<>(topic, null, key, value, headers);
    }


    private static void handlerFailure(Throwable ex) {
        KafkaFactoryProducer.log.log(Level.INFO, "Error Sending the Message and the exception is {0}", ex.getMessage());
        throw new GlobalException(ex.getMessage());
    }

    private static void handlerSuccess(Integer key, String value, SendResult<Integer, String> result) {
        Object[] params = {key, value, result.getRecordMetadata().partition()};

        log.log(Level.INFO, "Message Send SuccessFully for the Key: {0} and the value is {1}, partition is {2}", params);
    }
}
