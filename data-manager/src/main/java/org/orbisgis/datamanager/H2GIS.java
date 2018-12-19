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

import groovy.sql.Sql;
import org.h2.Driver;
import org.h2.util.OsgiDataSourceFactory;
import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.SFSUtilities;
import org.h2gis.utilities.TableLocation;
import org.h2gis.utilities.wrapper.ConnectionWrapper;
import org.h2gis.utilities.wrapper.StatementWrapper;
import org.orbisgis.datamanagerapi.dataset.IDataSet;
import org.orbisgis.datamanagerapi.dataset.ISpatialTable;
import org.orbisgis.datamanagerapi.dataset.ITable;
import org.orbisgis.datamanagerapi.datasource.IJdbcDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * Implementation of the IJdbcDataSource interface dedicated to the usage of an H2/H2GIS database.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2018)
 */
public class H2GIS extends Sql implements IJdbcDataSource {

    private static final OsgiDataSourceFactory dataSourceFactory = new OsgiDataSourceFactory(new Driver());
    private static final Logger LOGGER = LoggerFactory.getLogger(H2GIS.class);

    private ConnectionWrapper connectionWrapper;


    private H2GIS(Connection connection) {
        super(connection);
        connectionWrapper = (ConnectionWrapper) connection;
    }

    public static H2GIS open(Map<String, String> properties) {
        Properties props = new Properties();
        properties.forEach(props::put);
        Connection connection;
        properties.put("DATABASE_EVENT_LISTENER","'org.orbisgis.h2triggers.H2DatabaseEventListener'");
        // Init spatial
        try {
            connection = SFSUtilities.wrapConnection(dataSourceFactory.createDataSource(props).getConnection());
        } catch (SQLException e) {
            LOGGER.error("Unable to create the DataSource.\n" + e.getLocalizedMessage());
            return null;
        }
        Statement st;
        try {
            st = connection.createStatement();
        } catch (SQLException e) {
            LOGGER.error("Unable to create a Statement.\n" + e.getLocalizedMessage());
            return null;
        }
        boolean isH2;
        try {
            isH2 = JDBCUtilities.isH2DataBase(connection.getMetaData());
        } catch (SQLException e) {
            LOGGER.error("Unable to get Database metadata.\n" + e.getLocalizedMessage());
            return null;
        }
        boolean tableExists;
        try {
            tableExists = !JDBCUtilities.tableExists(connection, "PUBLIC.GEOMETRY_COLUMNS");
        } catch (SQLException e) {
            LOGGER.error("Unable to check if table 'PUBLIC.GEOMETRY_COLUMNS' exists.\n" + e.getLocalizedMessage());
            return null;
        }
        if (isH2 && tableExists) {
            try {
                st.execute("CREATE ALIAS IF NOT EXISTS H2GIS_SPATIAL FOR\n" +
                        "    \"org.h2gis.functions.factory.H2GISFunctions.load\";\n" +
                        "CALL H2GIS_SPATIAL();");
            } catch (SQLException e) {
                LOGGER.error("Unable to initialize H2GIS.\n" + e.getLocalizedMessage());
                return null;
            }
        }
        return new H2GIS(connection);
    }

    @Override
    public void close() {
        super.close();
    }

    @Override
    public ITable getTable(String tableName) {
        StatementWrapper statement;
        try {
            statement = (StatementWrapper)connectionWrapper.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        } catch (SQLException e) {
            LOGGER.error("Unable to create Statement.\n"+e.getLocalizedMessage());
            return null;
        }
        ResultSet rs;
        try {
            rs = statement.executeQuery(String.format("SELECT * FROM %s", tableName));
        } catch (SQLException e) {
            LOGGER.error("Unable execute query.\n"+e.getLocalizedMessage());
            return null;
        }
        return new Table(new TableLocation(tableName), rs, statement, Database.H2GIS);
    }

    @Override
    public ISpatialTable getSpatialTable(String tableName) {
        StatementWrapper statement;
        try {
            statement = (StatementWrapper)connectionWrapper.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        } catch (SQLException e) {
            LOGGER.error("Unable to create Statement.\n"+e.getLocalizedMessage());
            return null;
        }
        ResultSet rs;
        try {
            rs = statement.executeQuery(String.format("SELECT * FROM %s", tableName));
        } catch (SQLException e) {
            LOGGER.error("Unable execute query.\n"+e.getLocalizedMessage());
            return null;
        }
        return new SpatialTable(new TableLocation(tableName), rs, statement, Database.H2GIS);
    }

    @Override
    public Collection<String> getTableNames() {
        try {
            return JDBCUtilities.getTableNames(connectionWrapper.getMetaData(), null, null, null, null);
        } catch (SQLException e) {
            LOGGER.error("Unable to get the database metadata.\n" + e.getLocalizedMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public IDataSet getDataSet(String dataSetName) {
        List<String> geomFields;
        try {
            geomFields = SFSUtilities.getGeometryFields(connectionWrapper, new TableLocation(dataSetName));
        } catch (SQLException e) {

            return getTable(dataSetName);
        }
        if(geomFields.size() >= 1){
            return getSpatialTable(dataSetName);
        }
        return getTable(dataSetName);
    }
}
