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

import groovy.lang.MetaClass;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.datamanager.dsl.OptionBuilder;
import org.orbisgis.datamanager.dsl.WhereBuilder;
import org.orbisgis.datamanager.io.IOMethods;
import org.orbisgis.datamanagerapi.dataset.DataBaseType;
import org.orbisgis.datamanagerapi.dataset.IJdbcTable;
import org.orbisgis.datamanagerapi.dataset.ISpatialTable;
import org.orbisgis.datamanagerapi.dataset.ITable;
import org.orbisgis.datamanagerapi.dsl.IConditionOrOptionBuilder;
import org.orbisgis.datamanagerapi.dsl.IOptionBuilder;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.util.*;

/**
 * Contains the methods which are in common to all the IJdbcTable subclasses.
 */
public abstract class JdbcTable implements IJdbcTable {

    /** MetaClass use for groovy methods/properties binding */
    private MetaClass metaClass;
    /** Type of the database */
    private DataBaseType dataBaseType;
    /** DataSource to execute query */
    private JdbcDataSource jdbcDataSource;
    /** Table location */
    private TableLocation tableLocation;
    /** Map of the properties */
    private Map<String, Object> propertyMap;

    public JdbcTable(DataBaseType dataBaseType, JdbcDataSource jdbcDataSource, TableLocation tableLocation){
        this.metaClass = InvokerHelper.getMetaClass(getClass());
        this.dataBaseType = dataBaseType;
        this.jdbcDataSource = jdbcDataSource;
        this.tableLocation = tableLocation;
        propertyMap = new HashMap<>();
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
    public Map<String, Object> getPropertyMap() {
        return propertyMap;
    }

    @Override
    public void setProperty(String propertyName, Object newValue) {
        propertyMap.put(propertyName, newValue);
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
    public Collection<String> getColumnNames() {
        try {
            return JDBCUtilities.getFieldNames(getResultSet().getMetaData());
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public boolean save(String filePath) {
        return save(filePath, null);
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
    public Object asType(Class clazz) {
        return null;
    }

    @Override
    public ITable getTable() {
        return (ITable)asType(ITable.class);
    }

    @Override
    public ISpatialTable getSpatialTable() {
        return (ISpatialTable)asType(ISpatialTable.class);
    }



    /* ***************** */
    /* ResultSet methods */
    /* ***************** */

    @Override
    public boolean next() throws SQLException {
        return getResultSet().next();
    }

    @Override
    public void close() throws SQLException {
        getResultSet().close();
    }

    @Override
    public boolean wasNull() throws SQLException {
        return getResultSet().wasNull();
    }

    @Override
    public String getString(int i) throws SQLException {
        return getResultSet().getString(i);
    }

    @Override
    public boolean getBoolean(int i) throws SQLException {
        return getResultSet().getBoolean(i);
    }

    @Override
    public byte getByte(int i) throws SQLException {
        return getResultSet().getByte(i);
    }

    @Override
    public short getShort(int i) throws SQLException {
        return getResultSet().getShort(i);
    }

    @Override
    public int getInt(int i) throws SQLException {
        return getResultSet().getInt(i);
    }

    @Override
    public long getLong(int i) throws SQLException {
        return getResultSet().getLong(i);
    }

    @Override
    public float getFloat(int i) throws SQLException {
        return getResultSet().getFloat(i);
    }

    @Override
    public double getDouble(int i) throws SQLException {
        return getResultSet().getDouble(i);
    }

    @Override
    public BigDecimal getBigDecimal(int i, int i1) throws SQLException {
        return getResultSet().getBigDecimal(i, i1);
    }

    @Override
    public byte[] getBytes(int i) throws SQLException {
        return getResultSet().getBytes(i);
    }

    @Override
    public Date getDate(int i) throws SQLException {
        return getResultSet().getDate(i);
    }

    @Override
    public Time getTime(int i) throws SQLException {
        return getResultSet().getTime(i);
    }

    @Override
    public Timestamp getTimestamp(int i) throws SQLException {
        return getResultSet().getTimestamp(i);
    }

    @Override
    public InputStream getAsciiStream(int i) throws SQLException {
        return getResultSet().getAsciiStream(i);
    }

    @Override
    public InputStream getUnicodeStream(int i) throws SQLException {
        return getResultSet().getUnicodeStream(i);
    }

    @Override
    public InputStream getBinaryStream(int i) throws SQLException {
        return getResultSet().getBinaryStream(i);
    }

    @Override
    public String getString(String s) throws SQLException {
        return getResultSet().getString(s);
    }

    @Override
    public boolean getBoolean(String s) throws SQLException {
        return getResultSet().getBoolean(s);
    }

    @Override
    public byte getByte(String s) throws SQLException {
        return getResultSet().getByte(s);
    }

    @Override
    public short getShort(String s) throws SQLException {
        return getResultSet().getShort(s);
    }

    @Override
    public int getInt(String s) throws SQLException {
        return getResultSet().getInt(s);
    }

    @Override
    public long getLong(String s) throws SQLException {
        return getResultSet().getLong(s);
    }

    @Override
    public float getFloat(String s) throws SQLException {
        return getResultSet().getFloat(s);
    }

    @Override
    public double getDouble(String s) throws SQLException {
        return getResultSet().getDouble(s);
    }

    @Override
    public BigDecimal getBigDecimal(String s, int i) throws SQLException {
        return getResultSet().getBigDecimal(s, i);
    }

    @Override
    public byte[] getBytes(String s) throws SQLException {
        return getResultSet().getBytes(s);
    }

    @Override
    public Date getDate(String s) throws SQLException {
        return getResultSet().getDate(s);
    }

    @Override
    public Time getTime(String s) throws SQLException {
        return getResultSet().getTime(s);
    }

    @Override
    public Timestamp getTimestamp(String s) throws SQLException {
        return getResultSet().getTimestamp(s);
    }

    @Override
    public InputStream getAsciiStream(String s) throws SQLException {
        return getResultSet().getAsciiStream(s);
    }

    @Override
    public InputStream getUnicodeStream(String s) throws SQLException {
        return getResultSet().getUnicodeStream(s);
    }

    @Override
    public InputStream getBinaryStream(String s) throws SQLException {
        return getResultSet().getBinaryStream(s);
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return getResultSet().getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        getResultSet().clearWarnings();
    }

    @Override
    public String getCursorName() throws SQLException {
        return getResultSet().getCursorName();
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return getResultSet().getMetaData();
    }

    @Override
    public Object getObject(int i) throws SQLException {
        return getResultSet().getObject(i);
    }

    @Override
    public Object getObject(String s) throws SQLException {
        return getResultSet().getObject(s);
    }

    @Override
    public int findColumn(String s) throws SQLException {
        return getResultSet().findColumn(s);
    }

    @Override
    public Reader getCharacterStream(int i) throws SQLException {
        return getResultSet().getCharacterStream(i);
    }

    @Override
    public Reader getCharacterStream(String s) throws SQLException {
        return getResultSet().getCharacterStream(s);
    }

    @Override
    public BigDecimal getBigDecimal(int i) throws SQLException {
        return getResultSet().getBigDecimal(i);
    }

    @Override
    public BigDecimal getBigDecimal(String s) throws SQLException {
        return getResultSet().getBigDecimal(s);
    }

    @Override
    public boolean isBeforeFirst() throws SQLException {
        return getResultSet().isBeforeFirst();
    }

    @Override
    public boolean isAfterLast() throws SQLException {
        return getResultSet().isAfterLast();
    }

    @Override
    public boolean isFirst() throws SQLException {
        return getResultSet().isFirst();
    }

    @Override
    public boolean isLast() throws SQLException {
        return getResultSet().isLast();
    }

    @Override
    public void beforeFirst() throws SQLException {
        getResultSet().beforeFirst();
    }

    @Override
    public void afterLast() throws SQLException {
        getResultSet().afterLast();
    }

    @Override
    public boolean first() throws SQLException {
        return getResultSet().first();
    }

    @Override
    public boolean last() throws SQLException {
        return getResultSet().last();
    }

    @Override
    public int getRow() throws SQLException {
        return getResultSet().getRow();
    }

    @Override
    public boolean absolute(int i) throws SQLException {
        return getResultSet().absolute(i);
    }

    @Override
    public boolean relative(int i) throws SQLException {
        return getResultSet().relative(i);
    }

    @Override
    public boolean previous() throws SQLException {
        return getResultSet().previous();
    }

    @Override
    public void setFetchDirection(int i) throws SQLException {
        getResultSet().setFetchDirection(i);
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return getResultSet().getFetchDirection();
    }

    @Override
    public void setFetchSize(int i) throws SQLException {
        getResultSet().setFetchSize(i);
    }

    @Override
    public int getFetchSize() throws SQLException {
        return getResultSet().getFetchSize();
    }

    @Override
    public int getType() throws SQLException {
        return getResultSet().getType();
    }

    @Override
    public int getConcurrency() throws SQLException {
        return getResultSet().getConcurrency();
    }

    @Override
    public boolean rowUpdated() throws SQLException {
        return getResultSet().rowUpdated();
    }

    @Override
    public boolean rowInserted() throws SQLException {
        return getResultSet().rowInserted();
    }

    @Override
    public boolean rowDeleted() throws SQLException {
        return getResultSet().rowDeleted();
    }

    @Override
    public void updateNull(int i) throws SQLException {
        getResultSet().updateNull(i);
    }

    @Override
    public void updateBoolean(int i, boolean b) throws SQLException {
        getResultSet().updateBoolean(i, b);
    }

    @Override
    public void updateByte(int i, byte b) throws SQLException {
        getResultSet().updateByte(i, b);
    }

    @Override
    public void updateShort(int i, short i1) throws SQLException {
        getResultSet().updateShort(i, i1);
    }

    @Override
    public void updateInt(int i, int i1) throws SQLException {
        getResultSet().updateInt(i, i1);
    }

    @Override
    public void updateLong(int i, long l) throws SQLException {
        getResultSet().updateLong(i, l);
    }

    @Override
    public void updateFloat(int i, float v) throws SQLException {
        getResultSet().updateFloat(i, v);
    }

    @Override
    public void updateDouble(int i, double v) throws SQLException {
        getResultSet().updateDouble(i, v);
    }

    @Override
    public void updateBigDecimal(int i, BigDecimal bigDecimal) throws SQLException {
        getResultSet().updateBigDecimal(i, bigDecimal);
    }

    @Override
    public void updateString(int i, String s) throws SQLException {
        getResultSet().updateString(i, s);
    }

    @Override
    public void updateBytes(int i, byte[] bytes) throws SQLException {
        getResultSet().updateBytes(i, bytes);
    }

    @Override
    public void updateDate(int i, Date date) throws SQLException {
        getResultSet().updateDate(i, date);
    }

    @Override
    public void updateTime(int i, Time time) throws SQLException {
        getResultSet().updateTime(i, time);
    }

    @Override
    public void updateTimestamp(int i, Timestamp timestamp) throws SQLException {
        getResultSet().updateTimestamp(i, timestamp);
    }

    @Override
    public void updateAsciiStream(int i, InputStream inputStream, int i1) throws SQLException {
        getResultSet().updateAsciiStream(i, inputStream, i1);
    }

    @Override
    public void updateBinaryStream(int i, InputStream inputStream, int i1) throws SQLException {
        getResultSet().updateBinaryStream(i, inputStream, i1);
    }

    @Override
    public void updateCharacterStream(int i, Reader reader, int i1) throws SQLException {
        getResultSet().updateCharacterStream(i, reader, i1);
    }

    @Override
    public void updateObject(int i, Object o, int i1) throws SQLException {
        getResultSet().updateObject(i, o, i1);
    }

    @Override
    public void updateObject(int i, Object o) throws SQLException {
        getResultSet().updateObject(i, o);
    }

    @Override
    public void updateNull(String s) throws SQLException {
        getResultSet().updateNull(s);
    }

    @Override
    public void updateBoolean(String s, boolean b) throws SQLException {
        getResultSet().updateBoolean(s, b);
    }

    @Override
    public void updateByte(String s, byte b) throws SQLException {
        getResultSet().updateByte(s, b);
    }

    @Override
    public void updateShort(String s, short i) throws SQLException {
        getResultSet().updateShort(s, i);
    }

    @Override
    public void updateInt(String s, int i) throws SQLException {
        getResultSet().updateInt(s, i);
    }

    @Override
    public void updateLong(String s, long l) throws SQLException {
        getResultSet().updateLong(s, l);
    }

    @Override
    public void updateFloat(String s, float v) throws SQLException {
        getResultSet().updateFloat(s, v);
    }

    @Override
    public void updateDouble(String s, double v) throws SQLException {
        getResultSet().updateDouble(s, v);
    }

    @Override
    public void updateBigDecimal(String s, BigDecimal bigDecimal) throws SQLException {
        getResultSet().updateBigDecimal(s, bigDecimal);
    }

    @Override
    public void updateString(String s, String s1) throws SQLException {
        getResultSet().updateString(s, s1);
    }

    @Override
    public void updateBytes(String s, byte[] bytes) throws SQLException {
        getResultSet().updateBytes(s, bytes);
    }

    @Override
    public void updateDate(String s, Date date) throws SQLException {
        getResultSet().updateDate(s, date);
    }

    @Override
    public void updateTime(String s, Time time) throws SQLException {
        getResultSet().updateTime(s, time);
    }

    @Override
    public void updateTimestamp(String s, Timestamp timestamp) throws SQLException {
        getResultSet().updateTimestamp(s, timestamp);
    }

    @Override
    public void updateAsciiStream(String s, InputStream inputStream, int i) throws SQLException {
        getResultSet().updateAsciiStream(s, inputStream);
    }

    @Override
    public void updateBinaryStream(String s, InputStream inputStream, int i) throws SQLException {
        getResultSet().updateBinaryStream(s, inputStream);
    }

    @Override
    public void updateCharacterStream(String s, Reader reader, int i) throws SQLException {
        getResultSet().updateCharacterStream(s, reader);
    }

    @Override
    public void updateObject(String s, Object o, int i) throws SQLException {
        getResultSet().updateObject(s, o, i);
    }

    @Override
    public void updateObject(String s, Object o) throws SQLException {
        getResultSet().updateObject(s, o);
    }

    @Override
    public void insertRow() throws SQLException {
        getResultSet().insertRow();
    }

    @Override
    public void updateRow() throws SQLException {
        getResultSet().updateRow();
    }

    @Override
    public void deleteRow() throws SQLException {
        getResultSet().deleteRow();
    }

    @Override
    public void refreshRow() throws SQLException {
        getResultSet().refreshRow();
    }

    @Override
    public void cancelRowUpdates() throws SQLException {
        getResultSet().cancelRowUpdates();
    }

    @Override
    public void moveToInsertRow() throws SQLException {
        getResultSet().moveToInsertRow();
    }

    @Override
    public void moveToCurrentRow() throws SQLException {
        getResultSet().moveToCurrentRow();
    }

    @Override
    public Statement getStatement() throws SQLException {
        return getResultSet().getStatement();
    }

    @Override
    public Object getObject(int i, Map<String, Class<?>> map) throws SQLException {
        return getResultSet().getObject(i, map);
    }

    @Override
    public Ref getRef(int i) throws SQLException {
        return getResultSet().getRef(i);
    }

    @Override
    public Blob getBlob(int i) throws SQLException {
        return getResultSet().getBlob(i);
    }

    @Override
    public Clob getClob(int i) throws SQLException {
        return getResultSet().getClob(i);
    }

    @Override
    public Array getArray(int i) throws SQLException {
        return getResultSet().getArray(i);
    }

    @Override
    public Object getObject(String s, Map<String, Class<?>> map) throws SQLException {
        return getResultSet().getObject(s, map);
    }

    @Override
    public Ref getRef(String s) throws SQLException {
        return getResultSet().getRef(s);
    }

    @Override
    public Blob getBlob(String s) throws SQLException {
        return getResultSet().getBlob(s);
    }

    @Override
    public Clob getClob(String s) throws SQLException {
        return getResultSet().getClob(s);
    }

    @Override
    public Array getArray(String s) throws SQLException {
        return getResultSet().getArray(s);
    }

    @Override
    public Date getDate(int i, Calendar calendar) throws SQLException {
        return getResultSet().getDate(i, calendar);
    }

    @Override
    public Date getDate(String s, Calendar calendar) throws SQLException {
        return getResultSet().getDate(s, calendar);
    }

    @Override
    public Time getTime(int i, Calendar calendar) throws SQLException {
        return getResultSet().getTime(i, calendar);
    }

    @Override
    public Time getTime(String s, Calendar calendar) throws SQLException {
        return getResultSet().getTime(s, calendar);
    }

    @Override
    public Timestamp getTimestamp(int i, Calendar calendar) throws SQLException {
        return getResultSet().getTimestamp(i, calendar);
    }

    @Override
    public Timestamp getTimestamp(String s, Calendar calendar) throws SQLException {
        return getResultSet().getTimestamp(s, calendar);
    }

    @Override
    public URL getURL(int i) throws SQLException {
        return getResultSet().getURL(i);
    }

    @Override
    public URL getURL(String s) throws SQLException {
        return getResultSet().getURL(s);
    }

    @Override
    public void updateRef(int i, Ref ref) throws SQLException {
        getResultSet().updateRef(i, ref);
    }

    @Override
    public void updateRef(String s, Ref ref) throws SQLException {
        getResultSet().updateRef(s, ref);
    }

    @Override
    public void updateBlob(int i, Blob blob) throws SQLException {
        getResultSet().updateBlob(i, blob);
    }

    @Override
    public void updateBlob(String s, Blob blob) throws SQLException {
        getResultSet().updateBlob(s, blob);
    }

    @Override
    public void updateClob(int i, Clob clob) throws SQLException {
        getResultSet().updateClob(i, clob);
    }

    @Override
    public void updateClob(String s, Clob clob) throws SQLException {
        getResultSet().updateClob(s, clob);
    }

    @Override
    public void updateArray(int i, Array array) throws SQLException {
        getResultSet().updateArray(i, array);
    }

    @Override
    public void updateArray(String s, Array array) throws SQLException {
        getResultSet().updateArray(s, array);
    }

    @Override
    public RowId getRowId(int i) throws SQLException {
        return getResultSet().getRowId(i);
    }

    @Override
    public RowId getRowId(String s) throws SQLException {
        return getResultSet().getRowId(s);
    }

    @Override
    public void updateRowId(int i, RowId rowId) throws SQLException {
        getResultSet().updateRowId(i, rowId);
    }

    @Override
    public void updateRowId(String s, RowId rowId) throws SQLException {
        getResultSet().updateRowId(s, rowId);
    }

    @Override
    public int getHoldability() throws SQLException {
        return getResultSet().getHoldability();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return getResultSet().isClosed();
    }

    @Override
    public void updateNString(int i, String s) throws SQLException {
        getResultSet().updateNString(i, s);
    }

    @Override
    public void updateNString(String s, String s1) throws SQLException {
        getResultSet().updateNString(s, s1);
    }

    @Override
    public void updateNClob(int i, NClob nClob) throws SQLException {
        getResultSet().updateNClob(i, nClob);
    }

    @Override
    public void updateNClob(String s, NClob nClob) throws SQLException {
        getResultSet().updateNClob(s, nClob);
    }

    @Override
    public NClob getNClob(int i) throws SQLException {
        return getResultSet().getNClob(i);
    }

    @Override
    public NClob getNClob(String s) throws SQLException {
        return getResultSet().getNClob(s);
    }

    @Override
    public SQLXML getSQLXML(int i) throws SQLException {
        return getResultSet().getSQLXML(i);
    }

    @Override
    public SQLXML getSQLXML(String s) throws SQLException {
        return getResultSet().getSQLXML(s);
    }

    @Override
    public void updateSQLXML(int i, SQLXML sqlxml) throws SQLException {
        getResultSet().updateSQLXML(i, sqlxml);
    }

    @Override
    public void updateSQLXML(String s, SQLXML sqlxml) throws SQLException {
        getResultSet().updateSQLXML(s, sqlxml);
    }

    @Override
    public String getNString(int i) throws SQLException {
        return getResultSet().getNString(i);
    }

    @Override
    public String getNString(String s) throws SQLException {
        return getResultSet().getNString(s);
    }

    @Override
    public Reader getNCharacterStream(int i) throws SQLException {
        return getResultSet().getNCharacterStream(i);
    }

    @Override
    public Reader getNCharacterStream(String s) throws SQLException {
        return getResultSet().getNCharacterStream(s);
    }

    @Override
    public void updateNCharacterStream(int i, Reader reader, long l) throws SQLException {
        getResultSet().updateNCharacterStream(i, reader, l);
    }

    @Override
    public void updateNCharacterStream(String s, Reader reader, long l) throws SQLException {
        getResultSet().updateNCharacterStream(s, reader, l);
    }

    @Override
    public void updateAsciiStream(int i, InputStream inputStream, long l) throws SQLException {
        getResultSet().updateAsciiStream(i, inputStream, l);
    }

    @Override
    public void updateBinaryStream(int i, InputStream inputStream, long l) throws SQLException {
        getResultSet().updateAsciiStream(i, inputStream, l);
    }

    @Override
    public void updateCharacterStream(int i, Reader reader, long l) throws SQLException {
        getResultSet().updateCharacterStream(i, reader, l);
    }

    @Override
    public void updateAsciiStream(String s, InputStream inputStream, long l) throws SQLException {
        getResultSet().updateAsciiStream(s, inputStream, l);
    }

    @Override
    public void updateBinaryStream(String s, InputStream inputStream, long l) throws SQLException {
        getResultSet().updateBinaryStream(s, inputStream, l);
    }

    @Override
    public void updateCharacterStream(String s, Reader reader, long l) throws SQLException {
        getResultSet().updateCharacterStream(s, reader, l);
    }

    @Override
    public void updateBlob(int i, InputStream inputStream, long l) throws SQLException {
        getResultSet().updateBlob(i, inputStream, l);
    }

    @Override
    public void updateBlob(String s, InputStream inputStream, long l) throws SQLException {
        getResultSet().updateBlob(s, inputStream, l);
    }

    @Override
    public void updateClob(int i, Reader reader, long l) throws SQLException {
        getResultSet().updateClob(i, reader, l);
    }

    @Override
    public void updateClob(String s, Reader reader, long l) throws SQLException {
        getResultSet().updateClob(s, reader, l);
    }

    @Override
    public void updateNClob(int i, Reader reader, long l) throws SQLException {
        getResultSet().updateNClob(i, reader, l);
    }

    @Override
    public void updateNClob(String s, Reader reader, long l) throws SQLException {
        getResultSet().updateNClob(s, reader, l);
    }

    @Override
    public void updateNCharacterStream(int i, Reader reader) throws SQLException {
        getResultSet().updateNCharacterStream(i, reader);
    }

    @Override
    public void updateNCharacterStream(String s, Reader reader) throws SQLException {
        getResultSet().updateNCharacterStream(s, reader);
    }

    @Override
    public void updateAsciiStream(int i, InputStream inputStream) throws SQLException {
        getResultSet().updateAsciiStream(i, inputStream);
    }

    @Override
    public void updateBinaryStream(int i, InputStream inputStream) throws SQLException {
        getResultSet().updateBinaryStream(i, inputStream);
    }

    @Override
    public void updateCharacterStream(int i, Reader reader) throws SQLException {
        getResultSet().updateCharacterStream(i, reader);
    }

    @Override
    public void updateAsciiStream(String s, InputStream inputStream) throws SQLException {
        getResultSet().updateAsciiStream(s, inputStream);
    }

    @Override
    public void updateBinaryStream(String s, InputStream inputStream) throws SQLException {
        getResultSet().updateBinaryStream(s, inputStream);
    }

    @Override
    public void updateCharacterStream(String s, Reader reader) throws SQLException {
        getResultSet().updateCharacterStream(s, reader);
    }

    @Override
    public void updateBlob(int i, InputStream inputStream) throws SQLException {
        getResultSet().updateBlob(i, inputStream);
    }

    @Override
    public void updateBlob(String s, InputStream inputStream) throws SQLException {
        getResultSet().updateBlob(s, inputStream);
    }

    @Override
    public void updateClob(int i, Reader reader) throws SQLException {
        getResultSet().updateClob(i, reader);
    }

    @Override
    public void updateClob(String s, Reader reader) throws SQLException {
        getResultSet().updateClob(s, reader);
    }

    @Override
    public void updateNClob(int i, Reader reader) throws SQLException {
        getResultSet().updateNClob(i, reader);
    }

    @Override
    public void updateNClob(String s, Reader reader) throws SQLException {
        getResultSet().updateNClob(s, reader);
    }

    @Override
    public <T> T getObject(int i, Class<T> aClass) throws SQLException {
        return getResultSet().getObject(i, aClass);
    }

    @Override
    public <T> T getObject(String s, Class<T> aClass) throws SQLException {
        return getResultSet().getObject(s, aClass);
    }

    @Override
    public <T> T unwrap(Class<T> aClass) throws SQLException {
        return getResultSet().unwrap(aClass);
    }

    @Override
    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        return getResultSet().isWrapperFor(aClass);
    }
}
