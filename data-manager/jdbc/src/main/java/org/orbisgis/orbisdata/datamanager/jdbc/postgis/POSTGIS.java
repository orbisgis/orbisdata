package org.orbisgis.orbisdata.datamanager.jdbc.postgis;

import org.h2gis.functions.io.utility.FileUtil;
import org.h2gis.postgis_jts.ConnectionWrapper;
import org.h2gis.postgis_jts.StatementWrapper;
import org.h2gis.postgis_jts_osgi.DataSourceFactoryImpl;
import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.SFSUtilities;
import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.commons.annotations.Nullable;
import org.orbisgis.orbisdata.datamanager.api.dataset.DataBaseType;
import org.orbisgis.orbisdata.datamanager.api.dataset.IJdbcSpatialTable;
import org.orbisgis.orbisdata.datamanager.api.dataset.IJdbcTable;
import org.orbisgis.orbisdata.datamanager.jdbc.JdbcDataSource;
import org.orbisgis.orbisdata.datamanager.jdbc.TableLocation;
import org.osgi.service.jdbc.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * Implementation of the IJdbcDataSource interface dedicated to the usage of an postgres/postgis database.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2018)
 */
public class POSTGIS extends JdbcDataSource {

    private static final Logger LOGGER = LoggerFactory.getLogger(POSTGIS.class);
    private static final DataSourceFactory dataSourceFactory = new DataSourceFactoryImpl();

    private final ConnectionWrapper connectionWrapper;


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
     * Create an instance of {@link POSTGIS} from file
     *
     * @param file .properties file containing the information for the DataBase opening.
     * @return {@link POSTGIS} object if the DataBase has been successfully open, null otherwise.
     */
    @Nullable
    public static POSTGIS open(@NotNull File file) {
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
     * Create an instance of {@link POSTGIS} from properties
     *
     * @param properties Properties for the opening of the DataBase.
     * @return {@link POSTGIS} object if the DataBase has been successfully open, null otherwise.
     */
    @Nullable
    public static POSTGIS open(@NotNull Properties properties) {
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
     * Open the {@link POSTGIS} database with the given properties and return the corresponding {@link POSTGIS} object.
     *
     * @param properties Map of the properties to use for the database opening.
     * @return An instantiated {@link POSTGIS} object wrapping the Sql object connected to the database.
     */
    @Nullable
    public static POSTGIS open(Map<String, String> properties) {
        Properties props = new Properties();
        properties.forEach(props::put);
        return open(props);

    }

    /**
     * Open the {@link POSTGIS} database at the given path and return the corresponding {@link POSTGIS} object.
     *
     * @param path Path of the database to open.
     * @return An instantiated {@link POSTGIS} object wrapping the Sql object connected to the database.
     */
    @Nullable
    public static POSTGIS open(String path) {
        Map<String, String> map = new HashMap<>();
        map.put("databaseName", path);
        return open(map);
    }

    /**
     * Open the {@link POSTGIS} database at the given path and return the corresponding {@link POSTGIS} object.
     *
     * @param path     Path of the database to open.
     * @param user     User of the database.
     * @param password Password for the user.
     * @return An instantiated {@link POSTGIS} object wrapping the Sql object connected to the database.
     */
    @Nullable
    public static POSTGIS open(String path, String user, String password) {
        Map<String, String> map = new HashMap<>();
        map.put("databaseName", path);
        map.put("user", user);
        map.put("password", password);
        return open(map);
    }

    @Override
    public IJdbcTable getTable(@NotNull String tableName) {
        try {
            if (!JDBCUtilities.tableExists(connectionWrapper,
                    TableLocation.parse(tableName, getDataBaseType().equals(DataBaseType.H2GIS)).getTable())) {
                return null;
            }
        } catch (SQLException e) {
            LOGGER.error("Unable to find table.\n" + e.getLocalizedMessage());
            return null;
        }
        StatementWrapper statement;
        try {
            statement = (StatementWrapper) connectionWrapper.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        } catch (SQLException e) {
            LOGGER.error("Unable to create Statement.\n" + e.getLocalizedMessage());
            return null;
        }
        String query = String.format("SELECT * FROM %s", tableName);
        try {
            TableLocation location = new TableLocation(Objects.requireNonNull(getLocation()).toString(), tableName);
            Connection con = getConnection();
            if(con != null && !SFSUtilities.getGeometryFields(con, location).isEmpty()) {
                return new PostgisSpatialTable(new TableLocation(getLocation().toString(), tableName), query, statement, this);
            }
        } catch (SQLException e) {
            LOGGER.error("Unable to check if table '" + tableName + "' contains geometric fields.\n" +
                    e.getLocalizedMessage());
        }
        return new PostgisTable(new TableLocation(getLocation().toString(), tableName), query, statement, this);
    }

    @Override
    public IJdbcSpatialTable getSpatialTable(@NotNull String tableName) {
        try {
            if (!JDBCUtilities.tableExists(connectionWrapper,
                    TableLocation.parse(tableName, getDataBaseType().equals(DataBaseType.H2GIS)).getTable())) {
                return null;
            }
        } catch (SQLException e) {
            LOGGER.error("Unable to find table.\n" + e.getLocalizedMessage());
            return null;
        }
        StatementWrapper statement;
        try {
            statement = (StatementWrapper) connectionWrapper.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        } catch (SQLException e) {
            LOGGER.error("Unable to create Statement.\n" + e.getLocalizedMessage());
            return null;
        }
        String query = String.format("SELECT * FROM %s", tableName);
        try {
            TableLocation location = new TableLocation(Objects.requireNonNull(getLocation()).toString(), tableName);
            Connection con = getConnection();
            if(con != null && !SFSUtilities.getGeometryFields(con, new TableLocation(location.toString(), tableName)).isEmpty()) {
                return new PostgisSpatialTable(new TableLocation(this.getLocation().toString(), tableName), query, statement, this);
            }
        } catch (SQLException e) {
            LOGGER.error("Unable to check if table '" + tableName + "' contains geometric fields.\n" +
                    e.getLocalizedMessage());
        }
        LOGGER.error("The table '" + tableName + "' is not a spatial table.");
        return null;
    }

    @Override
    public boolean hasTable(@NotNull String tableName) {
        try {
            return JDBCUtilities.tableExists(connectionWrapper, TableLocation.parse(tableName, false).toString());
        } catch (SQLException ex) {
            LOGGER.error("Cannot find the table '" + tableName + ".\n" +
                    ex.getLocalizedMessage());
            return false;
        }
    }
}
