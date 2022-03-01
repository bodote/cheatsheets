# JPA vs Spring Data vs JDBC
## JDBC minimal Beispiel Daten aus DB lesen
Gegeben ist eine Tabelle `COFFEES` mit Caffeesorten und deren Preis in deiner SQL - Datebank deiner Wahl (Mysql, Postgresql, H2, ...).

| NAME  | PRICE |
| ------------- | ------------- |
| Jakobs Krönung  | 9  |
| Segafredo  | 11  |

- Wie kann man alle Einträge der Tabelle mit JPA auslesen?
## JPA minimal Beispiel, gleiche Vorgaben und Frage  wie oben.
(wir nehmen an , dass alle für JPA nötigen Parameter URL/port des DB-Servers etc.  in der `persistence.xml` korrekt gesetzt sind )

- Wie kann man alle Einträge der Tabelle mit JDBC auslesen?

- Welche 2 Annotations an einer Classe, deren Instanzen in einer Datebank gespeichert werden sollen sind mindestens für diese Klasse, die Caffee - Namen und Prei enthält  erforderlich? 


## SpringData minimal Beispiel, Vorgaben wie oben
(wir nehmen an , dass alle für SpringData nötigen Parameter URL/port des DB-Servers etc.  in der `application.properties`  Datei korrekt gesetzt sind )

- Wie kann man alle Einträge der Tabelle mit Spring Data auslesen?


## Vergleiche : 
was sind die Vorteile und Nachteile der 3 Arten auf DB-Daten zuzugreifen? 
z.B. ( aber nicht nur ) hinsichtlich diese Punkte:
- Transaction management ?
- Verständlichkeit der API
- Entwicklungsgeschwindigkeit für den SW-Entwickler je API
- Entwicklungsaufwand für DB-to-Object Mapping
- ...?

## Zusatzfrage: 
  - Wozu dient  eigentlich 'Spring Data REST' ?
  - wie kann man Spring Data oder JPA Debuggen und sich die SQL - Statements die Spring Data oder JPA generiert auf der Console ausgeben lassen ? (Hinweis: per application.properties )