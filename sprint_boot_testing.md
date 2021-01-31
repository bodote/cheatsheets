# Testing in Sprinboot mit Junit5 und Mockito
## Initialisierung
* `@ExtendWith(MockitoExtension.class)`vor der Klasse (Extension that initializes mocks and handles strict stubbings. This extension is the JUnit Jupiter equivalent of our JUnit4 MockitoJUnitRunner, `@ExtendWith(SpringExtension.class)`. It replaces the deprecated JUnit4 `@RunWith(SpringJUnit4ClassRunner.class)`)
* `@SprinBootTest` 
  * tells Spring Boot to look for a main configuration class (one with @SpringBootApplication, for instance) and use that to start a Spring application context.
  * Includiert bereits `@ExtendWith(SpringExtension.class)`
* `@Autowired MockMvc mockMvc;` ersetzt einen echten SpringBoot - Server durch einen Mock server, in dem man aber alle Schickten unterhalb des echten Servers wie in echt zur Verfügung hat.
  * To do that, use Spring’s `@MockMvc` and ask for that to be injected for you by using the @AutoConfigureMockMvc annotation on the test case.
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
