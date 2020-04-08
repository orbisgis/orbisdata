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

import groovy.lang.Closure;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Extension of the {@link IDataSet} interface. A {@link ITable} is a 2D (column/line) representation of raw data.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (Lab-STICC UBS 2018-2019)
 */
public interface ITable<T> extends IMatrix<T> {

    /**
     * Apply the given {@link Closure} to each row.
     *
     * @param closure {@link Closure} to apply to each row.
     */
    default void eachRow(Closure<Object> closure) {
        this.forEach(closure::call);
    }

    /**
     * Get all column names from the underlying table. If there isn't any column, return an empty {@link Collection}.
     * If an error occurs on requesting the column, return null.
     *
     * @return A {@link Collection} containing the name of the column.
     */
    Collection<String> getColumns();

    /**
     * Get all column information from the underlying table.
     *
     * @return A {@link Map} containing the information of the column.
     */
    Map<String, String> getColumnsTypes();

    /**
     * Get the type of the column from the underlying table.
     *
     * @param columnName set the name of the column
     * @return The type of the column.
     */
    String getColumnType(String columnName);

    /**
     * Return true if the {@link ITable} contains a column with the given name with the given type (case sensible).
     *
     * @param columnName Name of the column to check.
     * @param clazz      Class of the column to check.
     * @return True if the column is found, false otherwise.
     */
    boolean hasColumn(String columnName, Class clazz);

    /**
     * Return true if the {@link ITable} contains a column with the given name (case sensible).
     *
     * @param columnName Name of the column to check.
     * @return True if the column is found, false otherwise.
     */
    default boolean hasColumn(String columnName) {
        return getColumns().contains(columnName);
    }

    /**
     * Return true if the {@link ITable} contains all the column describes in the given {@link Map} (case sensible).
     *
     * @param columnMap {@link Map} containing the columns with the column name as key and the column type as value.
     * @return True if the columns are found, false otherwise.
     */
    default boolean hasColumns(Map<String, Class> columnMap) {
        return columnMap.entrySet().stream().allMatch(entry -> hasColumn(entry.getKey(), entry.getValue()));
    }

    /**
     * Return true if the {@link ITable} contains all the column describes in the given {@link List} (case sensible).
     *
     * @param columnList {@link List} containing the columns with the column name as key and the column type as value.
     * @return True if the columns are found, false otherwise.
     */
    default boolean hasColumns(List<String> columnList) {
        return columnList.stream().allMatch(this::hasColumn);
    }

    /**
     * Return the count of columns.
     *
     * @return The count of columns.
     */
    default int getColumnCount() {
        return getColumns().size();
    }

    /**
     * Return the count of lines or -1 if not able to find the {@link ITable}.
     *
     * @return The count of lines or -1 if not able to find the {@link ITable}.
     */
    int getRowCount();

    /**
     * Return the current row index.
     * The thrown exception is for compatibility purpose.
     *
     * @return The current row index.
     */
    int getRow() throws Exception;

    /**
     * Return a {@link Collection} of all the unique values of the {@link ITable}. This method can take a lot of time and
     * resources according the the table size. If no values are found, return an empty collection. If an error occurred,
     * return null.
     *
     * @param column Name of the column to request.
     * @return A {@link Collection} of all the unique values of the {@link ITable}.
     */
    Collection<String> getUniqueValues(String column);

    /**
     * Save the {@link ITable} into a file.
     *
     * @param filePath Path of the file to be saved.
     * @return True is the file has been saved, false otherwise.
     */
    default boolean save(String filePath) {
        return save(filePath, null);
    }

    /**
     * Save the {@link ITable} into a file.
     *
     * @param filePath Path of the file to be saved.
     * @param encoding Encoding of the file.
     * @return True is the file has been saved, false otherwise.
     */
    boolean save(String filePath, String encoding);

    /**
     * Return the values of the first row in a {@link List}. If there is no row, return an empty list.
     *
     * @return The values of the first row in a {@link List}.
     */
    List<Object> getFirstRow();

    /**
     * Indicates the columns use for the selection.
     *
     * @param columns Array of the columns use for the selection.
     * @return Filtered {@link ITable}.
     */
    ITable columns(String... columns);

    /**
     * Indicates the columns use for the selection.
     *
     * @param columns List of the columns use for the selection.
     * @return Filtered {@link ITable}.
     */
    ITable columns(List<String> columns);

    /**
     * Return true if the {@link ITable} is spatial.
     *
     * @return True if the {@link ITable} is spatial.
     */
    boolean isSpatial();

    @Override
    default int getNDim() {
        return 2;
    }

    @Override
    default boolean isEmpty() {
        return getRowCount() == 0;
    }

    @Override
    default int[] getSize() {
        return new int[]{getColumnCount(), getRowCount()};
    }

    /**
     * Return the {@link String} in the current row on the given column.
     *
     * @param column Index of the column.
     * @return The {@link String} in the current row on the given column.
     */
    String getString(int column) throws Exception;

    /**
     * Return the boolean in the current row on the given column.
     *
     * @param column Index of the column.
     * @return The boolean in the current row on the given column.
     */
    boolean getBoolean(int column) throws Exception;

    /**
     * Return the byte in the current row on the given column.
     *
     * @param column Index of the column.
     * @return The byte in the current row on the given column.
     */
    byte getByte(int column) throws Exception;

