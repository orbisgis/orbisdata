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

import org.h2gis.functions.io.utility.FileUtil
import org.junit.jupiter.api.Test
import org.orbisgis.datamanager.h2gis.H2GIS
import org.orbisgis.datamanagerapi.dataset.ITable

import java.sql.SQLException

import static org.junit.jupiter.api.Assertions.*
import static org.orbisgis.datamanagerapi.dsl.ISqlBuilder.Order.DESC

class GroovyH2GISTest {


    @Test
    void loadH2GIS() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        assertNotNull(h2GIS)
    }
    
    @Test
    void loadH2GISFromFile() {
        Properties properties = new Properties();
        properties.setProperty('databaseName' ,'./target/loadH2GIS')
        File propertiesFile = new File('target/config.properties')
        OutputStream out = new FileOutputStream(propertiesFile);
        properties.store(out, "H2GIS properties file");
        def h2GIS = H2GIS.open('target/config.properties')
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
        println(concat)
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
                DROP TABLE IF EXISTS h2gis;
                CREATE TABLE h2gis (id int, the_geom point);
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        h2GIS.save("h2gis","target/h2gis_imported.shp");
        h2GIS.load("target/h2gis_imported.shp", "h2gis_imported", null, false);
        def concat = ""
        h2GIS.getSpatialTable "h2gis_imported" eachRow { row -> concat += "$row.id $row.the_geom $row.geometry\n" }
        assertEquals("1 POINT (10 10) POINT (10 10)\n2 POINT (1 1) POINT (1 1)\n", concat)
        println(concat)
    }

    @Test
    void exportImportTwoTimesShpFile() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis;
                CREATE TABLE h2gis (id int, the_geom point);
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        h2GIS.save("h2gis","target/h2gis_imported.shp");
        h2GIS.load("target/h2gis_imported.shp", "h2gis_imported", null, false);
        h2GIS.load("target/h2gis_imported.shp", "h2gis_imported", null, true);
        def concat = ""
        h2GIS.getSpatialTable "h2gis_imported" eachRow { row -> concat += "$row.id $row.the_geom $row.geometry\n" }
        assertEquals("1 POINT (10 10) POINT (10 10)\n2 POINT (1 1) POINT (1 1)\n", concat)
        println(concat)
    }

    @Test
    void exportImportShpFileSimple1() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis;
                CREATE TABLE h2gis (id int, the_geom point);
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        h2GIS.save("h2gis","target/h2gis_imported.shp");
        h2GIS.load("target/h2gis_imported.shp", "h2gis_imported");
        def concat = ""
        h2GIS.getSpatialTable "h2gis_imported" eachRow { row -> concat += "$row.id $row.the_geom $row.geometry\n" }
        assertEquals("1 POINT (10 10) POINT (10 10)\n2 POINT (1 1) POINT (1 1)\n", concat)
        println(concat)
    }

    @Test
    void exportImportShpFileSimple2() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis;
                CREATE TABLE h2gis (id int, the_geom point);
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        h2GIS.save("h2gis","target/h2gis_imported.shp");
        h2GIS.load("target/h2gis_imported.shp");
        def concat = ""
        h2GIS.getSpatialTable "h2gis_imported" eachRow { row -> concat += "$row.id $row.the_geom $row.geometry\n" }
        assertEquals("1 POINT (10 10) POINT (10 10)\n2 POINT (1 1) POINT (1 1)\n", concat)
        println(concat)
    }
    @Test
    void exportImportGeoJsonShapeFile() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis;
                CREATE TABLE h2gis (id int, the_geom point);
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        h2GIS.save("h2gis","target/h2gis_imported.geojson");
        h2GIS.load("target/h2gis_imported.geojson");
        h2GIS.save("h2gis_imported","target/h2gis_imported.shp");
        h2GIS.load("target/h2gis_imported.shp", true);
        def concat = ""
        h2GIS.getSpatialTable "h2gis_imported" eachRow { row -> concat += "$row.id $row.the_geom $row.geometry\n" }
        assertEquals("1 POINT (10 10) POINT (10 10)\n2 POINT (1 1) POINT (1 1)\n", concat)
        println(concat)
    }

    @Test
    void exportImportCSV() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis;
                CREATE TABLE h2gis (id int, the_geom point);
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        h2GIS.save("h2gis","target/h2gis_imported.csv");
        h2GIS.load("target/h2gis_imported.csv");
        def concat = ""
        h2GIS.getSpatialTable "h2gis_imported" eachRow { row -> concat += "$row.id $row.the_geom $row.geometry\n" }
        assertEquals("1 POINT (10 10) POINT (10 10)\n2 POINT (1 1) POINT (1 1)\n", concat)
        println(concat)
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
                DROP TABLE IF EXISTS externalTable;
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
                DROP TABLE IF EXISTS h2gis;
                CREATE TABLE h2gis (id int, the_geom point);
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        def file = new File('target/myscript.sql')
        file.delete();
        file << 'CREATE TABLE super as SELECT * FROM $BINIOU;\n --COMMENTS HERE \nSELECT * FROM super;'
        h2GIS.executeScript("target/myscript.sql", [BINIOU:'h2gis']);
        def concat = ""
        h2GIS.getSpatialTable "super" eachRow { row -> concat += "$row.id $row.the_geom $row.geometry\n" }
        assertEquals("1 POINT (10 10) POINT (10 10)\n2 POINT (1 1) POINT (1 1)\n", concat)
        println(concat)
    }

    @Test
    void executeQueryNoBindings() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis;
                CREATE TABLE h2gis (id int, the_geom point);
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        def file = new File('target/myscript.sql')
        file.delete();
        file << 'CREATE TABLE super as SELECT * FROM h2gis;\n --COMMENTS HERE \nSELECT * FROM super;'
        h2GIS.executeScript("target/myscript.sql");
        def concat = ""
        h2GIS.getSpatialTable "super" eachRow { row -> concat += "$row.id $row.the_geom $row.geometry\n" }
        assertEquals("1 POINT (10 10) POINT (10 10)\n2 POINT (1 1) POINT (1 1)\n", concat)
        println(concat)
    }

    @Test
    void executeQueryEmptyBindings() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis;
                CREATE TABLE h2gis (id int, the_geom point);
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        def file = new File('target/myscript.sql')
        file.delete();
        file << 'CREATE TABLE super as SELECT * FROM h2gis;\n --COMMENTS HERE \nSELECT * FROM super;'
        h2GIS.executeScript("target/myscript.sql", [:]);
        def concat = ""
        h2GIS.getSpatialTable "super" eachRow { row -> concat += "$row.id $row.the_geom $row.geometry\n" }
        assertEquals("1 POINT (10 10) POINT (10 10)\n2 POINT (1 1) POINT (1 1)\n", concat)
        println(concat)
    }
    
    @Test
    void executeQueryBindingsNumeric() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis;
                CREATE TABLE h2gis (id int, the_geom point);
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        def file = new File('target/myscript.sql')
        file.delete();
        file << 'CREATE TABLE super as SELECT ST_BUFFER(the_geom, $DISTANCE) as the_geom FROM $BINIOU;'
        h2GIS.executeScript("target/myscript.sql", [BINIOU:'h2gis', DISTANCE:10]);
        def concat = ""
        h2GIS.getSpatialTable "super" eachRow { row -> concat += "$row.id $row.the_geom $row.geometry\n" }
        assertEquals("1 POINT (10 10) POINT (10 10)\n2 POINT (1 1) POINT (1 1)\n", concat)
        println(concat)
    }


    @Test
    void loadOSMFile() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        def bbox = "(47.63538867628185,-2.126747667789459,47.63620380562177,-2.1253328025341034)"
        def queryURL = "[timeout:900];(node[\"building\"]${bbox};way[\"building\"]${bbox};relation[\"building\"]${bbox};);out;"

        println(queryURL)

        def outputOSMFile = new File("target/map.osm")

        if (outputOSMFile.exists()) {
            outputOSMFile.delete()
        }
        def apiUrl = "https://lz4.overpass-api.de/api/interpreter?data="
        def connection = new URL(apiUrl + URLEncoder.encode(queryURL)).openConnection() as HttpURLConnection

        connection.setRequestMethod("GET")

        println "Executing query... $queryURL"
        //Save the result in a file
        if (connection.responseCode == 200) {
            println "Downloading the OSM data from overpass api"
            outputOSMFile << connection.inputStream
        } else {
            println "Cannot execute the query"
        }
        h2GIS.load(outputOSMFile.absolutePath, 'map', true)
        assertTrue(h2GIS.tableNames.count{it.startsWith('LOADH2GIS.PUBLIC.MAP')}==11 )

    }
}
