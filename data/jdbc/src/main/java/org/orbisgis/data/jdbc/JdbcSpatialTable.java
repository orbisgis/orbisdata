/*
 * Bundle JDBC is part of the OrbisGIS platform
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
 * JDBC is distributed under LGPL 3 license.
 *
 * Copyright (C) 2018 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * JDBC is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * JDBC is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * JDBC. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.data.jdbc;

import org.h2gis.utilities.*;
import org.h2gis.utilities.dbtypes.DBTypes;
import org.locationtech.jts.geom.Geometry;
import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.commons.annotations.Nullable;
import org.orbisgis.data.api.dataset.IJdbcSpatialTable;
import org.orbisgis.data.api.dataset.IRaster;
import org.orbisgis.data.api.datasource.IJdbcDataSource;
import org.orbisgis.data.jdbc.resultset.ResultSetSpliterator;
import org.orbisgis.data.jdbc.resultset.StreamSpatialResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Contains the methods which are in common to all the IJdbcTable subclasses.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public abstract class JdbcSpatialTable extends JdbcTable<StreamSpatialResultSet> implements IJdbcSpatialTable<StreamSpatialResultSet> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcTable.class);

    /**
     * Main constructor.
     *
     * @param dataBaseType   Type of the DataBase where this table comes from.
     * @param tableLocation  TableLocation that identify the represented table.
     * @param baseQuery      Query for the creation of the ResultSet
     * @param params         Parameters fo the query.
     * @param statement      Statement used to request the database.
     * @param jdbcDataSource DataSource to use for the creation of the resultSet.
     */
    public JdbcSpatialTable(@NotNull DBTypes dataBaseType, @NotNull IJdbcDataSource jdbcDataSource,
                            @Nullable TableLocation tableLocation, @NotNull Statement statement,
                            @NotNull String baseQuery, @Nullable List <Object> params) {
        super(dataBaseType, jdbcDataSource, tableLocation, statement, params, baseQuery);
    }

    @Override
    public boolean isSpatial() {
        return true;
    }

    @Override
    public Geometry getGeometry(int columnIndex) {
        try {
            SpatialResultSet rs = (SpatialResultSet)getResultSet();
            if(rs != null) {
                return rs.getGeometry(columnIndex);
            }
        } catch (SQLException e) {
            LOGGER.error("Unable to get the geometry at '" + columnIndex + "'.", e);
        }
        return null;
    }

    @Override
    public Geometry getGeometry(@NotNull String columnLabel) {
        try {
            SpatialResultSet rs = (SpatialResultSet)getResultSet();
            if(rs != null) {
                return rs.getGeometry(columnLabel);
            }
        } catch (SQLException e) {
            LOGGER.error("Unable to get the geometry of '" + columnLabel + "'.", e);
        }
        return null;
    }

    @Override
    public Geometry getGeometry() {
        try {
            SpatialResultSet rs = (SpatialResultSet)getResultSet();
            if(rs != null) {
                return rs.getGeometry();
            }
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
    public IRaster getRaster(@NotNull String columnLabel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IRaster getRaster() {
        throw new UnsupportedOperationException();
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
        if (getTableLocation() == null) {
            try {
                ResultSet rs = getResultSet();
                if(rs != null) {
                    return new ArrayList<>(GeometryTableUtilities.getGeometryColumnNames(rs.getMetaData()));
                }
            } catch (SQLException e) {
                LOGGER.error("Unable to get the geometric columns on ResultSet.", e);
            }
        } else {
            try {
                Connection con = getJdbcDataSource().getConnection();
                if(con == null){
                    LOGGER.error("Unable to get connection for the geometric columns.");
                    return null;
                }
                return new ArrayList<>(GeometryTableUtilities.getGeometryColumnNames(con, getTableLocation()));
            } catch (SQLException e) {
                LOGGER.error("Unable to get the geometric columns.", e);
            }
        }
        return null;
    }

    @Override
    public Geometry getExtent(String[] geometryColumns, String filter) {
        if (getTableLocation() == null) {
            try {
                Connection con = getJdbcDataSource().getConnection();
                if(con == null){
                    LOGGER.error("Unable to get connection for the geometric field.");
                    return null;
                }
                return GeometryTableUtilities.getEnvelope(con, getBaseQuery(), geometryColumns, filter);
            } catch (SQLException e) {
                LOGGER.error("Unable to get the table estimated extend.", e);
            }
        }
        else{
            try {
                Connection con = getJdbcDataSource().getConnection();
                if(con == null){
                    LOGGER.error("Unable to get connection for the geometric field.");
                    return null;
                }
                return GeometryTableUtilities.getEnvelope(con, getTableLocation(), geometryColumns, filter);
            } catch (SQLException e) {
                LOGGER.error("Unable to get the table estimated extend.", e);
            }
        }
        return null;
    }

    @Override
    public Geometry getExtent(String... geometryColumns) {
        if (getTableLocation() == null) {
            try {
                Connection con = getJdbcDataSource().getConnection();
                if(con == null){
                    LOGGER.error("Unable to get connection for the geometric field.");
                    return null;
                }
                return GeometryTableUtilities.getEnvelope(con, getBaseQuery(), geometryColumns);
            } catch (SQLException e) {
                LOGGER.error("Unable to get the table estimated extend.", e);
            }
        }
        else{
            try {
                Connection con = getJdbcDataSource().getConnection();
                if(con == null){
                    LOGGER.error("Unable to get connection for the geometric field.");
                    return null;
                }
                return GeometryTableUtilities.getEnvelope(con, getTableLocation(), geometryColumns);
            } catch (SQLException e) {
                LOGGER.error("Unable to get the table estimated extend.", e);
            }
        }
        return null;
    }

    @Override
    public Geometry getExtent() {
        if (getTableLocation() == null) {
            try {
                Connection con = getJdbcDataSource().getConnection();
                if(con == null){
                    LOGGER.error("Unable to get connection for the geometric field.");
                    return null;
                }
                ResultSet rs0 = getResultSet();
                if(rs0 == null) {
                    LOGGER.error("Unable to get the ResultSet.");
                    return null;
                }
                Tuple<String, Integer> geomMeta=null;
                try {
                    geomMeta = GeometryTableUtilities.getFirstGeometryColumnNameAndIndex(con, getTableLocation());
                } catch (SQLException e) {
                    LOGGER.error("There is no geometric field.", e);
                }
                return GeometryTableUtilities.getEnvelope(rs0, geomMeta.first());

            } catch (SQLException e) {
                LOGGER.error("Unable to get the table estimated extend on ResultSet.", e);
            }
        } else {
            try {
                Connection con = getJdbcDataSource().getConnection();
                if(con == null){
                    LOGGER.error("Unable to get connection for the geometric field.");
                    return null;
                }
                Tuple<String, Integer> geomMeta = null;
                try {
                    geomMeta = GeometryTableUtilities.getFirstGeometryColumnNameAndIndex(con, getTableLocation());
                } catch (SQLException e) {
                    LOGGER.error("There is no geometric field.", e);
                }
                return GeometryTableUtilities.getEnvelope(con, getTableLocation(), geomMeta.first());
            } catch (SQLException e) {
                LOGGER.error("Unable to get the table estimated extend.", e);
            }
        }
        return null;
    }

    @Override
    public Geometry getEstimatedExtent() {
        if (getTableLocation() == null) {
            throw new UnsupportedOperationException();
        }
        try {
            Connection con = getJdbcDataSource().getConnection();
            if(con == null){
                LOGGER.error("Unable to get connection for the geometric field.");
                return null;
            }
            Tuple<String, Integer> geomMeta=null;
            try {
                geomMeta = GeometryTableUtilities.getFirstGeometryColumnNameAndIndex(con, getTableLocation());
            } catch (SQLException e) {
                LOGGER.error("There is no geometric field.");
            }
            return GeometryTableUtilities.getEstimatedExtent(con, getTableLocation(), geomMeta.first());
        } catch (SQLException e) {
            LOGGER.error("Unable to get the table estimated extend.", e);
        }
        return null;
    }


    @Override
    public void setSrid(int srid) {
        List<String> geomColumns = getGeometricColumns();
        if (getTableLocation() == null || geomColumns.isEmpty()) {
            throw new UnsupportedOperationException();
        }
        try {
            Connection con = getJdbcDataSource().getConnection();
            if(con == null){
                LOGGER.error("Unable to set connection for the table SRID.");
            }
            String geomColumn = getGeometricColumns().get(0);
            String type = getColumnType(geomColumn);
            con.createStatement().execute(
                    "ALTER TABLE "+getLocation()+" ALTER COLUMN "+geomColumn+" TYPE geometry("+type+", "+srid+") USING ST_SetSRID("+geomColumn+","+srid+");");
        } catch (SQLException e) {
            LOGGER.error("Unable to set the table SRID.", e);
        }
    }

    @Override
    public Map<String, String> getGeometryTypes() {
        if (getTableLocation() == null) {
            try {
                Map<String, String> map = new HashMap<>();
                ResultSet rs = getResultSet();
                if(rs == null){
                    LOGGER.error("Unable to get the ResultSet.");
                    return null;
                }
                GeometryTableUtilities.getMetaData(getResultSet())
                        .forEach((key, value) -> map.put(key, value.getGeometryType()));
                return map;
            } catch (SQLException e) {
                LOGGER.error("Unable to get the metadata of the query.", e);
            }
        }
        try {
            Map<String, String> map = new HashMap<>();
            Connection con = getJdbcDataSource().getConnection();
            if(con == null){
                LOGGER.error("Unable to get connection for the geometry types.");
                return null;
            }
            GeometryTableUtilities.getMetaData(con, getTableLocation())
                    .forEach((s, meta) -> map.put(s, meta.getGeometryType()));
            return map;
        } catch (SQLException e) {
            LOGGER.error("Unable to get the geometry types.", e);
            return null;
        }
    }

    @Override
    public SpatialResultSetMetaData getMetaData() {
        try {
            ResultSet rs = getResultSet();
            if(rs == null){
                LOGGER.error("Unable to get the ResultSet.");
                return null;
            }
            return rs.getMetaData().unwrap(SpatialResultSetMetaData.class);
        } catch (SQLException e) {
            LOGGER.error("Unable to get the metadata.", e);
            return null;
        }
    }

    @Override
    public Iterator<StreamSpatialResultSet> iterator() {
        return new ResultSetIterator(this);
    }

    @Nullable
    @Override
    public Stream<StreamSpatialResultSet> stream() {
        Spliterator<StreamSpatialResultSet> spliterator = new ResultSetSpliterator<>(this.getRowCount(), new StreamSpatialResultSet((SpatialResultSet)getResultSet()));
        return StreamSupport.stream(spliterator, true);
    }
}
