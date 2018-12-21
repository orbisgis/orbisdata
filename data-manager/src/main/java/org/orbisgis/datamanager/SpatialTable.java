/*
 * Bundle DataManager is part of the OrbisGIS platform
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
 * DataManager is distributed under GPL 3 license.
 *
 * Copyright (C) 2018 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * DataManager is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * DataManager is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * DataManager. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.datamanager;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.h2gis.utilities.SpatialResultSetMetaData;
import org.h2gis.utilities.TableLocation;
import org.h2gis.utilities.wrapper.SpatialResultSetImpl;
import org.h2gis.utilities.wrapper.StatementWrapper;
import org.locationtech.jts.geom.Geometry;
import org.orbisgis.datamanagerapi.dataset.ISpatialTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SpatialTable extends SpatialResultSetImpl implements ISpatialTable, GroovyObject {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpatialTable.class);
    private static final String META_PROPERTY = "meta";

    private Database dataBase;
    private TableLocation tableLocation;
    private MetaClass metaClass;
    private Map<String, Object> propertyMap;

    public SpatialTable(TableLocation tableLocation, ResultSet resultSet, StatementWrapper statement, Database dataBase) {
        super(resultSet, statement);
        try {
            resultSet.beforeFirst();
        } catch (SQLException e) {
            LOGGER.error("Unable to go before the first ResultSet row.\n" + e.getLocalizedMessage());
        }
        this.dataBase = dataBase;
        this.tableLocation = tableLocation;
        this.metaClass = InvokerHelper.getMetaClass(getClass());
        this.propertyMap = new HashMap<>();
    }

    @Override
    public String getLocation() {
        return tableLocation.toString(dataBase.equals(Database.H2GIS));
    }

    @Override
    public String getName() {
        return tableLocation.getTable();
    }

    @Override
    public Iterator<Object> iterator() {
        return new ResultSetIterator(this);
    }

    @Override
    public void eachRow(Closure closure){
        this.forEach(closure::call);
    }

    @Override
    public Geometry getGeometry(int columnIndex){
        try {
            return super.getGeometry(columnIndex);
        } catch (SQLException e) {
            LOGGER.error("Unable to get the geometry at '" + columnIndex + "'.\n" + e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public Geometry getGeometry(String columnLabel){
        try {
            return super.getGeometry(columnLabel);
        } catch (SQLException e) {
            LOGGER.error("Unable to get the geometry of '" + columnLabel + "'.\n" + e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public Geometry getGeometry(){
        try {
            return super.getGeometry();
        } catch (SQLException e) {
            LOGGER.error("Unable to get the geometry.\n" + e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public Object invokeMethod(String name, Object args) {
        Method m = null;
        try {
            if(args == null) {
                m = this.getClass().getDeclaredMethod(name);
            }
            else {
                m = this.getClass().getDeclaredMethod(name, args.getClass());
            }
        } catch (NoSuchMethodException e) {
            LOGGER.error("Unable to get a method named '" + name + "'.\n" + e.getLocalizedMessage());
        }
        if(m == null){
            try {
                String getName = "get" + name.substring(0,1).toUpperCase() + name.substring(1);
                if(args == null) {
                    m = this.getClass().getDeclaredMethod(getName);
                }
                else {
                    m = this.getClass().getDeclaredMethod(getName, args.getClass());
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
            else {
                return m.invoke(this, args);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOGGER.error("Unable to invoke the method named '" + name + "'.\n" + e.getLocalizedMessage());
            return null;
        }
    }

    @Override
    public Object getProperty(String propertyName) {
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
        obj = propertyMap.get(propertyName);
        if(obj != null) {
            return obj;
        }
        return invokeMethod(propertyName, null);
    }

    @Override
    public void setProperty(String propertyName, Object newValue) {
        propertyMap.put(propertyName, newValue);
    }

    @Override
    public MetaClass getMetaClass() {
        return metaClass;
    }

    @Override
    public void setMetaClass(MetaClass metaClass) {
        this.metaClass = metaClass;
    }

    @Override
    public SpatialResultSetMetaData getMetadata(){
        try {
            return super.getMetaData().unwrap(SpatialResultSetMetaData.class);
        } catch (SQLException e) {
            LOGGER.error("Unable to get the metadata.\n" + e.getLocalizedMessage());
            return null;
        }
    }
}
