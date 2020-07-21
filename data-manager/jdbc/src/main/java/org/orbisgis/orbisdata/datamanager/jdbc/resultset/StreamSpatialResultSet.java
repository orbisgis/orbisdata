/*
 * Bundle DataManager is part of the OrbisGIS platform
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
 * DataManager is distributed under LGPL 3 license.
 *
 * Copyright (C) 2018 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * DataManager is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * DataManager is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * DataManager. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.orbisdata.datamanager.jdbc.resultset;

import org.h2gis.utilities.SpatialResultSet;
import org.locationtech.jts.geom.Geometry;
import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.commons.annotations.Nullable;
import org.orbisgis.commons.utilities.CheckUtils;
import org.orbisgis.orbisdata.datamanager.api.dataset.ITable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.Map;

/**
 * Wrapper of {@link ResultSet} used to simplified the usage of {@link ITable#stream()}, avoiding the usage of
 * try/catch.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC)
 */
public class StreamSpatialResultSet extends StreamResultSet implements SpatialResultSet {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamSpatialResultSet.class);

    /**
     * Internal {@link ResultSet}.
     */
    private final SpatialResultSet resultSet;

    public StreamSpatialResultSet(@NotNull SpatialResultSet resultSet){
        super(resultSet);
        CheckUtils.checkNotNull(resultSet, "The given ResultSet should not be null.");
        this.resultSet = resultSet;
    }

    @Override
    public Geometry getGeometry(int i) {
        try {
            return resultSet.getGeometry(i);
        } catch (SQLException e) {
            LOGGER.error("Unable to get the geometry at index '" + i + "'.", e);
        }
        return null;
    }

    @Override
    public Geometry getGeometry(String s) {
        try {
            return resultSet.getGeometry(s);
        } catch (SQLException e) {
            LOGGER.error("Unable to get the geometry at index '" + s + "'.", e);
        }
        return null;
    }

    @Override
    public Geometry getGeometry() {
        try {
            return resultSet.getGeometry();
        } catch (SQLException e) {
            LOGGER.error("Unable to get the geometry.", e);
        }
        return null;
    }

    @Override
    public void updateGeometry(int i, Geometry geometry) throws SQLException {
        resultSet.updateGeometry(i, geometry);
    }

    @Override
    public void updateGeometry(String s, Geometry geometry) throws SQLException {
        resultSet.updateGeometry(s, geometry);
    }
}
