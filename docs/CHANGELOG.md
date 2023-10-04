## Changelog for v2.1.0

- Update H2GIS to 2.2.0
- Fix getTable method when the user set a subquery
eg h2GIS.getSpatialTable("h2gis").columns("land", "st_precisionreducer(st_transform(the_geom, 4326), 3) as the_geom")
  .filter("limit 1").getSpatialTable()
- Add methods on datasource to create, drop indexes, drop table, drop columns
- Update H2GIS to 2.2.1-SNAPSHOT
- Add row count method on datasource
- Add method to create spatial index on first geometry column
- Filter null or empty table name before executing the drop table command 
- Add getExtent method on datasource
- Fix isEmpty when the table doesn't have any columns
- Update H2 to 2.2.224 and fix PostGIS tests
- Set SLF4J-SIMPLE as scope test
- Update to groovy 3.0.19
- Add a print method to display the content of a table