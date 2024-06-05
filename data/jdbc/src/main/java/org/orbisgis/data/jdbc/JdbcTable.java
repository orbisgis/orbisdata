/*
 * Bundle JDBC is part of the OrbisGIS platform
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
 * JDBC is distributed under LGPL 3 license.
 *
 * Copyright (C) 2018 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * JDBC is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * JDBC is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * JDBC. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.data.jdbc;

import groovy.lang.*;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.h2gis.functions.io.utility.IOMethods;
import org.h2gis.utilities.*;
import org.h2gis.utilities.dbtypes.DBTypes;
import org.orbisgis.commons.printer.Ascii;
import org.orbisgis.commons.printer.Html;
import org.orbisgis.commons.printer.ICustomPrinter;
import org.orbisgis.data.api.dataset.*;
import org.orbisgis.data.api.datasource.IJdbcDataSource;
import org.orbisgis.data.api.dsl.IBuilderResult;
import org.orbisgis.data.api.dsl.IFilterBuilder;
import org.orbisgis.data.api.dsl.IQueryBuilder;
import org.orbisgis.data.api.dsl.IResultSetProperties;
import org.orbisgis.data.jdbc.dsl.QueryBuilder;
import org.orbisgis.data.jdbc.dsl.ResultSetProperties;
import org.orbisgis.data.jdbc.resultset.DefaultResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.orbisgis.commons.printer.ICustomPrinter.CellPosition.*;

/**
 * Contains the methods which are in common to all the {@link IJdbcTable} subclasses.
 * Implements the {@link GroovyObject} to simplify the methods calling (i.e. .tableLocation instead of
 * .getTableLocation() ).
 *
 * @author Sylvain Palominos (Lab-STICC UBS 2019 / Chaire GEOTERA 2020)
 */
