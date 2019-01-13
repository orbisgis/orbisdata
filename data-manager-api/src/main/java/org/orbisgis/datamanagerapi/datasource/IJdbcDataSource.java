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
 * DataManager API  is distributed under GPL 3 license.
 *
 * Copyright (C) 2018 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * DataManager API  is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * DataManager API  is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.orbisgis.datamanagerapi.datasource;

import org.orbisgis.datamanagerapi.dataset.ISpatialTable;
import org.orbisgis.datamanagerapi.dataset.ITable;
import org.orbisgis.datamanagerapi.dataset.ITableWrapper;

import java.util.Collection;
import java.util.Map;

/**
 * Extension of the IDataSource interface dedicated to the usage of a JDBC database.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2018)
 */
public interface IJdbcDataSource extends IDataSource {

    /**
     * Close the underlying datasource
     */
    void close();

    /**
     * Return a {@link ITable} by name
     * @param tableName Name of the table
     * @return
     */
    ITable getTable(String tableName);

    /**
     * Return a {@link ISpatialTable} by name
     * @param tableName Name of the table
     * @return
     */
    ISpatialTable getSpatialTable(String tableName);

    /**
     * Get all table names from the underlying datasource.
     * @return
     */
    Collection<String> getTableNames();
    
    
    /**
     * Load a file to the database
     * @param filePath the path of the file or URI
     */
    ITableWrapper load(String filePath);


    /**
     * Load a file to the database
     * @param filePath the path of the file or URI
     * @param delete true to delete the table if exists
     */
    ITableWrapper load(String filePath, boolean delete);
    
    
    /**
     * Load a file to the database
     * @param filePath the path of the file or URI
     * @param tableName the name of the table 
     */
    ITableWrapper load(String filePath, String tableName);


    /**
     * Load a file to the database
     * @param filePath the path of the file or URI
     * @param tableName the name of the table
     * @param delete true to delete the table if exists
     */
    ITableWrapper load(String filePath, String tableName, boolean delete);
    
    /**
     * Load a file to the database
     * @param filePath the path of the file or URI
     * @param tableName the name of the table 
     * @param encoding Encoding property.
     * @param delete true to delete the table if exists
     */
    ITableWrapper load(String filePath, String tableName, String encoding, boolean delete);


    /**
     * Load a table from another database
     * @param properties to connect to the database
     * @param inputTableName the name of the table we want to import
     */
    ITableWrapper load(Map<String, String> properties, String inputTableName);


    /**
     * Load a table from another database
     * @param properties to connect to the database
     * @param inputTableName the name of the table we want to import
     * @param outputTableName the name of the imported table
     */
    ITableWrapper load(Map<String, String> properties, String inputTableName,String outputTableName);

    /**
     * Load a table from another database
     * @param properties to connect to the database
     * @param inputTableName the name of the table we want to import     *
     * @param delete true to delete the outputTableName if exists
     */
    ITableWrapper load(Map<String, String> properties, String inputTableName, boolean delete);

    /**
     * Load a table from another database
     * @param properties to connect to the database
     * @param inputTableName the name of the table we want to import
     * @param outputTableName the name of the imported table
     * @param delete true to delete the outputTableName if exists
     */
    ITableWrapper load(Map<String, String> properties, String inputTableName, String outputTableName, boolean delete);
    
    /**
     * Save a table to a file
     * @param tableName the name of the table   
     * @param filePath the path of the file to be saved
     */
    void save(String tableName, String filePath);

    /**
     * Save a table to a file
     * @param tableName the name of the table
     * @param filePath the path of the file to be saved
     * @param encoding Encoding property.
     */
    void save(String tableName, String filePath, String encoding);


    /**
     * Link a file to the database
     * @param filePath the path of the file or URI
     * @param tableName the name of the table
     * @param delete true to delete the table if exists
     */
    ITableWrapper link(String filePath, String tableName, boolean delete);

    /**
     * Link a file to the database
     * @param filePath the path of the file or URI
     * @param tableName the name of the table
     */
    ITableWrapper link(String filePath, String tableName);

    /**
     * Link a file to the database
     * @param filePath the path of the file or URI
     * @param delete true to delete the table if exists
     */
    ITableWrapper link(String filePath,  boolean delete);

    /**
     * Link a file to the database
     * @param filePath the path of the file or URI
     */
    ITableWrapper link(String filePath);
}
