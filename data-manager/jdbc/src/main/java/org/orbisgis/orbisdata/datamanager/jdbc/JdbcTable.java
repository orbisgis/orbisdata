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

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.MissingMethodException;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.h2.value.DataType;
import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.SFSUtilities;
import org.locationtech.jts.geom.Geometry;
import org.orbisgis.commons.printer.Ascii;
import org.orbisgis.commons.printer.Html;
import org.orbisgis.commons.printer.ICustomPrinter;
import org.orbisgis.orbisdata.datamanager.api.dataset.*;
import org.orbisgis.orbisdata.datamanager.api.datasource.IJdbcDataSource;
import org.orbisgis.orbisdata.datamanager.api.dsl.IConditionOrOptionBuilder;
import org.orbisgis.orbisdata.datamanager.api.dsl.IOptionBuilder;
import org.orbisgis.orbisdata.datamanager.jdbc.dsl.OptionBuilder;
import org.orbisgis.orbisdata.datamanager.jdbc.dsl.WhereBuilder;
import org.orbisgis.orbisdata.datamanager.jdbc.io.IOMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

import static org.orbisgis.commons.printer.ICustomPrinter.CellPosition.*;

/**
 * Contains the methods which are in common to all the IJdbcTable subclasses.
 * Implements the {@link GroovyObject} to simplify the methods calling (i.e. .tableLocation instead of
 * .getTableLocation() ).
 */
