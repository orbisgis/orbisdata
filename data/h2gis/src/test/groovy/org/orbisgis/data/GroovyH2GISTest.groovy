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
package org.orbisgis.data

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.MultiPolygon
import org.locationtech.jts.geom.Point
import org.orbisgis.commons.printer.Ascii
import org.orbisgis.commons.printer.Html
import org.orbisgis.data.api.dataset.ISpatialTable

import java.sql.SQLException
import java.sql.Time
import java.util.stream.Collectors

import static org.junit.jupiter.api.Assertions.*

class GroovyH2GISTest {

    @Test
    void openH2GIS() {
        assertNotNull H2GIS.open("./target/openH2GIS1")
        assertNotNull H2GIS.open("./target/openH2GIS2", "sa", "sa")
    }

    @Test
    void testColumnsType() throws SQLException {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        assertNotNull h2GIS
        def name = "TYPES"
        h2GIS.execute("DROP TABLE IF EXISTS $name".toString())
        h2GIS.execute("CREATE TABLE TYPES (colint INT, colreal REAL, colint2 MEDIUMINT, coltime TIME, " +
                "colvarchar VARCHAR2, colbool boolean, coltiny tinyint, colpoint GEOMETRY(POINT), colgeom GEOMETRY);")

        Map namesClasses = h2GIS.getColumnNamesClasses(name)
        def res = namesClasses.intersect(
                [COLINT    : Integer.class,
                 COLREAL   : Float.class,
                 COLINT2   : Integer.class,
                 COLTIME   : Time.class,
                 COLVARCHAR: String.class,
                 COLBOOL   : Boolean.class,
                 COLTINY   : Byte.class,
                 COLPOINT  : Point.class,
                 COLGEOM   : Geometry.class])
        assertEquals(namesClasses.size(), res.size())
    }

