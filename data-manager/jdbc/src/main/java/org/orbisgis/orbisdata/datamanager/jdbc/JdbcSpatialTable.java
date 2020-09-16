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

import org.h2gis.utilities.GeometryTableUtilities;
import org.h2gis.utilities.SpatialResultSet;
import org.h2gis.utilities.SpatialResultSetMetaData;
import org.h2gis.utilities.Tuple;
import org.locationtech.jts.geom.Geometry;
import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.commons.annotations.Nullable;
import org.orbisgis.orbisdata.datamanager.api.dataset.*;
import org.orbisgis.orbisdata.datamanager.api.datasource.IJdbcDataSource;
import org.orbisgis.orbisdata.datamanager.api.metadata.IJdbcSpatialTableMetaData;
import org.orbisgis.orbisdata.datamanager.jdbc.metadata.JdbcSpatialTableMetaData;
import org.orbisgis.orbisdata.datamanager.jdbc.resultset.ResultSetSpliterator;
import org.orbisgis.orbisdata.datamanager.jdbc.resultset.StreamSpatialResultSet;
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
public abstract class JdbcSpatialTable extends JdbcTable<SpatialResultSet, StreamSpatialResultSet>
        implements IJdbcSpatialTable<StreamSpatialResultSet> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcTable.class);

    /**
     * MetaData.
     */
    private IJdbcSpatialTableMetaData metaData = null;

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
    public JdbcSpatialTable(@NotNull DataBaseType dataBaseType, @NotNull IJdbcDataSource jdbcDataSource,
                            @Nullable TableLocation tableLocation, @NotNull Statement statement,
                            @NotNull String baseQuery, @Nullable List <Object> params) {
        super(dataBaseType, jdbcDataSource, tableLocation, statement, params, baseQuery);
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
    public IJdbcSpatialTableMetaData getMetaData() {
        if(metaData == null) {
            try {
                ResultSet rs = getResultSet();
                if (rs == null) {
                    LOGGER.error("Unable to get the ResultSet.");
                    return null;
                }
                SpatialResultSetMetaData rsMetaData = rs.getMetaData().unwrap(SpatialResultSetMetaData.class);
                if(rsMetaData != null) {
                    metaData = new JdbcSpatialTableMetaData(getLocation(), getName(), calculateRowCount(),
                            rsMetaData, getJdbcDataSource(), rs);
                }
            } catch (SQLException e) {
                LOGGER.error("Unable to get the metadata.", e);
                return null;
            }
        }
        return metaData;
    }

    @Override
    public Iterator<SpatialResultSet> iterator() {
        return new ResultSetIterator(this);
    }

    @Nullable
    @Override
    public Stream<StreamSpatialResultSet> stream() {
        Spliterator<StreamSpatialResultSet> spliterator =
                new ResultSetSpliterator<>(this.getRowCount(), new StreamSpatialResultSet((SpatialResultSet)getResultSet()));
        return StreamSupport.stream(spliterator, true);
    }
}
