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

import groovy.sql.Sql;
import org.h2gis.functions.factory.H2GISDBFactory;
import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.SFSUtilities;
import org.h2gis.utilities.wrapper.SpatialResultSetImpl;
import org.h2gis.utilities.wrapper.StatementWrapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.orbisgis.orbisdata.datamanager.api.dataset.DataBaseType;
import org.orbisgis.orbisdata.datamanager.api.dataset.IJdbcSpatialTable;
import org.orbisgis.orbisdata.datamanager.api.dataset.IJdbcTable;
import org.orbisgis.orbisdata.datamanager.api.dataset.ISpatialTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.locationtech.jts.geom.MultiPolygon;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class dedicated to {@link JdbcTable} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public class JdbcSpatialTableTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcSpatialTableTest.class);

    /**
     * Database connection
     */
    private static Connection connection;
    /**
     * Connection statement
     */
    private static Statement statement;
    /**
     * Dummy data source
     */
    private static DummyJdbcDataSource dataSource;


    private static final String BASE_DATABASE = JdbcTableTest.class.getSimpleName();
    private static final String TABLE_NAME = "ORBISGIS";

    private static final String COL_THE_GEOM = "THE_GEOM";
    private static final String COL_THE_GEOM2 = "THE_GEOM2";
    private static final String COL_ID = "ID";
    private static final String COL_VALUE = "VALUE";
    private static final String COL_MEANING = "MEANING";

    @BeforeAll
    public static void beforeAll() {
        try {
            connection = SFSUtilities.wrapConnection(H2GISDBFactory.createSpatialDataBase(BASE_DATABASE));
        } catch (SQLException | ClassNotFoundException e) {
            fail(e);
        }
        Sql sql = new Sql(connection);
        dataSource = new DummyJdbcDataSource(sql, DataBaseType.H2GIS);
    }

    /**
     * Set the database with some data.
     */
    @BeforeEach
    public void prepareDB() {
        try {
            statement = connection.createStatement();
            statement.execute("DROP TABLE IF EXISTS " + TABLE_NAME);
            statement.execute("CREATE TABLE " + TABLE_NAME + " (" + COL_THE_GEOM + " GEOMETRY(GEOMETRY, 2020), " + COL_THE_GEOM2 + " GEOMETRY(POINT Z)," +
                    COL_ID + " INTEGER, " + COL_VALUE + " FLOAT, " + COL_MEANING + " VARCHAR)");
            statement.execute("INSERT INTO " + TABLE_NAME + " VALUES (ST_SetSRID('POINT(0 0)', 2020), 'POINT(1 1 0)', 1, 2.3, 'Simple points')");
            statement.execute("INSERT INTO " + TABLE_NAME + " VALUES (ST_SetSRID('POINT(0 1 2)', 2020), 'POINT(10 11 12)', 2, 0.568, '3D point')");
        } catch (Exception e) {
            fail(e);
        }
    }

    /**
     * Test the {@link JdbcSpatialTable#isSpatial()} method.
     */
    @Test
    public void testIsSpatial() {
        assertTrue(dataSource.getSpatialTable(TABLE_NAME).isSpatial());
    }

    /**
     * Test the {@link JdbcSpatialTable#getGeometry()}, {@link JdbcSpatialTable#getGeometry(int)},
     * {@link JdbcSpatialTable#getGeometry(String)} methods.
     */
    @Test
    public void testGetGeometry() {
        ISpatialTable table = dataSource.getSpatialTable(TABLE_NAME);
        assertNull(table.getGeometry());
        assertNull(table.getGeometry(2));
        assertNull(table.getGeometry(3));
        assertNull(table.getGeometry(COL_THE_GEOM));
        assertNull(table.getGeometry(COL_THE_GEOM2));

        final String[] str = {"", "", "", "", ""};
        table.forEach(o -> {
            str[0] += ((DummyJdbcSpatialTable) o).getGeometry();
            str[1] += ((DummyJdbcSpatialTable) o).getGeometry(1);
            str[2] += ((DummyJdbcSpatialTable) o).getGeometry(2);
            str[3] += ((DummyJdbcSpatialTable) o).getGeometry(COL_THE_GEOM);
            str[4] += ((DummyJdbcSpatialTable) o).getGeometry(COL_THE_GEOM2);
        });
        assertEquals("POINT (0 0)POINT (0 1)", str[0]);
        assertEquals("POINT (0 0)POINT (0 1)", str[1]);
        assertEquals("POINT (1 1)POINT (10 11)", str[2]);
        assertEquals("POINT (0 0)POINT (0 1)", str[3]);
        assertEquals("POINT (1 1)POINT (10 11)", str[4]);
        assertNull(table.getGeometry(4));
        assertNull(table.getGeometry(COL_ID));
    }


    /**
     * Test the {@link JdbcSpatialTable#getRaster()}, {@link JdbcSpatialTable#getRaster(int)},
     * {@link JdbcSpatialTable#getRaster(String)} methods.
     */
    @Test
    public void testGetRaster() {
        ISpatialTable table = dataSource.getSpatialTable(TABLE_NAME);
        assertThrows(UnsupportedOperationException.class, table::getRaster);
        assertThrows(UnsupportedOperationException.class, () -> table.getRaster(0));
        assertThrows(UnsupportedOperationException.class, () -> table.getRaster("col"));
    }

    /**
     * Test the {@link JdbcSpatialTable#getGeometricColumns()} and
     * {@link JdbcSpatialTable#getRasterColumns()} and {@link JdbcSpatialTable#getSpatialColumns()} methods.
     */
    @Test
    public void testGetColumns() {
        assertEquals(2, dataSource.getSpatialTable(TABLE_NAME).getGeometricColumns().size());
        assertTrue(dataSource.getSpatialTable(TABLE_NAME).getGeometricColumns().contains(COL_THE_GEOM));
        assertTrue(dataSource.getSpatialTable(TABLE_NAME).getGeometricColumns().contains(COL_THE_GEOM2));

        assertEquals(0, dataSource.getSpatialTable(TABLE_NAME).getRasterColumns().size());

        assertEquals(2, dataSource.getSpatialTable(TABLE_NAME).getSpatialColumns().size());
        assertTrue(dataSource.getSpatialTable(TABLE_NAME).getSpatialColumns().contains(COL_THE_GEOM));
        assertTrue(dataSource.getSpatialTable(TABLE_NAME).getSpatialColumns().contains(COL_THE_GEOM2));
    }

    /**
     * Test the {@link JdbcSpatialTable#getExtend()} method.
     */
    @Test
    public void testGetExtend() {
        assertEquals("Env[0.0 : 0.0, 0.0 : 1.0]", dataSource.getSpatialTable(TABLE_NAME).getExtend().toString());
    }

    /**
     * Test the {@link JdbcSpatialTable#getEstimatedExtend()} method.
     */
    @Test
    public void testGetEstimatedExtend() {
        assertEquals("LINESTRING (0 0, 0 1)", dataSource.getSpatialTable(TABLE_NAME).getEstimatedExtend().toString());
    }

    /**
     * Test the {@link JdbcSpatialTable#getSrid()} method.
     */
    @Test
    public void testGetSrid() {
        assertEquals(2020, dataSource.getSpatialTable(TABLE_NAME).getSrid());
    }

    /**
     * Test the {@link JdbcSpatialTable#getMetaData()} method.
     */
    @Test
    public void testGetMetadata() {
        assertNotNull(dataSource.getSpatialTable(TABLE_NAME).getMetaData());
    }

    /**
     * Test the {@link JdbcSpatialTable#getGeometryTypes()} method.
     */
    @Test
    public void testGetGeometryTypes() {
        Map<String, String> map = dataSource.getSpatialTable(TABLE_NAME).getGeometryTypes();
        assertEquals(2, map.size());
        assertTrue(map.containsKey(COL_THE_GEOM));
        assertEquals("GEOMETRY", map.get(COL_THE_GEOM));
        assertTrue(map.containsKey(COL_THE_GEOM2));
        assertEquals("POINTZ", map.get(COL_THE_GEOM2));
    }

    /**
     * Simple instantiation of a {@link JdbcSpatialTable}.
     **/
    private static class DummyJdbcSpatialTable extends JdbcSpatialTable {

        /**
         * Main constructor.
         *
         * @param jdbcDataSource DataSource to use for the creation of the resultSet.
         * @param tableLocation  TableLocation that identify the represented table.
         * @param statement      Statement used to request the database.
         * @param baseQuery      Query for the creation of the ResultSet
         */
        public DummyJdbcSpatialTable(TableLocation tableLocation, String baseQuery, Statement statement,
                                     JdbcDataSource jdbcDataSource) {
            super(DataBaseType.H2GIS, jdbcDataSource, tableLocation, statement, baseQuery);
        }


        @Override
        protected ResultSet getResultSet() {
            if (resultSet == null) {
                try {
                    resultSet = getStatement().executeQuery(getBaseQuery());
                } catch (SQLException e) {
                    LOGGER.error("Unable to execute the query '" + getBaseQuery() + "'.\n" + e.getLocalizedMessage());
                    return null;
                }
                try {
                    resultSet.beforeFirst();
                } catch (SQLException e) {
                    LOGGER.error("Unable to go before the first ResultSet row.\n" + e.getLocalizedMessage());
                    return null;
                }
            }
            return new SpatialResultSetImpl(resultSet, (StatementWrapper) getStatement());
        }

        @Override
        public ISpatialTable reproject(int srid) {
            throw new IllegalArgumentException("Unsupported operation");
        }
    }

    /**
     * Simple implementation of the {@link JdbcDataSource} abstract class for test purpose.
     */
    private static class DummyJdbcDataSource extends JdbcDataSource {

        private DummyJdbcDataSource(Sql parent, DataBaseType databaseType) {
            super(parent, databaseType);
        }

        @Override
        public IJdbcTable getTable(String tableName) {
            return null;
        }

        @Override
        public IJdbcSpatialTable getSpatialTable(String tableName) {
            String name = TableLocation.parse(tableName, getDataBaseType().equals(DataBaseType.H2GIS)).toString(getDataBaseType().equals(DataBaseType.H2GIS));
            try {
                if (!JDBCUtilities.tableExists(connection, name)) {
                    return null;
                }
            } catch (SQLException e) {
                return null;
            }
            StatementWrapper statement;
            try {
                statement = (StatementWrapper) connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            } catch (SQLException e) {
                return null;
            }
            String query = String.format("SELECT * FROM %s", name);
            return new DummyJdbcSpatialTable(new TableLocation(null, name), query, statement, this);
        }

        @Override
        public Collection<String> getTableNames() {
            try {
                return JDBCUtilities.getTableNames(connection.getMetaData(), null, null, null, null);
            } catch (SQLException ignored) {
            }
            return null;
        }

        @Override
        public IJdbcTable getDataSet(String name) {
            return null;
        }

        @Override
        public boolean hasTable(String tableName) {
            try {
                return JDBCUtilities.tableExists(connection, tableName);
            } catch (SQLException ex) {
                return false;
            }
        }
    }


}
