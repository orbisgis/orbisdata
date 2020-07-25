/*
 * Bundle DataManager is part of the OrbisGIS platform
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
 * DataManager is distributed under LGPL 3 license.
 *
 * Copyright (C) 2018 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * DataManager is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * DataManager is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * DataManager. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.orbisdata.datamanager.jdbc;

import groovy.lang.GString;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.MissingMethodException;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.h2.value.DataType;
import org.h2gis.utilities.GeometryMetaData;
import org.h2gis.utilities.GeometryTableUtilities;
import org.h2gis.utilities.JDBCUtilities;
import org.locationtech.jts.geom.Geometry;
import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.commons.annotations.Nullable;
import org.orbisgis.commons.printer.Ascii;
import org.orbisgis.commons.printer.Html;
import org.orbisgis.commons.printer.ICustomPrinter;
import org.orbisgis.orbisdata.datamanager.api.dataset.DataBaseType;
import org.orbisgis.orbisdata.datamanager.api.dataset.IJdbcSpatialTable;
import org.orbisgis.orbisdata.datamanager.api.dataset.IJdbcTable;
import org.orbisgis.orbisdata.datamanager.api.dataset.ITable;
import org.orbisgis.orbisdata.datamanager.api.datasource.IJdbcDataSource;
import org.orbisgis.orbisdata.datamanager.api.dsl.IBuilderResult;
import org.orbisgis.orbisdata.datamanager.api.dsl.IFilterBuilder;
import org.orbisgis.orbisdata.datamanager.api.dsl.IQueryBuilder;
import org.orbisgis.orbisdata.datamanager.jdbc.dsl.QueryBuilder;
import org.orbisgis.orbisdata.datamanager.jdbc.io.IOMethods;
import org.orbisgis.orbisdata.datamanager.jdbc.resultset.DefaultResultSet;
import org.orbisgis.orbisdata.datamanager.jdbc.resultset.StreamResultSet;
import org.orbisgis.orbisdata.datamanager.jdbc.resultset.StreamSpatialResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static org.orbisgis.commons.printer.ICustomPrinter.CellPosition.*;

/**
 * Contains the methods which are in common to all the {@link IJdbcTable} subclasses.
 * Implements the {@link GroovyObject} to simplify the methods calling (i.e. .tableLocation instead of
 * .getTableLocation() ).
 *
 * @author Sylvain Palominos (Lab-STICC UBS 2019 / Chaire GEOTERA 2020)
 */
