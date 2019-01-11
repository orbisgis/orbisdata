package org.orbisgis.datamanager.postgis;

import org.h2gis.api.EmptyProgressVisitor;
import org.h2gis.functions.io.csv.CSVDriverFunction;
import org.h2gis.functions.io.dbf.DBFDriverFunction;
import org.h2gis.functions.io.geojson.GeoJsonReaderDriver;
import org.h2gis.functions.io.geojson.GeoJsonWriteDriver;
import org.h2gis.functions.io.json.JsonWriteDriver;
import org.h2gis.functions.io.kml.KMLWriterDriver;
import org.h2gis.functions.io.shp.SHPDriverFunction;
import org.h2gis.functions.io.tsv.TSVDriverFunction;
import org.h2gis.functions.io.utility.FileUtil;
import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.SFSUtilities;
import org.h2gis.utilities.TableLocation;
import org.h2gis.utilities.URIUtilities;
import org.orbisgis.datamanager.JdbcDataSource;
import org.orbisgis.datamanagerapi.dataset.Database;
import org.orbisgis.datamanagerapi.dataset.IDataSet;
import org.orbisgis.datamanagerapi.dataset.ISpatialTable;
import org.orbisgis.datamanagerapi.dataset.ITable;
import org.orbisgis.postgis_jts.ConnectionWrapper;
import org.orbisgis.postgis_jts.StatementWrapper;
import org.orbisgis.postgis_jts_osgi.DataSourceFactoryImpl;
import org.osgi.service.jdbc.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Implementation of the IJdbcDataSource interface dedicated to the usage of an postgres/postgis database.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2018)
 */
public class POSTGIS extends JdbcDataSource {

    private static final Logger LOGGER = LoggerFactory.getLogger(POSTGIS.class);
    private static final DataSourceFactory dataSourceFactory = new DataSourceFactoryImpl();

    private ConnectionWrapper connectionWrapper;

    /**
     * Private constructor to ensure the {@link #open(Map)} method.
     *
     * @param connection Connection to the database.
     */
    private POSTGIS(Connection connection) {
        super(connection, Database.POSTGIS);
        connectionWrapper = (ConnectionWrapper) connection;
    }

