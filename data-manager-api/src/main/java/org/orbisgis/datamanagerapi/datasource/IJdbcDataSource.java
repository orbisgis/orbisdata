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
 * Copyright (C) 2019 CNRS (Lab-STICC UMR CNRS 6285)
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

import groovy.lang.GroovyObject;
import org.orbisgis.datamanagerapi.dataset.ISpatialTable;
import org.orbisgis.datamanagerapi.dataset.ITable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Extension of the {@link IDataSource} interface dedicated to the usage of a JDBC database as a data source.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2018-2019)
 */
public interface IJdbcDataSource extends IDataSource, GroovyObject {

    /** Interface {@link Logger} */
    Logger LOGGER = LoggerFactory.getLogger(IJdbcDataSource.class);

    /**
     * Close the underlying database.
     */
    void close();

    /**
     * Return the {@link ITable} contained by the database with the given name.
     *
     * @param tableName Name of the requested table.
     *
     * @return The {@link ITable} with the given name or null if no table is found.
     */
    ITable getTable(String tableName);

    /**
     * Return a {@link ISpatialTable} contained by the database with the given name.
     *
     * @param tableName Name of the requested table.
     *
     * @return The {@link ISpatialTable} with the given name or null if no table is found.
     */
    ISpatialTable getSpatialTable(String tableName);

    /**
     * Get all table names from the underlying database.
     *
     * @return A {@link Collection} containing the names of all the available tables.
     */
    Collection<String> getTableNames();

    /**
     * Load a file into the database.
     *
     * @param filePath Path of the file or its {@link URI}.
     */
    ITable load(String filePath);

    /**
     * Load a file into the database.
     *
     * @param filePath Path of the file or its {@link URI}.
     * @param delete True to delete the table if exists, false otherwise.
 */
    ITable load(String filePath, boolean delete);

    /**
     * Load a file to the database.
     *
     * @param filePath Path of the file or its {@link URI}.
     * @param tableName Name of the table.
     */
    ITable load(String filePath, String tableName);

    /**
     * Load a file to the database.
     *
     * @param filePath Path of the file or its {@link URI}.
     * @param tableName Name of the table.
     * @param delete True to delete the table if exists, false otherwise.
     */
    ITable load(String filePath, String tableName, boolean delete);
    
    /**
     * Load a file to the database.
     *
     * @param filePath Path of the file or its {@link URI}
     * @param tableName Name of the table
     * @param encoding Encoding of the loaded file.
     * @param delete True to delete the table if exists, false otherwise.
     */
    ITable load(String filePath, String tableName, String encoding, boolean delete);

    /**
     * Load a table from another database.
     *
     * @param properties to connect to the database.
     * @param inputTableName the name of the table we want to import.
     */
    ITable load(Map<String, String> properties, String inputTableName);


    /**
     * Load a table from another database.
     *
     * @param properties Properties used to connect to the database.
     * @param inputTableName Name of the table to import.
     * @param outputTableName Name of the imported table in the database.
     */
    ITable load(Map<String, String> properties, String inputTableName,String outputTableName);

    /**
     * Load a table from another database.
     *
     * @param properties Properties used to connect to the database.
     * @param inputTableName Name of the table to import.
     * @param delete True to delete the outputTableName if exists, false otherwise.
     */
    ITable load(Map<String, String> properties, String inputTableName, boolean delete);

    /**
     * Load a table from another database.
     *
     * @param properties Properties used to connect to the database.
     * @param inputTableName Name of the table to import.
     * @param outputTableName Name of the imported table in the database.
     * @param delete True to delete the outputTableName if exists, false otherwise.
     */
    ITable load(Map<String, String> properties, String inputTableName, String outputTableName, boolean delete);
    
    /**
     * Save a table into a file.
     *
     * @param tableName Name of the table to save.
     * @param filePath Path of the file where the table will be saved.
     *
     * @return True if the file has been successfully saved, false otherwise.
     */
    boolean save(String tableName, String filePath);

    /**
     * Save a table into a file.
     *
     * @param tableName Name of the table to save.
     * @param filePath Path of the file where the table will be saved.
     * @param encoding Encoding of the file.
     *
     * @return True if the file has been successfully saved, false otherwise.
     */
    boolean save(String tableName, String filePath, String encoding);


    /**
     * Link a file to the database.
     *
     * @param filePath Path of the file or its {@link URI}
     * @param tableName Name of the database table.
     * @param delete True to delete the table if exists, false otherwise.
     *
     * @return A {@link ITable} representing the linked file.
     */
    ITable link(String filePath, String tableName, boolean delete);

    /**
     * Link a file to the database.
     *
     * @param filePath Path of the file or its {@link URI}
     * @param tableName Name of the database table.
     *
     * @return A {@link ITable} representing the linked file.
     */
    ITable link(String filePath, String tableName);

