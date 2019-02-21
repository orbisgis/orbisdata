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
package org.orbisgis.datamanagerapi.dataset;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.datamanagerapi.dsl.IWhereBuilderOrOptionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

/**
 * Extension of the {@link ITable} specially dedicated to the JDBC databases thanks to the extension of the
 * {@link ResultSet} interface. It also extends the {@link IWhereBuilderOrOptionBuilder} for the SQL requesting and
 * the {@link GroovyObject} to simplify the methods calling (i.e. .tableLocation instead of .getTableLocation() )
 */
public interface IJdbcTable extends ITable, GroovyObject, ResultSet, IWhereBuilderOrOptionBuilder {

    /** Interface {@link Logger} */
    Logger LOGGER = LoggerFactory.getLogger(IJdbcTable.class);
    /** {@link String} name of the metadata property use in the {@link #getProperty(String)} method */
    String META_PROPERTY = "meta";

    /**
     * Return the {@link TableLocation} of the {@link IJdbcTable}.
     *
     * @return The {@link TableLocation}.
     */
    TableLocation getTableLocation();

    /**
     * Return the DataBaseType type of the {@link IJdbcTable}.
     *
     * @return The DataBaseType type
     */
    DataBaseType getDbType();

    /**
     * Return the {@link Map} of properties.
     *
     * @return {@link Map} of the properties.
     */
    Map<String, Object> getPropertyMap();

    /**
     * Get the {@link ResultSetMetaData} wrapping the metadata of the {@link IJdbcTable}.
     *
     * @return The {@link ResultSetMetaData} object.
     */
    @Override
    ResultSetMetaData getMetadata();

    @Override
    default String getLocation() {
        return getTableLocation().toString(getDbType().equals(DataBaseType.H2GIS));
    }

    @Override
    default String getName() {
        return getTableLocation().getTable();
    }

    @Override
    default Iterator<Object> iterator() {
        return new ResultSetIterator(this);
    }

    @Override
    default void eachRow(Closure closure){
        this.forEach(closure::call);
    }

    @Override
    default Object invokeMethod(String name, Object args) {
        Method m = null;
        //First try the get the method with the given name
        try {
            if(args == null) {
                m = this.getClass().getMethod(name);
            }
            else {
                m = this.getClass().getMethod(name, args.getClass());
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
                //If the arguments are an object array, try to get the methods with the argument class array
                else if(args instanceof Object[]){
                    Object[] objects = (Object[])args;
                    Class[] classes = new Class[objects.length];
                    for(int i=0; i<objects.length; i++){
                        classes[i] = objects[i].getClass();
                    }
                    m = this.getClass().getMethod(getName, classes);
                }
                //Otherwise get the method with the argument
                else {
                    m = this.getClass().getMethod(getName, args.getClass());
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
            if(args instanceof Object[] && ((Object[])args).length == 1){
                return m.invoke(this, ((Object[])args)[0]);
            }
            else {
                return m.invoke(this, args);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOGGER.error("Unable to invoke the method named '" + name + "'.\n" + e.getLocalizedMessage());
            return null;
        }
    }

    @Override
    default Object getProperty(String propertyName) {
        //First test the predefined properties
        if(propertyName.equals(META_PROPERTY)){
            return getMetadata();
        }

        Object obj = null;
        try {
            obj = getObject(propertyName);
        } catch (SQLException e) {
            LOGGER.debug("Unable to find the column '" + propertyName + "'.\n" + e.getLocalizedMessage());
        }
        if(obj != null) {
            return obj;
        }
        obj = getPropertyMap().get(propertyName);
        if(obj != null) {
            return obj;
        }
        return invokeMethod(propertyName, null);
    }
}