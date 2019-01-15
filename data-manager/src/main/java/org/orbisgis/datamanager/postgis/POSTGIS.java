package org.orbisgis.datamanager.postgis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.SFSUtilities;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.datamanager.JdbcDataSource;
import org.orbisgis.datamanagerapi.dataset.Database;
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
import org.orbisgis.datamanagerapi.dataset.ITableWrapper;

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
     * Create an instance of POSTGIS from file
     * @param fileName
     * @return
     */
    public static POSTGIS open(String fileName) {
        File file = URIUtilities.fileFromString(fileName);
        try {
            if (FileUtil.isFileImportable(file, "properties")) {
                Properties prop = new Properties();
                FileInputStream fous = new FileInputStream(file);
                prop.load(fous);
                return open(prop);
            }
        } catch (SQLException | IOException e) {
            LOGGER.error("Unable to read the properties file.\n" + e.getLocalizedMessage());
            return null;
        }
        return null;
    }
    
    /**
     * Create an instance of POSTGIS from properties
     * @param properties
     * @return 
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
    public boolean save(String tableName, String filePath) {
        return save(tableName,filePath,null);
    }

    @Override
    public boolean save(String tableName, String filePath, String encoding) {
        return IOMethods.saveAsFile(getConnection(), false, tableName, filePath, encoding);
    }

    @Override
    public ITableWrapper link(String filePath, String tableName, boolean delete) {
        LOGGER.error("This feature is not supported");
        return null;
    }

    @Override
    public ITableWrapper link(String filePath, String tableName) {
        LOGGER.error("This feature is not supported");
        return null;
    }

    @Override
    public ITableWrapper link(String filePath, boolean delete) {
        LOGGER.error("This feature is not supported");
        return null;
    }

    @Override
    public ITableWrapper link(String filePath) {
        LOGGER.error("This feature is not supported");
        return null;
    }

    @Override
    public ITableWrapper load(String filePath, String tableName, String encoding, boolean delete) {
            PostgisLoad  postgisLoad = new PostgisLoad();
            postgisLoad.create(filePath,tableName,encoding,delete, this );
            return postgisLoad;
    }

    @Override
    public ITableWrapper load(Map<String, String> properties, String tableName) {
        LOGGER.error("This feature is not yet supported");
        return null;
    }

    @Override
    public ITableWrapper load(Map<String, String> properties, String inputTableName, String outputTableName) {
        LOGGER.error("This feature is not yet supported");
        return null;
    }

    @Override
    public ITableWrapper load(Map<String, String> properties, String inputTableName, boolean delete) {
        LOGGER.error("This feature is not yet supported");
        return null;
    }

    @Override
    public ITableWrapper load(Map<String, String> properties, String inputTableName, String outputTableName, boolean delete) {
        LOGGER.error("This feature is not yet supported");
        return null;
    }

    @Override
    public ITableWrapper load(String filePath, String tableName) {
        return load(filePath, tableName, null,false);
    }

    @Override
    public ITableWrapper load(String filePath, String tableName, boolean delete) {
        return load(filePath, tableName, null, delete);
    }

    @Override
    public ITableWrapper load(String filePath,boolean delete) {
        PostgisLoad postgisLoad =  new PostgisLoad();
        postgisLoad.create(filePath, delete, this);
        return postgisLoad;
    }

    @Override
    public ITableWrapper load(String filePath) {
        return load(filePath, false);
    }

    /**
     * Return the current ConnectionWrapper
     * @return ConnectionWrapper
     */
    public ConnectionWrapper getConnectionWrapper() {
        return connectionWrapper;
    }


}
