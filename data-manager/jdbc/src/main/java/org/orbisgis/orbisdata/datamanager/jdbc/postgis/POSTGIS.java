package org.orbisgis.orbisdata.datamanager.jdbc.postgis;

import groovy.lang.GString;
import org.h2gis.postgis_jts_osgi.DataSourceFactoryImpl;
import org.h2gis.utilities.FileUtilities;
import org.h2gis.utilities.GeometryTableUtilities;
import org.h2gis.utilities.JDBCUtilities;
import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.commons.annotations.Nullable;
import org.orbisgis.orbisdata.datamanager.api.dataset.DataBaseType;
import org.orbisgis.orbisdata.datamanager.api.dataset.IJdbcSpatialTable;
import org.orbisgis.orbisdata.datamanager.api.dataset.IJdbcTable;
import org.orbisgis.orbisdata.datamanager.api.dataset.ISpatialTable;
import org.orbisgis.orbisdata.datamanager.jdbc.JdbcDataSource;
import org.orbisgis.orbisdata.datamanager.jdbc.JdbcSpatialTable;
import org.orbisgis.orbisdata.datamanager.jdbc.TableLocation;
import org.osgi.service.jdbc.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * Implementation of the IJdbcDataSource interface dedicated to the usage of an postgres/postgis database.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC 2018 / Chaire GEOTERA 2020)
 */
public class POSTGIS extends JdbcDataSource {

    private static final Logger LOGGER = LoggerFactory.getLogger(POSTGIS.class);
    private static final DataSourceFactory dataSourceFactory = new DataSourceFactoryImpl();

    /**
     * Private constructor.
     *
     * @param connection {@link Connection} to the database.
     */
    private POSTGIS(Connection connection) {
        super(connection, DataBaseType.POSTGIS);
    }

    /**
     * Private constructor.
     *
     * @param dataSource {@link DataSource} to the database.
     */
    private POSTGIS(DataSource dataSource) {
        super(dataSource, DataBaseType.POSTGIS);
    }

    /**
     * Create an instance of {@link POSTGIS} from a {@link Connection}
     *
     * @param connection {@link Connection} of the DataBase.
     * @return {@link POSTGIS} object if the DataBase has been successfully open, null otherwise.
     */
    @Nullable
    public static POSTGIS open(@Nullable Connection connection) {
        if (connection != null) {
            return new POSTGIS(connection);
        } else {
            return null;
        }
    }

