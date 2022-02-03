/*
 * Bundle PostGIS is part of the OrbisGIS platform
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
 * PostGIS is distributed under LGPL 3 license.
 *
 * Copyright (C) 2018 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * PostGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * PostGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * PostGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.data;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.locationtech.jts.geom.Point;
import org.orbisgis.data.api.dataset.IJdbcSpatialTable;
import org.orbisgis.data.api.dataset.IJdbcTable;
import org.orbisgis.data.api.dataset.ISpatialTable;
import org.orbisgis.data.api.dataset.ITable;
import org.orbisgis.data.jdbc.JdbcSpatialTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class dedicated to the {@link PostgisSpatialTable} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public class PostGISSpatialTableTest {


    private static final Logger LOGGER = LoggerFactory.getLogger(PostGISSpatialTableTest.class);

    private static final String BASE_DATABASE = PostGISSpatialTableTest.class.getSimpleName();
    private static final String TABLE_NAME = "orbisgis";

    private static final String COL_THE_GEOM = "the_geom";
    private static final String COL_THE_GEOM2 = "the_geom2";
    private static final String COL_ID = "id";
    private static final String COL_VALUE = "val";
    private static final String COL_MEANING = "meaning";
    private static POSTGIS postgis;


    static POSTGIS postGIS;


    @BeforeAll
    static void init() {
        Properties dbProperties = new Properties();
        dbProperties.put("databaseName", "orbisgis_db");
        dbProperties.put("user", "orbisgis");
        dbProperties.put("password", "orbisgis");
        dbProperties.put("url", "jdbc:postgresql://localhost:5432/");
        postGIS = org.orbisgis.data.POSTGIS.open(dbProperties);
        System.setProperty("test.postgis", Boolean.toString(postGIS != null));
    }


    /**
     * Set the database with some data.
     */
    @BeforeEach
    @EnabledIfSystemProperty(named = "test.postgis", matches = "true")
    public void prepareDB() {
        try {
            postGIS.execute("DROP TABLE IF EXISTS " + TABLE_NAME);
            postGIS.execute("CREATE TABLE " + TABLE_NAME + " (" + COL_THE_GEOM + " GEOMETRY(GEOMETRY, 2020), " + COL_THE_GEOM2 + " GEOMETRY(POINTZ, 4326)," +
                    COL_ID + " INTEGER, " + COL_VALUE + " FLOAT, " + COL_MEANING + " VARCHAR)");
            postGIS.execute("INSERT INTO " + TABLE_NAME + " VALUES (ST_GEOMFROMTEXT('POINT(0 0)', 2020), ST_GEOMFROMTEXT('POINT Z(1 1 0)',4326), 1, 2.3, 'Simple points')");
            postGIS.execute("INSERT INTO " + TABLE_NAME + " VALUES (ST_GEOMFROMTEXT('POINT(0 1)', 2020), ST_GEOMFROMTEXT('POINT Z(10 11 12)',4326), 2, 0.568, '3D point')");
        } catch (Exception e) {
            fail(e);
        }
    }

    /**
     * Test the {@link PostgisSpatialTable#asType(Class)} method.
     */
    @Test
    @EnabledIfSystemProperty(named = "test.postgis", matches = "true")
    public void testAsType() {
        try {
            postGIS.execute("DROP TABLE IF EXISTS NAME; CREATE TABLE name (the_geom GEOMETRY)");
        } catch (SQLException e) {
            fail(e);
        }
        ISpatialTable table = postGIS.getSpatialTable("name");
        assertTrue(table.asType(ISpatialTable.class) instanceof ISpatialTable);
        assertTrue(table.asType(ITable.class) instanceof ITable);
        assertTrue(table.asType(PostgisSpatialTable.class) instanceof PostgisSpatialTable);
        assertTrue(table.asType(PostgisTable.class) instanceof PostgisTable);
        assertNull(table.asType(String.class));
    }

    @Test
    @EnabledIfSystemProperty(named = "test.postgis", matches = "true")
    void testReproject() throws SQLException {
        postGIS.execute(" DROP TABLE IF EXISTS orbisgis;" +
                "CREATE TABLE orbisgis (id int, the_geom geometry(point, 4326));" +
                "INSERT INTO orbisgis VALUES (1, 'SRID=4326;POINT(10 10)'::GEOMETRY), " +
                "(2, 'SRID=4326;POINT(1 1)'::GEOMETRY); ");

        IJdbcSpatialTable sp = postGIS.getSpatialTable("orbisgis");
        assertNotNull(sp);
        assertEquals(4326, sp.getSrid());
        assertEquals(2, sp.getRowCount());

        ISpatialTable spr = sp.reproject(2154);
        assertNotNull(spr);
        assertEquals(2, spr.getRowCount());
        assertEquals("target/reprojected_table.shp", spr.save("target/reprojected_table.shp", true));

        IJdbcTable reprojectedTable = postGIS.getTable(postGIS.load("target/reprojected_table.shp", true));
        assertNotNull(reprojectedTable);
        assertEquals(2, reprojectedTable.getRowCount());
        assertTrue(reprojectedTable instanceof IJdbcSpatialTable);

        IJdbcSpatialTable spatialReprojectedTable = (IJdbcSpatialTable) reprojectedTable;
        assertEquals(2154, spatialReprojectedTable.getSrid());

        IJdbcSpatialTable spLoaded = postGIS.getSpatialTable("REPROJECTED_TABLE");
        assertNotNull(spLoaded);
        assertEquals(2154, spLoaded.getSrid());
        assertEquals(2, spLoaded.getRowCount());
    }

    @Test
    @EnabledIfSystemProperty(named = "test.postgis", matches = "true")
    void testSaveQueryInFile() throws SQLException {
        postGIS.execute(" DROP TABLE IF EXISTS orbisgis, query_table;" +
                "CREATE TABLE orbisgis (id int, the_geom geometry(point, 4326));" +
                "INSERT INTO orbisgis VALUES (1, 'SRID=4326;POINT(10 10)'::GEOMETRY), " +
                "(2, 'SRID=4326;POINT(1 1)'::GEOMETRY); ");

        ISpatialTable sp = postGIS.getSpatialTable("ORBISGIS");
        assertNotNull(sp);
        assertEquals(2, sp.getRowCount());
        assertEquals("target/query_table.shp", sp.save("target/query_table.shp", true));

        IJdbcTable queryTable = postGIS.getTable(postGIS.load("target/query_table.shp"));
        assertNotNull(queryTable);
        assertEquals(2, queryTable.getRowCount());
        assertTrue(queryTable instanceof IJdbcSpatialTable);

        IJdbcSpatialTable spatialReprojectedTable = (IJdbcSpatialTable) queryTable;
        assertEquals(4326, spatialReprojectedTable.getSrid());

        IJdbcSpatialTable spLoaded = postGIS.getSpatialTable("QUERY_TABLE");
        assertEquals(2, spLoaded.getRowCount());
        assertEquals(4326, spLoaded.getSrid());
        assertTrue(spLoaded.getFirstRow().get(1) instanceof Point);
     }
    /**
     * Test the {@link JdbcSpatialTable#isSpatial()} method.
     */
    @Test
    @EnabledIfSystemProperty(named = "test.postgis", matches = "true")
    public void testIsSpatial() {
        assertTrue(postGIS.getSpatialTable(TABLE_NAME).isSpatial());
    }

    /**
     * Test the {@link JdbcSpatialTable#getGeometry()}, {@link JdbcSpatialTable#getGeometry(int)},
     * {@link JdbcSpatialTable#getGeometry(String)} methods.
     */
    @Test
    @EnabledIfSystemProperty(named = "test.postgis", matches = "true")
    public void testGetGeometry() {
        ISpatialTable table = postGIS.getSpatialTable(TABLE_NAME);
        assertNull(table.getGeometry());
        assertNull(table.getGeometry(1));
        assertNull(table.getGeometry(2));
        assertNull(table.getGeometry(COL_THE_GEOM));
        assertNull(table.getGeometry(COL_THE_GEOM2));

        final String[] str = {"", "", "", "", ""};
        table.forEach(o -> {
            str[0] += ((PostgisSpatialTable) o).getGeometry();
            str[1] += ((PostgisSpatialTable) o).getGeometry(1);
            str[2] += ((PostgisSpatialTable) o).getGeometry(2);
            str[3] += ((PostgisSpatialTable) o).getGeometry(COL_THE_GEOM);
            str[4] += ((PostgisSpatialTable) o).getGeometry(COL_THE_GEOM2);
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
    @EnabledIfSystemProperty(named = "test.postgis", matches = "true")
    public void testGetRaster() {
        ISpatialTable table = postGIS.getSpatialTable(TABLE_NAME);
        assertThrows(UnsupportedOperationException.class, table::getRaster);
        assertThrows(UnsupportedOperationException.class, () -> table.getRaster(0));
        assertThrows(UnsupportedOperationException.class, () -> table.getRaster("col"));
    }

    /**
     * Test the {@link JdbcSpatialTable#getGeometricColumns()} and
     * {@link JdbcSpatialTable#getRasterColumns()} and {@link JdbcSpatialTable#getSpatialColumns()} methods.
     */
    @Test
    @EnabledIfSystemProperty(named = "test.postgis", matches = "true")
    public void testGetColumns() {
        assertEquals(2, postGIS.getSpatialTable(TABLE_NAME).getGeometricColumns().size());
        assertTrue(postGIS.getSpatialTable(TABLE_NAME).getGeometricColumns().contains(COL_THE_GEOM));
        assertTrue(postGIS.getSpatialTable(TABLE_NAME).getGeometricColumns().contains(COL_THE_GEOM2));

        assertEquals(0, postGIS.getSpatialTable(TABLE_NAME).getRasterColumns().size());

        assertEquals(2, postGIS.getSpatialTable(TABLE_NAME).getSpatialColumns().size());
        assertTrue(postGIS.getSpatialTable(TABLE_NAME).getSpatialColumns().contains(COL_THE_GEOM));
        assertTrue(postGIS.getSpatialTable(TABLE_NAME).getSpatialColumns().contains(COL_THE_GEOM2));
    }

    /**
     * Test the {@link JdbcSpatialTable#getExtent()} method.
     */
    @Test
    @EnabledIfSystemProperty(named = "test.postgis", matches = "true")
    public void testGetExtend() {
        assertEquals("Env[0.0 : 0.0, 0.0 : 1.0]", postGIS.getSpatialTable(TABLE_NAME).getExtent().getEnvelopeInternal().toString());
    }

    /**
     * Test the {@link JdbcSpatialTable#getEstimatedExtent()} method.
     */
    @Test
    @EnabledIfSystemProperty(named = "test.postgis", matches = "true")
    public void testGetEstimatedExtend() throws SQLException {
        postGIS.execute("VACUUM ANALYZE");
        assertEquals(1, postGIS.getSpatialTable(TABLE_NAME).getEstimatedExtent().getLength(), 0.01);
    }

    /**
     * Test the {@link JdbcSpatialTable#getSrid()}, {@link JdbcSpatialTable#setSrid(int)} methods.
     */
    @Test
    @EnabledIfSystemProperty(named = "test.postgis", matches = "true")
    public void testGetSrid() {
        ISpatialTable table = postGIS.getSpatialTable(TABLE_NAME);
        //Always 0 for dummy jdbc table
        assertEquals(2020, table.getSrid());
        table.setSrid(2121);
        assertEquals(2121, table.getSrid());
    }
}
