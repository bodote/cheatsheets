## Aufgabe 1: JPA vs Spring Data vs JDBC
- JDBC minimal Beispiel Daten aus DB lesen
Gegeben ist eine Tabelle `COFFEE` mit Kaffeesorten und deren Preis in deiner SQL - Datebank deiner Wahl (Mysql, Postgresql, H2, ...).

| NAME  | PRICE |
| ------------- | ------------- |
| Jakobs Krönung  | 9  |
| Segafredo  | 11  |

- Wie kann man alle Einträge der Tabelle mit JPA auslesen?
## Aufgabe 2: JPA (ohne Spring Data) minimal Beispiel, gleiche Vorgaben und Frage wie oben
(wir nehmen an, dass alle für JPA nötigen Parameter URL/port des DB-Servers etc.  in der `persistence.xml` korrekt gesetzt sind)

- Wie kann man alle Einträge der Tabelle mit JDBC auslesen?

- Welche 2 Annotations an einer Klasse, deren Instanzen in einer Datebank gespeichert werden sollen sind mindestens für diese Klasse, die Kaffee-Namen und Preis enthält erforderlich? 


## Aufgabe 3: Spring Data JPA minimal Beispiel, gleiche Vorgaben und Frage wie oben
(wir nehmen an, dass alle für SpringData nötigen Parameter URL/port des DB-Servers etc.  in der `application.properties`  Datei korrekt gesetzt sind)

- Wie kann man alle Einträge der Tabelle mit Spring Data auslesen?

## Aufgabe 4:  Spring Data erweitertes Beispiel
- wie kann man in Spring Data JPA spezielle Abfragen in SQL-(ähnlicher) Syntax (JPQL)  machen ?  z.B. alle Coffees die billiger als 10 Euro sind, auslesen ?


## Aufgabe 5: Vergleiche JDBC, JPA und Spring Data JPA : 
was sind die Vorteile und Nachteile der 3 Arten auf DB-Daten zuzugreifen? z.B. (aber nicht nur) hinsichtlich diese Punkte:

- Transaction management
- Verständlichkeit der API
- Entwicklungsgeschwindigkeit für den SW-Entwickler je API
- Entwicklungsaufwand für DB-to-Object Mapping
- ...?

## Aufgabe 6:
- Wozu dient  eigentlich 'Spring Data REST'?
- Wie kann man Spring Data oder JPA Debuggen und sich die SQL - Statements die Spring Data oder JPA generiert auf der Console ausgeben lassen? (Hinweis: per application.properties )