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

import org.h2gis.utilities.SpatialResultSet;
import org.locationtech.jts.geom.Geometry;

import java.sql.SQLException;

/**
 * Interface for wrapper of {@link SpatialResultSet} used to simplified the usage of {@link ISpatialTable#stream()}, avoiding the usage of
 * try/catch.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC)
 */
public interface IStreamSpatialResultSet extends IStreamResultSet, SpatialResultSet {

    /**
     * Returns the {@link SpatialResultSet} used for streaming.
     * @return The {@link SpatialResultSet} used for streaming.
     */
    SpatialResultSet getSpatialResultSet();

    @Override
    default Geometry getGeometry(int i) {
        try {
            return getSpatialResultSet().getGeometry(i);
        } catch (SQLException e) {
            getLogger().error("Unable to get the geometry at index '" + i + "'.", e);
        }
        return null;
    }

    @Override
    default Geometry getGeometry(String s) {
        try {
            return getSpatialResultSet().getGeometry(s);
        } catch (SQLException e) {
            getLogger().error("Unable to get the geometry at index '" + s + "'.", e);
        }
        return null;
    }

    @Override
    default Geometry getGeometry() {
        try {
            return getSpatialResultSet().getGeometry();
        } catch (SQLException e) {
            getLogger().error("Unable to get the geometry.", e);
        }
        return null;
    }

    @Override
    default void updateGeometry(int i, Geometry geometry) throws SQLException {
        getSpatialResultSet().updateGeometry(i, geometry);
    }

    @Override
    default void updateGeometry(String s, Geometry geometry) throws SQLException {
        getSpatialResultSet().updateGeometry(s, geometry);
    }
}
