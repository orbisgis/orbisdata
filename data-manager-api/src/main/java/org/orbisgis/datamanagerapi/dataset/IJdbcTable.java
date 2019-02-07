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

public interface IJdbcTable extends ITable, GroovyObject, ResultSet, IWhereBuilderOrOptionBuilder {

    Logger LOGGER = LoggerFactory.getLogger(IJdbcTable.class);
    String META_PROPERTY = "meta";

    /**
     * Return the TableLocation of the JdbcTable.
     *
     * @return The TableLocation.
     */
    TableLocation getTableLocation();

    /**
     * Return the DataBaseType type of the JdbcTable.
     *
     * @return The DataBaseType type
     */
    DataBaseType getDbType();

    /**
     * Return the Map of properties.
     *
     * @return Map of the properties.
     */
    Map<String, Object> getPropertyMap();

    /**
     * Get the ResultSetMetaData of the DataSet.
     *
     * @return The metadata object.
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
        try {
            if(args == null) {
                m = this.getClass().getMethod(name);
            }
            else {
                m = this.getClass().getMethod(name, args.getClass());
            }
        } catch (NoSuchMethodException e) {
            LOGGER.error("Unable to get a method named '" + name + "'.\n" + e.getLocalizedMessage());
        }
        if(m == null){
            try {
                String getName = "get" + name.substring(0,1).toUpperCase() + name.substring(1);
                if(args == null) {
                    m = this.getClass().getMethod(getName);
                }
                else if(args instanceof Object[]){
                    Object[] objects = (Object[])args;
                    Class[] classes = new Class[objects.length];
                    for(int i=0; i<objects.length; i++){
                        classes[i] = objects[i].getClass();
                    }
                    m = this.getClass().getMethod(getName, classes);
                }
                else {
                    m = this.getClass().getMethod(getName, args.getClass());
                }
            } catch (NoSuchMethodException e) {
                LOGGER.error("Unable to get a method named '" + name + "'.\n" + e.getLocalizedMessage());
            }
        }
        if(m == null){
            return null;
        }
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
        if(propertyName.equals(META_PROPERTY)){
            return getMetadata();
        }
        Object obj = null;
        try {
            obj = getObject(propertyName);
        } catch (SQLException e) {
            LOGGER.warn("Unable to find the column '" + propertyName + "'.\n" + e.getLocalizedMessage());
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