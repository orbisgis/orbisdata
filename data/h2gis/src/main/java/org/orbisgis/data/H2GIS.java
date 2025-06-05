/*
 * Bundle H2GIS is part of the OrbisGIS platform
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
 * H2GIS is distributed under LGPL 3 license.
 *
 * Copyright (C) 2018 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * H2GIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * H2GIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * H2GIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.data;

import groovy.lang.GString;
import org.h2.tools.DeleteDbFiles;
import org.h2gis.functions.factory.H2GISDBFactory;
import org.h2gis.functions.factory.H2GISFunctions;
import org.h2gis.functions.io.utility.IOMethods;
import org.h2gis.network.functions.NetworkFunctions;
import org.h2gis.utilities.FileUtilities;
import org.h2gis.utilities.GeometryTableUtilities;
import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.TableLocation;
import org.h2gis.utilities.dbtypes.DBTypes;
import org.locationtech.jts.geom.Geometry;
import org.orbisgis.data.api.dataset.IJdbcSpatialTable;
import org.orbisgis.data.api.dataset.IJdbcTable;
import org.orbisgis.data.api.dataset.ISpatialTable;
import org.orbisgis.data.api.dataset.IStreamResultSet;
import org.orbisgis.data.jdbc.JdbcDataSource;
import org.orbisgis.data.jdbc.JdbcSpatialTable;
import org.orbisgis.data.jdbc.JdbcTable;
import org.orbisgis.data.jdbc.resultset.StreamSpatialResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link JdbcDataSource} interface dedicated to the usage of an H2/H2GIS database.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC 2018-2019 / Chaire GEOTERA 2020)
 */
public class H2GIS extends JdbcDataSource {


    private static final Logger LOGGER = LoggerFactory.getLogger(H2GIS.class);

    /**
     * Private constructor.
     *
     * @param connection {@link Connection} to the database.
     */
    private H2GIS(Connection connection) {
        super(connection, DBTypes.H2GIS);
    }

    /**
     * Private constructor.
     *
     * @param dataSource {@link DataSource} to the database.
     */
    private H2GIS(DataSource dataSource) {
        super(dataSource, DBTypes.H2GIS);
    }

    /**
     * Create an instance of {@link H2GIS} from properties
     *
     * @param file .properties file containing the information for the DataBase opening.
     * @return {@link H2GIS} object if the DataBase has been successfully open, null otherwise.
     */
    public static H2GIS open(File file) throws Exception {
        if (FileUtilities.isExtensionWellFormated(file, "properties")) {
                Properties prop = new Properties();
                FileInputStream fous = new FileInputStream(file);
                prop.load(fous);
                return open(prop);
        }
        throw new IllegalArgumentException("Invalid properties file");
    }

    /**
     * Create an instance of {@link H2GIS} from properties
     *
     * @param properties Properties for the opening of the DataBase.
     * @return {@link H2GIS} object if the DataBase has been successfully open, null otherwise.
     */
    public static H2GIS open(Properties properties) throws Exception {
        Connection connection = JDBCUtilities.wrapSpatialDataSource(H2GISDBFactory.createDataSource(properties)).getConnection();
        check(connection);
        return new H2GIS(connection);
    }

    /**
     * Create an instance of {@link H2GIS} from a {@link Connection}
     *
     * @param connection {@link Connection} of the DataBase.
     * @return {@link H2GIS} object if the DataBase has been successfully open, null otherwise.
     */
    public static H2GIS open(Connection connection) throws Exception {
        if (connection != null) {
            check(connection);
            return new H2GIS(connection);
        }
        throw new IllegalArgumentException("Invalid database connection");
    }

    /**
     * Create an instance of {@link H2GIS} from a {@link DataSource}
     *
     * @param dataSource {@link DataSource} of the database.
     * @return {@link H2GIS} object if the DataBase has been successfully open, null otherwise.
     */
    public static H2GIS open(DataSource dataSource) throws Exception {
        if (dataSource != null) {
            Connection connection = dataSource.getConnection();
            if (connection != null) {
                check(connection);
                return new H2GIS(dataSource);
            }
        }
        throw new IllegalArgumentException("Invalid datasource");
    }

    private static void check(Connection connection) throws Exception {
        boolean isH2 = JDBCUtilities.isH2DataBase(connection);
        boolean tableExists = JDBCUtilities.tableExists(connection, TableLocation.parse("PUBLIC.GEOMETRY_COLUMNS", DBTypes.H2GIS));
        if (isH2 && !tableExists) {
            H2GISFunctions.load(connection);
        }
    }

