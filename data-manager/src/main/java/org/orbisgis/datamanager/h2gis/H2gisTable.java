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
package org.orbisgis.datamanager.h2gis;

import groovy.lang.MetaClass;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.TableLocation;
import org.h2gis.utilities.wrapper.ResultSetWrapper;
import org.h2gis.utilities.wrapper.StatementWrapper;
import org.orbisgis.datamanager.JdbcDataSource;
import org.orbisgis.datamanager.dsl.ConditionOrOptionBuilder;
import org.orbisgis.datamanager.dsl.OptionBuilder;
import org.orbisgis.datamanager.dsl.WhereBuilder;
import org.orbisgis.datamanager.io.IOMethods;
import org.orbisgis.datamanager.postgis.PostgisSpatialTable;
import org.orbisgis.datamanager.postgis.PostgisTable;
import org.orbisgis.datamanagerapi.dataset.Database;
import org.orbisgis.datamanagerapi.dataset.IJdbcTable;
import org.orbisgis.datamanagerapi.dataset.ISpatialTable;
import org.orbisgis.datamanagerapi.dataset.ITable;
import org.orbisgis.datamanagerapi.dsl.IConditionOrOptionBuilder;
import org.orbisgis.datamanagerapi.dsl.IOptionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class H2gisTable extends ResultSetWrapper implements IJdbcTable {

    private static final Logger LOGGER = LoggerFactory.getLogger(H2gisTable.class);

    /** Type of the database */
    private Database dataBase;
    /** Table location */
    private TableLocation tableLocation;
    /** MetaClass use for groovy methods/properties binding */
    private MetaClass metaClass;
    /** Map of the properties */
    private Map<String, Object> propertyMap;
    /** DataSource to execute query */
    private JdbcDataSource jdbcDataSource;

    /**
     * Main constructor.
     *
     * @param tableLocation TableLocation that identify the represented table.
     * @param resultSet ResultSet containing the data of the table.
     * @param statement Statement used to request the database.
     */
    public H2gisTable(TableLocation tableLocation, ResultSet resultSet, StatementWrapper statement,
                         JdbcDataSource jdbcDataSource) {
        super(resultSet, statement);
        try {
            resultSet.beforeFirst();
        } catch (SQLException e) {
            LOGGER.error("Unable to go before the first ResultSet row.\n" + e.getLocalizedMessage());
        }
        this.tableLocation = tableLocation;
        this.dataBase = Database.H2GIS;
        this.metaClass = InvokerHelper.getMetaClass(getClass());
        this.propertyMap = new HashMap<>();
        this.jdbcDataSource = jdbcDataSource;
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
    public ResultSetMetaData getMetadata(){
        try {
            return super.getMetaData();
        } catch (SQLException e) {
            LOGGER.error("Unable to get the metadata.\n" + e.getLocalizedMessage());
            return null;
        }
    }

    @Override
    public TableLocation getTableLocation() {
        return tableLocation;
    }

    @Override
    public Database getDbType() {
        return dataBase;
    }

    @Override
    public Map<String, Object> getPropertyMap() {
        return propertyMap;
    }
    
    @Override
    public Collection<String> getColumnNames() {
        try {
            return JDBCUtilities.getFieldNames(super.getMetaData());
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public boolean save(String filePath) {
        return save(filePath, null);
    }

    @Override
    public boolean save(String filePath, String encoding) {
        try {
            return IOMethods.saveAsFile(getStatement().getConnection(), getTableLocation().toString(true),
                    filePath,encoding);
        } catch (SQLException e) {
            LOGGER.error("Cannot save the table.\n" + e.getLocalizedMessage());
            return false;
        }
    }

    private String getQuery(){
        return "SELECT * FROM " + tableLocation.getTable().toUpperCase();
    }

    @Override
    public IConditionOrOptionBuilder where(String condition) {
        return new WhereBuilder(getQuery(), jdbcDataSource).where(condition);
    }

    @Override
    public IOptionBuilder groupBy(String... fields) {
        return new OptionBuilder(getQuery(), jdbcDataSource).groupBy(fields);
    }

    @Override
    public IOptionBuilder orderBy(Map<String, Order> orderByMap) {
        return new OptionBuilder(getQuery(), jdbcDataSource).orderBy(orderByMap);
    }

    @Override
    public IOptionBuilder orderBy(String field, Order order) {
        return new OptionBuilder(getQuery(), jdbcDataSource).orderBy(field, order);
    }

    @Override
    public IOptionBuilder orderBy(String field) {
        return new OptionBuilder(getQuery(), jdbcDataSource).orderBy(field);
    }

    @Override
    public IOptionBuilder limit(int limitCount) {
        return new OptionBuilder(getQuery(), jdbcDataSource).limit(limitCount);
    }

    @Override
    public Object asType(Class clazz) {
        try {
            if (clazz == ITable.class || clazz == H2gisTable.class) {
                return new H2gisTable(tableLocation, this, (StatementWrapper)this.getStatement(), jdbcDataSource);
            } else if (clazz == ISpatialTable.class || clazz == PostgisSpatialTable.class) {
                return new H2gisSpatialTable(tableLocation, this, (StatementWrapper)this.getStatement(),
                        jdbcDataSource);
            }
        } catch (SQLException e) {
            LOGGER.error("Unable to cast object.\n" + e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public ITable getTable() {
        return (ITable)asType(ITable.class);
    }

    @Override
    public ISpatialTable getSpatialTable() {
        return (ISpatialTable)asType(ISpatialTable.class);
    }
}