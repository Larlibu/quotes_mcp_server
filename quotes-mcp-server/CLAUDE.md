# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Projektübersicht

MCP-Server (Model Context Protocol) für eine persönliche Zitate-Sammlung, implementiert mit Java 21 / Spring Boot und Spring AI. Der Server kommuniziert über **stdio** (kein HTTP) und wird typischerweise als lokaler MCP-Server in Claude Desktop eingebunden. Die Aufgabenstellung des Projekts steht in `../README.md` (Repo-Root).

## Build- und Testbefehle

Alle Befehle werden im Verzeichnis `quotes-mcp-server/` (dort liegt die `pom.xml`) ausgeführt.

```bash
# Bauen (inkl. Tests)
mvn clean package

# Nur Tests ausführen
mvn test

# Einzelne Testklasse ausführen
mvn test -Dtest=QuoteServiceTest

# Einzelne Testmethode ausführen
mvn test -Dtest=QuoteServiceTest#methodName

# Server starten (nach dem Build)
java -jar target/quotes-mcp-server-1.0.0.jar
```

Da der Server über stdio kommuniziert, ist ein direkter manueller Start im Terminal nicht sinnvoll zum Testen — Interaktion erfolgt über einen MCP-Client (Claude Desktop) oder über die Unit-/Integrationstests.

## Architektur

Klassischer Spring-Boot-Schichtenaufbau unter `src/main/java/de/larlibu/quotes/`:

- **`tools/QuoteTool`** – MCP-Tool-Endpunkte. Jede öffentliche Methode ist mit `@Tool` annotiert und wird über Spring AI als MCP-Tool exponiert (`addQuote`, `batchImportQuotes`, `searchQuotes`, `getRandomQuote`, `deleteQuote`). Diese Klasse validiert Eingaben und übersetzt Service-Exceptions in aussagekräftige Fehler für den MCP-Client — hier keine Geschäftslogik ergänzen.
- **`config/McpToolConfiguration`** – registriert `QuoteTool` als `ToolCallbackProvider` (`StaticToolCallbackProvider` via `ToolCallbacks.from(...)`), damit Spring AI die Tools dem stdio-MCP-Server bereitstellt. Neue Tool-Klassen müssen hier eingebunden werden.
- **`service/QuoteService`** (Interface) + **`QuoteServiceImpl`** – Geschäftslogik: ID-Generierung (`UUID`), Zeitstempel, case-insensitive Suche über Text/Autor/Thema/Sprache, gefilterte Zufallsauswahl, Kategorien-Ermittlung, Batch-Import. Wirft `IllegalStateException`/`IllegalArgumentException` bei Fehlern statt geprüfter Exceptions.
- **`repository/QuoteRepository`** – einzige Persistenzschicht. Liest/schreibt die komplette Zitate-Liste als JSON-Datei (Pfad aus `quotes.file.path`, siehe `application.yml`, Default: `${user.home}/.quotes-mcp/quotes.json`). Schreibvorgänge sind `synchronized` und erfolgen **atomar** über eine Temp-Datei + `Files.move` mit `ATOMIC_MOVE` (Fallback auf `REPLACE_EXISTING`, falls das Dateisystem keinen atomaren Move unterstützt). Die Datei enthält einen Wrapper mit `version`-Feld und `quotes`-Array.
- **`model/Quote`** – Datenmodell mit Jackson-`@JsonProperty`-Mapping auf deutsche Feldnamen (`autor_tag`, `themen_tag`, `sprachen_tag`) im JSON, während die Java-Felder camelCase sind. `erstellungsdatum` wird als `LocalDateTime` mit festem Pattern (`yyyy-MM-dd'T'HH:mm:ss'Z'`) (de-)serialisiert.

Datenfluss: `QuoteTool` (MCP-Schnittstelle, Validierung) → `QuoteService` (Geschäftslogik) → `QuoteRepository` (JSON-Persistenz, ganze Datei wird bei jedem Zugriff komplett neu geladen/geschrieben — es gibt keine Indizierung oder Teil-Updates).

Konfiguration (`src/main/resources/application.yml`): MCP-Server-Metadaten (Name, Version, Beschreibung) sowie Pfade für Zitate-Datei und Logs liegen standardmäßig unter `${user.home}/.quotes-mcp/`.

## Claude Desktop Integration

Nach dem Build wird der Server in `claude_desktop_config.json` registriert (siehe `README.md` im Server-Verzeichnis für die genaue JSON-Konfiguration) und Claude Desktop neu gestartet, damit die Tools verfügbar sind.
