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

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.commons.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Extension of {@link ITable}. A {@link ISpatialTable} is a specialisation with at least one Geometry column.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (Lab-STICC UBS 2018-2019)
 */
public interface ISpatialTable<T> extends ITable<T> {

    /**
     * Returns the {@link Geometry} value of the given column at the current row.
     *
     * @param columnIndex Index of the geometric column.
     * @return The {@link Geometry} value of the given column at the current row.
     */
    @Nullable
    Geometry getGeometry(int columnIndex);

    /**
     * Returns the {@link Geometry} value of the given column at the current row.
     *
     * @param columnLabel Label of the geometric column.
     * @return The {@link Geometry} value of the given column at the current row.
     */
    @Nullable
    Geometry getGeometry(@NotNull String columnLabel);

    /**
     * Returns the {@link Geometry} value of the first geometric column at the current row.
     *
     * @return The {@link Geometry} value of the first geometric column at the current row.
     */
    @Nullable
    Geometry getGeometry();

    /**
     * Returns the {@link IRaster} value of the given column at the current row.
     *
     * @param columnIndex Index of the raster column.
     * @return The {@link IRaster} value of the given column at the current row.
     */
    @Nullable
    IRaster getRaster(int columnIndex);

    /**
     * Returns the {@link IRaster} value of the given column at the current row.
     *
     * @param columnLabel Label of the raster column.
     * @return The {@link IRaster} value of the given column at the current row.
     */
    @Nullable
    IRaster getRaster(@NotNull String columnLabel);

    /**
     * Returns the {@link IRaster} value of the first raster column at the current row.
     *
     * @return The {@link IRaster} value of the first raster column at the current row.
     */
    @Nullable
    IRaster getRaster();

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
     * Return the full extend {@link Geometry} of the first geometry column of the table.
     *
     * @return The full extend {@link Geometry} of the first geometry column of the table.
     */
    @Nullable
    Envelope getExtend();

    /**
     * Return the estimated extend {@link Geometry} of the first geometry column of the table.
     *
     * @return The estimated extend {@link Geometry} of the first geometry column of the table.
     */
    @Nullable
    Geometry getEstimatedExtend();

    /**
     * Return the SRID code of the first geometry column of the table.
     *
     * @return The SRID code of the first geometry column of the table.
     */
    int getSrid();

    /**
     * Returns a {@link Map} containing the field names as key and the SFS geometry type (well known name) as value.
     *
     * @return The field names as key and geometry types as value.
     */
    @Nullable
    Map<String, String> getGeometryTypes();

    /**
     * Reproject the current {@link ISpatialTable} to another referenced coordinate system .The reprojection is
     * applied on the first geometry column.
     *
     * @param srid EPSG code as specified by the EPSG spatial reference system database.
     * @return A reproject {@link ISpatialTable}.
     */
    @Nullable
    ISpatialTable<T> reproject(int srid);
}
