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
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.MultiPolygon
import org.locationtech.jts.geom.Polygon
import org.locationtech.jts.io.WKTReader
import org.orbisgis.orbisdata.datamanager.jdbc.h2gis.H2GIS
import org.orbisgis.orbisdata.datamanager.jdbc.postgis.POSTGIS

import static org.junit.jupiter.api.Assertions.*

class GroovyPostGISTest {

    def static dbProperties = [databaseName: 'orbisgis_db',
                               user        : 'orbisgis',
                               password    : 'orbisgis',
                               url         : 'jdbc:postgresql://localhost:5432/'
    ]
    static POSTGIS postGIS;


    @BeforeAll
    static void init() {
        postGIS = POSTGIS.open(dbProperties)
        System.setProperty("test.postgis", Boolean.toString(postGIS != null));
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
                DROP TABLE IF EXISTS testtable;
                CREATE TABLE testtable (id int, the_geom geometry(point, 0));
                INSERT INTO testtable VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        def concat = ""
        postGIS.eachRow "SELECT THE_GEOM FROM testtable", { row -> concat += "$row.the_geom\n" }
        assertEquals("POINT (10 10)\nPOINT (1 1)\n", concat)
    }

    @Test
    void querySpatialTable() {
        def postGIS = POSTGIS.open(dbProperties)
        postGIS.execute("""
                DROP TABLE IF EXISTS testtable;
                CREATE TABLE testtable (id int, the_geom geometry(point, 0));
                INSERT INTO testtable VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        def concat = ""
        postGIS.getSpatialTable "testtable" eachRow { row -> concat += "$row.id $row.the_geom $row.geometry\n" }
        assertEquals("1 POINT (10 10) POINT (10 10)\n2 POINT (1 1) POINT (1 1)\n", concat)
    }


    @Test
    @EnabledIfSystemProperty(named = "test.postgis", matches = "true")
    void queryPostGISMetaData() {
        postGIS.execute("""
                DROP TABLE IF EXISTS testtable;
                CREATE TABLE testtable (id int, the_geom geometry(point, 0));
                INSERT INTO testtable VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        def concat = ""
        postGIS.rows "SELECT * FROM testtable", { meta ->
            concat += "${meta.getTableName(1)} $meta.columnCount\n"
        }
        assertEquals("testtable 2\n", concat)
    }

    @Test
    @EnabledIfSystemProperty(named = "test.postgis", matches = "true")
    void querySpatialTableMetaData() {
        postGIS.execute("""
                DROP TABLE IF EXISTS testtable;
                CREATE TABLE testtable (id int, the_geom geometry(point, 0));
                INSERT INTO testtable VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)

        def concat = ""
        postGIS.getSpatialTable("testtable").meta.each { row ->
            concat += "$row.columnLabel $row.columnType\n"
        }
        assertEquals("id 4\nthe_geom 1111\n", concat)

        concat = ""
        postGIS.getSpatialTable("testtable").meta.each { row ->
            concat += "$row.columnLabel $row.columnType\n"
        }
        assertEquals("id 4\nthe_geom 1111\n", concat)
    }


    @Test
    @EnabledIfSystemProperty(named = "test.postgis", matches = "true")
    void exportImportShpFile() {
        postGIS.execute("""
                DROP TABLE IF EXISTS testtable, testtable_imported;
                CREATE TABLE testtable (id int, the_geom geometry(point, 0));
                INSERT INTO testtable VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        postGIS.save("testtable", "target/postgis_imported.shp")
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
                DROP TABLE IF EXISTS testtable, testtable_imported;
                CREATE TABLE testtable (id int, the_geom geometry(point, 0));
                INSERT INTO testtable VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        postGIS.save("testtable", "target/postgis_imported.shp")
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
                DROP TABLE IF EXISTS testtable, testtable_imported;
                CREATE TABLE testtable (id int, the_geom geometry(point, 0));
                INSERT INTO testtable VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        postGIS.save("testtable", "target/postgis_imported.shp")
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
                DROP TABLE IF EXISTS testtable, testtable_imported;
                CREATE TABLE testtable (id int, the_geom geometry(point, 0));
                INSERT INTO testtable VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        postGIS.save("testtable", "target/postgis_imported.shp")
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
                DROP TABLE IF EXISTS testtable, testtable_imported;
                CREATE TABLE testtable (id int, the_geom geometry(point, 0));
                INSERT INTO testtable VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        postGIS.save("testtable", "target/postgis_imported.geojson")
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
                DROP TABLE IF EXISTS testtable, testtable_imported;
                CREATE TABLE testtable (id int, the_geom geometry(point, 0));
                INSERT INTO testtable VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        postGIS.save("testtable", "target/postgis_imported.csv")
        postGIS.load("target/postgis_imported.csv")
        def concat = ""
        postGIS.getTable "postgis_imported" eachRow { row -> concat += "$row.id $row.the_geom\n" }
        assertEquals("1 POINT (10 10)\n2 POINT (1 1)\n", concat)
    }

    @Test
    @EnabledIfSystemProperty(named = "test.postgis", matches = "true")
    void queryTableColumnNames() {
        postGIS.execute("""
                DROP TABLE IF EXISTS testtable;
                CREATE TABLE testtable (id int, the_geom geometry(point, 0));
                INSERT INTO testtable VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        assertEquals("id,the_geom", postGIS.getSpatialTable("testtable").columns.join(","))
        postGIS.execute "alter table testtable add column  columns integer"
        assertEquals("id,the_geom,columns", postGIS.getSpatialTable("testtable").getColumns().join(","))
    }

    @Disabled
    @Test
    @EnabledIfSystemProperty(named = "test.postgis", matches = "true")
    void exportSaveReadTableGeoJson() {
        postGIS.execute("""
                DROP TABLE IF EXISTS testtable, testtable_saved;
                CREATE TABLE testtable (id int, the_geom geometry(point, 0));
                INSERT INTO testtable VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        postGIS.getSpatialTable("testtable").save("target/postgis_saved.geojson");
        postGIS.load("target/postgis_saved.geojson");
        def concat = ""
        postGIS.getSpatialTable "postgis_saved" eachRow { row ->
             concat += "$row.id $row.the_geom $row.geometry\n" }
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
        assertNotNull(spr.save("target/reprojected_table_postgis.shp", true))
        def reprojectedTable = postGIS.getSpatialTable(postGIS.load("target/reprojected_table_postgis.shp", true))
        assertNotNull(reprojectedTable)
        reprojectedTable.next();
        assertEquals(2154, reprojectedTable.getGeometry(2).getSRID())
    }

    @Test
    @EnabledIfSystemProperty(named = "test.postgis", matches = "true")
    void testFilterReproject() {
        postGIS.execute("""
                DROP TABLE IF EXISTS orbisgis;
                CREATE TABLE orbisgis (id int, the_geom geometry(point, 4326));
                INSERT INTO orbisgis VALUES (1, 'SRID=4326;POINT(10 10)'::GEOMETRY), (2, 'SRID=4326;POINT(1 1)'::GEOMETRY);
        """)
        def tableName = postGIS.getTable("orbisgis").filter(" where id = 2").getSpatialTable().reproject(4326).save(postGIS, "output_filtered", true)
        assertNotNull(tableName)
        def reprojectedTable = postGIS.getSpatialTable(tableName)
        reprojectedTable.next();
        assertEquals(4326, reprojectedTable.getGeometry(2).getSRID())
        assertNotNull(reprojectedTable)
        //The SRID value is not set has constrained
        assertEquals(4326,reprojectedTable.srid )
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
        def sp = postGIS.getSpatialTable("orbisgis").columns("ST_BUFFER(THE_GEOM, 10) AS THE_GEOM").spatialTable
        sp.save("target/query_table_postgis.shp", true)
        def queryTable = postGIS.getSpatialTable(postGIS.load("target/query_table_postgis.shp", true))
        assertEquals 2, queryTable.rowCount
        assertEquals 4326, queryTable.srid
        assertTrue queryTable.getFirstRow()[1] instanceof MultiPolygon
    }

    @Test
    @EnabledIfSystemProperty(named = "test.postgis", matches = "true")
    void testEstimateExtent(){
        postGIS.execute"""DROP TABLE  IF EXISTS forests;
                CREATE TABLE forests ( fid INTEGER, name CHARACTER VARYING(64),
                 boundary GEOMETRY(MULTIPOLYGON, 0));
                INSERT INTO forests VALUES(109, 'Green Forest', ST_MPolyFromText( 'MULTIPOLYGON(((28 26,28 0,84 0,
                84 42,28 26), (52 18,66 23,73 9,48 6,52 18)),((59 18,67 18,67 13,59 13,59 18)))', 0));"""
        postGIS.execute("ANALYZE forests");
        Geometry geom = postGIS.getSpatialTable("forests").getEstimatedExtent()
        assertEquals 0, geom.SRID
        assertTrue geom instanceof Polygon
        WKTReader reader = new WKTReader();
        assertEquals(0.0, geom.distance(reader.read("POLYGON ((28 0, 28 42, 84 42, 84 0, 28 0))")));
    }

    @Test
    @EnabledIfSystemProperty(named = "test.postgis", matches = "true")
    void testExtend(){
        postGIS.execute"""DROP TABLE  IF EXISTS forests;
                CREATE TABLE forests ( fid INTEGER NOT NULL PRIMARY KEY, name CHARACTER VARYING(64),
                 boundary GEOMETRY(MULTIPOLYGON, 4326));
                INSERT INTO forests VALUES(109, 'Green Forest', ST_MPolyFromText( 'MULTIPOLYGON(((28 26,28 0,84 0,
                84 42,28 26), (52 18,66 23,73 9,48 6,52 18)),((59 18,67 18,67 13,59 13,59 18)))', 4326));"""
        Geometry geom = postGIS.getSpatialTable("forests").getExtent()
        assertEquals 4326, geom.SRID
        assertTrue geom instanceof Polygon
        assertEquals("POLYGON ((28 0, 28 42, 84 42, 84 0, 28 0))", geom.toString());
    }

    @Test
    @EnabledIfSystemProperty(named = "test.postgis", matches = "true")
    void sridOnEmptyTable() {
        postGIS.execute("""
                DROP TABLE IF EXISTS testtable;
                CREATE TABLE testtable (id int, the_geom geometry(point, 4326));
        """)
        assertEquals(4326, postGIS.getSpatialTable("testtable").srid)

        postGIS.execute("""
                DROP SCHEMA IF EXISTS cnrs CASCADE;
                CREATE SCHEMA cnrs ;
                CREATE TABLE cnrs.testtable (id int, the_geom geometry(point, 4326));
        """)
        assertEquals(4326, postGIS.getSpatialTable("cnrs.testtable").srid)
        postGIS.execute("DROP SCHEMA IF EXISTS cnrs CASCADE;")
    }

    @Test
    @EnabledIfSystemProperty(named = "test.postgis", matches = "true")
    void saveTableToH2GISBatchSize() {
        postGIS.execute("""
                DROP TABLE IF EXISTS h2gis;
                CREATE TABLE h2gis (id int, the_geom geometry(point));
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        def h2GISTarget = H2GIS.open([databaseName: './target/loadH2GIS_target'])
        assertNull(postGIS.getSpatialTable("h2gis").save(h2GISTarget, true, -1))
        assertNotNull(postGIS.getSpatialTable("h2gis").save(h2GISTarget, true, 100))
        def concat = ""
        h2GISTarget.spatialTable "H2GIS" eachRow { row -> concat += "$row.id $row.the_geom $row.geometry\n" }
        assertEquals("1 POINT (10 10) POINT (10 10)\n2 POINT (1 1) POINT (1 1)\n", concat)
        concat = ""
        h2GISTarget.execute("DROP TABLE IF EXISTS \"h2gis\" ")
        postGIS.getSpatialTable("h2gis").save(h2GISTarget)
        h2GISTarget.spatialTable "H2GIS" eachRow { row -> concat += "$row.id $row.the_geom $row.geometry\n" }
        assertEquals("1 POINT (10 10) POINT (10 10)\n2 POINT (1 1) POINT (1 1)\n", concat)
    }

    @Test
    @EnabledIfSystemProperty(named = "test.postgis", matches = "true")
    void loadTableFromH2GISToPostGIS() {
        def h2GISSource = H2GIS.open([databaseName: './target/loadH2GIS_source'])
        h2GISSource.execute("""
                DROP TABLE IF EXISTS externalTable;
                CREATE TABLE externalTable (id int, the_geom geometry(point));
                INSERT INTO externalTable VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        assertNull(postGIS.load(h2GISSource, 'SELECT the_geom from externalTable limit 1', true))
        assertNull(postGIS.load(h2GISSource, 'SELECT the_geom from externalTable limit 1'))
        assertNull( postGIS.load(h2GISSource, 'SELECT the_geom from externalTable limit 1', "QUERY_TABLE", true))
        postGIS.load(h2GISSource, '(SELECT the_geom from externalTable limit 1)', "query_table", true)
        assertEquals(1, postGIS.getSpatialTable("query_table").getRowCount())
        assertEquals(1, postGIS.getSpatialTable("query_table").getColumnCount())
        assertEquals("the_geom", postGIS.getSpatialTable("query_table").getColumns().first())
        def importedTable = postGIS.load(h2GISSource, 'externalTable', true)
        assertNotNull(importedTable)
        assertEquals(2, postGIS.getSpatialTable(importedTable).getRowCount())
        assertEquals(2, postGIS.getSpatialTable(importedTable).getColumnCount())
    }

    @Test
    void preparedQueryTest() {
        postGIS.execute("""
                DROP TABLE IF EXISTS testtable;
                CREATE TABLE testtable (id int, the_geom geometry(point));
                INSERT INTO testtable VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        def val = 2
        def String[] arr = []

        def table = postGIS.scrollInsensitive().getTable("(SELECT * FROM testtable where id=$val)")
        assert 2 == table.firstRow[0]
        table = postGIS.scrollInsensitive().getSpatialTable("(SELECT * FROM testtable where id=$val)")
        assert 2 == table.firstRow[0]

        table = postGIS.scrollInsensitive().getTable("(SELECT * FROM testtable where id=?)", [val])
        assert 2 == table.firstRow[0]
        table = postGIS.scrollInsensitive().getSpatialTable("(SELECT * FROM testtable where id=?)", [val])
        assert 2 == table.firstRow[0]

        table = postGIS.scrollInsensitive().getTable("testtable").columns("*").filter("where id=$val").getTable()
        assert 2 == table.firstRow[0]
        table = postGIS.scrollInsensitive().getSpatialTable("testtable").columns("*").filter("where id=$val").getSpatialTable()
        assert 2 == table.firstRow[0]

        table = postGIS.scrollInsensitive().getTable("testtable").columns(null).filter("where id=$val").getTable()
        assert 2 == table.firstRow[0]
        table = postGIS.scrollInsensitive().getSpatialTable("testtable").columns(null).filter("where id=$val").getSpatialTable()
        assert 2 == table.firstRow[0]

        table = postGIS.scrollInsensitive().getTable("testtable").columns(null, "").filter("where id=$val").getTable()
        assert 2 == table.firstRow[0]
        table = postGIS.scrollInsensitive().getSpatialTable("testtable").columns("", null).filter("where id=$val").getSpatialTable()
        assert 2 == table.firstRow[0]

        table = postGIS.scrollInsensitive().getTable("testtable").columns("*").filter(null).getTable()
        assert 1 == table.firstRow[0]
        table = postGIS.scrollInsensitive().getSpatialTable("testtable").columns("*").filter(null).getSpatialTable()
        assert 1 == table.firstRow[0]

        table = postGIS.scrollInsensitive().getTable("testtable").columns(arr).filter("where id=$val").getTable()
        assert 2 == table.firstRow[0]
        table = postGIS.scrollInsensitive().getSpatialTable("testtable").columns(arr).filter("where id=$val").getSpatialTable()
        assert 2 == table.firstRow[0]

        table = postGIS.scrollInsensitive().getTable("testtable").columns("*").filter("where id=?", [val]).getTable()
        assert 2 == table.firstRow[0]
        table = postGIS.scrollInsensitive().getSpatialTable("testtable").columns("*").filter("where id=?", [val]).getSpatialTable()
        assert 2 == table.firstRow[0]
    }

    @Test
    void filterTest() {
        postGIS.execute("""
                DROP TABLE IF EXISTS testtable;
                CREATE TABLE testtable (id int, the_geom geometry(point));
                INSERT INTO testtable VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)

        def table = postGIS.getTable("testtable").columns("id").filter("limit 1").getTable()
        assert 1 == table.getRowCount()
        assert 1 == table.firstRow[0]
        table = postGIS.getTable("testtable").columns("id").filter("limit 2").getTable()
        assert 2 == table.getRowCount()
        table = postGIS.getTable("testtable").columns("id").filter("limit 2").getTable().filter("where id=2").getTable().filter("where id=2").getTable()
        assert 1 == table.getRowCount()
        assert 2 == table.firstRow[0]
    }

    @Test
    @EnabledIfSystemProperty(named = "test.postgis", matches = "true")
    void testCreateSpatialIndex() {
        new File("target/query_table_postgis.shp").delete()
        postGIS.execute("""
                DROP TABLE IF EXISTS orbisgis;
                CREATE TABLE orbisgis (id int, the_geom geometry(point, 4326));
                INSERT INTO orbisgis VALUES (1, 'SRID=4326;POINT(10 10)'::GEOMETRY), (2, 'SRID=4326;POINT(1 1)'::GEOMETRY);
        """)
        postGIS.getSpatialTable("orbisgis").the_geom.createSpatialIndex()
        assertTrue(postGIS.getSpatialTable("orbisgis").the_geom.isIndexed())
        assertFalse(postGIS.getSpatialTable("orbisgis").id.isIndexed())
        postGIS.getSpatialTable("orbisgis").id.createIndex()
        assertTrue(postGIS.getSpatialTable("orbisgis").id.isIndexed())
        postGIS.getSpatialTable("orbisgis").the_geom.dropIndex();
        assertFalse(postGIS.getSpatialTable("orbisgis").the_geom.isIndexed())
    }

    @Test
    void preparedQueryTestWithFetch() {
        postGIS.execute("""
                DROP TABLE IF EXISTS big_geo;
                CREATE TABLE big_geo as select st_makepoint(-60 + n*random()/500.00, 30 + n*random()/500.00), n as id from generate_series(1,100000) as n;
        """)
        def spatialTable = postGIS.fetchSize(100).getSpatialTable("(select * from big_geo)");
        assertEquals(100000, spatialTable.getRowCount());
    }

    @Test
    @EnabledIfSystemProperty(named = "test.postgis", matches = "true")
    void saveH2GISTableToPostGIS() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS_source'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis;
                CREATE TABLE h2gis (id int, the_geom geometry(point, 4326));
                INSERT INTO h2gis VALUES (1, 'SRID=4326;POINT(10 10)'::GEOMETRY), (2, 'SRID=4326;POINT(1 1)'::GEOMETRY);
        """)
        def output_table = "postgis_exported"
        def tmpTable = h2GIS.getTable("h2gis").filter(" where id =2").getSpatialTable().reproject(4326).save(postGIS, output_table, true);
        assertNotNull(tmpTable)
        def sp = postGIS.getSpatialTable(output_table)
        assertTrue(sp.getRowCount()==1)
        sp.next()
        assertEquals(4326,sp.getGeometry().getSRID())
        tmpTable = h2GIS.getTable("h2gis").filter(" where id =2").getSpatialTable().reproject(4326).save(postGIS, output_table, true);
        assertNotNull(tmpTable)
        sp = postGIS.getSpatialTable(output_table)
        assertTrue(sp.getRowCount()==1)
        sp.next()
        assertEquals(4326,sp.getGeometry().getSRID())
    }

    @Test
    void getSridTest() {
        postGIS.execute("""
                DROP TABLE IF EXISTS testtable;
                CREATE TABLE testtable (id int, the_geom geometry(point, 0));
        """)
        assertEquals(0, postGIS.getSpatialTable("testtable").srid)
    }
}
