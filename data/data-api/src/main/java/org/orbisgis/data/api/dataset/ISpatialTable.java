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
package org.orbisgis.data.api.dataset;

import org.locationtech.jts.geom.Geometry;

import java.util.List;
import java.util.Map;

/**
 * Extension of {@link ITable}. A {@link ISpatialTable} is a specialisation with at least one Geometry column.
 *
 * @param <T> The type of elements returned by the iterator.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (Lab-STICC UBS 2018-2019)
 */
public interface ISpatialTable<T> extends ITable<T, T> {

    /**
     * Returns the {@link Geometry} value of the given column at the current row.
     *
     * @param columnIndex Index of the geometric column.
     * @return The {@link Geometry} value of the given column at the current row.
     */
    Geometry getGeometry(int columnIndex) throws Exception;

    /**
     * Returns the {@link Geometry} value of the given column at the current row.
     *
     * @param columnLabel Label of the geometric column.
     * @return The {@link Geometry} value of the given column at the current row.
     */
    Geometry getGeometry(String columnLabel) throws Exception;

    /**
     * Returns the {@link Geometry} value of the first geometric column at the current row.
     *
     * @return The {@link Geometry} value of the first geometric column at the current row.
     */
    Geometry getGeometry() throws Exception;

    /**
     * Returns the {@link IRaster} value of the given column at the current row.
     *
     * @param columnIndex Index of the raster column.
     * @return The {@link IRaster} value of the given column at the current row.
     */
    IRaster getRaster(int columnIndex) throws Exception;

    /**
     * Returns the {@link IRaster} value of the given column at the current row.
     *
     * @param columnLabel Label of the raster column.
     * @return The {@link IRaster} value of the given column at the current row.
     */
    IRaster getRaster(String columnLabel) throws Exception;

    /**
     * Returns the {@link IRaster} value of the first raster column at the current row.
     *
     * @return The {@link IRaster} value of the first raster column at the current row.
     */
    IRaster getRaster() throws Exception;

    /**
     * Return the list of the table spatial columns.
     *
     * @return The list of the table spatial columns.
     */
    List<String> getSpatialColumns() throws Exception;

    /**
     * Return the list of the table raster columns.
     *
     * @return The list of the table raster columns.
     */
    List<String> getRasterColumns() throws Exception;

    /**
     * Return the list of the table geometric columns.
     *
     * @return The list of the table geometric columns.
     */
    List<String> getGeometricColumns() throws Exception;

    /**
     * Return the full extent {@link Geometry} of a list of geometry columns.
     *
     * Note that a geometry column can be a geometry function
     * e.g. ST_Buffer(the_geom, 20)
     *
     * @return The full extent {@link Geometry}.
     */
    Geometry getExtent(String... geometryColumns) throws Exception;

    /**
     * Return the full extent {@link Geometry} of a list of geometry columns
     * and a filter
     *
     * The filter depends to the input datasource
     * e.g. {@code WHERE ID>12} for a SQL database
     *
     * Note that a geometry column can be a geometry function
     * e.g. {@code ST_Buffer(the_geom, 20)}
     *
     *
     * @return The full extent {@link Geometry}.
     */
    Geometry getExtent(String[] geometryColumns, String filter) throws Exception;

    /**
     * Return the full extent {@link Geometry} of the first geometry column of the table.
     *
     * @return The full extent {@link Geometry} of the first geometry column of the table.
     */
    Geometry getExtent() throws Exception;

    /**
     * Return the estimated extent {@link Geometry} of the first geometry column of the table.
     *
     * @return The estimated extent {@link Geometry} of the first geometry column of the table.
     */
    Geometry getEstimatedExtent() throws Exception;

    /**
     * Return the SRID code of the first geometry column of the {@link ISpatialTable}.
     *
     * @return The SRID code of the first geometry column of the {@link ISpatialTable}.
     */
    int getSrid() throws Exception;

    /**
     * Sets the SRID code of the first geometric column of the {@link ISpatialTable}.
     *
     * @param srid The SRID code of the first geometric column of the {@link ISpatialTable}.
     */
    void setSrid(int srid) throws Exception;

    /**
     * Returns a {@link Map} containing the field names as key and the SFS geometry type (well known name) as value.
     *
     * @return The field names as key and geometry types as value.
     */
    Map<String, String> getGeometryTypes() throws Exception;

    /**
     * Reproject the current {@link ISpatialTable} to another referenced coordinate system .The reprojection is
     * applied on the first geometry column.
     *
     * @param srid EPSG code as specified by the EPSG spatial reference system database.
     * @return A reproject {@link ISpatialTable}.
     */
    ISpatialTable<T> reproject(int srid) throws Exception;
}
