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
 * DataManager API  is distributed under GPL 3 license.
 *
 * Copyright (C) 2019 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * DataManager API  is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * DataManager API  is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.orbisgis.datamanagerapi.dataset;

import groovy.lang.Closure;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Extension of the {@link IDataSet} interface. A {@link ITable} is a 2D (column/line) representation of raw data.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2018-2019)
 */
public interface ITable extends IDataSet {

    /**
     * Apply the given {@link Closure} to each row.
     *
     * @param closure {@link Closure} to apply to each row.
     */
    void eachRow(Closure closure);
    
    /**
     * Get all column names from the underlying table. If there isn't any column, return an empty {@link Collection}.
     * If an error occurs on requesting the column, return null.
     *
     * @return A {@link Collection} containing the name of the column
     */
    Collection<String> getColumnNames();

    /**
     * Return true if the {@link ITable} contains a column with the given name with the given type (case sensible).
     *
     * @param columnName Name of the column to check.
     * @param clazz Class of the column to check.
     *
     * @return True if the column is found, false otherwise.
     */
    boolean hasColumn(String columnName, Class clazz);

    /**
     * Return true if the {@link ITable} contains a column with the given name (case sensible).
     *
     * @param columnName Name of the column to check.
     *
     * @return True if the column is found, false otherwise.
     */
    default boolean hasColumn(String columnName){
        return getColumnNames().contains(columnName);
    }

    /**
     * Return true if the {@link ITable} contains all the column describes in the given {@link Map} (case sensible).
     *
     * @param columnMap {@link Map} containing the columns with the column name as key and the column type as value.
     *
     * @return True if the columns are found, false otherwise.
     */
    default boolean hasColumns(Map<String, Class> columnMap){
        return columnMap.entrySet().stream().allMatch(entry -> hasColumn(entry.getKey(), entry.getValue()));
    }

    /**
     * Return true if the {@link ITable} contains all the column describes in the given {@link List} (case sensible).
     *
     * @param columnList {@link List} containing the columns with the column name as key and the column type as value.
     *
     * @return True if the columns are found, false otherwise.
     */
    default boolean hasColumns(List<String> columnList){
        return getColumnNames().containsAll(columnList);
    }

    /**
     * Return the count of columns.
     *
     * @return The count of columns.
     */
    default int getColumnCount(){
        return getColumnNames().size();
    }

    /**
     * Return the count of lines or -1 if not able to find the {@link ITable}.
     *
     * @return The count of lines or -1 if not able to find the {@link ITable}.
     */
    int getRowCount();

    /**
     * Return true if the {@link ITable} is empty (no lines), false otherwise.
     *
     * @return True if the {@link ITable} is empty (no lines), false otherwise.
     */
    default boolean isEmpty(){
        return getRowCount() == 0;
    }

    /**
     * Return a {@link Collection} of all the unique values of the {@link ITable}. This method can take a lot of time and
     * resources according the the table size. If no values are found, return an empty collection. If an error occurred,
     * return null.
     *
     * @param column Name of the column to request.
     *
     * @return A {@link Collection} of all the unique values of the {@link ITable}.
     */
    Collection<String> getUniqueValues(String column);

    /**
     * Save the {@link ITable} into a file.
     *
     * @param filePath Path of the file to be saved.
     *
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
     *
     * @return True is the file has been saved, false otherwise.
     */
    boolean save(String filePath, String encoding);
    
}
