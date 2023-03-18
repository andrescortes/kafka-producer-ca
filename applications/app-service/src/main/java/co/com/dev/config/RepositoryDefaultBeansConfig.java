package co.com.dev.config;

import co.com.dev.model.libraryevent.LibraryEvent;
import co.com.dev.model.libraryevent.gateways.LibraryEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Slf4j
@Configuration
public class RepositoryDefaultBeansConfig {
    private final LibraryEventRepository libraryEventRepository = new LibraryEventRepository() {
        @Override
        public void sendLibraryEvent(LibraryEvent libraryEvent) {
        }
    };

    @Bean
    @ConditionalOnMissingBean
    public LibraryEventRepository libraryEventRepository() {
        log.info("Using bean fake called libraryEventRepository");
        return libraryEventRepository;
    }
}
