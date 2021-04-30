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
import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.commons.annotations.Nullable;
import org.orbisgis.orbisdata.datamanager.api.datasource.IJdbcDataSource;
import org.orbisgis.orbisdata.datamanager.api.dsl.IQueryBuilder;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Extension of the {@link IDataSet} interface. A {@link ITable} is a 2D (column/line) representation of raw data.
 *
 * @param <T> The type of elements returned by the iterator.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (Lab-STICC UBS 2018-2020)
 */
public interface ITable<T, U> extends IMatrix<T>, IQueryBuilder {

    /**
     * Apply the given {@link Closure} to each row.
     *
     * @param closure {@link Closure} to apply to each row.
     */
    default void eachRow(@NotNull Closure<Object> closure) {
        this.forEach(closure::call);
    }

    /**
     * Get all column names from the underlying table. If there isn't any column, return an empty {@link Collection}.
     * If an error occurs on requesting the column, return null.
     *
     * @return A {@link Collection} containing the name of the column.
     */
    @Nullable
    Collection<String> getColumns();

    /**
     * Get all column information from the underlying table.
     *
     * @return A {@link Map} containing the information of the column.
     */
    @NotNull
    Map<String, String> getColumnsTypes();

    /**
     * Get the type of the column from the underlying table.
     *
     * @param columnName set the name of the column
     * @return The type of the column.
     */
    @Nullable
    String getColumnType(@NotNull String columnName);

    /**
     * Return true if the {@link ITable} contains a column with the given name with the given type (case sensible).
     *
     * @param columnName Name of the column to check.
     * @param clazz      Class of the column to check.
     * @return True if the column is found, false otherwise.
     */
    boolean hasColumn(@NotNull String columnName, @NotNull Class<?> clazz);

    /**
     * Return true if the {@link ITable} contains a column with the given name (case sensible).
     *
     * @param columnName Name of the column to check.
     * @return True if the column is found, false otherwise.
     */
    default boolean hasColumn(@NotNull String columnName) {
        return getColumns().contains(columnName);
    }

    /**
     * Return true if the {@link ITable} contains all the column describes in the given {@link Map} (case sensible).
     *
     * @param columnMap {@link Map} containing the columns with the column name as key and the column type as value.
     * @return True if the columns are found, false otherwise.
     */
    //TODO : do not iterate resulset set each time
    default boolean hasColumns(@NotNull Map<String, Class<?>> columnMap) {
        return columnMap.entrySet().stream().allMatch(entry -> hasColumn(entry.getKey(), entry.getValue()));
    }

