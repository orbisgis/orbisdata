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
 * DataManager API  is distributed under LGPL 3 license.
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
package org.orbisgis.orbisdata.datamanager.api.dataset;

/**
 * Column of a {@link IJdbcTable}.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public interface IJdbcColumn extends IColumn {

    /**
     * Return true if the column contains spatial data.
     *
     * @return True if the column contains spatial data.
     */
    boolean isSpatial();

    /**
     * Return true if the column has an index, false otherwise.
     *
     * @return True if the column has an index, false otherwise.
     */
    boolean isIndexed();

    /**
     * Return true if the column has a spatial index, false otherwise.
     *
     * @return True if the column has a spatial index, false otherwise.
     */
    boolean isSpatialIndexed();

    /**
     * Create an index of the column. If the column already has an index, no new index is created.
     *
     * @return True if an index has been created.
     */
    boolean createIndex();

    /**
     * Create an index of the column. If the column already has an index, no new index is created.
     *
     * @return True if an index has been created.
     */
    boolean createSpatialIndex();

    /**
     * Drop the index of the column if exists.
     */
    void dropIndex();

}
