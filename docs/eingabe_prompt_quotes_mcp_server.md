# Codex/IntelliJ AI Prompt: Zitate-MCP-Server Implementierung

## Projektübersicht

Erstelle ein vollständiges Spring Boot 3.3.x Projekt für einen MCP-Server (Model Context Protocol), der eine persönliche Zitate-Sammlung verwaltet. Das Projekt soll mit Java 21, Maven und Spring AI MCP Server Starter implementiert werden.

---

## 1. Projekt-Setup

### Maven pom.xml

Erstelle eine `pom.xml` mit folgenden Spezifikationen:

- **GroupId**: `de.larlibu`
- **ArtifactId**: `quotes-mcp-server`
- **Version**: `1.0.0`
- **Java Version**: 21
- **Spring Boot Version**: 3.3.5
- **Packaging**: jar

**Dependencies:**
```xml
<dependencies>
    <!-- Spring Boot Starter -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>

    <!-- Spring AI MCP Server Starter -->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-mcp-server</artifactId>
    </dependency>

    <!-- Jackson für JSON -->
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
    </dependency>

    <!-- Lombok (optional, für weniger Boilerplate) -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <!-- Testing -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-bom</artifactId>
            <version>1.0.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### Projektstruktur

```
quotes-mcp-server/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── de/larlibu/quotes/
│   │   │       ├── QuotesApplication.java
│   │   │       ├── model/
│   │   │       │   └── Quote.java
│   │   │       ├── repository/
│   │   │       │   └── QuoteRepository.java
│   │   │       ├── service/
│   │   │       │   └── QuoteService.java
│   │   │       └── tools/
│   │   │           └── QuoteTool.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── data/
│   │           └── quotes.json
│   └── test/
│       └── java/
│           └── de/larlibu/quotes/
│               ├── service/
│               │   └── QuoteServiceTest.java
│               └── repository/
│                   └── QuoteRepositoryTest.java
├── pom.xml
└── README.md
```

---

## 2. Datenmodell (Quote.java)

Erstelle eine Java-Klasse `Quote` im Package `de.larlibu.quotes.model`:

**Anforderungen:**
- Alle Felder als private Attribute
- Verwende Lombok `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor` (oder manuelle Getter/Setter)
- Jackson-Annotations für JSON-Serialisierung

**Felder:**
```java
private String id;                      // UUID als String
private String text;                    // Zitat-Text
private String autorTag;                // JSON: "autor_tag"
private String themenTag;               // JSON: "themen_tag"
private String sprachenTag;             // JSON: "sprachen_tag"
private LocalDateTime erstellungsdatum; // ISO-8601 Format
```

**Wichtig:**
- Verwende `@JsonProperty("autor_tag")` für Snake-Case JSON-Mapping
- Constructor ohne `id` und `erstellungsdatum` (werden automatisch generiert)

---

## 3. Repository (QuoteRepository.java)

Erstelle `QuoteRepository` im Package `de.larlibu.quotes.repository`:

**Verantwortung:** JSON-Datei lesen/schreiben

**Anforderungen:**
- `@Repository` Annotation
- `@Value("${quotes.file.path}")` für konfigurierbaren Dateipfad
- `ObjectMapper` für JSON-Serialisierung (autowired)
- UTF-8 Encoding explizit setzen

**Methoden:**
```java
public void addQuote(Quote quote) throws IOException
public List<Quote> getAllQuotes() throws IOException
public boolean deleteById(String id) throws IOException
private void saveToFile(List<Quote> quotes) throws IOException
private List<Quote> loadFromFile() throws IOException
```

**JSON-Struktur:**
```json
{
  "version": "1.0",
  "quotes": [
    {
      "id": "uuid",
      "text": "...",
      "autor_tag": "...",
      "themen_tag": "...",
      "sprachen_tag": "...",
      "erstellungsdatum": "2026-05-01T18:00:00Z"
    }
  ]
}
```

**Wichtig:**
- Atomic File Writes (tmp file + rename)
- Exception-Handling für I/O-Fehler
- Leere Liste zurückgeben, wenn Datei nicht existiert

---

## 4. Service (QuoteService.java)

Erstelle `QuoteService` im Package `de.larlibu.quotes.service`:

**Verantwortung:** Business Logic, UUID-Generierung, Timestamp, Filterlogik

**Anforderungen:**
- `@Service` Annotation
- Autowire `QuoteRepository`
- `@Slf4j` für Logging

**Methoden:**
```java
public Quote save(Quote quote)
public List<Quote> searchQuotes(String query)
public Quote getRandomQuote(String autorTag, String themenTag, String sprachenTag)
public boolean deleteQuote(String id)
public Set<String> getAllCategories()
public int batchImport(List<Quote> quotes)
```

**Wichtige Implementierungsdetails:**

1. **save():**
   - Generiere UUID mit `UUID.randomUUID().toString()`
   - Setze `erstellungsdatum` mit `LocalDateTime.now()`
   - Delegiere an Repository

2. **searchQuotes():**
   - Case-insensitive Suche: `query.toLowerCase()`
   - Filter über: text, autorTag, themenTag, sprachenTag
   - Stream API verwenden

3. **getRandomQuote():**
   - Lade alle Zitate
   - Filtere optional nach Tags
   - `Random.nextInt()` für Zufallsauswahl
   - Wirf Exception wenn keine Zitate gefunden

---

## 5. MCP Tools (QuoteTool.java)

Erstelle `QuoteTool` im Package `de.larlibu.quotes.tools`:

**Verantwortung:** MCP-Schnittstelle für KI-Agenten

**Anforderungen:**
- `@Component` Annotation
- Autowire `QuoteService`
- `@Tool` Annotation für jede öffentliche Methode
- `@ToolParam` für Parameter mit Beschreibungen

**Tools:**

```java
@Tool(description = "Fügt ein neues Zitat zur Sammlung hinzu")
public Quote addQuote(
    @ToolParam(description = "Der Text des Zitats") String text,
    @ToolParam(description = "Name des Autors") String autorTag,
    @ToolParam(description = "Thema oder Kategorie (z.B. Motivation, Liebe)") String themenTag,
    @ToolParam(description = "Sprache des Zitats (z.B. deutsch, englisch)") String sprachenTag
)

