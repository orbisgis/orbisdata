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
 * Implementation of the IDataSet interface. A table is a 2D (column/line) representation of data.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2018-2019)
 */
public interface ITable extends IDataSet {

    /**
     * Apply the given closure to each row.
     *
     * @param closure Closure to apply to each row.
     */
    void eachRow(Closure closure);
    
    /**
     * Get all column names from the underlying table
     *
     * @return A collection containing the name of the columns
     */
    Collection<String> getColumnNames();

    /**
     * Return true if the table contains a column with the given name with the given type.
     *
     * @param columnName Name of the column to check.
     * @param clazz Class of the column to check.
     *
     * @return True if the column is found, false otherwise.
     */
    boolean hasColumn(String columnName, Class clazz);

    /**
     * Return true if the table contains a column with the given name
     *
     * @param columnName Name of the column to check.
     *
     * @return True if the column is found, false otherwise.
     */
    boolean hasColumn(String columnName);

    /**
     * Return true if the table contains all the column describes in the given Map.
     *
     * @param columnMap Map containing the columns with the column name as key and the column type as value.
     *
     * @return True if the columns are found, false otherwise.
     */
    boolean hasColumns(Map<String, Class> columnMap);

    /**
     * Return true if the table contains all the column describes in the given List.
     *
     * @param columnList List containing the columns with the column name as key and the column type as value.
     *
     * @return True if the columns are found, false otherwise.
     */
    boolean hasColumns(List<String> columnList);

    /**
     * Return the count of columns.
     *
     * @return The count of columns.
     */
    int getColumnCount();

    /**
     * Return the count of lines or -1 if not able to find the table.
     *
     * @return The count of lines or -1 if not able to find the table.
     */
    int getRowCount();

    /**
     * Return true if the table is empty (no lines), false otherwise.
     *
     * @return True if the table is empty (no lines), false otherwise.
     */
    boolean isEmpty();

    /**
     * Return a collection of all the unique values of the table. This method can take a lot of time and resources
     * according the the table size. If no values are found, return an epty collection. If an error occurred, return
     * null.
     *
     * @param column Name of the column to request.
     *
     * @return A collection of all the unique values of the table.
     */
    Collection<String> getUniqueValues(String column);

    /**
     * Save the table to a file
     * @param filePath the path of the file to be saved
     * @return true is the file has been saved
     */
    boolean save(String filePath);

    /**
     * Save the table to a file
     * @param filePath the path of the file to be saved
     * @param encoding Encoding property.
     * @return true is the file has been saved
     */
    boolean save(String filePath, String encoding);
    
}
