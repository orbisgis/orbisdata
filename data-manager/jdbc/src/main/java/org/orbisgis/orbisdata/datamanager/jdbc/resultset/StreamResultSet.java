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
package org.orbisgis.orbisdata.datamanager.jdbc.resultset;

import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.commons.annotations.Nullable;
import org.orbisgis.commons.utilities.CheckUtils;
import org.orbisgis.orbisdata.datamanager.api.dataset.ITable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.Map;

/**
 * Wrapper of {@link ResultSet} used to simplified the usage of {@link ITable#stream()}, avoiding the usage of
 * try/catch.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC)
 */
public class StreamResultSet implements ResultSet {

    /**
     * Logger used for exception logging.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(StreamResultSet.class);

    /**
     * Internal {@link ResultSet}.
     */
    private final ResultSet resultSet;
    
    public StreamResultSet(@NotNull ResultSet resultSet){
        CheckUtils.checkNotNull(resultSet, "The given ResultSet should not be null.");
        this.resultSet = resultSet;
    }
    
    @Override
    public boolean next() {
        try {
            return resultSet.next();
        } catch (SQLException e) {
            LOGGER.error("Unable to call next() method on wrapped ResultSet.", e);
            return false;
        }
    }

    @Override
    public void close() {
        try {
            resultSet.close();
        } catch (SQLException e) {
            LOGGER.error("Unable to call close() method on wrapped ResultSet.", e);
        }
    }

    @Override
    public boolean wasNull() {
        try {
            return resultSet.wasNull();
        } catch (SQLException e) {
            LOGGER.error("Unable to call wasNull() method on wrapped ResultSet.", e);
            return false;
        }
    }

    @Override
    public String getString(int i) {
        try {
            return resultSet.getString(i);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getString(int) method on wrapped ResultSet.", e);
            return "";
        }
    }

    @Override
    public boolean getBoolean(int i) {
        try {
            return resultSet.getBoolean(i);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getBoolean(int) method on wrapped ResultSet.", e);
            return false;
        }
    }

    @Override
    public byte getByte(int i) {
        try {
            return resultSet.getByte(i);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getByte(int) method on wrapped ResultSet.", e);
            return 0;
        }
    }

    @Override
    public short getShort(int i) {
        try {
            return resultSet.getShort(i);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getShort(int) method on wrapped ResultSet.", e);
            return 0;
        }
    }

    @Override
    public int getInt(int i) {
        try {
            return resultSet.getInt(i);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getInt(int) method on wrapped ResultSet.", e);
            return 0;
        }
    }

    @Override
    public long getLong(int i) {
        try {
            return resultSet.getLong(i);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getLong(int) method on wrapped ResultSet.", e);
            return 0;
        }
    }

    @Override
    public float getFloat(int i) {
        try {
            return resultSet.getFloat(i);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getFloat(int) method on wrapped ResultSet.", e);
            return 0;
        }
    }

    @Override
    public double getDouble(int i) {
        try {
            return resultSet.getDouble(i);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getDouble(int) method on wrapped ResultSet.", e);
            return 0;
        }
    }

    @Override
    public BigDecimal getBigDecimal(int i, int i1) {
        try {
            return resultSet.getBigDecimal(i);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getBigDecimal(int, int) method on wrapped ResultSet.", e);
            return new BigDecimal("0");
        }
    }

    @Override
    public byte[] getBytes(int i) {
        try {
            return resultSet.getBytes(i);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getBytes(int) method on wrapped ResultSet.", e);
            return new byte[0];
        }
    }