    /**
     * Link a file to the database.
     *
     * @param filePath Path of the file or its {@link URI}
     * @param delete True to delete the table if exists, false otherwise.
     *
     * @return A {@link ITable} representing the linked file.
     */
    ITable link(String filePath,  boolean delete);

    /**
     * Link a file to the database.
     *
     * @param filePath Path or URI of the file to link.
     *
     * @return A {@link ITable} representing the linked file.
     */
    ITable link(String filePath);

    /**
     * Return the {@link Map} of the properties.
     *
     * @return {@link Map} of the properties.
     */
    Map<String, Object> getPropertyMap();

    @Override
    default Object invokeMethod(String name, Object args) {
        Method m = null;
        //First try the get the method with the given name
        try {
            //If arguments are null, get the method without arguments
            if(args == null) {
                m = this.getClass().getMethod(name);
            }
            else {
                if(!args.getClass().isArray()){
                    m = this.getClass().getMethod(name, args.getClass());
                }
                else if(((Object[])args).length==1){
                    m = this.getClass().getMethod(name, ((Object[])args)[0].getClass());
                }
                //If the arguments are an object array, try to get the methods with the argument class array
                else {
                    List<Class> list = Stream.of((Object[])args).map(Object::getClass).collect(Collectors.toList());
                    try {
                        m = this.getClass().getMethod(name, list.toArray(new Class[]{}));
                    } catch (NoSuchMethodException e) {
                        LOGGER.debug("Unable to get a method named '" + name + "'.\n" + e.getLocalizedMessage());
                    }
                    if(m == null) {
                        try {
                            List<Class> objList = new ArrayList<>();
                            for (int i = 0; i < list.size(); i++) {
                                objList.add(Object.class);
                            }
                            m = this.getClass().getMethod(name, objList.toArray(new Class[]{}));
                        } catch (NoSuchMethodException e) {
                            LOGGER.debug("Unable to get a method named '" + name + "'.\n" + e.getLocalizedMessage());
                        }
                        if(m == null) {
                            m = this.getClass().getMethod(name, args.getClass());
                        }
                    }
                }
            }
        } catch (NoSuchMethodException e) {
            LOGGER.debug("Unable to get a method named '" + name + "'.\n" + e.getLocalizedMessage());
        }
        //It no method found, try to get the the methods named 'get'+givenName
        if(m == null){
            try {
                String getName = "get" + name.substring(0,1).toUpperCase() + name.substring(1);
                //If arguments are null, get the method without arguments
                if(args == null) {
                    m = this.getClass().getMethod(getName);
                }
                else {
                    if(!args.getClass().isArray()){
                        m = this.getClass().getMethod(getName, args.getClass());
                    }
                    else if(((Object[])args).length==1){
                        m = this.getClass().getMethod(getName, ((Object[])args)[0].getClass());
                    }
                    //If the arguments are an object array, try to get the methods with the argument class array
                    else {
                        List<Class> list = Stream.of((Object[])args).map(Object::getClass).collect(Collectors.toList());
                        try {
                            m = this.getClass().getMethod(getName, list.toArray(new Class[]{}));
                        } catch (NoSuchMethodException e) {
                            LOGGER.debug("Unable to get a method named '" + getName + "'.\n" + e.getLocalizedMessage());
                        }
                        if(m == null) {
                            try {
                                List<Class> objList = new ArrayList<>();
                                for (int i = 0; i < list.size(); i++) {
                                    objList.add(Object.class);
                                }
                                m = this.getClass().getMethod(getName, objList.toArray(new Class[]{}));
                            } catch (NoSuchMethodException e) {
                                LOGGER.debug("Unable to get a method named '" + getName + "'.\n" + e.getLocalizedMessage());
                            }
                            if(m == null) {
                                m = this.getClass().getMethod(getName, args.getClass());
                            }
                        }
                    }
                }
            } catch (NoSuchMethodException e) {
                LOGGER.debug("Unable to get a method named '" + name + "'.\n" + e.getLocalizedMessage());
            }
        }
        if(m == null){
            return null;
        }
        //Try to call the found method
        try {
            if(args == null) {
                return m.invoke(this);
            }
            else {
                if(m.getParameterCount() == 1) {
                    if(args.getClass().isArray() && ((Object[])args).length==1){
                        return m.invoke(this, ((Object[])args)[0]);
                    }
                    else {
                        return m.invoke(this, args);
                    }
                }
                else{
                    return m.invoke(this, (Object[])args);
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOGGER.error("Unable to invoke the method named '" + name + "'.\n" + e.getLocalizedMessage());
            return null;
        }
    }

    @Override
    default Object getProperty(String propertyName) {
        Object obj = getPropertyMap().get(propertyName);
        if(obj != null) {
            return obj;
        }
        return invokeMethod(propertyName, null);
    }

    @Override
    default void setProperty(String propertyName, Object newValue) {
        getPropertyMap().put(propertyName, newValue);
    }
}
