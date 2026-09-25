package de.larlibu.quotes.service;

import de.larlibu.quotes.model.Quote;
import java.util.List;
import java.util.Set;

/**
 * Service-API fuer die Verwaltung von Zitaten.
 */
public interface QuoteService {

    /**
     * Speichert ein einzelnes Zitat.
     *
     * @param quote zu speicherndes Zitat
     * @return gespeichertes Zitat inklusive generierter Metadaten
     */
    Quote save(Quote quote);

    /**
     * Sucht Zitate nach Text, Autor, Thema oder Sprache.
     *
     * @param query Suchbegriff
     * @return gefilterte Trefferliste
     */
    List<Quote> searchQuotes(String query);

    /**
     * Liefert ein zufaelliges Zitat, optional gefiltert nach Tags.
     *
     * @param autorTag optionaler Autor-Filter
     * @param themenTag optionaler Themen-Filter
     * @param sprachenTag optionaler Sprach-Filter
     * @return zufaellig ausgewaehltes Zitat
     */
    Quote getRandomQuote(String autorTag, String themenTag, String sprachenTag);

    /**
     * Loescht ein Zitat anhand der ID.
     *
     * @param id eindeutige Zitat-ID
     * @return {@code true}, wenn ein Zitat geloescht wurde, sonst {@code false}
     */
    boolean deleteQuote(String id);

    /**
     * Ermittelt alle vorhandenen Kategorien/Themen.
     *
     * @return Menge aller nicht-leeren Themen-Tags
     */
    Set<String> getAllCategories();

    /**
     * Importiert mehrere Zitate nacheinander.
     *
     * @param quotes Liste zu importierender Zitate
     * @return Anzahl erfolgreich importierter Zitate
     */
    int batchImport(List<Quote> quotes);
}
