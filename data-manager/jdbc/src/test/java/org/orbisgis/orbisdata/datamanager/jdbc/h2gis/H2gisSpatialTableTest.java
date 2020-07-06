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
package org.orbisgis.orbisdata.datamanager.jdbc.h2gis;


import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.MultiPolygon;
import org.orbisgis.orbisdata.datamanager.api.dataset.IJdbcSpatialTable;
import org.orbisgis.orbisdata.datamanager.api.dataset.IJdbcTable;
import org.orbisgis.orbisdata.datamanager.api.dataset.ISpatialTable;
import org.orbisgis.orbisdata.datamanager.api.dataset.ITable;

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

    /**
     * Test the {@link H2gisSpatialTable#asType(java.lang.Class)} method.
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
        assertTrue(spr.save("target/reprojected_table.shp"));

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
        dataSource.execute(" DROP TABLE IF EXISTS orbisgis;" +
                "CREATE TABLE orbisgis (id int, the_geom geometry(point, 4326));" +
                "INSERT INTO orbisgis VALUES (1, 'SRID=4326;POINT(10 10)'::GEOMETRY), " +
                "(2, 'SRID=4326;POINT(1 1)'::GEOMETRY); ");

        ISpatialTable sp = dataSource.select("ST_BUFFER(THE_GEOM, 10) AS THE_GEOM").from("ORBISGIS").getSpatialTable();
        assertNotNull(sp);
        assertThrows(UnsupportedOperationException.class, sp::getSrid);
        assertEquals(2, sp.getRowCount());
        assertTrue(sp.save("target/query_table.shp"));

        IJdbcTable queryTable = dataSource.getTable(dataSource.load("target/query_table.shp"));
        assertNotNull(queryTable);
        assertEquals(2, queryTable.getRowCount());
        assertTrue(queryTable instanceof IJdbcSpatialTable);

        IJdbcSpatialTable spatialReprojectedTable = (IJdbcSpatialTable) queryTable;
        assertEquals(4326, spatialReprojectedTable.getSrid());

        IJdbcSpatialTable spLoaded = dataSource.getSpatialTable("QUERY_TABLE");
        assertEquals(2, spLoaded.getRowCount());
        assertEquals(4326, spLoaded.getSrid());
        assertTrue(spLoaded.getFirstRow().get(1) instanceof MultiPolygon);
    }
}
