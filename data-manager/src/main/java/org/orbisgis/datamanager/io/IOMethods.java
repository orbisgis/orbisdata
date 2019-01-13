package org.orbisgis.datamanager.io;

import org.h2gis.api.EmptyProgressVisitor;
import org.h2gis.functions.io.csv.CSVDriverFunction;
import org.h2gis.functions.io.dbf.DBFDriverFunction;
import org.h2gis.functions.io.geojson.GeoJsonWriteDriver;
import org.h2gis.functions.io.json.JsonWriteDriver;
import org.h2gis.functions.io.kml.KMLWriterDriver;
import org.h2gis.functions.io.shp.SHPDriverFunction;
import org.h2gis.functions.io.tsv.TSVDriverFunction;
import org.h2gis.functions.io.utility.FileUtil;
import org.h2gis.utilities.URIUtilities;
import org.orbisgis.datamanager.h2gis.H2gisLinked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * IO methods for database
 */
public class IOMethods {

    private static final Logger LOGGER = LoggerFactory.getLogger(H2gisLinked.class);


    /**
     * Save a table to a file
     * @param connection
     * @param tableName
     * @param filePath
     * @param encoding
     * @return
     */
    public static boolean saveAsFile(Connection connection, boolean isH2,String tableName, String filePath, String encoding){
        File fileToImport = URIUtilities.fileFromString(filePath);
        try {
            if (FileUtil.isExtensionWellFormated(fileToImport, "shp")) {
                SHPDriverFunction driverFunction = new SHPDriverFunction();
                driverFunction.exportTable(connection, isH2?tableName.toUpperCase():tableName, fileToImport, new EmptyProgressVisitor(),encoding);
                return true;
            }
            else if (FileUtil.isExtensionWellFormated(fileToImport, "geojson")) {
                LOGGER.warn("Encoding is not yet supported for this file format");
                GeoJsonWriteDriver driverFunction = new GeoJsonWriteDriver(connection, isH2?tableName.toUpperCase():tableName, fileToImport);
                driverFunction.write(new EmptyProgressVisitor());
                return true;
            }
            else if (FileUtil.isExtensionWellFormated(fileToImport, "json")) {
                LOGGER.warn("Encoding is not yet supported for this file format");
                JsonWriteDriver driverFunction = new JsonWriteDriver(connection, isH2?tableName.toUpperCase():tableName, fileToImport);
                driverFunction.write(new EmptyProgressVisitor());
                return true;
            }
            else if (FileUtil.isExtensionWellFormated(fileToImport, "tsv")) {
                LOGGER.warn("Encoding is not yet supported for this file format");
                TSVDriverFunction driverFunction = new TSVDriverFunction();
                driverFunction.exportTable(connection, isH2?tableName.toUpperCase():tableName, fileToImport,new EmptyProgressVisitor());
                return true;
            }
            else if (FileUtil.isExtensionWellFormated(fileToImport, "csv")) {
                if(encoding==null){
                    encoding="charset=UTF-8";
                }
                CSVDriverFunction driverFunction = new CSVDriverFunction();
                driverFunction.exportTable(connection, isH2?tableName.toUpperCase():tableName, fileToImport,new EmptyProgressVisitor(),encoding);
                return true;
            }
            else if (FileUtil.isExtensionWellFormated(fileToImport, "dbf")) {
                DBFDriverFunction driverFunction = new DBFDriverFunction();
                driverFunction.exportTable(connection, isH2?tableName.toUpperCase():tableName, fileToImport,new EmptyProgressVisitor(),encoding);
                return true;
            }
            else if (FileUtil.isExtensionWellFormated(fileToImport, "kml") ||FileUtil.isExtensionWellFormated(fileToImport, "kmz")) {
                LOGGER.warn("Encoding is not yet supported for this file format");
                KMLWriterDriver driverFunction = new KMLWriterDriver(connection, isH2?tableName.toUpperCase():tableName, fileToImport);
                driverFunction.write(new EmptyProgressVisitor());
                return true;
            }
            else{
                LOGGER.error("Unsupported file format");
                return false;
            }
        } catch (SQLException | FileNotFoundException e) {
            LOGGER.error("Cannot save.\n"+e.getLocalizedMessage());
        } catch (IOException e) {
            LOGGER.error("Cannot save.\n"+e.getLocalizedMessage());
        }
        return false;
    }
}
