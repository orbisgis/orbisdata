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
import org.orbisgis.datamanagerapi.dsl.IWhereBuilderOrOptionBuilder;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Collection;

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