    @Override
    @Nullable
    public Date getDate(int i) {
        try {
            return resultSet.getDate(i);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getDate(int) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    @Nullable
    public Time getTime(int i) {
        try {
            return resultSet.getTime(i);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getTime(int) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    @Nullable
    public Timestamp getTimestamp(int i) {
        try {
            return resultSet.getTimestamp(i);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getTimestamp(int) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    @Nullable
    public InputStream getAsciiStream(int i) {
        try {
            return resultSet.getAsciiStream(i);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getAsciiStream(int) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    @Nullable
    @Deprecated
    public InputStream getUnicodeStream(int i) {
        try {
            return resultSet.getUnicodeStream(i);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getUnicodeStream(int) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    @Nullable
    public InputStream getBinaryStream(int i) {
        try {
            return resultSet.getBinaryStream(i);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getBinaryStream(int) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    public String getString(String s) {
        try {
            return resultSet.getString(s);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getString(String) method on wrapped ResultSet.", e);
            return "";
        }
    }

    @Override
    public boolean getBoolean(String s) {
        try {
            return resultSet.getBoolean(s);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getBoolean(String) method on wrapped ResultSet.", e);
            return false;
        }
    }

    @Override
    public byte getByte(String s) {
        try {
            return resultSet.getByte(s);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getByte(String) method on wrapped ResultSet.", e);
            return 0;
        }
    }

    @Override
    public short getShort(String s) {
        try {
            return resultSet.getShort(s);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getShort(String) method on wrapped ResultSet.", e);
            return 0;
        }
    }

    @Override
    public int getInt(String s) {
        try {
            return resultSet.getInt(s);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getInt(String) method on wrapped ResultSet.", e);
            return 0;
        }
    }

    @Override
    public long getLong(String s) {
        try {
            return resultSet.getLong(s);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getLong(String) method on wrapped ResultSet.", e);
            return 0;
        }
    }

    @Override
    public float getFloat(String s) {
        try {
            return resultSet.getFloat(s);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getFloat(String) method on wrapped ResultSet.", e);
            return 0;
        }
    }

    @Override
    public double getDouble(String s) {
        try {
            return resultSet.getDouble(s);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getDouble(String) method on wrapped ResultSet.", e);
            return 0;
        }
    }

    @Override
    public BigDecimal getBigDecimal(String s, int i) {
        try {
            return resultSet.getBigDecimal(s);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getBigDecimal(String) method on wrapped ResultSet.", e);
            return new BigDecimal("0");
        }
    }

    @Override
    public byte[] getBytes(String s) {
        try {
            return resultSet.getBytes(s);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getBytes(String) method on wrapped ResultSet.", e);
            return new byte[0];
        }
    }

    @Override
    @Nullable
    public Date getDate(String s) {
        try {
            return resultSet.getDate(s);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getDate(String) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    @Nullable
    public Time getTime(String s) {
        try {
            return resultSet.getTime(s);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getTime(String) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    @Nullable
    public Timestamp getTimestamp(String s) {
        try {
            return resultSet.getTimestamp(s);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getTimestamp(String) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    @Nullable
    public InputStream getAsciiStream(String s) {
        try {
            return resultSet.getAsciiStream(s);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getAsciiStream(String) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    @Nullable
    @Deprecated
    public InputStream getUnicodeStream(String s) {
        try {
            return resultSet.getUnicodeStream(s);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getUnicodeStream(String) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    @Nullable
    public InputStream getBinaryStream(String s) {
        try {
            return resultSet.getBinaryStream(s);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getBinaryStream(String) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    @Nullable
    public SQLWarning getWarnings() {
        try {
            return resultSet.getWarnings();
        } catch (SQLException e) {
            LOGGER.error("Unable to call getWarnings() method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    public void clearWarnings() {
        try {
            resultSet.clearWarnings();
        } catch (SQLException e) {
            LOGGER.error("Unable to call clearWarnings() method on wrapped ResultSet.", e);
        }
    }

    @Override
    public String getCursorName() {
        try {
            return resultSet.getCursorName();
        } catch (SQLException e) {
            LOGGER.error("Unable to call getCursorName() method on wrapped ResultSet.", e);
            return "";
        }
    }

    @Override
    @Nullable
    public ResultSetMetaData getMetaData() {
        try {
            return resultSet.getMetaData();
        } catch (SQLException e) {
            LOGGER.error("Unable to call getMetaData() method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    @Nullable
    public Object getObject(int i) {
        try {
            return resultSet.getObject(i);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getObject(int) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    @Nullable
    public Object getObject(String s) {
        try {
            return resultSet.getObject(s);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getObject(String) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    public int findColumn(String s) {
        try {
            return resultSet.findColumn(s);
        } catch (SQLException e) {
            LOGGER.error("Unable to call findColumn(String) method on wrapped ResultSet.", e);
            return 0;
        }
    }

    @Override
    @Nullable
    public Reader getCharacterStream(int i) {
        try {
            return resultSet.getCharacterStream(i);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getObject(int) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    @Nullable
    public Reader getCharacterStream(String s) {
        try {
            return resultSet.getCharacterStream(s);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getCharacterStream(String) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    public BigDecimal getBigDecimal(int i) {
        try {
            return resultSet.getBigDecimal(i);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getBigDecimal(int) method on wrapped ResultSet.", e);
            return new BigDecimal("0");
        }
    }

    @Override
    public BigDecimal getBigDecimal(String s) {
        try {
            return resultSet.getBigDecimal(s);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getBigDecimal(String) method on wrapped ResultSet.", e);
            return new BigDecimal("0");
        }
    }

    @Override
    public boolean isBeforeFirst() {
        try {
            return resultSet.isBeforeFirst();
        } catch (SQLException e) {
            LOGGER.error("Unable to call isBeforeFirst() method on wrapped ResultSet.", e);
            return false;
        }
    }

    @Override
    public boolean isAfterLast() {
        try {
            return resultSet.isAfterLast();
        } catch (SQLException e) {
            LOGGER.error("Unable to call isAfterLast() method on wrapped ResultSet.", e);
            return false;
        }
    }

    @Override
    public boolean isFirst() {
        try {
            return resultSet.isFirst();
        } catch (SQLException e) {
            LOGGER.error("Unable to call isFirst() method on wrapped ResultSet.", e);
            return false;
        }
    }

    @Override
    public boolean isLast() {
        try {
            return resultSet.isLast();
        } catch (SQLException e) {
            LOGGER.error("Unable to call isLast() method on wrapped ResultSet.", e);
            return false;
        }
    }

    @Override
    public void beforeFirst() {
        try {
            resultSet.beforeFirst();
        } catch (SQLException e) {
            LOGGER.error("Unable to call beforeFirst() method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void afterLast() {
        try {
            resultSet.afterLast();
        } catch (SQLException e) {
            LOGGER.error("Unable to call afterLast() method on wrapped ResultSet.", e);
        }
    }

    @Override
    public boolean first() {
        try {
            return resultSet.first();
        } catch (SQLException e) {
            LOGGER.error("Unable to call first() method on wrapped ResultSet.", e);
            return false;
        }
    }

    @Override
    public boolean last() {
        try {
            return resultSet.isAfterLast();
        } catch (SQLException e) {
            LOGGER.error("Unable to call isAfterLast() method on wrapped ResultSet.", e);
            return false;
        }
    }

    @Override
    public int getRow() {
        try {
            return resultSet.getRow();
        } catch (SQLException e) {
            LOGGER.error("Unable to call getRow() method on wrapped ResultSet.", e);
            return -2;
        }
    }

    @Override
    public boolean absolute(int i) {
        try {
            return resultSet.absolute(i);
        } catch (SQLException e) {
            LOGGER.error("Unable to call absolute(i) method on wrapped ResultSet.", e);
            return false;
        }
    }

    @Override
    public boolean relative(int i) {
        try {
            return resultSet.relative(i);
        } catch (SQLException e) {
            LOGGER.error("Unable to call relative(i) method on wrapped ResultSet.", e);
            return false;
        }
    }

    @Override
    public boolean previous() {
        try {
            return resultSet.previous();
        } catch (SQLException e) {
            LOGGER.error("Unable to call previous() method on wrapped ResultSet.", e);
            return false;
        }
    }

    @Override
    public void setFetchDirection(int i) {
        try {
            resultSet.setFetchDirection(i);
        } catch (SQLException e) {
            LOGGER.error("Unable to call setFetchDirection(int) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public int getFetchDirection() {
        try {
            return resultSet.getFetchDirection();
        } catch (SQLException e) {
            LOGGER.error("Unable to call getFetchDirection() method on wrapped ResultSet.", e);
            return ResultSet.FETCH_UNKNOWN;
        }
    }

    @Override
    public void setFetchSize(int i) {
        try {
            resultSet.setFetchSize(i);
        } catch (SQLException e) {
            LOGGER.error("Unable to call setFetchSize(int) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public int getFetchSize() {
        try {
            return resultSet.getFetchSize();
        } catch (SQLException e) {
            LOGGER.error("Unable to call getFetchSize() method on wrapped ResultSet.", e);
        }
        return -1;
    }

    @Override
    public int getType() {
        try {
            return resultSet.getType();
        } catch (SQLException e) {
            LOGGER.error("Unable to call getType() method on wrapped ResultSet.", e);
        }
        return ResultSet.TYPE_FORWARD_ONLY;
    }

    @Override
    public int getConcurrency() {
        try {
            return resultSet.getConcurrency();
        } catch (SQLException e) {
            LOGGER.error("Unable to call getConcurrency() method on wrapped ResultSet.", e);
        }
        return ResultSet.CONCUR_READ_ONLY;
    }

    @Override
    public boolean rowUpdated() {
        try {
            return resultSet.rowUpdated();
        } catch (SQLException e) {
            LOGGER.error("Unable to call rowUpdated() method on wrapped ResultSet.", e);
        }
        return false;
    }

    @Override
    public boolean rowInserted() {
        try {
            return resultSet.rowInserted();
        } catch (SQLException e) {
            LOGGER.error("Unable to call rowInserted() method on wrapped ResultSet.", e);
        }
        return false;
    }

    @Override
    public boolean rowDeleted() {
        try {
            return resultSet.rowDeleted();
        } catch (SQLException e) {
            LOGGER.error("Unable to call rowDeleted() method on wrapped ResultSet.", e);
        }
        return false;
    }

    @Override
    public void updateNull(int i) {
        try {
            resultSet.updateNull(i);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateNull(int) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateBoolean(int i, boolean b) {
        try {
            resultSet.updateBoolean(i, b);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateBoolean(int, boolean) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateByte(int i, byte b) {
        try {
            resultSet.updateByte(i, b);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateByte(int, byte) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateShort(int i, short i1) {
        try {
            resultSet.updateShort(i, i1);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateShort(int, short) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateInt(int i, int i1) {
        try {
            resultSet.updateInt(i, i1);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateInt(int, int) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateLong(int i, long l) {
        try {
            resultSet.updateLong(i, l);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateLong(int, long) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateFloat(int i, float v) {
        try {
            resultSet.updateFloat(i, v);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateFloat(int, float) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateDouble(int i, double v) {
        try {
            resultSet.updateDouble(i, v);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateDouble(int, double) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateBigDecimal(int i, BigDecimal bigDecimal) {
        try {
            resultSet.updateBigDecimal(i, bigDecimal);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateBigDecimal(int, BigDecimal) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateString(int i, String s) {
        try {
            resultSet.updateString(i, s);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateString(int, String) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateBytes(int i, byte[] bytes) {
        try {
            resultSet.updateBytes(i, bytes);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateBytes(int, byte[]) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateDate(int i, Date date) {
        try {
            resultSet.updateDate(i, date);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateDate(int, Date) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateTime(int i, Time time) {
        try {
            resultSet.updateTime(i, time);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateTime(int, Time) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateTimestamp(int i, Timestamp timestamp) {
        try {
            resultSet.updateTimestamp(i, timestamp);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateTimestamp(int, Timestamp) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateAsciiStream(int i, InputStream inputStream, int i1) {
        try {
            resultSet.updateAsciiStream(i, inputStream, i1);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateAsciiStream(int, InputStream, int) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateBinaryStream(int i, InputStream inputStream, int i1) {
        try {
            resultSet.updateBinaryStream(i, inputStream, i1);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateBinaryStream(int, InputStream, int) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateCharacterStream(int i, Reader reader, int i1) {
        try {
            resultSet.updateCharacterStream(i, reader, i1);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateCharacterStream(int, Reader, int) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateObject(int i, Object o, int i1) {
        try {
            resultSet.updateObject(i, o, i1);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateObject(int, Object, int) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateObject(int i, Object o) {
        try {
            resultSet.updateObject(i, o);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateObject(int, Object) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateNull(String s) {
        try {
            resultSet.updateNull(s);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateNull(String) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateBoolean(String s, boolean b) {
        try {
            resultSet.updateBoolean(s, b);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateBoolean(String, boolean) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateByte(String s, byte b) {
        try {
            resultSet.updateByte(s, b);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateByte(String, byte) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateShort(String s, short i) {
        try {
            resultSet.updateShort(s, i);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateShort(String, short) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateInt(String s, int i) {
        try {
            resultSet.updateInt(s, i);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateInt(String, int) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateLong(String s, long l) {
        try {
            resultSet.updateLong(s, l);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateLong(String, long) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateFloat(String s, float v) {
        try {
            resultSet.updateFloat(s, v);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateFloat(String, float) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateDouble(String s, double v) {
        try {
            resultSet.updateDouble(s, v);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateDouble(String, double) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateBigDecimal(String s, BigDecimal bigDecimal) {
        try {
            resultSet.updateBigDecimal(s, bigDecimal);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateBigDecimal(String, BigDecimal) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateString(String s, String s1) {
        try {
            resultSet.updateString(s, s1);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateString(String, String) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateBytes(String s, byte[] bytes) {
        try {
            resultSet.updateBytes(s, bytes);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateBytes(String, byte[]) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateDate(String s, Date date) {
        try {
            resultSet.updateDate(s, date);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateDate(String, Date) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateTime(String s, Time time) {
        try {
            resultSet.updateTime(s, time);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateTime(String, Time) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateTimestamp(String s, Timestamp timestamp) {
        try {
            resultSet.updateTimestamp(s, timestamp);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateTimestamp(String, Timestamp) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateAsciiStream(String s, InputStream inputStream, int i) {
        try {
            resultSet.updateAsciiStream(s, inputStream, i);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateAsciiStream(String, InputStream, int) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateBinaryStream(String s, InputStream inputStream, int i) {
        try {
            resultSet.updateBinaryStream(s, inputStream, i);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateBinaryStream(String, InputStream, int) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateCharacterStream(String s, Reader reader, int i) {
        try {
            resultSet.updateCharacterStream(s, reader, i);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateCharacterStream(String, Reader, int) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateObject(String s, Object o, int i) {
        try {
            resultSet.updateObject(s, o, i);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateObject(String, Object, int) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateObject(String s, Object o) {
        try {
            resultSet.updateObject(s, o);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateObject(String, Object) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void insertRow() {
        try {
            resultSet.insertRow();
        } catch (SQLException e) {
            LOGGER.error("Unable to call insertRow() method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateRow() {
        try {
            resultSet.updateRow();
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateRow() method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void deleteRow() {
        try {
            resultSet.deleteRow();
        } catch (SQLException e) {
            LOGGER.error("Unable to call deleteRow() method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void refreshRow() {
        try {
            resultSet.refreshRow();
        } catch (SQLException e) {
            LOGGER.error("Unable to call refreshRow() method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void cancelRowUpdates() {
        try {
            resultSet.cancelRowUpdates();
        } catch (SQLException e) {
            LOGGER.error("Unable to call cancelRowUpdates() method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void moveToInsertRow() {
        try {
            resultSet.moveToInsertRow();
        } catch (SQLException e) {
            LOGGER.error("Unable to call moveToInsertRow() method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void moveToCurrentRow() {
        try {
            resultSet.moveToCurrentRow();
        } catch (SQLException e) {
            LOGGER.error("Unable to call moveToCurrentRow() method on wrapped ResultSet.", e);
        }
    }

    @Override
    @Nullable
    public Statement getStatement() {
        try {
            return resultSet.getStatement();
        } catch (SQLException e) {
            LOGGER.error("Unable to call getStatement() method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    @Nullable
    public Object getObject(int i, Map<String, Class<?>> map) {
        try {
            return resultSet.getObject(i, map);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getObject(int, Map<String, CLass<?>>) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    @Nullable
    public Ref getRef(int i) {
        try {
            return resultSet.getRef(i);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getRef(int) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    @Nullable
    public Blob getBlob(int i) {
        try {
            return resultSet.getBlob(i);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getBlob(int) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    @Nullable
    public Clob getClob(int i) {
        try {
            return resultSet.getClob(i);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getClob(int) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    @Nullable
    public Array getArray(int i) {
        try {
            return resultSet.getArray(i);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getArray(int) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    @Nullable
    public Object getObject(String s, Map<String, Class<?>> map) {
        try {
            return resultSet.getObject(s, map);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getObject(String, Map<String, Class<?>>) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    @Nullable
    public Ref getRef(String s) {
        try {
            return resultSet.getRef(s);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getRef(String) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    @Nullable
    public Blob getBlob(String s) {
        try {
            return resultSet.getBlob(s);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getBlob(String) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    @Nullable
    public Clob getClob(String s) {
        try {
            return resultSet.getClob(s);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getClob(String) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    @Nullable
    public Array getArray(String s) {
        try {
            return resultSet.getArray(s);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getArray(String) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    @Nullable
    public Date getDate(int i, Calendar calendar) {
        try {
            return resultSet.getDate(i, calendar);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getDate(int, Calendar) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    @Nullable
    public Date getDate(String s, Calendar calendar) {
        try {
            return resultSet.getDate(s, calendar);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getDate(String, Calendar) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    @Nullable
    public Time getTime(int i, Calendar calendar) {
        try {
            return resultSet.getTime(i, calendar);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getTime(int, Calendar) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    @Nullable
    public Time getTime(String s, Calendar calendar) {
        try {
            return resultSet.getTime(s, calendar);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getTime(String, Calendar) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    @Nullable
    public Timestamp getTimestamp(int i, Calendar calendar) {
        try {
            return resultSet.getTimestamp(i, calendar);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getTimestamp(int, Calendar) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    @Nullable
    public Timestamp getTimestamp(String s, Calendar calendar) {
        try {
            return resultSet.getTimestamp(s, calendar);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getTimestamp(String, Calendar) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    @Nullable
    public URL getURL(int i) {
        try {
            return resultSet.getURL(i);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getURL(int) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    @Nullable
    public URL getURL(String s) {
        try {
            return resultSet.getURL(s);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getURL(String) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    public void updateRef(int i, Ref ref) {
        try {
            resultSet.updateRef(i, ref);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateRef(int, Ref) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateRef(String s, Ref ref) {
        try {
            resultSet.updateRef(s, ref);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateRef(String, Ref) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateBlob(int i, Blob blob) {
        try {
            resultSet.updateBlob(i, blob);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateBlob(int, Blob) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateBlob(String s, Blob blob) {
        try {
            resultSet.updateBlob(s, blob);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateRef(int, Blob) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateClob(int i, Clob clob) {
        try {
            resultSet.updateClob(i, clob);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateRef(int, Clob) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateClob(String s, Clob clob) {
        try {
            resultSet.updateClob(s, clob);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateClob(String, Clob) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateArray(int i, Array array) {
        try {
            resultSet.updateArray(i, array);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateArray(int, Array) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateArray(String s, Array array) {
        try {
            resultSet.updateArray(s, array);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateArray(int, Array) method on wrapped ResultSet.", e);
        }
    }

    @Override
    @Nullable
    public RowId getRowId(int i) {
        try {
            return resultSet.getRowId(i);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getRowId(int) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    @Nullable
    public RowId getRowId(String s) {
        try {
            return resultSet.getRowId(s);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getRowId(String) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    public void updateRowId(int i, RowId rowId) {
        try {
            resultSet.updateRowId(i, rowId);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateRowId(int, RowId) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateRowId(String s, RowId rowId) {
        try {
            resultSet.updateRowId(s, rowId);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateRowId(String, RowId) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public int getHoldability() {
        try {
            return resultSet.getHoldability();
        } catch (SQLException e) {
            LOGGER.error("Unable to call getHoldability() method on wrapped ResultSet.", e);
            return ResultSet.HOLD_CURSORS_OVER_COMMIT;
        }
    }

    @Override
    public boolean isClosed() {
        try {
            return resultSet.isClosed();
        } catch (SQLException e) {
            LOGGER.error("Unable to call isClosed() method on wrapped ResultSet.", e);
            return true;
        }
    }

    @Override
    public void updateNString(int i, String s) {
        try {
            resultSet.updateNString(i, s);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateNString(int, String) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateNString(String s, String s1) {
        try {
            resultSet.updateNString(s, s1);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateNString(String, String) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateNClob(int i, NClob nClob) {
        try {
            resultSet.updateNClob(i, nClob);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateNClob(int, NClob) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateNClob(String s, NClob nClob) {
        try {
            resultSet.updateNClob(s, nClob);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateNClob(String, NClob) method on wrapped ResultSet.", e);
        }
    }

    @Override
    @Nullable
    public NClob getNClob(int i) {
        try {
            return resultSet.getNClob(i);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getNClob(int) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    @Nullable
    public NClob getNClob(String s) {
        try {
            return resultSet.getNClob(s);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getNClob(String) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    @Nullable
    public SQLXML getSQLXML(int i) {
        try {
            return resultSet.getSQLXML(i);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getSQLXML(int) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    @Nullable
    public SQLXML getSQLXML(String s) {
        try {
            return resultSet.getSQLXML(s);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getSQLXML(String) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    public void updateSQLXML(int i, SQLXML sqlxml) {
        try {
            resultSet.updateSQLXML(i, sqlxml);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateSQLXML(int, SQLXML) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateSQLXML(String s, SQLXML sqlxml) {
        try {
            resultSet.updateSQLXML(s, sqlxml);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateSQLXML(String, SQLXML) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public String getNString(int i) {
        try {
            return resultSet.getNString(i);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getNString(int) method on wrapped ResultSet.", e);
            return "";
        }
    }

    @Override
    public String getNString(String s) {
        try {
            return resultSet.getNString(s);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getNString(String) method on wrapped ResultSet.", e);
            return "";
        }
    }

    @Override
    @Nullable
    public Reader getNCharacterStream(int i) {
        try {
            return resultSet.getNCharacterStream(i);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getNCharacterStream(int) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    @Nullable
    public Reader getNCharacterStream(String s) {
        try {
            return resultSet.getNCharacterStream(s);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getNCharacterStream(String) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    public void updateNCharacterStream(int i, Reader reader, long l) {
        try {
            resultSet.updateNCharacterStream(i, reader, l);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateNCharacterStream(int, Reader, long) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateNCharacterStream(String s, Reader reader, long l) {
        try {
            resultSet.updateNCharacterStream(s, reader, l);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateNCharacterStream(String, Reader, long) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateAsciiStream(int i, InputStream inputStream, long l) {
        try {
            resultSet.updateAsciiStream(i, inputStream, l);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateAsciiStream(int, InputStream, long) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateBinaryStream(int i, InputStream inputStream, long l) {
        try {
            resultSet.updateBinaryStream(i, inputStream, l);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateBinaryStream(int, InputStream, long) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateCharacterStream(int i, Reader reader, long l) {
        try {
            resultSet.updateCharacterStream(i, reader, l);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateCharacterStream(int, Reader, long) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateAsciiStream(String s, InputStream inputStream, long l) {
        try {
            resultSet.updateAsciiStream(s, inputStream, l);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateAsciiStream(String, InputStream, long) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateBinaryStream(String s, InputStream inputStream, long l) {
        try {
            resultSet.updateBinaryStream(s, inputStream, l);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateBinaryStream(String, InputStream, l) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateCharacterStream(String s, Reader reader, long l) {
        try {
            resultSet.updateCharacterStream(s, reader, l);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateCharacterStream(String, Reader, long) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateBlob(int i, InputStream inputStream, long l) {
        try {
            resultSet.updateBlob(i, inputStream, l);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateBlob(int, InputStream, long) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateBlob(String s, InputStream inputStream, long l) {
        try {
            resultSet.updateBlob(s, inputStream, l);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateBlob(int, InputStream, long) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateClob(int i, Reader reader, long l) {
        try {
            resultSet.updateClob(i, reader, l);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateClob(int, Reader, long) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateClob(String s, Reader reader, long l) {
        try {
            resultSet.updateClob(s, reader, l);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateClob(String, Reader, long) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateNClob(int i, Reader reader, long l) {
        try {
            resultSet.updateNClob(i, reader, l);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateNClob(int, Reader, long) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateNClob(String s, Reader reader, long l) {
        try {
            resultSet.updateNClob(s, reader, l);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateNClob(String, InputStream, long) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateNCharacterStream(int i, Reader reader) {
        try {
            resultSet.updateNCharacterStream(i, reader);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateNCharacterStream(int, InputStream) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateNCharacterStream(String s, Reader reader) {
        try {
            resultSet.updateNCharacterStream(s, reader);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateCharacterStream(String, Reader) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateAsciiStream(int i, InputStream inputStream) {
        try {
            resultSet.updateAsciiStream(i, inputStream);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateAsciiStream(int, InputStream) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateBinaryStream(int i, InputStream inputStream) {
        try {
            resultSet.updateBinaryStream(i, inputStream);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateBinaryStream(int, InputStream) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateCharacterStream(int i, Reader reader) {
        try {
            resultSet.updateCharacterStream(i, reader);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateCharacterStream(int, InputStream) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateAsciiStream(String s, InputStream inputStream) {
        try {
            resultSet.updateAsciiStream(s, inputStream);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateAsciiStream(String, InputStream) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateBinaryStream(String s, InputStream inputStream) {
        try {
            resultSet.updateBinaryStream(s, inputStream);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateBinaryStream(String, InputStream) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateCharacterStream(String s, Reader reader) {
        try {
            resultSet.updateCharacterStream(s, reader);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateCharacterStream(int, Reader) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateBlob(int i, InputStream inputStream) {
        try {
            resultSet.updateBlob(i, inputStream);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateBlob(int, InputStream) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateBlob(String s, InputStream inputStream) {
        try {
            resultSet.updateBlob(s, inputStream);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateBlob(String, InputStream) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateClob(int i, Reader reader) {
        try {
            resultSet.updateClob(i, reader);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateClob(int, Reader) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateClob(String s, Reader reader) {
        try {
            resultSet.updateClob(s, reader);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateClob(String, Reader) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateNClob(int i, Reader reader) {
        try {
            resultSet.updateNClob(i, reader);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateNClob(int, Reader) method on wrapped ResultSet.", e);
        }
    }

    @Override
    public void updateNClob(String s, Reader reader) {
        try {
            resultSet.updateNClob(s, reader);
        } catch (SQLException e) {
            LOGGER.error("Unable to call updateNClob(String, Reader) method on wrapped ResultSet.", e);
        }
    }

    @Override
    @Nullable
    public <T> T getObject(int i, Class<T> aClass) {
        try {
            return resultSet.getObject(i, aClass);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getObject(int, Class<T>) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    @Nullable
    public <T> T getObject(String s, Class<T> aClass) {
        try {
            return resultSet.getObject(s, aClass);
        } catch (SQLException e) {
            LOGGER.error("Unable to call getObject(String, Class<T>) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    @Nullable
    public <T> T unwrap(Class<T> aClass) {
        try {
            return resultSet.unwrap(aClass);
        } catch (SQLException e) {
            LOGGER.error("Unable to call unwrap(Class<T>) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    public boolean isWrapperFor(Class<?> aClass) {
        try {
            return resultSet.isWrapperFor(aClass);
        } catch (SQLException e) {
            LOGGER.error("Unable to call isWrapperFor(Class<?>) method on wrapped ResultSet.", e);
            return false;
        }
    }
}
