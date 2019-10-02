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

import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.commons.annotations.Nullable;

/**
 * Interface describing the location of a {@link ITable} under the pattern 'dataSource : catalog.schema.table'
 */
public interface ITableLocation {

    /**
     * Return the name of the table of the {@link ITable}.
     * @return The name of the table
     */
    @NotNull
    String getTable();

    /**
     * Returns the schema of the {@link ITable} if exists, otherwise, return null.
     * @return The schema or null.
     */
    @Nullable
    String getSchema();

    /**
     * Returns the catalog of the {@link ITable} if exists, otherwise, return null.
     * @return The catalog or null.
     */
    @Nullable
    String getCatalog();

    /**
     * Return the name of the dataSource of the {@link ITable}.
     * @return The name of the dataSource
     */
    @NotNull
    String getDataSource();

    /**
     * Return the String representation of the table location for the given database type.
     * @param type Type of teh database.
     * @return The String representation of the table location.
     */
    @NotNull
    String toString(@Nullable DataBaseType type);
}
