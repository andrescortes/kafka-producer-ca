package co.com.dev.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// debido al contexto de una aplicacion modular, es necesaria para crear un contexto
@SpringBootApplication(scanBasePackages = "co.com.dev.*")
public class TestApp {
    public static void main(String[] args) {
        SpringApplication.run(TestApp.class, args);
    }
}
