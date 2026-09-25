package de.larlibu.quotes.service;

import de.larlibu.quotes.model.Quote;
import de.larlibu.quotes.repository.QuoteRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Standard-Implementierung von {@link QuoteService} auf Basis des {@link QuoteRepository}.
 */
@Service
public class QuoteServiceImpl implements QuoteService {

    private static final Logger log = LoggerFactory.getLogger(QuoteServiceImpl.class);

    private final QuoteRepository repository;
    private final Random random;

    /**
     * Erzeugt den Service mit Repository-Dependency.
     *
     * @param repository Persistenzzugriff fuer Zitate
     */
    public QuoteServiceImpl(QuoteRepository repository) {
        this.repository = repository;
        this.random = new Random();
    }

    @Override
    public Quote save(Quote quote) {
        quote.setId(UUID.randomUUID().toString());
        quote.setErstellungsdatum(LocalDateTime.now());

        try {
            repository.addQuote(quote);
            return quote;
        } catch (IOException e) {
            log.error("Fehler beim Speichern des Zitats.", e);
            throw new IllegalStateException("Zitat konnte nicht gespeichert werden.", e);
        }
    }

    @Override
    public List<Quote> searchQuotes(String query) {
        try {
            List<Quote> allQuotes = repository.getAllQuotes();
            if (query == null || query.isBlank()) {
                return allQuotes;
            }

            String normalizedQuery = query.toLowerCase(Locale.ROOT);
            return allQuotes.stream()
                    .filter(quote -> containsIgnoreCase(quote.getText(), normalizedQuery)
                            || containsIgnoreCase(quote.getAutorTag(), normalizedQuery)
                            || containsIgnoreCase(quote.getThemenTag(), normalizedQuery)
                            || containsIgnoreCase(quote.getSprachenTag(), normalizedQuery))
                    .toList();
        } catch (IOException e) {
            log.error("Fehler bei der Suche nach Zitaten.", e);
            throw new IllegalStateException("Suche nach Zitaten fehlgeschlagen.", e);
        }
    }

    @Override
    public Quote getRandomQuote(String autorTag, String themenTag, String sprachenTag) {
        try {
            List<Quote> filteredQuotes = repository.getAllQuotes().stream()
                    .filter(quote -> matchesOptionalTag(quote.getAutorTag(), autorTag))
                    .filter(quote -> matchesOptionalTag(quote.getThemenTag(), themenTag))
                    .filter(quote -> matchesOptionalTag(quote.getSprachenTag(), sprachenTag))
                    .toList();

            if (filteredQuotes.isEmpty()) {
                throw new IllegalStateException("Kein Zitat fuer die angegebenen Filter gefunden.");
            }

            int randomIndex = random.nextInt(filteredQuotes.size());
            return filteredQuotes.get(randomIndex);
        } catch (IOException e) {
            log.error("Fehler beim Laden eines Zufallszitats.", e);
            throw new IllegalStateException("Zufallszitat konnte nicht geladen werden.", e);
        }
    }

    @Override
    public boolean deleteQuote(String id) {
        try {
            return repository.deleteById(id);
        } catch (IOException e) {
            log.error("Fehler beim Loeschen des Zitats mit ID {}.", id, e);
            throw new IllegalStateException("Zitat konnte nicht geloescht werden.", e);
        }
    }

    @Override
    public Set<String> getAllCategories() {
        try {
            return repository.getAllQuotes().stream()
                    .map(Quote::getThemenTag)
                    .filter(Objects::nonNull)
                    .filter(tag -> !tag.isBlank())
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            log.error("Fehler beim Laden der Kategorien.", e);
            throw new IllegalStateException("Kategorien konnten nicht geladen werden.", e);
        }
    }

    @Override
    public int batchImport(List<Quote> quotes) {
        int imported = 0;
        for (Quote quote : quotes) {
            save(quote);
            imported++;
        }
        return imported;
    }

    private boolean containsIgnoreCase(String value, String normalizedQuery) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(normalizedQuery);
    }

    private boolean matchesOptionalTag(String quoteTag, String filterTag) {
        if (filterTag == null || filterTag.isBlank()) {
            return true;
        }
        return quoteTag != null && quoteTag.equalsIgnoreCase(filterTag);
    }
}
