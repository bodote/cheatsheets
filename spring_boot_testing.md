# Testing in Sprinboot mit Junit5 und Mockito
siehe auch [hier](java_junit5.md)
## Initialisierung
* `@ExtendWith(MockitoExtension.class)`vor der Klasse (Extension that initializes mocks and handles strict stubbings. This extension is the JUnit Jupiter equivalent of our JUnit4 MockitoJUnitRunner, `@ExtendWith(SpringExtension.class)`. It replaces the deprecated JUnit4 `@RunWith(SpringJUnit4ClassRunner.class)`)
* `@SprinBootTest` 
  * tells Spring Boot to look for a main configuration class (one with @SpringBootApplication, for instance) and use that to start a Spring application context.
  * Includiert bereits `@ExtendWith(SpringExtension.class)`
  * ist für Integrationstest, da ein vollständiger Webcontainer gestartet wird
* `@WebMvcTest` testet dagegen nur den Weblayer **OHNE** vollständigen Webcontainer. ist also schneller als `@SprinBootTest`
* `@Autowired MockMvc mockMvc;` ersetzt einen echten SpringBoot - Server durch einen Mock server, in dem man aber alle Schichten unterhalb des echten Servers wie in echt zur Verfügung hat.
  * To do that, use Spring’s `@MockMvc` and ask for that to be injected for you by using the `@AutoConfigureMockMvc` annotation on the test case.
  * `@AutoConfigureMockMvc`: Annotation that can be applied to a test class to enable and configure auto-configuration of MockMvc.
* Spring only pick up and registers beans with `@Component`  and doesn't look for `@Service` and `@Repository` in general.
  * `@Component` is a generic stereotype for any Spring-managed component
  * `@Service` annotates classes at the service layer for **business logic**
  * `@Repository` annotates classes at the persistence layer, which will act as a database repository
* `@Entity` JPA-Entity , DB-Classe entspricht oft einer DB-Table


### Testing slices 
..of the application Sometimes you would like to test a simple “slice” of the application instead of auto-configuring the whole application. Test Slices are a Spring Boot feature introduced in the 1.4. The idea is fairly simple, Spring will create a reduced application context for a specific slice of your app.
Also, the framework will take care of configuring the very minimum. Spring Boot 1.4 introduces 4 new test annotations:
* `@WebMvcTest` - for testing the controller/Web layer,  mock MVC testing slice without the rest of the app, Also auch `@Configuration` und `@Services` werden **NICHT** automatisch mit initialisiert. (WorkAround: `@ContextConfiguration()` siehe unten). Auch JPA Repositories funktionieren hier nicht.
* `@JsonTest` - for testing the JSON marshalling and unmarshalling
* `@DataJpaTest` - for testing the repository layer
* `@RestClientTests` - for testing REST clients
* `@JdbcTest`: Useful for raw JDBC tests, takes care of the data source and in memory DBs without ORM frills
* `@DataMongoTest`: Tries to provide an in-memory mongo testing setup
* `@SpringBootTest`: Kompletter Integrationtest: hier kann man im TestCode auch `@Autowired` verwenden !
As of Spring Boot >= 2.1, we no longer need to load the `@ExtendWith(SpringExtension.class)` because it's included as a meta annotation in the Spring Boot test annotations like `@DataJpaTest`, `@WebMvcTest`, and `@SpringBootTest`.

### Caching of Test slices 
the Spring context is cached across tests for test slices (like `@WebMvcTest` see above). If the two test classes are in the **same module** and they have the **same configuration** (meaning that they are not configuring different beans or different properties), then the Spring context will be loaded only once and shared across those tests.

### optimizing Spring Boot Integration Tests
https://www.baeldung.com/spring-integration-test-optimize 

## SpringBootTest (Integrationtest)
### arguments
By default, `@SpringBootTest` will not start a server.
* `@SpringBootTest(args = "--spring.main.banner-mode=off")`
* `@SpringBootTest(properties = "spring.main.web-application-type=reactive")`
* `@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT )` will also start the management server on a separate random port if your application uses a different port for the management server.
* [SpringBoot Doku](https://docs.spring.io/spring-boot/docs/2.4.2/reference/html/spring-boot-features.html#boot-features-testing-spring-boot-applications)
### SpringBootTest using random port 
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class QuearnApplicationTests {
    @LocalServerPort
    private int port;
```
### Overwriteing @Configuration with @TestConfiguration
* if `@TestConfiguration` is an inner class of the test class, it is used automatically,
* otherwise use `@Import(myTestConfig.class)`
* to overwrite the production `@Configuration` (which can **not** be easyly exlcuded, but overwritten) use:
```java
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
            "whatever=true",
            "spring.main.allow-bean-definition-overriding=true"
        })
