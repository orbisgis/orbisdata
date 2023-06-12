/*
 * Bundle DataManager API is part of the OrbisGIS platform
 *
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 *
 * DataManager API is distributed under LGPL 3 license.
 *
 * Copyright (C) 2019-2020 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * DataManager API is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * DataManager API is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * DataManager API. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.data.api.datasource;

import groovy.lang.GString;
import groovy.lang.GroovyObject;
import groovy.lang.MissingMethodException;
import org.h2gis.utilities.dbtypes.DBTypes;
import org.orbisgis.data.api.dataset.IDataSet;
import org.orbisgis.data.api.dataset.IJdbcSpatialTable;
import org.orbisgis.data.api.dataset.IJdbcTable;
import org.orbisgis.data.api.dataset.ISpatialTable;

import javax.sql.DataSource;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Extension of the {@link IDataSource} interface dedicated to the usage of a JDBC database as a data source.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (Lab-STICC UBS 2018-2019 / Chaire GEOTERA 2020)
 */
public interface IJdbcDataSource extends IDataSource<ResultSet>, GroovyObject, DataSource {

    enum TableType{
        TABLE, VIEW, FOREIGN_TABLE, TEMPORARY, TABLE_LINK, UNKOWN, SYSTEM_TABLE;

        public String toString() {
            return super.toString().replaceAll("_", " ");
        }
    }

    /**
     * Close the underlying database.
     */
    void close();

    /**
     * Return the {@link IJdbcTable} contained by the database with the given name. If the table contains a geometric
     * field, return a {@link ISpatialTable}.
     *
     * @param nameOrQuery Name of the requested table.
     * @return The {@link IJdbcTable} with the given name or null if no table is found.
     */
    IJdbcTable getTable(String nameOrQuery);

    /**
     * Return the {@link IJdbcTable} contained by the database with the given name. If the table contains a geometric
     * field, return a {@link ISpatialTable}.
     *
     * @param nameOrQuery Name of the requested table.
     * @return The {@link IJdbcTable} with the given name or null if no table is found.
     */
    IJdbcTable getTable(GString nameOrQuery);

    /**
     * Return the {@link IJdbcTable} contained by the database with the given name. If the table contains a geometric
     * field, return a {@link ISpatialTable}.
     *
     * @param nameOrQuery Name of the requested table.
     * @return The {@link IJdbcTable} with the given name or null if no table is found.
     */
    IJdbcTable getTable(String nameOrQuery, List<Object> params);

    /**
     * Return the {@link IJdbcTable} contained by the database with the given name. If the table contains a geometric
     * field, return a {@link ISpatialTable}.
     *
     * @param nameOrQuery Name of the requested table.
     * @param statement {@link Statement} to use.
     * @return The {@link IJdbcTable} with the given name or null if no table is found.
     */
    IJdbcTable getTable(String nameOrQuery, Statement statement);

    /**
     * Return the {@link IJdbcTable} contained by the database with the given name. If the table contains a geometric
     * field, return a {@link ISpatialTable}.
     *
     * @param nameOrQuery Name of the requested table.
     * @param statement {@link Statement} to use.
     * @return The {@link IJdbcTable} with the given name or null if no table is found.
     */
    IJdbcTable getTable(GString nameOrQuery, Statement statement);

    /**
     * Return the {@link IJdbcTable} contained by the database with the given name. If the table contains a geometric
     * field, return a {@link ISpatialTable}.
     *
     * @param nameOrQuery   Name of the requested table.
     * @param params        Parameters to use with the {@link PreparedStatement}.
     * @param statement {@link Statement} to use.
     * @return The {@link IJdbcTable} with the given name or null if no table is found.
     */
    IJdbcTable getTable(String nameOrQuery,List<Object> params, Statement statement);