    /**
     * Create an instance of {@link POSTGIS} from a {@link DataSource}
     *
     * @param dataSource {@link Connection} of the DataBase.
     * @return {@link POSTGIS} object if the DataBase has been successfully open, null otherwise.
     */
    @Nullable
    public static POSTGIS open(@Nullable DataSource dataSource) {
        if (dataSource != null) {
            return new POSTGIS(dataSource);
        } else {
            return null;
        }
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
            if (FileUtilities.isExtensionWellFormated(file, "properties")) {
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
    @Nullable
    public IJdbcTable getTable(@NotNull String nameOrQuery, @NotNull Statement statement) {
        return getTable(nameOrQuery, null, statement);
    }

    @Nullable
    @Override
    public IJdbcTable getTable(@NotNull GString nameOrQuery, @NotNull Statement statement) {
        if(nameOrQuery.getValueCount() == 0 ||
                !nameOrQuery.toString().startsWith("(") && !nameOrQuery.toString().endsWith("(")) {
            return getTable(nameOrQuery.toString(), statement);
        }
        List<Object> params = this.getParameters(nameOrQuery);
        String sql = this.asSql(nameOrQuery, params);
        return getTable(sql, params, statement);
    }

    @Override
    @Nullable
    public IJdbcTable getTable(@NotNull String nameOrQuery, @Nullable List<Object> params,
                               @NotNull Statement statement) {
        Connection connection = getConnection();
        String query;
        TableLocation location;
        if(!nameOrQuery.startsWith("(") && !nameOrQuery.endsWith(")")) {
            org.h2gis.utilities.TableLocation inputLocation = TableLocation.parse(nameOrQuery, false);
            try {
                if (!JDBCUtilities.tableExists(connection, inputLocation)) {
                    LOGGER.error("Unable to find table " + nameOrQuery);
                    return null;
                }
            } catch (SQLException e) {
                LOGGER.error("Unable to find table.\n" + e.getLocalizedMessage());
                return null;
            }
            query = String.format("SELECT * FROM %s", inputLocation);
            location = new TableLocation(Objects.requireNonNull(getLocation()).toString(), inputLocation.getCatalog(), inputLocation.getSchema(), inputLocation.getTable());
        }
        else {
            query = nameOrQuery;
            location = null;
        }
        try {
            if(connection != null){
                if(location != null){
                    if(GeometryTableUtilities.hasGeometryColumn(connection, location)) {
                        return new PostgisSpatialTable(location, query, statement, params, this);
                    }
                    else {
                        return new PostgisTable(location, query, statement, params, this);
                    }
                }
                else {
                    ResultSet rs = statement instanceof PreparedStatement ?
                            ((PreparedStatement)statement).executeQuery() :
                            statement.executeQuery(query);
                    boolean hasGeom = GeometryTableUtilities.hasGeometryColumn(rs);
                    if(hasGeom) {
                        return new PostgisSpatialTable(location, query, statement, params, this);
                    }
                    else {
                        return new PostgisTable(location, query, statement, params, this);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Unable to check if table '" + location + "' contains geometric fields.\n" +
                    e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    @Nullable
    public IJdbcTable getTable(@NotNull String tableName) {
        return getTable(tableName, getStatement( tableName));
    }

    @Override
    public IJdbcTable getTable(GString nameOrQuery) {
        if(nameOrQuery.getValueCount() == 0 ||
                !nameOrQuery.toString().startsWith("(") && !nameOrQuery.toString().endsWith("(")) {
            return getTable(nameOrQuery.toString());
        }
        List<Object> params = this.getParameters(nameOrQuery);
        String sql = this.asSql(nameOrQuery, params);
        return getTable(sql, params);
    }

    @Override
    public IJdbcTable getTable(String query, List<Object> params) {
        if(params == null || params.isEmpty()) {
            return getTable(query);
        }
        return getTable(query, params, getPreparedStatement( query));
    }

    @Override
    @Nullable
    public IJdbcSpatialTable getSpatialTable(@NotNull String tableName, @NotNull Statement statement) {
        IJdbcTable table = getTable(tableName, statement);
        if (table instanceof ISpatialTable) {
            return (JdbcSpatialTable) table;
        } else {
            String name = "";
            if(table != null){
                name = "'" + table.getName() + "' ";
            }
            LOGGER.error("The table " + name + "is not a spatial table.");
            return null;
        }
    }

    @Nullable
    @Override
    public IJdbcSpatialTable getSpatialTable(@NotNull GString nameOrQuery, @NotNull Statement statement) {
        if(nameOrQuery.getValueCount() == 0 ||
                !nameOrQuery.toString().startsWith("(") && !nameOrQuery.toString().endsWith("(")) {
            return getSpatialTable(nameOrQuery.toString(), statement);
        }
        List<Object> params = this.getParameters(nameOrQuery);
        String sql = this.asSql(nameOrQuery, params);
        return getSpatialTable(sql, params, statement);
    }

    @Nullable
    @Override
    public IJdbcSpatialTable getSpatialTable(@NotNull String nameOrQuery, @Nullable List<Object> params, @NotNull Statement statement) {
        IJdbcTable table = getTable(nameOrQuery, params, statement);
        if (table instanceof ISpatialTable) {
            return (JdbcSpatialTable) table;
        } else {
            String name = "";
            if(table != null){
                name = "'" + table.getName() + "' ";
            }
            LOGGER.error("The table " + name + "is not a spatial table.");
            return null;
        }
    }

    @Nullable
    @Override
    public IJdbcSpatialTable getSpatialTable(@NotNull String query, @Nullable List<Object> params) {
        IJdbcTable table = getTable(query, params);
        if (table instanceof ISpatialTable) {
            return (JdbcSpatialTable) table;
        } else {
            String name = "";
            if(table != null){
                name = "'" + table.getName() + "' ";
            }
            LOGGER.error("The table " + name + "is not a spatial table.");
            return null;
        }
    }

    @Override
    @Nullable
    public IJdbcSpatialTable getSpatialTable(@NotNull String tableName) {
        IJdbcTable table = getTable(tableName);
        if (table instanceof ISpatialTable) {
            return (JdbcSpatialTable) table;
        } else {
            String name = "";
            if(table != null){
                name = "'" + table.getName() + "' ";
            }
            LOGGER.error("The table " + name + "is not a spatial table.");
            return null;
        }
    }

    @Override
    public IJdbcSpatialTable getSpatialTable(GString nameOrQuery) {
        if(nameOrQuery.getValueCount() == 0 ||
                !nameOrQuery.toString().startsWith("(") && !nameOrQuery.toString().endsWith("(")) {
            return getSpatialTable(nameOrQuery.toString());
        }
        List<Object> params = this.getParameters(nameOrQuery);
        String sql = this.asSql(nameOrQuery, params);
        return getSpatialTable(sql, params);
    }

    @Override
    public boolean hasTable(@NotNull String tableName) {
        try {
            return JDBCUtilities.tableExists(getConnection(), TableLocation.parse(tableName, false));
        } catch (SQLException ex) {
            LOGGER.error("Cannot find the table '" + tableName + ".\n" +
                    ex.getLocalizedMessage());
            return false;
        }
    }

    @Nullable
    @Override
    public Object asType(@NotNull Class<?> clazz) {
        return null;
    }


}
