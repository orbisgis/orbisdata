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
package org.orbisgis.orbisdata.datamanager.jdbc;

import org.h2gis.utilities.SFSUtilities;
import org.h2gis.utilities.SpatialResultSet;
import org.h2gis.utilities.SpatialResultSetMetaData;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.orbisgis.orbisdata.datamanager.api.dataset.DataBaseType;
import org.orbisgis.orbisdata.datamanager.api.dataset.IJdbcSpatialTable;
import org.orbisgis.orbisdata.datamanager.api.dataset.IRaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains the methods which are in common to all the IJdbcTable subclasses.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public abstract class JdbcSpatialTable extends JdbcTable implements IJdbcSpatialTable {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcTable.class);

    /**
     * Main constructor.
     *
     * @param dataBaseType   Type of the DataBase where this table comes from.
     * @param tableLocation  TableLocation that identify the represented table.
     * @param baseQuery      Query for the creation of the ResultSet
     * @param statement      Statement used to request the database.
     * @param jdbcDataSource DataSource to use for the creation of the resultSet.
     */
    public JdbcSpatialTable(DataBaseType dataBaseType, JdbcDataSource jdbcDataSource, TableLocation tableLocation,
                            Statement statement, String baseQuery) {
        super(dataBaseType, jdbcDataSource, tableLocation, statement, baseQuery);
    }

    @Override
    public boolean isSpatial() {
        return true;
    }

    @Override
    public Geometry getGeometry(int columnIndex) {
        try {
            return ((SpatialResultSet) getResultSet()).getGeometry(columnIndex);
        } catch (SQLException e) {
            LOGGER.error("Unable to get the geometry at '" + columnIndex + "'.", e);
        }
        return null;
    }

    @Override
    public Geometry getGeometry(String columnLabel) {
        try {
            return ((SpatialResultSet) getResultSet()).getGeometry(columnLabel);
        } catch (SQLException e) {
            LOGGER.error("Unable to get the geometry of '" + columnLabel + "'.", e);
        }
        return null;
    }

    @Override
    public Geometry getGeometry() {
        try {
            return ((SpatialResultSet) getResultSet()).getGeometry();
        } catch (SQLException e) {
            LOGGER.error("Unable to get the geometry.", e);
        }
        return null;
    }

    @Override
    public IRaster getRaster(int columnIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IRaster getRaster(String columnLabel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IRaster getRaster() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> getSpatialColumns() {
        List<String> list = new ArrayList<>();
        list.addAll(getRasterColumns());
        list.addAll(getGeometricColumns());
        return list;
    }

    @Override
    public List<String> getRasterColumns() {
        return new ArrayList<>();
    }

    @Override
    public List<String> getGeometricColumns() {
        if (getTableLocation() == null) {
            try {
                return SFSUtilities.getGeometryFields(getResultSet());
            } catch (SQLException e) {
                LOGGER.error("Unable to get the geometric columns on ResultSet.", e);
            }
        } else {
            try {
                return SFSUtilities.getGeometryFields(getJdbcDataSource().getConnection(), getTableLocation());
            } catch (SQLException e) {
                LOGGER.error("Unable to get the geometric columns.", e);
            }
        }
        return null;
    }

    @Override
    public Envelope getExtend() {
        if (getTableLocation() == null) {
            try {
                Connection conn = getJdbcDataSource().getConnection();
                List<String> names = SFSUtilities.getGeometryFields(getResultSet());
                if (names.isEmpty()) {
                    LOGGER.error("There is no geometric field.");
                    return null;
                }

                ResultSet rs = conn.createStatement().executeQuery("SELECT ST_Extent(" +
                        TableLocation.quoteIdentifier(names.get(0)) + ") ext FROM SELECT" + getBaseQuery());
                if (rs.next()) {
                    return ((Geometry) rs.getObject(1)).getEnvelopeInternal();
                } else {
                    throw new SQLException("Unable to get the table extent it may be empty");
                }
            } catch (SQLException e) {
                LOGGER.error("Unable to get the table estimated extend on ResultSet.", e);
            }
        } else {
            try {
                Connection conn = getJdbcDataSource().getConnection();
                List<String> names = SFSUtilities.getGeometryFields(conn, getTableLocation());
                if (names.isEmpty()) {
                    LOGGER.error("There is no geometric field.");
                    return null;
                }
                return SFSUtilities.getTableEnvelope(conn, getTableLocation(), names.get(0));
            } catch (SQLException e) {
                LOGGER.error("Unable to get the table estimated extend.", e);
            }
        }
        return null;
    }

    @Override
    public Geometry getEstimatedExtend() {
        if (getTableLocation() == null) {
            throw new UnsupportedOperationException();
        }
        try {
            Connection conn = getJdbcDataSource().getConnection();
            List<String> names = SFSUtilities.getGeometryFields(conn, getTableLocation());
            if (names.isEmpty()) {
                LOGGER.error("There is no geometric field.");
                return null;
            }
            return SFSUtilities.getEstimatedExtent(conn, getTableLocation(), names.get(0));
        } catch (SQLException e) {
            LOGGER.error("Unable to get the table estimated extend.", e);
        }
        return null;
    }

    @Override
    public int getSrid() {
        if (getTableLocation() == null) {
            throw new UnsupportedOperationException();
        }
        try {
            return SFSUtilities.getSRID(getJdbcDataSource().getConnection(), getTableLocation());
        } catch (SQLException e) {
            LOGGER.error("Unable to get the table SRID.", e);
        }
        return -1;
    }

    @Override
    public Map<String, String> getGeometryTypes() {
        if (getTableLocation() == null) {
            try {
                boolean isH2 = getDbType() == DataBaseType.H2GIS;
                Map<String, String> map = new HashMap<>();
                ResultSetMetaData metaData = getResultSet().getMetaData();
                for (int i = 0; i < metaData.getColumnCount(); i++) {
                    String type;
                    if (isH2) {
                        type = SFSUtilities.getGeometryTypeNameFromCode(metaData.getColumnType(i));
                    } else {
                        type = metaData.getColumnTypeName(i).toLowerCase();
                    }
                    map.put(metaData.getColumnName(i), type);
                }
                return map;
            } catch (SQLException e) {
                LOGGER.error("Unable to get the metadata of the query.", e);
            }
        }
        try {
            Map<String, String> map = new HashMap<>();
            PreparedStatement geomStatement = SFSUtilities.prepareInformationSchemaStatement(getJdbcDataSource().getConnection(), getTableLocation().getCatalog(), getTableLocation().getSchema(),
                    getTableLocation().getTable(), "geometry_columns", "");
            ResultSet geomResultSet = geomStatement.executeQuery();
            boolean isH2 = getDbType() == DataBaseType.H2GIS;
            while (geomResultSet.next()) {
                String fieldName = geomResultSet.getString("F_GEOMETRY_COLUMN");
                String type;
                if (isH2) {
                    type = SFSUtilities.getGeometryTypeNameFromCode(geomResultSet.getInt("GEOMETRY_TYPE"));
                } else {
                    type = geomResultSet.getString("type").toLowerCase();
                }
                map.put(fieldName, type);
            }
            return map;
        } catch (SQLException e) {
            LOGGER.error("Unable to get the geometry types.", e);
            return null;
        }
    }

    @Override
    public SpatialResultSetMetaData getMetaData() {
        try {
            return getResultSet().getMetaData().unwrap(SpatialResultSetMetaData.class);
        } catch (SQLException e) {
            LOGGER.error("Unable to get the metadata.", e);
            return null;
        }
    }
}
