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
package org.orbisgis.orbisdata.datamanager.jdbc.metadata;

import org.h2gis.utilities.GeometryTableUtilities;
import org.h2gis.utilities.SpatialResultSetMetaData;
import org.h2gis.utilities.TableLocation;
import org.h2gis.utilities.Tuple;
import org.locationtech.jts.geom.Geometry;
import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.commons.annotations.Nullable;
import org.orbisgis.orbisdata.datamanager.api.datasource.IJdbcDataSource;
import org.orbisgis.orbisdata.datamanager.api.metadata.IJdbcSpatialTableMetaData;
import org.orbisgis.orbisdata.datamanager.jdbc.JdbcDataSource;
import org.orbisgis.orbisdata.datamanager.jdbc.JdbcSpatialTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains the metadata of a {@link JdbcSpatialTable}
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Chaire GEOTERA 2020)
 */
public class JdbcSpatialTableMetaData extends JdbcTableMetaData implements IJdbcSpatialTableMetaData {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcSpatialTableMetaData.class);

    /**
     * Cached and wrapped {@link SpatialResultSetMetaData}.
     */
    private SpatialResultSetMetaData metaData;

    private IJdbcDataSource jdbcDataSource;

    private ResultSet resultSet;

    public JdbcSpatialTableMetaData(String location, String name, int rowCount, SpatialResultSetMetaData metaData,
                                    IJdbcDataSource jdbcDataSource, @Nullable ResultSet resultSet) throws SQLException {
        super(location, name, rowCount, metaData, jdbcDataSource, resultSet);
        this.metaData = metaData;
        this.jdbcDataSource = jdbcDataSource;
        this.resultSet = resultSet;
    }

    @Override
    public Geometry getExtent() {
        if (getLocation() == null) {
            try {
                Connection con = jdbcDataSource.getConnection();
                if(con == null){
                    LOGGER.error("Unable to get connection for the geometric field.");
                    return null;
                }
                if(resultSet == null) {
                    LOGGER.error("Unable to get the ResultSet.");
                    return null;
                }
                Tuple<String, Integer> geomMeta=null;
                try {
                    geomMeta = GeometryTableUtilities.getFirstGeometryColumnNameAndIndex(con, TableLocation.parse(getLocation()));
                } catch (SQLException e) {
                    LOGGER.error("There is no geometric field.", e);
                }
                return GeometryTableUtilities.getEnvelope(resultSet, geomMeta.first());

            } catch (SQLException e) {
                LOGGER.error("Unable to get the table estimated extend on ResultSet.", e);
            }
        } else {
            try {
                Connection con = jdbcDataSource.getConnection();
                if(con == null){
                    LOGGER.error("Unable to get connection for the geometric field.");
                    return null;
                }
                Tuple<String, Integer> geomMeta = null;
                try {
                    geomMeta = GeometryTableUtilities.getFirstGeometryColumnNameAndIndex(con, TableLocation.parse(getLocation()));
                } catch (SQLException e) {
                    LOGGER.error("There is no geometric field.", e);
                }
                return GeometryTableUtilities.getEnvelope(con, TableLocation.parse(getLocation()), geomMeta.first());
            } catch (SQLException e) {
                LOGGER.error("Unable to get the table estimated extend.", e);
            }
        }
        return null;
    }

    @Override
    public Geometry getEstimatedExtent() {
        if (getLocation() == null) {
            throw new UnsupportedOperationException();
        }
        try {
            Connection con = jdbcDataSource.getConnection();
            if(con == null){
                LOGGER.error("Unable to get connection for the geometric field.");
                return null;
            }
            Tuple<String, Integer> geomMeta=null;
            try {
                geomMeta = GeometryTableUtilities.getFirstGeometryColumnNameAndIndex(con, TableLocation.parse(getLocation()));
            } catch (SQLException e) {
                LOGGER.error("There is no geometric field.");
            }
            return GeometryTableUtilities.getEstimatedExtent(con, TableLocation.parse(getLocation()), geomMeta.first());
        } catch (SQLException e) {
            LOGGER.error("Unable to get the table estimated extend.", e);
        }
        return null;
    }

    @Override
    public int getSrid() {
        if (getLocation() == null) {
            throw new UnsupportedOperationException();
        }
        try {
            Connection con = jdbcDataSource.getConnection();
            if(con == null){
                LOGGER.error("Unable to get connection for the table SRID.");
                return -1;
            }
            return GeometryTableUtilities.getSRID(con, TableLocation.parse(getLocation()));
        } catch (SQLException e) {
            LOGGER.error("Unable to get the table SRID.", e);
        }
        return -1;
    }

    @Override
    public List<String> getSpatialColumns() {
        List<String> list = new ArrayList<>(getRasterColumns());
        List<String> geometric = getGeometricColumns();
        if(geometric != null) {
            list.addAll(geometric);
        }
        return list;
    }

    @Override
    @NotNull
    public List<String> getRasterColumns() {
        return new ArrayList<>();
    }

    @Override
    public List<String> getGeometricColumns() {
        if (getLocation() == null) {
            try {
                return new ArrayList<>(GeometryTableUtilities.getGeometryColumnNames(metaData));
            } catch (SQLException e) {
                LOGGER.error("Unable to get the geometric columns on ResultSet.", e);
            }
        } else {
            try {
                Connection con = jdbcDataSource.getConnection();
                if(con == null){
                    LOGGER.error("Unable to get connection for the geometric columns.");
                    return null;
                }
                return new ArrayList<>(GeometryTableUtilities.getGeometryColumnNames(con, TableLocation.parse(getLocation())));
            } catch (SQLException e) {
                LOGGER.error("Unable to get the geometric columns.", e);
            }
        }
        return null;
    }

    @Override
    public Map<String, String> getGeometryTypes() {
        if (getLocation() == null) {
            try {
                Map<String, String> map = new HashMap<>();
                GeometryTableUtilities.getMetaData(resultSet)
                        .forEach((key, value) -> map.put(key, value.getGeometryType()));
                return map;
            } catch (SQLException e) {
                LOGGER.error("Unable to get the metadata of the query.", e);
            }
        }
        try {
            Map<String, String> map = new HashMap<>();
            Connection con = jdbcDataSource.getConnection();
            if(con == null){
                LOGGER.error("Unable to get connection for the geometry types.");
                return null;
            }
            GeometryTableUtilities.getMetaData(con, TableLocation.parse(getLocation()))
                    .forEach((s, meta) -> map.put(s, meta.getGeometryType()));
            return map;
        } catch (SQLException e) {
            LOGGER.error("Unable to get the geometry types.", e);
            return null;
        }
    }

    @Override
    public int getGeometryType(int column) throws SQLException {
        return metaData.getGeometryType(column);
    }

    @Override
    public int getGeometryType() throws SQLException {
        return metaData.getGeometryType();
    }

    @Override
    public int getFirstGeometryFieldIndex() throws SQLException {
        return metaData.getFirstGeometryFieldIndex();
    }
}
