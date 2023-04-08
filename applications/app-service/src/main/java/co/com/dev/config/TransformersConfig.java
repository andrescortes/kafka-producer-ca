package co.com.dev.config;

import co.com.dev.api.library.transformers.LibraryEventTransformer;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransformersConfig {
    @Bean
    public LibraryEventTransformer libraryEventTransformer() {
        return Mappers.getMapper(LibraryEventTransformer.class);
    }
}
