package de.larlibu.quotes.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.larlibu.quotes.model.Quote;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = QuoteRepositoryTest.TestConfig.class)
@TestPropertySource(properties = "quotes.file.path=${java.io.tmpdir}/quotes-repository-test.json")
class QuoteRepositoryTest {

    @EnableAutoConfiguration
    @Import(QuoteRepository.class)
    static class TestConfig {
    }

    @Autowired
    private QuoteRepository repository;

    private final Path testFilePath = Path.of(System.getProperty("java.io.tmpdir"), "quotes-repository-test.json");

    @BeforeEach
    void cleanup() throws IOException {
        Files.deleteIfExists(testFilePath);
    }

    @Test
    void testSaveAndLoad() throws IOException {
        Quote quote = new Quote(
                "Testzitat",
                "Testautor",
                "Testthema",
                "deutsch");
        quote.setId("repo-test-1");
        quote.setErstellungsdatum(LocalDateTime.now());

        repository.addQuote(quote);
        List<Quote> loadedQuotes = repository.getAllQuotes();

        assertEquals(1, loadedQuotes.size());
        assertEquals("repo-test-1", loadedQuotes.get(0).getId());
        assertEquals("Testzitat", loadedQuotes.get(0).getText());
    }

    @Test
    void testDeleteById() throws IOException {
        Quote quote = new Quote(
                "Loesch mich",
                "Testautor",
                "Testthema",
                "deutsch");
        quote.setId("repo-test-2");
        quote.setErstellungsdatum(LocalDateTime.now());
        repository.addQuote(quote);

        boolean deleted = repository.deleteById("repo-test-2");
        List<Quote> loadedQuotes = repository.getAllQuotes();

        assertTrue(deleted);
        assertTrue(loadedQuotes.isEmpty());
    }
}
