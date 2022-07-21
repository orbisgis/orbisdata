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
package org.orbisgis.data;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;
import org.orbisgis.data.api.dataset.IJdbcSpatialTable;
import org.orbisgis.data.api.dataset.IJdbcTable;
import org.orbisgis.data.api.dataset.ISpatialTable;
import org.orbisgis.data.api.dataset.ITable;
import org.orbisgis.data.jdbc.JdbcSpatialTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class dedicated to the {@link H2gisSpatialTable} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public class H2gisSpatialTableTest {


    private static final Logger LOGGER = LoggerFactory.getLogger(H2gisSpatialTableTest.class);

    private static final String BASE_DATABASE = "./target/H2gisSpatialTableTest";
    private static final String TABLE_NAME = "ORBISGIS";

    private static final String COL_THE_GEOM = "THE_GEOM";
    private static final String COL_THE_GEOM2 = "THE_GEOM2";
    private static final String COL_ID = "ID";
    private static final String COL_VALUE = "VAL";
    private static final String COL_MEANING = "MEANING";
    private static H2GIS h2GIS;

    @BeforeAll
    public static void beforeAll() {
       h2GIS=  H2GIS.open(BASE_DATABASE);
    }

    /**
     * Set the database with some data.
     */
    @BeforeEach
    public void prepareDB() {
        try {
            h2GIS.execute("DROP TABLE IF EXISTS " + TABLE_NAME);
            h2GIS.execute("CREATE TABLE " + TABLE_NAME + " (" + COL_THE_GEOM + " GEOMETRY(GEOMETRY, 2020), " + COL_THE_GEOM2 + " GEOMETRY(POINT Z)," +
                    COL_ID + " INTEGER, " + COL_VALUE + " FLOAT, " + COL_MEANING + " VARCHAR)");
            h2GIS.execute("INSERT INTO " + TABLE_NAME + " VALUES (ST_SetSRID('POINT(0 0)', 2020), 'POINTZ(1 1 0)', 1, 2.3, 'Simple points')");
            h2GIS.execute("INSERT INTO " + TABLE_NAME + " VALUES (ST_SetSRID('POINT(0 1)', 2020), 'POINTZ(10 11 12)', 2, 0.568, '3D point')");
        } catch (Exception e) {
            fail(e);
        }
    }

    /**
     * Test the {@link H2gisSpatialTable#asType(Class)} method.
     */
    @Test
    public void testAsType() {
        H2GIS h2gis = H2GIS.open("./target/test");
        try {
            h2gis.execute("DROP TABLE IF EXISTS NAME; CREATE TABLE name (the_geom GEOMETRY)");
        } catch (SQLException e) {
            fail(e);
        }
        ISpatialTable table = h2gis.getSpatialTable("name");
        assertTrue(table.asType(ISpatialTable.class) instanceof ISpatialTable);
        assertTrue(table.asType(ITable.class) instanceof ITable);
        assertTrue(table.asType(H2gisSpatialTable.class) instanceof H2gisSpatialTable);
        assertTrue(table.asType(H2gisTable.class) instanceof H2gisTable);
        assertNull(table.asType(String.class));
    }

    @Test
    void testReproject() throws SQLException {
        new File("target/reprojected_table.shp").delete();
        H2GIS dataSource = H2GIS.open("./target/test");
        dataSource.execute(" DROP TABLE IF EXISTS orbisgis;" +
                "CREATE TABLE orbisgis (id int, the_geom geometry(point, 4326));" +
                "INSERT INTO orbisgis VALUES (1, 'SRID=4326;POINT(10 10)'::GEOMETRY), " +
                "(2, 'SRID=4326;POINT(1 1)'::GEOMETRY); ");

        IJdbcSpatialTable sp = dataSource.getSpatialTable("orbisgis");
        assertNotNull(sp);
        assertEquals(4326, sp.getSrid());
        assertEquals(2, sp.getRowCount());

        ISpatialTable spr = sp.reproject(2154);
        assertNotNull(spr);
        assertThrows(UnsupportedOperationException.class, spr::getSrid);
        assertEquals(2, spr.getRowCount());
        assertEquals("target/reprojected_table.shp", spr.save("target/reprojected_table.shp", true));

        IJdbcTable reprojectedTable = dataSource.getTable(dataSource.load("target/reprojected_table.shp", true));
        assertNotNull(reprojectedTable);
        assertEquals(2, reprojectedTable.getRowCount());
        assertTrue(reprojectedTable instanceof IJdbcSpatialTable);

        IJdbcSpatialTable spatialReprojectedTable = (IJdbcSpatialTable) reprojectedTable;
        assertEquals(2154, spatialReprojectedTable.getSrid());

        IJdbcSpatialTable spLoaded = dataSource.getSpatialTable("REPROJECTED_TABLE");
        assertNotNull(spLoaded);
        assertEquals(2154, spLoaded.getSrid());
        assertEquals(2, spLoaded.getRowCount());
    }

    @Test
    void testSaveQueryInFile() throws SQLException {
        new File("target/query_table.shp").delete();
        H2GIS dataSource = H2GIS.open("./target/test");
        dataSource.execute(" DROP TABLE IF EXISTS orbisgis, query_table;" +
                "CREATE TABLE orbisgis (id int, the_geom geometry(point, 4326));" +
                "INSERT INTO orbisgis VALUES (1, 'SRID=4326;POINT(10 10)'::GEOMETRY), " +
                "(2, 'SRID=4326;POINT(1 1)'::GEOMETRY); ");

        ISpatialTable sp = dataSource.getSpatialTable("ORBISGIS");
        assertNotNull(sp);
        assertEquals(2, sp.getRowCount());
        assertEquals("target/query_table.shp", sp.save("target/query_table.shp", true));

        IJdbcTable queryTable = dataSource.getTable(dataSource.load("target/query_table.shp"));
        assertNotNull(queryTable);
        assertEquals(2, queryTable.getRowCount());
        assertTrue(queryTable instanceof IJdbcSpatialTable);

        IJdbcSpatialTable spatialReprojectedTable = (IJdbcSpatialTable) queryTable;
        assertEquals(4326, spatialReprojectedTable.getSrid());

        IJdbcSpatialTable spLoaded = dataSource.getSpatialTable("QUERY_TABLE");
        assertEquals(2, spLoaded.getRowCount());
        assertEquals(4326, spLoaded.getSrid());
        assertTrue(spLoaded.getFirstRow().get(1) instanceof Point);
     }
    /**
     * Test the {@link JdbcSpatialTable#isSpatial()} method.
     */
    @Test
    public void testIsSpatial() {
        assertTrue(h2GIS.getSpatialTable(TABLE_NAME).isSpatial());
    }

    /**
     * Test the {@link JdbcSpatialTable#getGeometry()}, {@link JdbcSpatialTable#getGeometry(int)},
     * {@link JdbcSpatialTable#getGeometry(String)} methods.
     */
    @Test
    public void testGetGeometry() {
        ISpatialTable table = h2GIS.getSpatialTable(TABLE_NAME);
        assertNull(table.getGeometry());
        assertNull(table.getGeometry(1));
        assertNull(table.getGeometry(2));
        assertNull(table.getGeometry(COL_THE_GEOM));
        assertNull(table.getGeometry(COL_THE_GEOM2));

        final String[] str = {"", "", "", "", ""};
        table.forEach(o -> {
            str[0] += ((H2gisSpatialTable) o).getGeometry();
            str[1] += ((H2gisSpatialTable) o).getGeometry(1);
            str[2] += ((H2gisSpatialTable) o).getGeometry(2);
            str[3] += ((H2gisSpatialTable) o).getGeometry(COL_THE_GEOM);
            str[4] += ((H2gisSpatialTable) o).getGeometry(COL_THE_GEOM2);
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
        ISpatialTable table = h2GIS.getSpatialTable(TABLE_NAME);
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
        assertEquals(2, h2GIS.getSpatialTable(TABLE_NAME).getGeometricColumns().size());
        assertTrue(h2GIS.getSpatialTable(TABLE_NAME).getGeometricColumns().contains(COL_THE_GEOM));
        assertTrue(h2GIS.getSpatialTable(TABLE_NAME).getGeometricColumns().contains(COL_THE_GEOM2));

        assertEquals(0, h2GIS.getSpatialTable(TABLE_NAME).getRasterColumns().size());

        assertEquals(2, h2GIS.getSpatialTable(TABLE_NAME).getSpatialColumns().size());
        assertTrue(h2GIS.getSpatialTable(TABLE_NAME).getSpatialColumns().contains(COL_THE_GEOM));
        assertTrue(h2GIS.getSpatialTable(TABLE_NAME).getSpatialColumns().contains(COL_THE_GEOM2));
    }

    /**
     * Test the {@link JdbcSpatialTable#getExtent()} method.
     */
    @Test
    public void testGetExtend() {
        assertEquals("Env[0.0 : 0.0, 0.0 : 1.0]", h2GIS.getSpatialTable(TABLE_NAME).getExtent().getEnvelopeInternal().toString());
    }

    /**
     * Test the {@link JdbcSpatialTable#getEstimatedExtent()} method.
     */
    @Test
    public void testGetEstimatedExtend() {
        assertEquals("LINESTRING (0 0, 0 1)", h2GIS.getSpatialTable(TABLE_NAME).getEstimatedExtent().toString());
    }

    /**
     * Test the {@link JdbcSpatialTable#getSrid()}, {@link JdbcSpatialTable#setSrid(int)} methods.
     */
    @Test
    public void testGetSrid() {
        ISpatialTable table = h2GIS.getSpatialTable(TABLE_NAME);
        //Always 0 for dummy jdbc table
        assertEquals(2020, table.getSrid());
        table.setSrid(2121);
        assertEquals(2121, table.getSrid());
    }
}
