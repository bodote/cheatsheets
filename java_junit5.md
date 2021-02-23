# Exceptions
```java
Exception exception = assertThrows(NumberFormatException.class, () -> {
        Integer.parseInt("1a");
    });
```

# matchers
 * entweder `org.hamcrest.Matcher.*` oder  
 * die `org.assertj.core.api.Assertions` , die eigentlich mehr **fluent** sind.
 * `org.junit.jupiter.api.Assertions.*`;