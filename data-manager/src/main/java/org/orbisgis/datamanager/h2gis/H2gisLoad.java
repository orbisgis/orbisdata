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

import org.h2gis.utilities.TableLocation;
import org.h2gis.utilities.URIUtilities;
import org.h2gis.utilities.wrapper.ConnectionWrapper;
import org.h2gis.utilities.wrapper.StatementWrapper;
import org.orbisgis.datamanager.io.IOMethods;
import org.orbisgis.datamanagerapi.dataset.ISpatialTable;
import org.orbisgis.datamanagerapi.dataset.ITable;
import org.orbisgis.datamanagerapi.dataset.ITableWrapper;
import org.osgi.service.jdbc.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * This class is used to load a file to a H2GIS database
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2018-2019)
 */
public class H2gisLoad implements ITableWrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(H2gisLoad.class);
    private String tableName;
    private ConnectionWrapper connectionWrapper;

    /**
     * Load a table to a H2GIS database from another database
     *
     * @param properties external database properties to set up the connection
     * @param inputTableName the name of the table in the external database
     * @param outputTableName the name of the table in the H2GIS database
     * @param delete true to delete the table if exists
     * @param h2GIS the H2GIS database
     */
    public H2gisLoad(Map<String, String> properties, String inputTableName, String outputTableName, boolean delete, H2GIS h2GIS){
        create(properties, inputTableName, outputTableName, delete, h2GIS);
    }

    /**
     * Load a file to a H2GIS database
     *
     * @param filePath the path of the file.
     * @param delete true to delete the table if exists
     * @param h2GIS the H2GIS database
     */
    public H2gisLoad(String filePath, boolean delete, H2GIS h2GIS) {
        final String name = URIUtilities.fileFromString(filePath).getName();
        String tableName = name.substring(0, name.lastIndexOf(".")).toUpperCase();
        if (tableName.matches("^[a-zA-Z][a-zA-Z0-9_]*$")) {
            create(filePath,tableName, null, delete,h2GIS);
        } else {
            LOGGER.error("Unsupported file characters");
        }
    }

    /**
     * Load a file to a H2GIS database
     *
     * @param filePath the path of the file
     * @param tableName the name of the table created to store the file
     * @param encoding an encoding value to read the file
     * @param delete true to delete the table if exists
     * @param h2GIS the H2GIS database
     */
    public H2gisLoad(String filePath, String tableName, String encoding, boolean delete, H2GIS h2GIS) {
        create(filePath, tableName, encoding, delete, h2GIS);
    }

    @Override
    public Object asType(Class clazz) {
        if(tableName==null){
            return null;
        }
        if(tableName.isEmpty()){
            return null;
        }
        if (clazz == ITable.class){
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
            return new H2gisTable(new TableLocation(tableName), rs, statement);
        }
        else if(clazz == ISpatialTable.class){
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
            return new H2gisSpatialTable(new TableLocation(tableName), rs, statement);
        }
        return null;
    }

    /**
     * Load a file to a H2GIS database
     *
     * @param filePath the path of the file
     * @param tableName the name of the table created to store the file
     * @param encoding an encoding value to read the file
     * @param delete true to delete the table if exists
     * @param h2GIS the H2GIS database
     */
    private void create(String filePath, String tableName, String encoding, boolean delete, H2GIS h2GIS) {
        this.tableName=tableName;
        this.connectionWrapper =  h2GIS.getConnectionWrapper();
        IOMethods.loadFile(filePath, tableName, encoding, delete, h2GIS);
    }

    /**
     * Load a table to a H2GIS database from another database
     *
     * @param properties external database properties to set up the connection
     * @param inputTableName the name of the table in the external database
     * @param outputTableName the name of the table in the H2GIS database
     * @param delete true to delete the table if exists
     * @param h2GIS the H2GIS database
     */
    private void create(Map<String, String> properties, String inputTableName, String outputTableName, boolean delete, H2GIS h2GIS){
        String user = properties.get(DataSourceFactory.JDBC_USER);
        String password = properties.get(DataSourceFactory.JDBC_PASSWORD);
        String driverName = "";
        String jdbc_url = properties.get("url");
        if(jdbc_url!=null) {
            if (jdbc_url.startsWith("jdbc:")) {
                String url = jdbc_url.substring("jdbc:".length());
                if (url.startsWith("h2")) {
                    driverName = "org.h2.Driver";
                } else if (url.startsWith("postgresql")) {
                    driverName = "org.orbisgis.postgis_jts.Driver";
                }
                if(!driverName.isEmpty()) {
                    if (delete) {
                        try {
                            h2GIS.execute("DROP TABLE IF EXISTS " + outputTableName);
                        } catch (SQLException e) {
                            LOGGER.error("Cannot drop the table.\n" + e.getLocalizedMessage());
                        }
                    }
                    try {
                        String tmpTableName =  "TMP_"+ System.currentTimeMillis();
                        h2GIS.execute(String.format("CREATE LINKED TABLE %s('%s', '%s', '%s', '%s', '%s')", tmpTableName, driverName, jdbc_url, user, password, inputTableName));
                        h2GIS.execute(String.format("CREATE TABLE %s as SELECT * from %s", outputTableName, tmpTableName));
                        h2GIS.execute("DROP TABLE IF EXISTS " + tmpTableName);
                    } catch (SQLException e) {
                        LOGGER.error("Cannot load the table.\n" + e.getLocalizedMessage());
                    }
                }
                else{
                    LOGGER.error("This database is not yet supported");
                }
            }
            else{
                LOGGER.error("JDBC Url must start with jdbc:");
            }
        }
        else {
            LOGGER.error("The URL of the external database cannot be null");
        }
    }
}
