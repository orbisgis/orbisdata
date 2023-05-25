## Changelog for v2.1.0

- Update H2GIS to 2.2.0
- Fix getTable method when the user set a subquery
eg h2GIS.getSpatialTable("h2gis").columns("land", "st_precisionreducer(st_transform(the_geom, 4326), 3) as the_geom")
  .filter("limit 1").getSpatialTable()
- Add methods on datasource to create, drop indexes, drop table, drop columns
- Update H2GIS to 2.2.1-SNAPSHOT
- Add row count method on datasource
