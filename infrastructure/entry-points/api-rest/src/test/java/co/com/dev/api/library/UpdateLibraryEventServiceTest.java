package co.com.dev.api.library;

import co.com.dev.api.library.dto.BookDTO;
import co.com.dev.api.library.dto.LibraryEventDTO;
import co.com.dev.api.library.dto.LibraryEventTypeDTO;
import co.com.dev.api.library.transformers.LibraryEventTransformer;
import co.com.dev.model.libraryevent.Book;
import co.com.dev.model.libraryevent.LibraryEvent;
import co.com.dev.usecase.libraryevent.UpdateLibraryEventUseCase;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UpdateLibraryEventServiceTest {

    @InjectMocks
    private UpdateLibraryEventService service;
    @Mock
    private UpdateLibraryEventUseCase useCase;

    private MockMvc mockMvc;

    @Mock
    private LibraryEventTransformer transformer;

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
                .libraryEventType(LibraryEventTypeDTO.UPDATE)
                .libraryEventId(123)
                .book(BookDTO.builder().bookId(321).bookAuthor("author1").bookName("book1").build())
                .build();
    }

    @Test
    void postLibraryEventSuccess() throws Exception {
        Mockito.when(transformer.toEntity(any())).thenReturn(libraryEvent);
        Mockito.when(transformer.toDTO(any())).thenReturn(libraryEventDTO);
        Mockito.doNothing().when(useCase).updateLibraryEvent(any());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/api/v1/libraryevent")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(mapper.writeValueAsString(libraryEventDTO))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.libraryEventId").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.libraryEventType").value("UPDATE"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.book.bookId").value(321))
                .andExpect(MockMvcResultMatchers.jsonPath("$.book.bookName").value("book1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.book.bookAuthor").value("author1"));
    }

    @Test
    void postLibraryEventBadRequest() throws Exception {
        libraryEvent.setLibraryEventId(null);
        Mockito.when(transformer.toEntity(any())).thenReturn(libraryEvent);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/api/v1/libraryevent")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(mapper.writeValueAsString(libraryEventDTO))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(content().string("LibraryEventId is required"));

    }
}
