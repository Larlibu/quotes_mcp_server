package de.larlibu.quotes.tools;

import de.larlibu.quotes.model.Quote;
import de.larlibu.quotes.service.QuoteService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * MCP-Tool-Endpunkte fuer die Zitate-Verwaltung.
 */
@Component
public class QuoteTool {

    private static final Logger log = LoggerFactory.getLogger(QuoteTool.class);

    private final QuoteService quoteService;

    /**
     * Erzeugt das Tool mit Service-Dependency.
     *
     * @param quoteService Service fuer Zitate
     */
    public QuoteTool(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    /**
     * Legt ein neues Zitat an.
     *
     * @param text Zitattext
     * @param autorTag Autor-Name
     * @param themenTag Kategorie/Thema
     * @param sprachenTag Sprache
     * @return gespeichertes Zitat
     */
    @Tool(description = "Fuegt ein neues Zitat zur Sammlung hinzu")
    public Quote addQuote(
            @ToolParam(description = "Der Text des Zitats") String text,
            @ToolParam(description = "Name des Autors") String autorTag,
            @ToolParam(description = "Thema oder Kategorie (z.B. Motivation, Liebe)") String themenTag,
            @ToolParam(description = "Sprache des Zitats (z.B. deutsch, englisch)") String sprachenTag) {
        validateRequired(text, "text");
        validateRequired(autorTag, "autorTag");
        validateRequired(themenTag, "themenTag");
        validateRequired(sprachenTag, "sprachenTag");

        log.info("Tool-Aufruf addQuote: autorTag='{}', themenTag='{}', sprachenTag='{}'.",
                autorTag, themenTag, sprachenTag);

        try {
            return quoteService.save(new Quote(text, autorTag, themenTag, sprachenTag));
        } catch (RuntimeException e) {
            log.error("Fehler in addQuote.", e);
            throw new IllegalStateException("Zitat konnte nicht hinzugefuegt werden: " + e.getMessage(), e);
        }
    }

    /**
     * Importiert mehrere Zitate in einem Aufruf.
     *
     * @param quotes Liste der zu importierenden Zitate
     * @return Anzahl importierter Zitate
     */
    @Tool(description = "Importiert mehrere Zitate auf einmal")
    public int batchImportQuotes(@ToolParam(description = "Liste von Zitaten zum Importieren") List<Quote> quotes) {
        if (quotes == null || quotes.isEmpty()) {
            throw new IllegalArgumentException("quotes darf nicht leer sein.");
        }

        log.info("Tool-Aufruf batchImportQuotes: {} Zitate.", quotes.size());

        try {
            return quoteService.batchImport(quotes);
        } catch (RuntimeException e) {
            log.error("Fehler in batchImportQuotes.", e);
            throw new IllegalStateException("Batch-Import fehlgeschlagen: " + e.getMessage(), e);
        }
    }

    /**
     * Sucht Zitate ueber Freitext.
     *
     * @param query Suchbegriff
     * @return Trefferliste
     */
    @Tool(description = "Sucht Zitate anhand von Text oder Tags (case-insensitive)")
    public List<Quote> searchQuotes(
            @ToolParam(description = "Suchbegriff fuer Text, Autor, Thema oder Sprache") String query) {
        validateRequired(query, "query");
        log.info("Tool-Aufruf searchQuotes: query='{}'.", query);

        try {
            return quoteService.searchQuotes(query);
        } catch (RuntimeException e) {
            log.error("Fehler in searchQuotes.", e);
            throw new IllegalStateException("Suche fehlgeschlagen: " + e.getMessage(), e);
        }
    }

    /**
     * Liefert ein zufaelliges Zitat mit optionalen Filtern.
     *
     * @param autorTag optionaler Autor-Filter
     * @param themenTag optionaler Themen-Filter
     * @param sprachenTag optionaler Sprach-Filter
     * @return ein passendes Zufallszitat
     */
    @Tool(description = "Gibt ein zufaelliges Zitat zurueck, optional gefiltert nach Tags")
    public Quote getRandomQuote(
            @ToolParam(description = "Filter nach Autor (optional)", required = false) String autorTag,
            @ToolParam(description = "Filter nach Thema (optional)", required = false) String themenTag,
            @ToolParam(description = "Filter nach Sprache (optional)", required = false) String sprachenTag) {
        log.info("Tool-Aufruf getRandomQuote: autorTag='{}', themenTag='{}', sprachenTag='{}'.",
                autorTag, themenTag, sprachenTag);

        try {
            return quoteService.getRandomQuote(emptyToNull(autorTag), emptyToNull(themenTag), emptyToNull(sprachenTag));
        } catch (RuntimeException e) {
            log.error("Fehler in getRandomQuote.", e);
            throw new IllegalStateException("Zufallszitat konnte nicht ermittelt werden: " + e.getMessage(), e);
        }
    }

    /**
     * Loescht ein Zitat anhand der ID.
     *
     * @param id Zitat-ID
     * @return {@code true}, wenn geloescht wurde
     */
    @Tool(description = "Loescht ein Zitat anhand seiner ID")
    public boolean deleteQuote(@ToolParam(description = "Die eindeutige ID des zu loeschenden Zitats") String id) {
        validateRequired(id, "id");
        log.info("Tool-Aufruf deleteQuote: id='{}'.", id);

        try {
            return quoteService.deleteQuote(id);
        } catch (RuntimeException e) {
            log.error("Fehler in deleteQuote.", e);
            throw new IllegalStateException("Zitat konnte nicht geloescht werden: " + e.getMessage(), e);
        }
    }

    private void validateRequired(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " darf nicht leer sein.");
        }
    }

    private String emptyToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value;
    }
}