    /**
     * Return true if the {@link ITable} contains all the column describes in the given {@link List} (case sensible).
     *
     * @param columnList {@link List} containing the columns with the column name as key and the column type as value.
     * @return True if the columns are found, false otherwise.
     */
    default boolean hasColumns(@NotNull List<String> columnList) {
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
     * Goes to the next row.
     *
     * @return True if the next row has been reach, false otherwise.
     */
    boolean next() throws Exception;

    /**
     * Goes to the previous row.
     *
     * @return True if the previous row has been reach, false otherwise.
     */
    boolean previous() throws Exception;

    /**
     * Goes to the first row.
     *
     * @return True if the first row has been reach, false otherwise.
     */
    boolean first() throws Exception;

    /**
     * Goes to the last row.
     *
     * @return True if the last row has been reach, false otherwise.
     */
    boolean last() throws Exception;

    /**
     * Returns true if the current row position is the first row.
     *
     * @return True if the current row position is the first row.
     */
    boolean isFirst() throws Exception;

    /**
     * Returns true if the current row position is the last row.
     *
     * @return True if the current row position is the last row.
     */
    boolean isLast() throws Exception;

    /**
     * Return a {@link Collection} of all the unique values of the {@link ITable}. This method can take a lot of time and
     * resources according the the table size. If no values are found, return an empty collection.
     *
     * @param column Name of the column to request.
     * @return A {@link Collection} of all the unique values of the {@link ITable}.
     */
    @Nullable
    Collection<String> getUniqueValues(@NotNull String column);

    /**
     * Save the {@link ITable} into a file.
     *
     * @param filePath Path of the file to be saved.
     * @return the full path of the saved file.
     * If the file cannot be saved return null.
     */
    default String save(@NotNull String filePath) {
        return save(filePath, null);
    }

    /**
     * Save the {@link ITable} into a file.
     *
     * @param filePath Path of the file to be saved.
     * @param delete True to delete the file if exists.
     * @return the full path of the saved file.
     * If the file cannot be saved return null.
     */
    String save(@NotNull String filePath,  boolean delete);

    /**
     * Save the {@link ITable} into a file.
     *
     * @param filePath Path of the file to be saved.
     * @param encoding Encoding of the file.
     * @return the full path of the saved file.
     * If the file cannot be saved return null.
     */
    String save(@NotNull String filePath, @Nullable String encoding);


    /**
     * Save the {@link ITable} into another database.
     *
     * @param dataSource Connection to the output database
     * @return The name of the saved table, formatted according the output datasource
     * Null is the table cannot be saved.
     */
    default String save(@Nullable IJdbcDataSource dataSource) {
        return save(dataSource, false);
    }

    /**
     * Save the {@link ITable} into another database.
     *
     * @param dataSource Connection to the output database
     * @param batchSize Number of rows that must be accumulated in memory.
     * @return The name of the saved table, formatted according the output datasource
     * Null is the table cannot be saved.
     */
    String save(@Nullable IJdbcDataSource dataSource, int batchSize);

    /**
     * Save the {@link ITable} into another database.
     *
     * @param dataSource Connection to the output database
     * @param deleteTable True to delete the output table is exists
     * @return The name of the saved table, formatted according the output datasource
     * Null is the table cannot be saved.
     */
    String save(@Nullable IJdbcDataSource dataSource, boolean deleteTable);

    /**
     * Save the {@link ITable} into another database.
     *
     * @param dataSource Connection to the output database
     * @param deleteTable True to delete the output table is exists
     * @param batchSize Number of rows that must be accumulated in memory.
     * @return The name of the saved table, formatted according the output datasource.
     * Null is the table cannot be saved.
     */
    String save(@Nullable IJdbcDataSource dataSource, boolean deleteTable, int batchSize);

    /**
     * Save the {@link ITable} into another database.
     *
     * @param dataSource Connection to the output database
     * @param outputTableName name of the output table
     * @param deleteTable True to delete the output table is exists
     * @return The name of the saved table, formatted according the output datasource
     * Null is the table cannot be saved.
     */
    String save(@Nullable IJdbcDataSource dataSource, @NotNull String outputTableName, boolean deleteTable);

    /**
     * Save the {@link ITable} into another database.
     *
     * @param dataSource Connection to the output database
     * @param outputTableName name of the output table
     * @param deleteTable True to delete the output table is exists
     * @param batchSize Number of rows that must be accumulated in memory.
     * @return The name of the saved table, formatted according the output datasource
     * Null is the table cannot be saved.
     */
    String save(@Nullable IJdbcDataSource dataSource, @NotNull String outputTableName, boolean deleteTable, int batchSize);

    /**
     * Return the values of the first row in a {@link List}. If there is no row, return an empty list.
     *
     * @return The values of the first row in a {@link List}.
     */
    @NotNull
    List<Object> getFirstRow();

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
    @NotNull
    default int[] getSize() {
        return new int[]{getColumnCount(), getRowCount()};
    }

    /**
     * Return the {@link String} in the current row on the given column.
     *
     * @param column Index of the column.
     * @return The {@link String} in the current row on the given column.
     */
    @Nullable
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
    @Nullable
    byte[] getBytes(int column) throws Exception;

    /**
     * Return the {@link Date} in the current row on the given column.
     *
     * @param column Index of the column.
     * @return The {@link Date} in the current row on the given column.
     */
    @Nullable
    Date getDate(int column) throws Exception;

    /**
     * Return the {@link Time} in the current row on the given column.
     *
     * @param column Index of the column.
     * @return The {@link Time} in the current row on the given column.
     */
    @Nullable
    Time getTime(int column) throws Exception;

    /**
     * Return the {@link Timestamp} in the current row on the given column.
     *
     * @param column Index of the column.
     * @return The {@link Timestamp} in the current row on the given column.
     */
    @Nullable
    Timestamp getTimestamp(int column) throws Exception;

    /**
     * Return the {@link Object} in the current row on the given column.
     *
     * @param column Index of the column.
     * @return The {@link Object} in the current row on the given column.
     */
    @Nullable
    Object getObject(int column) throws Exception;

    /**
     * Return the {@link BigDecimal} in the current row on the given column.
     *
     * @param column Index of the column.
     * @return The {@link BigDecimal} in the current row on the given column.
     */
    @Nullable
    BigDecimal getBigDecimal(int column) throws Exception;

    /**
     * Return the {@link String} in the current row on the given column.
     *
     * @param column Name of the column.
     * @return The {@link String} in the current row on the given column.
     */
    @Nullable
    String getString(@NotNull String column) throws Exception;

    /**
     * Return the boolean in the current row on the given column.
     *
     * @param column Name of the column.
     * @return The boolean in the current row on the given column.
     */
    boolean getBoolean(@NotNull String column) throws Exception;

    /**
     * Return the byte in the current row on the given column.
     *
     * @param column Name of the column.
     * @return The byte in the current row on the given column.
     */
    byte getByte(@NotNull String column) throws Exception;

    /**
     * Return the short in the current row on the given column.
     *
     * @param column Name of the column.
     * @return The short in the current row on the given column.
     */
    short getShort(@NotNull String column) throws Exception;

    /**
     * Return the int in the current row on the given column.
     *
     * @param column Name of the column.
     * @return The int in the current row on the given column.
     */
    int getInt(@NotNull String column) throws Exception;

    /**
     * Return the long in the current row on the given column.
     *
     * @param column Name of the column.
     * @return The long in the current row on the given column.
     */
    long getLong(@NotNull String column) throws Exception;

    /**
     * Return the float in the current row on the given column.
     *
     * @param column Name of the column.
     * @return The float in the current row on the given column.
     */
    float getFloat(@NotNull String column) throws Exception;

    /**
     * Return the double in the current row on the given column.
     *
     * @param column Name of the column.
     * @return The double in the current row on the given column.
     */
    double getDouble(@NotNull String column) throws Exception;

    /**
     * Return the byte array in the current row on the given column.
     *
     * @param column Name of the column.
     * @return The byte array in the current row on the given column.
     */
    @Nullable
    byte[] getBytes(@NotNull String column) throws Exception;

    /**
     * Return the {@link Date} in the current row on the given column.
     *
     * @param column Name of the column.
     * @return The {@link Date} in the current row on the given column.
     */
    @Nullable
    Date getDate(@NotNull String column) throws Exception;

    /**
     * Return the {@link Time} in the current row on the given column.
     *
     * @param column Name of the column.
     * @return The {@link Time} in the current row on the given column.
     */
    @Nullable
    Time getTime(@NotNull String column) throws Exception;

    /**
     * Return the {@link Timestamp} in the current row on the given column.
     *
     * @param column Name of the column.
     * @return The {@link Timestamp} in the current row on the given column.
     */
    @Nullable
    Timestamp getTimestamp(@NotNull String column) throws Exception;

    /**
     * Return the {@link Object} in the current row on the given column.
     *
     * @param column Name of the column.
     * @return The {@link Object} in the current row on the given column.
     */
    @Nullable
    Object getObject(@NotNull String column) throws Exception;

    /**
     * Return the {@link BigDecimal} in the current row on the given column.
     *
     * @param column Name of the column.
     * @return The {@link BigDecimal} in the current row on the given column.
     */
    @Nullable
    BigDecimal getBigDecimal(@NotNull String column) throws Exception;

    /**
     * Return the {@link V} in the current row on the given column.
     *
     * @param column Index of the column.
     * @param clazz {@link Class} of the object.
     * @return The {@link V} in the current row on the given column.
     */
    @Nullable
    <V> V getObject(int column, @NotNull Class<V> clazz) throws Exception;

    /**
     * Return the {@link V} in the current row on the given column.
     *
     * @param column Name of the column.
     * @param clazz {@link Class} of the object.
     * @return The {@link V} in the current row on the given column.
     */
    @Nullable
    <V> V getObject(@NotNull String column, @NotNull Class<V> clazz) throws Exception;

    /**
     * Return a {@link Stream} of {@link T} objects.
     *
     * @return A {@link Stream} of {@link T} objects.
     */
    @Nullable
    Stream<? extends U> stream();

    @NotNull
    Map<String, Object> firstRow();
}
