package de.larlibu.quotes.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

/**
 * Datenmodell fuer ein Zitat inklusive Metadaten.
 */
public class Quote {

    private String id;
    private String text;

    @JsonProperty("autor_tag")
    private String autorTag;

    @JsonProperty("themen_tag")
    private String themenTag;

    @JsonProperty("sprachen_tag")
    private String sprachenTag;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime erstellungsdatum;

    /**
     * No-Args-Konstruktor fuer Jackson-Deserialisierung.
     */
    public Quote() {
    }

    /**
     * Vollstaendiger Konstruktor.
     *
     * @param id eindeutige ID
     * @param text Zitattext
     * @param autorTag Autor
     * @param themenTag Thema/Kategorie
     * @param sprachenTag Sprache
     * @param erstellungsdatum Erstellungszeitpunkt
     */
    public Quote(String id, String text, String autorTag, String themenTag, String sprachenTag,
                 LocalDateTime erstellungsdatum) {
        this.id = id;
        this.text = text;
        this.autorTag = autorTag;
        this.themenTag = themenTag;
        this.sprachenTag = sprachenTag;
        this.erstellungsdatum = erstellungsdatum;
    }

    /**
     * Konstruktor fuer neue Zitate ohne ID und Zeitstempel.
     *
     * @param text Zitattext
     * @param autorTag Autor
     * @param themenTag Thema/Kategorie
     * @param sprachenTag Sprache
     */
    public Quote(String text, String autorTag, String themenTag, String sprachenTag) {
        this.text = text;
        this.autorTag = autorTag;
        this.themenTag = themenTag;
        this.sprachenTag = sprachenTag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAutorTag() {
        return autorTag;
    }

    public void setAutorTag(String autorTag) {
        this.autorTag = autorTag;
    }

    public String getThemenTag() {
        return themenTag;
    }

    public void setThemenTag(String themenTag) {
        this.themenTag = themenTag;
    }

    public String getSprachenTag() {
        return sprachenTag;
    }

    public void setSprachenTag(String sprachenTag) {
        this.sprachenTag = sprachenTag;
    }

    public LocalDateTime getErstellungsdatum() {
        return erstellungsdatum;
    }

    public void setErstellungsdatum(LocalDateTime erstellungsdatum) {
        this.erstellungsdatum = erstellungsdatum;
    }
}