    /**
     * Return a {@link ISpatialTable} contained by the database with the given name. If the table doesn't contains a
     * geometric field, return null;
     *
     * @param nameOrQuery Name of the requested table.
     * @return The {@link ISpatialTable} with the given name or null if no table is found or if the table doesn't
     * contains a geometric field.
     */
    IJdbcSpatialTable getSpatialTable(String nameOrQuery);

    /**
     * Return a {@link ISpatialTable} contained by the database with the given name. If the table doesn't contains a
     * geometric field, return null;
     *
     * @param nameOrQuery Name of the requested table.
     * @return The {@link ISpatialTable} with the given name or null if no table is found or if the table doesn't
     * contains a geometric field.
     */
    IJdbcSpatialTable getSpatialTable(GString nameOrQuery);

    /**
     * Return a {@link ISpatialTable} contained by the database with the given name. If the table doesn't contains a
     * geometric field, return null;
     *
     * @param nameOrQuery Name of the requested table.
     * @return The {@link ISpatialTable} with the given name or null if no table is found or if the table doesn't
     * contains a geometric field.
     */
    IJdbcSpatialTable getSpatialTable(String nameOrQuery, List<Object> params);

    /**
     * Return a {@link ISpatialTable} contained by the database with the given name. If the table doesn't contains a
     * geometric field, return null;
     *
     * @param nameOrQuery Name of the requested table.
     * @param statement {@link Statement} to use.
     * @return The {@link ISpatialTable} with the given name or null if no table is found or if the table doesn't
     * contains a geometric field.
     */
    IJdbcSpatialTable getSpatialTable(String nameOrQuery, Statement statement);

    /**
     * Return a {@link ISpatialTable} contained by the database with the given name. If the table doesn't contains a
     * geometric field, return null;
     *
     * @param nameOrQuery Name of the requested table.
     * @param statement {@link Statement} to use.
     * @return The {@link ISpatialTable} with the given name or null if no table is found or if the table doesn't
     * contains a geometric field.
     */
    IJdbcSpatialTable getSpatialTable(GString nameOrQuery, Statement statement);

    /**
     * Return the {@link IJdbcTable} contained by the database with the given name. If the table contains a geometric
     * field, return a {@link ISpatialTable}.
     *
     * @param nameOrQuery Name of the requested table.
     * @param params      Parameters to use with the {@link PreparedStatement}.
     * @param statement   {@link Statement} to use.
     * @return The {@link IJdbcTable} with the given name or null if no table is found.
     */
    IJdbcSpatialTable getSpatialTable(String nameOrQuery,List<Object> params, Statement statement);

    /**
     * Get all table names from the underlying database.
     *
     * @return A {@link Collection} containing the names of all the available tables.
     */
    Collection<String> getTableNames();

    /**
     * Return the list of the table name corresponding to the given patterns and types.
     *
     * @param namePattern Pattern of the table name.
     * @return            List of the table corresponding to the given patterns and types.
     */
    Collection<String> getTableNames(String namePattern);

    /**
     * Return the list of the table name corresponding to the given patterns and types.
     *
     * @param namePattern Pattern of the table name.
     * @param types       Type of the table.
     * @return            List of the table corresponding to the given patterns and types.
     */
    Collection<String> getTableNames(String namePattern,TableType... types);

    /**
     * Return the list of the table name corresponding to the given patterns and types.
     *
     * @param schemaPattern Pattern of the schema name.
     * @param namePattern   Pattern of the table name.
     * @return              List of the table corresponding to the given patterns and types.
     */
    Collection<String> getTableNames(String schemaPattern,String namePattern);

    /**
     * Return the list of the table name corresponding to the given patterns and types.
     *
     * @param schemaPattern Pattern of the schema name.
     * @param namePattern   Pattern of the table name.
     * @param types         Type of the table.
     * @return              List of the table corresponding to the given patterns and types.
     */
    Collection<String> getTableNames(String schemaPattern,String namePattern,
                                    TableType... types);

