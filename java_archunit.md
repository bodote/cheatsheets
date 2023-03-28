## cycles:
```java
 @ArchTest
    public static final ArchRule cycleRoute =
            slices().matching("de.mypackage.(**)").should().beFreeOfCycles();
```
## dto
```java
@ArchTest
    public static final ArchRule dTODependencyRules = classes()
            .that()
            .resideInAPackage("..dto..")
            .should()
            .onlyDependOnClassesThat()
            .resideInAnyPackage(
                    "java..",  "lombok..");
```
## accessed by
```java
    @ArchTest
    public static final ArchRule servicesDependencyRule = classes()
            .that()
            .resideInAPackage("..service..")
            .should()
            .onlyBeAccessed()
            .byAnyPackage("..controller..", "..service..", "..config..");
```
## ChatGPT (GPT-4)
### Question: 
"Do you have an example for an "archunit"  test using it's version 1.0.1 , that implements a "clean architecture" as described by Robert C Martin ?"

### Answer
"Clean Architecture" is a software design principle introduced by Robert C. Martin that focuses on separation of concerns and maintainability. In this example, we'll create a simple project structure following Clean Architecture principles and then use ArchUnit 1.0.1 to enforce the dependency rules.

Here's a basic project structure:

* your.base.package
  * domain
    * entities
    * usecases
* adapters
  * primary
    * controllers
  * secondary
    * repositories
* framework

Now let's create ArchUnit tests to enforce Clean Architecture dependency rules:

1. Add ArchUnit dependency as described in the previous answer.

1. Create a test class using your preferred test framework (e.g., JUnit):

```java
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class CleanArchitectureTest {

    @Test
    public void domainShouldNotDependOnAdaptersOrFramework() {
        JavaClasses importedClasses = new ClassFileImporter().importPackages("your.base.package");

        ArchRule rule = noClasses()
                .that().resideInAnyPackage("your.base.package.domain..")
                .should().dependOnClassesThat().resideInAnyPackage("your.base.package.adapters..", 
                 "your.base.package.framework..");
       rule.check(importedClasses);
    }

    @Test
    public void adaptersShouldNotDependOnFramework() {
        JavaClasses importedClasses = new ClassFileImporter().importPackages("your.base.package");
    
        ArchRule rule = noClasses()
                .that().resideInAnyPackage("your.base.package.adapters..")
                .should().dependOnClassesThat().resideInAnyPackage("your.base.package.framework..");
    
        rule.check(importedClasses);
    }

   @Test
   public void primaryAdaptersShouldDependOnlyOnDomain() {
       JavaClasses importedClasses = new ClassFileImporter().importPackages("your.base.package");
   
       ArchRule rule = classes()
               .that().resideInAnyPackage("your.base.package.adapters.primary..")
               .should().onlyHaveDependentClassesThat().resideInAnyPackage("your.base.package.domain..");
   
       rule.check(importedClasses);
   }
   
   @Test
   public void secondaryAdaptersShouldDependOnlyOnDomain() {
       JavaClasses importedClasses = new ClassFileImporter().importPackages("your.base.package");
   
       ArchRule rule = classes()
               .that().resideInAnyPackage("your.base.package.adapters.secondary..")
               .should().onlyHaveDependentClassesThat().resideInAnyPackage("your.base.package.domain..");
   
       rule.check(importedClasses);
   }
}
```

Replace `your.base.package` with the base package of your application where you want to apply the rules.

This test class defines four ArchUnit rules:

1. Domain should not depend on adapters or framework: This rule ensures that classes in the domain package don't depend on classes in adapters or framework packages.
2. Adapters should not depend on framework: This rule ensures that classes in the adapters package don't depend on classes in the framework package.
3. Primary adapters should depend only on domain: This rule ensures that primary adapter classes (e.g., controllers) only depend on classes in the domain package.
4. Secondary adapters should depend only on domain: This rule ensures that secondary adapter classes (e.g., repositories) only depend on classes in the domain package.

These rules help enforce the dependencies between layers as described in the Clean Architecture. If any of the rules are violated, the corresponding test will fail, and you'll see a description of the violations in the test output.

