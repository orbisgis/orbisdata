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

import groovy.lang.GroovyObject;
import groovy.lang.MissingMethodException;
import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.commons.annotations.Nullable;
import org.orbisgis.orbisdata.datamanager.api.dataset.DataBaseType;
import org.orbisgis.orbisdata.datamanager.api.dataset.IJdbcSpatialTable;
import org.orbisgis.orbisdata.datamanager.api.dataset.IJdbcTable;
import org.orbisgis.orbisdata.datamanager.api.dataset.ISpatialTable;

import javax.sql.DataSource;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

/**
 * Extension of the {@link IDataSource} interface dedicated to the usage of a JDBC database as a data source.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (Lab-STICC UBS 2018-2019)
 */
public interface IJdbcDataSource extends IDataSource<Object>, GroovyObject, DataSource {

    /**
     * Close the underlying database.
     */
    void close();

    /**
     * Return the {@link IJdbcTable} contained by the database with the given name. If the table contains a geometric
     * field, return a {@link ISpatialTable}.
     *
     * @param tableName Name of the requested table.
     * @return The {@link IJdbcTable} with the given name or null if no table is found.
     */
    @Nullable
    IJdbcTable getTable(@NotNull String tableName);

    /**
     * Return a {@link ISpatialTable} contained by the database with the given name. If the table doesn't contains a
     * geometric field, return null;
     *
     * @param tableName Name of the requested table.
     * @return The {@link ISpatialTable} with the given name or null if no table is found or if the table doesn't
     * contains a geometric field.
     */
    @Nullable
    IJdbcSpatialTable getSpatialTable(@NotNull String tableName);

    /**
     * Get all table names from the underlying database.
     *
     * @return A {@link Collection} containing the names of all the available tables.
     */
    @NotNull
    Collection<String> getTableNames();

    /**
     * Return true if the {@link IJdbcDataSource} contains a table with the given name.
     *
     * @param tableName Name of the table to check.
     * @return True if {@link IJdbcTable} is found, false otherwise.
     */
    boolean hasTable(@NotNull String tableName);


    /* ********************** */
    /*      Load methods      */
    /* ********************** */

    /**
     * Load a file into the database.
     *
     * @param filePath Path of the file.
     * @return The {@link IJdbcTable} containing the loaded data.
     */
    @Nullable
    IJdbcTable load(@NotNull String filePath);

    /**
     * Load a file into the database.
     *
     * @param filePath Path of the file.
     * @param delete   True to delete the table if exists, false otherwise.
     * @return The {@link IJdbcTable} containing the loaded data.
     */
    @Nullable
    IJdbcTable load(@NotNull String filePath, boolean delete);

    /**
     * Load a file to the database.
     *
     * @param filePath  Path of the file.
     * @param tableName Name of the table.
     * @return The {@link IJdbcTable} containing the loaded data.
     */
    @Nullable
    IJdbcTable load(@NotNull String filePath, @NotNull String tableName);

    /**
     * Load a file to the database.
     *
     * @param filePath  Path of the file.
     * @param tableName Name of the table.
     * @param delete    True to delete the table if exists, false otherwise.
     * @return The {@link IJdbcTable} containing the loaded data.
     */
    @Nullable
    IJdbcTable load(@NotNull String filePath, @NotNull String tableName, boolean delete);

    /**
     * Load a file to the database.
     *
     * @param filePath  Path of the file.
     * @param tableName Name of the table
     * @param encoding  Encoding of the loaded file.
     * @param delete    True to delete the table if exists, false otherwise.
     * @return The {@link IJdbcTable} containing the loaded data.
     */
    @Nullable
    IJdbcTable load(@NotNull String filePath, @NotNull String tableName, @Nullable String encoding, boolean delete);

    /**
     * Load a file into the database.
     *
     * @param url {@link URL} of the file.
     * @return The {@link IJdbcTable} containing the loaded data.
     */
    @Nullable
    IJdbcTable load(@NotNull URL url);

    /**
     * Load a file into the database.
     *
     * @param url    {@link URL} of the file.
     * @param delete True to delete the table if exists, false otherwise.
     * @return The {@link IJdbcTable} containing the loaded data.
     */
    @Nullable
    IJdbcTable load(@NotNull URL url, boolean delete);