    /**
     * Return the list of the table name corresponding to the given patterns and types.
     *
     * @param catalogPattern Pattern of the catalog name.
     * @param schemaPattern  Pattern of the schema name.
     * @param namePattern    Pattern of the table name.
     * @return               List of the table corresponding to the given patterns and types.
     */
    Collection<String> getTableNames(String catalogPattern,String schemaPattern,
                                    String namePattern);

    /**
     * Return the list of the table name corresponding to the given patterns and types.
     *
     * @param catalogPattern Pattern of the catalog name.
     * @param schemaPattern  Pattern of the schema name.
     * @param namePattern    Pattern of the table name.
     * @param types          Type of the table.
     * @return               List of the table corresponding to the given patterns and types.
     */
    Collection<String> getTableNames(String catalogPattern,String schemaPattern,
                                    String namePattern,TableType... types);

    /**
     * Return true if the {@link IJdbcDataSource} contains a table with the given name.
     *
     * @param tableName Name of the table to check.
     * @return True if {@link IJdbcTable} is found, false otherwise.
     */
    boolean hasTable(String tableName);

    /**
     * Returns the names of the column of the given table.
     *
     * @param location Location of the table with the pattern : [[catalog.]schema.]table
     * @return The names of the column of the given table.
     */
    Collection<String> getColumnNames(String location);


    /**
     * Create a spatial index on the column. If the column already has an index, no new index is created.
     * @param tableName name of the table
     * @param columnName name of the column
     * @return
     */
    boolean createSpatialIndex(String tableName, String columnName);


    /**
     * Create a spatial index on the first geometry column.
     * @param tableName name of the table
     * @return
     */
    boolean createSpatialIndex(String tableName);

    /**
     * Create an index of the column. If the column already has an index, no new index is created.
     * @param tableName name of the table
     * @param columnName name of the column
     * @return
     */
    boolean createIndex(String tableName, String columnName);


    /**
     * Return true if the table has a geometry column.
     *
     * @param tableName name of the table
     * @return True if the table has a geometry column.
     */
    boolean hasGeometryColumn(String tableName);

    /**
     * Return a list of geometry column names.
     *
     * @param tableName name of the table
     * @return a list of geometry column names.
     */
    List<String> getGeometryColumns(String tableName);

    /**
     * Return the first geometry column name.
     *
     * @param tableName name of the table
     * @return the first geometry column name.
     */
    String getGeometryColumn(String tableName);

    /**
     * Return true if the column from the table has an index, false otherwise.
     *
     * @param tableName name of the table
     * @param columnName name of the column
     * @return True if the column has an index, false otherwise.
     */
    boolean isIndexed(String tableName, String columnName);

    /**
     * Return true if the column from the table has a spatial index, false otherwise.
     *
     * @param tableName name of the table
     * @param columnName name of the column
     * @return True if the column has a spatial index, false otherwise.
     */
    boolean isSpatialIndexed(String tableName, String columnName);

    /**
     * Return true if the first geometry column from the table has a spatial index, false otherwise.
     *
     * @param tableName name of the table
     * @return True if the column has a spatial index, false otherwise.
     */
    boolean isSpatialIndexed(String tableName);


    /**
     * Drop the index of the column from the table if exists.
     *
     * @param tableName name of the table
     * @param columnName name of the column
     */
    void dropIndex(String tableName, String columnName);


    /**
     * Drop the tables if exists.
     *
     * @param tableName name of the table
     */
    void dropTable(String... tableName);


    /**
     * Drop the tables if exists.
     *
     * @param tableNames name of the tables
     */
    void dropTable(List tableNames);

    /**
     * Drop the column if the table exists.
     *
     * @param tableName name of the table
     * @param columnName name of the columns
     */
    void dropColumn(String tableName, String... columnName);


    /**
     * Drop the column if the table exists.
     *
     * @param tableName name of the table
     * @param columnNames name of the columns
     */
    void dropColumn(String tableName, List columnNames);

