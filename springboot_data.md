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

# Entities (und Lombok) 
...werden wieder durch Annoations definiert :
* `@Entity`, mandatory
* `@Table`(mit argument `name=<name der Tabelle in der DB>`)
*  Lobok: `@Data` oder `@Getter` `@Setter` 
*  `@ID` für den primary key mandatory, dazu optional `@GeneratedValue` (meist `Auto`) 
*  `@Column` optional, wenn man die DB-Tabelle anders nennen will

