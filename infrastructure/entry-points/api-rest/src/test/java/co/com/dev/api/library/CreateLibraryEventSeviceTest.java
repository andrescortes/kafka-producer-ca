package co.com.dev.api.library;

import co.com.dev.api.library.dto.BookDTO;
import co.com.dev.api.library.dto.LibraryEventDTO;
import co.com.dev.api.library.dto.LibraryEventTypeDTO;
import co.com.dev.api.library.transformers.LibraryEventTransformer;
import co.com.dev.model.libraryevent.Book;
import co.com.dev.model.libraryevent.LibraryEvent;
import co.com.dev.usecase.libraryevent.CreateLibraryEventUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CreateLibraryEventSeviceTest {

    private MockMvc mockMvc;

    @Mock
    private CreateLibraryEventUseCase useCase;

    @Mock
    private LibraryEventTransformer transformer;

    @InjectMocks
    private CreateLibraryEventSevice service;

    private LibraryEvent libraryEvent;
    private LibraryEventDTO libraryEventDTO;

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(service).build();
        mapper = new ObjectMapper();
        libraryEvent = LibraryEvent.builder()
                .libraryEventId(123)
                .book(Book.builder()
                        .bookId(321)
                        .bookName("book1")
                        .bookAuthor("author1")
                        .build())
                .build();

        libraryEventDTO = LibraryEventDTO.builder()
                .libraryEventType(LibraryEventTypeDTO.NEW)
                .libraryEventId(123)
                .book(BookDTO.builder().bookId(321).bookAuthor("author1").bookName("book1").build())
                .build();
    }

    @Test
    void postLibraryEventSuccess() throws Exception {
        Mockito.when(transformer.toEntity(any())).thenReturn(libraryEvent);
        Mockito.when(transformer.toDTO(any())).thenReturn(libraryEventDTO);
        Mockito.doNothing().when(useCase).sendToKafka(any());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/v1/libraryevent")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(mapper.writeValueAsString(libraryEventDTO))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.libraryEventId").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.libraryEventType").value("NEW"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.book.bookId").value(321))
                .andExpect(MockMvcResultMatchers.jsonPath("$.book.bookName").value("book1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.book.bookAuthor").value("author1"));
    }

    @Test
    void postLibraryEventBadRequest() throws Exception {
        libraryEvent.setLibraryEventId(null);
        Mockito.when(transformer.toEntity(any())).thenReturn(libraryEvent);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/v1/libraryevent")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(mapper.writeValueAsString(libraryEventDTO))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }
}
