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
 * DataManager is distributed under LGPL 3 license.
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
package org.orbisgis.orbisdata.datamanager.jdbc;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.MissingMethodException;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.orbisdata.datamanager.api.dataset.DataBaseType;
import org.orbisgis.orbisdata.datamanager.api.dataset.IJdbcColumn;
import org.orbisgis.orbisdata.datamanager.api.datasource.IJdbcDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Contains the methods which are in common to all the {@link IJdbcColumn} subclasses.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public class JdbcColumn implements IJdbcColumn, GroovyObject {;

    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcColumn.class);

    /** MetaClass use for groovy methods/properties binding */
    private MetaClass metaClass;
    /** Name of the column. */
    private String name;
    /** Name of the table of the column. */
    private TableLocation tableName;
    /** {@link JdbcDataSource} of the column. */
    private JdbcDataSource dataSource;
    /** Indicates if the database is an H2 one. */
    private boolean isH2;

    /**
     * Default constructor.
     *
     * @param name Name of the column.
     * @param tableName Name of the table of the column.
     * @param dataSource {@link IJdbcDataSource} of the column.
     */
    public JdbcColumn(String name, String tableName, JdbcDataSource dataSource){
        this.isH2 = dataSource.getDataBaseType() == DataBaseType.H2GIS;
        this.name = TableLocation.capsIdentifier(name, isH2);
        this.tableName = TableLocation.parse(tableName, isH2);
        this.dataSource = dataSource;
        this.metaClass = InvokerHelper.getMetaClass(JdbcColumn.class);
    }

    @Override
    public String getName(){
        return name;
    }

    @Override
    public String getType(){
        try {
            Map map = dataSource.firstRow("SELECT TYPE_NAME FROM INFORMATION_SCHEMA.COLUMNS " +
                    "WHERE INFORMATION_SCHEMA.COLUMNS.TABLE_NAME=? " +
                    "AND INFORMATION_SCHEMA.COLUMNS.TABLE_SCHEMA=? " +
                    "AND INFORMATION_SCHEMA.COLUMNS.COLUMN_NAME=?;",
                    new Object[]{tableName.getTable(), tableName.getSchema("PUBLIC"), name});
            if(map != null && map.containsKey("TYPE_NAME")){
                return map.get("TYPE_NAME").toString();
            }
        } catch (SQLException e) {
            LOGGER.error("Unable to get the type of the column '"+name+"' in the table '"+tableName+"'.\n"+
                    e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public long getSize(){
        try {
            Map map = dataSource.firstRow("SELECT count("+TableLocation.quoteIdentifier(name, isH2)+
                    ") FROM "+tableName.getTable());
            if(map != null && !map.isEmpty() && map.values().toArray()[0] instanceof Long){
                return (Long) map.values().toArray()[0];
            }
        } catch (SQLException e) {
            LOGGER.error("Unable to get the size of the column '"+name+"' in the table '"+tableName+"'.\n"+
                    e.getLocalizedMessage());
        }
        return -1;
    }

    @Override
    public boolean isSpatial(){
        return "GEOMETRY".equals(getType());
    }

    @Override
    public boolean isIndexed(){
        try {
            Map map = dataSource.firstRow("SELECT * FROM INFORMATION_SCHEMA.INDEXES " +
                            "WHERE INFORMATION_SCHEMA.INDEXES.TABLE_NAME=? " +
                            "AND INFORMATION_SCHEMA.INDEXES.TABLE_SCHEMA=? " +
                            "AND INFORMATION_SCHEMA.INDEXES.COLUMN_NAME=?;",
                    new Object[]{tableName.getTable(), tableName.getSchema("PUBLIC"), name});
            return map != null;
        } catch (SQLException e) {
            LOGGER.error("Unable to get the type of the column '"+name+"' in the table '"+tableName+"'.\n"+
                    e.getLocalizedMessage());
        }
        return false;
    }

    @Override
    public boolean isSpatialIndexed(){
        try {
            Map map = dataSource.firstRow("SELECT * FROM INFORMATION_SCHEMA.INDEXES " +
                            "WHERE INFORMATION_SCHEMA.INDEXES.TABLE_NAME=? " +
                            "AND INFORMATION_SCHEMA.INDEXES.TABLE_SCHEMA=? " +
                            "AND INFORMATION_SCHEMA.INDEXES.COLUMN_NAME=?;",
                    new Object[]{tableName.getTable(), tableName.getSchema("PUBLIC"), name});
            return map != null && map.get("INDEX_TYPE_NAME").toString().contains("SPATIAL");
        } catch (SQLException e) {
            LOGGER.error("Unable to get the type of the column '"+name+"' in the table '"+tableName+"'.\n"+
                    e.getLocalizedMessage());
        }
        return false;
    }

    @Override
    public boolean createIndex(){
        if(!isIndexed()) {
            try {
                dataSource.execute("CREATE INDEX ON "+tableName.toString(isH2)+" USING BTREE ("+
                        TableLocation.quoteIdentifier(name, isH2)+")");
                return true;
            } catch (SQLException e) {
                LOGGER.error("Unable to create an index on the column '" + name + "' in the table '" + tableName + "'.\n" +
                        e.getLocalizedMessage());
            }
        }
        return false;
    }

    @Override
    public boolean createSpatialIndex(){
        if(!isIndexed() && isSpatial()) {
            try {
                if(isH2){
                    dataSource.execute("CREATE INDEX ON "+tableName.toString(isH2)+" USING RTREE ("+name+")");
                }
                else {
                    dataSource.execute("CREATE INDEX ON "+tableName.toString(isH2)+" USING GIST ("+name+")");
                }
                return true;
            } catch (SQLException e) {
                LOGGER.error("Unable to create an index on the column '" + name + "' in the table '" + tableName + "'.\n" +
                        e.getLocalizedMessage());
            }
        }
        return false;
    }

    @Override
    public void dropIndex(){
        List<String> indexes = new ArrayList<>();
        try {
            List<? extends Map> list = dataSource.rows("SELECT INDEX_NAME FROM INFORMATION_SCHEMA.INDEXES " +
                            "WHERE INFORMATION_SCHEMA.INDEXES.TABLE_NAME=? " +
                            "AND INFORMATION_SCHEMA.INDEXES.TABLE_SCHEMA=? " +
                            "AND INFORMATION_SCHEMA.INDEXES.COLUMN_NAME=?;",
                    new Object[]{tableName.getTable(), tableName.getSchema("PUBLIC"), name});
            for(Map map : list){
                indexes.add(map.get("INDEX_NAME").toString());
            }
        } catch (SQLException e) {
            LOGGER.error("Unable to get the indexes of the column '"+name+"' in the table '"+tableName+"'.\n"+
                    e.getLocalizedMessage());
        }
        for(String index : indexes) {
            try {
                dataSource.execute("DROP INDEX IF EXISTS " + index);
            } catch (SQLException e) {
                LOGGER.error("Unable to drop the index '"+index+"' on the column '" + name + "' in the table '" +
                        tableName + "'.\n" + e.getLocalizedMessage());
            }
        }
    }

    @Override
    public Object invokeMethod(String name, Object args) {
        try {
            return getMetaClass().invokeMethod(this, name, args);
        } catch (MissingMethodException e) {
            LOGGER.debug("Unable to find the '"+name+"' methods, trying with the getter.\n"+e.getLocalizedMessage());
            try {
                return getMetaClass()
                    .invokeMethod(this, "get" + name.substring(0, 1).toUpperCase() + name.substring(1), args);
            } catch (MissingMethodException e2) {
                LOGGER.debug("Unable to find the '" + name + "' methods, trying with the is getter.\n" +
                        e.getLocalizedMessage());
            }
            return getMetaClass()
                            .invokeMethod(this, "is" + name.substring(0, 1).toUpperCase() + name.substring(1), args);
        }
    }

    @Override
    public Object getProperty(String propertyName) {
        return getMetaClass().getProperty(this, propertyName);
    }

    @Override
    public void setProperty(String propertyName, Object newValue) {
        getMetaClass().setProperty(this, propertyName, newValue);
    }

    @Override
    public MetaClass getMetaClass() {
        return this.metaClass;
    }

    @Override
    public void setMetaClass(MetaClass metaClass) {
        this.metaClass = metaClass;
    }
}
