# Startup and Config
## Using CommandLineRunner
Spring Boot provides different mechanisms to run a specific code at [Application Startup.](https://medium.com/@cancerian0684/run-method-on-application-startup-in-spring-boot-37aa5e82c948)

Eine Möglichkeit ist der `CommandLineRunner`:

CommandLineRunner can be used to run code at application startup, provided it should be contained within SpringApplication.
```Java
@SpringBootApplication
public class Application {
   private static final Logger log = LoggerFactory.getLogger(Application.class);

   public static void main(String[] args) {
      SpringApplication.run(Application.class);
   }

   @Bean
   public CommandLineRunner demo(CustomerRepository repository) {
      return (args) -> {
         // save a couple of customers
      };
   }
}
```
wobei `(args) ->` hier die `args` von `main(args)` sind
## Configuration Klassen
`@Configuration` kann man jeder Klasse geben, damit es von SpringBoot beim Startup gescannt wird. 
Da `@Configuration` auch `@Component` einschließt , kann letzteres auch funktionieren, insb. wenn dessen Methoden auch noch mit `@Bean` markiert sind, muss aber nicht.

# Logging and Debugging

## Spring Boot
* enable color console, disable banner:
```properties
spring.main.banner-mode=off
spring.output.ansi.enabled=ALWAYS
logging.config=/Users/<mylocation>/src/main/resources/mylogbackconfig.xml
management.endpoints.web.exposure.include=*
spring.websecurity.debug=true
```
## Debugging Execption Handling on HTTP responses
[see here](https://reflectoring.io/spring-boot-exception-handling/)

add to application properities:
```properties
server.error.include-message=always
server.error.include-binding-errors=always
server.error.include-stacktrace=ON_PARAM  # or always or never
server.error.include-exception=true
```




## Spring Boot Logback extensions (logback-classic)
* nur wenn man `logback-spring.xml` verwendet, klappt nicht mit `logback.xml` !
* `scan` klappt nur von src-dir aus wenn man in application.properties: 
`logging.config=/Users/me/myproj/src/main/resources/logback-spring.xml` setzt.
```xml
<configuration debug="true" scan="true" scanPeriod="3 seconds">
        <include resource="org/springframework/boot/logging/logback/defaults.xml"/>
        <include resource="org/springframework/boot/logging/logback/console-appender.xml" />
        <include resource="org/springframework/boot/logging/logback/file-appender.xml" />

        <springProfile name="staging">
            <!-- configuration to be enabled when the "staging" profile is active -->
        </springProfile>

        <springProfile name="dev | staging">
            <!-- configuration to be enabled when the "dev" or "staging" profiles are active -->
        </springProfile>

        <springProfile name="!production">
            
            <root level="INFO">
                <appender-ref ref="CONSOLE" />
            </root>
            <logger name="org.springframework.web" level="DEBUG"/>
        </springProfile>
</configuration>
```

## H2-Database console access:
* [http://localhost:9090/h2-console/](http://localhost:9090/h2-console/) 
* user/password: add to `src/main/resources/application.properties`
* add `security.headers.frame-options=disable` to `src/main/resources/application.properties`;
  * because : This is because the H2 Database console is typically accessed within an iframe, and by default, the X-Frame-Options header is set to DENY or SAMEORIGIN, which prevents the console from being loaded within a frame.

## Logback-access does NOT work with REST endpoints
The logback-access module, part of the standard logback distribution, integrates with Servlet containers such as Jetty or Tomcat to provide rich and powerful HTTP-access log functionality. [more on logback-access](https://logback.qos.ch/access.html)


## DEBUG Log output for Spring-Boot Start up
* in Intellij, to force spring debug logging use : ![assets/Intellij_debug_log.png](assets/Intellij_debug_log.png)
* or (even without Intellij), add `logging.level.root=DEBUG` to `src/test/resources/application.properties` or `src/main/resources/application.properties`
* open  *"View->Tools Windows->Service->Edit Configuration"* in Intellij und setze "Enable debug output" (sets the -Ddebug switch)
* now spring shows during its startup what Beans/AutoConfiguration are configured 


## Debug WebMCV oder webmvc tests
### Debug log for webmvc rest calls:
add  to `src/test/resources/application.properties` or `src/main/resources/application.properties`
```properties
logging.level.org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping=trace
logging.level.org.springframework.test.web.servlet.TestDispatcherServlet=trace
```
OR put it in logback-spring.xml: 
```xml
 <logger 
    name="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping" 
    level="TRACE"/>
<logger name="org.springframework.test.web.servlet.TestDispatcherServlet" level="TRACE"/>

```

### Debuglog with an Interceptor **instead** of a Filter
Filter vs Interceptors:
* Filter are part of Java Servlet Spec , Interceptors are not
* Interceptor are more powerful, have access to more details
* Interceptor can log the response IF it overwrites the `afterCompletion()` method
* however Interceptors does **not log errors** if a Filter in the securityFilterChain does deny a request, HandlerInterceptor's afterCompletion method is only called after successful completion of a request, not when an exception has been thrown.
* https://www.baeldung.com/spring-mvc-handlerinterceptor-vs-filter
* org.springframework.web.servlet.HandlerInterceptor interface. This gives us the option to override three methods:
    * preHandle() – Executed before the target handler is called
    * postHandle() – Executed after the target handler but before the DispatcherServlet renders the view
    * afterCompletion() – Callback after completion of request processing and view rendering*
* ![FiltersVsInterceptors](https://www.baeldung.com/wp-content/uploads/2021/05/filters_vs_interceptors.jpg)


```Java
@Component
public class LoggingInterceptor implements HandlerInterceptor {
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    System.out.println("-------------- Request Data --------------");
    System.out.println("Method: " + request.getMethod());
    System.out.println("URL: " + request.getRequestURL().toString());
    System.out.println("Headers: ");
    Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();
      System.out.println(headerName + " = " + request.getHeader(headerName));
    }
    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    System.out.println("-------------- Response Data --------------");
    System.out.println("Status: " + response.getStatus());
    HttpHeaders headers = new HttpHeaders();
    response.getHeaderNames().forEach(name -> headers.add(name, response.getHeader(name)));
    System.out.println("Headers: " + headers);
  }
}
//------
@Configuration
@Slf4j
public class ProjectConfig implements WebMvcConfigurer {
    private final LoggingInterceptor loggingInterceptor;

    @Autowired
    public ProjectConfig(LoggingInterceptor loggingInterceptor) {
        this.loggingInterceptor = loggingInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor);
    }
}

```

### Record (and Debug) all HTTP exchange (but **without the body**)
add this to the `@Configuration`:
```Java
    @Bean
    public HttpExchangeRepository httpExchangeRepository() {
//        return new InMemoryHttpExchangeRepository(); //can be used for /actuator/httpexchanges 
        return new HttpExchangeRepository() {
            @Override
            public List<HttpExchange> findAll() {
                return null; // only implement if you use /actuator/httpexchanges
            }

            @Override
            public void add(HttpExchange httpExchange) {
                StringBuffer out = new StringBuffer();
                out.append("Request:\n");
                httpExchange.getRequest().getHeaders().forEach((key, value) -> {
                    out.append(key + ": " + value + "\n");
                });
                if (httpExchange.getRequest().getMethod() != null && httpExchange.getRequest().getMethod().length() > 0) {
                    out.append(httpExchange.getRequest().getMethod());
                    out.append(" ");
                    out.append(httpExchange.getRequest().getUri());
                }
                out.append("\nResponse:\n");
                httpExchange.getResponse().getHeaders().forEach((key, value) -> {
                    out.append(key + ": " + value + "\n");
                });
                out.append(String.format("Status: %s  Time taken: %.6f sec",httpExchange.getResponse().getStatus(),
                        httpExchange.getTimeTaken().getNano()*0.0000000001));

                log.info(out.toString());
                MDC.clear();
            }
        };
    }
```

### Actuator Endpoints:
in `pom.xml` : add `<artifactId>spring-boot-starter-actuator</artifactId>`
```properties
#management.endpoints.enabled-by-default=true
management.endpoint.mappings.enabled=true
management.endpoints.web.exposure.include=info, health, mappings
```
call `http://localhost:9090/actuator/` oder direkt `http://localhost:9090/actuator/mappings` to show the REST (and other ) enpoints 


# Configuration
```java
@Configuration
@PropertySource(value = "classpath:build.properties", ignoreResourceNotFound = false)
```

## Weitere Annotations
### @Controller
**Spring MVC** , Spring Basis Componenten die auf built on the [Servlet API](https://javaee.github.io/servlet-spec/downloads/servlet-4.0/servlet-4_0_FINAL.pdf) (ein offizieller Java Standard ) aufsetzt und letztlich das "HttpServlet" abstrahiert, vor dem Entwickler verbirgt und damit leichter benutzbar macht.
Letztlich ersetzt die `@Controller` die Notwendigkeit sich mit dem  HttpServlet auseinandersetzten zu müssen.
Zum `@Controller` gehören weitere Annotations, die das ganze erst rund machen, z.B. :
* `@PostMapping("/meinpfad")`, ebenso `@GetMapping`, `@PutMapping`, `@DeleteMapping`, `@PatchMapping`, mit [7 möglichen Parametern](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation/PostMapping.html)
* `@RequestParam(required = false) `
### @RequestBody
Simply put, the `@RequestBody` annotation on a method argument inside a `@Controller` class maps the HttpRequest body to a transfer or domain object, enabling automatic deserialization of the inbound HttpRequest body onto a Java object. Der Text im "request body" wird dann als JSON oder XML angesehen und es wird versucht davon direkt ein entsprechendes Java- Objekt zu erzeugen und zu bestücken.
* optinal: `@RequestBody(required = true)`
### @ResponseBody
das Gegenstück zu `@RequestBody` nur für die Rückgabe des Ergebnisses 
### @RestController
`@RestController`’s source code shows that it actually is a `@Controller`, with the added `@ResponseBody` annotation. Which is equivalent to writing `@Controllers` which have `@ResponseBody` annotated on **every single method**.

## mit Lombok
### @RequiredArgsConstructor(onConstructor = @__(@Autowired))
ergänzt den `@RequiredArgsConstructor` um eine `@Autowired` Annotation
# Json Object Mapping
Passiert ja im RestController unter Umständen komplett implizit, daher [hier](https://attacomsian.com/blog/processing-json-spring-boot) nochmal zur Erinnerung , wie es explizit geht:
```java
//create ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();

        //read json file and convert to customer object
        Customer customer = objectMapper.readValue(new File("customer.json"), Customer.class);
``` 
## Api Documentation Sourcefile download Intellij/maven/gradle
- in Intellij - maven see https://stackoverflow.com/a/36071308/3952407
- gradle: add `plugins{ id 'idea' }` and 
```groovy   
idea {
    module {
        downloadJavadoc = true
    }
}
```

## spring-integration-smb
pom.xml:
```xml
<dependency>
  <groupId>org.springframework.integration</groupId>
  <artifactId>spring-integration-smb</artifactId>
  <version>6.0.4</version>
</dependency>
```


```java
 void integrationSpringtest() throws IOException {
        var ssf = new SmbSessionFactory(); 
        ssf.setHost(host);
        // ssf.setHost(transfer);
        ssf.setUsername(host + "\\" + username);
        ssf.setPassword(password);
        ssf.setShareAndDir("/SHAREName/mydir/"); 
        var session = ssf.getSession();

        var files = session.list(".");
        for (var file : files) {
            log.info("CanonicalPath {}", file.getCanonicalPath());
        }

        log.info("test.csv {}",session.isFile("test.csv"));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        session.read("test.csv",outputStream);
        byte[] fileContents = outputStream.toByteArray();
        outputStream.close();
        session.close();
        log.info("File content: " + new String(fileContents));
    }
```
# Implementing own Filters for the SecurityFilterChain
* use `OncePerRequestFilter` and excend that.
* consider using and implementing a `HandlerInterceptorAdapter` instead of a filter  and implementing a `WebMvcConfigurer` that adds that `HandlerInterceptorAdapter`
* However, a `OncePerRequestFilter` might be more suitable because:
  * It is at a lower level than `HandlerInterceptorAdapter`, which means it is executed before any Spring-specific processing takes place. It ensures that your check is performed once and only once per request. It allows access to the raw request and response objects.
* On the other hand, `HandlerInterceptorAdapter` could be a better fit 
  * if your custom filter logic is tightly integrated with your Spring MVC controllers, and you need to utilize Spring MVC specific features.
## Error handling for own filters:
* define a OncePerRequestFilter : 
```Java
@Component
@Slf4j
public class MyOncePerRequestExceptionHandlingFilter extends OncePerRequestFilter {
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

    // Process the request and catch exceptions from other filters , therefore avoid spring boots own
    // exception handling which would reroute the request to "/error" page , which does not make much sense for
    // REST requests
    try {
      filterChain.doFilter(request, response);
    } catch (Exception ex){
      log.error("exception: ",ex);
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
  }
}
```
* add this filter even before your other filters: 
```Java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
http.addFilterBefore(new MyOncePerRequestExceptionHandlingFilter(), 
           BasicAuthenticationFilter.class)
        .addFilterAfter(new MyRequestValidationFilter(), 
          MyOncePerRequestExceptionHandlingFilter.class)
        .addFilterAfter(new AuthenticationLoggingFilter(), 
          BasicAuthenticationFilter.class).authorizeRequests()
        .anyRequest().permitAll();

return http.build();
}
```




