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

For adding of new types or change existing it is possible to use external file (YAML, XML, JSON).

Maven configuration:
```xml
    <plugin>
        ...
        <configuration>
            <synapseArtifactTypesFile>
                ${project.basedir}/src/main/resources/synapse-artifact-types.yaml
            </synapseArtifactTypesFile>
        </configuration>
    </plugin>
```

File synapse-artifact-types.yaml
```yaml
---
registry:
  priority: 100
  type: "registry/resource"

local-entries:
  priority: 200
  type: "synapse/local-entry"
```

#### Resources of WSO2 registry :

 src/main/resources
 * _system
   * config
   * governance
   * ...
 * artifacts.list

For adding of new types or change existing it is possible to use external file (YAML, XML, JSON).

Maven configuration:
```xml
    <plugin>
        ...
        <configuration>
            <registryMediaTypesFile>
                ${project.basedir}/src/main/resources/registry-media-types.yaml
            </registryMediaTypesFile>
        </configuration>
    </plugin>
```

File registry-media-types.yaml
```yaml
---
js:
  type: "application/javascript"

css:
  type: "text/css"
```

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
                <version>1.0.2</version>
                <extensions>true</extensions>
                <configuration/>
            </plugin>
        </plugins>
    </build>
```

To build a CAR run the following command:

`$ mvn clean package`
