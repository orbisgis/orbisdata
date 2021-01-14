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
package org.orbisgis.orbisdata.datamanager.jdbc.io;

import org.h2gis.api.DriverFunction;
import org.h2gis.api.EmptyProgressVisitor;
import org.h2gis.functions.io.csv.CSVDriverFunction;
import org.h2gis.functions.io.dbf.DBFDriverFunction;
import org.h2gis.functions.io.geojson.GeoJsonDriverFunction;
import org.h2gis.functions.io.gpx.GPXDriverFunction;
import org.h2gis.functions.io.json.JsonDriverFunction;
import org.h2gis.functions.io.kml.KMLDriverFunction;
import org.h2gis.functions.io.osm.OSMDriverFunction;
import org.h2gis.functions.io.shp.SHPDriverFunction;
import org.h2gis.functions.io.tsv.TSVDriverFunction;
import org.h2gis.utilities.FileUtilities;
import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.TableLocation;
import org.h2gis.utilities.URIUtilities;
import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.commons.annotations.Nullable;
import org.orbisgis.orbisdata.datamanager.api.dataset.DataBaseType;
import org.orbisgis.orbisdata.datamanager.api.datasource.IJdbcDataSource;
import org.orbisgis.orbisdata.datamanager.jdbc.JdbcDataSource;
import org.osgi.service.jdbc.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Map;

