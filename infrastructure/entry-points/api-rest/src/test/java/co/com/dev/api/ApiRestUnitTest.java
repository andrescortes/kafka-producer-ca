package co.com.dev.api;

import co.com.dev.api.library.CreateLibraryEventSevice;
import co.com.dev.api.library.dto.BookDTO;
import co.com.dev.api.library.dto.LibraryEventDTO;
import co.com.dev.api.library.dto.LibraryEventTypeDTO;
import co.com.dev.api.library.transformers.LibraryEventTransformer;
import co.com.dev.kafkahelper.KafkaFactoryProducer;
import co.com.dev.model.common.DomainEvent;
import co.com.dev.model.libraryevent.Book;
import co.com.dev.model.libraryevent.LibraryEvent;
import co.com.dev.model.libraryevent.LibraryEventType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(CreateLibraryEventSevice.class)
@AutoConfigureMockMvc
class ApiRestUnitTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KafkaFactoryProducer kafkaFactoryProducer;

    @MockBean
    private LibraryEventTransformer libraryEventTransformer;

    private ObjectMapper mapper;

    private LibraryEvent libraryEvent;

    private LibraryEventDTO libraryEventDTO;
    private BookDTO bookDTO;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        Book book = new Book(1, "Cien años de soledad", "Gabriel García Márquez");
        bookDTO = new BookDTO(1, "Cien años de soledad", "Gabriel García Márquez");
        libraryEvent = LibraryEvent.builder()
                .libraryEventType(LibraryEventType.NEW)
                .libraryEventId(1)
                .book(book)
                .build();
        libraryEventDTO = LibraryEventDTO.builder()
                .libraryEventType(LibraryEventTypeDTO.NEW)
                .libraryEventId(1)
                .book(bookDTO)
                .build();
    }

    @Test
    void shouldBePostLibraryEventSuccess() throws Exception {
        //given
        String json = mapper.writeValueAsString(libraryEventDTO);
        MockHttpServletRequestBuilder builder = post("/api/libraryevent")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON);

        //when
        doReturn(libraryEvent).when(libraryEventTransformer).toEntity(any());
        doReturn(libraryEventDTO).when(libraryEventTransformer).toDTO(any());
        doNothing().when(kafkaFactoryProducer).emitWithTopic(isA(DomainEvent.class));

        //then
        mockMvc.perform(builder)
                .andExpect(status().isCreated());
    }

    @Test
    void shouldBePostLibraryEventError() throws Exception {
        //given
        libraryEventDTO.setBook(null);
        String json = mapper.writeValueAsString(libraryEventDTO);
        MockHttpServletRequestBuilder builder = post("/api/libraryevent")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON);

        //when
        doReturn(libraryEvent).when(libraryEventTransformer).toEntity(any());
        doReturn(libraryEventDTO).when(libraryEventTransformer).toDTO(any());
        doNothing().when(kafkaFactoryProducer).emitWithTopic(isA(DomainEvent.class));

        //then
        mockMvc.perform(builder)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void shouldBePostLibraryEventErrorValid() throws Exception {
        //given
        String errorExpected = "book.bookAuthor - must not be null, book.bookName - must not be null";
        libraryEventDTO.setBook(BookDTO.builder()
                .bookId(1)
                .bookAuthor(null)
                .bookName(null)
                .build());
        String json = mapper.writeValueAsString(libraryEventDTO);
        MockHttpServletRequestBuilder builder = post("/api/libraryevent")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON);

        //when
        doReturn(libraryEvent).when(libraryEventTransformer).toEntity(any());
        doReturn(libraryEventDTO).when(libraryEventTransformer).toDTO(any());
        doNothing().when(kafkaFactoryProducer).emitWithTopic(isA(DomainEvent.class));

        //then
        mockMvc.perform(builder)
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(errorExpected));
    }
}
