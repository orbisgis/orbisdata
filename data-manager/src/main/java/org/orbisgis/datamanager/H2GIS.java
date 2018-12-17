package org.orbisgis.datamanager;

import groovy.sql.Sql;
import org.orbisgis.datamanagerapi.dataset.IDataSet;
import org.orbisgis.datamanagerapi.dataset.IFeatureTable;
import org.orbisgis.datamanagerapi.dataset.ITable;
import org.orbisgis.datamanagerapi.datasource.IJDBCDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * Implementation of the IJDBCDataSource interface dedicated to the usage of an H2/H2GIS database.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2018)
 */
public class H2GIS extends Sql implements IJDBCDataSource {

    private H2GIS(Connection connection) {
        super(connection);
    }

    public static H2GIS open(Map<String, String> properties) throws SQLException {
        /**Properties props = new Properties();
        properties.forEach(props::put);
        Connection connection;
        String jdbcConnectionReference = coreWorkspace.getJDBCConnectionReference();
        if(!jdbcConnectionReference.isEmpty()) {
            String driverName = "h2";
            props.setProperty(DataSourceFactory.JDBC_USER,coreWorkspace.getDataBaseUser());
            if(coreWorkspace.isRequirePassword()) {
                properties.setProperty(DataSourceFactory.JDBC_PASSWORD, coreWorkspace.getDataBasePassword());
            }
            // Fetch requested Driver
            String osgiDriverName = URI_DRIVER_TO_OSGI_DRIVER.get(driverName);
            DataSourceFactory dataSourceFactory = dataSourceFactories.get(osgiDriverName);
            if(dataSourceFactory != null) {
                if(H2_OSGI_DRIVER_NAME.equals(osgiDriverName) && !properties.containsKey(DataSourceFactory.JDBC_SERVER_NAME)) {
                    //;DATABASE_EVENT_LISTENER='org.orbisgis.h2triggers.H2DatabaseEventListener'
                    // For local H2 Database link immediately with a database listener
                    // as it will allow open a database event if some db objects cannot be initialised
                    // see https://github.com/orbisgis/orbisgis/issues/793
                    properties.put("DATABASE_EVENT_LISTENER","'org.orbisgis.h2triggers.H2DatabaseEventListener'");
                }
                // Init spatial
                connection = SFSUtilities.wrapConnection(dataSourceFactory.createDataSource(properties));;
                Statement st = connection.createStatement();
                if (JDBCUtilities.isH2DataBase(connection.getMetaData()) &&
                        !JDBCUtilities.tableExists(connection, "PUBLIC.GEOMETRY_COLUMNS")) {
                st.execute("CREATE ALIAS IF NOT EXISTS H2GIS_SPATIAL FOR\n" +
                        "    \"org.h2gis.functions.factory.H2GISFunctions.load\";\n" +
                        "CALL H2GIS_SPATIAL();");
                }
            } else {
                throw new SQLException(String.format("The database driver %s is not available",driverName));
            }
        } else {
            throw new SQLException("DataBase path not found");
        }
        return new H2GIS(connection);**/
    }

    @Override
    public void close() {
        super.close();
    }

    @Override
    public ITable getTable(String name) {
        return null;
    }

    @Override
    public IFeatureTable getFeatureTable(String name) {
        return null;
    }

    @Override
    public IDataSet getDataSet(String name) {
        return null;
    }
}
