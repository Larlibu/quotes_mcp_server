# MCP Project – Zitate-Sammlung

In diesem Projekt entwickelst du einen MCP-Server für eine persönliche Zitate-Sammlung. Der Server soll es ermöglichen, inspirierende Zitate zu sammeln, zu kategorisieren und gezielt oder zufällig wiederzufinden.

## Rahmenbedingungen

- **AI-First:** Nutze den AI-First Ansatz und verwende AI-Tools aktiv zur Unterstützung in allen Phasen der Entwicklung.
- **Persistierung:** Die Daten müssen in einer JSON-Datei gespeichert werden.
- **Sprachwahl:** Die Implementierungssprache ist frei wählbar.
- **Technische Robustheit:** Die Suche muss case-insensitive funktionieren und der Server muss Fehler robust behandeln.

## Teilaufgaben

### 1. Subtask 1 – Planung und Konzeption

Beginne mit der Auswahl einer geeigneten Implementierungssprache und entscheide dich für die Architektur des MCP-Servers.

- **Datenstruktur:** Definiere die JSON-Datenstruktur für die Persistierung der Zitate.

### 2. Subtask 2 – Datenmodell und Grundfunktionen

Erstelle das Datenmodell und implementiere die Basisfunktionen für die Speicherverwaltung.

- **Datenmodell:** Ein Zitat muss folgende Felder enthalten:
  - Eindeutige ID
  - Text des Zitats
  - Erstellungsdatum
  - Tags:
    - Autoren-Tag (wer hat es gesagt)
    - Themen-Tag (z. B. „Motivation", „Liebe")
    - Sprachen-Tag (z. B. „deutsch", „englisch")
- **Speicherung:** Implementiere die Funktion, um einzelne Zitate zu speichern.
- **Batch-Import:** Ermögliche den Import mehrerer Zitate auf einmal.

Beispiel für das Datenformat:

```json
{
  "text": "Be yourself; everyone else is already taken.",
  "autor_tag": "Oscar Wilde",
  "themen_tag": "Authentizität",
  "sprachen_tag": "englisch"
}
```

### 3. Subtask 3 – Suchfunktionen und Logik

Entwickle die Logik für das Wiederfinden und Verwalten der Zitate.

- **Suche:** Implementiere eine Suchfunktion, die Zitate anhand aller drei Tag-Typen oder dem Text selbst findet (case-insensitive).
- **Zufalls-Zitat:** Erstelle eine Funktion, die ein zufälliges Zitat zurückgibt. Diese soll optional nach bestimmten Tags gefiltert werden können.
- **Löschen:** Implementiere die Möglichkeit, Zitate wieder zu entfernen.

### 4. Subtask 4 – MCP-Server Integration

Integriere deine Logik in das MCP-Framework, um den Server nutzbar zu machen.

- **Integration:** Verbinde die implementierten Funktionen (Tools) mit der MCP-Schnittstelle.
- **Test:** Teste die Funktionalität und die Robustheit des Servers mit Claude oder einem entsprechenden MCP-Client.
