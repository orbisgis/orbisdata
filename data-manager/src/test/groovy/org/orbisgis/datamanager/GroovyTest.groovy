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
import org.orbisgis.datamanager.h2gis.H2GIS

import java.sql.SQLException

import static org.junit.jupiter.api.Assertions.*
import static org.orbisgis.datamanagerapi.dsl.ISqlBuilder.Order.DESC

class GroovyTest {

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
    void queryTableNames() throws SQLException {
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
    void request() throws SQLException {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis;
                CREATE TABLE h2gis (id int PRIMARY KEY, code int, the_geom point);
                insert into h2gis values (1,22, 'POINT(10 10)'::GEOMETRY);
                insert into h2gis values (2,56, 'POINT(20 20)'::GEOMETRY);
                insert into h2gis values (3,22, 'POINT(10 10)'::GEOMETRY);
                insert into h2gis values (4,22, 'POINT(10 10)'::GEOMETRY);
                insert into h2gis values (5,22, 'POINT(20 10)'::GEOMETRY);
                """)

        def table = h2GIS.select"COUNT(id)","code","the_geom"from"h2gis"where"h2gis.code=22"and"id<5"groupBy"code"execute()

        def values = new ArrayList<>()
        table.eachRow {row ->
                    values.add(row.getInt(1))
                    values.add(row.getInt(2))
        }
        assertEquals(2,values.size())
        assertEquals(3, (int) values.get(0))
        assertEquals(22, (int) values.get(1))

        table = h2GIS.select "*" from "h2gis" where "code=22" or "code=56" orderBy "id",DESC execute()

        def values2 = new ArrayList<>();
        table.eachRow {row -> values2.add(row.getInt(1))}
        assertEquals(5,values2.size())
        assertEquals(5, (int) values2.get(0))
        assertEquals(4, (int) values2.get(1))
        assertEquals(3, (int) values2.get(2))
        assertEquals(2, (int) values2.get(3))
        assertEquals(1, (int) values2.get(4))
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
}
