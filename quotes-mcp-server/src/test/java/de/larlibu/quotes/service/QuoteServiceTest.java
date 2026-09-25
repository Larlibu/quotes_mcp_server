package de.larlibu.quotes.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.larlibu.quotes.model.Quote;
import de.larlibu.quotes.repository.QuoteRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class QuoteServiceTest {

    @Mock
    private QuoteRepository repository;

    private QuoteService service;

    @BeforeEach
    void setUp() {
        service = new QuoteServiceImpl(repository);
    }

    @Test
    void testSaveQuoteGeneratesIdAndTimestamp() throws IOException {
        Quote input = new Quote("Ein guter Gedanke", "Autor", "Motivation", "deutsch");

        Quote result = service.save(input);

        assertNotNull(result.getId());
        assertNotNull(result.getErstellungsdatum());
        UUID.fromString(result.getId());
        verify(repository).addQuote(any(Quote.class));
    }

    @Test
    void testSearchQuotesCaseInsensitive() throws IOException {
        Quote quote = new Quote(
                "Bleib dran.",
                "Unbekannt",
                "motivation",
                "deutsch");
        quote.setId("id-1");
        quote.setErstellungsdatum(LocalDateTime.now());

        when(repository.getAllQuotes()).thenReturn(List.of(quote));

        List<Quote> results = service.searchQuotes("MOTIVATION");

        assertEquals(1, results.size());
        assertEquals("motivation", results.get(0).getThemenTag());
    }

    @Test
    void testGetRandomQuoteWithFilter() throws IOException {
        Quote quote = new Quote("Text", "Autor", "Motivation", "deutsch");
        quote.setId("id-2");
        quote.setErstellungsdatum(LocalDateTime.now());

        Quote other = new Quote("Andere", "Autor2", "Philosophie", "deutsch");
        other.setId("id-3");
        other.setErstellungsdatum(LocalDateTime.now());

        when(repository.getAllQuotes()).thenReturn(List.of(quote, other));

        Quote random = service.getRandomQuote(null, "Motivation", null);

        assertNotNull(random);
        assertTrue(random.getThemenTag().equalsIgnoreCase("Motivation"));
    }
}