    /**
     * Open the {@link H2GIS} database with the given properties and return the corresponding {@link H2GIS} object.
     *
     * @param properties Map of the properties to use for the database opening.
     * @return An instantiated {@link H2GIS} object wrapping the Sql object connected to the database.
     */
    public static H2GIS open(Map<String, String> properties) throws Exception {
        Properties props = new Properties();
        props.putAll(properties);
        return open(props);
    }

    /**
     * Open the {@link H2GIS} database at the given path and return the corresponding {@link H2GIS} object.
     *
     * @param path Path of the database to open.
     * @return An instantiated {@link H2GIS} object wrapping the Sql object connected to the database.
     */
    public static H2GIS open(String path) throws Exception {
        return open(path, "sa", "");
    }

    /**
     * Open the {@link H2GIS} database at the given path and return the corresponding {@link H2GIS} object.
     *
     * @param path     Path of the database to open.
     * @param user     User of the database.
     * @param password Password for the user.
     * @return An instantiated {@link H2GIS} object wrapping the Sql object connected to the database.
     */
    public static H2GIS open(String path, String user, String password) throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("databaseName", path);
        map.put("user", user);
        map.put("password", password);
        return open(map);
    }

    @Override
    public JdbcTable<? extends IStreamResultSet> getTable(String nameOrQuery, Statement statement) throws Exception {
        return getTable(nameOrQuery, null, statement);
    }

    @Override
    public JdbcTable<? extends IStreamResultSet> getTable(GString nameOrQuery, Statement statement) throws Exception {
        if (nameOrQuery.getValueCount() == 0 ||
                !nameOrQuery.toString().startsWith("(") && !nameOrQuery.toString().endsWith("(")) {
            return getTable(nameOrQuery.toString(), statement);
        }
        List<Object> params = this.getParameters(nameOrQuery);
        String sql = this.asSql(nameOrQuery, params);
        return getTable(sql, params, statement);
    }

    @Override
    public JdbcTable<? extends IStreamResultSet> getTable(String nameOrQuery, List<Object> params,
                                                          Statement statement) throws Exception {        String query;
        TableLocation location;
        if (!nameOrQuery.startsWith("(") && !nameOrQuery.endsWith(")")) {
            TableLocation inputLocation = TableLocation.parse(nameOrQuery, DBTypes.H2GIS);
            query = String.format("SELECT * FROM %s", inputLocation);
            location = new TableLocation(inputLocation.getCatalog(), inputLocation.getSchema(), inputLocation.getTable(), DBTypes.H2GIS);
        } else {
            query = nameOrQuery;
            location = null;
        }
        try {
            Connection con = getConnection();
            if (con != null) {
                if (location != null) {
                    boolean hasGeom = GeometryTableUtilities.hasGeometryColumn(con, location);
                    if (!getConnection().getAutoCommit()) {
                        super.commit();
                    }
                    if (hasGeom) {
                        return new H2gisSpatialTable(location, query, statement, params, this);
                    } else {
                        return new H2gisTable(location, query, statement, params, this);
                    }
                } else {
                    ResultSet rs;
                    if (statement instanceof PreparedStatement) {
                        PreparedStatement st = con.prepareStatement("(SELECT * FROM " + query + "AS foo WHERE 1=0)");
                        for (int i = 0; i < params.size(); i++) {
                            st.setObject(i + 1, params.get(i));
                        }
                        rs = st.executeQuery();
                    } else {
                        rs = statement.executeQuery("(SELECT * FROM " + query + "AS foo WHERE 1=0)");
                    }
                    boolean hasGeom = GeometryTableUtilities.hasGeometryColumn(rs);
                    if (!getConnection().getAutoCommit()) {
                        super.commit();
                    }
                    if (hasGeom) {
                        return new H2gisSpatialTable(location, query, statement, params, this);
                    } else {
                        return new H2gisTable(location, query, statement, params, this);
                    }
                }
            }
        } catch (SQLException e) {
            try {
                if (!getConnection().getAutoCommit()) {
                    super.rollback();
                }
            } catch (SQLException e2) {
                throw new SQLException("Unable to rollback", e2);
            }
        }
        throw new SQLException("Cannot read the table "+ query);
    }

    @Override
    public JdbcTable<? extends IStreamResultSet> getTable(String tableName) throws Exception {
        Connection connection = getConnection();
        Statement statement;
        try {
            DatabaseMetaData dbdm = connection.getMetaData();
            int type = ResultSet.TYPE_FORWARD_ONLY;
            if (dbdm.supportsResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE)) {
                type = ResultSet.TYPE_SCROLL_SENSITIVE;
            } else if (dbdm.supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE)) {
                type = ResultSet.TYPE_SCROLL_INSENSITIVE;
            }
            int concur = ResultSet.CONCUR_READ_ONLY;
            if (dbdm.supportsResultSetConcurrency(type, ResultSet.CONCUR_UPDATABLE)) {
                concur = ResultSet.CONCUR_UPDATABLE;
            }
            statement = connection.createStatement(type, concur);
        } catch (SQLException e) {
            throw new SQLException("Cannot read the table " + tableName, e);
        }
        return getTable(tableName, statement);
    }

    @Override
    public IJdbcTable<? extends IStreamResultSet> getTable(GString nameOrQuery) throws Exception {
        if (nameOrQuery.getValueCount() == 0 ||
                !nameOrQuery.toString().startsWith("(") && !nameOrQuery.toString().endsWith("(")) {
            return getTable(nameOrQuery.toString());
        }
        List<Object> params = this.getParameters(nameOrQuery);
        String sql = this.asSql(nameOrQuery, params);
        return getTable(sql, params);
    }

    @Override
    public IJdbcTable<? extends IStreamResultSet> getTable(String query, List<Object> params) throws Exception {
        if (params == null || params.isEmpty()) {
            return getTable(query);
        }
        PreparedStatement prepStatement;
        try {
            Connection connection = getConnection();
            DatabaseMetaData dbdm = connection.getMetaData();
            int type = ResultSet.TYPE_FORWARD_ONLY;
            if (dbdm.supportsResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE)) {
                type = ResultSet.TYPE_SCROLL_SENSITIVE;
            } else if (dbdm.supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE)) {
                type = ResultSet.TYPE_SCROLL_INSENSITIVE;
            }
            int concur = ResultSet.CONCUR_READ_ONLY;
            if (dbdm.supportsResultSetConcurrency(type, ResultSet.CONCUR_UPDATABLE)) {
                concur = ResultSet.CONCUR_UPDATABLE;
            }
            prepStatement = connection.prepareStatement(query, type, concur);
            setStatementParameters(prepStatement, params);
        } catch (SQLException e) {
            throw new SQLException("Cannot read the table " + query, e);
        }
        return getTable(query, params, prepStatement);
    }

    @Override
    public IJdbcSpatialTable<StreamSpatialResultSet> getSpatialTable(String tableName, Statement statement) throws Exception {
        IJdbcTable<? extends ResultSet> table = getTable(tableName, statement);
        if (table instanceof ISpatialTable) {
            return (JdbcSpatialTable) table;
        } else {
            throw new SQLException("The table " + tableName + "is not a spatial table.");
        }
    }

    @Override
    public IJdbcSpatialTable<StreamSpatialResultSet> getSpatialTable(GString nameOrQuery, Statement statement) throws Exception {
        if (nameOrQuery.getValueCount() == 0 ||
                !nameOrQuery.toString().startsWith("(") && !nameOrQuery.toString().endsWith("(")) {
            return getSpatialTable(nameOrQuery.toString(), statement);
        }
        List<Object> params = this.getParameters(nameOrQuery);
        String sql = this.asSql(nameOrQuery, params);
        return getSpatialTable(sql, params, statement);
    }

    @Override
    public IJdbcSpatialTable<StreamSpatialResultSet> getSpatialTable(String nameOrQuery, List<Object> params, Statement statement) throws Exception {
        IJdbcTable<? extends ResultSet> table = getTable(nameOrQuery, params, statement);
        if (table instanceof ISpatialTable) {
            return (JdbcSpatialTable) table;
        } else {
            throw new SQLException("The table " + nameOrQuery + "is not a spatial table.");
        }
    }

    @Override
    public IJdbcSpatialTable<StreamSpatialResultSet> getSpatialTable(String query, List<Object> params) throws Exception {
        IJdbcTable<? extends ResultSet> table = getTable(query, params);
        if (table instanceof ISpatialTable) {
            return (JdbcSpatialTable) table;
        } else {
            throw new SQLException("The table " + query + "is not a spatial table.");
        }
    }

    @Override
    public IJdbcSpatialTable<StreamSpatialResultSet> getSpatialTable(String tableName) throws Exception {
        IJdbcTable<? extends ResultSet> table = getTable(tableName);
        if (table instanceof ISpatialTable) {
            return (JdbcSpatialTable) table;
        } else {
            throw new SQLException("The table " + tableName + " is not a spatial table.");
        }
    }

    @Override
    public IJdbcSpatialTable<StreamSpatialResultSet> getSpatialTable(GString nameOrQuery) throws Exception {
        if (nameOrQuery.getValueCount() == 0 ||
                !nameOrQuery.toString().startsWith("(") && !nameOrQuery.toString().endsWith("(")) {
            return getSpatialTable(nameOrQuery.toString());
        }
        List<Object> params = this.getParameters(nameOrQuery);
        String sql = this.asSql(nameOrQuery, params);
        return getSpatialTable(sql, params);
    }

    @Override
    public boolean hasTable(String tableName) throws Exception {
        try {
            return JDBCUtilities.tableExists(getConnection(), TableLocation.parse(tableName, DBTypes.H2GIS));
        }catch (SQLException e){
            return false;
        }
    }

    @Override
    public Collection<String> getColumnNames(String location) throws Exception {
        try {
            return JDBCUtilities.getColumnNames(getConnection(), TableLocation.parse(location, DBTypes.H2GIS).toString());
        }catch (SQLException e){
            return null;
        }
    }

    @Override
    public void dropColumn(String tableName, List<String> columnNames) throws Exception {
        if (tableName == null || columnNames == null || columnNames.isEmpty()) {
            throw new IllegalArgumentException("Illegal argument to drop the column");
        }
        String query = columnNames.stream().filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.joining(" , "));
        if (!query.isEmpty()) {
            execute("ALTER TABLE IF EXISTS " + TableLocation.parse(tableName, DBTypes.H2GIS) + " DROP COLUMN IF EXISTS (" + query + ")");
        }
    }

    @Override
    public void dropColumn(String tableName, String... columnName) throws Exception {
         dropColumn(tableName, List.of(columnName));
    }

    @Override
    public long getRowCount(String tableName) throws Exception{
        if (tableName == null || tableName.isEmpty()) {
            throw new IllegalArgumentException("Cannot get row count on empty or null table");
        }
        return JDBCUtilities.getRowCount(getConnection(), TableLocation.parse(tableName, DBTypes.H2GIS));
    }

    @Override
    public Geometry getExtent(String tableName) throws Exception{
        if (tableName == null || tableName.isEmpty()) {
            throw new IllegalArgumentException("Cannot get the extent on empty or null table");
        }
        return GeometryTableUtilities.getEnvelope(getConnection(), TableLocation.parse(tableName, DBTypes.H2GIS));
    }

    @Override
    public Geometry getExtent(String tableName, String... geometryColumns) throws Exception{
        if (tableName == null || tableName.isEmpty()) {
            throw new IllegalArgumentException("Cannot get row count on empty or null table");
        }
        return GeometryTableUtilities.getEnvelope(getConnection(), TableLocation.parse(tableName, DBTypes.H2GIS), geometryColumns);
     }

    @Override
    public String link(Map dataSourceProperties, String sourceTableName, boolean delete) throws Exception{
        return link(dataSourceProperties, sourceTableName, sourceTableName, delete);
    }

    @Override
    public String link(Map dataSourceProperties, String sourceTableName, String targetTableName, boolean delete) throws Exception{
        return IOMethods.linkedTable(getConnection(), dataSourceProperties, sourceTableName, targetTableName, delete);
    }

    @Override
    public String link(Map dataSourceProperties, String sourceTableName) throws Exception{
        return link(dataSourceProperties, sourceTableName, sourceTableName, false);
    }

    /**
     * Load the H2GIS Network function in the current H2GIS DataSource.
     *
     * @return True if the functions have been successfully loaded, false otherwise.
     */
    public boolean addNetworkFunctions() {
        Connection connection = getConnection();
        if (connection == null) {
            LOGGER.error("Cannot load the H2GIS Network extension.\n");
            return false;
        }
        try {
            NetworkFunctions.load(connection);
        } catch (SQLException e) {
            LOGGER.error("Cannot load the H2GIS Network extension.\n", e);
            return false;
        }
        return true;
    }

    @Override
    public Object asType(Class<?> clazz) {
        return null;
    }

    /**
     * Delete the H2GIS database file
     */
    public void deleteClose() throws Exception {
        try {
            execute("drop all objects delete files");
            close();
        }catch (SQLException ex){
            throw new Exception("Cannot delete the database files");
        }
    }

}
