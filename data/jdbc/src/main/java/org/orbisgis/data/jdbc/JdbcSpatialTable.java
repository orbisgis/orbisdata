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
    public JdbcSpatialTable(DBTypes dataBaseType, IJdbcDataSource jdbcDataSource,
                            TableLocation tableLocation, Statement statement,
                            String baseQuery, List<Object> params) {
        super(dataBaseType, jdbcDataSource, tableLocation, statement, params, baseQuery);
    }

    @Override
    public boolean isSpatial() {
        return true;
    }

    @Override
    public Geometry getGeometry(int columnIndex) throws Exception {
        SpatialResultSet rs = (SpatialResultSet) getResultSet();
        if (rs != null) {
            return rs.getGeometry(columnIndex);
        }
        throw new SQLException("Cannot find any geometry on column index " + columnIndex);
    }

    @Override
    public Geometry getGeometry(String columnLabel) throws Exception {
        SpatialResultSet rs = (SpatialResultSet) getResultSet();
        if (rs != null) {
            return rs.getGeometry(columnLabel);
        }
        throw new SQLException("Cannot find any geometry on column name " + columnLabel);
    }

    @Override
    public Geometry getGeometry() throws Exception {
        SpatialResultSet rs = (SpatialResultSet) getResultSet();
        if (rs != null) {
            return rs.getGeometry();
        }
        throw new SQLException("Cannot read the data");
    }

    @Override
    public IRaster getRaster(int columnIndex) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public IRaster getRaster(String columnLabel) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public IRaster getRaster() throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> getSpatialColumns() throws Exception {
        List<String> list = new ArrayList<>(getRasterColumns());
        List<String> geometric = getGeometricColumns();
        if (geometric != null) {
            list.addAll(geometric);
        }
        return list;
    }

    @Override
    public List<String> getRasterColumns() throws Exception {
        return new ArrayList<>();
    }

    @Override
    public List<String> getGeometricColumns() throws Exception {
        if (getTableLocation() == null) {
            try {
                ResultSet rs = getResultSet();
                if (rs != null) {
                    return GeometryTableUtilities.getGeometryColumnNames(rs.getMetaData());
                }
            } catch (SQLException e) {
                LOGGER.error("Unable to get the geometric columns on ResultSet.", e);
                return null;
            }
        } else {
                Connection con = getJdbcDataSource().getConnection();
                if (con == null) {
                    throw new SQLException("Cannot get the connection to the database");
                }
            try {
                return GeometryTableUtilities.getGeometryColumnNames(con, getTableLocation());
            } catch (SQLException e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public Geometry getExtent(String[] geometryColumns, String filter) throws Exception {
        if (getTableLocation() == null) {
            Connection con = getJdbcDataSource().getConnection();
            if (con == null) {
                throw new SQLException("Cannot get the connection to the database");
            }
            return GeometryTableUtilities.getEnvelope(con, getBaseQuery(), geometryColumns, filter);

        } else {
            Connection con = getJdbcDataSource().getConnection();
            if (con == null) {
                throw new SQLException("Cannot get the connection to the database");
            }
            return GeometryTableUtilities.getEnvelope(con, getTableLocation(), geometryColumns, filter);
        }
    }

    @Override
    public Geometry getExtent(String... geometryColumns) throws Exception {
        if (getTableLocation() == null) {
            Connection con = getJdbcDataSource().getConnection();
            if (con == null) {
                throw new SQLException("Cannot get the connection to the database");
            }
            try {
                return GeometryTableUtilities.getEnvelope(con, getBaseQuery(), geometryColumns);
            } catch (SQLException e) {
                return null;
            }
        } else {
            Connection con = getJdbcDataSource().getConnection();
            if (con == null) {
                throw new SQLException("Cannot get the connection to the database");
            }
            return GeometryTableUtilities.getEnvelope(con, getTableLocation(), geometryColumns);
        }
    }

    @Override
    public Geometry getExtent() throws Exception {
        if (getTableLocation() == null) {
            Connection con = getJdbcDataSource().getConnection();
            if (con == null) {
                throw new SQLException("Cannot get the connection to the database");
            }
            ResultSet rs0 = getResultSet();
            if (rs0 == null) {
                throw new SQLException("Cannot read the data");
            }
            Tuple<String, Integer> geomMeta = null;
            TableLocation tableLocation = getTableLocation();
            if (tableLocation == null) {
                geomMeta = GeometryTableUtilities.getFirstGeometryColumnNameAndIndex(rs0.getMetaData());
            } else {
                geomMeta = GeometryTableUtilities.getFirstGeometryColumnNameAndIndex(con, tableLocation);
            }
            if(geomMeta==null){
                return null;
            }
            return GeometryTableUtilities.getEnvelope(rs0, geomMeta.first());
        } else {
            Connection con = getJdbcDataSource().getConnection();
            if (con == null) {
                throw new SQLException("Cannot get the connection to the database");
            }
            Tuple<String, Integer> geomMeta = GeometryTableUtilities.getFirstGeometryColumnNameAndIndex(con, getTableLocation());
            return GeometryTableUtilities.getEnvelope(con, getTableLocation(), geomMeta.first());
        }
    }

    @Override
    public Geometry getEstimatedExtent() throws Exception {
        if (getTableLocation() == null) {
            throw new UnsupportedOperationException();
        }
        Connection con = getJdbcDataSource().getConnection();
        if (con == null) {
            throw new SQLException("Cannot get the connection to the database");
        }
        Tuple<String, Integer> geomMeta = GeometryTableUtilities.getFirstGeometryColumnNameAndIndex(con, getTableLocation());
        return GeometryTableUtilities.getEstimatedExtent(con, getTableLocation(), geomMeta.first());
    }


    @Override
    public void setSrid(int srid) throws Exception {
        List<String> geomColumns = getGeometricColumns();
        if (getTableLocation() == null || geomColumns.isEmpty()) {
            throw new IllegalArgumentException("Invalid table");
        }
        Connection con = getJdbcDataSource().getConnection();
        if (con == null) {
            throw new SQLException("Cannot get the connection to the database");
        }
        String geomColumn = getGeometricColumns().get(0);
        String type = getColumnType(geomColumn);
        con.createStatement().execute(
                "ALTER TABLE " + getLocation() + " ALTER COLUMN " + geomColumn + " TYPE geometry(" + type + ", " + srid + ") USING ST_SetSRID(" + geomColumn + "," + srid + ");");

    }

    @Override
    public Map<String, String> getGeometryTypes() throws Exception {
        if (getTableLocation() == null) {
            Map<String, String> map = new HashMap<>();
            ResultSet rs = getResultSet();
            if (rs == null) {
                throw new SQLException("Cannot read the data");
            }
            try {
                GeometryTableUtilities.getMetaData(getResultSet())
                        .forEach((key, value) -> map.put(key, value.getGeometryType()));
                return map;
            }catch (SQLException e){
                return null;
            }
        } else {
            Map<String, String> map = new HashMap<>();
            Connection con = getJdbcDataSource().getConnection();
            if (con == null) {
                throw new SQLException("Cannot get the connection to the database");
            }
            try {
                GeometryTableUtilities.getMetaData(con, getTableLocation())
                        .forEach((s, meta) -> map.put(s, meta.getGeometryType()));
                return map;
            }catch (SQLException e){
                return null;
            }
        }
    }

    @Override
    public SpatialResultSetMetaData getMetaData() throws SQLException {
        ResultSet rs = getResultSet();
        return rs.getMetaData().unwrap(SpatialResultSetMetaData.class);
    }

    @Override
    public Iterator<StreamSpatialResultSet> iterator() {
        return new ResultSetIterator(this);
    }

    @Override
    public Stream<StreamSpatialResultSet> stream() throws Exception {
        Spliterator<StreamSpatialResultSet> spliterator = new ResultSetSpliterator<>(this.getRowCount(), new StreamSpatialResultSet((SpatialResultSet) getResultSet()));
        return StreamSupport.stream(spliterator, true);
    }
}
