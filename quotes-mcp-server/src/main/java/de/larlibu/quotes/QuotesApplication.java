package de.larlibu.quotes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Einstiegspunkt der Spring-Boot-Anwendung.
 */
@SpringBootApplication
public class QuotesApplication {

    /**
     * Startet die Anwendung.
     *
     * @param args Kommandozeilenparameter
     */
    static void main(String[] args) {
        SpringApplication.run(QuotesApplication.class, args);
    }
}