    /**
     * Sets a new SRID code to the column of the table.
     *
     * @param tableName name of the table
     * @param columnName name of the column
     * @param srid The SRID code of the column.
     */
    boolean setSrid(String tableName, String columnName, int srid);


    /**
     * Sets a new SRID code on the first geometry column.
     * If there is no geometry, the method does nothing.
     *
     * @param tableName name of the table
     * @param srid The SRID code of the column.
     */
    boolean setSrid(String tableName, int srid);


    /**
     * Return the SRID code of the first geometry column of the tableName.
     *
     * @return The SRID code of the first geometry column of the tableName.
     */
    int getSrid(String tableName);


    /**
     * Return the SRID code of a geometry column of the tableName.
     *
     * @return The SRID code of the geometry column of the tableName.
     */
    int getSrid(String tableName, String columnName);

     * Return the count of lines or -1 if not able to find the given table.
     *
     * @return The count of lines or -1 if not able to find the given table.
     */
    long getRowCount(String tableName);



    /* ********************** */
    /*      Load methods      */
    /* ********************** */

    /**
     * Load a file into the {@link IDataSource}.
     *
     * @param filePath Path of the file.
     * @return The name of the loaded table, formatted according this datasource
     * Null is the table cannot be loaded.
     */
    String load(String filePath);

    /**
     * Load a file into the {@link IDataSource}.
     *
     * @param filePath Path of the file.
     * @param delete   True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The name of the loaded table, formatted according this datasource
     * Null is the table cannot be loaded.
     */
    String load(String filePath, boolean delete);

    /**
     * Load a file to the {@link IDataSource}.
     *
     * @param filePath  Path of the file.
     * @param dataSetId Identifier of the {@link IDataSet}.
     * @return The name of the loaded table, formatted according this datasource
     * Null is the table cannot be loaded.
     */
    String load(String filePath, String dataSetId);

    /**
     * Load a file to the {@link IDataSource}.
     *
     * @param filePath  Path of the file.
     * @param dataSetId Identifier of the {@link IDataSet}.
     * @param delete    True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The name of the loaded table, formatted according this datasource
     * Null is the table cannot be loaded.
     */
    String load(String filePath, String dataSetId, boolean delete);

    /**
     * Load a file to the {@link IDataSource}.
     *
     * @param filePath  Path of the file.
     * @param dataSetId Identifier of the {@link IDataSet}.
     * @param encoding  Encoding of the loaded file.
     * @param delete    True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The name of the loaded table, formatted according this datasource
     * Null is the table cannot be loaded.
     */
    String load(String filePath, String dataSetId,String encoding, boolean delete);

    /**
     * Load a file into the {@link IDataSource}.
     *
     * @param url {@link URL} of the file.
     * @return The name of the loaded table, formatted according this datasource
     * Null is the table cannot be loaded.
     */
    String load(URL url);

    /**
     * Load a file into the {@link IDataSource}.
     *
     * @param url    {@link URL} of the file.
     * @param delete True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The name of the loaded table, formatted according this datasource
     * Null is the table cannot be loaded.
     */
    String load(URL url, boolean delete);

    /**
     * Load a file to the {@link IDataSource}.
     *
     * @param url       {@link URL} of the file.
     * @param dataSetId Name of the table.
     * @return The name of the loaded table, formatted according this datasource
     * Null is the table cannot be loaded.
     */
    String load(URL url, String dataSetId);

    /**
     * Load a file to the {@link IDataSource}.
     *
     * @param url       {@link URL} of the file.
     * @param dataSetId Name of the table.
     * @param delete    True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The name of the loaded table, formatted according this datasource
     * Null is the table cannot be loaded.
     */
    String load(URL url, String dataSetId, boolean delete);

    /**
     * Load a file to the {@link IDataSource}.
     *
     * @param url       {@link URL} of the file.
     * @param dataSetId Name of the table
     * @param encoding  Encoding of the loaded file.
     * @param delete    True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The name of the loaded table, formatted according this datasource
     * Null is the table cannot be loaded.
     */
    String load(URL url, String dataSetId,String encoding, boolean delete);

