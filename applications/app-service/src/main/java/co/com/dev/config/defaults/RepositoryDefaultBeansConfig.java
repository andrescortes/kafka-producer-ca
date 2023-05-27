package co.com.dev.config.defaults;

import co.com.dev.model.libraryevent.gateways.LibraryEventRepository;
import co.com.dev.model.libraryevent.gateways.LibraryEventUpdateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RepositoryDefaultBeansConfig {
    private final LibraryEventRepository libraryEventRepository = libraryEvent -> log.info("Using bean fake called LibraryEventRepository.sendLibraryEvent");

    private final LibraryEventUpdateRepository libraryEventUpdateRepository = libraryEvent -> log.info("Using bean fake called LibraryEventUpdateRepository.updateEventRepository");

    @Bean
    @ConditionalOnMissingBean
    public LibraryEventRepository libraryEventRepository() {
        log.info("Using bean fake called libraryEventRepository");
        return libraryEventRepository;
    }

    @Bean
    @ConditionalOnMissingBean
    public LibraryEventUpdateRepository libraryEventUpdateRepository() {
        log.info("Using bean fake called libraryEventUpdateRepository");
        return libraryEventUpdateRepository;
    }
}
