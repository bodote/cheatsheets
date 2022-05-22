
# SpringData vs JPA vs JDBC Lösungen
## JDBC minimal Beispiel Daten aus DB lesen
Gegeben ist eine Tabelle `COFFEES` mit Caffeesorten und deren Preis in deiner SQL - Datebank deiner Wahl (Mysql, Postgresql, H2, ...).

| NAME  | PRICE |
| ------------- | ------------- |
| Jakobs Krönung  | 9  |
| Segafredo  | 11  |

- Wie kann man alle Einträge der Tabelle mit JDBC auslesen?

- **Lösung**
```java
Class.forName("com.mysql.jdbc.Driver").newInstance();
String url = "jdbc:mysql://localhost/coffeebreak";
Connection  conn = DriverManager.getConnection(url, "username", "password");
String query = "SELECT NAME, PRICE FROM COFFEES";
Statement st = conn.createStatement();
      ResultSet rs = st.executeQuery(query);
      while (rs.next()){
          String s = rs.getString("NAME");
          float n = rs.getFloat("PRICE");
          log.info(s + "   " + n);
      }
``` 

## JPA minimal Beispiel, gleiche Vorgaben und Frage  wie oben.
(wir nehmen an , dass alle für JPA nötigen Parameter URL/port des DB-Servers etc.  in der `persistence.xml` korrekt gesetzt sind )

- **Lösung**
```java
@Entity
public class Coffee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String price;
}
//---------
EntityManagerFactory factory = Persistence.createEntityManagerFactory('coffeebreak');
EntityManager em = factory.createEntityManager();
Query q = em.createQuery("select c from COFFEES c");
List<Coffee> coffeeList = q.getResultList();
        for (Todo coffee : coffeeList) {
            log.info(coffee);
        }
```


- Welche 2 Annotations an einer Classe, deren Instanzen in einer Datebank gespeichert werden sollen sind mindestens für diese Klasse, die Caffee - Namen und Prei enthält  erforderlich? 


## SpringData minimal Beispiel, Vorgaben wie oben
(wir nehmen an , dass alle für SpringData nötigen Parameter URL/port des DB-Servers etc.  in der `application.properties`  Datei korrekt gesetzt sind )
- **Lösung:**
```java
@Entity
public class Coffee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String price;
}
//---------

public interface CoffeeRepository extends CrudRepository<Coffee, Long> {
 // Iterable<T>	findAll() per spring magic : allready exists in Base Class!
}
//---------

@Autowired
CoffeeRepository coffeeRepository;

for (Coffee coffee : coffeeRepository.findAll()) {
        log.info(coffee.toString());
      }
```
## Spring Data erweitertes Beispiel
- wie kann man in Spring Data trotzdem spezielle Abfragen in sql-(ähnlicher) Syntax (JPQL)  machen ? hier z.B. alle coffees die billiger als 10 Euro sind
- **Lösung**: 
```java

public interface CoffeeRepository extends CrudRepository<Coffee, Long> {
 // Iterable<T>	findAll() per spring magic : allready exists in BaseClass
  @Query("select c from COFFEES c WHERE c.price <= 10")
  Collection<Coffee> findCheapCoffee();
}
``` 

- wie geht das mit nativem SQL ?
```java
@Query(
  value = "SELECT NAME, PRICE FROM COFFEES c  WHERE c.price < 10", 
  nativeQuery = true)
Collection<Coffee> findCheapCoffee();
```

## Vergleiche : 
was sind die Vorteile und Nachteile der 3 Arten auf DB-Daten zuzugreifen? 
z.B. ( aber nicht nur ) hinsichtlich diese Punkte:
- Transaction management ?
- Verständlichkeit der API
- Entwicklungsgeschwindigkeit für den SW-Entwickler je API
- Entwicklungsaufwand für DB-to-Object Mapping
- in welcher Beziehung stehen Spring Data, Spring Data JPA, CrudRepository, MongoRepository, JPA und JDBC? Graphische Lösung! 

- **Lösung**
z.B. : 
  - SpringData per Annotation ; JDBC , JPA : explizit per java-methodenaufruf
  - JDBC am einfachsten ? 
  - Geht schneller mit SpringData ? 
  - OR-Mapping mit Spring und JPA automatisch, mit JDBC explizit einzeln je spalte
  - zusätzlicher Vorteil SpringData: einheitliches API für ganz unterschiedlichen DB-Typen: 

![Spring Data][Spring Data]



  ## Zusatzfragen: 
  Wozu ist  eigentlich 'Spring Data REST' ?
- **Lösung**:
  Rest interface für CRUDRepositorys per Annotations: "Spring Data REST builds on top of the Spring Data repositories and automatically exports those as REST resources. It leverages hypermedia to let clients automatically find functionality exposed by the repositories and integrate these resources into related hypermedia-based functionality." aus : https://docs.spring.io/spring-data/rest/docs/current/reference/html/#intro-chapter 

- wie kann man Spring Data oder JPA Debuggen und sich die SQL - Statements die Spring Data oder JPA generiert auf der Console ausgeben lassen ? (Hinweis: per application.properties )
- **Lösung**:
```
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```  
---
[Spring Data]: https://programmer.ink/images/think/2b23fb3d4796fab9ef024dc9d6c9e0f8.jpg "Spring Data"