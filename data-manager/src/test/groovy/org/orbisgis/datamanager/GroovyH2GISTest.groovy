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
 * DataManager is distributed under GPL 3 license.
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
package org.orbisgis.datamanager

import org.junit.jupiter.api.Test
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.Point
import org.orbisgis.datamanager.h2gis.H2GIS
import org.orbisgis.datamanagerapi.dataset.ISpatialTable
import org.orbisgis.datamanagerapi.dataset.ITable

import java.sql.SQLException
import java.sql.Time

import static org.junit.jupiter.api.Assertions.*
import static org.orbisgis.datamanagerapi.dsl.IOptionBuilder.Order.DESC

class GroovyH2GISTest {

    @Test
    void openH2GIS(){
        assertNotNull H2GIS.open("./target/openH2GIS1")
        assertNotNull H2GIS.open("./target/openH2GIS2", "sa", "sa")
    }

    @Test
    void testColumnsType() throws SQLException {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        assertNotNull h2GIS
        def name = "TYPES"
        h2GIS.execute("DROP TABLE IF EXISTS $name")
        h2GIS.execute("CREATE TABLE TYPES (colint INT, colreal REAL, colint2 MEDIUMINT, coltime TIME, " +
                "colvarchar VARCHAR2, colbool boolean, coltiny tinyint, colpoint POINT, colgeom GEOMETRY)")
        assertTrue(h2GIS.getTable("TYPES").hasColumn("colint", Integer.class))
        assertFalse(h2GIS.getTable("TYPES").hasColumn("colint", Short.class))
        assertTrue(h2GIS.getTable("TYPES").hasColumns(
                [colint:Integer.class,
                 colreal:Float.class,
                 colint2:Integer.class,
                 coltime:Time.class,
                 colvarchar:String.class,
                 colbool:Boolean.class,
                 coltiny:Byte.class,
                 colpoint:Point.class,
                 colgeom:Geometry.class]))
    }

