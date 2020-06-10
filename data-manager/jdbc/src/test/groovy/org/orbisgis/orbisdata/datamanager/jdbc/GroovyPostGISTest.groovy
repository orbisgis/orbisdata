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
package org.orbisgis.orbisdata.datamanager.jdbc

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.MultiPolygon
import org.locationtech.jts.geom.Polygon
import org.locationtech.jts.io.WKTReader
import org.orbisgis.orbisdata.datamanager.api.dataset.ISpatialTable
import org.orbisgis.orbisdata.datamanager.jdbc.postgis.POSTGIS

import static org.junit.jupiter.api.Assertions.*

class GroovyPostGISTest {

    def static dbProperties = [databaseName: 'orbisgis_db',
                               user        : 'orbisgis',
                               password    : 'orbisgis',
                               url         : 'jdbc:postgresql://localhost:5432/'
    ]
    def static postGIS;


    @BeforeAll
    static void init() {
        postGIS = POSTGIS.open(dbProperties)
        System.setProperty("test.postgis",
                Boolean.toString(postGIS!=null));
    }



    @Test
    @EnabledIfSystemProperty(named = "test.postgis", matches = "true")
    void loadPostGIS() {
        assertNotNull(postGIS)
    }

    @Test
    @EnabledIfSystemProperty(named = "test.postgis", matches = "true")
    void queryPostGIS() {
        postGIS.execute("""
                DROP TABLE IF EXISTS postgis;
                CREATE TABLE postgis (id int, the_geom geometry(point, 0));
                INSERT INTO postgis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        def concat = ""
        postGIS.eachRow "SELECT THE_GEOM FROM postgis", { row -> concat += "$row.the_geom\n" }
        assertEquals("POINT (10 10)\nPOINT (1 1)\n", concat)
    }

    @Test
    @EnabledIfSystemProperty(named = "test.postgis", matches = "true")
    void querySpatialTable() {
        def postGIS = POSTGIS.open(dbProperties)
        postGIS.execute("""
                DROP TABLE IF EXISTS postgis;
                CREATE TABLE postgis (id int, the_geom geometry(point, 0));
                INSERT INTO postgis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        def concat = ""
        postGIS.getSpatialTable "postgis" eachRow { row -> concat += "$row.id $row.the_geom $row.geometry\n" }
        assertEquals("1 POINT (10 10) POINT (10 10)\n2 POINT (1 1) POINT (1 1)\n", concat)
        println(concat)
    }


    @Test
    @EnabledIfSystemProperty(named = "test.postgis", matches = "true")
    void queryPostGISMetaData() {
        postGIS.execute("""
                DROP TABLE IF EXISTS postgis;
                CREATE TABLE postgis (id int, the_geom geometry(point, 0));
                INSERT INTO postgis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        def concat = ""
        postGIS.rows "SELECT * FROM postgis", { meta ->
            concat += "${meta.getTableName(1)} $meta.columnCount\n"
        }
        assertEquals("postgis 2\n", concat)
    }

    @Test
    @EnabledIfSystemProperty(named = "test.postgis", matches = "true")
    void querySpatialTableMetaData() {
        postGIS.execute("""
                DROP TABLE IF EXISTS postgis;
                CREATE TABLE postgis (id int, the_geom geometry(point, 0));
                INSERT INTO postgis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)

        def concat = ""
        postGIS.getSpatialTable("postgis").meta.each { row ->
            concat += "$row.columnLabel $row.columnType\n"
        }
        assertEquals("id 4\nthe_geom 1111\n", concat)

        concat = ""
        postGIS.getSpatialTable("postgis").meta.each { row ->
            concat += "$row.columnLabel $row.columnType\n"
        }
        assertEquals("id 4\nthe_geom 1111\n", concat)
    }


    @Test
    @EnabledIfSystemProperty(named = "test.postgis", matches = "true")
    void exportImportShpFile() {
        postGIS.execute("""
                DROP TABLE IF EXISTS postgis, postgis_imported;
                CREATE TABLE postgis (id int, the_geom geometry(point, 0));
                INSERT INTO postgis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        postGIS.save("postgis", "target/postgis_imported.shp")
        postGIS.load("target/postgis_imported.shp", "postgis_imported", null, false)
        def concat = ""
        postGIS.getSpatialTable "postgis_imported" eachRow { row -> concat += "$row.id $row.the_geom $row.geometry\n" }
        assertEquals("1 POINT (10 10) POINT (10 10)\n2 POINT (1 1) POINT (1 1)\n", concat)
        println(concat)
    }

    @Test
    @EnabledIfSystemProperty(named = "test.postgis", matches = "true")
    void exportImportTwoTimesShpFile() {
        postGIS.execute("""
                DROP TABLE IF EXISTS postgis, postgis_imported;
                CREATE TABLE postgis (id int, the_geom geometry(point, 0));
                INSERT INTO postgis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        postGIS.save("postgis", "target/postgis_imported.shp")
        postGIS.load("target/postgis_imported.shp", "postgis_imported", null, false)
        postGIS.load("target/postgis_imported.shp", "postgis_imported", null, true)
        def concat = ""
        postGIS.getSpatialTable "postgis_imported" eachRow { row -> concat += "$row.id $row.the_geom $row.geometry\n" }
        assertEquals("1 POINT (10 10) POINT (10 10)\n2 POINT (1 1) POINT (1 1)\n", concat)
        println(concat)
    }

    @Test
    @EnabledIfSystemProperty(named = "test.postgis", matches = "true")
    void exportImportShpFileSimple1() {
        postGIS.execute("""
                DROP TABLE IF EXISTS postgis, postgis_imported;
                CREATE TABLE postgis (id int, the_geom geometry(point, 0));
                INSERT INTO postgis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        postGIS.save("postgis", "target/postgis_imported.shp")
        postGIS.load("target/postgis_imported.shp", "postgis_imported")
        def concat = ""
        postGIS.getSpatialTable "postgis_imported" eachRow { row -> concat += "$row.id $row.the_geom $row.geometry\n" }
        assertEquals("1 POINT (10 10) POINT (10 10)\n2 POINT (1 1) POINT (1 1)\n", concat)
        println(concat)
    }

    @Test
    @EnabledIfSystemProperty(named = "test.postgis", matches = "true")
    void exportImportShpFileSimple2() {
        postGIS.execute("""
                DROP TABLE IF EXISTS postgis, postgis_imported;
                CREATE TABLE postgis (id int, the_geom geometry(point, 0));
                INSERT INTO postgis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        postGIS.save("postgis", "target/postgis_imported.shp")
        postGIS.load("target/postgis_imported.shp")
        def concat = ""
        postGIS.getSpatialTable "postgis_imported" eachRow { row -> concat += "$row.id $row.the_geom $row.geometry\n" }
        assertEquals("1 POINT (10 10) POINT (10 10)\n2 POINT (1 1) POINT (1 1)\n", concat)
        println(concat)
    }

    @Test
    @EnabledIfSystemProperty(named = "test.postgis", matches = "true")
    void exportImportGeoJsonShapeFile() {
        postGIS.execute("""
                DROP TABLE IF EXISTS postgis, postgis_imported;
                CREATE TABLE postgis (id int, the_geom geometry(point, 0));
                INSERT INTO postgis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        postGIS.save("postgis", "target/postgis_imported.geojson")
        postGIS.load("target/postgis_imported.geojson")
        postGIS.save("postgis_imported", "target/postgis_imported.shp")
        postGIS.load("target/postgis_imported.shp", true)
        def concat = ""
        postGIS.getSpatialTable "postgis_imported" eachRow { row -> concat += "$row.id $row.the_geom $row.geometry\n" }
        assertEquals("1 POINT (10 10) POINT (10 10)\n2 POINT (1 1) POINT (1 1)\n", concat)
        println(concat)
    }

    @Test
    @EnabledIfSystemProperty(named = "test.postgis", matches = "true")
    void exportImportCSV() {
        postGIS.execute("""
                DROP TABLE IF EXISTS postgis, postgis_imported;
                CREATE TABLE postgis (id int, the_geom geometry(point, 0));
                INSERT INTO postgis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        postGIS.save("postgis", "target/postgis_imported.csv")
        postGIS.load("target/postgis_imported.csv")
        def concat = ""
        postGIS.getTable "postgis_imported" eachRow { row -> concat += "$row.id $row.the_geom\n" }
        assertEquals("1 POINT (10 10)\n2 POINT (1 1)\n", concat)
    }

    @Test
    @EnabledIfSystemProperty(named = "test.postgis", matches = "true")
    void queryTableColumnNames() {
        postGIS.execute("""
                DROP TABLE IF EXISTS postgis;
                CREATE TABLE postgis (id int, the_geom geometry(point, 0));
                INSERT INTO postgis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        assertEquals("id,the_geom", postGIS.getSpatialTable("postgis").columns.join(","))
    }

    @Test
    @EnabledIfSystemProperty(named = "test.postgis", matches = "true")
    void exportSaveReadTableGeoJson() {
        postGIS.execute("""
                DROP TABLE IF EXISTS postgis, postgis_saved;
                CREATE TABLE postgis (id int, the_geom geometry(point, 0));
                INSERT INTO postgis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        postGIS.getSpatialTable("postgis").save("target/postgis_saved.geojson");
        postGIS.load("target/postgis_saved.geojson");
        def concat = ""
        postGIS.getSpatialTable "postgis_saved" eachRow { row -> concat += "$row.id $row.the_geom $row.geometry\n" }
        assertEquals("1 POINT (10 10) POINT (10 10)\n2 POINT (1 1) POINT (1 1)\n", concat)
    }

    @Test
    @EnabledIfSystemProperty(named = "test.postgis", matches = "true")
    void testReproject() {
        new File("target/reprojected_table_postgis.shp").delete()
        postGIS.execute("""
                DROP TABLE IF EXISTS orbisgis;
                CREATE TABLE orbisgis (id int, the_geom geometry(point, 4326));
                INSERT INTO orbisgis VALUES (1, 'SRID=4326;POINT(10 10)'::GEOMETRY), (2, 'SRID=4326;POINT(1 1)'::GEOMETRY);
        """)
        def sp = postGIS.getSpatialTable("orbisgis")
        assertNotNull(sp)
        def spr = sp.reproject(2154)
        assertNotNull(spr)
        assertThrows(UnsupportedOperationException.class, spr::getSrid);
        assertTrue(spr.save("target/reprojected_table_postgis.shp"))
        def reprojectedTable = postGIS.load("target/reprojected_table_postgis.shp", true).spatialTable
        assertNotNull(reprojectedTable)
        reprojectedTable.next();
        assertEquals(2154, reprojectedTable.getGeometry(2).getSRID())
    }

    @Test
    @EnabledIfSystemProperty(named = "test.postgis", matches = "true")
    void testSaveQueryInFile() {
        new File("target/query_table_postgis.shp").delete()
        postGIS.execute("""
                DROP TABLE IF EXISTS orbisgis;
                CREATE TABLE orbisgis (id int, the_geom geometry(point, 4326));
                INSERT INTO orbisgis VALUES (1, 'SRID=4326;POINT(10 10)'::GEOMETRY), (2, 'SRID=4326;POINT(1 1)'::GEOMETRY);
        """)
        def sp = postGIS.select("ST_BUFFER(THE_GEOM, 10) AS THE_GEOM").from("orbisgis").spatialTable
        sp.save("target/query_table_postgis.shp")
        def queryTable = postGIS.load("target/query_table_postgis.shp", true)
        assertEquals 2, queryTable.rowCount
        assertEquals 4326, queryTable.srid
        assertTrue queryTable.getFirstRow()[1] instanceof MultiPolygon
    }

    @Test
    void testEstimateExtent(){
        postGIS.execute"""DROP TABLE  IF EXISTS forests;
                CREATE TABLE forests ( fid INTEGER NOT NULL PRIMARY KEY, name CHARACTER VARYING(64),
                 boundary GEOMETRY(MULTIPOLYGON, 0));
                INSERT INTO forests VALUES(109, 'Green Forest', ST_MPolyFromText( 'MULTIPOLYGON(((28 26,28 0,84 0,
                84 42,28 26), (52 18,66 23,73 9,48 6,52 18)),((59 18,67 18,67 13,59 13,59 18)))', 0));"""
        postGIS.execute("ANALYZE forests");
        Geometry geom = postGIS.getSpatialTable("forests").getEstimatedExtend()
        assertEquals 0, geom.SRID
        assertTrue geom instanceof Polygon
        WKTReader reader = new WKTReader();
        assertEquals(0.0, geom.distance(reader.read("POLYGON ((28 0, 28 42, 84 42, 84 0, 28 0))")));
    }

    @Test
    void testExtend(){
        postGIS.execute"""DROP TABLE  IF EXISTS forests;
                CREATE TABLE forests ( fid INTEGER NOT NULL PRIMARY KEY, name CHARACTER VARYING(64),
                 boundary GEOMETRY(MULTIPOLYGON, 4326));
                INSERT INTO forests VALUES(109, 'Green Forest', ST_MPolyFromText( 'MULTIPOLYGON(((28 26,28 0,84 0,
                84 42,28 26), (52 18,66 23,73 9,48 6,52 18)),((59 18,67 18,67 13,59 13,59 18)))', 4326));"""
        Geometry geom = postGIS.getSpatialTable("forests").getExtend()
        assertEquals 4326, geom.SRID
        assertTrue geom instanceof Polygon
        assertEquals("POLYGON ((28 0, 28 42, 84 42, 84 0, 28 0))", geom.toString());
    }
}
