# Lockback in SpringBoot
## Example logback.groovy configuration:
```groovy
// logback.groovy replaces logback.xml, since the groovy syntax is much more readable
scan("30 seconds")

appender("Console-Appender", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss,SSS} [%thread] %highlight(%-5level) %boldGreen(%logger{40})  : %m%xThrowable%n"
        // Throwable ist the superclass of all Exceptions AND Errors
    }
}

logger("io.github.bodote", INFO, ["Console-Appender"], false)
logger("org.springframework.test.web.servlet.result", DEBUG, ["Console-Appender"], false)

root(INFO, ["Console-Appender"])
```