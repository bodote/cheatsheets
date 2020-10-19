# JPA/Hibernate  

## Debug:
```
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

## Cascading OneToMany
* JPA translates entity state transitions to database DML statements. Because it’s common to operate on entity graphs, 
JPA allows us to propagate entity state changes from **Parents to Child** entities.
This behavior is configured through the CascadeType mappings.
```java
@OneToMany(cascade = CascadeType.ALL, mappedBy = "<the other class property>")
```
* [jpa-and-hibernate-cascade-types](https://vladmihalcea.com/a-beginners-guide-to-jpa-and-hibernate-cascade-types/)
## ManyToOne mit Join
```Java
@ManyToOne
@JoinColumn(name = "<foreign key in dieser Tabelle>", nullable = false)
//@JoinColumn  'name' ist der foreign key in dieser Tabelle, der auf  "id" in anderen Tabelle zeigt
private TheOtherClass otherObj;
```

## persist vs merge
### persist
* Insert a new register to the database
* Attach the object to the entity manager.

### merge
* Find an attached object with the same id and update it.
* If exists update and return the already attached object.
* If doesn't exist insert the new register to the database.

### persist() efficiency:
* It could be more efficient for inserting a new register to a database than merge().
*  It doesn't duplicates the original object.

### persist() semantics:
* It makes sure that you are inserting and not updating by mistake.

## Primary Key.
```java
@Id
@GeneratedValue(strategy = GenerationType.AUTO)
//GenerationType.AUTO besser weil GenerationType.IDENTITY=bad optimization in hibernate 
//
@Column(name = "id")
private Long id;
```
[Details hier](https://thorben-janssen.com/jpa-generate-primary-keys/)

# JPA und REST 
## Basics
Im grunde reicht es das Interface `JpaRepository` zu extenden:
```java
public interface MeineEntityKlasseRepository extends JpaRepository<MeineEntityKlasse,Long> {}
```
wobei der primary key hier ein im Beispiel ein `Long` ist.
## Json und api-url-link ändern : 
```java
@RepositoryRestResource(collectionResourceRel = "<JsonName>", path = "<api-url-pfad name>")
public interface MeineEntityKlasseRepository extends JpaRepository<MeineEntityKlasse,Long> {}
```
## findBy ergänzen
* das Interface wird einfach mit einer Method-Signature ergänzt: 
`public Page<MyActualClass> findByMySelectionID (@RequestParameter("id") Long id, Pageable pageble);`
* der alles andere ist Springboot - Magic  und führt zu folgendem SQL: 
`SELECT * FROM myclass where myselection_id=?`
* `http://localhost:8080/api/myactualclass/search` sollte dann die `findBymyselectionId`anzeigen und `http://localhost:8080/api/myactualclass/search/findByMyselectionId?id=1` müsste dann die gefilterten Ergebnisse liefern 

## Config ändern
Änderung für "MyClass", read only:

```Java
@Configuration
public class RestConfig implements RepositoryRestConfigurer {
    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration restConfig) {
       HttpMethod[] theUnsupportedActions = {HttpMethod.PUT,HttpMethod.POST,HttpMethod.DELETE};
       restConfig.getExposureConfiguration()
          .forDomainType(<MyClass>.class)
          .withItemExposure((metadata,httpMethods)-> httpMethods.disable(theUnsupportedActions))
          .withCollectionExposure((metadata,httpMethods)-> httpMethods.disable(theUnsupportedActions));
    }
}
```
GGf. braucht man noch ein EntityManager Instanz für die Configänderung, dann ergänze: 
eine Constructor der den EnitiyManager per @Autowired injektet.
```Java
....
private EntityManager entityManager;
@Autowired
public RestConfig(EntityManager theEntityManager){
   this.entityManager= theEntityManager;
}
...
```
z.B. um vom EntityManager alle Typen, die er verwaltet zu lesen:
```Java
...
 for (EntityType<?> entity : entityManager.getMetamodel().getEntities()) {
         logger.debug("Java Type:" + entity.getJavaType().toString());
         restConfig.exposeIdsFor(entity.getJavaType())
    }
...
``` 
das kann man verwenden um die dann einzeln zu Konfigurieren, hier z.B. um die **Id in der Json Antwort** mit einzuschließen (was eben defaultmäßig nicht der Fall ist):
`config.exposeIdsFor(User.class)`  wenn User.class eine von den Entitys des EntityMangers ist;

siehe auch https://docs.spring.io/spring-data/rest/docs/current/reference/html/#getting-started.changing-base-uri

## Spring-Rest default args
JpaRepository<my-stuff,Long> hat beim Zugriff über `http://whatever/api/my-stuff` schon eingebaute Argumente, 
* z.b. `?size=<number>`  (default ist  *20*)
* also `http://whatever/api/my-stuf?size=100` liefert dann 100 Ergebnisse.



# Entities (und Lombok) 
...werden wieder durch Annoations definiert :
* `@Entity`, mandatory
* `@Table`(mit argument `name=<name der Tabelle in der DB>`)
*  Lobok: `@Data` oder `@Getter` `@Setter` 
*  `@ID` für den primary key mandatory, dazu optional `@GeneratedValue` (meist `Auto`) 
*  `@Column` optional, wenn man die DB-Spalte anders nennen will

# REST 
## ERROR: Access-Control-Allow-Origin
`@CrossOrigin("http://localhost:4200")` dem  `inferface` hinzufügen , welches  `JpaRepository<>` extended