@Tool(description = "Importiert mehrere Zitate auf einmal")
public int batchImportQuotes(
    @ToolParam(description = "Liste von Zitaten zum Importieren") List<Quote> quotes
)

@Tool(description = "Sucht Zitate anhand von Text oder Tags (case-insensitive)")
public List<Quote> searchQuotes(
    @ToolParam(description = "Suchbegriff für Text, Autor, Thema oder Sprache") String query
)

@Tool(description = "Gibt ein zufälliges Zitat zurück, optional gefiltert nach Tags")
public Quote getRandomQuote(
    @ToolParam(description = "Filter nach Autor (optional)", required = false) String autorTag,
    @ToolParam(description = "Filter nach Thema (optional)", required = false) String themenTag,
    @ToolParam(description = "Filter nach Sprache (optional)", required = false) String sprachenTag
)

@Tool(description = "Löscht ein Zitat anhand seiner ID")
public boolean deleteQuote(
    @ToolParam(description = "Die eindeutige ID des zu löschenden Zitats") String id
)
```

**Wichtig:**
- Input-Validierung: Werfe `IllegalArgumentException` bei leeren Strings
- Logging aller Tool-Aufrufe
- Exception-Handling mit klaren Meldungen

---

## 6. Application Configuration

### application.yml

```yaml
spring:
  application:
    name: quotes-mcp-server

  ai:
    mcp:
      server:
        transport: stdio
        name: "Zitate-MCP-Server"
        version: "1.0.0"
        description: "MCP-Server für persönliche Zitate-Verwaltung"

quotes:
  file:
    path: ${user.home}/.quotes-mcp/quotes.json

logging:
  level:
    de.larlibu.quotes: INFO
    org.springframework.ai: DEBUG