    /**
     * Load a file into the {@link IDataSource}.
     *
     * @param uri {@link URI} of the file.
     * @return The name of the loaded table, formatted according this datasource
     * Null is the table cannot be loaded.
     */
    String load(URI uri);

    /**
     * Load a file into the {@link IDataSource}.
     *
     * @param uri    {@link URI} of the file.
     * @param delete True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The name of the loaded table, formatted according this datasource
     * Null is the table cannot be loaded.
     */
    String load(URI uri, boolean delete);

    /**
     * Load a file to the {@link IDataSource}.
     *
     * @param uri       {@link URI} of the file.
     * @param dataSetId Name of the table.
     * @return The name of the loaded table, formatted according this datasource
     * Null is the table cannot be loaded.
     */
    String load(URI uri, String dataSetId);

    /**
     * Load a file to the {@link IDataSource}.
     *
     * @param uri       {@link URI} of the file.
     * @param dataSetId Name of the table.
     * @param delete    True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The name of the loaded table, formatted according this datasource
     * Null is the table cannot be loaded.
     */
    String load(URI uri, String dataSetId, boolean delete);

    /**
     * Load a file to the {@link IDataSource}.
     *
     * @param uri       {@link URI} of the file.
     * @param dataSetId Name of the table
     * @param encoding  Encoding of the loaded file.
     * @param delete    True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The name of the loaded table, formatted according this datasource
     * Null is the table cannot be loaded.
     */
    String load(URI uri, String dataSetId,String encoding, boolean delete);

    /**
     * Load a file into the {@link IDataSource}.
     *
     * @param file {@link File}.
     * @return The name of the loaded table, formatted according this datasource
     * Null is the table cannot be loaded.
     */
    String load(File file);

    /**
     * Load a file into the {@link IDataSource}.
     *
     * @param file   {@link File}.
     * @param delete True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The name of the loaded table, formatted according this datasource
     * Null is the table cannot be loaded.
     */
    String load(File file, boolean delete);

    /**
     * Load a file to the {@link IDataSource}.
     *
     * @param file      {@link File}.
     * @param dataSetId Name of the table.
     * @return The name of the loaded table, formatted according this datasource
     * Null is the table cannot be loaded.
     */
    String load(File file, String dataSetId);

    /**
     * Load a file to the {@link IDataSource}.
     *
     * @param file      {@link File}.
     * @param dataSetId Name of the table.
     * @param delete    True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The name of the loaded table, formatted according this datasource
     * Null is the table cannot be loaded.
     */
    String load(File file, String dataSetId, boolean delete);

    /**
     * Load a file to the {@link IDataSource}.
     *
     * @param file      {@link File}.
     * @param dataSetId Name of the table
     * @param encoding  Encoding of the loaded file.
     * @param delete    True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The name of the loaded table, formatted according this datasource
     * Null is the table cannot be loaded.
     */
    String load(File file, String dataSetId,String encoding, boolean delete);

    /**
     * Load a table from another {@link IDataSource}.
     *
     * @param dataSource      DataSource reference to the input database
     * @param inputTableName  Name of the table to import.
     * @param deleteIfExists  True to delete the outputTableName if exists, false otherwise.
     * @return The name of the loaded table, formatted according this datasource
     * Null is the table cannot be loaded.
     */
    String load(IJdbcDataSource dataSource, String inputTableName,
                boolean deleteIfExists);

    /**
     * Load a table from another {@link IDataSource}.
     *
     * @param dataSource      DataSource reference to the input database
     * @param inputTableName  Name of the table to import.
     * @param outputTableName Name of the imported table in the database.
     * @return The name of the loaded table, formatted according this datasource
     * Null is the table cannot be loaded.
     */
    String load(IJdbcDataSource dataSource, String inputTableName,
                String outputTableName);

