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