    @Test
    void loadH2GIS() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        assertNotNull h2GIS
        File directory = new File("./target/db_for_test")
        H2GIS datasource = H2GIS.open(directory.absolutePath + File.separator + "osm_chain_db;AUTO_SERVER=TRUE")
        assertNotNull datasource
    }

    @Test
    void loadH2GISFromFile() {
        Properties properties = new Properties()
        properties.setProperty('databaseName', './target/loadH2GIS')
        File propertiesFile = new File('target/config.properties')
        OutputStream out = new FileOutputStream(propertiesFile)
        properties.store(out, "H2GIS properties file")
        def h2GIS = H2GIS.open(new File('target/config.properties'))
        assertNotNull(h2GIS)
    }

    @Test
    void queryH2GIS() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis;
                CREATE TABLE h2gis (id int, the_geom geometry(point));
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        def concat = ""
        h2GIS.eachRow "SELECT THE_GEOM FROM h2gis", { row -> concat += "$row.the_geom\n" }
        assertEquals("POINT (10 10)\nPOINT (1 1)\n", concat)
    }

    @Test
    void queryH2GISWithBatch() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis;
                CREATE TABLE h2gis (id int);
                INSERT INTO h2gis VALUES (1), (2);
        """)
        h2GIS.withBatch(1) { stmt ->
            h2GIS.eachRow "SELECT id FROM h2gis", { row ->
                stmt.addBatch """INSERT INTO  h2gis VALUES(${row.id+10})""" }
        }
        assertEquals(4, h2GIS.getTable("H2GIS").getRowCount())
    }

    @Test
    void querySpatialTable() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis;
                CREATE TABLE h2gis (id int, the_geom geometry(point));
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        def concat = ""
        h2GIS.getSpatialTable "h2gis" eachRow { row -> concat += "$row.id $row.the_geom $row.geometry\n" }
        assertEquals("1 POINT (10 10) POINT (10 10)\n2 POINT (1 1) POINT (1 1)\n", concat)
    }

    @Test
    void queryTableNames() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS table1, table2;
                CREATE TABLE table1 (id int, the_geom geometry(point));
                CREATE TABLE table2 (id int, the_geom geometry(point));
        """)

        def values = h2GIS.tableNames
        assertTrue values.contains("LOADH2GIS.PUBLIC.TABLE1")
        assertTrue values.contains("LOADH2GIS.PUBLIC.TABLE2")
    }

    @Test
    void queryH2GISMetaData() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis;
                CREATE TABLE h2gis (id int, the_geom geometry(point));
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        def concat = ""
        h2GIS.rows "SELECT * FROM h2gis", { meta ->
            concat += "${meta.getTableName(1)} $meta.columnCount\n"
        }
        assertEquals("H2GIS 2\n", concat)
    }

    @Test
    void querySpatialTableMetaData() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis;
                CREATE TABLE h2gis (id int, the_geom geometry(point));
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)

        def concat = ""
        h2GIS.getSpatialTable("h2gis").getMetaData().each { row ->
            concat += "${row.getColumnLabel()} ${row.getColumnType()}\n"
        }
        assertEquals("ID 4\nTHE_GEOM 1111\n", concat)

        concat = ""
        h2GIS.getSpatialTable("h2gis").metaData.each { row ->
            concat += "$row.columnLabel $row.columnType\n"
        }
        assertEquals("ID 4\nTHE_GEOM 1111\n", concat)
    }


    @Test
    void exportImportShpFile() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis, h2gis_imported;
                CREATE TABLE h2gis (id int, the_geom geometry(point));
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        h2GIS.save("h2gis", "target/h2gis_imported.shp", true)
        h2GIS.load("target/h2gis_imported.shp", "h2gis_imported", null, false)
        def concat = ""
        h2GIS.getSpatialTable "h2gis_imported" eachRow { row -> concat += "$row.id $row.the_geom $row.geometry\n" }
        assertEquals("1 POINT (10 10) POINT (10 10)\n2 POINT (1 1) POINT (1 1)\n", concat)
    }

    @Test
    void exportImportTwoTimesShpFile() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis, h2gis_imported;
                CREATE TABLE h2gis (id int, the_geom geometry(point));
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        h2GIS.save("h2gis", "target/h2gis_imported.shp", true)
        h2GIS.load("target/h2gis_imported.shp", "h2gis_imported", null, false)
        h2GIS.load("target/h2gis_imported.shp", "h2gis_imported", null, true)
        def concat = ""
        h2GIS.getSpatialTable "h2gis_imported" eachRow { row -> concat += "$row.id $row.the_geom $row.geometry\n" }
        assertEquals("1 POINT (10 10) POINT (10 10)\n2 POINT (1 1) POINT (1 1)\n", concat)
    }

    @Test
    void exportImportShpFileSimple1() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis, h2gis_imported;
                CREATE TABLE h2gis (id int, the_geom geometry(point));
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        h2GIS.save("h2gis", "target/h2gis_imported.shp", true)
        h2GIS.load("target/h2gis_imported.shp", "h2gis_imported", true)
        def concat = ""
        h2GIS.getSpatialTable "h2gis_imported" eachRow { row -> concat += "$row.id $row.the_geom $row.geometry\n" }
        assertEquals("1 POINT (10 10) POINT (10 10)\n2 POINT (1 1) POINT (1 1)\n", concat)
    }

    @Test
    void exportImportShpFileSimple2() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis, h2gis_imported;
                CREATE TABLE h2gis (id int, the_geom geometry(point));
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        h2GIS.save("h2gis", "target/h2gis_imported.shp", true)
        h2GIS.load("target/h2gis_imported.shp")
        def concat = ""
        h2GIS.getSpatialTable "h2gis_imported" eachRow { row -> concat += "$row.id $row.the_geom $row.geometry\n" }
        assertEquals("1 POINT (10 10) POINT (10 10)\n2 POINT (1 1) POINT (1 1)\n", concat)
    }

    @Test
    void exportImportGeoJsonShapeFile() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis, h2gis_imported;
                CREATE TABLE h2gis (id int, the_geom geometry(point));
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        h2GIS.save("h2gis", "target/h2gis_imported.geojson", true)
        h2GIS.load("target/h2gis_imported.geojson", true)
        h2GIS.save("h2gis_imported", "target/h2gis_imported.shp", true)
        h2GIS.load("target/h2gis_imported.shp", true)
        def concat = ""
        h2GIS.getSpatialTable "h2gis_imported" eachRow { row -> concat += "$row.id $row.the_geom $row.geometry\n" }
        assertEquals("1 POINT (10 10) POINT (10 10)\n2 POINT (1 1) POINT (1 1)\n", concat)
    }

    @Test
    void exportImportCSV() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis, h2gis_imported;
                CREATE TABLE h2gis (id int, the_geom geometry(point));
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        h2GIS.save("h2gis", "target/h2gis_imported.csv", true)
        h2GIS.load("target/h2gis_imported.csv", true)
        def concat = ""
        h2GIS.getTable "h2gis_imported" eachRow { row ->
            concat += "$row.id $row.the_geom\n"
        }
        assertEquals("1 POINT (10 10)\n2 POINT (1 1)\n", concat)
    }

    @Test
    void querySpatialTableWhere() throws SQLException {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis, h2gis_imported;
                CREATE TABLE h2gis (id int, the_geom geometry(point));
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)

        def values = new ArrayList<>()
        h2GIS.getSpatialTable "h2gis" filter "where id=2" eachRow { row ->
            values.add "$row.the_geom"
        }
        assertEquals(1, values.size())
        assertEquals("POINT (1 1)", values.get(0).toString())
    }

    @Test
    void queryTableColumnNames() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis;
                CREATE TABLE h2gis (id int, the_geom geometry(point));
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        assertEquals("ID,THE_GEOM", h2GIS.getSpatialTable("h2gis").columns.join(","))
        assertTrue(h2GIS.getSpatialTable("h2gis").columns.indexOf("THE_GEOM") != -1)
    }

    @Test
    void loadExternalTableFromDataBase() {
        def h2External = H2GIS.open([databaseName: './target/secondH2GIS', user: 'sa', password: 'sa'])
        h2External.execute("""
                DROP TABLE IF EXISTS externalTable;
                CREATE TABLE externalTable (id int, the_geom geometry(point));
                INSERT INTO externalTable VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.load(h2External, 'externalTable', true)
        assertTrue(h2GIS.tableNames.contains("LOADH2GIS.PUBLIC.EXTERNALTABLE"))
    }

    @Test
    void loadExternalTableFromDataBaseWithQuery() {
        def h2External = H2GIS.open([databaseName: './target/secondH2GIS', user: 'sa', password: 'sa'])
        h2External.execute("""
                DROP TABLE IF EXISTS externalTable;
                CREATE TABLE externalTable (id int, the_geom geometry(point));
                INSERT INTO externalTable VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        assertThrows(Exception.class, ()-> h2GIS.load(h2External, 'SELECT the_geom from externalTable limit 1', true))
        assertThrows(Exception.class,()-> h2GIS.load(h2External, 'SELECT the_geom from externalTable limit 1'))
        assertThrows(Exception.class, ()->  h2GIS.load(h2External, 'SELECT the_geom from externalTable limit 1', "QUERY_TABLE", true))
        h2GIS.load(h2External, '(SELECT the_geom from externalTable limit 1)', "QUERY_TABLE", true)
        assertEquals(1, h2GIS.getSpatialTable("QUERY_TABLE").getRowCount())
        assertEquals(1, h2GIS.getSpatialTable("QUERY_TABLE").getColumnCount())
        assertEquals("THE_GEOM", h2GIS.getSpatialTable("QUERY_TABLE").getColumns().first())
    }

    @Test
    void linkExternalFile() {
        def h2GIS = H2GIS.open([databaseName: './target/secondH2GIS', user: 'sa', password: 'sa'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS externalTable;
                CREATE TABLE externalTable (id int, the_geom geometry(point));
                INSERT INTO externalTable VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        h2GIS.save("externalTable", 'target/externalFile.shp', true)
        h2GIS.link('target/externalFile.shp', 'externalimported', true)
        assertTrue(h2GIS.tableNames.contains("SECONDH2GIS.PUBLIC.EXTERNALIMPORTED"))
    }

    @Test
    void linkExternalFileAsType() {
        def h2GIS = H2GIS.open([databaseName: './target/secondH2GIS', user: 'sa', password: 'sa'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS externalTable;
                CREATE TABLE externalTable (id int, the_geom geometry(point));
                INSERT INTO externalTable VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        h2GIS.save("externalTable", 'target/externalFile.shp', true)
        def table = h2GIS.getTable(h2GIS.link('target/externalFile.shp', 'super', true))
        assertEquals("PK,THE_GEOM,ID", table.columns.join(","))
    }

    @Test
    void linkExternalFileAsTypeAndSave() {
        def h2GIS = H2GIS.open([databaseName: './target/secondH2GIS', user: 'sa', password: 'sa'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS externalTable, supersave;
                CREATE TABLE externalTable (id int, the_geom geometry(point));
                INSERT INTO externalTable VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        h2GIS.save("externalTable", 'target/externalFile.shp', true)
        def table = h2GIS.getTable(h2GIS.link('target/externalFile.shp', 'super', true))
        table.save('target/supersave.shp', true)
        h2GIS.load('target/supersave.shp', true)
        assertTrue(h2GIS.tableNames.contains("SECONDH2GIS.PUBLIC.SUPERSAVE"))
        assertEquals("PK,THE_GEOM,ID", table.columns.join(","))
    }

    @Test
    void executeQueryBindings() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis, super;
                CREATE TABLE h2gis (id int, the_geom geometry(point));
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        def file = new File('target/myscript.sql')
        file.delete()
        file << 'CREATE TABLE super as SELECT * FROM $BINIOU;\n --COMMENTS HERE \nSELECT * FROM super;'
        h2GIS.executeScript("target/myscript.sql", [BINIOU: 'h2gis'])
        def concat = ""
        h2GIS.getSpatialTable( "super") eachRow { row -> concat += "$row.id $row.the_geom $row.geometry\n" }
        assertEquals("1 POINT (10 10) POINT (10 10)\n2 POINT (1 1) POINT (1 1)\n", concat)
    }

    @Test
    void executeQueryNoBindings() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis, super;
                CREATE TABLE h2gis (id int, the_geom geometry(point));
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        def file = new File('target/myscript.sql')
        file.delete()
        file << 'CREATE TABLE super as SELECT * FROM h2gis;\n --COMMENTS HERE \nALTER TABLE super RENAME  to super_rename;'
        h2GIS.executeScript("target/myscript.sql")
        def concat = ""
        h2GIS.getSpatialTable "super_rename" eachRow { row -> concat += "$row.id $row.the_geom $row.geometry\n" }
        assertEquals("1 POINT (10 10) POINT (10 10)\n2 POINT (1 1) POINT (1 1)\n", concat)
    }

    @Test
    void executeQueryEmptyBindings() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis, super;
                CREATE TABLE h2gis (id int, the_geom geometry(point));
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        def file = new File('target/myscript.sql')
        file.delete()
        file << 'CREATE TABLE super as SELECT * FROM h2gis;\n --COMMENTS HERE \nSELECT * FROM super;'
        h2GIS.executeScript("target/myscript.sql", [:])
        def concat = ""
        h2GIS.getSpatialTable "super" eachRow { row -> concat += "$row.id $row.the_geom $row.geometry\n" }
        assertEquals("1 POINT (10 10) POINT (10 10)\n2 POINT (1 1) POINT (1 1)\n", concat)
    }

    @Test
    void executeQueryBindingsNumeric() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis, super;
                CREATE TABLE h2gis (id int, the_geom geometry(point));
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        def file = new File('target/myscript.sql')
        file.delete()
        file << 'CREATE TABLE super as SELECT ST_BUFFER(the_geom, $DISTANCE) as the_geom FROM $BINIOU;'
        h2GIS.executeScript("target/myscript.sql", [BINIOU: 'h2gis', DISTANCE: 10])
        def concat = ""
        h2GIS.getSpatialTable "super" eachRow { row ->
            concat += "$row.the_geom $row.geometry\n"
            assertTrue "$row.the_geom".startsWith("POLYGON ((")
            assertTrue "$row.geometry".startsWith("POLYGON ((")
        }
    }


    @Disabled
    @Test
    void loadOSMFile() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        def bbox = "(47.63538867628185,-2.126747667789459,47.63620380562177,-2.1253328025341034)"
        def queryURL = "[timeout:900];(node[\"building\"]${bbox};way[\"building\"]${bbox};relation[\"building\"]${bbox};);out;"

        def outputOSMFile = new File("target/map.osm")

        if (outputOSMFile.exists()) {
            outputOSMFile.delete()
        }
        def apiUrl = "https://lz4.overpass-api.de/api/interpreter?data="
        def connection = new URL(apiUrl + URLEncoder.encode(queryURL)).openConnection() as HttpURLConnection

        connection.setRequestMethod("GET")

        //Save the result in a file
        if (connection.responseCode == 200) {
            outputOSMFile << connection.inputStream
        } else {
            fail()
        }
        h2GIS.load(outputOSMFile.absolutePath, 'map', true)
        assertEquals 10, h2GIS.tableNames.count { it.startsWith('LOADH2GIS.PUBLIC.MAP') }
    }

    @Test
    void request() throws SQLException {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute """DROP TABLE IF EXISTS h2gis;
                CREATE TABLE h2gis (id int PRIMARY KEY, code int, the_geom geometry(point));
                insert into h2gis values (1,22, 'POINT(10 10)'::GEOMETRY);
                insert into h2gis values (2,56, 'POINT(20 20)'::GEOMETRY);
                insert into h2gis values (3,22, 'POINT(10 10)'::GEOMETRY);
                insert into h2gis values (4,22, 'POINT(10 10)'::GEOMETRY);
                insert into h2gis values (5,22, 'POINT(20 10)'::GEOMETRY);"""

        def table = h2GIS.spatialTable "h2gis" columns "COUNT(id)", "code", "the_geom" filter "where code=22" filter "and id<5" filter "group By code" spatialTable

        def values = new ArrayList<>()
        table.eachRow { row ->
            values.add row.getInt(1)
            values.add row.getInt(2)
        }
        assertEquals(2, values.size())
        assertEquals(3, (int) values.get(0))
        assertEquals(22, (int) values.get(1))

        values = new ArrayList<>()

        h2GIS.table "h2gis" filter "where code=22" filter "or code=56" filter "order By id DESC" eachRow { row ->
            values.add row.getInt(1)
        }
        assertEquals(5, values.size())
        assertEquals(5, (int) values.get(0))
        assertEquals(4, (int) values.get(1))
        assertEquals(3, (int) values.get(2))
        assertEquals(2, (int) values.get(3))
        assertEquals(1, (int) values.get(4))

        values = new ArrayList<>()

        h2GIS.table "h2gis" filter "order By id DESC" eachRow { row ->
            values.add row.getInt(1)
        }
        assertEquals(5, values.size())
        assertEquals(5, (int) values.get(0))
        assertEquals(4, (int) values.get(1))
        assertEquals(3, (int) values.get(2))
        assertEquals(2, (int) values.get(3))
        assertEquals(1, (int) values.get(4))
    }

    @Test
    void geometryTypes() {
        def h2GIS = H2GIS.open([databaseName: './target/secondH2GIS', user: 'sa', password: 'sa'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS externalTable;
                CREATE TABLE externalTable (id int, the_geom geometry(point));
                INSERT INTO externalTable VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        h2GIS.save("externalTable", 'target/externalFile.shp', true)
        def table = h2GIS.link('target/externalFile.shp', 'super', true)
        assertNotNull(table)
        assert (h2GIS.getSpatialTable(table).geometryTypes.toString() == "[THE_GEOM:MULTIPOINT]")
    }

    @Test
    void importOSMFile() {
        H2GIS h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        URL url = getClass().getResource("osm_test.osm")
        assertNotNull url

        String osmFile = new File(url.toURI()).absolutePath
        h2GIS.execute("DROP TABLE IF EXISTS  OSM_TAG, OSM_NODE, OSM_NODE_TAG, OSM_WAY,OSM_WAY_TAG, OSM_WAY_NODE, OSM_RELATION, OSM_RELATION_TAG, OSM_NODE_MEMBER, OSM_WAY_MEMBER, OSM_RELATION_MEMBER;")

        assertEquals"OSM", h2GIS.load(osmFile, "OSM")
        assertEquals"OSM", h2GIS.load(osmFile, "OSM", true)

        h2GIS.eachRow "SELECT count(TABLE_NAME) as nb FROM INFORMATION_SCHEMA.TABLES where TABLE_NAME LIKE 'OSM%'",
                { row ->
                    assertEquals 10, row.nb
                }

        // Check number
        h2GIS.eachRow "SELECT count(ID_NODE) as nb FROM OSM_NODE", { row ->
            assertEquals 8, row.nb
        }
        h2GIS.eachRow "SELECT count(ID_WAY) as nb FROM OSM_WAY", { row ->
            assertEquals 3, row.nb
        }
        h2GIS.eachRow "SELECT count(ID_RELATION) as nb FROM OSM_RELATION", { row ->
            assertEquals 2, row.nb
        }
    }

    @Test
    void getLocation() {
        H2GIS h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        assertEquals './target/loadH2GIS', h2GIS.location as String
        assertEquals new File('./target/loadH2GIS'), h2GIS.location as File
        assertEquals new File('./target/loadH2GIS').toURI(), h2GIS.location as URI
        assertEquals new File('./target/loadH2GIS').toURI().toURL(), h2GIS.location as URL

        assertNull h2GIS.getLocation() as Geometry
    }

    @Test
    void testColumn() {
        def h2GIS = H2GIS.open('./target/orbisgis')
        h2GIS.execute("""
                DROP TABLE IF EXISTS orbisgis;
                CREATE TABLE orbisgis (id int, the_geom geometry(point));
                INSERT INTO orbisgis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        Map columns = h2GIS.getColumnNamesTypes("orbisgis")
        assertTrue(columns.containsKey("ID"))
        assertEquals "INTEGER", columns.get("ID")
    }

    @Test
    void testPrint() {
        def h2GIS = H2GIS.open('./target/orbisgis')
        h2GIS.execute("""
                DROP TABLE IF EXISTS orbisgis;
                CREATE TABLE orbisgis (id int, the_geom geometry(point), very_long_title_to_test_size_limits varchar);
                INSERT INTO orbisgis VALUES (1, 'POINT(10 10)'::GEOMETRY, 'just a string a bit too long'), 
                (2, 'POINT(1 1)'::GEOMETRY, 'another string');
        """)
        assertEquals(
                "+--------------------+\n" +
                        "|      ORBISGIS      |\n" +
                        "+--------------------+--------------------+--------------------+\n" +
                        "|         ID         |      THE_GEOM      |VERY_LONG_TITLE_T...|\n" +
                        "+--------------------+--------------------+--------------------+\n" +
                        "|                   1|POINT (10 10)       |just a string a b...|\n" +
                        "|                   2|POINT (1 1)         |another string      |\n" +
                        "+--------------------+--------------------+--------------------+\n",
                        (h2GIS.getSpatialTable("orbisgis") as Ascii).toString())
        assertEquals(
                "+--------------------+\n" +
                        "|       query        |\n" +
                        "+--------------------+--------------------+--------------------+\n" +
                        "|         ID         |      THE_GEOM      |VERY_LONG_TITLE_T...|\n" +
                        "+--------------------+--------------------+--------------------+\n" +
                        "|                   1|POINT (10 10)       |just a string a b...|\n" +
                        "+--------------------+--------------------+--------------------+\n",
                (h2GIS.getSpatialTable("orbisgis").filter("limit 1") as Ascii).toString())
        assertEquals "<table>\n" +
                "<caption>ORBISGIS</caption>\n" +
                "<tr></tr>\n" +
                "<tr>\n" +
                "<th align=\"CENTER\">ID</th>\n" +
                "<th align=\"CENTER\">THE_GEOM</th>\n" +
                "<th align=\"CENTER\">VERY_LONG_TITLE_T...</th>\n" +
                "</tr>\n" +
                "<tr></tr>\n" +
                "<tr>\n" +
                "<td align=\"RIGHT\">1</td>\n" +
                "<td align=\"LEFT\">POINT (10 10)</td>\n" +
                "<td align=\"LEFT\">just a string a b...</td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "<td align=\"RIGHT\">2</td>\n" +
                "<td align=\"LEFT\">POINT (1 1)</td>\n" +
                "<td align=\"LEFT\">another string</td>\n" +
                "</tr>\n" +
                "<tr></tr>\n" +
                "</table>\n",
                (h2GIS.getSpatialTable("orbisgis") as Html).toString()
    }

    @Test
    void firstRows() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis;
                CREATE TABLE h2gis (id int, the_geom geometry(point));
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        def concat = ""
        println h2GIS.firstRow("select count(*) as nb from h2gis").nb
    }


    @Test
    void testReproject() {
        def h2GIS = H2GIS.open('./target/orbisgis')
        new File("target/reprojected_table.shp").delete()
        h2GIS.execute("""
                DROP TABLE IF EXISTS orbisgis;
                CREATE TABLE orbisgis (id int, the_geom geometry(point, 4326));
                INSERT INTO orbisgis VALUES (1, 'SRID=4326;POINT(10 10)'::GEOMETRY), (2, 'SRID=4326;POINT(1 1)'::GEOMETRY);
        """)
        def sp = h2GIS.getSpatialTable("orbisgis")
        assertNotNull(sp)
        assertEquals(4326, sp.getSrid())
        def spr = sp.reproject(2154)
        assertNotNull(spr)
        assertThrows(Exception.class, spr::getSrid)
        assertEquals("target/reprojected_table.shp", spr.save("target/reprojected_table.shp", true))
        def reprojectedTable = h2GIS.getSpatialTable(h2GIS.load("target/reprojected_table.shp", true))
        assertNotNull(reprojectedTable)
        assertEquals(2154, reprojectedTable.srid)
    }

    @Test
    void testReprojectGeoJson() {
        def h2GIS = H2GIS.open('./target/orbisgis')
        new File("target/reprojected_table.shp").delete()
        h2GIS.execute("""
                DROP TABLE IF EXISTS orbisgis;
                CREATE TABLE orbisgis (id int, the_geom geometry(point, 4326));
                INSERT INTO orbisgis VALUES (1, 'SRID=4326;POINT(10 10)'::GEOMETRY), (2, 'SRID=4326;POINT(1 1)'::GEOMETRY);
        """)
        def sp = h2GIS.getSpatialTable("orbisgis")
        assertNotNull(sp)
        assertEquals(4326, sp.getSrid())
        def spr = sp.reproject(2154)
        assertNotNull(spr)
        assertThrows(Exception.class, spr::getSrid)
        assertEquals("target/reprojected_table.geojson", spr.save("target/reprojected_table.geojson", true))
        def reprojectedTable = h2GIS.getSpatialTable(h2GIS.load("target/reprojected_table.geojson", true))
        assertNotNull(reprojectedTable)
        assertEquals(2154, reprojectedTable.srid)
    }

    @Test
    void testReprojectQuery() {
        def h2GIS = H2GIS.open('./target/orbisgis')
        new File("target/reprojected_table.shp").delete()
        h2GIS.execute("""
                DROP TABLE IF EXISTS orbisgis;
                CREATE TABLE orbisgis (id int, the_geom geometry(point, 4326));
                INSERT INTO orbisgis VALUES (1, 'SRID=4326;POINT(10 10)'::GEOMETRY), (2, 'SRID=4326;POINT(1 1)'::GEOMETRY);
        """)
        ISpatialTable sp = h2GIS.getSpatialTable("ORBISGIS").columns("ST_BUFFER(THE_GEOM, 10) AS THE_GEOM").getSpatialTable()
        assertNotNull(sp)
        ISpatialTable spr = sp.reproject(2154)
        assertNotNull(spr)
        assertEquals("target/reprojected_table.shp", spr.save("target/reprojected_table.shp", true))
        ISpatialTable reprojectedTable = h2GIS.getSpatialTable(h2GIS.load("target/reprojected_table.shp", true))
        assertNotNull(reprojectedTable)
        assertEquals(2, reprojectedTable.getRowCount())
        assertEquals(2154, reprojectedTable.srid)
        assertTrue(reprojectedTable.getFirstRow()[1].area > 0)
    }

    @Test
    void testSaveQueryInFile() {
        def h2GIS = H2GIS.open('./target/orbisgis')
        new File("target/query_table.shp").delete()
        h2GIS.execute("""
                DROP TABLE IF EXISTS orbisgis, query_table;
                CREATE TABLE orbisgis (id int, the_geom geometry(point, 4326));
                INSERT INTO orbisgis VALUES (1, 'SRID=4326;POINT(10 10)'::GEOMETRY), (2, 'SRID=4326;POINT(1 1)'::GEOMETRY);
        """)
        def sp = h2GIS.getSpatialTable("ORBISGIS").columns("ST_BUFFER(THE_GEOM, 10) AS THE_GEOM").getSpatialTable()
        sp.save("target/query_table.shp", true)
        def queryTable = h2GIS.getTable(h2GIS.load("target/query_table.shp"))
        assertEquals 2, queryTable.rowCount
        assertEquals 4326, queryTable.srid
        assertTrue queryTable.getFirstRow()[1] instanceof MultiPolygon
    }

    @Test
    void streamTest(){
        def h2GIS = H2GIS.open('./target/orbisgis')
        h2GIS.execute("""
                DROP TABLE IF EXISTS orbisgis;
                CREATE TABLE orbisgis (id int, the_geom geometry(point), very_long_title_to_test_size_limits varchar);
                INSERT INTO orbisgis VALUES (1, 'POINT(10 10)'::GEOMETRY, 'just a string'), 
                                            (2, 'POINT(1 1)'::GEOMETRY, 'another string'), 
                                            (3, 'POINT(0 2.36)'::GEOMETRY, 'a last very, very long string');
        """)
        String str = h2GIS.getTable("orbisgis")
                .stream()
                .map {
                    rs ->
                        rs.getObject("THE_GEOM").toString()}
                .collect(Collectors.joining(";"))
        assertEquals("POINT (10 10);POINT (1 1);POINT (0 2.36)", str)


        String str2 = h2GIS.getTable("orbisgis")
                .stream() //Get the stream
                .map { [it.getInt(1), it.getString(3)] } //Get the first and third columns as array
                .map { [it[0], it[1].size() > 15 ? it[1][0..12]+"..." : it[1]] } // Reduce the string to 15 char and add '...' if needed
                .filter{ it[0]%2 == 1 } //Filter only the odd id row
                .collect(Collectors.toList()) //Gather data into a list
        assertEquals("[[1, just a string], [3, a last very, ...]]", str2)
    }


    @Test
    void testEstimateExtent(){
        def h2GIS = H2GIS.open('./target/orbisgis')
        h2GIS.execute"""DROP TABLE  IF EXISTS forests;
                CREATE TABLE forests ( fid INTEGER NOT NULL PRIMARY KEY, name CHARACTER VARYING(64),
                 boundary GEOMETRY(MULTIPOLYGON, 4326));
                INSERT INTO forests VALUES(109, 'Green Forest', ST_MPolyFromText( 'MULTIPOLYGON(((28 26,28 0,84 0,
                84 42,28 26), (52 18,66 23,73 9,48 6,52 18)),((59 18,67 18,67 13,59 13,59 18)))', 4326));"""
        Geometry geom = h2GIS.getSpatialTable("forests").getEstimatedExtent()
        assertEquals 4326, geom.SRID
        assertEquals("POLYGON ((28 0, 28 42, 84 42, 84 0, 28 0))", geom.toString())
        h2GIS.execute("drop table forests")
    }

    @Test
    void testExtent(){
        def h2GIS = H2GIS.open('./target/orbisgis')
        h2GIS.execute"""DROP TABLE  IF EXISTS forests;
                CREATE TABLE forests ( fid INTEGER NOT NULL PRIMARY KEY, name CHARACTER VARYING(64),
                 boundary GEOMETRY(MULTIPOLYGON, 4326));
                INSERT INTO forests VALUES(109, 'Green Forest', ST_MPolyFromText( 'MULTIPOLYGON(((28 26,28 0,84 0,
                84 42,28 26), (52 18,66 23,73 9,48 6,52 18)),((59 18,67 18,67 13,59 13,59 18)))', 4326));"""
        Geometry geom = h2GIS.getSpatialTable("forests").getExtent()
        assertEquals 4326, geom.SRID
        assertEquals("POLYGON ((28 0, 28 42, 84 42, 84 0, 28 0))", geom.toString())
        geom = h2GIS.getSpatialTable("forests").getExtent("boundary")
        assertEquals 4326, geom.SRID
        assertEquals("POLYGON ((28 0, 28 42, 84 42, 84 0, 28 0))", geom.toString())
        geom = h2GIS.getSpatialTable("forests").getExtent("ST_Buffer(boundary,0)", "boundary")
        assertEquals 4326, geom.SRID
        assertEquals("POLYGON ((28 0, 28 42, 84 42, 84 0, 28 0))", geom.toString())
        geom = h2GIS.getSpatialTable("forests").getExtent("boundary")
        assertEquals 4326, geom.SRID
        assertEquals("POLYGON ((28 0, 28 42, 84 42, 84 0, 28 0))", geom.toString())
        h2GIS.execute("drop table forests")
    }

    @Test
    void testExtentWithFilter(){
        def h2GIS = H2GIS.open('./target/orbisgis')
        h2GIS.execute"""DROP TABLE  IF EXISTS forests;
                CREATE TABLE forests ( fid INTEGER NOT NULL PRIMARY KEY, name CHARACTER VARYING(64),
                 boundary GEOMETRY(MULTIPOLYGON, 4326));
                INSERT INTO forests VALUES(109, 'Green Forest', ST_MPolyFromText( 'MULTIPOLYGON(((28 26,28 0,84 0,
                84 42,28 26), (52 18,66 23,73 9,48 6,52 18)),((59 18,67 18,67 13,59 13,59 18)))', 4326));"""
        Geometry geom = h2GIS.getSpatialTable("forests").getExtent()
        assertEquals 4326, geom.SRID
        assertEquals("POLYGON ((28 0, 28 42, 84 42, 84 0, 28 0))", geom.toString())
        geom = h2GIS.getSpatialTable("forests").getExtent(["boundary"] as String[], "limit 1")
        assertEquals 4326, geom.SRID
        assertEquals("POLYGON ((28 0, 28 42, 84 42, 84 0, 28 0))", geom.toString())
        geom = h2GIS.getSpatialTable("forests").getExtent(["ST_Buffer(boundary,0)", "boundary"]as String[],"limit 1")
        assertEquals 4326, geom.SRID
        assertEquals("POLYGON ((28 0, 28 42, 84 42, 84 0, 28 0))", geom.toString())
        geom = h2GIS.getSpatialTable("forests").getExtent(["ST_Buffer(boundary,0)", "boundary"]as String[],"limit 1")
        assertEquals 4326, geom.SRID
        assertEquals("POLYGON ((28 0, 28 42, 84 42, 84 0, 28 0))", geom.toString())
        h2GIS.execute("drop table forests")
    }

    @Test
    void sridOnEmptyTable() {
        def h2GIS = H2GIS.open('./target/orbisgis')
        h2GIS.execute("""
                DROP TABLE IF EXISTS H2GIS;
                CREATE TABLE H2GIS (id int, the_geom geometry(point, 4326));
        """)
        assertEquals(4326, h2GIS.getSpatialTable("H2GIS").srid)

        h2GIS.execute("""
                DROP SCHEMA IF EXISTS cnrs CASCADE;
                CREATE SCHEMA cnrs ;
                CREATE TABLE cnrs.H2GIS (id int, the_geom geometry(point, 4326));
        """)
        assertEquals(4326, h2GIS.getSpatialTable("CNRS.H2GIS").srid)
    }

    @Test
    void saveTableToPOSTGIS() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis;
                CREATE TABLE h2gis (id int, the_geom geometry(point));
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)

        def dbProperties = [databaseName: 'orbisgis_db',
                            user        : 'orbisgis',
                            password    : 'orbisgis',
                            url         : 'jdbc:postgresql://localhost:5432/'
        ]
        def postGIS
        try {
            postGIS = POSTGIS.open(dbProperties)
        }catch (Exception e){

        }
        if(postGIS) {
            h2GIS.getSpatialTable("h2gis").save(postGIS, true)
            def concat = ""
            postGIS.spatialTable "h2gis" eachRow { row -> concat += "$row.id $row.the_geom $row.geometry\n" }
            assertEquals("1 POINT (10 10) POINT (10 10)\n2 POINT (1 1) POINT (1 1)\n", concat)
            concat = ""
            postGIS.execute("DROP TABLE IF EXISTS h2gis")
            h2GIS.getSpatialTable("h2gis").save(postGIS, true)
            postGIS.spatialTable "h2gis" eachRow { row -> concat += "$row.id $row.the_geom $row.geometry\n" }
            assertEquals("1 POINT (10 10) POINT (10 10)\n2 POINT (1 1) POINT (1 1)\n", concat)
        }
    }
    @Test
    void saveQueryToPOSTGIS() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis;
                CREATE TABLE h2gis (id int, the_geom geometry(point));
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)

        def dbProperties = [databaseName: 'orbisgis_db',
                            user        : 'orbisgis',
                            password    : 'orbisgis',
                            url         : 'jdbc:postgresql://localhost:5432/'
        ]
        def  postGIS
        try {
              postGIS = POSTGIS.open(dbProperties)
        }catch (Exception e){

        }

        if(postGIS){
        assertEquals("h2gis", h2GIS.getSpatialTable("h2gis").save(postGIS, true))
        h2GIS.getSpatialTable("h2gis").save(postGIS, "H2GIS",true )
        def concat = ""
        postGIS.spatialTable "h2gis" eachRow { row -> concat += "$row.id $row.the_geom $row.geometry\n" }
        assertEquals("1 POINT (10 10) POINT (10 10)\n2 POINT (1 1) POINT (1 1)\n", concat)
        }
    }

    @Test
    void saveTableToH2GIS() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis;
                CREATE TABLE h2gis (id int, the_geom geometry(point));
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        def h2GISTarget = H2GIS.open([databaseName: './target/loadH2GIS_target'])
        h2GIS.getSpatialTable("h2gis").save(h2GISTarget, true)
        def concat = ""
        h2GISTarget.spatialTable "H2GIS" eachRow { row -> concat += "$row.id $row.the_geom $row.geometry\n" }
        assertEquals("1 POINT (10 10) POINT (10 10)\n2 POINT (1 1) POINT (1 1)\n", concat)
        concat = ""
        h2GISTarget.execute("DROP TABLE IF EXISTS \"H2GIS\" ")
        h2GIS.getSpatialTable("h2gis").save(h2GISTarget)
        h2GISTarget.spatialTable "H2GIS" eachRow { row -> concat += "$row.id $row.the_geom $row.geometry\n" }
        assertEquals("1 POINT (10 10) POINT (10 10)\n2 POINT (1 1) POINT (1 1)\n", concat)
    }

    @Test
    void saveTableToH2GISBatchSize() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis;
                CREATE TABLE h2gis (id int, the_geom geometry(point));
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        def h2GISTarget = H2GIS.open([databaseName: './target/loadH2GIS_target'])
        assertThrows(Exception.class, ()->h2GIS.getSpatialTable("h2gis").save(h2GISTarget, true, -1))
        assertNotNull(h2GIS.getSpatialTable("h2gis").save(h2GISTarget, true, 100))
        def concat = ""
        h2GISTarget.getSpatialTable("H2GIS").eachRow {
            row ->
                concat += "${row.get("id")} ${row.getGeometry()}\n"
        }
        assertEquals("1 POINT (10 10)\n2 POINT (1 1)\n", concat)
        concat = ""
        h2GISTarget.execute("DROP TABLE IF EXISTS \"H2GIS\" ")
        h2GIS.getSpatialTable("h2gis").save(h2GISTarget)
        h2GISTarget.getSpatialTable( "H2GIS").eachRow { row -> concat += "$row.id $row.the_geom $row.geometry\n" }
        assertEquals("1 POINT (10 10) POINT (10 10)\n2 POINT (1 1) POINT (1 1)\n", concat)
    }

    @Test
    void preparedQueryTest() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis;
                CREATE TABLE h2gis (id int, the_geom geometry(point));
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        def val = 2
        String[] arr = []

        def table = h2GIS.scrollInsensitive().getTable("(SELECT * FROM h2gis where id=$val)")
        assert 2 == table.firstRow[0]
        table = h2GIS.scrollInsensitive().getSpatialTable("(SELECT * FROM h2gis where id=$val)")
        assert 2 == table.firstRow[0]

        table = h2GIS.scrollInsensitive().getTable("(SELECT * FROM h2gis where id=?)", [val])
        assert 2 == table.firstRow[0]
        table = h2GIS.scrollInsensitive().getSpatialTable("(SELECT * FROM h2gis where id=?)", [val])
        assert 2 == table.firstRow[0]

        table = h2GIS.scrollInsensitive().getTable("h2gis").columns("*").filter("where id=$val").getTable()
        assert 2 == table.firstRow[0]
        table = h2GIS.scrollInsensitive().getSpatialTable("h2gis").columns("*").filter("where id=$val").getSpatialTable()
        assert 2 == table.firstRow[0]

        table = h2GIS.scrollInsensitive().getTable("h2gis").columns(null).filter("where id=$val").getTable()
        assert 2 == table.firstRow[0]
        table = h2GIS.scrollInsensitive().getSpatialTable("h2gis").columns(null).filter("where id=$val").getSpatialTable()
        assert 2 == table.firstRow[0]

        table = h2GIS.scrollInsensitive().getTable("h2gis").columns(null, "").filter("where id=$val").getTable()
        assert 2 == table.firstRow[0]
        table = h2GIS.scrollInsensitive().getSpatialTable("h2gis").columns("", null).filter("where id=$val").getSpatialTable()
        assert 2 == table.firstRow[0]

        table = h2GIS.scrollInsensitive().getTable("h2gis").columns("*").filter(null).getTable()
        assert 1 == table.firstRow[0]
        table = h2GIS.scrollInsensitive().getSpatialTable("h2gis").columns("*").filter(null).getSpatialTable()
        assert 1 == table.firstRow[0]

        table = h2GIS.scrollInsensitive().getTable("h2gis").columns(arr).filter("where id=$val").getTable()
        assert 2 == table.firstRow[0]
        table = h2GIS.scrollInsensitive().getSpatialTable("h2gis").columns(arr).filter("where id=$val").getSpatialTable()
        assert 2 == table.firstRow[0]

        table = h2GIS.scrollInsensitive().getTable("h2gis").columns("*").filter("where id=?", [val]).getTable()
        assert 2 == table.firstRow[0]
        table = h2GIS.scrollInsensitive().getSpatialTable("h2gis").columns("*").filter("where id=?", [val]).getSpatialTable()
        assert 2 == table.firstRow[0]
    }

    @Test
    void filterTest() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS postgis;
                CREATE TABLE postgis (id int, the_geom geometry(point));
                INSERT INTO postgis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)

        def table = h2GIS.getTable("postgis").columns("id").filter("limit 1").getTable()
        assert 1 == table.getRowCount()
        assert 1 == table.firstRow[0]
        table = h2GIS.getTable("postgis").columns("id").filter("limit 2").getTable()
        assert 2 == table.getRowCount()
        table = h2GIS.getTable("postgis").columns("id").filter("limit 2").getTable().filter("where id=2").getTable().filter("where id=2").getTable()
        assert 1 == table.getRowCount()
        assert 2 == table.firstRow[0]
    }

    @Test
    void testFilterReproject() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS orbisgis;
                CREATE TABLE orbisgis (id int, the_geom geometry(point, 4326));
                INSERT INTO orbisgis VALUES (1, 'SRID=4326;POINT(10 10)'::GEOMETRY), (2, 'SRID=4326;POINT(1 1)'::GEOMETRY);
        """)
        def tableName = h2GIS.getTable("orbisgis").filter(" where id = 2").getSpatialTable().reproject(4326).save(h2GIS, "output_filtered", true)
        assertNotNull(tableName)
        def reprojectedTable = h2GIS.getSpatialTable(tableName)
        assertNotNull(reprojectedTable)
        reprojectedTable.next()
        assertEquals(4326, reprojectedTable.getGeometry(2).getSRID())
        //H2GIS looks on the first geometry SRID
        assertEquals(4326,reprojectedTable.srid )
    }

    @Test
    void exportImportGeoJsonGZ() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis, h2gis_imported;
                CREATE TABLE h2gis (id int, the_geom geometry(point));
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        h2GIS.save("h2gis", "target/h2gis_imported.geojson.gz", true)
        h2GIS.load("target/h2gis_imported.geojson.gz", true)
        h2GIS.save("h2gis_imported_geojson", "target/h2gis_imported.shp", true)
        h2GIS.load("target/h2gis_imported.shp", true)
        def concat = ""
        h2GIS.getSpatialTable "h2gis_imported" eachRow { row -> concat += "$row.id $row.the_geom $row.geometry\n" }
        assertEquals("1 POINT (10 10) POINT (10 10)\n2 POINT (1 1) POINT (1 1)\n", concat)
    }

    @Test
    void isTableEmpty() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("DROP TABLE IF EXISTS geotable; CREATE TABLE geotable(id integer)")
        assertTrue h2GIS.getTable("geotable").isEmpty()
        h2GIS.execute("insert into geotable values(1)")
        assertFalse h2GIS.getTable("geotable").isEmpty()
        h2GIS.execute("delete from geotable")
        assertTrue h2GIS.getTable("geotable").isEmpty()
        h2GIS.execute("insert into geotable values(null)")
        assertFalse h2GIS.getTable("geotable").isEmpty()
        h2GIS.execute("drop table geotable")
    }

    @Test
    void filterExtend2() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis;
                CREATE TABLE h2gis (id int, land varchar, the_geom geometry(point));
                INSERT INTO h2gis VALUES (1, 'corn', 'SRID=4326;POINT(10.2322222666 10)'::GEOMETRY), (2, 'grass', 'SRID=4326;POINT(1 1)'::GEOMETRY);
        """)
        ISpatialTable table  = h2GIS.getSpatialTable("h2gis").columns("land", "st_precisionreducer(st_transform(the_geom, 4326), 3) as the_geom")
                .filter("limit 1").getSpatialTable()
        assertEquals(1, table.getRowCount())
        assertEquals("POINT (10.232 10)", table.getExtent().getEnvelope().toString())
    }

    @Test
    void testPrintFromDataSource() {
        def h2GIS = H2GIS.open('./target/orbisgis')
        h2GIS.execute("""
                DROP TABLE IF EXISTS orbisgis;
                CREATE TABLE orbisgis (id int, the_geom geometry(point), very_long_title_to_test_size_limits varchar);
                INSERT INTO orbisgis VALUES (1, 'POINT(10 10)'::GEOMETRY, 'just a string a bit too long'), 
                (2, 'POINT(1 1)'::GEOMETRY, 'another string');
        """)
        h2GIS.print("orbisgis")
        h2GIS.print("(SELECT * FROM generate_series(0, 10000) where x > 1000 limit 10)")
        h2GIS.print("(SELECT * FROM generate_series(0, 10000))")
    }

    @Test
    void testInteporlateString() throws SQLException {
        def h2GIS = H2GIS.open('./target/orbisgis')
        def tableName="mytable"
        h2GIS.execute("""
                DROP TABLE IF EXISTS $tableName;
                CREATE TABLE $tableName (id int, the_geom geometry(point), very_long_title_to_test_size_limits varchar);
                INSERT INTO $tableName VALUES (1, 'POINT(10 10)'::GEOMETRY, 'just a string a bit too long'), 
                (2, 'POINT(1 1)'::GEOMETRY, 'another string');
                DROP TABLE IF EXISTS $tableName;
        """)
    }
}
