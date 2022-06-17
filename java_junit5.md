# Junit5
## Exceptions

```java
Exception exception = assertThrows(NumberFormatException.class, () -> {
        Integer.parseInt("1a");
    });
```

## matchers

- entweder `org.hamcrest.Matcher.*` oder
- die `org.assertj.core.api.Assertions` , die eigentlich mehr **fluent** sind.
- `org.junit.jupiter.api.Assertions.*` sind nicht so gut

### filter logmessages in a test case

#### Variante B: logback TurboFilter

#### Variante A: Appender Filter (log4j 2.x)

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

#### Variante B: logback TurboFilter

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

### Variante C

https://www.baeldung.com/junit-asserting-logs
MemoryAppender:

```java
    Logger logger = (Logger) LoggerFactory.getLogger(LOGGER_NAME);
    memoryAppender = new MemoryAppender();
    memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
    logger.setLevel(Level.DEBUG);
    logger.addAppender(memoryAppender);
    memoryAppender.start();
    // ....
    assertThat(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(4);
    assertThat(memoryAppender.search(MSG, Level.INFO).size()).isEqualTo(1);
    assertThat(memoryAppender.contains(MSG, Level.TRACE)).isFalse();
```

### Pitest

- plugin nötig für junit5

```xml
<plugins>   
   <groupId>org.pitest</groupId>
   <artifactId>pitest-maven</artifactId>
   <version>1.8.1</version>
   <configuration>
     <jvmArgs>
       <jvmArg>--enable-preview</jvmArg>
     </jvmArgs>
   </configuration>
   <dependencies>
       <dependency>
           <groupId>org.pitest</groupId>
           <artifactId>pitest-junit5-plugin</artifactId>
           <version>0.16</version>
       </dependency>
   </dependencies>  
</plugins>
```

- test starten:
  `mvn org.pitest:pitest-maven:mutationCoverage`
  `mvn -DtargetTests=*CustomUserAttributesHelperTest org.pitest:pitest-maven:mutationCoverage`
  `mvn -dtargetTests=my.package.Foo*Test org.pitest:pitest-maven:mutationCoverage`
  `mvn -dtargetTests=*EmailChangeControllerTest,*StateTreeEmailChangeTest org.pitest:pitest-maven:mutationCoverage`
- execute only some tests:
  `mvn -Dtest=TestSquare,TestCi*le test`

- skip unittests und code recompile
  `<skipTests>${skip.surefire.tests}</skipTests>` in pom.xml beim surefire - Configuration einbauen, dann:
  `mvn -Dskip.surefire.tests=true -Dcodegen.skip=true verify`
  siehe [sprint_boot_testing](/Users/bodo/swe_projects/cheatsheets/sprint_boot_testing.md)
  `mvn -Dtest=*EmailChangeController* -Dcodegen.skip=true test`
  `mvn -Dskip.surefire.tests=true -Dcodegen.skip=true -Dswagger.skip=true -Dit.test=*AccountControllerTestIT verify`

- set additional environment
  `mvn -Dskip.surefire.tests=true -Dcodegen.skip=true -Dswagger.skip=true -Dit.test=*AccountControllerTestIT verify`

`mvn -DmyVariable=someValue verify`

### Arguments

org.mockito.ArgumentMatchers benutzen, achtung: Normale Argumente lassen sich nicht mit ArgumentMatchers mischen !
`doReturn(userResource).when(usersResource).get(ArgumentMatchers.any());`

## ArgumentCaptor vs ArgumentMatcher

from https://www.baeldung.com/mockito-argumentcaptor :

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
## Method references as parameters
If in 2 or more tests, most code lines are dublicates, except a method call,
eg. in the first test you want to call `put()` and in the 2nd test you want to call `post()` but with all other parameters are the same , you can do this: 
```java
  //...
  {
   Function<String, MockHttpServletRequestBuilder> putMethod = (String uri) ->  put(uri);
   Function<String, MockHttpServletRequestBuilder> postMethod = (String uri) ->  post(uri);
   // not possible here: 
   // Function<String, MockHttpServletRequestBuilder> postMethod = MockHttpServletRequestBuilder::post;
   // because MockHttpServletRequestBuilder::post is ambigous / overloaded with different sets of parameters.
    
    // Act;
    assertMvcResult(journeyUpdateDTO,expectedJourneyDTO, postMethod, "");
    assertMvcResult(journeyUpdateDTO,expectedJourneyDTO, putMethod, "/1234");
  }

  private void assertMvcResult(JourneyCreateDTO journeyUpdateDTO, JourneyDTO expectedJourneyDTO,Function<String, MockHttpServletRequestBuilder> method, String id) throws Exception {
    MvcResult mvcResult = mockMvc.perform(method.apply(API_URI + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(journeyUpdateDTO)))
            .andExpect(status().isCreated()).andReturn();

    String contentString = mvcResult.getResponse().getContentAsString();
    JourneyDTO actualDTO = objectMapper.readValue(contentString, JourneyDTO.class);

    assertEquals(expectedJourneyDTO, actualDTO);
  }
  ```

  using the "FunctionalInterface" `Function<T, R>` with its `apply()`- method call . 