    @Test
    void loadH2GIS() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        assertNotNull h2GIS
    }
    
    @Test
    void loadH2GISFromFile() {
        Properties properties = new Properties()
        properties.setProperty('databaseName' ,'./target/loadH2GIS')
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
                CREATE TABLE h2gis (id int, the_geom point);
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        def concat = ""
        h2GIS.eachRow "SELECT THE_GEOM FROM h2gis", { row -> concat += "$row.the_geom\n" }
        assertEquals("POINT (10 10)\nPOINT (1 1)\n", concat)
    }

    @Test
    void querySpatialTable() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis;
                CREATE TABLE h2gis (id int, the_geom point);
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
                CREATE TABLE table1 (id int, the_geom point);
                CREATE TABLE table2 (id int, the_geom point);
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
                CREATE TABLE h2gis (id int, the_geom point);
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        def concat = ""
        h2GIS.rows "SELECT * FROM h2gis", {  meta ->
            concat += "${meta.getTableName(1)} $meta.columnCount\n"
        }
        assertEquals("H2GIS 2\n", concat)
    }

    @Test
    void querySpatialTableMetaData() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis;
                CREATE TABLE h2gis (id int, the_geom point);
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)

        def concat = ""
        h2GIS.getSpatialTable("h2gis").meta.each {row ->
            concat += "$row.columnLabel $row.columnType\n"
        }
        assertEquals("ID 4\nTHE_GEOM 1111\n", concat)

        concat = ""
        h2GIS.getSpatialTable("h2gis").metadata.each {row ->
            concat += "$row.columnLabel $row.columnType\n"
        }
        assertEquals("ID 4\nTHE_GEOM 1111\n", concat)
    }
    
    
    @Test
    void exportImportShpFile() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis, h2gis_imported;
                CREATE TABLE h2gis (id int, the_geom point);
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        h2GIS.save("h2gis","target/h2gis_imported.shp");
        h2GIS.load("target/h2gis_imported.shp", "h2gis_imported", null, false);
        def concat = ""
        h2GIS.getSpatialTable "h2gis_imported" eachRow { row -> concat += "$row.id $row.the_geom $row.geometry\n" }
        assertEquals("1 POINT (10 10) POINT (10 10)\n2 POINT (1 1) POINT (1 1)\n", concat)
    }

    @Test
    void exportImportTwoTimesShpFile() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis, h2gis_imported;
                CREATE TABLE h2gis (id int, the_geom point);
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        h2GIS.save("h2gis","target/h2gis_imported.shp");
        h2GIS.load("target/h2gis_imported.shp", "h2gis_imported", null, false);
        h2GIS.load("target/h2gis_imported.shp", "h2gis_imported", null, true);
        def concat = ""
        h2GIS.getSpatialTable "h2gis_imported" eachRow { row -> concat += "$row.id $row.the_geom $row.geometry\n" }
        assertEquals("1 POINT (10 10) POINT (10 10)\n2 POINT (1 1) POINT (1 1)\n", concat)
    }

    @Test
    void exportImportShpFileSimple1() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis, h2gis_imported;
                CREATE TABLE h2gis (id int, the_geom point);
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        h2GIS.save("h2gis","target/h2gis_imported.shp");
        h2GIS.load("target/h2gis_imported.shp", "h2gis_imported");
        def concat = ""
        h2GIS.getSpatialTable "h2gis_imported" eachRow { row -> concat += "$row.id $row.the_geom $row.geometry\n" }
        assertEquals("1 POINT (10 10) POINT (10 10)\n2 POINT (1 1) POINT (1 1)\n", concat)
    }

    @Test
    void exportImportShpFileSimple2() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis, h2gis_imported;
                CREATE TABLE h2gis (id int, the_geom point);
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        h2GIS.save("h2gis","target/h2gis_imported.shp");
        h2GIS.load("target/h2gis_imported.shp");
        def concat = ""
        h2GIS.getSpatialTable "h2gis_imported" eachRow { row -> concat += "$row.id $row.the_geom $row.geometry\n" }
        assertEquals("1 POINT (10 10) POINT (10 10)\n2 POINT (1 1) POINT (1 1)\n", concat)
    }
    @Test
    void exportImportGeoJsonShapeFile() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis, h2gis_imported;
                CREATE TABLE h2gis (id int, the_geom point);
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        h2GIS.save("h2gis","target/h2gis_imported.geojson")
        h2GIS.load("target/h2gis_imported.geojson")
        h2GIS.save("h2gis_imported","target/h2gis_imported.shp")
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
                CREATE TABLE h2gis (id int, the_geom point);
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        h2GIS.save("h2gis","target/h2gis_imported.csv")
        h2GIS.load("target/h2gis_imported.csv")
        def concat = ""
        h2GIS.getTable "h2gis_imported" eachRow { row ->
            concat += "$row.id $row.the_geom\n" }
        assertEquals("1 POINT (10 10)\n2 POINT (1 1)\n", concat)
    }

    @Test
    void querySpatialTableWhere() throws SQLException {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis, h2gis_imported;
                CREATE TABLE h2gis (id int, the_geom point);
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)

        def values = new ArrayList<>()
        h2GIS.getSpatialTable "h2gis" where "id=2" eachRow {row ->
            values.add "$row.the_geom"
        }
        assertEquals(1,values.size())
        assertEquals("POINT (1 1)", values.get(0).toString())
    }
    
    @Test
    void queryTableColumnNames() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis;
                CREATE TABLE h2gis (id int, the_geom point);
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        assertEquals("ID,THE_GEOM", h2GIS.getSpatialTable("h2gis").columnNames.join(","))
        assertTrue(h2GIS.getSpatialTable("h2gis").columnNames.indexOf("THE_GEOM")!=-1)
    }

    @Test
    void loadExternalTableFromDataBase() {
        def h2External = H2GIS.open([databaseName: './target/secondH2GIS', user:'sa', password:'sa'])
        h2External.execute("""
                DROP TABLE IF EXISTS externalTable;
                CREATE TABLE externalTable (id int, the_geom point);
                INSERT INTO externalTable VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.load([user: 'sa', password: 'sa',url :'jdbc:h2:./target/secondH2GIS'], 'externalTable',true )
        assertTrue(h2GIS.tableNames.contains("LOADH2GIS.PUBLIC.EXTERNALTABLE"))
    }

    @Test
    void linkExternalFile() {
        def h2GIS = H2GIS.open([databaseName: './target/secondH2GIS', user:'sa', password:'sa'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS externalTable;
                CREATE TABLE externalTable (id int, the_geom point);
                INSERT INTO externalTable VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        h2GIS.save("externalTable", 'target/externalFile.shp' )
        h2GIS.link('target/externalFile.shp', 'externalimported', true)
        assertTrue(h2GIS.tableNames.contains("SECONDH2GIS.PUBLIC.EXTERNALIMPORTED"))
    }

    @Test
    void linkExternalFileAsType() {
        def h2GIS = H2GIS.open([databaseName: './target/secondH2GIS', user:'sa', password:'sa'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS externalTable;
                CREATE TABLE externalTable (id int, the_geom point);
                INSERT INTO externalTable VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        h2GIS.save("externalTable", 'target/externalFile.shp' )
        def table = h2GIS.link('target/externalFile.shp', 'super',true) as ITable
        assertEquals("PK,THE_GEOM,ID", table.columnNames.join(","))
    }

    @Test
    void linkExternalFileAsTypeAndSave() {
        def h2GIS = H2GIS.open([databaseName: './target/secondH2GIS', user:'sa', password:'sa'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS externalTable, supersave;
                CREATE TABLE externalTable (id int, the_geom point);
                INSERT INTO externalTable VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        h2GIS.save("externalTable", 'target/externalFile.shp' )
        def table = h2GIS.link('target/externalFile.shp', 'super',true) as ITable
        table.save('target/supersave.shp')
        h2GIS.load( 'target/supersave.shp',true )
        assertTrue(h2GIS.tableNames.contains("SECONDH2GIS.PUBLIC.SUPERSAVE"))
        assertEquals("PK,THE_GEOM,ID", table.columnNames.join(","))
    }
    
    @Test
    void executeQueryBindings() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis, super;
                CREATE TABLE h2gis (id int, the_geom point);
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        def file = new File('target/myscript.sql')
        file.delete()
        file << 'CREATE TABLE super as SELECT * FROM $BINIOU;\n --COMMENTS HERE \nSELECT * FROM super;'
        h2GIS.executeScript("target/myscript.sql", [BINIOU:'h2gis']);
        def concat = ""
        h2GIS.spatialTable "super" eachRow { row -> concat += "$row.id $row.the_geom $row.geometry\n" }
        assertEquals("1 POINT (10 10) POINT (10 10)\n2 POINT (1 1) POINT (1 1)\n", concat)
    }

    @Test
    void executeQueryNoBindings() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis, super;
                CREATE TABLE h2gis (id int, the_geom point);
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        def file = new File('target/myscript.sql')
        file.delete()
        file << 'CREATE TABLE super as SELECT * FROM h2gis;\n --COMMENTS HERE \nSELECT * FROM super;'
        h2GIS.executeScript("target/myscript.sql");
        def concat = ""
        h2GIS.getSpatialTable "super" eachRow { row -> concat += "$row.id $row.the_geom $row.geometry\n" }
        assertEquals("1 POINT (10 10) POINT (10 10)\n2 POINT (1 1) POINT (1 1)\n", concat)
    }

    @Test
    void executeQueryEmptyBindings() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis, super;
                CREATE TABLE h2gis (id int, the_geom point);
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        def file = new File('target/myscript.sql')
        file.delete()
        file << 'CREATE TABLE super as SELECT * FROM h2gis;\n --COMMENTS HERE \nSELECT * FROM super;'
        h2GIS.executeScript("target/myscript.sql", [:]);
        def concat = ""
        h2GIS.getSpatialTable "super" eachRow { row -> concat += "$row.id $row.the_geom $row.geometry\n" }
        assertEquals("1 POINT (10 10) POINT (10 10)\n2 POINT (1 1) POINT (1 1)\n", concat)
    }
    
    @Test
    void executeQueryBindingsNumeric() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis, super;
                CREATE TABLE h2gis (id int, the_geom point);
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        def file = new File('target/myscript.sql')
        file.delete()
        file << 'CREATE TABLE super as SELECT ST_BUFFER(the_geom, $DISTANCE) as the_geom FROM $BINIOU;'
        h2GIS.executeScript("target/myscript.sql", [BINIOU:'h2gis', DISTANCE:10])
        def concat = ""
        h2GIS.getSpatialTable "super" eachRow { row ->
            concat += "$row.the_geom $row.geometry\n"
            assertTrue "$row.the_geom".startsWith("POLYGON ((")
            assertTrue "$row.geometry".startsWith("POLYGON ((")
        }
    }


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
        assertTrue(h2GIS.tableNames.count{it.startsWith('LOADH2GIS.PUBLIC.MAP')}==11 )

    }

    @Test
    void request() throws SQLException {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute """DROP TABLE IF EXISTS h2gis;
                CREATE TABLE h2gis (id int PRIMARY KEY, code int, the_geom point);
                insert into h2gis values (1,22, 'POINT(10 10)'::GEOMETRY);
                insert into h2gis values (2,56, 'POINT(20 20)'::GEOMETRY);
                insert into h2gis values (3,22, 'POINT(10 10)'::GEOMETRY);
                insert into h2gis values (4,22, 'POINT(10 10)'::GEOMETRY);
                insert into h2gis values (5,22, 'POINT(20 10)'::GEOMETRY);"""

        def table = h2GIS.select "COUNT(id)", "code", "the_geom" from "h2gis" where "code=22" and "id<5" groupBy "code" spatialTable

        def values = new ArrayList<>()
        table.eachRow {row ->
            values.add row.getInt(1)
            values.add row.getInt(2)
        }
        assertEquals(2,values.size())
        assertEquals(3, (int) values.get(0))
        assertEquals(22, (int) values.get(1))

        values = new ArrayList<>()

        h2GIS.select("*") from "h2gis" where "code=22" or "code=56" orderBy "id", DESC eachRow {row ->
            values.add row.getInt(1)
        }
        assertEquals(5,values.size())
        assertEquals(5, (int) values.get(0))
        assertEquals(4, (int) values.get(1))
        assertEquals(3, (int) values.get(2))
        assertEquals(2, (int) values.get(3))
        assertEquals(1, (int) values.get(4))

        values = new ArrayList<>()

        h2GIS.select("*") from "h2gis" orderBy "id", DESC eachRow {row ->
            values.add row.getInt(1)
        }
        assertEquals(5,values.size())
        assertEquals(5, (int) values.get(0))
        assertEquals(4, (int) values.get(1))
        assertEquals(3, (int) values.get(2))
        assertEquals(2, (int) values.get(3))
        assertEquals(1, (int) values.get(4))
    }

    @Test
    void geometryTypes() {
        def h2GIS = H2GIS.open([databaseName: './target/secondH2GIS', user:'sa', password:'sa'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS externalTable;
                CREATE TABLE externalTable (id int, the_geom point);
                INSERT INTO externalTable VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        h2GIS.save("externalTable", 'target/externalFile.shp' )
        def table = h2GIS.link('target/externalFile.shp', 'super',true) as ISpatialTable
        assert (table.geometryTypes.toString()== "[THE_GEOM:MULTIPOINT]")
    }
    
    @Test
    void importOSMFile() {
        H2GIS h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        URL url = getClass().getResource("osm_test.osm")
        assertNotNull url

        String osmFile = new File(url.toURI()).absolutePath
        h2GIS.execute("DROP TABLE IF EXISTS  OSM_TAG, OSM_NODE, OSM_NODE_TAG, OSM_WAY,OSM_WAY_TAG, OSM_WAY_NODE, OSM_RELATION, OSM_RELATION_TAG, OSM_NODE_MEMBER, OSM_WAY_MEMBER, OSM_RELATION_MEMBER;")

        assertNull h2GIS.load(osmFile, "OSM")
        assertNull h2GIS.load(osmFile, "OSM", true)

        h2GIS.eachRow "SELECT count(TABLE_NAME) as nb FROM INFORMATION_SCHEMA.TABLES where TABLE_NAME LIKE 'OSM%'",
                { row ->
                    assertEquals 11,row.nb
                }

        // Check number
        h2GIS.eachRow "SELECT count(ID_NODE) as nb FROM OSM_NODE",{ row ->
            assertEquals 8, row.nb }
        h2GIS.eachRow "SELECT count(ID_WAY) as nb FROM OSM_WAY",{ row ->
            assertEquals 3, row.nb }
        h2GIS.eachRow "SELECT count(ID_RELATION) as nb FROM OSM_RELATION", { row ->
            assertEquals 2, row.nb }
    }

    @Test
    void getLocation(){
        H2GIS h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        assertEquals './target/loadH2GIS', h2GIS.location as String
        assertEquals new File('./target/loadH2GIS'), h2GIS.location as File
        assertEquals new File('./target/loadH2GIS').toURI(), h2GIS.location as URI
        assertEquals new File('./target/loadH2GIS').toURI().toURL(), h2GIS.location as URL

        assertNull h2GIS.getLocation() as Geometry
    }
}
