# Logging in Java
## General Rules about Logging (applies not only to Java/Spring Boot)

There are general rules and best practices for logging in a Spring Boot application in a production environment. Some of these guidelines apply to logging in general, while others are more specific to Splunk, ELK, or similar log management systems.

1. **Log Levels**: Use appropriate log levels such as ERROR, WARN, INFO, DEBUG, and TRACE. Use 
  * **ERROR** for serious issues that need immediate attention, 
  * **WARN** for potential issues, that might need attention but not immediately
  * **INFO** for high-level application flow, (e.g. can be used for statistics as well) 
  * **DEBUG** for detailed debugging information, (NOT in production, exept for debuggin a specific problem) and 
  * **TRACE** for very detailed information for diagnostic purposes. In a production environment, normally the log level is set to INFO, but it might be adjusted based on your requirements.

2. **Standardized Logging Format**: All logs should follow a standard format. It should include at least timestamp, log level, service ID or name, thread ID, class or method name, and the message. You can use the logging capabilities of Spring Boot and SLF4J to accomplish this.

3. **Structured Logging**: If you are using a log management system like Splunk or ELK, structured logging can be very helpful. You could use JSON format for your logs, which can then be easily parsed and analyzed by these systems. Libraries such as Logstash Logback Encoder can be used in Spring Boot for this purpose.

4. **Use Unique ID for each request**: For microservices architecture or even for monoliths, assign a unique ID to each request at the very beginning. This will help you track all the logs corresponding to a particular request, especially when logs are distributed across different services or components. Spring Sleuth can be used for this purpose in Spring Boot applications.

5. **Sensitive Data**: Do not log sensitive information such as passwords, personal identifiable information, etc. This is crucial from a security and compliance perspective.

6. **Exception Logging**: Always log the complete stack trace for exceptions. Without the stack trace, it could be very difficult to diagnose the issue. You should log the exception message at ERROR level, but also consider the security perspective while logging exceptions.

7. **Use MDC for Important Context Information**: Mapped Diagnostic Context (MDC) can be used to add important contextual information to the logs, which can be very helpful during debugging.

8. **Externalized Logging Configuration**: Logging configurations like log levels, patterns, etc. should be externalized and should not be hard-coded. Spring Boot allows you to configure these in application.properties or application.yml file.

9. **Log Rotation and Retention**: Depending on your storage capacity, you should configure log rotation and retention policies. This can be done using the logging system or the log management system.

10. **Monitoring and Alerts**: With Splunk or ELK, set up alerts for critical errors or unusual activities. You should regularly monitor your logs and analyze them to get insights about your application's behavior, performance issues, etc.

Remember, effective logging can be invaluable when diagnosing issues or understanding application behavior in a production environment. Also for discovering security issues 
(Quelle: ChatCPT plus eigene Gedanken)


## General tips and tricks with logback (and/or Log4j2)
logback and log4j2 are similar because they both decendens of log4j V1
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

## MDC
`@SLF4J` example:
```Java
MDC.put("remoteAddr",request.getRemoteAddr());
MDC.put("remotePort",Integer.toString(request.getRemotePort()));
MDC.put("requestID",requestId);
```
in another method that is called later: 
`MDC.clear();`

add to logback config `%blue(%X{requestID}) %green(%X{remoteAddr}:%X{remotePort})`here:
```xml
<appender name="MyAppender" class="ch.qos.logback.core.ConsoleAppender">
    <!-- encoders are assigned the type
            ch.qos.logback.classic.encoder.PatternLayoutEncoder by default -->
    <encoder>
        <pattern>%-30(%d{dd'T'HH:mm:ss.SSS} %blue([%thread])) %red(%-5level) %cyan(%logger{15}) %blue(%X{requestID}) %green(%X{remoteAddr}:%X{remotePort}) %msg %n</pattern>
    </encoder>
</appender>
```
