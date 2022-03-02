## Aufgabe 1:  JPA vs Spring Data vs JDBC
- JDBC minimal Beispiel Daten aus DB lesen
Gegeben ist eine Tabelle `COFFEES` mit Caffeesorten und deren Preis in einer SQL - Datebank deiner Wahl (Mysql, Postgresql, H2, ...).

| NAME  | PRICE |
| ------------- | ------------- |
| Jakobs Krönung  | 9  |
| Segafredo  | 11  |

- Wie kann man alle Einträge der Tabelle mit JPA auslesen?
## Aufgabe 2: JPA (ohne Spring Data) minimal Beispiel, gleiche Vorgaben und Frage  wie oben.
(wir nehmen an , dass alle für JPA nötigen Parameter URL/port des DB-Servers etc.  in der `persistence.xml` korrekt gesetzt sind )

- Wie kann man alle Einträge der Tabelle mit JDBC auslesen?

- Welche 2 Annotations an einer Classe, deren Instanzen in einer Datebank gespeichert werden sollen sind mindestens für diese Klasse, die Caffee - Namen und Prei enthält  erforderlich? 


## Aufgabe 3: Spring Data JPA minimal Beispiel, Vorgaben wie oben
(wir nehmen an , dass alle für SpringData nötigen Parameter URL/port des DB-Servers etc.  in der `application.properties`  Datei korrekt gesetzt sind )

- Wie kann man alle Einträge der Tabelle mit Spring Data auslesen?


## Aufgabe 4: Vergleiche JDBC, JPA und Spring Data JPA : 
was sind die Vorteile und Nachteile der 3 Arten auf DB-Daten zuzugreifen? 
z.B. ( aber nicht nur ) hinsichtlich diese Punkte:
- Transaction management ?
- Verständlichkeit der API
- Entwicklungsgeschwindigkeit für den SW-Entwickler je API
- Entwicklungsaufwand für DB-to-Object Mapping
- ...?

## Aufgabe 5: 
  - Wozu dient  eigentlich 'Spring Data REST' ?
  - wie kann man Spring Data oder JPA Debuggen und sich die SQL - Statements die Spring Data oder JPA generiert auf der Console ausgeben lassen ? (Hinweis: per application.properties )