# Exceptions
```java
Exception exception = assertThrows(NumberFormatException.class, () -> {
        Integer.parseInt("1a");
    });
```

# matchers
 * entweder `org.hamcrest.Matcher.*` oder  
 * die `org.assertj.core.api.Assertions` , die eigentlich mehr **fluent** sind.
 * `org.junit.jupiter.api.Assertions.*` sind nicht so gut

 ## filter logmessages in a test case
 ### Variante B: logback TurboFilter
 ### Variante A: Appender Filter
 ```java
    @MockBean
        private Appender<ILoggingEvent> mockedAppender;

    @Captor
        private ArgumentCaptor<ch.qos.logback.classic.spi.LoggingEvent> loggingEventCaptor;

    @Test
    public void testTerribleCase() throws ModuleException {
        Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.addAppender(mockedAppender);
        rootLogger.setLevel(Level.ERROR);
        ...
        Mockito.verify(mockedAppender, times(1)).doAppend(loggingEventCaptor.capture());    
        ILoggingEvent loggingEvent = loggingEventCaptor.getAllValues().get(0);
        assertThat(loggingEvent.getMessage()).contains("error execute actual user data change in keycloak");
    }
```
### Variante B: logback TurboFilter
```java
@Test
public void testTerribleCase() throws ModuleException {
        AtomicBoolean wasCalled = new AtomicBoolean(false);
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.addTurboFilter(new TurboFilter() {
            @Override
            public FilterReply decide(Marker marker, Logger logger, Level level, String msgFormatString, Object[] params, Throwable t) {
                if (marker != null && marker.getName().equals(Constants.CRITICAL)
                        && msgFormatString.equals("blabla ") && level.equals(Level.ERROR)){ 
                    wasCalled.set(true);
                    return FilterReply.DENY;
                }
                return FilterReply.NEUTRAL;
            }
        });
       //act
       ...
       //assert 
       assertThat(wasCalled.get()).isTrue();
       //reset filter
       loggerContext.resetTurboFilterList();   
}
``` 
## Pitest
* plugin nötig für junit5
```xml
<plugins>
      <plugin>
        <groupId>org.pitest</groupId>
        <artifactId>pitest-maven</artifactId>
        <version>1.4.9</version>
        <dependencies>
          <dependency>
            <groupId>org.pitest</groupId>
            <artifactId>pitest-junit5-plugin</artifactId>
            <version>0.12</version>
          </dependency>
        </dependencies>
      </plugin>
   </plugins>
   ```
* test starten:
`mvn org.pitest:pitest-maven:mutationCoverage`
`mvn -DtargetTests=*CustomUserAttributesHelperTest org.pitest:pitest-maven:mutationCoverage`

## Arguments
 org.mockito.ArgumentMatchers benutzen, achtung: Normale Argumente lassen sich nicht mit  ArgumentMatchers mischen !
`doReturn(userResource).when(usersResource).get(ArgumentMatchers.any());`

# ArgumentCaptor vs ArgumentMatcher

```java
@Captor
ArgumentCaptor<Email> emailCaptor;
// oder: ArgumentCaptor.forClass(Email.class);
Mockito.verify(platform).deliver(emailCaptor.capture());
Email emailCaptorValue = emailCaptor.getValue();
assertEquals("correkt@mail.com", emailCaptorValue);
```
oder 
```java
Credentials credentials = new Credentials("baeldung", "correct_password", "correct_key");
Mockito.when(platform.authenticate(Mockito.eq(credentials)))
  .thenReturn(AuthenticationStatus.AUTHENTICATED);
assertTrue(emailService.authenticatedSuccessfully(credentials));
```