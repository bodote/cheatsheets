## Optional
### ifPresent(<lambda>)
```java
   Optional<String> opt = Optional.of("baeldung");
    opt.ifPresent(name -> System.out.println(name.length()));
```
### orElseGet(<lambda>)
```java
 String nullName = null;
    String name = Optional.ofNullable(nullName).orElseGet(() -> "john");
    assertEquals("john", name);
``` 
```java
String result = Optional.ofNullable(text).orElseGet(this::getMyDefault);
``` 
### throw Exception 
```java
 String nullName = null;
 // throws a NoSuchElementException
    String name = Optional.ofNullable(nullName).orElseThrow();
```
### filter and map
```java
Optional.ofNullable(modem2)
       .map(Modem::getPrice)
       .filter(p -> p >= 10)
       .filter(p -> p <= 15)
       .isPresent(price->doSomeThing(price));
``` 
```java
    List<String> companyNames = Arrays.asList(
      "paypal", "oracle", "", "microsoft", "", "apple");
    Optional<List<String>> listOptional = Optional.of(companyNames);

    int size = listOptional
      .map(List::size)
      .orElse(0);
``` 