    /**
     * Load a table from another {@link IDataSource}.
     *
     * @param dataSource      DataSource reference to the input database
     * @param inputTableName  Name of the table to import.
     * @return The name of the loaded table, formatted according this datasource
     * Null is the table cannot be loaded.
     */
    String load(IJdbcDataSource dataSource, String inputTableName);

    /**
     * Load a table from another {@link IDataSource}.
     *
     * @param dataSource      DataSource reference to the input database
     * @param inputTableName  Name of the table to import.
     * @param outputTableName Name of the imported table in the database.
     * @param deleteIfExists  True to delete the outputTableName if exists, false otherwise.
     * @return The name of the loaded table, formatted according this datasource
     * Null is the table cannot be loaded.
     */
    String load(IJdbcDataSource dataSource, String inputTableName,
                String outputTableName, boolean deleteIfExists);

    /**
     * Load a table from another {@link IDataSource}.
     *
     * @param dataSource      DataSource reference to the input database
     * @param inputTableName  Name of the table to import.
     * @param outputTableName Name of the imported table in the database.
     * @param deleteIfExists  True to delete the outputTableName if exists, false otherwise.
     * @param batchSize       Integer value to queue the data before executing the query
     * @return The name of the loaded table, formatted according this datasource
     * Null is the table cannot be loaded.
     */
    String load(IJdbcDataSource dataSource, String inputTableName,
                String outputTableName, boolean deleteIfExists, int batchSize);

    /* ********************** */
    /*      Save methods      */
    /* ********************** */

    /**
     * Save a table into a file.
     *
     * @param dataSetId Name of the table to save.
     * @param filePath  Path of the file where the table will be saved.
     * @return True if the file has been successfully saved, false otherwise.
     */
    boolean save(String dataSetId, String filePath);


    /**
     * Save a table into a file.
     *
     * @param dataSetId Name of the table to save.
     * @param filePath  Path of the file where the table will be saved.
     * @param delete true to delete the file if exists
     * @return True if the file has been successfully saved, false otherwise.
     */
    boolean save(String dataSetId, String filePath, boolean delete);

    /**
     * Save a table into a file.
     *
     * @param dataSetId Name of the table to save.
     * @param filePath  Path of the file where the table will be saved.
     * @param encoding  Encoding of the file.
     * @return True if the file has been successfully saved, false otherwise.
     */
    boolean save(String dataSetId, String filePath,String encoding);

    /**
     * Save a table into a file.
     *
     * @param dataSetId Name of the table to save.
     * @param uri       {@link URI} of the file where the table will be saved.
     * @return True if the file has been successfully saved, false otherwise.
     */
    boolean save(String dataSetId, URI uri);

    /**
     * Save a table into a file.
     *
     * @param dataSetId Name of the table to save.
     * @param uri       {@link URI} of the file where the table will be saved.
     * @param encoding  Encoding of the file.
     * @return True if the file has been successfully saved, false otherwise.
     */
    boolean save(String dataSetId, URI uri,String encoding);

    /**
     * Save a table into a file.
     *
     * @param dataSetId Name of the table to save.
     * @param url       {@link URL} of the file where the table will be saved.
     * @return True if the file has been successfully saved, false otherwise.
     */
    boolean save(String dataSetId, URL url);

    /**
     * Save a table into a file.
     *
     * @param dataSetId Name of the table to save.
     * @param url       {@link URL} of the file where the table will be saved.
     * @param encoding  Encoding of the file.
     * @return True if the file has been successfully saved, false otherwise.
     */
    boolean save(String dataSetId, URL url,String encoding);

    /**
     * Save a table into a file.
     *
     * @param dataSetId Name of the table to save.
     * @param file      {@link File} of the file where the table will be saved.
     * @return True if the file has been successfully saved, false otherwise.
     */
    boolean save(String dataSetId, File file);

