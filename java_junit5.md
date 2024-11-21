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

## Test log messages

### Approach 1: Mocking the Logger
One approach is to mock the logger itself, using a mocking framework like Mockito. This is particularly useful if you cannot easily manipulate the logging framework's configuration or when you're looking for a quick way to assert log calls without changing much of the logging setup.

Inject a Mock Logger: If your class under test injects its logger (though not a common practice, it's possible for testing purposes), you can inject a mocked logger and verify interactions with it.

Write Your Test:

Use Mockito to mock the logger.
Inject the mock into your class (constructor, setter, or field injection for tests).
Perform your test action.
Verify that the expected logging methods were called on the mock.
Mocking the logger might look like this:

```java

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import static org.mockito.Mockito.*;

class MyServiceTest {

    @Test
    void testErrorLogging() {
        Logger mockLogger = mock(Logger.class);
        MyService service = new MyService(mockLogger);

        service.doSomethingThatFails();

        verify(mockLogger).error("Expected error message");
    }
}
```

**However:** This approach requires your service to allow injecting a logger, which might not align with typical logging practices.

## with `@Slf4J``
```java
// What you write:
@Slf4j
public class MyClass {
    // Your code here
}

// What Lombok generates:
public class MyClass {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MyClass.class);
    
    // Your code here
}
```

### check @Slf4J-logs with Mockito.mockStatic:
however when using `@WebMvcTest` or `@SpringBootTest` , then `MockedStatic` would have to be called **before** the Beans are created, which is ususaly not feasable.
```java
void testStaticMocking() {
    try (MockedStatic<LoggerFactory> mockedStatic = Mockito.mockStatic(LoggerFactory.class)) {
        Logger loggerMock = Mockito.mock(Logger.class);
        mockedStatic.when(() -> LoggerFactory.getLogger(anyString())).thenReturn(loggerMock);
        mockedStatic.when(() -> LoggerFactory.getLogger(any(Class.class))).thenReturn(loggerMock);
        LoggingEventBuilder lEvBuilderMock = Mockito.mock(LoggingEventBuilder.class);
        when(loggerMock.atInfo()).thenReturn(lEvBuilderMock);
        // Use your class that calls LoggerFactory.getLogger()
        DemoCtr instance = new DemoCtr();
        instance.demo("Your expected log message", "x");
        // Verify logger interactions
        Mockito.verify(loggerMock).debug("Your expected log message");
        Mockito.verify(lEvBuilderMock).log("my log info{}", 1L);
    }
}
```

#### Here are the key points to understand about Mockito.mockStatic() in multi-threaded scenarios:

##### Thread Safety:

MockedStatic is designed to be thread-safe. Each MockedStatic instance is isolated from others.
However, the static mocking itself affects the global state of the application, which can lead to issues in concurrent testing.


##### Scoping:

The static mock is active only within the scope of the try-with-resources block.
Once the block is exited, the mock is automatically cleared, restoring the original behavior.


##### Potential Issues:

If multiple tests mock the same static method concurrently, they might interfere with each other.
One test might see the mocked behavior set by another test, leading to unpredictable results.


##### Best Practices:

Use @Execution(ExecutionMode.CONCURRENT) (JUnit 5) or similar annotations to explicitly define which tests can run concurrently.
Keep the scope of static mocking as narrow as possible.
Consider using different classes or methods for static mocking in different tests to avoid conflicts.


##### Alternative Approaches:

If possible, refactor the code to avoid static methods, making it more testable and less prone to concurrency issues.
Use dependency injection to provide mockable instances instead of relying on static methods.


##### Mockito's Handling:

Mockito attempts to handle concurrent mocking by using thread-local storage for mocks.
However, it's still possible for tests to interfere with each other if not properly isolated.


##### Testing Frameworks:

Some testing frameworks (like JUnit 5) provide mechanisms to control test execution order and parallelism, which can help manage these issues.

### filter logmessages in a test case

**THIS IS PROBALBY NOT THREAD SAVE!**

#### extend Test with TestWithLogger

> [!WARNING]
> THIS IS PROBALBY NOT THREAD SAVE! (except you would use ThreadLocal varables )

```java
public class TestWithLogger {
  // never ever use the the
    //ROOT_LOGGER! because that would interfere with other tests running in parallel at the same time!
    protected final Logger logger;

    protected ListAppender<ILoggingEvent> logEventlistAppender;
    public TestWithLogger(Logger logger) {
        this.logger = logger;
    }

    public TestWithLogger(org.slf4j.Logger logger) {
        this.logger = (Logger) logger;
    }
    @BeforeEach
    public void setUp() {
        if (logger == null) {
            fail("no logger set in constructor");
        }

        // Create and start the ListAppender
        logEventlistAppender = new ListAppender<>();
        logEventlistAppender.start();

        // Add the ListAppender to the logger
        logger.addAppender(logEventlistAppender);
    }

    @AfterEach
    public void tearDown() {
        // Remove the ListAppender from the logger
        logger.detachAppender(logEventlistAppender);
    }

    protected boolean hasLogsOfLevel(Level level) {
        return logEventlistAppender.list.stream().anyMatch(event -> event.getLevel() == level);
    }

    protected boolean hasNoLogsOfLevel(Level level) {
        return !hasLogsOfLevel(level);
    }

    protected List<ILoggingEvent> searchLogEvents(String string) {
        return this.logEventlistAppender.list.stream()
                .filter(event -> event.toString().contains(string))
                .collect(Collectors.toList());
    }

    protected List<ILoggingEvent> searchLogEvents(String string, Level level) {
        return this.logEventlistAppender.list.stream()
                .filter(event ->
                        event.toString().contains(string) && event.getLevel().equals(level))
                .collect(Collectors.toList());
    }

    protected boolean hasMDCKeyInLogEvents(String message, Level level, String mdcKey) {
        return searchLogEvents(message, level).stream()
                .flatMap(logEv -> logEv.getMDCPropertyMap().keySet().stream())
                .anyMatch(key -> key.equals(mdcKey));
    }
}
```

### Using TestWithLogger:
- extend your test class with `TestWithLogger`
- OR: instatiate the `TestWithLogger` as a delegate in your test class, call the `setUp()` and `tearDown()` and then `searchLogEvents()` 

#### Variante C: logback TurboFilter

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
If in 2 or more tests, most code lines are duplicates, except a specific method call,
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

## AssertJ, SoftAssertions and assertAll
```java
import static org.junit.jupiter.api.Assertions.assertAll;

assertAll(
  () -> assertThat("a").as("Phone 1").isEqualTo("a"),
  () -> assertThat("b").as("Service bundle").endsWith("c")
);
```
or
```java
SoftAssertions phoneBundle = new SoftAssertions();
phoneBundle.assertThat("a").as("Phone 1").isEqualTo("a");
phoneBundle.assertThat("b").as("Service bundle").endsWith("c");
phoneBundle.assertAll();
```

### verify calling mock-methods
```java 
@Captor
    ArgumentCaptor<String> stringArgumentCaptorCaptor;
verify(bar, times(1)).someMethod(stringArgumentCaptorCaptor.capture());
stringArg = stringArgumentCaptorCaptor.getAllValues().get(0)
assertThat(stringArg).isEqualTo("whatever")
```

### AssertJ and extracting mutiple attributes and ListAssert<?>

```java
record ClassWithAttributs(String first, String second){}
List<ClassWithAttributes> listWithAttributes = List.of(
   new ClassWithAttributs("The first","the second"),
   new ClassWithAttributs("2nd The first","2nd the second"));
assertThat(listWithAttributes).extracting(ClassWithAttributes::first,c->c.second())
    .contains(
      tuple("The first","the second"),
      //...
    )
```
### AssertJ and comparing time
`asserThat(oneMinAgo).isCloseTo(now(),byLessThan(1,SECONDS))`

### AssertJ and Clock

use static mock: 
```java
var lastChristmas = LocalDate.of("2023-12-24");
try(var staticMock = Mockito.mockStatic(LocalDate.class,CALLS_REAL_METHOD)){
  staticMock.when(LocalDate::now).thenReturn(lastChristmas)
}
```

### Junit5 AOP and InvocationInterceptor

```java
public class CustomInvocationInterceptor implements InvocationInterceptor {

    @Override
    public void interceptTestMethod(Invocation<Void> invocation, 
                                    ExtensionContext context, 
                                    Method testMethod) throws Throwable {
        System.out.println("Before executing method: " + testMethod.getName());
        
        // Proceed with the actual test method execution
        invocation.proceed();

        System.out.println("After executing method: " + testMethod.getName());
    }
}
```
use it: 
```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(CustomInvocationInterceptor.class)
public class SampleTest {

    @Test
    void testExample() {
        System.out.println("Executing the test method.");
    }
}
```
### Soft Assertions 
```java

public class SoftAssertionsTest {

    @Test
    void testWithSoftAssertions() {
        try (AutoCloseableSoftAssertions softly = new AutoCloseableSoftAssertions()) {
            // Perform multiple assertions
            softly.assertThat("Hello World").startsWith("Hello");
            softly.assertThat(42).isGreaterThan(30);
            softly.assertThat("JUnit 5").contains("5");
            softly.assertThat(100).isLessThan(50);  // This will fail
            softly.assertThat("SoftAssertions").endsWith("xyz");  // This will also fail
        }
        // Test will fail at the end of the block if any assertions failed
    }
}
```


