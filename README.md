# Zitate-MCP-Server

MCP-Server fuer persoenliche Zitate-Verwaltung mit Spring Boot.

[Javadoc oeffnen](https://larlibu.github.io/quotes_mcp_server/)

## Features

- Zitate hinzufuegen (einzeln oder Batch)
- Case-insensitive Suche
- Zufaelliges Zitat (gefiltert oder ungefiltert)
- Kategorisierung nach Autor, Thema, Sprache
- Integration mit Claude Desktop

## Technologie

- Java 21
- Spring Boot 3.3.5
- Spring AI MCP Server
- JSON-Persistierung

## Build

```bash
mvn clean package
```

## Setup

1. JAR bauen.
2. Claude Desktop konfigurieren (`claude_desktop_config.json`).
3. Claude Desktop neu starten.

## Claude Desktop Konfiguration

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

## Verwendung

Natuerlichsprachliche Anfragen:

- "Fuege ein Zitat von Einstein hinzu"
- "Suche nach Motivations-Zitaten"
- "Gib mir ein zufaelliges deutsches Zitat"