    /**
     * Load a file to the database.
     *
     * @param url       {@link URL} of the file.
     * @param tableName Name of the table.
     * @return The {@link IJdbcTable} containing the loaded data.
     */
    @Nullable
    IJdbcTable load(@NotNull URL url, @NotNull String tableName);

    /**
     * Load a file to the database.
     *
     * @param url       {@link URL} of the file.
     * @param tableName Name of the table.
     * @param delete    True to delete the table if exists, false otherwise.
     * @return The {@link IJdbcTable} containing the loaded data.
     */
    @Nullable
    IJdbcTable load(@NotNull URL url, @NotNull String tableName, boolean delete);

    /**
     * Load a file to the database.
     *
     * @param url       {@link URL} of the file.
     * @param tableName Name of the table
     * @param encoding  Encoding of the loaded file.
     * @param delete    True to delete the table if exists, false otherwise.
     * @return The {@link IJdbcTable} containing the loaded data.
     */
    @Nullable
    IJdbcTable load(@NotNull URL url, @NotNull String tableName, @Nullable String encoding, boolean delete);

    /**
     * Load a file into the database.
     *
     * @param uri {@link URI} of the file.
     * @return The {@link IJdbcTable} containing the loaded data.
     */
    @Nullable
    IJdbcTable load(@NotNull URI uri);

    /**
     * Load a file into the database.
     *
     * @param uri    {@link URI} of the file.
     * @param delete True to delete the table if exists, false otherwise.
     * @return The {@link IJdbcTable} containing the loaded data.
     */
    @Nullable
    IJdbcTable load(@NotNull URI uri, boolean delete);

    /**
     * Load a file to the database.
     *
     * @param uri       {@link URI} of the file.
     * @param tableName Name of the table.
     * @return The {@link IJdbcTable} containing the loaded data.
     */
    @Nullable
    IJdbcTable load(@NotNull URI uri, @NotNull String tableName);

    /**
     * Load a file to the database.
     *
     * @param uri       {@link URI} of the file.
     * @param tableName Name of the table.
     * @param delete    True to delete the table if exists, false otherwise.
     * @return The {@link IJdbcTable} containing the loaded data.
     */
    @Nullable
    IJdbcTable load(@NotNull URI uri, @NotNull String tableName, boolean delete);

    /**
     * Load a file to the database.
     *
     * @param uri       {@link URI} of the file.
     * @param tableName Name of the table
     * @param encoding  Encoding of the loaded file.
     * @param delete    True to delete the table if exists, false otherwise.
     * @return The {@link IJdbcTable} containing the loaded data.
     */
    @Nullable
    IJdbcTable load(@NotNull URI uri, @NotNull String tableName, @Nullable String encoding, boolean delete);

    /**
     * Load a file into the database.
     *
     * @param file {@link File}.
     * @return The {@link IJdbcTable} containing the loaded data.
     */
    @Nullable
    IJdbcTable load(@NotNull File file);

    /**
     * Load a file into the database.
     *
     * @param file   {@link File}.
     * @param delete True to delete the table if exists, false otherwise.
     * @return The {@link IJdbcTable} containing the loaded data.
     */
    @Nullable
    IJdbcTable load(@NotNull File file, boolean delete);

    /**
     * Load a file to the database.
     *
     * @param file      {@link File}.
     * @param tableName Name of the table.
     * @return The {@link IJdbcTable} containing the loaded data.
     */
    @Nullable
    IJdbcTable load(@NotNull File file, @NotNull String tableName);

    /**
     * Load a file to the database.
     *
     * @param file      {@link File}.
     * @param tableName Name of the table.
     * @param delete    True to delete the table if exists, false otherwise.
     * @return The {@link IJdbcTable} containing the loaded data.
     */
    @Nullable
    IJdbcTable load(@NotNull File file, @NotNull String tableName, boolean delete);

    /**
     * Load a file to the database.
     *
     * @param file      {@link File}.
     * @param tableName Name of the table
     * @param encoding  Encoding of the loaded file.
     * @param delete    True to delete the table if exists, false otherwise.
     * @return The {@link IJdbcTable} containing the loaded data.
     */
    @Nullable
    IJdbcTable load(@NotNull File file, @NotNull String tableName, @Nullable String encoding, boolean delete);

    /**
     * Load a table from another database.
     *
     * @param properties     Properties used to connect to the database.
     * @param inputTableName Name of the table to import.
     * @return The {@link IJdbcTable} containing the loaded data.
     */
    @Nullable
    IJdbcTable load(@NotNull Map<String, String> properties, @NotNull String inputTableName);