public abstract class JdbcTable extends DefaultResultSet implements IJdbcTable, GroovyObject {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcTable.class);

    /** Default width of the columns in ascii print */
    private static final int ASCII_COLUMN_WIDTH = 20;
    /** MetaClass use for groovy methods/properties binding */
    private MetaClass metaClass;
    /** Type of the database */
    private DataBaseType dataBaseType;
    /** DataSource to execute query */
    private IJdbcDataSource jdbcDataSource;
    /** Table location */
    private TableLocation tableLocation;
    /** Statement */
    private Statement statement;
    /** Base SQL query for the creation of the ResultSet */
    private String baseQuery;
    /** Cached resultSet */
    protected ResultSet resultSet;

    /**
     * Main constructor.
     *
     * @param dataBaseType Type of the DataBase where this table comes from.
     * @param tableLocation TableLocation that identify the represented table.
     * @param baseQuery Query for the creation of the ResultSet
     * @param statement Statement used to request the database.
     * @param jdbcDataSource DataSource to use for the creation of the resultSet.
     */
    public JdbcTable(DataBaseType dataBaseType, IJdbcDataSource jdbcDataSource, TableLocation tableLocation,
                     Statement statement, String baseQuery){
        this.metaClass = InvokerHelper.getMetaClass(getClass());
        this.dataBaseType = dataBaseType;
        this.jdbcDataSource = jdbcDataSource;
        this.tableLocation = tableLocation;
        this.statement = statement;
        this.baseQuery = baseQuery;
    }

    /**
     * Return the base query for the creation of the ResultSet.
     *
     * @return The base query.
     */
    protected String getBaseQuery(){
        return baseQuery;
    }

    @Override
    protected ResultSet getResultSet(){
        if(resultSet == null) {
            try {
                resultSet = getStatement().executeQuery(getBaseQuery());
            } catch (SQLException e) {
                LOGGER.error("Unable to execute the query '"+getBaseQuery()+"'.\n"+e.getLocalizedMessage());
                return null;
            }
        }
        return resultSet;
    }

    /**
     * Return the {@link ResultSet} with a limit.
     *
     * @param limit Limit of the result set.
     *
     * @return The {@link ResultSet} with a limit.
     */
    private ResultSet getResultSetLimit(int limit){
        int _limit = limit;
        if(_limit < 0){
            LOGGER.warn("The ResultSet limit should not be under 0. Set it to 0.");
            _limit = 0;
        }
        ResultSet resultSet;
        try {
            if(getBaseQuery().contains(" LIMIT ")){
                resultSet = getResultSet();
            }
            else {
                resultSet = jdbcDataSource.getConnection().createStatement().executeQuery(getBaseQuery() + " LIMIT " + _limit);
            }
        } catch (SQLException e) {
            LOGGER.error("Unable to execute the query '"+getBaseQuery()+"'.\n"+e.getLocalizedMessage());
            return null;
        }
        try {
            resultSet.beforeFirst();
        } catch (SQLException e) {
            LOGGER.error("Unable to go before the first ResultSet row.\n" + e.getLocalizedMessage());
            return null;
        }
        return resultSet;
    }

    @Override
    public ResultSetMetaData getMetaData(){
        try {
            ResultSet rs = getResultSetLimit(0);
            if(rs == null){
                LOGGER.error("The ResultSet is null.");
            }
            else {
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
    protected IJdbcDataSource getJdbcDataSource(){
        return jdbcDataSource;
    }

    @Override
    public ITableLocation getTableLocation() {
        return tableLocation;
    }

    @Override
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
    public boolean isSpatial(){
        return false;
    }

    @Override
    public boolean isLinked(){
        try {
            return JDBCUtilities.isLinkedTable(jdbcDataSource.getConnection(), getTableLocation().toString());
        } catch (SQLException e) {
            LOGGER.error("Unable to get the type of the table '"+getTableLocation().getTable()+".\n"+e.getLocalizedMessage());
        }
        return false;
    }

    @Override
    public boolean isTemporary(){
        try {
            return JDBCUtilities.isTemporaryTable(jdbcDataSource.getConnection(), getTableLocation().toString());
        } catch (SQLException e) {
            LOGGER.error("Unable to get the type of the table '"+getTableLocation().getTable()+".\n"+e.getLocalizedMessage());
        }
        return false;
    }

    @Override
    public Collection<String> getColumns() {
        try {
            return JDBCUtilities
                    .getFieldNames(getResultSetLimit(0).getMetaData())
                    .stream()
                    .map(this::formatColumnName)
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (SQLException e) {
            LOGGER.error("Unable to get the collection of columns names");
            return null;
        }
    }

    @Override
    public Map<String, String> getColumnsTypes(){
        Map<String, String> map = new LinkedHashMap<>();
        getColumns().forEach((name) -> {
            map.put(name, getColumnType(name));
        });
        return map;
    }

    private DataType getColumnDataType(String columnName){
        boolean found = false;
        int type = -1;
        ResultSet rs;
        try {
            rs = jdbcDataSource.getConnection().getMetaData().getColumns(tableLocation.getCatalog(),
                    tableLocation.getSchema(), TableLocation.capsIdentifier(tableLocation.getTable(),
                            getDbType().equals(DataBaseType.H2GIS)), null);
        } catch (SQLException e) {
            LOGGER.error("Unable to get the connection MetaData.\n"+e.getLocalizedMessage());
            return null;
        }
        try {
            while (rs.next() && !found) {
                found = rs.getString("COLUMN_NAME").equalsIgnoreCase(columnName);
                type = DataType.convertSQLTypeToValueType(rs.getInt("DATA_TYPE"));
            }
        } catch (SQLException e) {
            LOGGER.error("Unable to read the ResultSet.\n"+e.getLocalizedMessage());
            return null;
        }
        return DataType.getDataType(type);
    }

    @Override
    public String getColumnType(String columnName){
        if(!hasColumn(columnName)){
            return null;
        }
        DataType dataType = getColumnDataType(columnName);
        Objects.requireNonNull(dataType);
        if("OTHER".equals(dataType.name)){
            try {
                return SFSUtilities.getGeometryTypeNameFromCode(
                        SFSUtilities.getGeometryType(jdbcDataSource.getConnection(), tableLocation, columnName));
            } catch (SQLException e) {
                LOGGER.error("Unable to get the geometric type of the column '" + columnName + "'\n" +
                        e.getLocalizedMessage());
            }
        }
        return dataType.name;
    }

    @Override
    public boolean hasColumn(String columnName){
        return getColumns().contains(TableLocation.capsIdentifier(columnName, getDbType().equals(DataBaseType.H2GIS)));
    }

    @Override
    public boolean hasColumn(String columnName, Class clazz){
        if(!hasColumn(columnName)){
            return false;
        }
        if(Geometry.class.isAssignableFrom(clazz)){
            String str = null;
            try {
                str = SFSUtilities.getGeometryTypeNameFromCode(
                        SFSUtilities.getGeometryType(jdbcDataSource.getConnection(), tableLocation, columnName));
            } catch (SQLException e) {
                LOGGER.error("Unable to get the geometric type of the column '" + columnName + "'\n" + e.getLocalizedMessage());
            }
            return clazz.getSimpleName().equalsIgnoreCase(str) ||
                    (clazz.getSimpleName()+"Z").equalsIgnoreCase(str) ||
                    (clazz.getSimpleName()+"M").equalsIgnoreCase(str) ||
                    (clazz.getSimpleName()+"ZM").equalsIgnoreCase(str);
        }
        else{
            DataType dataType = getColumnDataType(columnName);
            if(dataType == null){
                return false;
            }
            DataType dtClass = DataType.getDataType(DataType.getTypeFromClass(clazz));
            if(dataType.equals(dtClass)){
                return true;
            }
            DataType dtSql = DataType.getDataType(DataType.convertSQLTypeToValueType(DataType.getTypeFromClass(clazz)));
            return dataType.equals(dtSql);
        }
    }

    @Override
    public int getRowCount(){
        if(tableLocation.getTable().isEmpty()) {
            int count = 0;
            ResultSet rs = getResultSet();
            try {
                rs.beforeFirst();
            } catch (SQLException e) {
                LOGGER.error("Unable to get the row count on " + tableLocation.toString() + ".\n", e);
                return -1;
            }
            try {
                while (rs.next()) {
                    count++;
                }
            } catch (SQLException e) {
                LOGGER.error("Unable to iterate on " + tableLocation.toString() + ".\n", e);
                return -1;
            }
            return count;
        }
        else {
            try {
                return JDBCUtilities.getRowCount(jdbcDataSource.getConnection(),
                        getTableLocation().toString(getDbType()));
            } catch (SQLException e) {
                LOGGER.error("Unable to get the row count on " + tableLocation.toString() + ".\n", e);
                return -1;
            }
        }
    }

    @Override
    public Collection<String> getUniqueValues(String column){
        if(tableLocation.getTable().isEmpty()) {
            LOGGER.error("Unable to request unique values fo the column '" + column + "'.\n");
        }
        else {
            try {
                return JDBCUtilities.getUniqueFieldValues(jdbcDataSource.getConnection(),
                        getTableLocation().toString(getDbType()),
                        column);
            } catch (SQLException e) {
                LOGGER.error("Unable to request unique values fo the column '" + column + "'.\n", e);
            }
        }
        return null;
    }

    @Override
    public boolean save(String filePath, String encoding) {
        try {
            return IOMethods.saveAsFile(getStatement().getConnection(), getTableLocation().toString(getDbType()),
                    filePath,encoding);
        } catch (SQLException e) {
            LOGGER.error("Cannot save the table.\n" + e.getLocalizedMessage());
            return false;
        }
    }

    private String getQuery(){
        return baseQuery.trim();
    }

    private String getQuery(String ... columns){
        return "SELECT " + String.join(", ", columns) + " FROM " + getTableLocation().getTable().toUpperCase();
    }

    @Override
    public IJdbcTable columns(String... columns){
        WhereBuilder builder = new WhereBuilder(getQuery(columns), getJdbcDataSource());
        if(isSpatial()){
            return (IJdbcTable)builder.getSpatialTable();
        }
        else {
            return (IJdbcTable)builder.getTable();
        }
    }

    @Override
    public IJdbcTable columns(List<String> columns){
        WhereBuilder builder = new WhereBuilder(getQuery(columns.toArray(new String[0])), getJdbcDataSource());
        if(isSpatial()){
            return (IJdbcTable)builder.getSpatialTable();
        }
        else {
            return (IJdbcTable)builder.getTable();
        }
    }

    @Override
    public IConditionOrOptionBuilder where(String condition) {
        return new WhereBuilder(getQuery(), getJdbcDataSource()).where(condition);
    }

    @Override
    public IOptionBuilder groupBy(String... fields) {
        return new OptionBuilder(getQuery(), getJdbcDataSource()).groupBy(fields);
    }

    @Override
    public IOptionBuilder orderBy(Map<String, Order> orderByMap) {
        return new OptionBuilder(getQuery(), getJdbcDataSource()).orderBy(orderByMap);
    }

    @Override
    public IOptionBuilder orderBy(String field, Order order) {
        return new OptionBuilder(getQuery(), getJdbcDataSource()).orderBy(field, order);
    }

    @Override
    public IOptionBuilder orderBy(String field) {
        return new OptionBuilder(getQuery(), getJdbcDataSource()).orderBy(field);
    }

    @Override
    public IOptionBuilder limit(int limitCount) {
        return new OptionBuilder(getQuery(), getJdbcDataSource()).limit(limitCount);
    }

    @Override
    public IJdbcTable getTable() {
        return (IJdbcTable)asType(IJdbcTable.class);
    }

    @Override
    public IJdbcSpatialTable getSpatialTable() {
        if(isSpatial()) {
            return (IJdbcSpatialTable) asType(IJdbcSpatialTable.class);
        }
        else{
            return null;
        }
    }

    @Override
    public List<Object> getFirstRow(){
        List<Object> list = new ArrayList<>();
        ResultSet rs = getResultSetLimit(1);
        try {
            if(rs.next()){
                for(int i=1; i<=getColumnCount(); i++){
                    list.add(rs.getObject(i));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Unable to query the table.\n" + e.getLocalizedMessage());
        }
        return list;
    }

    @Override
    public Object invokeMethod(String name, Object args) {
        try {
            return getMetaClass().invokeMethod(this, name, args);
        } catch (MissingMethodException e) {
            LOGGER.debug("Unable to find the '"+name+"' methods, trying with the getter");
            return getMetaClass()
                    .invokeMethod(this, "get" + name.substring(0, 1).toUpperCase() + name.substring(1), args);
        }
    }

    @Override
    public Object getProperty(String propertyName) {
        if(propertyName == null){
            LOGGER.error("Trying to get null property name.");
            return null;
        }
        //First test the predefined properties
        if(propertyName.equals(META_PROPERTY)){
            return getMetaData();
        }
        Collection<String> columns = getColumns();
        if(columns!= null &&
                (columns.contains(propertyName.toLowerCase()) || columns.contains(propertyName.toUpperCase()))
                || "id".equalsIgnoreCase(propertyName)) {
            try {
                if(isBeforeFirst() || (this.getRow() == 0 && this.getRowCount() == 0)){
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
    public Statement getStatement(){
        return statement;
    }

    @Override
    public Object asType(Class clazz){
        if(ICustomPrinter.class.isAssignableFrom(clazz)){
            StringBuilder builder = new StringBuilder();
            ICustomPrinter printer;
            if(clazz == Ascii.class){
                printer = new Ascii(builder);
            }
            else if(clazz == Html.class){
                printer = new Html(builder);
            }
            else{
                return this;
            }
            Collection<String> columnNames = getColumns();

            printer.startTable(ASCII_COLUMN_WIDTH, columnNames.size());
            printer.appendTableTitle(this.getName());
            printer.appendTableLineSeparator();
            for(String column : columnNames){
                printer.appendTableHeaderValue(column, CENTER);
            }
            printer.appendTableLineSeparator();
            ResultSet rs = getResultSet();
            try {
                while (rs.next()) {
                    for (String column : columnNames) {
                        Object obj = rs.getObject(column);
                        if(obj instanceof Number){
                            printer.appendTableValue(rs.getObject(column), RIGHT);
                        }
                        else{
                            printer.appendTableValue(rs.getObject(column), LEFT);
                        }
                    }
                }
            } catch(Exception e){
                LOGGER.error("Error while reading the table '"+getName()+"'.\n" + e.getLocalizedMessage());
            }
            printer.appendTableLineSeparator();
            printer.endTable();

            return printer;
        }
        else if(ITable.class.isAssignableFrom(clazz)) {
            return this;
        }
        return null;
    }

    /**
     * Format the column according to the DB type.
     *
     * @param column Columne name to format.
     *
     * @return The formatted column name.
     */
    private String formatColumnName(String column){
        return getDbType()==DataBaseType.H2GIS ? column.toUpperCase() : column.toLowerCase();
    }
}
