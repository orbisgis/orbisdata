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
package org.orbisgis.orbisdata.datamanager.jdbc.h2gis;

import org.h2.Driver;
import org.h2.util.OsgiDataSourceFactory;
import org.h2gis.functions.factory.H2GISFunctions;
import org.h2gis.functions.io.utility.FileUtil;
import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.SFSUtilities;
import org.h2gis.utilities.wrapper.ConnectionWrapper;
import org.h2gis.utilities.wrapper.StatementWrapper;
import org.orbisgis.orbisdata.datamanager.jdbc.JdbcDataSource;
import org.orbisgis.orbisdata.datamanager.jdbc.TableLocation;
import org.orbisgis.orbisdata.datamanager.api.dataset.DataBaseType;
import org.orbisgis.orbisdata.datamanager.api.dataset.ISpatialTable;
import org.orbisgis.orbisdata.datamanager.api.dataset.ITable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Implementation of the IJdbcDataSource interface dedicated to the usage of an H2/H2GIS database.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2018-2019)
 */
public class H2GIS extends JdbcDataSource {

    private static final OsgiDataSourceFactory dataSourceFactory = new OsgiDataSourceFactory(new Driver());
    private static final Logger LOGGER = LoggerFactory.getLogger(H2GIS.class);

    private ConnectionWrapper connectionWrapper;

    /**
     * Private constructor to ensure the {@link #open(Map)} method.
     *
     * @param connection Connection to the database.
     */
    private H2GIS(Connection connection) {
        super(connection, DataBaseType.H2GIS);
        connectionWrapper = (ConnectionWrapper) connection;
    }

    /**
     * Create an instance of H2GIS from properties
     *
     * @param file .properties file containing the information for the DataBase opening.
     *
     * @return H2GIS object if the DataBase has been successfully open, null otherwise.
     */
    public static H2GIS open(File file) {
        try {
            if (FileUtil.isExtensionWellFormated(file, "properties")) {
                Properties prop = new Properties();
                FileInputStream fous = new FileInputStream(file);
                prop.load(fous);
                return open(prop);
            }
        } catch (IOException e) {
            LOGGER.error("Unable to read the properties file.\n" + e.getLocalizedMessage());
        }
        return null;
    }

    /**
     * Create an instance of H2GIS from properties
     *
     * @param properties Properties for the opening of the DataBase.
     *
     * @return H2GIS object if the DataBase has been successfully open, null otherwise.
     */
    public static H2GIS open(Properties properties) {
        Connection connection;
        // Init spatial
        try {
            connection = SFSUtilities.wrapConnection(dataSourceFactory.createDataSource(properties).getConnection());
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
            LOGGER.error("Unable to get DataBaseType metadata.\n" + e.getLocalizedMessage());
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
                H2GISFunctions.load(connection);
            } catch (SQLException e) {
                LOGGER.error("Unable to initialize H2GIS.\n" + e.getLocalizedMessage());
                return null;
            }
        }
        return new H2GIS(connection);
    }

    /**
     * Open the H2GIS database with the given properties and return the corresponding H2GIS object.
     *
     * @param properties Map of the properties to use for the database opening.
     *
     * @return An instantiated H2GIS object wrapping the Sql object connected to the database.
     */
    public static H2GIS open(Map<String, String> properties) {
        Properties props = new Properties();
        properties.forEach(props::put);
        return open(props);
    }

    /**
     * Open the H2GIS database at the given path and return the corresponding H2GIS object.
     *
     * @param path Path of the database to open.
     *
     * @return An instantiated H2GIS object wrapping the Sql object connected to the database.
     */
    public static H2GIS open(String path) {
        return open(path, "sa", "");
    }

    /**
     * Open the H2GIS database at the given path and return the corresponding H2GIS object.
     *
     * @param path Path of the database to open.
     * @param user User of the database.
     * @param password Password for the user.
     *
     * @return An instantiated H2GIS object wrapping the Sql object connected to the database.
     */
    public static H2GIS open(String path, String user, String password) {
        Map<String, String> map = new HashMap<>();
        map.put("databaseName", path);
        map.put("user", user);
        map.put("password", password);
        return open(map);
    }

    @Override
    public ITable getTable(String tableName) {
        String name = TableLocation.parse(tableName, getDataBaseType().equals(DataBaseType.H2GIS)).toString( getDataBaseType().equals(DataBaseType.H2GIS));
        try {
            if(!JDBCUtilities.tableExists(connectionWrapper,name)){
                return null;
            }
        } catch (SQLException e) {
            LOGGER.error("Unable to find table.\n"+e.getLocalizedMessage());
            return null;
        }
        StatementWrapper statement;
        try {
            statement = (StatementWrapper)connectionWrapper.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        } catch (SQLException e) {
            LOGGER.error("Unable to create Statement.\n"+e.getLocalizedMessage());
            return null;
        }
        String query = String.format("SELECT * FROM %s", name);
        try {
            if(!SFSUtilities.getGeometryFields(getConnection(), new TableLocation(this.getLocation().toString(), name)).isEmpty()) {
                return new H2gisSpatialTable(new TableLocation(this.getLocation().toString(), name), query, statement, this);
            }
        } catch (SQLException e) {
            LOGGER.error("Unable to check if table '" + name + "' contains geometric fields.\n" +
                    e.getLocalizedMessage());
        }
        return new H2gisTable(new TableLocation(this.getLocation().toString(), name), query, statement, this);
    }

    @Override
    public ISpatialTable getSpatialTable(String tableName) {
        ITable table = getTable(tableName);
        if(table instanceof ISpatialTable){
            return (ISpatialTable) table;
        }
        else {
            LOGGER.error("The table '" + table.getName() + "' is not a spatial table.");
            return null;
        }
    }

    @Override
    public boolean hasTable(String tableName) {
        try {
            return JDBCUtilities.tableExists(connectionWrapper,TableLocation.parse(tableName, true).toString());
        } catch (SQLException ex) {
            LOGGER.error("Cannot find the table '" + tableName + ".\n" +
                    ex.getLocalizedMessage());
            return false;
        }
    }
}
