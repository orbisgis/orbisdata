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




# Funding

Orbisdata is developed within the following frameworks :


* PAENDORA (Planification, Adaptation et Energie des DOnnées territoriales et Accompagnement, 2017-2020), ADEME Programme: MODEVAL-URBA 2017

* URCLIM  (URban CLIMate services, 2017-2020), JPI Climate Programme (http://www.jpi-climate.eu/nl/25223460-URCLIM.html)

* ANR CENSE (Caractérisation des environnements sonores urbains : vers une approche globale associant données libres, mesures et modélisations, 2016 -2020) (http://www.agence-nationale-recherche.fr/Projet-ANR-16-CE22-0012)

