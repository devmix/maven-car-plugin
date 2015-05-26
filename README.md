# maven-car-plugin [![Build Status](https://travis-ci.org/devmix/maven-car-plugin.svg?branch=master)](https://travis-ci.org/devmix/maven-car-plugin)

Maven plugin for building Carbon Application Archive (CAR) based on configuration of Apache Synapse.

### Project example

#### Synapse config:

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
 
#### Resources of WSO2 registry :

 src/main/resources
 * _system
   * config
   * governance
   * ...
 * artifacts.list


#### File artifacts.list

Format:

```
:<mime type 1>
<relative path to resource 1>
<relative path to resource 2>
...

:<mime type 2>
...
```

Example:
```
:application/vnd.wso2.sequence
 _system/governance/sequences/sequence.xml
```

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
