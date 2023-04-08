package co.com.dev.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AutoCreateConfigTest {
    @InjectMocks
    private AutoCreateConfig autoCreateConfig;

    @Test
    void shouldBeAutoCreatingConfigNotNull() {
        Assertions.assertNotNull(autoCreateConfig.libraryEvents());
    }
}
