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
+ Add get*Type*() methods on `ITable` interface