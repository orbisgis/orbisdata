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
 * Copyright (C) 2018 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.datamanagerapi.dsl;


import groovy.lang.Closure;
import org.orbisgis.datamanagerapi.dataset.ISpatialTable;
import org.orbisgis.datamanagerapi.dataset.ITable;

/**
 * Define the methods use to get the result of a SQL request built throw the {@link org.orbisgis.datamanagerapi.dsl}
 * package interfaces.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public interface IBuilderResult {

    /**
     * Apply the given closure on each row of the result of the SQL request.
     *
     * @param closure Closure to apply to each row.
     */
    void eachRow(Closure closure);

    /**
     * Convert the result of the SQL request into a ITable or ISpatialTable.
     *
     * @param clazz New class of the result.
     *
     * @return The result wrapped into the given class.
     */
    Object asType(Class clazz);

    /**
     * Return the {@link ITable} representing the result of the SQL query.
     *
     * @return The {@link ITable} representing the result of the SQL query.
     */
    ITable getTable();

    /**
     * Return the {@link ISpatialTable} representing the result of the SQL query.
     *
     * @return The {@link ISpatialTable} representing the result of the SQL query.
     */
    ISpatialTable getSpatialTable();
}