    /**
     * Load a table from another database.
     *
     * @param properties      Properties used to connect to the database.
     * @param inputTableName  Name of the table to import.
     * @param outputTableName Name of the imported table in the database.
     * @return The {@link IJdbcTable} containing the loaded data.
     */
    @Nullable
    IJdbcTable load(@NotNull Map<String, String> properties, @NotNull String inputTableName,
                    @NotNull String outputTableName);

    /**
     * Load a table from another database.
     *
     * @param properties     Properties used to connect to the database.
     * @param inputTableName Name of the table to import.
     * @param delete         True to delete the outputTableName if exists, false otherwise.
     * @return The {@link IJdbcTable} containing the loaded data.
     */
    @Nullable
    IJdbcTable load(@NotNull Map<String, String> properties, @NotNull String inputTableName, boolean delete);

    /**
     * Load a table from another database.
     *
     * @param properties      Properties used to connect to the database.
     * @param inputTableName  Name of the table to import.
     * @param outputTableName Name of the imported table in the database.
     * @param delete          True to delete the outputTableName if exists, false otherwise.
     * @return The {@link IJdbcTable} containing the loaded data.
     */
    @Nullable
    IJdbcTable load(@NotNull Map<String, String> properties, @NotNull String inputTableName,
                    @NotNull String outputTableName, boolean delete);


    /* ********************** */
    /*      Save methods      */
    /* ********************** */

    /**
     * Save a table into a file.
     *
     * @param tableName Name of the table to save.
     * @param filePath  Path of the file where the table will be saved.
     * @return True if the file has been successfully saved, false otherwise.
     */
    boolean save(@NotNull String tableName, @NotNull String filePath);

    /**
     * Save a table into a file.
     *
     * @param tableName Name of the table to save.
     * @param filePath  Path of the file where the table will be saved.
     * @param encoding  Encoding of the file.
     * @return True if the file has been successfully saved, false otherwise.
     */
    boolean save(@NotNull String tableName, @NotNull String filePath, @Nullable String encoding);

    /**
     * Save a table into a file.
     *
     * @param tableName Name of the table to save.
     * @param uri       {@link URI} of the file where the table will be saved.
     * @return True if the file has been successfully saved, false otherwise.
     */
    boolean save(@NotNull String tableName, @NotNull URI uri);

    /**
     * Save a table into a file.
     *
     * @param tableName Name of the table to save.
     * @param uri       {@link URI} of the file where the table will be saved.
     * @param encoding  Encoding of the file.
     * @return True if the file has been successfully saved, false otherwise.
     */
    boolean save(@NotNull String tableName, @NotNull URI uri, @Nullable String encoding);

    /**
     * Save a table into a file.
     *
     * @param tableName Name of the table to save.
     * @param url       {@link URL} of the file where the table will be saved.
     * @return True if the file has been successfully saved, false otherwise.
     */
    boolean save(@NotNull String tableName, @NotNull URL url);

    /**
     * Save a table into a file.
     *
     * @param tableName Name of the table to save.
     * @param url       {@link URL} of the file where the table will be saved.
     * @param encoding  Encoding of the file.
     * @return True if the file has been successfully saved, false otherwise.
     */
    boolean save(@NotNull String tableName, @NotNull URL url, @Nullable String encoding);

    /**
     * Save a table into a file.
     *
     * @param tableName Name of the table to save.
     * @param file      {@link File} of the file where the table will be saved.
     * @return True if the file has been successfully saved, false otherwise.
     */
    boolean save(@NotNull String tableName, @NotNull File file);

    /**
     * Save a table into a file.
     *
     * @param tableName Name of the table to save.
     * @param file      {@link File} of the file where the table will be saved.
     * @param encoding  Encoding of the file.
     * @return True if the file has been successfully saved, false otherwise.
     */
    boolean save(@NotNull String tableName, @NotNull File file, @Nullable String encoding);


    /* ********************** */
    /*      Link methods      */
    /* ********************** */

    /**
     * Link a file to the database.
     *
     * @param filePath  Path of the file.
     * @param tableName Name of the database table.
     * @param delete    True to delete the table if exists, false otherwise.
     * @return A {@link IJdbcTable} representing the linked file.
     */
    @Nullable
    IJdbcTable link(@NotNull String filePath, @NotNull String tableName, boolean delete);

