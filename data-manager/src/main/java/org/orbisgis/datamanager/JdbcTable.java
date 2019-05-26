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
 * DataManager is distributed under GPL 3 license.
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
package org.orbisgis.datamanager;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.MissingMethodException;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.h2.value.DataType;
import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.SFSUtilities;
import org.h2gis.utilities.TableLocation;
import org.locationtech.jts.geom.Geometry;
import org.orbisgis.commons.printer.Ascii;
import org.orbisgis.datamanager.dsl.OptionBuilder;
import org.orbisgis.datamanager.dsl.WhereBuilder;
import org.orbisgis.datamanager.io.IOMethods;
import org.orbisgis.datamanagerapi.dataset.DataBaseType;
import org.orbisgis.datamanagerapi.dataset.IJdbcTable;
import org.orbisgis.datamanagerapi.dataset.ISpatialTable;
import org.orbisgis.datamanagerapi.dataset.ITable;
import org.orbisgis.datamanagerapi.dsl.IConditionOrOptionBuilder;
import org.orbisgis.datamanagerapi.dsl.IOptionBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;

import static org.orbisgis.commons.printer.Ascii.CellPosition.*;

/**
 * Contains the methods which are in common to all the IJdbcTable subclasses.
 * Implements the {@link GroovyObject} to simplify the methods calling (i.e. .tableLocation instead of
 * .getTableLocation() ).
 */
public abstract class JdbcTable extends DefaultResultSet implements IJdbcTable, GroovyObject {

    /** Default width of the columns in ascii print */
    private static final int ASCII_COLUMN_WIDTH = 20;
    /** MetaClass use for groovy methods/properties binding */
    private MetaClass metaClass;
    /** Type of the database */
    private DataBaseType dataBaseType;
    /** DataSource to execute query */
    private JdbcDataSource jdbcDataSource;
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
    public JdbcTable(DataBaseType dataBaseType, JdbcDataSource jdbcDataSource, TableLocation tableLocation,
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

    /**
     * Return the contained ResultSet.
     *
     * @return The table ResultSet.
     */
    protected abstract ResultSet getResultSet();

    /**
     * Return the parent DataSource.
     *
     * @return The parent DataSource.
     */
    protected JdbcDataSource getJdbcDataSource(){
        return jdbcDataSource;
    }

    @Override
    public TableLocation getTableLocation() {
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
    public Collection<String> getColumnNames() {
        try {
            return JDBCUtilities.getFieldNames(getResultSet().getMetaData());
        } catch (SQLException e) {
            LOGGER.error("Unable to get the collection of columns names");
            return null;
        }
    }

    @Override
    public boolean hasColumn(String columnName){
        return getColumnNames().contains(TableLocation.capsIdentifier(columnName, getDbType().equals(DataBaseType.H2GIS)));
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
            return clazz.getSimpleName().equalsIgnoreCase(str);
        }
        else {
            ResultSet rs;
            int type = DataType.getTypeFromClass(clazz);
            boolean hasGoodType = false;
            try {
                rs = jdbcDataSource.getConnection().getMetaData().getColumns(tableLocation.getCatalog(null),
                        tableLocation.getSchema(null), TableLocation.capsIdentifier(tableLocation.getTable(),
                                getDbType().equals(DataBaseType.H2GIS)), null);
                while (rs.next() && !hasGoodType) {
                    hasGoodType = (DataType.convertSQLTypeToValueType(rs.getInt("DATA_TYPE")) == type ||
                            rs.getInt("DATA_TYPE") == type)&&
                            rs.getString("COLUMN_NAME").equalsIgnoreCase(columnName);
                }
            } catch (SQLException e) {
                LOGGER.error("Unable to get the type of the column '" + columnName + "'\n" + e.getLocalizedMessage());
            }
            return hasGoodType;
        }
    }

    @Override
    public int getRowCount(){
        try {
            return JDBCUtilities.getRowCount(jdbcDataSource.getConnection(),
                    getTableLocation().toString(getDbType().equals(DataBaseType.H2GIS)));
        } catch (SQLException e) {
            LOGGER.error("Unable to get the row count on "+tableLocation.toString()+".\n"+e.getLocalizedMessage());
            return -1;
        }
    }

    @Override
    public Collection<String> getUniqueValues(String column){
        try {
            return JDBCUtilities.getUniqueFieldValues(jdbcDataSource.getConnection(),
                    getTableLocation().getTable(), column);
        } catch (SQLException e) {
            LOGGER.error("Unable to request unique values fo the column '"+column+"'.\n"+e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public boolean save(String filePath, String encoding) {
        try {
            return IOMethods.saveAsFile(getStatement().getConnection(), getTableLocation().toString(true),
                    filePath,encoding);
        } catch (SQLException e) {
            LOGGER.error("Cannot save the table.\n" + e.getLocalizedMessage());
            return false;
        }
    }

    private String getQuery(){
        return "SELECT * FROM " + getTableLocation().getTable().toUpperCase();
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
    public ITable getTable() {
        return (ITable)asType(ITable.class);
    }

    @Override
    public ISpatialTable getSpatialTable() {
        return (ISpatialTable)asType(ISpatialTable.class);
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
            return getMetadata();
        }
        Collection<String> columns = getColumnNames();
        if(columns!= null &&
                (columns.contains(propertyName.toLowerCase()) || columns.contains(propertyName.toUpperCase()))
                || "id".equals(propertyName)) {
            try {
                if(isBeforeFirst()){
                    return new JdbcColumn(propertyName, this.getName(), getJdbcDataSource());
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
        if(clazz.equals(Ascii.class)){
            StringBuilder builder = new StringBuilder();
            Collection<String> columnNames = getColumnNames();

            Ascii ascii = new Ascii(builder);
            ascii.startTable(ASCII_COLUMN_WIDTH, 1);
            ascii.appendTableLineSeparator();
            ascii.appendTableValue(this.getName(), CENTER);
            ascii.endTable();
            ascii.startTable(ASCII_COLUMN_WIDTH, columnNames.size());
            ascii.appendTableLineSeparator();
            for(String column : columnNames){
                ascii.appendTableValue(column, CENTER);
            }
            ascii.appendTableLineSeparator();
            ResultSet rs = getResultSet();
            try {
                while (rs.next()) {
                    for (String column : columnNames) {
                        Object obj = rs.getObject(column);
                        if(obj instanceof Number){
                            ascii.appendTableValue(rs.getObject(column), RIGHT);
                        }
                        else{
                            ascii.appendTableValue(rs.getObject(column), LEFT);
                        }
                    }
                }
            } catch(Exception e){
                LOGGER.error("Error while reading the table '"+getName()+"'.\n" + e.getLocalizedMessage());
            }
            ascii.appendTableLineSeparator();
            ascii.endTable();

            return ascii;
        }
        return this;
    }
}
