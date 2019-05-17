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
import groovy.lang.MissingMethodException;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.datamanagerapi.dsl.IWhereBuilderOrOptionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
     * Return the {@link DataBaseType} type of the {@link IJdbcTable}.
     *
     * @return The {@link DataBaseType} type
     */
    DataBaseType getDbType();

    /**
     * Get the {@link ResultSetMetaData} wrapping the metadata of the {@link IJdbcTable}.
     *
     * @return The {@link ResultSetMetaData} object.
     */
    @Override
    ResultSetMetaData getMetadata();

    /**
     * Return true if the {@link ITable} is spatial.
     *
     * @return True if the {@link ITable} is spatial.
     */
    boolean isSpatial();

    /**
     * Return true if the {@link ITable} is a linked one.
     *
     * @return True if the {@link ITable} is a linked one.
     */
    boolean isLinked();

    /**
     * Return true if the {@link ITable} is a temporary one.
     *
     * @return True if the {@link ITable} is a temporary one.
     */
    boolean isTemporary();

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
        try {
            return new ResultSetIterator(this);
        } catch (SQLException e) {
            LOGGER.error(e.getLocalizedMessage());
            return new ResultSetIterator();
        }
    }

    @Override
    default void eachRow(Closure closure){
        this.forEach(closure::call);
    }

    @Override
    default Object invokeMethod(String name, Object args) {
        try {
            return getMetaClass().invokeMethod(this, name, args);
        } catch (MissingMethodException e) {
            LOGGER.debug("Unable to find the '"+name+"' methods, trying with the getter");
            return getMetaClass()
                    .invokeMethod(this, "get" + name.substring(0, 1).toUpperCase() + name.substring(1), args);
        }
    }

    @Override
    default Object getProperty(String propertyName) {
        if(propertyName == null){
            LOGGER.error("Trying to get null property name.");
            return null;
        }
        //First test the predefined properties
        if(propertyName.equals(META_PROPERTY)){
            return getMetadata();
        }
        if(getColumnNames()!= null &&
                (getColumnNames().contains(propertyName.toLowerCase()) || getColumnNames().contains(propertyName.toUpperCase()))
                || "id".equals(propertyName)) {
            try {
                return getObject(propertyName);
            } catch (SQLException e) {
                LOGGER.debug("Unable to find the column '" + propertyName + "'.\n" + e.getLocalizedMessage());
            }
        }
        return getMetaClass().getProperty(this, propertyName);
    }

    @Override
    default void setProperty(String propertyName, Object newValue) {
        getMetaClass().setProperty(this, propertyName, newValue);
    }
}