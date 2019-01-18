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
package org.orbisgis.datamanager.postgis;

import groovy.lang.MetaClass;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.datamanager.JdbcDataSource;
import org.orbisgis.datamanager.dsl.ConditionOrOptionBuilder;
import org.orbisgis.datamanager.io.IOMethods;
import org.orbisgis.datamanagerapi.dataset.Database;
import org.orbisgis.datamanagerapi.dataset.IJdbcTable;
import org.h2gis.postgis_jts.ResultSetWrapper;
import org.h2gis.postgis_jts.StatementWrapper;
import org.orbisgis.datamanagerapi.dsl.IConditionOrOptionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.h2gis.utilities.JDBCUtilities;

/**
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2018-2019)
 */
public class PostgisTable extends ResultSetWrapper implements IJdbcTable {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostgisTable.class);

    /** Type of the database */
    private Database dataBase;
    /** Table location */
    private TableLocation tableLocation;
    /** MetaClass use for groovy methods/properties binding */
    private MetaClass metaClass;
    /** Map of the properties */
    private Map<String, Object> propertyMap;
    /** Filter query */
    private StringBuilder query = new StringBuilder();
    /** DataSource to execute query */
    private JdbcDataSource jdbcDataSource;

    public PostgisTable(TableLocation tableLocation, ResultSet resultSet, StatementWrapper statement,
                           JdbcDataSource jdbcDataSource) {
        super(statement, resultSet);
        try {
            resultSet.beforeFirst();
        } catch (SQLException e) {
            LOGGER.error("Unable to go before the first ResultSet row.\n" + e.getLocalizedMessage());
        }
        this.tableLocation = tableLocation;
        this.dataBase = Database.POSTGIS;
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
    public Database getDataBase() {
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
            LOGGER.error("Unable to get the column names.\n" + e.getLocalizedMessage());
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
            return IOMethods.saveAsFile(getStatement().getConnection(),false, getTableLocation().toString(false),filePath,encoding);
        } catch (SQLException e) {
            LOGGER.error("Cannot save the table.\n" + e.getLocalizedMessage());
            return false;
        }
    }

    @Override
    public IConditionOrOptionBuilder where(String condition) {
        query = new StringBuilder();
        query.append("SELECT * FROM ");
        query.append(tableLocation.getTable().toLowerCase());
        query.append(" WHERE ");
        query.append(condition);
        return new ConditionOrOptionBuilder(query.toString(), jdbcDataSource);
    }
}