package co.com.dev.asyncmessagingsenders.common;

import co.com.dev.model.common.ex.GlobalException;
import lombok.extern.java.Log;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.logging.Level;


@Log
public class KafkaLog {
    private KafkaLog() {

    }

    public static <K, V> void showResult(ListenableFuture<SendResult<K, V>> listenableFutureResult) {
        listenableFutureResult.addCallback(new ListenableFutureCallback<>() {

            /**
             * Called when the {@link ListenableFuture} completes with success.
             * <p>Note that Exceptions raised by this method are ignored.
             *
             * @param result the result
             */
            @Override
            public void onSuccess(SendResult<K, V> result) {
                handlerSuccess(result.getProducerRecord().key(), result.getProducerRecord().value(), result);
            }

            /**
             * Called when the {@link ListenableFuture} completes with failure.
             * <p>Note that Exceptions raised by this method are ignored.
             *
             * @param ex the failure
             */
            @Override
            public void onFailure(Throwable ex) {
                handlerFailure(ex);
            }
        });
    }

    private static <K, V> void handlerSuccess(K key, V value, SendResult<K, V> result) {
        Object[] params = {key, value, result.getRecordMetadata().partition()};
        KafkaLog.log.log(Level.INFO, "Message Send SuccessFully for the Key: {0} and the value is {1}, partition is {2}", params);
    }

    private static void handlerFailure(Throwable ex) {
        KafkaLog.log.log(Level.SEVERE, "Error Sending the Message and the exception is {0}", ex.getMessage());
        throw new GlobalException(ex.getMessage());
    }
}
