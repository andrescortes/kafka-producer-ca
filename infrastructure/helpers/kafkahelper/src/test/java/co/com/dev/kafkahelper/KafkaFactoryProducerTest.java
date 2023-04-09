package co.com.dev.kafkahelper;

import co.com.dev.model.common.DomainEvent;
import co.com.dev.model.libraryevent.Book;
import co.com.dev.model.libraryevent.LibraryEvent;
import co.com.dev.model.libraryevent.LibraryEventType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;

@ExtendWith(MockitoExtension.class)
class KafkaFactoryProducerTest {
    @InjectMocks
    private KafkaFactoryProducer kafkaFactoryProducer;

    @Mock
    private KafkaTemplate<Integer, String> kafkaTemplate;

    @Spy
    private ObjectMapper mapper;

    private LibraryEvent libraryEvent;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(kafkaFactoryProducer, "topic", "library-events", String.class);

        mapper = new ObjectMapper();
        Book book = new Book(1, "Kafka to developers", "Dilip Tur");
        libraryEvent = LibraryEvent.builder()
                .libraryEventType(LibraryEventType.NEW)
                .libraryEventId(1)
                .book(book)
                .build();
    }

    @Test
    void shouldBeEmitWithTopicApproach2Error() {
        //given
        SettableListenableFuture<?> future = new SettableListenableFuture<>();
        future.setException(new RuntimeException("Exception calling Kafka"));

        //when
        Mockito.when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(future);

        //then
        assertThrows(Exception.class, kafkaFactoryProducer.emitWithTopicApproach2(new DomainEvent<>(libraryEvent.getLibraryEventId(), libraryEvent))::get);
    }

    @Test
    void shouldBeEmitWithTopicApproach2Success() throws JsonProcessingException, ExecutionException, InterruptedException {
        //given
        String valueAsString = mapper.writeValueAsString(libraryEvent);
        String expectedValue = "{\"libraryEventId\":1,\"libraryEventType\":\"NEW\",\"book\":{\"bookId\":1,\"bookName\":\"Kafka to developers\",\"bookAuthor\":\"Dilip Tur\"}}";
        SettableListenableFuture<SendResult<Integer, String>> future = new SettableListenableFuture<>();
        ProducerRecord<Integer, String> producerRecord = new ProducerRecord<>("library-events", libraryEvent.getLibraryEventId(), valueAsString);
        RecordMetadata recordMetadata = new RecordMetadata(new TopicPartition("library-events", 1), 1, 1, 342, 1, 1);
        SendResult<Integer, String> sendResult = new SendResult<>(producerRecord, recordMetadata);
        future.set(sendResult);

        //when
        Mockito.when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(future);
        ListenableFuture<SendResult<Integer, String>> sendResultListenableFuture = kafkaFactoryProducer.emitWithTopicApproach2(new DomainEvent<>(libraryEvent.getLibraryEventId(), libraryEvent));

        //then
        SendResult<Integer, String> result = sendResultListenableFuture.get();
        assertThat(result.getRecordMetadata().partition()).isEqualTo(1);
        assertThat(result.getRecordMetadata().topic()).isEqualTo("library-events");
        assertThat(result.getProducerRecord().value()).isEqualTo(expectedValue);
    }
}