    /**
     * Return the short in the current row on the given column.
     *
     * @param column Index of the column.
     * @return The short in the current row on the given column.
     */
    short getShort(int column) throws Exception;

    /**
     * Return the int in the current row on the given column.
     *
     * @param column Index of the column.
     * @return The int in the current row on the given column.
     */
    int getInt(int column) throws Exception;

    /**
     * Return the long in the current row on the given column.
     *
     * @param column Index of the column.
     * @return The long in the current row on the given column.
     */
    long getLong(int column) throws Exception;

    /**
     * Return the float in the current row on the given column.
     *
     * @param column Index of the column.
     * @return The float in the current row on the given column.
     */
    float getFloat(int column) throws Exception;

    /**
     * Return the double in the current row on the given column.
     *
     * @param column Index of the column.
     * @return The double in the current row on the given column.
     */
    double getDouble(int column) throws Exception;

    /**
     * Return the byte array in the current row on the given column.
     *
     * @param column Index of the column.
     * @return The byte array in the current row on the given column.
     */
    byte[] getBytes(int column) throws Exception;

    /**
     * Return the {@link Date} in the current row on the given column.
     *
     * @param column Index of the column.
     * @return The {@link Date} in the current row on the given column.
     */
    Date getDate(int column) throws Exception;

    /**
     * Return the {@link Time} in the current row on the given column.
     *
     * @param column Index of the column.
     * @return The {@link Time} in the current row on the given column.
     */
    Time getTime(int column) throws Exception;

    /**
     * Return the {@link Timestamp} in the current row on the given column.
     *
     * @param column Index of the column.
     * @return The {@link Timestamp} in the current row on the given column.
     */
    Timestamp getTimestamp(int column) throws Exception;

    /**
     * Return the {@link Object} in the current row on the given column.
     *
     * @param column Index of the column.
     * @return The {@link Object} in the current row on the given column.
     */
    Object getObject(int column) throws Exception;

    /**
     * Return the {@link BigDecimal} in the current row on the given column.
     *
     * @param column Index of the column.
     * @return The {@link BigDecimal} in the current row on the given column.
     */
    BigDecimal getBigDecimal(int column) throws Exception;

    /**
     * Return the {@link String} in the current row on the given column.
     *
     * @param column Name of the column.
     * @return The {@link String} in the current row on the given column.
     */
    String getString(String column) throws Exception;

    /**
     * Return the boolean in the current row on the given column.
     *
     * @param column Name of the column.
     * @return The boolean in the current row on the given column.
     */
    boolean getBoolean(String column) throws Exception;

    /**
     * Return the byte in the current row on the given column.
     *
     * @param column Name of the column.
     * @return The byte in the current row on the given column.
     */
    byte getByte(String column) throws Exception;

    /**
     * Return the short in the current row on the given column.
     *
     * @param column Name of the column.
     * @return The short in the current row on the given column.
     */
    short getShort(String column) throws Exception;

    /**
     * Return the int in the current row on the given column.
     *
     * @param column Name of the column.
     * @return The int in the current row on the given column.
     */
    int getInt(String column) throws Exception;

    /**
     * Return the long in the current row on the given column.
     *
     * @param column Name of the column.
     * @return The long in the current row on the given column.
     */
    long getLong(String column) throws Exception;

    /**
     * Return the float in the current row on the given column.
     *
     * @param column Name of the column.
     * @return The float in the current row on the given column.
     */
    float getFloat(String column) throws Exception;

    /**
     * Return the double in the current row on the given column.
     *
     * @param column Name of the column.
     * @return The double in the current row on the given column.
     */
    double getDouble(String column) throws Exception;

    /**
     * Return the byte array in the current row on the given column.
     *
     * @param column Name of the column.
     * @return The byte array in the current row on the given column.
     */
    byte[] getBytes(String column) throws Exception;

    /**
     * Return the {@link Date} in the current row on the given column.
     *
     * @param column Name of the column.
     * @return The {@link Date} in the current row on the given column.
     */
    Date getDate(String column) throws Exception;

    /**
     * Return the {@link Time} in the current row on the given column.
     *
     * @param column Name of the column.
     * @return The {@link Time} in the current row on the given column.
     */
    Time getTime(String column) throws Exception;

    /**
     * Return the {@link Timestamp} in the current row on the given column.
     *
     * @param column Name of the column.
     * @return The {@link Timestamp} in the current row on the given column.
     */
    Timestamp getTimestamp(String column) throws Exception;

    /**
     * Return the {@link Object} in the current row on the given column.
     *
     * @param column Name of the column.
     * @return The {@link Object} in the current row on the given column.
     */
    Object getObject(String column) throws Exception;

    /**
     * Return the {@link BigDecimal} in the current row on the given column.
     *
     * @param column Name of the column.
     * @return The {@link BigDecimal} in the current row on the given column.
     */
    BigDecimal getBigDecimal(String column) throws Exception;

    /**
     * Return the {@link U} in the current row on the given column.
     *
     * @param column Index of the column.
     * @param clazz {@link Class} of the object.
     * @return The {@link U} in the current row on the given column.
     */
    <U> U getObject(int column, Class<U> clazz) throws Exception;

    /**
     * Return the {@link U} in the current row on the given column.
     *
     * @param column Name of the column.
     * @param clazz {@link Class} of the object.
     * @return The {@link U} in the current row on the given column.
     */
    <U> U getObject(String column, Class<U> clazz) throws Exception;
}