```

### QuotesApplication.java

```java
package de.larlibu.quotes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QuotesApplication {
    public static void main(String[] args) {
        SpringApplication.run(QuotesApplication.class, args);
    }
}
```

---

## 7. Initiale quotes.json

Erstelle `src/main/resources/data/quotes.json`:

```json
{
  "version": "1.0",
  "quotes": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "text": "Be yourself; everyone else is already taken.",
      "autor_tag": "Oscar Wilde",
      "themen_tag": "Authentizität",
      "sprachen_tag": "englisch",
      "erstellungsdatum": "2026-05-01T18:00:00Z"
    },
    {
      "id": "550e8400-e29b-41d4-a716-446655440001",
      "text": "Der Weg ist das Ziel.",
      "autor_tag": "Konfuzius",
      "themen_tag": "Lebensweisheit",
      "sprachen_tag": "deutsch",
      "erstellungsdatum": "2026-05-01T18:00:00Z"
    }
  ]
}
```

---

## 8. Unit Tests

### QuoteServiceTest.java

```java
@ExtendWith(MockitoExtension.class)
class QuoteServiceTest {
    @Mock
    private QuoteRepository repository;

    @InjectMocks
    private QuoteService service;

    @Test
    void testSaveQuote_generatesIdAndTimestamp() {
        // Test: UUID und Timestamp werden generiert
    }

    @Test
    void testSearchQuotes_caseInsensitive() {
        // Test: Suche mit "MOTIVATION" findet "motivation"
    }

    @Test
    void testGetRandomQuote_withFilter() {
        // Test: Zufallszitat gefiltert nach themenTag
    }
}
```

### QuoteRepositoryTest.java

```java
@SpringBootTest
class QuoteRepositoryTest {
    @Autowired
    private QuoteRepository repository;

    @Test
    void testSaveAndLoad() throws IOException {
        // Test: Zitat speichern und wieder laden
    }

    @Test
    void testDeleteById() throws IOException {
        // Test: Zitat löschen
    }
}
```

---

## 9. Build und Deployment

### Build-Befehl:
```bash
mvn clean package
```

### Output:
```
target/quotes-mcp-server-1.0.0.jar
```

### Claude Desktop Konfiguration:

Pfad (macOS):
```
~/Library/Application Support/Claude/claude_desktop_config.json
```

Inhalt:
```json
{
  "mcpServers": {
    "quotes-server": {
      "command": "java",
      "args": [
        "-jar",
        "/absolute/path/to/quotes-mcp-server-1.0.0.jar"
      ]
    }
  }
}
```

---

## 10. README.md

Erstelle eine README.md:

```markdown
# Zitate-MCP-Server

MCP-Server für persönliche Zitate-Verwaltung mit Spring Boot.

## Features

- ✅ Zitate hinzufügen (einzeln oder Batch)
- ✅ Case-insensitive Suche
- ✅ Zufälliges Zitat (gefiltert oder ungefiltert)
- ✅ Kategorisierung nach Autor, Thema, Sprache
- ✅ Integration mit Claude Desktop

## Technologie

- Java 21
- Spring Boot 3.3.x
- Spring AI MCP Server
- JSON-Persistierung

## Build

\`\`\`bash
mvn clean package
\`\`\`

## Setup

1. Build JAR
2. Konfiguriere Claude Desktop (`claude_desktop_config.json`)
3. Starte Claude Desktop neu

## Verwendung

Natürlichsprachliche Anfragen:
- "Füge ein Zitat von Einstein hinzu"
- "Suche nach Motivations-Zitaten"
- "Gib mir ein zufälliges deutsches Zitat"
```

---

## Wichtige Hinweise für die Implementierung

1. **Fehlerbehandlung:**
   - Validiere alle Eingaben (nicht-leere Strings)
   - Werfe `IllegalArgumentException` mit klaren Meldungen
   - Logge alle Fehler

2. **Performance:**
   - Verwende Stream API für Filterung
   - Optional: In-Memory-Cache für häufige Zugriffe

3. **Sicherheit:**
   - UTF-8 Encoding explizit setzen
   - Atomic File Writes (tmp + rename)

4. **Code-Qualität:**
   - Google Java Style Guide
   - Sprechende Variablennamen
   - Javadoc für öffentliche Methoden

5. **Testing:**
   - Unit-Tests für Service
   - Integration-Tests für Repository
   - Mock externe Dependencies

---

## Erwartetes Ergebnis

Ein vollständig funktionsfähiger MCP-Server, der:
- ✅ Mit Claude Desktop kommuniziert
- ✅ Zitate in JSON speichert
- ✅ Alle 5 Tools bereitstellt
- ✅ Robust Fehler behandelt
- ✅ Unit-Tests hat

---

**Starte die Implementierung mit der pom.xml und arbeite dich durch die Schichten: Model → Repository → Service → Tools → Application.**
