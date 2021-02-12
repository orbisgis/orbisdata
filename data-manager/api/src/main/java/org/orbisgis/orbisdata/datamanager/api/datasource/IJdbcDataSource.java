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
package org.orbisgis.orbisdata.datamanager.api.datasource;

import groovy.lang.GString;
import groovy.lang.GroovyObject;
import groovy.lang.MissingMethodException;
import org.h2gis.utilities.dbtypes.DBTypes;
import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.commons.annotations.Nullable;
import org.orbisgis.orbisdata.datamanager.api.dataset.IDataSet;
import org.orbisgis.orbisdata.datamanager.api.dataset.IJdbcSpatialTable;
import org.orbisgis.orbisdata.datamanager.api.dataset.IJdbcTable;
import org.orbisgis.orbisdata.datamanager.api.dataset.ISpatialTable;

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
    @Nullable
    IJdbcTable getTable(@NotNull String nameOrQuery);

    /**
     * Return the {@link IJdbcTable} contained by the database with the given name. If the table contains a geometric
     * field, return a {@link ISpatialTable}.
     *
     * @param nameOrQuery Name of the requested table.
     * @return The {@link IJdbcTable} with the given name or null if no table is found.
     */
    @Nullable
    IJdbcTable getTable(@NotNull GString nameOrQuery);

    /**
     * Return the {@link IJdbcTable} contained by the database with the given name. If the table contains a geometric
     * field, return a {@link ISpatialTable}.
     *
     * @param nameOrQuery Name of the requested table.
     * @return The {@link IJdbcTable} with the given name or null if no table is found.
     */
    @Nullable
    IJdbcTable getTable(@NotNull String nameOrQuery, List<Object> params);

    /**
     * Return the {@link IJdbcTable} contained by the database with the given name. If the table contains a geometric
     * field, return a {@link ISpatialTable}.
     *
     * @param nameOrQuery Name of the requested table.
     * @param statement {@link Statement} to use.
     * @return The {@link IJdbcTable} with the given name or null if no table is found.
     */
    @Nullable
    IJdbcTable getTable(@NotNull String nameOrQuery, @NotNull Statement statement);

    /**
     * Return the {@link IJdbcTable} contained by the database with the given name. If the table contains a geometric
     * field, return a {@link ISpatialTable}.
     *
     * @param nameOrQuery Name of the requested table.
     * @param statement {@link Statement} to use.
     * @return The {@link IJdbcTable} with the given name or null if no table is found.
     */
    @Nullable
    IJdbcTable getTable(@NotNull GString nameOrQuery, @NotNull Statement statement);

    /**
     * Return the {@link IJdbcTable} contained by the database with the given name. If the table contains a geometric
     * field, return a {@link ISpatialTable}.
     *
     * @param nameOrQuery   Name of the requested table.
     * @param params        Parameters to use with the {@link PreparedStatement}.
     * @param statement {@link Statement} to use.
     * @return The {@link IJdbcTable} with the given name or null if no table is found.
     */
    @Nullable
    IJdbcTable getTable(@NotNull String nameOrQuery, @Nullable List<Object> params, @NotNull Statement statement);

    /**
     * Return a {@link ISpatialTable} contained by the database with the given name. If the table doesn't contains a
     * geometric field, return null;
     *
     * @param nameOrQuery Name of the requested table.
     * @return The {@link ISpatialTable} with the given name or null if no table is found or if the table doesn't
     * contains a geometric field.
     */
    @Nullable
    IJdbcSpatialTable getSpatialTable(@NotNull String nameOrQuery);

    /**
     * Return a {@link ISpatialTable} contained by the database with the given name. If the table doesn't contains a
     * geometric field, return null;
     *
     * @param nameOrQuery Name of the requested table.
     * @return The {@link ISpatialTable} with the given name or null if no table is found or if the table doesn't
     * contains a geometric field.
     */
    @Nullable
    IJdbcSpatialTable getSpatialTable(@NotNull GString nameOrQuery);

    /**
     * Return a {@link ISpatialTable} contained by the database with the given name. If the table doesn't contains a
     * geometric field, return null;
     *
     * @param nameOrQuery Name of the requested table.
     * @return The {@link ISpatialTable} with the given name or null if no table is found or if the table doesn't
     * contains a geometric field.
     */
    @Nullable
    IJdbcSpatialTable getSpatialTable(@NotNull String nameOrQuery, List<Object> params);

    /**
     * Return a {@link ISpatialTable} contained by the database with the given name. If the table doesn't contains a
     * geometric field, return null;
     *
     * @param nameOrQuery Name of the requested table.
     * @param statement {@link Statement} to use.
     * @return The {@link ISpatialTable} with the given name or null if no table is found or if the table doesn't
     * contains a geometric field.
     */
    @Nullable
    IJdbcSpatialTable getSpatialTable(@NotNull String nameOrQuery, @NotNull Statement statement);

    /**
     * Return a {@link ISpatialTable} contained by the database with the given name. If the table doesn't contains a
     * geometric field, return null;
     *
     * @param nameOrQuery Name of the requested table.
     * @param statement {@link Statement} to use.
     * @return The {@link ISpatialTable} with the given name or null if no table is found or if the table doesn't
     * contains a geometric field.
     */
    @Nullable
    IJdbcSpatialTable getSpatialTable(@NotNull GString nameOrQuery, @NotNull Statement statement);

    /**
     * Return the {@link IJdbcTable} contained by the database with the given name. If the table contains a geometric
     * field, return a {@link ISpatialTable}.
     *
     * @param nameOrQuery Name of the requested table.
     * @param params      Parameters to use with the {@link PreparedStatement}.
     * @param statement   {@link Statement} to use.
     * @return The {@link IJdbcTable} with the given name or null if no table is found.
     */
    @Nullable
    IJdbcSpatialTable getSpatialTable(@NotNull String nameOrQuery, @Nullable List<Object> params, @NotNull Statement statement);

    /**
     * Get all table names from the underlying database.
     *
     * @return A {@link Collection} containing the names of all the available tables.
     */
    @NotNull
    Collection<String> getTableNames();

    /**
     * Return the list of the table name corresponding to the given patterns and types.
     *
     * @param namePattern Pattern of the table name.
     * @return            List of the table corresponding to the given patterns and types.
     */
    @NotNull
    Collection<String> getTableNames(@Nullable String namePattern);

    /**
     * Return the list of the table name corresponding to the given patterns and types.
     *
     * @param namePattern Pattern of the table name.
     * @param types       Type of the table.
     * @return            List of the table corresponding to the given patterns and types.
     */
    @NotNull
    Collection<String> getTableNames(@Nullable String namePattern, @Nullable TableType... types);

    /**
     * Return the list of the table name corresponding to the given patterns and types.
     *
     * @param schemaPattern Pattern of the schema name.
     * @param namePattern   Pattern of the table name.
     * @return              List of the table corresponding to the given patterns and types.
     */
    @NotNull
    Collection<String> getTableNames(@Nullable String schemaPattern, @Nullable String namePattern);

    /**
     * Return the list of the table name corresponding to the given patterns and types.
     *
     * @param schemaPattern Pattern of the schema name.
     * @param namePattern   Pattern of the table name.
     * @param types         Type of the table.
     * @return              List of the table corresponding to the given patterns and types.
     */
    @NotNull
    Collection<String> getTableNames(@Nullable String schemaPattern, @Nullable String namePattern,
                                     @Nullable TableType... types);

    /**
     * Return the list of the table name corresponding to the given patterns and types.
     *
     * @param catalogPattern Pattern of the catalog name.
     * @param schemaPattern  Pattern of the schema name.
     * @param namePattern    Pattern of the table name.
     * @return               List of the table corresponding to the given patterns and types.
     */
    @NotNull
    Collection<String> getTableNames(@Nullable String catalogPattern, @Nullable String schemaPattern,
                                     @Nullable String namePattern);

    /**
     * Return the list of the table name corresponding to the given patterns and types.
     *
     * @param catalogPattern Pattern of the catalog name.
     * @param schemaPattern  Pattern of the schema name.
     * @param namePattern    Pattern of the table name.
     * @param types          Type of the table.
     * @return               List of the table corresponding to the given patterns and types.
     */
    @NotNull
    Collection<String> getTableNames(@Nullable String catalogPattern, @Nullable String schemaPattern,
                                     @Nullable String namePattern, @Nullable TableType... types);

    /**
     * Return true if the {@link IJdbcDataSource} contains a table with the given name.
     *
     * @param tableName Name of the table to check.
     * @return True if {@link IJdbcTable} is found, false otherwise.
     */
    boolean hasTable(@NotNull String tableName);

    /**
     * Returns the names of the column of the given table.
     *
     * @param location Location of the table with the pattern : [[catalog.]schema.]table
     * @return The names of the column of the given table.
     */
    @Nullable
    Collection<String> getColumnNames(String location);

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
    @Nullable
    String load(@NotNull String filePath);

    /**
     * Load a file into the {@link IDataSource}.
     *
     * @param filePath Path of the file.
     * @param delete   True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The name of the loaded table, formatted according this datasource
     * Null is the table cannot be loaded.
     */
    @Nullable
    String load(@NotNull String filePath, boolean delete);

    /**
     * Load a file to the {@link IDataSource}.
     *
     * @param filePath  Path of the file.
     * @param dataSetId Identifier of the {@link IDataSet}.
     * @return The name of the loaded table, formatted according this datasource
     * Null is the table cannot be loaded.
     */
    @Nullable
    String load(@NotNull String filePath, @NotNull String dataSetId);

    /**
     * Load a file to the {@link IDataSource}.
     *
     * @param filePath  Path of the file.
     * @param dataSetId Identifier of the {@link IDataSet}.
     * @param delete    True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The name of the loaded table, formatted according this datasource
     * Null is the table cannot be loaded.
     */
    @Nullable
    String load(@NotNull String filePath, @NotNull String dataSetId, boolean delete);

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
    @Nullable
    String load(@NotNull String filePath, @NotNull String dataSetId, @Nullable String encoding, boolean delete);

    /**
     * Load a file into the {@link IDataSource}.
     *
     * @param url {@link URL} of the file.
     * @return The name of the loaded table, formatted according this datasource
     * Null is the table cannot be loaded.
     */
    @Nullable
    String load(@NotNull URL url);

    /**
     * Load a file into the {@link IDataSource}.
     *
     * @param url    {@link URL} of the file.
     * @param delete True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The name of the loaded table, formatted according this datasource
     * Null is the table cannot be loaded.
     */
    @Nullable
    String load(@NotNull URL url, boolean delete);

    /**
     * Load a file to the {@link IDataSource}.
     *
     * @param url       {@link URL} of the file.
     * @param dataSetId Name of the table.
     * @return The name of the loaded table, formatted according this datasource
     * Null is the table cannot be loaded.
     */
    @Nullable
    String load(@NotNull URL url, @NotNull String dataSetId);

    /**
     * Load a file to the {@link IDataSource}.
     *
     * @param url       {@link URL} of the file.
     * @param dataSetId Name of the table.
     * @param delete    True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The name of the loaded table, formatted according this datasource
     * Null is the table cannot be loaded.
     */
    @Nullable
    String load(@NotNull URL url, @NotNull String dataSetId, boolean delete);

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
    @Nullable
    String load(@NotNull URL url, @NotNull String dataSetId, @Nullable String encoding, boolean delete);

    /**
     * Load a file into the {@link IDataSource}.
     *
     * @param uri {@link URI} of the file.
     * @return The name of the loaded table, formatted according this datasource
     * Null is the table cannot be loaded.
     */
    @Nullable
    String load(@NotNull URI uri);

    /**
     * Load a file into the {@link IDataSource}.
     *
     * @param uri    {@link URI} of the file.
     * @param delete True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The name of the loaded table, formatted according this datasource
     * Null is the table cannot be loaded.
     */
    @Nullable
    String load(@NotNull URI uri, boolean delete);

    /**
     * Load a file to the {@link IDataSource}.
     *
     * @param uri       {@link URI} of the file.
     * @param dataSetId Name of the table.
     * @return The name of the loaded table, formatted according this datasource
     * Null is the table cannot be loaded.
     */
    @Nullable
    String load(@NotNull URI uri, @NotNull String dataSetId);

    /**
     * Load a file to the {@link IDataSource}.
     *
     * @param uri       {@link URI} of the file.
     * @param dataSetId Name of the table.
     * @param delete    True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The name of the loaded table, formatted according this datasource
     * Null is the table cannot be loaded.
     */
    @Nullable
    String load(@NotNull URI uri, @NotNull String dataSetId, boolean delete);

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
    @Nullable
    String load(@NotNull URI uri, @NotNull String dataSetId, @Nullable String encoding, boolean delete);

    /**
     * Load a file into the {@link IDataSource}.
     *
     * @param file {@link File}.
     * @return The name of the loaded table, formatted according this datasource
     * Null is the table cannot be loaded.
     */
    @Nullable
    String load(@NotNull File file);

    /**
     * Load a file into the {@link IDataSource}.
     *
     * @param file   {@link File}.
     * @param delete True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The name of the loaded table, formatted according this datasource
     * Null is the table cannot be loaded.
     */
    @Nullable
    String load(@NotNull File file, boolean delete);

    /**
     * Load a file to the {@link IDataSource}.
     *
     * @param file      {@link File}.
     * @param dataSetId Name of the table.
     * @return The name of the loaded table, formatted according this datasource
     * Null is the table cannot be loaded.
     */
    @Nullable
    String load(@NotNull File file, @NotNull String dataSetId);

    /**
     * Load a file to the {@link IDataSource}.
     *
     * @param file      {@link File}.
     * @param dataSetId Name of the table.
     * @param delete    True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The name of the loaded table, formatted according this datasource
     * Null is the table cannot be loaded.
     */
    @Nullable
    String load(@NotNull File file, @NotNull String dataSetId, boolean delete);

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
    @Nullable
    String load(@NotNull File file, @NotNull String dataSetId, @Nullable String encoding, boolean delete);

    /**
     * Load a table from another {@link IDataSource}.
     *
     * @param dataSource      DataSource reference to the input database
     * @param inputTableName  Name of the table to import.
     * @param deleteIfExists  True to delete the outputTableName if exists, false otherwise.
     * @return The name of the loaded table, formatted according this datasource
     * Null is the table cannot be loaded.
     */
    @Nullable
    String load(@Nullable IJdbcDataSource dataSource, @NotNull String inputTableName,
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
    @Nullable
    String load(@Nullable IJdbcDataSource dataSource, @NotNull String inputTableName,
                @NotNull String outputTableName);

    /**
     * Load a table from another {@link IDataSource}.
     *
     * @param dataSource      DataSource reference to the input database
     * @param inputTableName  Name of the table to import.
     * @return The name of the loaded table, formatted according this datasource
     * Null is the table cannot be loaded.
     */
    @Nullable
    String load(@Nullable IJdbcDataSource dataSource, @NotNull String inputTableName);

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
    @Nullable
    String load(@Nullable IJdbcDataSource dataSource, @NotNull String inputTableName,
                @NotNull String outputTableName, boolean deleteIfExists);

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
    @Nullable
    String load(@Nullable IJdbcDataSource dataSource, @NotNull String inputTableName,
                @NotNull String outputTableName, boolean deleteIfExists, int batchSize);

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
    boolean save(@NotNull String dataSetId, @NotNull String filePath);


    /**
     * Save a table into a file.
     *
     * @param dataSetId Name of the table to save.
     * @param filePath  Path of the file where the table will be saved.
     * @param delete true to delete the file if exists
     * @return True if the file has been successfully saved, false otherwise.
     */
    boolean save(@NotNull String dataSetId, @NotNull String filePath, boolean delete);

    /**
     * Save a table into a file.
     *
     * @param dataSetId Name of the table to save.
     * @param filePath  Path of the file where the table will be saved.
     * @param encoding  Encoding of the file.
     * @return True if the file has been successfully saved, false otherwise.
     */
    boolean save(@NotNull String dataSetId, @NotNull String filePath, @Nullable String encoding);

    /**
     * Save a table into a file.
     *
     * @param dataSetId Name of the table to save.
     * @param uri       {@link URI} of the file where the table will be saved.
     * @return True if the file has been successfully saved, false otherwise.
     */
    boolean save(@NotNull String dataSetId, @NotNull URI uri);

    /**
     * Save a table into a file.
     *
     * @param dataSetId Name of the table to save.
     * @param uri       {@link URI} of the file where the table will be saved.
     * @param encoding  Encoding of the file.
     * @return True if the file has been successfully saved, false otherwise.
     */
    boolean save(@NotNull String dataSetId, @NotNull URI uri, @Nullable String encoding);

    /**
     * Save a table into a file.
     *
     * @param dataSetId Name of the table to save.
     * @param url       {@link URL} of the file where the table will be saved.
     * @return True if the file has been successfully saved, false otherwise.
     */
    boolean save(@NotNull String dataSetId, @NotNull URL url);

    /**
     * Save a table into a file.
     *
     * @param dataSetId Name of the table to save.
     * @param url       {@link URL} of the file where the table will be saved.
     * @param encoding  Encoding of the file.
     * @return True if the file has been successfully saved, false otherwise.
     */
    boolean save(@NotNull String dataSetId, @NotNull URL url, @Nullable String encoding);

    /**
     * Save a table into a file.
     *
     * @param dataSetId Name of the table to save.
     * @param file      {@link File} of the file where the table will be saved.
     * @return True if the file has been successfully saved, false otherwise.
     */
    boolean save(@NotNull String dataSetId, @NotNull File file);

    /**
     * Save a table into a file.
     *
     * @param dataSetId Name of the table to save.
     * @param file      {@link File} of the file where the table will be saved.
     * @param encoding  Encoding of the file.
     * @return True if the file has been successfully saved, false otherwise.
     */
    boolean save(@NotNull String dataSetId, @NotNull File file, @Nullable String encoding);

    /**
     * This method is used to execute a SQL file
     *
     * @param fileName The sql file
     * @return True if the script has been successfully run, false otherwise.
     */
    default boolean executeScript(@NotNull String fileName) {
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
    boolean executeScript(@NotNull String fileName, @Nullable Map<String, String> bindings);

    /**
     * This method is used to execute a SQL script
     *
     * @param stream Input stream of the sql file
     * @return True if the script has been successfully run, false otherwise.
     */
    default boolean executeScript(@NotNull InputStream stream) {
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
    boolean executeScript(@NotNull InputStream stream, @Nullable Map<String, String> bindings);

    /**
     * Return the type of the database.
     *
     * @return The type of the database.
     */
    @NotNull
    DBTypes getDataBaseType();

    @Override
    @Nullable
    IJdbcTable getDataSet(@NotNull String name);

    @Override
    @Nullable
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
    @Nullable
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
    @Nullable
    String link(@NotNull Map dataSourceProperties, String sourceTableName);


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
    @Nullable
    String link(@NotNull String filePath, @NotNull String dataSetId, boolean delete);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param filePath  Path of the file.
     * @param dataSetId Identifier of the {@link IDataSet}.
     * @return The name of the linked table, formatted according this datasource
     * Null is the table cannot be linked.
     */
    @Nullable
    String link(@NotNull String filePath, @NotNull String dataSetId);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param filePath Path of the file.
     * @param delete   True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The name of the linked table, formatted according this datasource
     * Null is the table cannot be linked.
     */
    @Nullable
    String link(@NotNull String filePath, boolean delete);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param filePath Path of the file to link.
     * @return The name of the linked table, formatted according this datasource
     * Null is the table cannot be linked.
     */
    @Nullable
    String link(@NotNull String filePath);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param uri       {@link URI} of the file.
     * @param dataSetId Identifier of the {@link IDataSet}.
     * @param delete    True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The name of the linked table, formatted according this datasource
     * Null is the table cannot be linked.
     */
    @Nullable
    String link(@NotNull URI uri, @Nullable String dataSetId, boolean delete);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param uri       {@link URI} of the file.
     * @param dataSetId Identifier of the {@link IDataSet}.
     * @return The name of the linked table, formatted according this datasource
     * Null is the table cannot be linked.
     */
    @Nullable
    String link(@NotNull URI uri, @Nullable String dataSetId);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param uri    {@link URI} of the file.
     * @param delete True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The name of the linked table, formatted according this datasource
     * Null is the table cannot be linked.
     */
    @Nullable
    String link(@NotNull URI uri, boolean delete);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param uri {@link URI} of the file.
     * @return The name of the linked table, formatted according this datasource
     * Null is the table cannot be linked.
     */
    @Nullable
    String link(@NotNull URI uri);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param url       {@link URL} of the file.
     * @param dataSetId Identifier of the {@link IDataSet}.
     * @param delete    True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The name of the linked table, formatted according this datasource
     * Null is the table cannot be linked.
     */
    @Nullable
    String link(@NotNull URL url, @Nullable String dataSetId, boolean delete);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param url       {@link URI} of the file.
     * @param dataSetId Identifier of the {@link IDataSet}.
     * @return The name of the linked table, formatted according this datasource
     * Null is the table cannot be linked.
     */
    @Nullable
    String link(@NotNull URL url, @Nullable String dataSetId);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param url    {@link URI} of the file.
     * @param delete True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The name of the linked table, formatted according this datasource
     * Null is the table cannot be linked.
     */
    @Nullable
    String link(@NotNull URL url, boolean delete);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param url {@link URI} of the file.
     * @return The name of the linked table, formatted according this datasource
     * Null is the table cannot be linked.
     */
    @Nullable
    String link(@NotNull URL url);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param file      {@link File}.
     * @param dataSetId Identifier of the {@link IDataSet}.
     * @param delete    True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The name of the linked table, formatted according this datasource
     * Null is the table cannot be linked.
     */
    @Nullable
    String link(@NotNull File file, @NotNull String dataSetId, boolean delete);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param file      {@link File}.
     * @param dataSetId Identifier of the {@link IDataSet}.
     * @return The name of the linked table, formatted according this datasource
     * Null is the table cannot be linked.
     */
    @Nullable
    String link(@NotNull File file, @NotNull String dataSetId);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param file   {@link File}.
     * @param delete True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The name of the linked table, formatted according this datasource
     * Null is the table cannot be linked.
     */
    @Nullable
    String link(@NotNull File file, boolean delete);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param file {@link File}.
     * @return The name of the linked table, formatted according this datasource
     * Null is the table cannot be linked.
     */
    @Nullable
    String link(@NotNull File file);
}
