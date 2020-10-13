# JPA/Hibernate Cascading 
* JPA translates entity state transitions to database DML statements. Because it’s common to operate on entity graphs, 
JPA allows us to propagate entity state changes from **Parents to Child** entities.
This behavior is configured through the CascadeType mappings.
```java
@OneToMany(cascade = CascadeType.ALL, mappedBy = "<the other class property>")
```
* [jpa-and-hibernate-cascade-types](https://vladmihalcea.com/a-beginners-guide-to-jpa-and-hibernate-cascade-types/)
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
