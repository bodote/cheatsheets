# Maven tricks
## Filtering resources
um im properties dateien Variablen durch maven ersetzten zu lassen , kann man : 
```xml
<build>
		<resources>
			<!-- https://stackoverflow.com/questions/3697449/retrieve-version-from-maven-pom-xml-in-code/41791885 
				https://stackoverflow.com/questions/38983934/cannot-get-maven-project-version-property-in-a-spring-application-with-value -->
			<resource>
				<filtering>true</filtering>
				<directory>src/main/resources</directory>
				<includes>
					<include>build.properties</include>
				</includes>
			</resource>
			<resource>
				<filtering>false</filtering>
				<directory>src/main/resources</directory>
				<includes>
					<include>**/*</include>
					<include>*</include>
				</includes>
				<excludes>
            <exclude>build.properties</exclude>
        </excludes>
			</resource>
		</resources>
...
````
