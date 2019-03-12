# Process-Manager (PM)

Process-Manager (PM) is a framework to manage multiple processing factories.

A factory is a group of processes. A factory can be locked in this case it's not possible to add to it a new process or delete one.
A factory is referenced by a String identifier.

A default locked factory called orbisgis is provided.

A process is a way of packaging algorithms. A process is defined by inputs and outputs.


The main classes to implement a PM are defined in the process-manager-api module.


# Examples


## How to 

soon

# Groovy Grab


```xml
@GrabResolver(name='orbisgis', root='http://repo.orbisgis.org/')
@Grab(group='org.orbisgis', module='process-manager', version='1.0-SNAPSHOT')
```

# Maven dependency

You can include PM in your project thanks to Maven repositories.


Use the current snapshot add it in the pom

```xml
<repository>
  <id>orbisgis-nexus-snapshot</id>
  <name>OrbisGIS nexus snapshot repository</name>
  <url>http://nexus.orbisgis.org/content/repositories/osgi-maven-snapshot</url>
</repository>
```
 then declared the dependency

```xml
<dependency>
  <groupId>org.orbisgis</groupId>
  <artifactId>process-manager</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

