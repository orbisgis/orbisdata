
# OrbisData 
[![GitHub](https://img.shields.io/github/license/orbisgis/orbisdata.svg)](https://github.com/orbisgis/orbisdata/blob/master/docs/LICENSE.md) 
[![Codacy Badge](https://img.shields.io/codacy/grade/93899ea0675d43a2a3787ce5dd3c5595.svg)](https://www.codacy.com/app/orbisgis/orbisdata?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=orbisgis/orbisdata&amp;utm_campaign=Badge_Grade) 

OrbisData is a library providing a unique access point to query, manage and retrieve data dedicated to Groovy scripting.

It is based on Groovy specific syntax and aims to provide an elegant and 
fluent framework to work with geospatial data.

## Getting Started

OrbisData need at least Java 11 to work.

### Architecture

Each module of OrbisData is divided in two : the API mainly, constituted 
of interfaces, and its implementation.

#### Data

The Data modules are dedicated to the creation, the access and the 
requesting of databases.
`H2/H2GIS` and `Postgresql/Postgis` are supported.

#### Process

The Process modules are dedicated to the creation of processes 
with the definition of the in/output. Processes can be executed with 
input values and can also be chained.

### How to add OrbisData

#### Maven

To add OrbisData to a Maven project, add the nexus repository to the pom : 
``` xml
...
    <repositories>
        <repository>
            <id>orbisgis-release</id>
            <url>https://oss.sonatype.org/content/repositories/releases/</url>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
            <releases>
                <enabled>true</enabled>
            </releases>
        </repository>
        <repository>
            <id>orbisgis-snapshot</id>
            <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
            <releases>
                <enabled>false</enabled>
            </releases>
        </repository>
    </repositories>
...
```

and add the desired module :
``` xml
...
    <build>
        ...
        <plugins>
            ...
            <plugin>
                <groupId>org.orbisgis</groupId>
                <artifactId>data</artifactId>
                <version>2.0.0-SNAPSHOT</version>
            </plugin>
            <plugin>
                <groupId>org.orbisgis</groupId>
                <artifactId>process</artifactId>
                <version>2.0.0-SNAPSHOT</version>
            </plugin>
            ...
        </plugins>
        ...
    </build>
...
```

#### Groovy

Using Grab annotation :
``` groovy
@GrabResolver(name='orbisgis', root='https://oss.sonatype.org/content/repositories/snapshots/')
@Grab(group='org.orbisgis.data', module='h2gis', version='2.0.0-SNAPSHOT')
@Grab(group='org.orbisgis.data', module='postgis', version='2.0.0-SNAPSHOT')
@Grab(group='org.orbisgis', module='process', version='2.0.0-SNAPSHOT')
```

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code 
of conduct, and the process for submitting pull requests.

## License

This project is licensed under the LGPL v3 License - see the 
[LICENSE.md](../LICENSE.md) file for details

##  Funding

OrbisData is developed within the following frameworks :


* PAENDORA (Planification, Adaptation et Energie des DOnnées
 territoriales et Accompagnement, 2017-2020), ADEME Programme: 
 MODEVAL-URBA 2017

* URCLIM  (URban CLIMate services, 2017-2020), 
[JPI Climate Programme](http://www.jpi-climate.eu/nl/25223460-URCLIM.html)

* ANR [CENSE](http://www.agence-nationale-recherche.fr/Projet-ANR-16-CE22-0012) 
(Caractérisation des environnements sonores urbains : vers une approche 
globale associant données libres, mesures et modélisations, 2016 -2020)