public abstract class JdbcTable<T extends ResultSet, U> extends DefaultResultSet implements IJdbcTable<T, U>, GroovyObject {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcTable.class);

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
    private final DataBaseType dataBaseType;
    /**
     * DataSource to execute query
     */
    private final IJdbcDataSource jdbcDataSource;
    /**
     * Table location
     */
    @Nullable
    private final TableLocation tableLocation;
    /**
     * Any kind of statement
     */
    private  Statement statement;
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
    @Nullable
    protected ResultSet resultSet;

    /**
     * Main constructor.
     *
     * @param dataBaseType   Type of the DataBase where this table comes from.
     * @param tableLocation  TableLocation that identify the represented table.
     * @param baseQuery      Query for the creation of the ResultSet.
     * @param statement  PreparedStatement used to request the database.
     * @param params         Map containing the parameters for the query.
     * @param jdbcDataSource DataSource to use for the creation of the resultSet.
     */
    public JdbcTable(@NotNull DataBaseType dataBaseType, @NotNull IJdbcDataSource jdbcDataSource,
                     @Nullable TableLocation tableLocation, @NotNull Statement statement,
                     @Nullable List <Object> params, @NotNull String baseQuery) {
        this.metaClass = InvokerHelper.getMetaClass(getClass());
        this.dataBaseType = dataBaseType;
        this.jdbcDataSource = jdbcDataSource;
        this.tableLocation = tableLocation;
        this.statement = statement;
        this.params = params;
        this.baseQuery = baseQuery;
    }

    /**
     * Return the base query for the creation of the ResultSet.
     *
     * @return The base query.
     */
    @NotNull
    protected String getBaseQuery() {
        return baseQuery;
    }

    @Override
    public boolean reload(){
        resultSet = null;
        return getResultSet() != null;
    }

    @Override
    protected ResultSet getResultSet() {
        if (resultSet == null) {
            try {
                Statement st = getStatement();
                if(st instanceof PreparedStatement) {
                    resultSet = ((PreparedStatement)st).executeQuery();
                }
                else {
                    resultSet = getStatement().executeQuery(getBaseQuery());
                }
            } catch (SQLException e) {
                LOGGER.error("Unable to execute the query '" + getBaseQuery() + "'.\n" + e.getLocalizedMessage());
                return null;
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
    @Nullable
    protected ResultSet getResultSetLimit(int limit) {
        int _limit = limit;
        if (_limit < 0) {
            LOGGER.warn("The ResultSet limit should not be under 0. Set it to 0.");
            _limit = 0;
        }
        ResultSet resultSet;
        try {
            if (getBaseQuery().contains(" LIMIT ")) {
                resultSet = getResultSet();
            } else {
                Connection con = jdbcDataSource.getConnection();
                if(con == null){
                    LOGGER.error("Unable to get the connection.");
                    return null;
                }
                resultSet = con.createStatement().executeQuery(getBaseQuery() + " LIMIT " + _limit);
            }
        } catch (SQLException e) {
            LOGGER.error("Unable to execute the query '" + getBaseQuery() + "'.\n" + e.getLocalizedMessage());
            return null;
        }
        if(resultSet != null) {
            try {
                resultSet.beforeFirst();
            } catch (SQLException e) {
                LOGGER.error("Unable to go before the first ResultSet row.\n" + e.getLocalizedMessage());
                return null;
            }
        }
        return resultSet;
    }

    @Override
    public ResultSetMetaData getMetaData() {
        try {
            ResultSet rs = getResultSet();
            if (rs == null) {
                LOGGER.error("The ResultSet is null.");
            } else {
                return rs.getMetaData();
            }
        } catch (SQLException e) {
            LOGGER.error("Unable to get the metadata.\n" + e.getLocalizedMessage());
        }
        return null;
    }

    /**
     * Return the parent DataSource.
     *
     * @return The parent DataSource.
     */
    protected IJdbcDataSource getJdbcDataSource() {
        return jdbcDataSource;
    }

    @Override
    @Nullable
    public TableLocation getTableLocation() {
        return tableLocation;
    }

    @Override
    @NotNull
    public DataBaseType getDbType() {
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
                if(con == null){
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
                if(con == null){
                    LOGGER.error("Unable to get the connection.");
                    return false;
                }
                return JDBCUtilities.isTemporaryTable(con, getTableLocation().toString());
            } catch (SQLException e) {
                LOGGER.error("Unable to get the type of the table '" + getTableLocation().getTable() + ".\n" + e.getLocalizedMessage());
            }
        }
        return false;
    }

    @Override
    public Collection<String> getColumns() {
        try {
            ResultSet rs = getResultSet();
            if(rs == null){
                LOGGER.error("Unable to get the ResultSet");
                return null;
            }
            return JDBCUtilities
                    .getColumnNames(rs.getMetaData())
                    .stream()
                    .map(this::formatColumnName)
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (SQLException e) {
            LOGGER.error("Unable to get the collection of columns names");
            return null;
        }
    }

    @Override
    @NotNull
    public Map<String, String> getColumnsTypes() {
        Map<String, String> map = new LinkedHashMap<>();
        Collection<String> columns = getColumns();
        if(columns != null){
            getColumns().forEach((name) -> map.put(name, getColumnType(name)));
        }
        return map;
    }

    @Nullable
    private DataType getColumnDataType(@NotNull String columnName) {
        boolean found = false;
        int type = -1;
        if (tableLocation != null && !getName().isEmpty()) {
            try {
                Connection con = jdbcDataSource.getConnection();
                if(con == null){
                    LOGGER.error("Unable to get the connection.");
                    return null;
                }
                ResultSet rs = con.getMetaData().getColumns(tableLocation.getCatalog(),
                        tableLocation.getSchema(), TableLocation.capsIdentifier(tableLocation.getTable(),
                                getDbType().equals(DataBaseType.H2GIS)), null);
                while (rs.next() && !found) {
                    found = rs.getString("COLUMN_NAME").equalsIgnoreCase(columnName);
                    type = rs.getInt("DATA_TYPE");
                }
            } catch (SQLException e) {
                LOGGER.error("Unable to get the connection MetaData or to read it", e);
                return null;
            }
        } else {
            try {
                ResultSet rs = getResultSet();
                if(rs == null){
                    LOGGER.error("Unable to get the ResultSet.");
                    return null;
                }
                ResultSetMetaData metaData = rs.getMetaData();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    if (metaData.getColumnName(i).equalsIgnoreCase(columnName)) {
                        type = metaData.getColumnType(i);
                        break;
                    }
                }
            } catch (SQLException e) {
                LOGGER.error("unable to request the resultset metadata.", e);
                return null;
            }
        }
        int valueType = DataType.convertSQLTypeToValueType(type);
        if(valueType == -1) {
            return DataType.getDataType(19);
        }
        return DataType.getDataType(valueType);
    }

    @Override
    public String getColumnType(@NotNull String columnName) {
        if (!hasColumn(columnName)) {
            return null;
        }
        DataType dataType = getColumnDataType(columnName);
        Objects.requireNonNull(dataType);
        if ("OTHER".equals(dataType.name) || "JAVA_OBJECT".equals(dataType.name)) {
            return getGeometricType(columnName);
        }
        return dataType.name;
    }

    @Nullable
    private String getGeometricType(String columnName) {
        if (tableLocation != null && !getName().isEmpty()) {
            try {
                Connection con = jdbcDataSource.getConnection();
                if(con == null){
                    LOGGER.error("Unable to get the connection.");
                    return null;
                }
                return GeometryTableUtilities.getMetaData(con,
                        TableLocation.parse(tableLocation.getTable()),
                        TableLocation.capsIdentifier(columnName, dataBaseType.equals(DataBaseType.H2GIS))
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
                if(!map.containsKey(columnName)) {
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
    public boolean hasColumn(@NotNull String columnName) {
        return getColumns() != null &&
                getColumns().contains(TableLocation.capsIdentifier(columnName, getDbType().equals(DataBaseType.H2GIS)));
    }

    @Override
    public boolean hasColumn(@NotNull String columnName, @NotNull Class<?> clazz) {
        String name = TableLocation.capsIdentifier(columnName, dataBaseType.equals(DataBaseType.H2GIS));
        if (!hasColumn(name)) {
            return false;
        }
        if (Geometry.class.isAssignableFrom(clazz)) {
            String str = getGeometricType(name);
            return clazz.getSimpleName().equalsIgnoreCase(str) ||
                    (clazz.getSimpleName() + "Z").equalsIgnoreCase(str) ||
                    (clazz.getSimpleName() + "M").equalsIgnoreCase(str) ||
                    (clazz.getSimpleName() + "ZM").equalsIgnoreCase(str);
        } else {
            DataType dataType = getColumnDataType(name);
            if (dataType == null) {
                return false;
            }
            DataType dtClass = DataType.getDataType(DataType.getTypeFromClass(clazz));
            if (dataType.equals(dtClass)) {
                return true;
            }
            DataType dtSql = DataType.getDataType(DataType.convertSQLTypeToValueType(DataType.getTypeFromClass(clazz)));
            return dataType.equals(dtSql);
        }
    }

    @Override
    public int getColumnCount(){
        ResultSet rs = getResultSet();
        if(rs == null){
            return -1;
        }
        ResultSetMetaData metaData = null;
        try {
            metaData = rs.getMetaData();
        } catch (SQLException e) {
            LOGGER.error("Unable to get the ResultSet Metadata.", e);
            return -1;
        }
        try {
            return metaData.getColumnCount();
        } catch (SQLException e) {
            LOGGER.error("Unable to get the ResultSet Metadata column count.", e);
            return -1;
        }
    }

    @Override
    public int getRowCount() {
        Connection con = null;
        try {
            con = jdbcDataSource.getConnection();
        } catch (SQLException e) {
            LOGGER.error("Unable to get the connection.");
            return -1;
        }
        String query = "";
        if (tableLocation == null) {
           query = getBaseQuery();
        } else  {
           query =  "SELECT * FROM "+tableLocation.toString(getDbType());
        }
        try {
            ResultSet rowCountRs = con.createStatement().executeQuery("SELECT COUNT(*) FROM (" + query + ") as foo");
            rowCountRs.next();
            return rowCountRs.getInt(1);
        } catch (SQLException e) {
            LOGGER.error("Unable to get the number of rows.");
            return -1;
        }
    }

    @Override
    public Collection<String> getUniqueValues(@NotNull String column) {
        if (tableLocation == null) {
            throw new UnsupportedOperationException();
        }
        if (tableLocation.getTable().isEmpty()) {
            LOGGER.error("Unable to request unique values fo the column '" + column + "'.\n");
            throw new UnsupportedOperationException();
        } else {
            try {
                Connection con = jdbcDataSource.getConnection();
                if(con == null){
                    LOGGER.error("Unable to get the connection.");
                    return null;
                }
                TableLocation loc = getTableLocation();
                if(loc == null){
                    return null;
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
    @NotNull
    public Map<String, Object> firstRow() {
        Map<String, Object> map = new HashMap<>();
        if(isEmpty()){
            return map;
        }
        ResultSet rs = getResultSetLimit(1);
        try {
            rs.next();
        } catch (SQLException e) {
            LOGGER.error("Unable to get first row.", e);
        }
        for(String column : getColumns()){
            try {
                map.put(column, rs.getObject(column));
            } catch (SQLException e) {
                LOGGER.error("Unable to get data from first row.", e);
            }
        }
        return map;
    }

    @Override
    public String save(@NotNull String filePath, String encoding) {
        try {
            String toSave = getTableLocation() == null ? "(" + getBaseQuery() + ")" : getTableLocation().toString(getDbType());
            if(IOMethods.saveAsFile(getStatement().getConnection(), toSave, filePath, encoding, false)){
                return filePath;
            }else{
                LOGGER.error("Cannot save the table in the file : "+filePath);
                return null;
            }

        } catch (SQLException e) {
            LOGGER.error("Cannot save the table.\n", e);
            return null;
        }
    }

    @Override
    public String save(IJdbcDataSource dataSource, boolean deleteTable) {
        return save(dataSource,  deleteTable,  1000);
    }

    @Override
    public String save(IJdbcDataSource dataSource, String outputTableName, boolean deleteTable) {
        return save( dataSource,  outputTableName,  deleteTable, 1000);
    }

    @Override
    public String save(IJdbcDataSource dataSource, int batchSize) {
        return save(dataSource,  false,  batchSize);
    }

    @Override
    public String save(IJdbcDataSource dataSource, String outputTableName, boolean deleteTable, int batchSize) {
        if(dataSource==null){
            LOGGER.error("The output datasource connexion cannot ne null\n");
            return null;
        }
        try {
            if(getTableLocation() != null ){
                if(outputTableName==null || outputTableName.isEmpty()){
                    LOGGER.error("The output table name cannot be null or empty\n");
                    return null;
                }
                org.h2gis.utilities.TableLocation outputTableLoc = TableLocation.parse(outputTableName, dataSource.getDataBaseType() == DataBaseType.H2GIS);
                if(IOMethods.saveInDB(getStatement().getConnection(), getTableLocation(), getDbType(),  deleteTable, dataSource,outputTableLoc,batchSize)){
                    return outputTableLoc.toString(dataSource.getDataBaseType()  == DataBaseType.H2GIS);
                }
                else {
                    LOGGER.error("Cannot save the table.\n");
                    return null;
                }
            }
            else{
                org.h2gis.utilities.TableLocation outputTableLoc = TableLocation.parse(outputTableName, dataSource.getDataBaseType() == DataBaseType.H2GIS);
                if(IOMethods.saveInDB(getStatement().getConnection(), getBaseQuery(),  getDbType(), deleteTable, dataSource, outputTableLoc,batchSize)){
                    return outputTableLoc.toString(dataSource.getDataBaseType()  == DataBaseType.H2GIS);
                } else {
                    LOGGER.error("Cannot save the query.");
                    return null;
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Cannot save the table.\n", e);
            return null;
        }
    }

    @Override
    public String save(IJdbcDataSource dataSource, boolean deleteTable, int batchSize) {
        if(dataSource==null){
            LOGGER.error("The output datasource connexion cannot be null\n");
            return null;
        }
        try {
            if(getTableLocation() != null ){
                if(IOMethods.saveInDB(getStatement().getConnection(), getTableLocation(), getDbType(),
                        deleteTable, dataSource, getTableLocation(), batchSize)){
                    return getTableLocation().toString(DataBaseType.H2GIS);
                }
                else{
                    LOGGER.error("Cannot save the table.\n");
                    return null;
                }
            }
            else{
                LOGGER.error("Cannot save the table without a provided name.\n");
                return null;
            }
        } catch (SQLException e) {
            LOGGER.error("Cannot save the table.\n", e);
            return null;
        }
    }

    @NotNull
    private String getQuery() {
        return baseQuery.trim();
    }

    @Nullable
    protected String getQuery(String... columns) {
        TableLocation loc = getTableLocation();
        if(loc == null){
            return null;
        }
        return "SELECT " + String.join(", ", columns) + " FROM " + getTableLocation().getTable().toUpperCase();
    }

    @Override
    @NotNull
    public IBuilderResult filter(String filter) {
        String loc = getTableLocation() != null ? getTableLocation().toString(getDbType()) : getBaseQuery();
        IQueryBuilder builder = new QueryBuilder(getJdbcDataSource(), loc);
        String sql = getTableLocation() != null ? getTableLocation().toString() : getBaseQuery();
        builder.setStatement(getJdbcDataSource().getStatement(sql));
        return builder.filter(filter);
    }

    @Override
    @NotNull
    public IBuilderResult filter(GString filter) {
        String loc = getTableLocation() != null ? getTableLocation().toString(getDbType()) : getBaseQuery();
        IQueryBuilder builder = new QueryBuilder(getJdbcDataSource(), loc);
        builder.setStatement(getJdbcDataSource().getStatement(loc));
        return builder.filter(filter);
    }

    @Override
    @NotNull
    public IBuilderResult filter(String filter, List<Object> params) {
        String loc = getTableLocation() != null ? getTableLocation().toString(getDbType()) : getBaseQuery();
        IQueryBuilder builder = new QueryBuilder(getJdbcDataSource(), loc);
        builder.setStatement(getJdbcDataSource().getStatement(loc));
        return builder.filter(filter, params);
    }

    @Override
    @NotNull
    public IFilterBuilder columns(@NotNull String... columns) {
        String loc = getTableLocation() != null ? getTableLocation().toString(getDbType()) : getBaseQuery();
        IQueryBuilder builder = new QueryBuilder(getJdbcDataSource(), loc);
        builder.setStatement(getJdbcDataSource().getStatement(loc));
        return builder.columns(columns);
    }

    @Override
    public IJdbcTable<ResultSet, StreamResultSet> getTable() {
        return (IJdbcTable) asType(IJdbcTable.class);
    }

    @Override
    public IJdbcSpatialTable<StreamSpatialResultSet> getSpatialTable() {
        if (isSpatial()) {
            return (IJdbcSpatialTable) asType(IJdbcSpatialTable.class);
        } else {
            return null;
        }
    }

    @Override
    @NotNull
    public List<Object> getFirstRow() {
        List<Object> list = new ArrayList<>();
        ResultSet rs = getResultSet();
        if(rs != null) {
            try {
                rs.first();
                for (int i = 1; i <= getColumnCount(); i++) {
                    list.add(rs.getObject(i));
                }
            } catch (SQLException e) {
                LOGGER.error("Unable to query the first row of the table.\n" + e.getLocalizedMessage());
            }
        }
        return list;
    }

    @Override
    public Object invokeMethod(String name, Object args) {
        try {
            return getMetaClass().invokeMethod(this, name, args);
        } catch (MissingMethodException e) {
            LOGGER.debug("Unable to find the '" + name + "' methods, trying with the getter");
            return getMetaClass()
                    .invokeMethod(this, "get" + name.substring(0, 1).toUpperCase() + name.substring(1), args);
        }
    }

    @Override
    @Nullable
    public Object getProperty(String propertyName) {
        if (propertyName == null) {
            LOGGER.error("Trying to get null property name.");
            return null;
        }
        //First test the predefined properties
        if (propertyName.equals(META_PROPERTY)) {
            return getMetaData();
        }
        Collection<String> columns = getColumns();
        if (columns != null &&
                (columns.contains(propertyName.toLowerCase()) || columns.contains(propertyName.toUpperCase()))
                || "id".equalsIgnoreCase(propertyName)) {
            try {
                if (isBeforeFirst() || (this.getRow() == 0 && this.getRowCount() == 0)) {
                    return new JdbcColumn(formatColumnName(propertyName), this.getName(), getJdbcDataSource());
                }
                return getObject(propertyName);
            } catch (SQLException e) {
                LOGGER.debug("Unable to find the column '" + propertyName + "'.\n" + e.getLocalizedMessage());
            }
        }
        return getMetaClass().getProperty(this, propertyName);
    }

    @Override
    public void setProperty(String propertyName, Object newValue) {
        getMetaClass().setProperty(this, propertyName, newValue);
    }

    @Override
    public Statement getStatement() {
        return statement;
    }

    @Override
    public Object asType(@NotNull Class<?> clazz) {
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
            Collection<String> columnNames = getColumns();
            if(columnNames == null){
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
            if(rs != null) {
                try {
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
                } catch (Exception e) {
                    LOGGER.error("Error while reading the table '" + getName() + "'.\n" + e.getLocalizedMessage());
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
        return getDbType() == DataBaseType.H2GIS ? column.toUpperCase() : column.toLowerCase();
    }

    @Override
    @NotNull
    public JdbcTableSummary getSummary() {
        return new JdbcTableSummary(getTableLocation(), getColumnCount(), getRowCount());
    }

    @NotNull
    public List<Object> getParams() {
        return params;
    }

    @Override
    public void setStatement(Statement statement) {
        this.statement = statement;
    }

}