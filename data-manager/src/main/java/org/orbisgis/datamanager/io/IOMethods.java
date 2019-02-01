package org.orbisgis.datamanager.io;

import org.h2gis.api.DriverFunction;
import org.h2gis.api.EmptyProgressVisitor;
import org.h2gis.functions.io.csv.CSVDriverFunction;
import org.h2gis.functions.io.dbf.DBFDriverFunction;
import org.h2gis.functions.io.geojson.GeoJsonReaderDriver;
import org.h2gis.functions.io.gpx.GPXDriverFunction;
import org.h2gis.functions.io.kml.KMLWriterDriver;
import org.h2gis.functions.io.osm.OSMDriverFunction;
import org.h2gis.functions.io.shp.SHPDriverFunction;
import org.h2gis.functions.io.tsv.TSVDriverFunction;
import org.h2gis.functions.io.utility.FileUtil;
import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.URIUtilities;
import org.orbisgis.datamanager.JdbcDataSource;
import org.orbisgis.datamanager.h2gis.H2GIS;
import org.osgi.service.jdbc.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import org.h2gis.functions.io.geojson.GeoJsonDriverFunction;
import org.h2gis.functions.io.json.JsonDriverFunction;

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


    /**
     * Save a table to a file
     * @param connection The connection to use for the save.
     * @param tableName Name of the table to save.
     * @param filePath Path of the destination file.
     * @param encoding Encoding of the file.
     *
     * @return True if the file has been saved, false otherwise.
     */
    public static boolean saveAsFile(Connection connection, String tableName, String filePath, String encoding){
        boolean isH2 = false;
        try {
            isH2 = JDBCUtilities.isH2DataBase(connection.getMetaData());
        } catch (SQLException e) {
            LOGGER.error("Unable to get the DataBase metadata.\n"+e.getLocalizedMessage());
        }
        File fileToSave = URIUtilities.fileFromString(filePath);
        try {
            DriverFunction driverFunction = null;
            if (FileUtil.isExtensionWellFormated(fileToSave, "shp")) {
                driverFunction = new SHPDriverFunction();
            }
            else if (FileUtil.isExtensionWellFormated(fileToSave, "geojson")) {
                driverFunction = new GeoJsonDriverFunction();
            }
            else if (FileUtil.isExtensionWellFormated(fileToSave, "json")) {
                driverFunction = new JsonDriverFunction();
            }
            else if (FileUtil.isExtensionWellFormated(fileToSave, "tsv")) {
                driverFunction = new TSVDriverFunction();
            }
            else if (FileUtil.isExtensionWellFormated(fileToSave, "csv")) {
                if(encoding==null){
                    encoding=ENCODING_OPTION + UTF_ENCODING;
                }
                driverFunction = new CSVDriverFunction();
            }
            else if (FileUtil.isExtensionWellFormated(fileToSave, "dbf")) {
                driverFunction = new DBFDriverFunction();
            }
            else if (FileUtil.isExtensionWellFormated(fileToSave, "kml") ||FileUtil.isExtensionWellFormated(fileToSave, "kmz")) {
                LOGGER.warn("Encoding is not yet supported for this file format");
                KMLWriterDriver driver = new KMLWriterDriver(connection, isH2?tableName.toUpperCase():tableName, fileToSave);
                driver.write(new EmptyProgressVisitor());
                return true;
            }
            else{
                LOGGER.error("Unsupported file format");
            }
            if(driverFunction != null){
                driverFunction.exportTable(connection, isH2?tableName.toUpperCase():tableName, fileToSave,
                        new EmptyProgressVisitor(), encoding);
                return true;
            }
        } catch (SQLException | IOException e) {
            LOGGER.error("Cannot save.\n"+e.getLocalizedMessage());
        }
        return false;
    }

    /**
     * Load a file to a H2GIS database
     *
     * @param filePath the path of the file
     * @param tableName the name of the table created to store the file
     * @param encoding an encoding value to read the file
     * @param delete true to delete the table if exists
     * @param dataSource the database
     */
    //TODO reformat the code once all the driver have the same importFile signature
    public static boolean loadFile(String filePath, String tableName, String encoding, boolean delete, JdbcDataSource dataSource) {
        Connection connection = dataSource.getConnection();
        File fileToImport = URIUtilities.fileFromString(filePath);
        try {
            DriverFunction driverFunction = null;
            if (FileUtil.isExtensionWellFormated(fileToImport, "shp")) {
                driverFunction = new SHPDriverFunction();
            }
            else if (FileUtil.isExtensionWellFormated(fileToImport, "geojson")) {
                dataSource.execute("DROP TABLE IF EXISTS " + tableName);
                GeoJsonReaderDriver driver = new GeoJsonReaderDriver(connection, fileToImport);
                driver.read(new EmptyProgressVisitor(), tableName);
                return true;
            }
            else if (FileUtil.isExtensionWellFormated(fileToImport, "csv")) {
                driverFunction = new CSVDriverFunction();
            }
            else if (FileUtil.isExtensionWellFormated(fileToImport, "dbf")) {
                driverFunction = new DBFDriverFunction();
            }
            else if (FileUtil.isExtensionWellFormated(fileToImport, "tsv")) {
                LOGGER.warn("Encoding is not yet supported for this file format");
                encoding = null;
                delete = false;
                driverFunction = new TSVDriverFunction();
            }
            else if (FileUtil.isExtensionWellFormated(fileToImport, "osm") ||
                        FileUtil.isExtensionWellFormated(fileToImport, "gz") ||
                        FileUtil.isExtensionWellFormated(fileToImport, "bz")) {
                LOGGER.warn("Encoding is not yet supported for this file format");
                encoding = null;
                driverFunction = new OSMDriverFunction();
            }
            else if (FileUtil.isExtensionWellFormated(fileToImport, "gpx")) {
                LOGGER.warn("Encoding is not yet supported for this file format");
                encoding = null;
                driverFunction = new GPXDriverFunction();
            }
            else{
                LOGGER.error("Unsupported file format");
            }
            if(driverFunction != null){
                dataSource.execute("DROP TABLE IF EXISTS " + tableName);
                if(encoding != null) {
                    driverFunction.importFile(connection, tableName, fileToImport, new EmptyProgressVisitor(), encoding);
                }
                else {
                    driverFunction.importFile(connection, tableName, fileToImport, new EmptyProgressVisitor(), delete);
                }
                return true;
            }
        } catch (SQLException | IOException e) {
            LOGGER.error("Cannot load.\n"+e.getLocalizedMessage());
        }
        return false;
    }

    /**
     * Load a table to a H2GIS database from another database
     *
     * @param properties external database properties to set up the connection
     * @param inputTableName the name of the table in the external database
     * @param outputTableName the name of the table in the H2GIS database
     * @param delete true to delete the table if exists
     * @param dataSource the H2GIS database
     */
    public static void loadTable(Map<String, String> properties, String inputTableName, String outputTableName,
                           boolean delete, H2GIS dataSource){
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
                            dataSource.execute("DROP TABLE IF EXISTS " + outputTableName);
                        } catch (SQLException e) {
                            LOGGER.error("Cannot drop the table.\n" + e.getLocalizedMessage());
                        }
                    }
                    try {
                        String tmpTableName =  "TMP_"+ System.currentTimeMillis();
                        dataSource.execute(String.format("CREATE LINKED TABLE %s('%s', '%s', '%s', '%s', '%s')",
                                tmpTableName, driverName, jdbc_url, user, password, inputTableName));
                        dataSource.execute(String.format("CREATE TABLE %s as SELECT * from %s", outputTableName,
                                tmpTableName));
                        dataSource.execute("DROP TABLE IF EXISTS " + tmpTableName);
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

    /**
     * Create a dynamic link from a file
     *
     * @param filePath the path of the file
     * @param tableName the name of the table created to store the file
     * @param delete true to delete the table if exists
     * @param h2GIS the H2GIS database
     */
    public static void link(String filePath, String tableName, boolean delete, H2GIS h2GIS) {
        if(delete){
            try {
                h2GIS.execute("DROP TABLE IF EXISTS "+ tableName);
            } catch (SQLException e) {
                LOGGER.error("Cannot drop the table.\n"+e.getLocalizedMessage());
            }
        }

        try {
            h2GIS.execute(String.format("CALL FILE_TABLE('%s','%s')", filePath, tableName));
        } catch (SQLException e) {
            LOGGER.error("Cannot link the file.\n"+e.getLocalizedMessage());
        }
    }
}
