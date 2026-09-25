package de.larlibu.quotes.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.larlibu.quotes.model.Quote;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/**
 * Dateibasierte Persistenz fuer Zitate.
 * Speichert die Inhalte als JSON-Datei und schreibt atomar ueber eine temporaere Datei.
 */
@Repository
public class QuoteRepository {

    private static final String FILE_VERSION = "1.0";

    private final ObjectMapper objectMapper;
    private final Path quotesFilePath;

    /**
     * Initialisiert das Repository.
     *
     * @param objectMapper Basis-Mapper, wird intern kopiert und konfiguriert
     * @param quotesFilePath Pfad zur JSON-Datei mit den Zitaten
     */
    public QuoteRepository(ObjectMapper objectMapper, @Value("${quotes.file.path}") String quotesFilePath) {
        this.objectMapper = objectMapper.copy();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.quotesFilePath = Path.of(quotesFilePath);
    }

    /**
     * Fuegt ein neues Zitat hinzu.
     *
     * @param quote zu speicherndes Zitat
     * @throws IOException wenn Lesen oder Schreiben der Datei fehlschlaegt
     */
    public synchronized void addQuote(Quote quote) throws IOException {
        List<Quote> quotes = loadFromFile();
        quotes.add(quote);
        saveToFile(quotes);
    }

    /**
     * Laedt alle gespeicherten Zitate.
     *
     * @return Liste aller Zitate
     * @throws IOException wenn das Lesen der Datei fehlschlaegt
     */
    public synchronized List<Quote> getAllQuotes() throws IOException {
        return loadFromFile();
    }

    /**
     * Loescht ein Zitat anhand der ID.
     *
     * @param id eindeutige Zitat-ID
     * @return {@code true}, wenn ein Eintrag geloescht wurde
     * @throws IOException wenn Lesen oder Schreiben der Datei fehlschlaegt
     */
    public synchronized boolean deleteById(String id) throws IOException {
        List<Quote> quotes = loadFromFile();
        boolean removed = quotes.removeIf(existing -> id.equals(existing.getId()));
        if (removed) {
            saveToFile(quotes);
        }
        return removed;
    }

    private void saveToFile(List<Quote> quotes) throws IOException {
        Path parent = quotesFilePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        QuoteFileWrapper wrapper = new QuoteFileWrapper(FILE_VERSION, quotes);
        String jsonContent = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(wrapper);

        Path tempFile = Files.createTempFile(parent, "quotes-", ".tmp");
        Files.writeString(
                tempFile,
                jsonContent,
                StandardCharsets.UTF_8,
                StandardOpenOption.TRUNCATE_EXISTING);

        try {
            Files.move(
                    tempFile,
                    quotesFilePath,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException ignored) {
            // Manche Dateisysteme (z.B. FAT32 oder ein Move ueber Volume-Grenzen) unterstuetzen
            // keinen atomaren Move. Nicht-atomarer Fallback, um die Datei trotzdem zu ersetzen.
            Files.move(tempFile, quotesFilePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private List<Quote> loadFromFile() throws IOException {
        if (!Files.exists(quotesFilePath)) {
            return new ArrayList<>();
        }

        String jsonContent = Files.readString(quotesFilePath, StandardCharsets.UTF_8);
        if (jsonContent.isBlank()) {
            return new ArrayList<>();
        }

        QuoteFileWrapper wrapper = objectMapper.readValue(jsonContent, QuoteFileWrapper.class);
        if (wrapper == null || wrapper.getQuotes() == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(wrapper.getQuotes());
    }

    private static class QuoteFileWrapper {

        private String version;
        private List<Quote> quotes;

        public QuoteFileWrapper() {
        }

        private QuoteFileWrapper(String version, List<Quote> quotes) {
            this.version = version;
            this.quotes = quotes;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public List<Quote> getQuotes() {
            return quotes;
        }

        public void setQuotes(List<Quote> quotes) {
            this.quotes = quotes;
        }
    }
}
