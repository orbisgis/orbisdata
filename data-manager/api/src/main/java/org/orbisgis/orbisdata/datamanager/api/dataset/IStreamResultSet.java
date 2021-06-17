/*
 * Bundle DataManager API is part of the OrbisGIS platform
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
 * DataManager API is distributed under LGPL 3 license.
 *
 * Copyright (C) 2019-2020 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * DataManager API is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * DataManager API is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * DataManager API. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.orbisdata.datamanager.api.dataset;

import org.orbisgis.commons.annotations.Nullable;
import org.slf4j.Logger;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.Map;

/**
 * Interface for wrapper of {@link ResultSet} used to simplified the usage of {@link ITable#stream()}, avoiding the usage of
 * try/catch.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC)
 */
public interface IStreamResultSet extends ResultSet {

    /**
     * Returns the {@link ResultSet} used for streaming.
     * @return The {@link ResultSet} used for streaming.
     */
    ResultSet getResultSet();

    /**
     * Returns the {@link Logger} used for logging.
     * @return The {@link Logger} used for logging.
     */
    Logger getLogger();
    
    @Override
    default boolean next() {
        try {
            return getResultSet().next();
        } catch (SQLException e) {
            getLogger().error("Unable to call next() method on wrapped ResultSet.", e);
            return false;
        }
    }

    @Override
    default void close() {
        try {
            getResultSet().close();
        } catch (SQLException e) {
            getLogger().error("Unable to call close() method on wrapped ResultSet.", e);
        }
    }

    @Override
    default boolean wasNull() {
        try {
            return getResultSet().wasNull();
        } catch (SQLException e) {
            getLogger().error("Unable to call wasNull() method on wrapped ResultSet.", e);
            return false;
        }
    }

    @Override
    default String getString(int i) {
        try {
            return getResultSet().getString(i);
        } catch (SQLException e) {
            getLogger().error("Unable to call getString(int) method on wrapped ResultSet.", e);
            return "";
        }
    }

    @Override
    default boolean getBoolean(int i) {
        try {
            return getResultSet().getBoolean(i);
        } catch (SQLException e) {
            getLogger().error("Unable to call getBoolean(int) method on wrapped ResultSet.", e);
            return false;
        }
    }

    @Override
    default byte getByte(int i) {
        try {
            return getResultSet().getByte(i);
        } catch (SQLException e) {
            getLogger().error("Unable to call getByte(int) method on wrapped ResultSet.", e);
            return 0;
        }
    }

    @Override
    default short getShort(int i) {
        try {
            return getResultSet().getShort(i);
        } catch (SQLException e) {
            getLogger().error("Unable to call getShort(int) method on wrapped ResultSet.", e);
            return 0;
        }
    }

    @Override
    default int getInt(int i) {
        try {
            return getResultSet().getInt(i);
        } catch (SQLException e) {
            getLogger().error("Unable to call getInt(int) method on wrapped ResultSet.", e);
            return 0;
        }
    }

    @Override
    default long getLong(int i) {
        try {
            return getResultSet().getLong(i);
        } catch (SQLException e) {
            getLogger().error("Unable to call getLong(int) method on wrapped ResultSet.", e);
            return 0;
        }
    }

    @Override
    default float getFloat(int i) {
        try {
            return getResultSet().getFloat(i);
        } catch (SQLException e) {
            getLogger().error("Unable to call getFloat(int) method on wrapped ResultSet.", e);
            return 0;
        }
    }

    @Override
    default double getDouble(int i) {
        try {
            return getResultSet().getDouble(i);
        } catch (SQLException e) {
            getLogger().error("Unable to call getDouble(int) method on wrapped ResultSet.", e);
            return 0;
        }
    }

    @Override
    default BigDecimal getBigDecimal(int i, int i1) {
        try {
            return getResultSet().getBigDecimal(i);
        } catch (SQLException e) {
            getLogger().error("Unable to call getBigDecimal(int, int) method on wrapped ResultSet.", e);
            return new BigDecimal("0");
        }
    }

    @Override
    default byte[] getBytes(int i) {
        try {
            return getResultSet().getBytes(i);
        } catch (SQLException e) {
            getLogger().error("Unable to call getBytes(int) method on wrapped ResultSet.", e);
            return new byte[0];
        }
    }

