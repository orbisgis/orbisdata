# Changelog for v1.0.1

+ Add docs/CHANGELOG.md file
+ IJdbcDataSource now implements Java DataSource
+ Add Raster connection point
+ Add the IMatrix interface for multidimensional structured data
+ Rename `getColumns()` into `getColumnsType()` and `getColumnsName()` into `getColumns()`
+ Rename `getColumnsType(String)` into `getColumnType(String)`
+ Add a new module named `dataframe` for statistical analysis based on the [smile](https://haifengl.github.io/) project
+ Wrap the `smile.data.DataFrame` class into `DataFrame` which also implements `ITable` interface and which is 
compatible with `Geometry` data type
+ The interfaces `IDataSet`, `IMatrix`, `ITable` have a generic type which is the type of their `Iterator` (done to 
make it compatible with `smile` API)
+ Change the license to LGPL 3
+ Add the interface `IJdbcSpatialTable`.
+ Make the interface/classes overrides parents method return type in order to return their corresponding types.
+ Fix bug on DSL built tables.
+ Add a method `getSummary()` on the `IDataSet` interface.
+ Add the `ProgressMonitor` mechanism.
+ Add get*Type*() methods on `ITable` interface.
+ Merge `createSpatialIndex` in `createIndex()`.
+ Rename `getShape()` into `getSize()`.
+ Add `next()` methods on `ITable` interface.
+ Move  `link`, `load`, `save` methods from `IJdbcDataSource` to `IDataSource`.
+ Add `previous()`, `first()`, `last()`, `isFirst()`, `isLast()` methods to `ITable`.
+ Add `IDataSet<?> filter(String filter)` method to `IDataSet`.
+ Add `stream()` method to `IDataSet`.
+ Make `IDataSet` have two value parameter : one for the iterator, a second for the stream.
+ Improve `ResultSet` type detection.
+ Add `getColumnNames(String)` method to `IJdbcDataSource`.
+ Add `firstRow()` method to `ITable`.
+ Add `setSrid(int)` method to `ISpatialTable` and to `IColumn`.
+ Add to `H2GIS` and `POSTGIS` an `open(java.sql.DataSource)` method.
+ Add to `DataFrame` the method `DataFrame apply(Formula)`.
+ Add to `JdbcDataSource` the méthods `Collection<String> getTableNames(...)` with multiple signatures.
+ Change the request building from a `ITable`, only use `columns(String...)` (optional) followed by `filter(String)` (optional) to make request.
  A sub request can be made by calling `getTable()` after calling again `columns(String...)` and `filter(String)`.
+ Use H2 2.0.206
+ Upgrade H2 to 2.1.214 and Groovy to 3.0.11