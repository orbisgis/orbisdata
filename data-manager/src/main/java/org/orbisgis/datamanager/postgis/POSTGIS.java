package org.orbisgis.datamanager.postgis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.SFSUtilities;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.datamanager.JdbcDataSource;
import org.orbisgis.datamanagerapi.dataset.DataBaseType;
import org.orbisgis.datamanagerapi.dataset.IDataSet;
import org.orbisgis.datamanagerapi.dataset.ISpatialTable;
import org.orbisgis.datamanagerapi.dataset.ITable;
import org.osgi.service.jdbc.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import org.h2gis.functions.io.utility.FileUtil;
import org.h2gis.postgis_jts.ConnectionWrapper;
import org.h2gis.postgis_jts.StatementWrapper;
import org.h2gis.postgis_jts_osgi.DataSourceFactoryImpl;
import org.h2gis.utilities.URIUtilities;
import org.orbisgis.datamanager.io.IOMethods;

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
        super(connection, DataBaseType.POSTGIS);
        connectionWrapper = (ConnectionWrapper) connection;
    }

    
    /**
     * Create an instance of POSTGIS from file
     *
     * @param file .properties file containing the information for the DataBase opening.
     *
     * @return POSTGIS object if the DataBase has been successfully open, null otherwise.
     */
    public static POSTGIS open(File file) {
        try {
            if (FileUtil.isExtensionWellFormated(file, "properties")) {
                Properties prop = new Properties();
                FileInputStream fous = new FileInputStream(file);
                prop.load(fous);
                return open(prop);
            }
        } catch (IOException e) {
            LOGGER.error("Unable to read the properties file.\n" + e.getLocalizedMessage());
            return null;
        }
        return null;
    }
    
    /**
     * Create an instance of POSTGIS from properties
     *
     * @param properties Properties for the opening of the DataBase.
     *
     * @return POSTGIS object if the DataBase has been successfully open, null otherwise.
     */
    public static POSTGIS open(Properties properties) {
        Connection connection;
        try {
            connection = dataSourceFactory.createDataSource(properties).getConnection();
        } catch (SQLException e) {
            LOGGER.error("Unable to create the DataSource.\n" + e.getLocalizedMessage());
            return null;
        }
        return new POSTGIS(connection);
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
        return open(props);
        
    }

    /**
     * Open the POSTGIS database at the given path and return the corresponding POSTGIS object.
     *
     * @param path Path of the database to open.
     *
     * @return An instantiated POSTGIS object wrapping the Sql object connected to the database.
     */
    public static POSTGIS open(String path) {
        Map<String, String> map = new HashMap<>();
        map.put("databaseName", path);
        return open(map);
    }

    /**
     * Open the POSTGIS database at the given path and return the corresponding POSTGIS object.
     *
     * @param path Path of the database to open.
     * @param user User of the database.
     * @param password Password for the user.
     *
     * @return An instantiated POSTGIS object wrapping the Sql object connected to the database.
     */
    public static POSTGIS open(String path, String user, String password) {
        Map<String, String> map = new HashMap<>();
        map.put("databaseName", path);
        map.put("user", user);
        map.put("password", password);
        return open(map);
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
        String query = String.format("SELECT * FROM %s", tableName);
        return new PostgisTable(new TableLocation(tableName), query, statement, this);
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
        String query = String.format("SELECT * FROM %s", tableName);
        return new PostgisSpatialTable(new TableLocation(tableName), query, statement, this);
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
