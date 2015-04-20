# maven-car-plugin

Maven plugin for building Carbon Application Archive (CAR) based on configuration of Apache Synapse.

### Project example

#### Sources:

 src/main/synapse-config
 * api
 * endpoints
 * local-entries
 * message-processors
 * message-stores
 * proxy-services
 * sequences
 * tasks
 * templates

#### pom.xml:

```xml
    <build>
        <plugins>
            <plugin>
                <groupId>com.github.devmix.esb</groupId>
                <artifactId>maven-car-plugin</artifactId>
                <version>1.0.0</version>
                <extensions>true</extensions>
                <configuration/>
            </plugin>
        </plugins>
    </build>
```

To build a CAR run the following command:

`$ mvn clean package`