public abstract class JdbcTable<T extends ResultSet> extends DefaultResultSet implements IJdbcTable<T>, GroovyObject {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcTable.class);

    private IOMethods ioMethods = null;

    /**
     * Default width of the columns in ascii print
     */
    private static final int ASCII_COLUMN_WIDTH = 20;
    /**
     * MetaClass use for groovy methods/properties binding
     */
    private MetaClass metaClass;
    /**
     * Type of the database
     */
    private final DBTypes dataBaseType;
    /**
     * DataSource to execute query
     */
    private final IJdbcDataSource jdbcDataSource;
    /**
     * Table location
     */
    private final TableLocation tableLocation;
    /**
     * PreparedStatement
     */
    private final Statement statement;
    /**
     * PreparedStatement
     */
    private final List<Object> params;
    /**
     * Base SQL query for the creation of the ResultSet
     */
    private final String baseQuery;
    /**
     * Cached resultSet
     */
    protected ResultSet resultSet;
    /**
     * {@link ResultSet} properties.
     */
    private IResultSetProperties rsp;

    /**
     * Main constructor.
     *
     * @param dataBaseType   Type of the DataBase where this table comes from.
     * @param tableLocation  TableLocation that identify the represented table.
     * @param baseQuery      Query for the creation of the ResultSet.
     * @param statement      PreparedStatement used to request the database.
     * @param params         Map containing the parameters for the query.
     * @param jdbcDataSource DataSource to use for the creation of the resultSet.
     */
    public JdbcTable(DBTypes dataBaseType, IJdbcDataSource jdbcDataSource,
                     TableLocation tableLocation, Statement statement,
                     List<Object> params, String baseQuery) {
        this.metaClass = InvokerHelper.getMetaClass(getClass());
        this.dataBaseType = dataBaseType;
        this.jdbcDataSource = jdbcDataSource;
        this.tableLocation = tableLocation;
        this.statement = statement;
        this.params = params;
        this.baseQuery = baseQuery;
        this.rsp = new ResultSetProperties();
    }

    /**
     * Return the base query for the creation of the ResultSet.
     *
     * @return The base query.
     */
    public String getBaseQuery() {
        return baseQuery;
    }

    @Override
    public boolean reload() throws SQLException {
        resultSet = null;
        return getResultSet() != null;
    }

    @Override
    protected ResultSet getResultSet() throws SQLException {
        if (resultSet == null) {
            Statement st = getStatement();
            if (st instanceof PreparedStatement) {
                resultSet = ((PreparedStatement) st).executeQuery();
            } else {
                resultSet = getStatement().executeQuery(getBaseQuery());
            }
        }
        return resultSet;
    }

    /**
     * Return the {@link ResultSet} with a limit.
     *
     * @param limit Limit of the result set.
     * @return The {@link ResultSet} with a limit.
     */
    protected ResultSet getResultSetLimit(int limit) throws SQLException {
        ResultSet resultSet;
        if (getBaseQuery().contains(" LIMIT ")) {
            resultSet = getResultSet();
        } else {
            Connection con = jdbcDataSource.getConnection();
            resultSet = con.createStatement().executeQuery("SELECT * FROM (" + getBaseQuery() + ") AS FOO LIMIT " + limit);
        }
        return resultSet;
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException{
        ResultSet rs = getResultSet();
       return rs.getMetaData();
    }

    /**
     * Return the parent DataSource.
     *
     * @return The parent DataSource.
     */
    public IJdbcDataSource getJdbcDataSource() {
        return jdbcDataSource;
    }

    @Override
    public TableLocation getTableLocation() {
        return tableLocation;
    }

    @Override

    public DBTypes getDbType() {
        return dataBaseType;
    }

    @Override
    public MetaClass getMetaClass() {
        return metaClass;
    }

    @Override
    public void setMetaClass(MetaClass metaClass) {
        this.metaClass = metaClass;
    }

    @Override
    public boolean isSpatial() {
        return false;
    }

    @Override
    public boolean isLinked() {
        if (getTableLocation() != null) {
            try {
                Connection con = jdbcDataSource.getConnection();
                if (con == null) {
                    LOGGER.error("Unable to get the connection.");
                    return false;
                }
                return JDBCUtilities.isLinkedTable(con, getTableLocation().toString());
            } catch (SQLException e) {
                LOGGER.error("Unable to get the type of the table '" + getTableLocation().getTable() + ".\n" + e.getLocalizedMessage());
            }
        }
        return false;
    }

    @Override
    public boolean isTemporary() {
        if (getTableLocation() != null) {
            try {
                Connection con = jdbcDataSource.getConnection();
                if (con == null) {
                    LOGGER.error("Unable to get the connection.");
                    return false;
                }
                return JDBCUtilities.isTemporaryTable(con, getTableLocation());
            } catch (SQLException e) {
                LOGGER.error("Unable to get the type of the table '" + getTableLocation().getTable() + ".\n" + e.getLocalizedMessage());
            }
        }
        return false;
    }

    @Override
    public Collection<String> getColumnNames() throws Exception {
        Connection con = jdbcDataSource.getConnection();
        if (tableLocation == null) {
            try {
                ResultSet rs = getResultSetLimit(0);
                return JDBCUtilities
                        .getColumnNames(rs.getMetaData())
                        .stream()
                        .map(this::formatColumnName)
                        .collect(Collectors.toCollection(ArrayList::new));
            } catch (SQLException e) {
                throw new SQLException("Unable to get the collection of columns names", e);
            }
        } else {
            try {
                return JDBCUtilities.getColumnNames(con, tableLocation);
            } catch (SQLException e) {
                throw new SQLException("Unable to get the column names of the table " + tableLocation + ".", e);
            }
        }
    }

    @Override
    public Map<String, String> getColumnNamesTypes() throws Exception {
        Map<String, String> map = new LinkedHashMap<>();
        try {
            ResultSet rs = getResultSetLimit(0);
            ResultSetMetaData metaData = rs.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                map.put(metaData.getColumnName(i), metaData.getColumnTypeName(i));
            }
        } catch (SQLException e) {
            throw new SQLException("Unable to get the column types", e);
        }
        return map;
    }

    @Override
    public String getColumnType(String columnName) throws SQLException {
        try {
            ResultSet rs = getResultSetLimit(0);
            ResultSetMetaData metaData = rs.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                if (columnName.equalsIgnoreCase(metaData.getColumnName(i))) {
                    //Take into account the geometry type
                    String type = metaData.getColumnTypeName(i);
                    if (type.toLowerCase().startsWith("geometry")) {
                        if (dataBaseType == DBTypes.H2 || dataBaseType == DBTypes.H2GIS) {
                            return GeometryMetaData.getMetaDataFromTablePattern(type).getGeometryType();
                        } else if (tableLocation != null && !getName().isEmpty()) {
                            return GeometryTableUtilities.getMetaData(jdbcDataSource.getConnection(),
                                    tableLocation,
                                    TableLocation.capsIdentifier(columnName, dataBaseType)
                            ).getGeometryType();
                        }
                    }
                    return type;
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Cannot get the type of the column.", e);
        }
        return null;
    }

    private String getGeometricType(String columnName) {
        if (tableLocation != null && !getName().isEmpty()) {
            try {
                Connection con = jdbcDataSource.getConnection();
                return GeometryTableUtilities.getMetaData(con,
                        tableLocation,
                        TableLocation.capsIdentifier(columnName, dataBaseType)
                ).getGeometryType();
            } catch (SQLException e) {
                LOGGER.error("Unable to get the geometric type of the column '" + columnName + "'\n" +
                        e.getLocalizedMessage());
            }
        } else {
            try {
                ResultSet rs = getResultSet();
                if (rs == null) {
                    LOGGER.error("Unable to get the resultset.");
                    return null;
                }
                Map<String, GeometryMetaData> map = GeometryTableUtilities.getMetaData(rs);
                if (!map.containsKey(columnName)) {
                    LOGGER.error("Unable to get data from the column '" + columnName + "'.");
                    return null;
                }
                return map.get(columnName).getGeometryType();
            } catch (SQLException e) {
                LOGGER.error("Unable to get data from the resultset.", e);
                return null;
            }
        }
        return null;
    }

    @Override
    public int getColumnCount() throws SQLException {
        ResultSet rs = getResultSet();
        ResultSetMetaData metaData = rs.getMetaData();
        return metaData.getColumnCount();
    }

    @Override
    public int getRowCount() throws SQLException {
        Connection con = jdbcDataSource.getConnection();
        String query = "";
        if (tableLocation == null) {
            if (getBaseQuery().startsWith("(") && getBaseQuery().endsWith(")")) {
                query = "SELECT COUNT(*) FROM " + getBaseQuery() + "AS FOO";
            } else {
                query = "SELECT COUNT(*) FROM (" + getBaseQuery() + ") AS FOO";
            }
        } else {
            query = "SELECT count(*) FROM " + tableLocation.toString(getDbType());
        }
        try {
            ResultSet rowCountRs = con.createStatement().executeQuery(query);
            rowCountRs.next();
            int c = rowCountRs.getInt(1);
            if (!con.getAutoCommit()) {
                con.commit();
            }
            return c;
        } catch (SQLException e) {
            try {
                if (!con.getAutoCommit()) {
                    con.rollback();
                }
            } catch (SQLException e1) {
                LOGGER.error("Unable to rollback.", e1);
            }
            throw e;
        }
    }

    @Override
    public Collection<String> getUniqueValues(String column) throws Exception{
        if (tableLocation == null) {
            throw new IllegalArgumentException("Cannot get data on null or empty table");
        }
        if (tableLocation.getTable().isEmpty()) {
            throw new IllegalArgumentException("Cannot get data on null or empty table");
        } else {
            try {
                Connection con = jdbcDataSource.getConnection();
                if (con == null) {
                    throw new SQLException("Cannot get the connection to the database");
                }
                TableLocation loc = getTableLocation();
                if (loc == null) {
                    throw new IllegalArgumentException("Cannot get data on null or empty table");
                }
                return JDBCUtilities.getUniqueFieldValues(con, loc.toString(getDbType()),
                        column);
            } catch (SQLException e) {
                LOGGER.error("Unable to request unique values fo the column '" + column + "'.\n", e);
            }
        }
        return null;
    }

    @Override

    public Map<String, Object> firstRow() throws Exception {
        Map<String, Object> map = new HashMap<>();
        if (isEmpty()) {
            return map;
        }
        ResultSet rs = getResultSetLimit(1);
        try {
            rs.next();
        } catch (SQLException e) {
            throw e;
        }
        Collection<String> columns = getColumnNames();
        for (String column : columns) {
            try {
                map.put(column, rs.getObject(column));
            } catch (SQLException e) {
                throw new SQLException("Unable to get data from first row.", e);
            }
        }
        return map;
    }

    @Override
    public String save(String filePath, boolean deleteFile) throws Exception {
        String toSave = getTableLocation() == null ? "(" + getBaseQuery() + ")" : getTableLocation().toString(getDbType());
        try {
            if (ioMethods == null) {
                ioMethods = new IOMethods();
            }
            ioMethods.exportToFile(getJdbcDataSource().getConnection(), toSave, filePath, null, deleteFile);
            return filePath;
        } catch (SQLException e) {
            throw new SQLException("Cannot save the file : " + filePath);
        }
    }

    @Override
    public String save(String filePath, String encoding) throws Exception {
        String toSave = getTableLocation() == null ? "(" + getBaseQuery() + ")" : getTableLocation().toString(getDbType());
        try {
            if (ioMethods == null) {
                ioMethods = new IOMethods();
            }
            ioMethods.exportToFile(getJdbcDataSource().getConnection(), toSave, filePath, encoding, false);
            return filePath;
        } catch (SQLException e) {
            throw new SQLException("Cannot save the file : " + filePath, e);
        }
    }

    @Override
    public String save(IJdbcDataSource dataSource, boolean deleteTable) throws Exception {
        return save(dataSource, deleteTable, 1000);
    }

    @Override
    public String save(IJdbcDataSource dataSource, String outputTableName, boolean deleteTable) throws Exception {
        return save(dataSource, outputTableName, deleteTable, 1000);
    }

    @Override
    public String save(IJdbcDataSource dataSource, int batchSize) throws Exception {
        return save(dataSource, false, batchSize);
    }

    @Override
    public String save(IJdbcDataSource dataSource, String outputTableName, boolean deleteTable, int batchSize) throws Exception {
        if (dataSource == null) {
            throw new SQLException("Cannot get the connection to the database");
        }
        String inputTableName = getTableLocation() == null ? "(" + getBaseQuery() + ")" : getTableLocation().toString(getDbType());
        try {
            return IOMethods.exportToDataBase(getJdbcDataSource().getConnection(), inputTableName, dataSource.getConnection(), outputTableName, deleteTable ? -1 : 0, batchSize);
        } catch (SQLException e) {
            throw new SQLException("Unable to save the table " + inputTableName + " to " + dataSource.getLocation().toString());
        }
    }

    @Override
    public String save(IJdbcDataSource dataSource, boolean deleteTable, int batchSize) throws Exception {
        if (dataSource == null) {
            throw new SQLException("Cannot get the connection to the database");
        }
        String inputTableName = getTableLocation() == null ? "(" + getBaseQuery() + ")" : getTableLocation().toString(getDbType());
        return IOMethods.exportToDataBase(getJdbcDataSource().getConnection(), inputTableName, dataSource.getConnection(), inputTableName, deleteTable ? -1 : 0, batchSize);
        }


    private String getQuery() {
        return baseQuery.trim();
    }

    protected String getQuery(String... columns) {
        TableLocation loc = getTableLocation();
        if (loc == null) {
            return null;
        }
        return "SELECT " + String.join(", ", columns) + " FROM " + getTableLocation().getTable().toUpperCase();
    }

    @Override

    public IBuilderResult filter(String filter) {
        String loc = getTableLocation() != null ? getTableLocation().toString(getDbType()) : getBaseQuery();
        IQueryBuilder builder = new QueryBuilder(getJdbcDataSource(), loc, getResultSetProperties());
        return builder.filter(filter);
    }

    @Override

    public IBuilderResult filter(GString filter) {
        String loc = getTableLocation() != null ? getTableLocation().toString(getDbType()) : getBaseQuery();
        IQueryBuilder builder = new QueryBuilder(getJdbcDataSource(), loc, getResultSetProperties());
        return builder.filter(filter);
    }

    @Override

    public IBuilderResult filter(String filter, List<Object> params) {
        String loc = getTableLocation() != null ? getTableLocation().toString(getDbType()) : getBaseQuery();
        IQueryBuilder builder = new QueryBuilder(getJdbcDataSource(), loc, getResultSetProperties());
        return builder.filter(filter, params);
    }

    @Override

    public IFilterBuilder columns(String... columns) {
        String loc = getTableLocation() != null ? getTableLocation().toString(getDbType()) : getBaseQuery();
        IQueryBuilder builder = new QueryBuilder(getJdbcDataSource(), loc, getResultSetProperties());
        return builder.columns(columns);
    }

    @Override
    public IJdbcTable<? extends IStreamResultSet> getTable() throws Exception{
        return (IJdbcTable) asType(IJdbcTable.class);
    }

    @Override
    public IJdbcSpatialTable<IStreamSpatialResultSet> getSpatialTable() throws Exception{
        if (isSpatial()) {
            return (IJdbcSpatialTable) asType(IJdbcSpatialTable.class);
        } else {
            return null;
        }
    }

    @Override
    public List<Object> getFirstRow() throws Exception {
        ResultSet rs = getResultSet();
        if (rs != null) {
            try {
                if (rs.isBeforeFirst() && !rs.next()) {
                    throw new SQLException("Unable go to the first row.");
                }
                if (!rs.isFirst() && !rs.first()) {
                    throw new SQLException("Unable go to the first row.");
                }
                List<Object> list = new ArrayList<>();
                for (int i = 1; i <= getColumnCount(); i++) {
                    list.add(rs.getObject(i));
                }
                return list;
            } catch (SQLException e) {
                throw new SQLException("Unable to query the first row of the table.", e);
            }
        }
        throw new SQLException("Unable to query the first row of the table.");
    }




    @Override
    public Statement getStatement() {
        return statement;
    }

    @Override
    public Object asType(Class<?> clazz) throws Exception {
        if (ICustomPrinter.class.isAssignableFrom(clazz)) {
            StringBuilder builder = new StringBuilder();
            ICustomPrinter printer;
            if (clazz == Ascii.class) {
                printer = new Ascii(builder);
            } else if (clazz == Html.class) {
                printer = new Html(builder);
            } else {
                return this;
            }
            Collection<String> columnNames = getColumnNames();
            if (columnNames == null) {
                printer.endTable();
                return printer;
            }

            printer.startTable(ASCII_COLUMN_WIDTH, columnNames.size());
            printer.appendTableTitle(this.getName());
            printer.appendTableLineSeparator();
            for (String column : columnNames) {
                printer.appendTableHeaderValue(column, CENTER);
            }
            printer.appendTableLineSeparator();
            ResultSet rs = getResultSet();
            if (rs != null) {
                    while (rs.next()) {
                        for (String column : columnNames) {
                            Object obj = rs.getObject(column);
                            if (obj instanceof Number) {
                                printer.appendTableValue(rs.getObject(column), RIGHT);
                            } else {
                                printer.appendTableValue(rs.getObject(column), LEFT);
                            }
                        }
                    }
            }
            printer.appendTableLineSeparator();
            printer.endTable();
            return printer;
        } else if (ITable.class.isAssignableFrom(clazz)) {
            return this;
        }
        return null;
    }

    /**
     * Format the column according to the DB type.
     *
     * @param column Columne name to format.
     * @return The formatted column name.
     */
    private String formatColumnName(String column) {
        return getDbType() == DBTypes.H2GIS ? column.toUpperCase() : column.toLowerCase();
    }

    @Override

    public JdbcTableSummary getSummary() throws Exception{
        return new JdbcTableSummary(getTableLocation(), getColumnCount(), getRowCount());
    }

    public List<Object> getParams() {
        return params;
    }

    @Override
    public void setResultSetProperties(IResultSetProperties properties) {
        if (properties != null) {
            this.rsp = properties.copy();
        }
    }

    @Override

    public IResultSetProperties getResultSetProperties() {
        return rsp;
    }

    @Override
    public void eachRow(Closure<Object> closure) {
        this.forEach(closure::call);
        Connection con = null;
        try {
            con = this.getJdbcDataSource().getConnection();
        } catch (SQLException e) {
            LOGGER.error("Unable to get connection.", e);
        }
        try {
            if (!con.getAutoCommit()) {
                con.commit();
            }
        } catch (SQLException e) {
            LOGGER.error("Unable to commit each row action.", e);
            try {
                con.rollback();
            } catch (SQLException e2) {
                LOGGER.error("Unable to rollback.", e2);
            }
        }
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        Objects.requireNonNull(action);
        Iterator<T> var2 = this.iterator();

        while (var2.hasNext()) {
            T t = var2.next();
            action.accept(t);
        }
    }

    @Override
    public boolean isEmpty() throws Exception {
        Connection con = jdbcDataSource.getConnection();
        String query = "";
        if (tableLocation == null) {
            query = "SELECT 1 FROM (" + getBaseQuery() + ") AS FOO LIMIT 1";
        } else {
            query = "SELECT 1 FROM " + tableLocation.toString(getDbType()) + " LIMIT 1";
        }
        try {
            ResultSet rowQuery = con.createStatement().executeQuery(query);
            return !rowQuery.next();
        } catch (SQLException e) {
            try {
                if (con != null && !con.getAutoCommit()) {
                    con.rollback();
                }
            } catch (SQLException e1) {
                throw new SQLException("Unable to rollback.", e1);
            }
            throw new SQLException("Unable to read the table.", e);
        }
    }

    @Override
    public Object get(int column) throws Exception {
        ResultSet rs = getResultSet();
        if (rs != null) {
            return rs.getObject(column);
        }
        throw new SQLException("Cannot get the value");
    }

    @Override
    public Object get(String column) throws Exception {
        ResultSet rs = getResultSet();
        if (rs != null) {
            return rs.getObject(column);
        }
        throw new SQLException("Cannot get the value");
    }
}