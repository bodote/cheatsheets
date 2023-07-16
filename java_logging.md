# Logging in Java
## Spring Boot
* enable color console, disable banner:
```properties
spring.main.banner-mode=off 
spring.output.ansi.enabled=ALWAYS
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

* debugging logback itself: `<configuration debug="true">`
* scan for changes of the log config: `<configuration  scan="true" scanPeriod="3 seconds">`
    * `scan` klappt nur von src-dir aus wenn man in application.properties: 
`logging.config=/Users/me/myproj/src/main/resources/my-logback.xml` setzt (sonst wird vom class path , also local von `target/..` gelesen)
* Custom Appenders and Patterns
```xml
    <appender name="MyAppender" class="ch.qos.logback.core.ConsoleAppender">        
        <encoder>
            <pattern>"%clr(%d{yyyy-MM-dd'T'HH:mm:ss.SSSXXX}){faint} %clr(%5p) %clr(${PID:- }){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr(){faint}%clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n$%wEx"</pattern>
        </encoder>
    </appender>
        <root level="INFO">
            <appender-ref ref="MyAppender" />
        </root>
```

## Logback-access does NOT work with REST endpoints
The logback-access module, part of the standard logback distribution, integrates with Servlet containers such as Jetty or Tomcat to provide rich and powerful HTTP-access log functionality. [more on logback-access](https://logback.qos.ch/access.html)