```
(see [Testing with Spring Boot’s @TestConfiguration Annotation](https://reflectoring.io/spring-boot-testconfiguration) )
### @SpringBootTest with `@MockMvc` vs. `@WebMvcTest`

To use `@MockMvc`in a `@SpringBootTest` you need `@AutoConfigureMockMvc`. 
Wheras with `@WebMvcTest` the  use of `@MockMvc` is included in `@WebMvcTest`, no need to `@AutoConfigureMockMvc`

### caching contexts of @SpringBootTest between tests
When using `@SpringBootTest` in your JUnit tests, the Spring context is cached across tests. If the two test classes are in the **same module** and they have the **same configuration** (meaning that they are not configuring different beans or different properties), then the Spring context will be **loaded only once** and shared across those tests.

### RestTemplate vs. TestRestTemplate

```java
  @Test
    @DisplayName("test our api on the full blown spring-boot server")
    public void springApiEnd2End() {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        Exception thrown = Assertions.assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<String> response = restTemplate.exchange(
                    createURLWithPort("/api/users"),
                    HttpMethod.GET, entity, String.class);
        }, "org.springframework.web.client.HttpClientErrorException$Unauthorized: 401  expected");


        //not needed: assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("same as before but with TestRestTemplate ")
    public void springApiEnd2EndWTestRestTemplate() {
        TestRestTemplate restTemplate = new TestRestTemplate();
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    createURLWithPort("/api/users"),
                    HttpMethod.GET, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
```


### SpringBootTest with BASIC Authentication
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local")
class HelloApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void HalloEndpoint() {
        ResponseEntity<HelloList> responseEntity =
                restTemplate.withBasicAuth("myuser", "test").getForEntity("/api/hello", HelloList.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(new HelloList(Arrays.asList("Hallo", "Welt")));
    }
}
```
### SpringBootTest with JWT Authentication
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(HelloWorldIT.TestConfig.class)
classHelloWorldIT {

    // Define a custom JWT decoder as a lambda function returning a dummy token
    @TestConfiguration
    static class TestConfig {
        @Bean
        public JwtDecoder jwtDecoder() {
            return token -> new Jwt(
                    "token", Instant.now(), Instant.MAX, Map.of("alg", "none"), Map.of(JwtClaimNames.SUB, "testUser"));
        }
    }

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
     void testRestEndpoint() {
        // add Token as Bearer-Autorisierungsheader 
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + "dummy"); 
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response =
                testRestTemplate.exchange("/api/hello", HttpMethod.GET, entity, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
       
    }
}
```




## Testing with @WebMvcTest
* [@TestConfiguration](https://www.logicbig.com/tutorials/spring-framework/spring-boot/test-configuration.html)
* [@TestConfiguration In static nested class](https://www.logicbig.com/tutorials/spring-framework/spring-boot/test-configuration-in-nested-class.html)
```Java
@WebMvcTest(controllers = AccountRedirectionController.class, excludeFilters = {
        @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { SecurityConfig.class, MyFilter.class }) })
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = { "pkce.auth-endpoint=/oauth2/login", "pkce.token-endpoint=/oauth2/token" })
@Import({ JWTKeyUtil.class, AesGcmSecretService.class, CookieService.class }) 
...
// innerhalb der TestKlasse:
    // @Configuration // does also work
    @TestConfiguration // does also work
    static class MyTestConfig {
        @Bean // ersetzt ein @MockBean weiter oben, aber hier mit ner echte Bean und der möglichkeit einzelne methoden zu ueberschreiben
        public SomeServiceOrStuff mygetterMethod() {
            return new SomeServiceOrStuff() {
                @Override
                public String getwhatever() {
                    return "something";
                }
            };
        }

```
* with authentication:
```Java
    @Test
    @SneakyThrows
    void helloWorld()  {
        // Hint: don't use //.with(SecurityMockMvcRequestPostProcessors.httpBasic("userInADatabase", "secret")) because that can not be resolved
        // instead SecurityMockMvcRequestPostProcessors.user("mvctestUser").roles(ROLE_END_USER_DEV) bypasses the real authentication and sets user and role
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hello")
                        .with(SecurityMockMvcRequestPostProcessors.user("mvctestUser").roles(ROLE_END_USER_DEV))).
                andDo(MockMvcResultHandlers.log()).andExpect(jsonPath("$",hasSize(2)));
    }