    @Override
    default Date getDate(int i) {
        try {
            return getResultSet().getDate(i);
        } catch (SQLException e) {
            getLogger().error("Unable to call getDate(int) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default Time getTime(int i) {
        try {
            return getResultSet().getTime(i);
        } catch (SQLException e) {
            getLogger().error("Unable to call getTime(int) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default Timestamp getTimestamp(int i) {
        try {
            return getResultSet().getTimestamp(i);
        } catch (SQLException e) {
            getLogger().error("Unable to call getTimestamp(int) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default InputStream getAsciiStream(int i) {
        try {
            return getResultSet().getAsciiStream(i);
        } catch (SQLException e) {
            getLogger().error("Unable to call getAsciiStream(int) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    @Deprecated
    default InputStream getUnicodeStream(int i) {
        try {
            return getResultSet().getUnicodeStream(i);
        } catch (SQLException e) {
            getLogger().error("Unable to call getUnicodeStream(int) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default InputStream getBinaryStream(int i) {
        try {
            return getResultSet().getBinaryStream(i);
        } catch (SQLException e) {
            getLogger().error("Unable to call getBinaryStream(int) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default String getString(String s) {
        try {
            return getResultSet().getString(s);
        } catch (SQLException e) {
            getLogger().error("Unable to call getString(String) method on wrapped ResultSet.", e);
            return "";
        }
    }

    @Override
    default boolean getBoolean(String s) {
        try {
            return getResultSet().getBoolean(s);
        } catch (SQLException e) {
            getLogger().error("Unable to call getBoolean(String) method on wrapped ResultSet.", e);
            return false;
        }
    }

    @Override
    default byte getByte(String s) {
        try {
            return getResultSet().getByte(s);
        } catch (SQLException e) {
            getLogger().error("Unable to call getByte(String) method on wrapped ResultSet.", e);
            return 0;
        }
    }

    @Override
    default short getShort(String s) {
        try {
            return getResultSet().getShort(s);
        } catch (SQLException e) {
            getLogger().error("Unable to call getShort(String) method on wrapped ResultSet.", e);
            return 0;
        }
    }

    @Override
    default int getInt(String s) {
        try {
            return getResultSet().getInt(s);
        } catch (SQLException e) {
            getLogger().error("Unable to call getInt(String) method on wrapped ResultSet.", e);
            return 0;
        }
    }

    @Override
    default long getLong(String s) {
        try {
            return getResultSet().getLong(s);
        } catch (SQLException e) {
            getLogger().error("Unable to call getLong(String) method on wrapped ResultSet.", e);
            return 0;
        }
    }

    @Override
    default float getFloat(String s) {
        try {
            return getResultSet().getFloat(s);
        } catch (SQLException e) {
            getLogger().error("Unable to call getFloat(String) method on wrapped ResultSet.", e);
            return 0;
        }
    }

    @Override
    default double getDouble(String s) {
        try {
            return getResultSet().getDouble(s);
        } catch (SQLException e) {
            getLogger().error("Unable to call getDouble(String) method on wrapped ResultSet.", e);
            return 0;
        }
    }

    @Override
    default BigDecimal getBigDecimal(String s, int i) {
        try {
            return getResultSet().getBigDecimal(s);
        } catch (SQLException e) {
            getLogger().error("Unable to call getBigDecimal(String) method on wrapped ResultSet.", e);
            return new BigDecimal("0");
        }
    }

    @Override
    default byte[] getBytes(String s) {
        try {
            return getResultSet().getBytes(s);
        } catch (SQLException e) {
            getLogger().error("Unable to call getBytes(String) method on wrapped ResultSet.", e);
            return new byte[0];
        }
    }

    @Override
    default Date getDate(String s) {
        try {
            return getResultSet().getDate(s);
        } catch (SQLException e) {
            getLogger().error("Unable to call getDate(String) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default Time getTime(String s) {
        try {
            return getResultSet().getTime(s);
        } catch (SQLException e) {
            getLogger().error("Unable to call getTime(String) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default Timestamp getTimestamp(String s) {
        try {
            return getResultSet().getTimestamp(s);
        } catch (SQLException e) {
            getLogger().error("Unable to call getTimestamp(String) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default InputStream getAsciiStream(String s) {
        try {
            return getResultSet().getAsciiStream(s);
        } catch (SQLException e) {
            getLogger().error("Unable to call getAsciiStream(String) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    @Deprecated
    default InputStream getUnicodeStream(String s) {
        try {
            return getResultSet().getUnicodeStream(s);
        } catch (SQLException e) {
            getLogger().error("Unable to call getUnicodeStream(String) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default InputStream getBinaryStream(String s) {
        try {
            return getResultSet().getBinaryStream(s);
        } catch (SQLException e) {
            getLogger().error("Unable to call getBinaryStream(String) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default SQLWarning getWarnings() {
        try {
            return getResultSet().getWarnings();
        } catch (SQLException e) {
            getLogger().error("Unable to call getWarnings() method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default void clearWarnings() {
        try {
            getResultSet().clearWarnings();
        } catch (SQLException e) {
            getLogger().error("Unable to call clearWarnings() method on wrapped ResultSet.", e);
        }
    }

    @Override
    default String getCursorName() {
        try {
            return getResultSet().getCursorName();
        } catch (SQLException e) {
            getLogger().error("Unable to call getCursorName() method on wrapped ResultSet.", e);
            return "";
        }
    }

    @Override
    default ResultSetMetaData getMetaData() {
        try {
            return getResultSet().getMetaData();
        } catch (SQLException e) {
            getLogger().error("Unable to call getMetaData() method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default Object getObject(int i) {
        try {
            return getResultSet().getObject(i);
        } catch (SQLException e) {
            getLogger().error("Unable to call getObject(int) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default Object getObject(String s) {
        try {
            return getResultSet().getObject(s);
        } catch (SQLException e) {
            getLogger().error("Unable to call getObject(String) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default int findColumn(String s) {
        try {
            return getResultSet().findColumn(s);
        } catch (SQLException e) {
            getLogger().error("Unable to call findColumn(String) method on wrapped ResultSet.", e);
            return 0;
        }
    }

    @Override
    default Reader getCharacterStream(int i) {
        try {
            return getResultSet().getCharacterStream(i);
        } catch (SQLException e) {
            getLogger().error("Unable to call getObject(int) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default Reader getCharacterStream(String s) {
        try {
            return getResultSet().getCharacterStream(s);
        } catch (SQLException e) {
            getLogger().error("Unable to call getCharacterStream(String) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default BigDecimal getBigDecimal(int i) {
        try {
            return getResultSet().getBigDecimal(i);
        } catch (SQLException e) {
            getLogger().error("Unable to call getBigDecimal(int) method on wrapped ResultSet.", e);
            return new BigDecimal("0");
        }
    }

    @Override
    default BigDecimal getBigDecimal(String s) {
        try {
            return getResultSet().getBigDecimal(s);
        } catch (SQLException e) {
            getLogger().error("Unable to call getBigDecimal(String) method on wrapped ResultSet.", e);
            return new BigDecimal("0");
        }
    }

    @Override
    default boolean isBeforeFirst() {
        try {
            return getResultSet().isBeforeFirst();
        } catch (SQLException e) {
            getLogger().error("Unable to call isBeforeFirst() method on wrapped ResultSet.", e);
            return false;
        }
    }

    @Override
    default boolean isAfterLast() {
        try {
            return getResultSet().isAfterLast();
        } catch (SQLException e) {
            getLogger().error("Unable to call isAfterLast() method on wrapped ResultSet.", e);
            return false;
        }
    }

    @Override
    default boolean isFirst() {
        try {
            return getResultSet().isFirst();
        } catch (SQLException e) {
            getLogger().error("Unable to call isFirst() method on wrapped ResultSet.", e);
            return false;
        }
    }

    @Override
    default boolean isLast() {
        try {
            return getResultSet().isLast();
        } catch (SQLException e) {
            getLogger().error("Unable to call isLast() method on wrapped ResultSet.", e);
            return false;
        }
    }

    @Override
    default void beforeFirst() {
        try {
            getResultSet().beforeFirst();
        } catch (SQLException e) {
            getLogger().error("Unable to call beforeFirst() method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void afterLast() {
        try {
            getResultSet().afterLast();
        } catch (SQLException e) {
            getLogger().error("Unable to call afterLast() method on wrapped ResultSet.", e);
        }
    }

    @Override
    default boolean first() {
        try {
            return getResultSet().first();
        } catch (SQLException e) {
            getLogger().error("Unable to call first() method on wrapped ResultSet.", e);
            return false;
        }
    }

    @Override
    default boolean last() {
        try {
            return getResultSet().isAfterLast();
        } catch (SQLException e) {
            getLogger().error("Unable to call isAfterLast() method on wrapped ResultSet.", e);
            return false;
        }
    }

    @Override
    default int getRow() {
        try {
            return getResultSet().getRow();
        } catch (SQLException e) {
            getLogger().error("Unable to call getRow() method on wrapped ResultSet.", e);
            return -2;
        }
    }

    @Override
    default boolean absolute(int i) {
        try {
            return getResultSet().absolute(i);
        } catch (SQLException e) {
            getLogger().error("Unable to call absolute(i) method on wrapped ResultSet.", e);
            return false;
        }
    }

    @Override
    default boolean relative(int i) {
        try {
            return getResultSet().relative(i);
        } catch (SQLException e) {
            getLogger().error("Unable to call relative(i) method on wrapped ResultSet.", e);
            return false;
        }
    }

    @Override
    default boolean previous() {
        try {
            return getResultSet().previous();
        } catch (SQLException e) {
            getLogger().error("Unable to call previous() method on wrapped ResultSet.", e);
            return false;
        }
    }

    @Override
    default void setFetchDirection(int i) {
        try {
            getResultSet().setFetchDirection(i);
        } catch (SQLException e) {
            getLogger().error("Unable to call setFetchDirection(int) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default int getFetchDirection() {
        try {
            return getResultSet().getFetchDirection();
        } catch (SQLException e) {
            getLogger().error("Unable to call getFetchDirection() method on wrapped ResultSet.", e);
            return ResultSet.FETCH_UNKNOWN;
        }
    }

    @Override
    default void setFetchSize(int i) {
        try {
            getResultSet().setFetchSize(i);
        } catch (SQLException e) {
            getLogger().error("Unable to call setFetchSize(int) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default int getFetchSize() {
        try {
            return getResultSet().getFetchSize();
        } catch (SQLException e) {
            getLogger().error("Unable to call getFetchSize() method on wrapped ResultSet.", e);
        }
        return -1;
    }

    @Override
    default int getType() {
        try {
            return getResultSet().getType();
        } catch (SQLException e) {
            getLogger().error("Unable to call getType() method on wrapped ResultSet.", e);
        }
        return ResultSet.TYPE_FORWARD_ONLY;
    }

    @Override
    default int getConcurrency() {
        try {
            return getResultSet().getConcurrency();
        } catch (SQLException e) {
            getLogger().error("Unable to call getConcurrency() method on wrapped ResultSet.", e);
        }
        return ResultSet.CONCUR_READ_ONLY;
    }

    @Override
    default boolean rowUpdated() {
        try {
            return getResultSet().rowUpdated();
        } catch (SQLException e) {
            getLogger().error("Unable to call rowUpdated() method on wrapped ResultSet.", e);
        }
        return false;
    }

    @Override
    default boolean rowInserted() {
        try {
            return getResultSet().rowInserted();
        } catch (SQLException e) {
            getLogger().error("Unable to call rowInserted() method on wrapped ResultSet.", e);
        }
        return false;
    }

    @Override
    default boolean rowDeleted() {
        try {
            return getResultSet().rowDeleted();
        } catch (SQLException e) {
            getLogger().error("Unable to call rowDeleted() method on wrapped ResultSet.", e);
        }
        return false;
    }

    @Override
    default void updateNull(int i) {
        try {
            getResultSet().updateNull(i);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateNull(int) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateBoolean(int i, boolean b) {
        try {
            getResultSet().updateBoolean(i, b);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateBoolean(int, boolean) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateByte(int i, byte b) {
        try {
            getResultSet().updateByte(i, b);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateByte(int, byte) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateShort(int i, short i1) {
        try {
            getResultSet().updateShort(i, i1);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateShort(int, short) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateInt(int i, int i1) {
        try {
            getResultSet().updateInt(i, i1);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateInt(int, int) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateLong(int i, long l) {
        try {
            getResultSet().updateLong(i, l);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateLong(int, long) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateFloat(int i, float v) {
        try {
            getResultSet().updateFloat(i, v);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateFloat(int, float) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateDouble(int i, double v) {
        try {
            getResultSet().updateDouble(i, v);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateDouble(int, double) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateBigDecimal(int i, BigDecimal bigDecimal) {
        try {
            getResultSet().updateBigDecimal(i, bigDecimal);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateBigDecimal(int, BigDecimal) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateString(int i, String s) {
        try {
            getResultSet().updateString(i, s);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateString(int, String) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateBytes(int i, byte[] bytes) {
        try {
            getResultSet().updateBytes(i, bytes);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateBytes(int, byte[]) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateDate(int i, Date date) {
        try {
            getResultSet().updateDate(i, date);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateDate(int, Date) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateTime(int i, Time time) {
        try {
            getResultSet().updateTime(i, time);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateTime(int, Time) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateTimestamp(int i, Timestamp timestamp) {
        try {
            getResultSet().updateTimestamp(i, timestamp);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateTimestamp(int, Timestamp) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateAsciiStream(int i, InputStream inputStream, int i1) {
        try {
            getResultSet().updateAsciiStream(i, inputStream, i1);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateAsciiStream(int, InputStream, int) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateBinaryStream(int i, InputStream inputStream, int i1) {
        try {
            getResultSet().updateBinaryStream(i, inputStream, i1);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateBinaryStream(int, InputStream, int) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateCharacterStream(int i, Reader reader, int i1) {
        try {
            getResultSet().updateCharacterStream(i, reader, i1);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateCharacterStream(int, Reader, int) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateObject(int i, Object o, int i1) {
        try {
            getResultSet().updateObject(i, o, i1);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateObject(int, Object, int) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateObject(int i, Object o) {
        try {
            getResultSet().updateObject(i, o);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateObject(int, Object) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateNull(String s) {
        try {
            getResultSet().updateNull(s);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateNull(String) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateBoolean(String s, boolean b) {
        try {
            getResultSet().updateBoolean(s, b);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateBoolean(String, boolean) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateByte(String s, byte b) {
        try {
            getResultSet().updateByte(s, b);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateByte(String, byte) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateShort(String s, short i) {
        try {
            getResultSet().updateShort(s, i);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateShort(String, short) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateInt(String s, int i) {
        try {
            getResultSet().updateInt(s, i);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateInt(String, int) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateLong(String s, long l) {
        try {
            getResultSet().updateLong(s, l);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateLong(String, long) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateFloat(String s, float v) {
        try {
            getResultSet().updateFloat(s, v);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateFloat(String, float) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateDouble(String s, double v) {
        try {
            getResultSet().updateDouble(s, v);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateDouble(String, double) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateBigDecimal(String s, BigDecimal bigDecimal) {
        try {
            getResultSet().updateBigDecimal(s, bigDecimal);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateBigDecimal(String, BigDecimal) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateString(String s, String s1) {
        try {
            getResultSet().updateString(s, s1);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateString(String, String) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateBytes(String s, byte[] bytes) {
        try {
            getResultSet().updateBytes(s, bytes);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateBytes(String, byte[]) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateDate(String s, Date date) {
        try {
            getResultSet().updateDate(s, date);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateDate(String, Date) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateTime(String s, Time time) {
        try {
            getResultSet().updateTime(s, time);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateTime(String, Time) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateTimestamp(String s, Timestamp timestamp) {
        try {
            getResultSet().updateTimestamp(s, timestamp);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateTimestamp(String, Timestamp) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateAsciiStream(String s, InputStream inputStream, int i) {
        try {
            getResultSet().updateAsciiStream(s, inputStream, i);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateAsciiStream(String, InputStream, int) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateBinaryStream(String s, InputStream inputStream, int i) {
        try {
            getResultSet().updateBinaryStream(s, inputStream, i);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateBinaryStream(String, InputStream, int) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateCharacterStream(String s, Reader reader, int i) {
        try {
            getResultSet().updateCharacterStream(s, reader, i);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateCharacterStream(String, Reader, int) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateObject(String s, Object o, int i) {
        try {
            getResultSet().updateObject(s, o, i);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateObject(String, Object, int) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateObject(String s, Object o) {
        try {
            getResultSet().updateObject(s, o);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateObject(String, Object) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void insertRow() {
        try {
            getResultSet().insertRow();
        } catch (SQLException e) {
            getLogger().error("Unable to call insertRow() method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateRow() {
        try {
            getResultSet().updateRow();
        } catch (SQLException e) {
            getLogger().error("Unable to call updateRow() method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void deleteRow() {
        try {
            getResultSet().deleteRow();
        } catch (SQLException e) {
            getLogger().error("Unable to call deleteRow() method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void refreshRow() {
        try {
            getResultSet().refreshRow();
        } catch (SQLException e) {
            getLogger().error("Unable to call refreshRow() method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void cancelRowUpdates() {
        try {
            getResultSet().cancelRowUpdates();
        } catch (SQLException e) {
            getLogger().error("Unable to call cancelRowUpdates() method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void moveToInsertRow() {
        try {
            getResultSet().moveToInsertRow();
        } catch (SQLException e) {
            getLogger().error("Unable to call moveToInsertRow() method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void moveToCurrentRow() {
        try {
            getResultSet().moveToCurrentRow();
        } catch (SQLException e) {
            getLogger().error("Unable to call moveToCurrentRow() method on wrapped ResultSet.", e);
        }
    }

    @Override
    default Statement getStatement() {
        try {
            return getResultSet().getStatement();
        } catch (SQLException e) {
            getLogger().error("Unable to call getStatement() method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default Object getObject(int i, Map<String, Class<?>> map) {
        try {
            return getResultSet().getObject(i, map);
        } catch (SQLException e) {
            getLogger().error("Unable to call getObject(int, Map<String, CLass<?>>) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default Ref getRef(int i) {
        try {
            return getResultSet().getRef(i);
        } catch (SQLException e) {
            getLogger().error("Unable to call getRef(int) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default Blob getBlob(int i) {
        try {
            return getResultSet().getBlob(i);
        } catch (SQLException e) {
            getLogger().error("Unable to call getBlob(int) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default Clob getClob(int i) {
        try {
            return getResultSet().getClob(i);
        } catch (SQLException e) {
            getLogger().error("Unable to call getClob(int) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default Array getArray(int i) {
        try {
            return getResultSet().getArray(i);
        } catch (SQLException e) {
            getLogger().error("Unable to call getArray(int) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default Object getObject(String s, Map<String, Class<?>> map) {
        try {
            return getResultSet().getObject(s, map);
        } catch (SQLException e) {
            getLogger().error("Unable to call getObject(String, Map<String, Class<?>>) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default Ref getRef(String s) {
        try {
            return getResultSet().getRef(s);
        } catch (SQLException e) {
            getLogger().error("Unable to call getRef(String) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default Blob getBlob(String s) {
        try {
            return getResultSet().getBlob(s);
        } catch (SQLException e) {
            getLogger().error("Unable to call getBlob(String) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default Clob getClob(String s) {
        try {
            return getResultSet().getClob(s);
        } catch (SQLException e) {
            getLogger().error("Unable to call getClob(String) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default Array getArray(String s) {
        try {
            return getResultSet().getArray(s);
        } catch (SQLException e) {
            getLogger().error("Unable to call getArray(String) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default Date getDate(int i, Calendar calendar) {
        try {
            return getResultSet().getDate(i, calendar);
        } catch (SQLException e) {
            getLogger().error("Unable to call getDate(int, Calendar) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default Date getDate(String s, Calendar calendar) {
        try {
            return getResultSet().getDate(s, calendar);
        } catch (SQLException e) {
            getLogger().error("Unable to call getDate(String, Calendar) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default Time getTime(int i, Calendar calendar) {
        try {
            return getResultSet().getTime(i, calendar);
        } catch (SQLException e) {
            getLogger().error("Unable to call getTime(int, Calendar) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default Time getTime(String s, Calendar calendar) {
        try {
            return getResultSet().getTime(s, calendar);
        } catch (SQLException e) {
            getLogger().error("Unable to call getTime(String, Calendar) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default Timestamp getTimestamp(int i, Calendar calendar) {
        try {
            return getResultSet().getTimestamp(i, calendar);
        } catch (SQLException e) {
            getLogger().error("Unable to call getTimestamp(int, Calendar) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default Timestamp getTimestamp(String s, Calendar calendar) {
        try {
            return getResultSet().getTimestamp(s, calendar);
        } catch (SQLException e) {
            getLogger().error("Unable to call getTimestamp(String, Calendar) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default URL getURL(int i) {
        try {
            return getResultSet().getURL(i);
        } catch (SQLException e) {
            getLogger().error("Unable to call getURL(int) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default URL getURL(String s) {
        try {
            return getResultSet().getURL(s);
        } catch (SQLException e) {
            getLogger().error("Unable to call getURL(String) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default void updateRef(int i, Ref ref) {
        try {
            getResultSet().updateRef(i, ref);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateRef(int, Ref) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateRef(String s, Ref ref) {
        try {
            getResultSet().updateRef(s, ref);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateRef(String, Ref) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateBlob(int i, Blob blob) {
        try {
            getResultSet().updateBlob(i, blob);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateBlob(int, Blob) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateBlob(String s, Blob blob) {
        try {
            getResultSet().updateBlob(s, blob);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateRef(int, Blob) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateClob(int i, Clob clob) {
        try {
            getResultSet().updateClob(i, clob);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateRef(int, Clob) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateClob(String s, Clob clob) {
        try {
            getResultSet().updateClob(s, clob);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateClob(String, Clob) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateArray(int i, Array array) {
        try {
            getResultSet().updateArray(i, array);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateArray(int, Array) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateArray(String s, Array array) {
        try {
            getResultSet().updateArray(s, array);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateArray(int, Array) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default RowId getRowId(int i) {
        try {
            return getResultSet().getRowId(i);
        } catch (SQLException e) {
            getLogger().error("Unable to call getRowId(int) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default RowId getRowId(String s) {
        try {
            return getResultSet().getRowId(s);
        } catch (SQLException e) {
            getLogger().error("Unable to call getRowId(String) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default void updateRowId(int i, RowId rowId) {
        try {
            getResultSet().updateRowId(i, rowId);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateRowId(int, RowId) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateRowId(String s, RowId rowId) {
        try {
            getResultSet().updateRowId(s, rowId);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateRowId(String, RowId) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default int getHoldability() {
        try {
            return getResultSet().getHoldability();
        } catch (SQLException e) {
            getLogger().error("Unable to call getHoldability() method on wrapped ResultSet.", e);
            return ResultSet.HOLD_CURSORS_OVER_COMMIT;
        }
    }

    @Override
    default boolean isClosed() {
        try {
            return getResultSet().isClosed();
        } catch (SQLException e) {
            getLogger().error("Unable to call isClosed() method on wrapped ResultSet.", e);
            return true;
        }
    }

    @Override
    default void updateNString(int i, String s) {
        try {
            getResultSet().updateNString(i, s);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateNString(int, String) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateNString(String s, String s1) {
        try {
            getResultSet().updateNString(s, s1);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateNString(String, String) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateNClob(int i, NClob nClob) {
        try {
            getResultSet().updateNClob(i, nClob);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateNClob(int, NClob) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateNClob(String s, NClob nClob) {
        try {
            getResultSet().updateNClob(s, nClob);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateNClob(String, NClob) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default NClob getNClob(int i) {
        try {
            return getResultSet().getNClob(i);
        } catch (SQLException e) {
            getLogger().error("Unable to call getNClob(int) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default NClob getNClob(String s) {
        try {
            return getResultSet().getNClob(s);
        } catch (SQLException e) {
            getLogger().error("Unable to call getNClob(String) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default SQLXML getSQLXML(int i) {
        try {
            return getResultSet().getSQLXML(i);
        } catch (SQLException e) {
            getLogger().error("Unable to call getSQLXML(int) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default SQLXML getSQLXML(String s) {
        try {
            return getResultSet().getSQLXML(s);
        } catch (SQLException e) {
            getLogger().error("Unable to call getSQLXML(String) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default void updateSQLXML(int i, SQLXML sqlxml) {
        try {
            getResultSet().updateSQLXML(i, sqlxml);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateSQLXML(int, SQLXML) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateSQLXML(String s, SQLXML sqlxml) {
        try {
            getResultSet().updateSQLXML(s, sqlxml);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateSQLXML(String, SQLXML) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default String getNString(int i) {
        try {
            return getResultSet().getNString(i);
        } catch (SQLException e) {
            getLogger().error("Unable to call getNString(int) method on wrapped ResultSet.", e);
            return "";
        }
    }

    @Override
    default String getNString(String s) {
        try {
            return getResultSet().getNString(s);
        } catch (SQLException e) {
            getLogger().error("Unable to call getNString(String) method on wrapped ResultSet.", e);
            return "";
        }
    }

    @Override
    default Reader getNCharacterStream(int i) {
        try {
            return getResultSet().getNCharacterStream(i);
        } catch (SQLException e) {
            getLogger().error("Unable to call getNCharacterStream(int) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default Reader getNCharacterStream(String s) {
        try {
            return getResultSet().getNCharacterStream(s);
        } catch (SQLException e) {
            getLogger().error("Unable to call getNCharacterStream(String) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default void updateNCharacterStream(int i, Reader reader, long l) {
        try {
            getResultSet().updateNCharacterStream(i, reader, l);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateNCharacterStream(int, Reader, long) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateNCharacterStream(String s, Reader reader, long l) {
        try {
            getResultSet().updateNCharacterStream(s, reader, l);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateNCharacterStream(String, Reader, long) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateAsciiStream(int i, InputStream inputStream, long l) {
        try {
            getResultSet().updateAsciiStream(i, inputStream, l);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateAsciiStream(int, InputStream, long) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateBinaryStream(int i, InputStream inputStream, long l) {
        try {
            getResultSet().updateBinaryStream(i, inputStream, l);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateBinaryStream(int, InputStream, long) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateCharacterStream(int i, Reader reader, long l) {
        try {
            getResultSet().updateCharacterStream(i, reader, l);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateCharacterStream(int, Reader, long) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateAsciiStream(String s, InputStream inputStream, long l) {
        try {
            getResultSet().updateAsciiStream(s, inputStream, l);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateAsciiStream(String, InputStream, long) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateBinaryStream(String s, InputStream inputStream, long l) {
        try {
            getResultSet().updateBinaryStream(s, inputStream, l);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateBinaryStream(String, InputStream, l) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateCharacterStream(String s, Reader reader, long l) {
        try {
            getResultSet().updateCharacterStream(s, reader, l);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateCharacterStream(String, Reader, long) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateBlob(int i, InputStream inputStream, long l) {
        try {
            getResultSet().updateBlob(i, inputStream, l);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateBlob(int, InputStream, long) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateBlob(String s, InputStream inputStream, long l) {
        try {
            getResultSet().updateBlob(s, inputStream, l);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateBlob(int, InputStream, long) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateClob(int i, Reader reader, long l) {
        try {
            getResultSet().updateClob(i, reader, l);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateClob(int, Reader, long) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateClob(String s, Reader reader, long l) {
        try {
            getResultSet().updateClob(s, reader, l);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateClob(String, Reader, long) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateNClob(int i, Reader reader, long l) {
        try {
            getResultSet().updateNClob(i, reader, l);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateNClob(int, Reader, long) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateNClob(String s, Reader reader, long l) {
        try {
            getResultSet().updateNClob(s, reader, l);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateNClob(String, InputStream, long) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateNCharacterStream(int i, Reader reader) {
        try {
            getResultSet().updateNCharacterStream(i, reader);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateNCharacterStream(int, InputStream) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateNCharacterStream(String s, Reader reader) {
        try {
            getResultSet().updateNCharacterStream(s, reader);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateCharacterStream(String, Reader) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateAsciiStream(int i, InputStream inputStream) {
        try {
            getResultSet().updateAsciiStream(i, inputStream);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateAsciiStream(int, InputStream) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateBinaryStream(int i, InputStream inputStream) {
        try {
            getResultSet().updateBinaryStream(i, inputStream);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateBinaryStream(int, InputStream) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateCharacterStream(int i, Reader reader) {
        try {
            getResultSet().updateCharacterStream(i, reader);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateCharacterStream(int, InputStream) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateAsciiStream(String s, InputStream inputStream) {
        try {
            getResultSet().updateAsciiStream(s, inputStream);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateAsciiStream(String, InputStream) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateBinaryStream(String s, InputStream inputStream) {
        try {
            getResultSet().updateBinaryStream(s, inputStream);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateBinaryStream(String, InputStream) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateCharacterStream(String s, Reader reader) {
        try {
            getResultSet().updateCharacterStream(s, reader);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateCharacterStream(int, Reader) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateBlob(int i, InputStream inputStream) {
        try {
            getResultSet().updateBlob(i, inputStream);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateBlob(int, InputStream) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateBlob(String s, InputStream inputStream) {
        try {
            getResultSet().updateBlob(s, inputStream);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateBlob(String, InputStream) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateClob(int i, Reader reader) {
        try {
            getResultSet().updateClob(i, reader);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateClob(int, Reader) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateClob(String s, Reader reader) {
        try {
            getResultSet().updateClob(s, reader);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateClob(String, Reader) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateNClob(int i, Reader reader) {
        try {
            getResultSet().updateNClob(i, reader);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateNClob(int, Reader) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default void updateNClob(String s, Reader reader) {
        try {
            getResultSet().updateNClob(s, reader);
        } catch (SQLException e) {
            getLogger().error("Unable to call updateNClob(String, Reader) method on wrapped ResultSet.", e);
        }
    }

    @Override
    default <T> T getObject(int i, Class<T> aClass) {
        try {
            return getResultSet().getObject(i, aClass);
        } catch (SQLException e) {
            getLogger().error("Unable to call getObject(int, Class<T>) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default <T> T getObject(String s, Class<T> aClass) {
        try {
            return getResultSet().getObject(s, aClass);
        } catch (SQLException e) {
            getLogger().error("Unable to call getObject(String, Class<T>) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default <T> T unwrap(Class<T> aClass) {
        try {
            return getResultSet().unwrap(aClass);
        } catch (SQLException e) {
            getLogger().error("Unable to call unwrap(Class<T>) method on wrapped ResultSet.", e);
            return null;
        }
    }

    @Override
    default boolean isWrapperFor(Class<?> aClass) {
        try {
            return getResultSet().isWrapperFor(aClass);
        } catch (SQLException e) {
            getLogger().error("Unable to call isWrapperFor(Class<?>) method on wrapped ResultSet.", e);
            return false;
        }
    }
}
