# Logging in Java
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
