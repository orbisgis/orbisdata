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

import org.locationtech.jts.geom.Geometry;
import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.commons.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Cached metadata of a {@link ISpatialTable}.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Chaire GEOTERA 2020)
 */
public interface ISpatialTableMetaData extends ITableMetaData {

    @Override
    default boolean isSpatial() {
        return true;
    }

    /**
     * Return the SRID code of the first geometry column of the {@link ISpatialTable}.
     *
     * @return The SRID code of the first geometry column of the {@link ISpatialTable}.
     */
    int getSrid();

    /**
     * Return the list of the table spatial columns.
     *
     * @return The list of the table spatial columns.
     */
    @Nullable
    List<String> getSpatialColumns();

    /**
     * Return the list of the table raster columns.
     *
     * @return The list of the table raster columns.
     */
    @NotNull
    List<String> getRasterColumns();

    /**
     * Return the list of the table geometric columns.
     *
     * @return The list of the table geometric columns.
     */
    @Nullable
    List<String> getGeometricColumns();

    /**
     * Return the full extent {@link Geometry} of the first geometry column of the table.
     *
     * @return The full extent {@link Geometry} of the first geometry column of the table.
     */
    @Nullable
    Geometry getExtent();

    /**
     * Return the estimated extent {@link Geometry} of the first geometry column of the table.
     *
     * @return The estimated extent {@link Geometry} of the first geometry column of the table.
     */
    @Nullable
    Geometry getEstimatedExtent();

    /**
     * Returns a {@link Map} containing the field names as key and the SFS geometry type (well known name) as value.
     *
     * @return The field names as key and geometry types as value.
     */
    @Nullable
    Map<String, String> getGeometryTypes();
}