/**
 * IO methods for database
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public class IOMethods {

    private static final Logger LOGGER = LoggerFactory.getLogger(IOMethods.class);

    private static final String ENCODING_OPTION = "charset=";
    private static final String UTF_ENCODING = "UTF-8";

    @Nullable
    private static DriverFunction getDriverFromFile(@NotNull File file) {
        String path = file.getAbsolutePath();
        String extension = "";
        int i = path.lastIndexOf(46);
        if (i >= 0) {
            extension = path.substring(i + 1);
        }
        switch (extension.toLowerCase()) {
            case "shp":
                return new SHPDriverFunction();
            case "geojson":
                return new GeoJsonDriverFunction();
            case "json":
                return new JsonDriverFunction();
            case "tsv":
                return new TSVDriverFunction();
            case "csv":
                return new CSVDriverFunction();
            case "dbf":
                return new DBFDriverFunction();
            case "kml":
            case "kmz":
                return new KMLDriverFunction();
            case "osm":
                return new OSMDriverFunction();
            case "gz":
                //Look for the following path .geojson.gz
                String sub_extension = path.substring(0, i);
                int subDot = sub_extension.lastIndexOf(".");
                String subExtension ="";
                if(subDot>=0) {
                    subExtension = path.substring(subDot + 1, i);
                }
                switch (subExtension.toLowerCase()) {
                    case "gpx":
                        return new GPXDriverFunction();
                    case "geojson":
                        return new GeoJsonDriverFunction();
                    case "json":
                        return new JsonDriverFunction();
                    case "tsv":
                        return new TSVDriverFunction();
                    case "dbf":
                        return new DBFDriverFunction();
                    case "osm":
                        return new OSMDriverFunction();
                    default:
                        LOGGER.error("Unsupported file format.\n"
                                + "Supported formats are : [ geojson.gz,json.gz, tsv.gz, dbf.gz, osm.gz, gpx.gz].");
                        return null;
                }
            case "gpx":
                return new GPXDriverFunction();
            default:
                LOGGER.error("Unsupported file format.\n"
                        + "Supported formats are : [shp, geojson,json, tsv, csv, dbf, kml, kmz, osm, gz, bz, gpx].");
                return null;
        }
    }

    @Nullable
    private static String unsupportedEncoding(@Nullable String encoding) {
        if (encoding != null && !encoding.isEmpty()) {
            LOGGER.warn("Encoding is not yet supported for this file format");
        }
        return null;
    }

    /**
     * Save a table to a file
     *
     * @param connection The connection to use for the save.
     * @param tableName Name of the table to save.
     * @param filePath Path of the destination file.
     * @param encoding Encoding of the file.
     * @return True if the file has been saved, false otherwise.
     */
    public static boolean saveAsFile(@NotNull Connection connection, @NotNull String tableName,
            @NotNull String filePath, @Nullable String encoding, boolean deleteFile) {
        String enc = encoding;
        boolean isH2 = false;
        try {
            isH2 = JDBCUtilities.isH2DataBase(connection);
        } catch (SQLException e) {
            LOGGER.error("Unable to get the DataBase metadata.\n", e);
        }
        File fileToSave = URIUtilities.fileFromString(filePath);
        DriverFunction driverFunction = getDriverFromFile(fileToSave);
        try {
            if (FileUtilities.isExtensionWellFormated(fileToSave, "csv")) {
                if (enc == null) {
                    enc = ENCODING_OPTION + UTF_ENCODING;
                }
            }
            if (driverFunction != null) {
                driverFunction.exportTable(connection, isH2 ? tableName.toUpperCase() : tableName, fileToSave,
                        enc, deleteFile, new EmptyProgressVisitor());
                return fileToSave.exists();
            }
        } catch (SQLException | IOException e) {
            LOGGER.error("Cannot save.\n", e);
        }
        return false;
    }

    /**
     * Load a file to a H2GIS database
     *
     * @param filePath The path of the file
     * @param tableName The name of the table created to store the file
     * @param encoding An encoding value to read the file
     * @param deleteTable True to delete the table if exists
     * @param dataSource The database
     */
    public static boolean loadFile(@NotNull String filePath, @NotNull String tableName, @Nullable String encoding,
            boolean deleteTable, @NotNull JdbcDataSource dataSource) {
        Connection connection = dataSource.getConnection();
        File fileToImport = URIUtilities.fileFromString(filePath);
        DriverFunction driverFunction = getDriverFromFile(fileToImport);
        try {
            if (driverFunction != null) {
                driverFunction.importFile(connection, tableName, fileToImport, encoding, deleteTable,
                        new EmptyProgressVisitor());
                if(!dataSource.getConnection().getAutoCommit()) {
                    dataSource.commit();
                }
                return true;
            }
        } catch (SQLException | IOException e) {
            LOGGER.error("Cannot load the file.\n", e.getMessage());
            try {
                dataSource.rollback();
            } catch (SQLException e1) {
                LOGGER.error("Unable to rollback.", e1);
            }
        }
        return false;
    }

    /**
     * Load a table to a H2GIS database from another database
     *
     * @param properties External database properties to set up the connection
     * @param inputTableName The name of the table in the external database
     * @param outputTableName The name of the table in the H2GIS database
     * @param delete True to delete the table if exists
     * @param jdbcDataSource The database
     */
    public static void loadTable(Map<String, String> properties, String inputTableName, String outputTableName,
            boolean delete, JdbcDataSource jdbcDataSource) {
        if (jdbcDataSource.getDataBaseType() != DataBaseType.H2GIS) {
            DataBaseType dbType = jdbcDataSource.getDataBaseType();
            String name = dbType.name();
            LOGGER.error(name + " database not supported for file link.");
            return;
        }
        String user = properties.getOrDefault(DataSourceFactory.JDBC_USER, "sa");
        String password = properties.getOrDefault(DataSourceFactory.JDBC_PASSWORD, "");
        String driverName = "";
        String jdbc_url = properties.get("url");
        if (jdbc_url != null) {
            if (jdbc_url.startsWith("jdbc:")) {
                String url = jdbc_url.substring("jdbc:".length());
                if (url.startsWith("h2")) {
                    driverName = "org.h2.Driver";
                } else if (url.startsWith("postgresql_h2")) {
                    driverName = "org.h2gis.postgis_jts.Driver";
                }
                if (!driverName.isEmpty()) {
                    if (delete) {
                        try {
                            jdbcDataSource.execute("DROP TABLE IF EXISTS " + outputTableName);
                            if(!jdbcDataSource.getConnection().getAutoCommit()) {
                                jdbcDataSource.commit();
                            }
                            try {
                                jdbcDataSource.rollback();
                            } catch (SQLException e1) {
                                LOGGER.error("Unable to rollback.", e1);
                            }
                        } catch (SQLException e) {
                            LOGGER.error("Cannot drop the table.\n", e);
                        }
                    }
                    try {
                        String tmpTableName = "TMP_" + System.currentTimeMillis();
                        jdbcDataSource.execute(String.format("CREATE LINKED TABLE %s('%s', '%s', '%s', '%s', '%s')",
                                tmpTableName, driverName, jdbc_url, user, password, inputTableName));
                        jdbcDataSource.execute(String.format("CREATE TABLE %s as SELECT * from %s", outputTableName,
                                tmpTableName));
                        jdbcDataSource.execute("DROP TABLE IF EXISTS " + tmpTableName);
                        if(!jdbcDataSource.getConnection().getAutoCommit()) {
                            jdbcDataSource.commit();
                        }
                    } catch (SQLException e) {
                        LOGGER.error("Cannot load the table.\n", e);
                        try {
                            jdbcDataSource.rollback();
                        } catch (SQLException e1) {
                            LOGGER.error("Unable to rollback.", e1);
                        }
                    }
                } else {
                    LOGGER.error("This database is not yet supported");
                }
            } else {
                LOGGER.error("JDBC Url must start with jdbc:");
            }
        } else {
            LOGGER.error("The URL of the external database cannot be null");
        }
    }

    /**
     * Create a dynamic link from a file
     *
     * @param filePath The path of the file
     * @param tableName The name of the table created to store the file
     * @param delete True to delete the table if exists
     * @param jdbcDataSource The database
     */
    public static void link(String filePath, String tableName, boolean delete, JdbcDataSource jdbcDataSource) {
        if (jdbcDataSource.getDataBaseType() != DataBaseType.H2GIS) {
            DataBaseType dbType = jdbcDataSource.getDataBaseType();
            String name = dbType.name();
            LOGGER.error(name + " database not supported for file link.");
            return;
        }
        if (delete) {
            try {
                jdbcDataSource.execute("DROP TABLE IF EXISTS " + tableName);
                if(!jdbcDataSource.getConnection().getAutoCommit()) {
                    jdbcDataSource.commit();
                }
            } catch (SQLException e) {
                LOGGER.error("Cannot drop the table.", e);
                try {
                    jdbcDataSource.rollback();
                } catch (SQLException e1) {
                    LOGGER.error("Unable to rollback.", e1);
                }
            }
        }

        try {
            jdbcDataSource.execute(String.format("CALL FILE_TABLE('%s','%s')", filePath, tableName));
            jdbcDataSource.commit();
        } catch (SQLException e) {
            LOGGER.error("Cannot link the file.\n", e);
            try {
                jdbcDataSource.rollback();
            } catch (SQLException e1) {
                LOGGER.error("Unable to rollback.", e1);
            }
        }
    }

    /**
     * Method to save a table into another database
     *
     * @param connection source database connection
     * @param tableLocation source table name
     * @param dbType source db type
     * @param deleteTable True to delete the target table if exists
     * @param outputdataSource target database
     * @param outputTableLocation target table name
     * @param batch_size batch size value before sending the data
     *
     * @return True if the table is saved
     */
    public static boolean saveInDB(Connection connection, TableLocation tableLocation, DataBaseType dbType,
            boolean deleteTable, IJdbcDataSource outputdataSource, TableLocation outputTableLocation, int batch_size) {
        if (outputdataSource == null) {
            LOGGER.error("The connection to the output database cannot be null.\n");
            return false;
        }
        if (batch_size <= 0) {
            LOGGER.error("The batch size must be greater than 0.\n");
            return false;
        }
        try {
            String outputTableName = tableLocation.toString(outputdataSource.getDataBaseType() == DataBaseType.H2GIS);
            if (outputTableLocation != null) {
                outputTableName = outputTableLocation.toString(outputdataSource.getDataBaseType() == DataBaseType.H2GIS);
            }
            String innputTableName = tableLocation.toString(dbType == DataBaseType.H2GIS);
            String ddlCommand = JDBCUtilities.createTableDDL(connection, innputTableName, outputTableName);
            if (!ddlCommand.isEmpty()) {
                Connection outputconnection = outputdataSource.getConnection();
                PreparedStatement preparedStatement = null;
                ResultSet inputRes = null;
                try {
                    Statement outputconnectionStatement = outputconnection.createStatement();
                    if (deleteTable) {
                        outputconnectionStatement.execute("DROP TABLE IF EXISTS " + outputTableName);
                    }
                    outputconnectionStatement.execute(ddlCommand);
                    Statement inputStat = connection.createStatement();
                    inputRes = inputStat.executeQuery("SELECT * FROM " + innputTableName);
                    int columnsCount = inputRes.getMetaData().getColumnCount();
                    StringBuilder insertTable = new StringBuilder("INSERT INTO ");
                    insertTable.append(outputTableName).append(" VALUES(?");
                    for (int i = 1; i < columnsCount; i++) {
                        insertTable.append(",").append("?");
                    }
                    insertTable.append(")");

                    preparedStatement = outputconnection.prepareStatement(insertTable.toString());
                    //Check the first row in order to limit the batch size if the query doesn't work
                    inputRes.next();
                    for (int i = 0; i < columnsCount; i++) {
                        preparedStatement.setObject(i + 1, inputRes.getObject(i + 1));
                    }
                    preparedStatement.execute();
                    long batchSize = 0;
                    while (inputRes.next()) {
                        for (int i = 0; i < columnsCount; i++) {
                            preparedStatement.setObject(i + 1, inputRes.getObject(i + 1));
                        }
                        preparedStatement.addBatch();
                        batchSize++;
                        if (batchSize >= batch_size) {
                            preparedStatement.executeBatch();
                            preparedStatement.clearBatch();
                            batchSize = 0;
                        }
                    }
                    if (batchSize > 0) {
                        preparedStatement.executeBatch();
                    }
                } catch (SQLException e) {
                    LOGGER.error("Cannot save the table "+tableLocation+ ".\n", e);
                    return false;
                } finally {
                    if (preparedStatement != null) {
                        preparedStatement.close();
                    }
                    if (inputRes != null) {
                        inputRes.close();
                    }
                }
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            LOGGER.error("Cannot save the table "+tableLocation+ ".\n", e);
            return false;
        }
    }

    /**
     * Method to save a table into another database
     *
     * @param connection source database connection
     * @param query source query from source database
     * @param dbType source db type
     * @param deleteTable True to delete the target table if exists
     * @param outputdataSource target database
     * @param outputTableLocation target table name
     * @param batch_size batch size value before sending the data
     * @return True if the table is saved
     */
    public static boolean saveInDB(Connection connection, String query, DataBaseType dbType,
            boolean deleteTable, IJdbcDataSource outputdataSource, TableLocation outputTableLocation, int batch_size) {
        if (outputdataSource == null) {
            LOGGER.error("The connection to the output database cannot be null.\n");
            return false;
        }
        if (batch_size <= 0) {
            LOGGER.error("The batch size must be greater than 0.\n");
            return false;
        }
        try {
            if (outputTableLocation == null) {
                LOGGER.error("The output table name cannot be null or empty\n");
                return false;
            }
            String outputTableName = outputTableLocation.toString(outputdataSource.getDataBaseType() == DataBaseType.H2GIS);
            Statement inputStat = connection.createStatement();
            ResultSet inputRes = inputStat.executeQuery(query);
            String ddlCommand = JDBCUtilities.createTableDDL(inputRes, outputTableName);
            if (!ddlCommand.isEmpty()) {
                Connection outputconnection = outputdataSource.getConnection();
                PreparedStatement preparedStatement = null;
                try {
                    Statement outputconnectionStatement = outputconnection.createStatement();
                    if (deleteTable) {
                        outputconnectionStatement.execute("DROP TABLE IF EXISTS " + outputTableName);
                    }
                    outputconnectionStatement.execute(ddlCommand);
                    int columnsCount = inputRes.getMetaData().getColumnCount();
                    StringBuilder insertTable = new StringBuilder("INSERT INTO ");
                    insertTable.append(outputTableName).append(" VALUES(?");
                    for (int i = 1; i < columnsCount; i++) {
                        insertTable.append(",").append("?");
                    }
                    insertTable.append(")");
                    outputconnection.setAutoCommit(false);
                    preparedStatement = outputconnection.prepareStatement(insertTable.toString());
                    //Check the first row in order to limit the batch size if the query doesn't work
                    inputRes.next();
                    for (int i = 0; i < columnsCount; i++) {
                        preparedStatement.setObject(i + 1, inputRes.getObject(i + 1));
                    }
                    preparedStatement.execute();
                    outputconnection.commit();
                    long batchSize = 0;
                    while (inputRes.next()) {
                        for (int i = 0; i < columnsCount; i++) {
                            preparedStatement.setObject(i + 1, inputRes.getObject(i + 1));
                        }
                        preparedStatement.addBatch();
                        batchSize++;
                        if (batchSize >= batch_size) {
                            preparedStatement.executeBatch();
                            connection.commit();
                            preparedStatement.clearBatch();
                            batchSize = 0;
                        }
                    }
                    if (batchSize > 0) {
                        preparedStatement.executeBatch();
                    }
                } catch (SQLException e) {
                    LOGGER.error("Cannot save the table "+ outputTableName+".\n", e);
                    return false;
                } finally {
                    outputconnection.setAutoCommit(true);
                    if (preparedStatement != null) {
                        preparedStatement.close();
                    }
                    if (inputRes != null) {
                        inputRes.close();
                    }
                }
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            LOGGER.error("Cannot save the table "+ outputTableLocation.toString()+".\n", e);
            return false;
        }
    }

    /**
     * Method to load the content of query from a source database in a target
     * database
     *
     * @param targetDataSource target database
     * @param sourceDataSource source database
     * @param targetTableName name of the table into the target database
     * @param query query to execute on the source database
     * @param deleteOutputIfExists True to delete the imported table if exists
     * @param batch_size batch size value before sending the data
     *
     * @return True if the table is saved
     */
    public static boolean loadFromDB(IJdbcDataSource targetDataSource, IJdbcDataSource sourceDataSource,
            TableLocation targetTableName, String query,
            boolean deleteOutputIfExists, int batch_size) {
        if (targetDataSource == null) {
            LOGGER.error("The connection to the output database cannot be null.\n");
            return false;
        }
        if (batch_size <= 0) {
            LOGGER.error("The batch size must be greater than 0.\n");
            return false;
        }
        try {
            String targetTableNameTmp = targetTableName.toString(targetDataSource.getDataBaseType() == DataBaseType.H2GIS);
            Connection sourceConnection = sourceDataSource.getConnection();
            Statement inputStat = sourceConnection.createStatement();
            ResultSet inputRes = inputStat.executeQuery(query);
            String ddlCommand = JDBCUtilities.createTableDDL(inputRes, targetTableNameTmp);
            if (!ddlCommand.isEmpty()) {
                Connection targetConnection = targetDataSource.getConnection();
                PreparedStatement preparedStatement = null;
                try {
                    Statement targetStatement = targetConnection.createStatement();
                    if (deleteOutputIfExists) {
                        targetStatement.execute("DROP TABLE IF EXISTS " + targetTableNameTmp);
                    }
                    targetStatement.execute(ddlCommand);
                    int columnsCount = inputRes.getMetaData().getColumnCount();
                    StringBuilder insertTable = new StringBuilder("INSERT INTO ");
                    insertTable.append(targetTableNameTmp).append(" VALUES(?");
                    for (int i = 1; i < columnsCount; i++) {
                        insertTable.append(",").append("?");
                    }
                    insertTable.append(")");
                    targetConnection.setAutoCommit(false);
                    preparedStatement = targetConnection.prepareStatement(insertTable.toString());
                    //Check the first row in order to limit the batch size if the query doesn't work
                    inputRes.next();
                    for (int i = 0; i < columnsCount; i++) {
                        preparedStatement.setObject(i + 1, inputRes.getObject(i + 1));
                    }
                    preparedStatement.execute();
                    targetConnection.commit();
                    long batchSize = 0;
                    while (inputRes.next()) {
                        for (int i = 0; i < columnsCount; i++) {
                            preparedStatement.setObject(i + 1, inputRes.getObject(i + 1));
                        }
                        preparedStatement.addBatch();
                        batchSize++;
                        if (batchSize >= batch_size) {
                            preparedStatement.executeBatch();
                            targetConnection.commit();
                            preparedStatement.clearBatch();
                            batchSize = 0;
                        }
                    }
                    if (batchSize > 0) {
                        preparedStatement.executeBatch();
                    }
                } catch (SQLException e) {
                    LOGGER.error("Cannot save the query $query.\n", e);
                    return false;
                } finally {
                    targetConnection.setAutoCommit(true);
                    if (preparedStatement != null) {
                        preparedStatement.close();
                    }
                    if (inputRes != null) {
                        inputRes.close();
                    }
                }
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            LOGGER.error("Cannot load the query $tableLocation from the datasource ${sourceDataSource.getLocation()}\n", e);
            return false;
        }
    }

    /**
     * Method to load a table from a source database in a target database
     *
     * @param targetDataSource target database
     * @param sourceDataSource source database
     * @param targetTableName name of the table into the target database
     * @param sourceTableName name of the imported table
     * @param deleteOutputIfExists True to delete the imported table if exists
     * @param batch_size batch size value before sending the data
     *
     * @return True if the table is saved
     */
    public static boolean loadFromDB(IJdbcDataSource targetDataSource, IJdbcDataSource sourceDataSource,
            TableLocation targetTableName, TableLocation sourceTableName,
            boolean deleteOutputIfExists, int batch_size) {
        if (targetDataSource == null) {
            LOGGER.error("The connection to the output database cannot be null.\n");
            return false;
        }
        if (batch_size <= 0) {
            LOGGER.error("The batch size must be greater than 0.\n");
            return false;
        }
        try {
            String targetTableNameTmp = targetTableName.toString(targetDataSource.getDataBaseType() == DataBaseType.H2GIS);
            String sourceTableNameTmp = sourceTableName.toString(sourceDataSource.getDataBaseType() == DataBaseType.H2GIS);
            String ddlCommand = JDBCUtilities.createTableDDL(sourceDataSource.getConnection(), sourceTableNameTmp, targetTableNameTmp);
            if (!ddlCommand.isEmpty()) {
                Connection targetConnection = targetDataSource.getConnection();
                Connection sourceConnection = sourceDataSource.getConnection();
                PreparedStatement preparedStatement = null;
                ResultSet inputRes = null;
                try {
                    Statement targetStatement = targetConnection.createStatement();
                    if (deleteOutputIfExists) {
                        targetStatement.execute("DROP TABLE IF EXISTS " + targetTableNameTmp);
                    }
                    targetStatement.execute(ddlCommand);
                    Statement inputStat = sourceConnection.createStatement();
                    inputRes = inputStat.executeQuery("SELECT * FROM " + sourceTableNameTmp);
                    int columnsCount = inputRes.getMetaData().getColumnCount();
                    StringBuilder insertTable = new StringBuilder("INSERT INTO ");
                    insertTable.append(targetTableNameTmp).append(" VALUES(?");
                    for (int i = 1; i < columnsCount; i++) {
                        insertTable.append(",").append("?");
                    }
                    insertTable.append(")");
                    targetConnection.setAutoCommit(false);
                    preparedStatement = targetConnection.prepareStatement(insertTable.toString());
                    //Check the first row in order to limit the batch size if the query doesn't work
                    inputRes.next();
                    for (int i = 0; i < columnsCount; i++) {
                        preparedStatement.setObject(i + 1, inputRes.getObject(i + 1));
                    }
                    preparedStatement.execute();
                    targetConnection.commit();
                    long batchSize = 0;
                    while (inputRes.next()) {
                        for (int i = 0; i < columnsCount; i++) {
                            preparedStatement.setObject(i + 1, inputRes.getObject(i + 1));
                        }
                        preparedStatement.addBatch();
                        batchSize++;
                        if (batchSize >= batch_size) {
                            preparedStatement.executeBatch();
                            targetConnection.commit();
                            preparedStatement.clearBatch();
                            batchSize = 0;
                        }
                    }
                    if (batchSize > 0) {
                        preparedStatement.executeBatch();
                    }
                } catch (SQLException e) {
                    LOGGER.error("Cannot save the table $tableLocation.\n", e);
                    return false;
                } finally {
                    targetConnection.setAutoCommit(true);
                    if (preparedStatement != null) {
                        preparedStatement.close();
                    }
                    if (inputRes != null) {
                        inputRes.close();
                    }
                }
                return true;
            }
            else {
                return false;
            }
        } catch (SQLException e) {
            LOGGER.error("Cannot load the table $tableLocation from the datasource ${sourceDataSource.getLocation()}\n" , e);
            return false;
        }
    }
}