    /**
     * Open the POSTGIS database with the given properties and return the corresponding POSTGIS object.
     *
     * @param properties Map of the properties to use for the database opening.
     *
     * @return An instantiated POSTGIS object wrapping the Sql object connected to the database.
     */
    public static POSTGIS open(Map<String, String> properties) {
        Properties props = new Properties();
        properties.forEach(props::put);
        Connection connection;
        try {
            connection = dataSourceFactory.createDataSource(props).getConnection();
        } catch (SQLException e) {
            LOGGER.error("Unable to create the DataSource.\n" + e.getLocalizedMessage());
            return null;
        }
        return new POSTGIS(connection);
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
        return new PostgisTable(new TableLocation(tableName), rs, statement);
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
        return new PostgisSpatialTable(new TableLocation(tableName), rs, statement);
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

    @Override
    public void save(String tableName, String filePath) {
        save(tableName,filePath,null);
    }

    @Override
    public void save(String tableName, String filePath, String encoding) {
        File fileToImport = URIUtilities.fileFromString(filePath);
        try {
            if (FileUtil.isExtensionWellFormated(fileToImport, "shp")) {
                SHPDriverFunction driverFunction = new SHPDriverFunction();
                driverFunction.exportTable(connectionWrapper, tableName, fileToImport, new EmptyProgressVisitor(),encoding);
            }
            else if (FileUtil.isExtensionWellFormated(fileToImport, "geojson")) {
                LOGGER.warn("Encoding is not yet supported for this file format");
                GeoJsonWriteDriver driverFunction = new GeoJsonWriteDriver(connectionWrapper, tableName, fileToImport);
                driverFunction.write(new EmptyProgressVisitor());
            }
            else if (FileUtil.isExtensionWellFormated(fileToImport, "json")) {
                LOGGER.warn("Encoding is not yet supported for this file format");
                JsonWriteDriver driverFunction = new JsonWriteDriver(connectionWrapper, tableName, fileToImport);
                driverFunction.write(new EmptyProgressVisitor());
            }
            else if (FileUtil.isExtensionWellFormated(fileToImport, "tsv")) {
                LOGGER.warn("Encoding is not yet supported for this file format");
                TSVDriverFunction driverFunction = new TSVDriverFunction();
                driverFunction.exportTable(connectionWrapper, tableName, fileToImport,new EmptyProgressVisitor());
            }
            else if (FileUtil.isExtensionWellFormated(fileToImport, "csv")) {
                if(encoding==null){
                    encoding="charset=UTF-8";
                }
                CSVDriverFunction driverFunction = new CSVDriverFunction();
                driverFunction.exportTable(connectionWrapper, tableName, fileToImport,new EmptyProgressVisitor(),encoding);
            }
            else if (FileUtil.isExtensionWellFormated(fileToImport, "dbf")) {
                DBFDriverFunction driverFunction = new DBFDriverFunction();
                driverFunction.exportTable(connectionWrapper, tableName, fileToImport,new EmptyProgressVisitor(),encoding);
            }
            else if (FileUtil.isExtensionWellFormated(fileToImport, "kml") ||FileUtil.isExtensionWellFormated(fileToImport, "kmz")) {
                LOGGER.warn("Encoding is not yet supported for this file format");
                KMLWriterDriver driverFunction = new KMLWriterDriver(connectionWrapper, tableName, fileToImport);
                driverFunction.write(new EmptyProgressVisitor());
            }
            else{
                LOGGER.error("Unsupported file format");
            }
        } catch (SQLException | FileNotFoundException e) {
            LOGGER.error("Cannot load.\n"+e.getLocalizedMessage());
        } catch (IOException e) {
            LOGGER.error("Cannot load.\n"+e.getLocalizedMessage());
        }
    }

    @Override
    public void load(String filePath, String tableName, String encoding, boolean delete) {
        if(delete){
            try {
                execute("DROP TABLE IF EXISTS "+ tableName);
            } catch (SQLException e) {
                LOGGER.error("Cannot drop the table.\n"+e.getLocalizedMessage());
            }
        }
        File fileToImport = URIUtilities.fileFromString(filePath);
        try {
            if (FileUtil.isFileImportable(fileToImport, "shp")) {
                SHPDriverFunction driverFunction = new SHPDriverFunction();
                driverFunction.importFile(connectionWrapper, tableName, fileToImport, new EmptyProgressVisitor(),encoding);
            }
            else if (FileUtil.isFileImportable(fileToImport, "geojson")) {
                GeoJsonReaderDriver driverFunction = new GeoJsonReaderDriver(connectionWrapper, fileToImport);
                driverFunction.read(new EmptyProgressVisitor(), tableName);
            }
            else if (FileUtil.isFileImportable(fileToImport, "csv")) {
                if(encoding==null){
                    encoding="charset=UTF-8";
                }
                CSVDriverFunction driverFunction = new CSVDriverFunction();
                driverFunction.importFile(connectionWrapper, tableName, fileToImport,new EmptyProgressVisitor(),encoding);
            }
            else if (FileUtil.isFileImportable(fileToImport, "dbf")) {
                DBFDriverFunction driverFunction = new DBFDriverFunction();
                driverFunction.importFile(connectionWrapper, tableName, fileToImport,new EmptyProgressVisitor(),encoding);
            }
            else if (FileUtil.isFileImportable(fileToImport, "tsv")) {
                LOGGER.warn("Encoding is not yet supported for this file format");
                TSVDriverFunction driverFunction = new TSVDriverFunction();
                driverFunction.importFile(connectionWrapper, tableName, fileToImport,new EmptyProgressVisitor());
            }
            else{
                LOGGER.error("Unsupported file format");
            }
        } catch (SQLException | FileNotFoundException e) {
            LOGGER.error("Cannot load.\n"+e.getLocalizedMessage());
        } catch (IOException e) {
            LOGGER.error("Cannot load.\n"+e.getLocalizedMessage());
        }
    }
    @Override
    public void load(String filePath, String tableName) {
        load(filePath, tableName, null,false);
    }

    @Override
    public void load(String filePath, String tableName, boolean delete) {
        load(filePath, tableName, null, delete);
    }

    @Override
    public void load(String filePath) {
        load(filePath, false);
    }

    @Override
    public void load(String filePath, boolean delete) {
        final String name = URIUtilities.fileFromString(filePath).getName();
        String tableName = name.substring(0, name.lastIndexOf(".")).toUpperCase();
        if (tableName.matches("^[a-zA-Z][a-zA-Z0-9_]*$")) {
            if(delete){
                try {
                    execute("DROP TABLE IF EXISTS "+ tableName);
                } catch (SQLException e) {
                    LOGGER.error("Cannot drop the table.\n"+e.getLocalizedMessage());
                }
            }
            load(filePath,tableName);
        } else {
            LOGGER.error("Unsupported file characters");
        }
    }

}
