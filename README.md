# OrbisData
Orbisdata is library to provide a unique access point to query, manage, retrieve data.
Orbisdata is based on lambda expressions and sugar programming methods introduced since JAVA 8.
Orbisdata is closed to Groovy syntax and aims to provide an elegant and fluent framework to play with geospatial data.

**IMPORTANT :  OrbisData needs JAVA 11.**

# Architecture

OrbisData is organized around a set of interfaces that must be implemented to access and store data in a range of formats including flat file, databases, remote service...

IDataSource : Any data source. Allows to obtain a dataset from an identifier.

IDataSet : Any kind of raw data. A dataset is an iterable GroovyObject.

ITable : A 2D (column/row) representation of data that implements IDataSet. 

ISpatialTable : A 2D (column/row) representation of data that contains at least one Geometry column. 

IJdbcDataSource : Extension of the IDataSource interface dedicated to the usage of a JDBC database.


# H2GIS datasource

Allows to open, create an H2GIS database. The main entry is the H2GIS class that offers methods to get a Table or a SpatialTable.

**Groovy examples :** 


```groovy
// Connect to a file data, if the database doesn't exist a new one is created
H2GIS.open([databaseName: './target/loadH2GIS'])
```

```groovy
//How to query a spatial table
def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis;
                CREATE TABLE h2gis (id int, the_geom point);
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
def concat = ""
h2GIS.getSpatialTable "h2gis" eachRow { row -> concat += "$row.id $row.the_geom\n" }
println concat
//Returns 1 POINT (10 10) 2 POINT (1 1)
```
```groovy
//How to display the metadata of a spatial table
def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis;
                CREATE TABLE h2gis (id int, the_geom point);
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)

def concat = ""
h2GIS.getSpatialTable("h2gis").meta.each {row -> concat += "$row.columnLabel $row.columnType\n"}
println concat
//Returns ID 4 THE_GEOM 1111

```

# Funding

Orbisdata is developed within the following frameworks :


* PAENDORA (Planification, Adaptation et Energie des DOnnées territoriales et Accompagnement, 2017-2020), ADEME Programme: MODEVAL-URBA 2017

* URCLIM  (URban CLIMate services, 2017-2020), JPI Climate Programme (http://www.jpi-climate.eu/nl/25223460-URCLIM.html)

* ANR CENSE (Caractérisation des environnements sonores urbains : vers une approche globale associant données libres, mesures et modélisations, 2016 -2020) (http://www.agence-nationale-recherche.fr/Projet-ANR-16-CE22-0012)