``` 
siehe auch :[Auto-configured Spring MVC Tests](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-testing-spring-boot-applications-testing-autoconfigured-mvc-tests)

### Testing RestServices with JsonPath
* `mockMvc.perform(MockMvcRequestBuilders.get("/todos").contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$", hasSize(2)))`
* [JsonPath docu](https://github.com/json-path/JsonPath)
* `MockMvcResultMatchers.jsonPath();` erwartet als 2.Argument einen `org.hamcrest.Matchers`, z.B.: `hasSize(2)`
### Assertions and Matchers
* org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath: Evaluate the given [JsonPath](https://github.com/jayway/JsonPath)  expression against the response body and assert the resulting value with the given  `org.hamcrest.Matcher.*`. wobei die `org.assertj.core.api.Assertions` eigentlich mehr **fluent** sind.

### Testing with MockMvc
*  `mockMvc.perform().andDo(MockMvcResultHandlers.print())` `MockMvcResultHandlers.print()` das Ergebniss übersichtlich ausdruckt oder mit  `MockMvcResultHandlers.print()` im logger `"org.springframework.test.web.servlet.result", DEBUG` ausgibt, siehe auch [Logback in SpringBoot](logback.md)



## Use of **InjectMocks**
* [Vorsicht beim Verwenden](https://tedvinke.wordpress.com/2014/02/13/mockito-why-you-should-not-use-injectmocks-annotation-to-autowire-fields/) von `@InjectMocks`! 
  * bei manchen Mocks schlägt das fehl, statt dessen wird die Properity innerhalb von der Klasse die mit `@InjectMocks` annotiert ist, auf null gesetzt und es kommt KEINE Fehlermeldung!
  * Beispiel: es gibt einen Constructor für **mehrere aber nicht alle** dependencies. Dann werden von `@InjectMocks` nur die Dependencies , die per Constructor funktioniere, gesetzt, **alle anderen** Properties beiben auf **null** !
* [Bottom line is](https://stackoverflow.com/questions/40620000/spring-autowire-on-properties-vs-constructor):  constructor injection is actually recommended over field injection, and has several advantages. 
### Lösung statt `@InjectMocks`
* Definiere einen Constructor der alle @Autowired - dependencies enthält und
* verwende statt `@InjectMocks` :
```Java
 @BeforeEach
    public void init() {
        myComponent = new Component( arg1, arg2,...)
    }
```      
## Logging tests
### Use of @Autowired for logger
`@Autowired Logger log;` funktioniert nur wenn man folgendes definiert und konfiguriert:
* LoggingConfiguration klasse: 
```java
@Configuration
public class LoggingConfiguration {

    @Bean
    @Scope("prototype")
    public Logger produceLogger(InjectionPoint injectionPoint) {
        Class<?> classOnWired = injectionPoint.getMember().getDeclaringClass();
        return LoggerFactory.getLogger(classOnWired);
    }
}
```
* Die Test-KLASSE  mit `@ContextConfiguration(classes = { LoggingConfiguration.class, ...  })` zusätzlich annotieren. **ACHTUNG** : dabei muss aber nach `LoggingConfiguration.class,` auch alle weiteren Classen, die getestet werden sollen mit angegeben werden. 
**not necessary when using lomboks @Slf4j annotoation**

## @ExtendWith(OutputCaptureExtension.class) and @SpringBootTest();
```java
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"de.brandad.omega.service.k8s.K8sDevServer.timeoutSeconds=1"})
@Slf4j
@ExtendWith(OutputCaptureExtension.class)
class MyTestClazz {
    @Test
    @Description("make sure that property de.brandad.omega.service.k8s.K8sDevServer.timeoutSeconds=1 ")
    void k8sdevserverScheduler(CapturedOutput output) {
        // here you can check the output
    }

}
```

### controlling log level
To disable the logs, turn off the logging.level in both `src/test/resources/application.properties` and `src/test/resources/logback-test.xml`:
```properties
logging.level.org.springframework=OFF
logging.level.root=OFF
```
and
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="org/springframework/boot/logging/logback/base.xml" />
    <logger name="org.springframework" level="OFF"/>
</configuration>
```

## @SpyBean 
geht nicht ohneweiteres. statt dessen machs , wie oben unter "Testing with **@WebMvcTest** -> **@TestConfiguration In static nested class** beschrieben und dann: 
```java 
@Bean
public SomeServiceOrStuff mygetterMethod() {
    return Mockito.spy(new SomeServiceOrStuff() {
      ...
    })
```

## Maven and Testing
### maven integration tests auch laufen lassen:
* `mvn test` nur unittest und `mvn verify `  ABER : alle unittests müssen erst **grün** sein 
* EINzelnen Test ausführen: `mvn -Dtest=FinApiProviderTestIT test` oder alle Integration-tests:  `mvn -Dtest=*TestIT test` oder besser `mvn -Dit.test=AccountControllerTestIT -Dskip.surefire.tests verify` (skipt dann die Unittest und macht direkt den integrationtest)
geht nur wenn :
```xml
<plugin>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>2.14</version>
    <configuration>
        <!-- skips surefire tests without skipping failsafe tests.
                 Property value seems to magically default to false -->
        <skipTests>${skip.surefire.tests}</skipTests>
    </configuration>
</plugin>
```
* skip maven code gen: `mvn -Dcodegen.skip test`
* alles zusammen um eine Integrationtest schneller auszuführen: 
* `mvn -Dcodegen.skip -Dit.test=InfoControllerTestIT -Dskip.surefire.tests verify`
* `mvn -Dcodegen.skip  -DfailIfNoTests=false -Dskip.surefire.tests verify`
* `mvn -Dcodegen.skip  -DfailIfNoTests=false -Dskip.surefire.tests -Dit.test=*InfoControllerTestIT verify`