    /**
     * Save a table into a file.
     *
     * @param dataSetId Name of the table to save.
     * @param file      {@link File} of the file where the table will be saved.
     * @param encoding  Encoding of the file.
     * @return True if the file has been successfully saved, false otherwise.
     */
    boolean save(String dataSetId, File file,String encoding);

    /**
     * This method is used to execute a SQL file
     *
     * @param fileName The sql file
     * @return True if the script has been successfully run, false otherwise.
     */
    default boolean executeScript(String fileName) {
        return executeScript(fileName, null);
    }

    /**
     * This method is used to execute a SQL file that contains parametrized text
     * Parametrized text must be expressed with $value or ${value}
     *
     * @param fileName The sql file
     * @param bindings The map between parametrized text and its value. eg.
     *                 ["value", "myvalue"] to replace ${value} by myvalue
     * @return True if the script has been successfully run, false otherwise.
     */
    boolean executeScript(String fileName,Map<String, String> bindings);

    /**
     * This method is used to execute a SQL script
     *
     * @param stream Input stream of the sql file
     * @return True if the script has been successfully run, false otherwise.
     */
    default boolean executeScript(InputStream stream) {
        return executeScript(stream, null);
    }

    /**
     * This method is used to execute a SQL file that contains parametrized text
     * Parametrized text must be expressed with $value or ${value}
     *
     * @param stream   Input stream of the sql file
     * @param bindings The map between parametrized text and its value. eg.
     *                 ["value", "myvalue"] to replace ${value} by myvalue
     * @return True if the script has been successfully run, false otherwise.
     */
    boolean executeScript(InputStream stream,Map<String, String> bindings);

    /**
     * Return the type of the database.
     *
     * @return The type of the database.
     */
    DBTypes getDataBaseType();

    @Override
    IJdbcTable getDataSet(String name);

    @Override
    default Object invokeMethod(String name, Object args) {
        try {
            return getMetaClass().invokeMethod(this, name, args);
        } catch (MissingMethodException e) {
            //LOGGER.debug("Unable to find the '"+name+"' methods, trying with the getter");
            return getMetaClass()
                    .invokeMethod(this, "get" + name.substring(0, 1).toUpperCase() + name.substring(1), args);
        }
    }

    @Override
    default Object getProperty(String propertyName) {
        if (propertyName == null) {
            //LOGGER.error("Trying to get null property name.");
            return null;
        }
        return getMetaClass().getProperty(this, propertyName);
    }

    @Override
    default void setProperty(String propertyName, Object newValue) {
        getMetaClass().setProperty(this, propertyName, newValue);
    }

    /**
     * Get the parametrized query parameters from the given {@link GString}.
     *
     * @param gString {@link GString} parametrized query.
     * @return List of the parameters of the query.
     */
    List<Object> getParameters(GString gString);

    /**
     * Get the parametrized SQL query from the given GString and its parameters.
     *
     * @param gString {@link GString} parametrized query.
     * @param params  List of the parameters of the query.
     * @return String SQL query.
     */
    String asSql(GString gString, List<Object> params);


    /**
     * Enables auto-commit mode, which means that each statement is once again
     * committed automatically when it is completed.
     * @param autoCommit false to disable auto-commit mode
     */
    IJdbcDataSource autoCommit(boolean autoCommit);

    /**
     * Convert the type name to a java class using a lookup map
     * @param typeName
     * @return
     */
     Class<?> typeNameToClass(String typeName);

    /**
     * Link a table from an external database.
     *
     * @param dataSourceProperties {@link IJdbcDataSource}.
     * @param sourceTableName name of the external table
     * @return The name of the linked table
     */
    String link(Map dataSourceProperties, String sourceTableName);


    /**
     * Link a table from an external database.
     *
     * @param dataSourceProperties {@link IJdbcDataSource}.
     * @param sourceTableName name of the external table
     * @param delete true to delete the target table if exists
     * @return The name of the linked table
     */
    String link(Map dataSourceProperties, String sourceTableName, boolean delete);

