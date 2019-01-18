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
import org.h2gis.utilities.URIUtilities;
import org.orbisgis.datamanager.JdbcDataSource;
import org.orbisgis.datamanager.h2gis.H2GIS;
import org.orbisgis.datamanager.h2gis.H2gisLinked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import org.h2gis.functions.io.geojson.GeoJsonDriverFunction;
import org.h2gis.functions.io.json.JsonDriverFunction;

/**
 * IO methods for database
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public class IOMethods {

    private static final Logger LOGGER = LoggerFactory.getLogger(H2gisLinked.class);


    /**
     * Save a table to a file
     * @param connection
     * @param isH2
     * @param tableName
     * @param filePath
     * @param encoding
     * @return
     */
    public static boolean saveAsFile(Connection connection, boolean isH2,String tableName, String filePath, String encoding){
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
                    encoding="charset=UTF-8";
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
}
