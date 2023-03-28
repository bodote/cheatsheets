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