    /**
     * Link a file to the database.
     *
     * @param filePath  Path of the file.
     * @param tableName Name of the database table.
     * @return A {@link IJdbcTable} representing the linked file.
     */
    @Nullable
    IJdbcTable link(@NotNull String filePath, @NotNull String tableName);

    /**
     * Link a file to the database.
     *
     * @param filePath Path of the file.
     * @param delete   True to delete the table if exists, false otherwise.
     * @return A {@link IJdbcTable} representing the linked file.
     */
    @Nullable
    IJdbcTable link(@NotNull String filePath, boolean delete);

    /**
     * Link a file to the database.
     *
     * @param filePath Path of the file to link.
     * @return A {@link IJdbcTable} representing the linked file.
     */
    @Nullable
    IJdbcTable link(@NotNull String filePath);

    /**
     * Link a file to the database.
     *
     * @param uri       {@link URI} of the file.
     * @param tableName Name of the database table.
     * @param delete    True to delete the table if exists, false otherwise.
     * @return A {@link IJdbcTable} representing the linked file.
     */
    @Nullable
    IJdbcTable link(@NotNull URI uri, @Nullable String tableName, boolean delete);

    /**
     * Link a file to the database.
     *
     * @param uri       {@link URI} of the file.
     * @param tableName Name of the database table.
     * @return A {@link IJdbcTable} representing the linked file.
     */
    @Nullable
    IJdbcTable link(@NotNull URI uri, @Nullable String tableName);

    /**
     * Link a file to the database.
     *
     * @param uri    {@link URI} of the file.
     * @param delete True to delete the table if exists, false otherwise.
     * @return A {@link IJdbcTable} representing the linked file.
     */
    @Nullable
    IJdbcTable link(@NotNull URI uri, boolean delete);

    /**
     * Link a file to the database.
     *
     * @param uri {@link URI} of the file.
     * @return A {@link IJdbcTable} representing the linked file.
     */
    @Nullable
    IJdbcTable link(@NotNull URI uri);

    /**
     * Link a file to the database.
     *
     * @param url       {@link URL} of the file.
     * @param tableName Name of the database table.
     * @param delete    True to delete the table if exists, false otherwise.
     * @return A {@link IJdbcTable} representing the linked file.
     */
    @Nullable
    IJdbcTable link(@NotNull URL url, @Nullable String tableName, boolean delete);

    /**
     * Link a file to the database.
     *
     * @param url       {@link URI} of the file.
     * @param tableName Name of the database table.
     * @return A {@link IJdbcTable} representing the linked file.
     */
    @Nullable
    IJdbcTable link(@NotNull URL url, @Nullable String tableName);

    /**
     * Link a file to the database.
     *
     * @param url    {@link URI} of the file.
     * @param delete True to delete the table if exists, false otherwise.
     * @return A {@link IJdbcTable} representing the linked file.
     */
    @Nullable
    IJdbcTable link(@NotNull URL url, boolean delete);

    /**
     * Link a file to the database.
     *
     * @param url {@link URI} of the file.
     * @return A {@link IJdbcTable} representing the linked file.
     */
    @Nullable
    IJdbcTable link(@NotNull URL url);

    /**
     * Link a file to the database.
     *
     * @param file      {@link File}.
     * @param tableName Name of the database table.
     * @param delete    True to delete the table if exists, false otherwise.
     * @return A {@link IJdbcTable} representing the linked file.
     */
    @Nullable
    IJdbcTable link(@NotNull File file, @NotNull String tableName, boolean delete);

    /**
     * Link a file to the database.
     *
     * @param file      {@link File}.
     * @param tableName Name of the database table.
     * @return A {@link IJdbcTable} representing the linked file.
     */
    @Nullable
    IJdbcTable link(@NotNull File file, @NotNull String tableName);

    /**
     * Link a file to the database.
     *
     * @param file   {@link File}.
     * @param delete True to delete the table if exists, false otherwise.
     * @return A {@link IJdbcTable} representing the linked file.
     */
    @Nullable
    IJdbcTable link(@NotNull File file, boolean delete);

    /**
     * Link a file to the database.
     *
     * @param file {@link File}.
     * @return A {@link IJdbcTable} representing the linked file.
     */
    @Nullable
    IJdbcTable link(@NotNull File file);

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
    DataBaseType getDataBaseType();

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
}