    /**
     * Link a table from an external database.
     *
     * @param dataSourceProperties {@link IJdbcDataSource}.
     * @param sourceTableName name of the external table
     * @param targetTableName name of the target table
     * @param delete true to delete the target table if exists
     * @return The name of the linked table
     */
    String link(Map dataSourceProperties, String sourceTableName, String targetTableName, boolean delete);

    /* ********************** */
    /*      Link methods      */
    /* ********************** */

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param filePath  Path of the file.
     * @param dataSetId Identifier of the {@link IDataSet}.
     * @param delete    True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The name of the linked table, formatted according this datasource
     * Null is the table cannot be linked.
     */
    String link(String filePath, String dataSetId, boolean delete);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param filePath  Path of the file.
     * @param dataSetId Identifier of the {@link IDataSet}.
     * @return The name of the linked table, formatted according this datasource
     * Null is the table cannot be linked.
     */
    String link(String filePath, String dataSetId);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param filePath Path of the file.
     * @param delete   True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The name of the linked table, formatted according this datasource
     * Null is the table cannot be linked.
     */
    String link(String filePath, boolean delete);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param filePath Path of the file to link.
     * @return The name of the linked table, formatted according this datasource
     * Null is the table cannot be linked.
     */
    String link(String filePath);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param uri       {@link URI} of the file.
     * @param dataSetId Identifier of the {@link IDataSet}.
     * @param delete    True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The name of the linked table, formatted according this datasource
     * Null is the table cannot be linked.
     */
    String link(URI uri,String dataSetId, boolean delete);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param uri       {@link URI} of the file.
     * @param dataSetId Identifier of the {@link IDataSet}.
     * @return The name of the linked table, formatted according this datasource
     * Null is the table cannot be linked.
     */
    String link(URI uri,String dataSetId);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param uri    {@link URI} of the file.
     * @param delete True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The name of the linked table, formatted according this datasource
     * Null is the table cannot be linked.
     */
    String link(URI uri, boolean delete);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param uri {@link URI} of the file.
     * @return The name of the linked table, formatted according this datasource
     * Null is the table cannot be linked.
     */
    String link(URI uri);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param url       {@link URL} of the file.
     * @param dataSetId Identifier of the {@link IDataSet}.
     * @param delete    True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The name of the linked table, formatted according this datasource
     * Null is the table cannot be linked.
     */
    String link(URL url,String dataSetId, boolean delete);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param url       {@link URI} of the file.
     * @param dataSetId Identifier of the {@link IDataSet}.
     * @return The name of the linked table, formatted according this datasource
     * Null is the table cannot be linked.
     */
    String link(URL url,String dataSetId);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param url    {@link URI} of the file.
     * @param delete True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The name of the linked table, formatted according this datasource
     * Null is the table cannot be linked.
     */
    String link(URL url, boolean delete);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param url {@link URI} of the file.
     * @return The name of the linked table, formatted according this datasource
     * Null is the table cannot be linked.
     */
    String link(URL url);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param file      {@link File}.
     * @param dataSetId Identifier of the {@link IDataSet}.
     * @param delete    True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The name of the linked table, formatted according this datasource
     * Null is the table cannot be linked.
     */
    String link(File file, String dataSetId, boolean delete);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param file      {@link File}.
     * @param dataSetId Identifier of the {@link IDataSet}.
     * @return The name of the linked table, formatted according this datasource
     * Null is the table cannot be linked.
     */
    String link(File file, String dataSetId);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param file   {@link File}.
     * @param delete True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The name of the linked table, formatted according this datasource
     * Null is the table cannot be linked.
     */
    String link(File file, boolean delete);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param file {@link File}.
     * @return The name of the linked table, formatted according this datasource
     * Null is the table cannot be linked.
     */
    String link(File file);

    /**
     * Return true if the tableName is empty, false otherwise.
     *
     * @return True if the tableName is empty, false otherwise.
     */
    boolean isEmpty(String tableName);
}
