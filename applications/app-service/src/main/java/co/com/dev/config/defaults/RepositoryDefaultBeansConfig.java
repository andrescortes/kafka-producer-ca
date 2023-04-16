package co.com.dev.config.defaults;

import co.com.dev.model.libraryevent.LibraryEvent;
import co.com.dev.model.libraryevent.gateways.LibraryEventRepository;
import co.com.dev.model.libraryevent.gateways.LibraryEventUpdateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Slf4j
@Configuration
public class RepositoryDefaultBeansConfig<T> {
    private final LibraryEventRepository libraryEventRepository = new LibraryEventRepository() {
        @Override
        public void sendLibraryEvent(LibraryEvent libraryEvent) {
            log.info("Using bean fake called LibraryEventRepository.sendLibraryEvent");
        }
    };

    private final LibraryEventUpdateRepository<T> libraryEventUpdateRepository = new LibraryEventUpdateRepository() {
        @Override
        public T updateEventRepository(LibraryEvent libraryEvent) {
            log.info("Using bean fake called LibraryEventUpdateRepository.updateEventRepository");
            return null;
        }
    };

    @Bean
    @ConditionalOnMissingBean
    public LibraryEventRepository libraryEventRepository() {
        log.info("Using bean fake called libraryEventRepository");
        return libraryEventRepository;
    }
    @Bean
    @ConditionalOnMissingBean
    public LibraryEventUpdateRepository<T> libraryEventUpdateRepository() {
        log.info("Using bean fake called libraryEventUpdateRepository");
        return libraryEventUpdateRepository;
    }
}
