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

import groovy.lang.Closure;
import groovy.lang.GString;
import groovy.lang.MetaClass;
import groovy.sql.GroovyRowResult;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.h2gis.utilities.dbtypes.DBTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.orbisgis.data.api.dataset.ISpatialTable;
import org.orbisgis.data.api.dataset.ITable;
import org.orbisgis.data.api.dsl.IResultSetProperties;
import org.orbisgis.data.jdbc.JdbcDataSource;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class dedicated to {@link JdbcDataSource} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
class H2GISDATASOURCETEST {

    private static final String DB_NAME = "./target/H2GISDATASOURCETEST";
    private static H2GIS h2gis;
    private static POSTGIS postgis;

    private static final int SIZE = 200;
    private static final int TIMEOUT = 300;
    private static final int MAX_ROW = 400;
    private static final int FIELD_SIZE = 500;

    /**
     * Initialize three {@link JdbcDataSource} with each constructors.
     *
     * @throws SQLException Sql exception.
     */
    @BeforeEach
    void init() throws SQLException {
        h2gis = H2GIS.open(DB_NAME);
        h2gis.execute("DROP TABLE IF EXISTS test_h2gis");
        h2gis.execute("CREATE TABLE test_h2gis(id int, the_geom GEOMETRY, text varchar)");
        h2gis.execute("INSERT INTO test_h2gis VALUES (1, 'POINT(0 0)', 'toto')");
        h2gis.execute("INSERT INTO test_h2gis VALUES (2, 'LINESTRING(0 0, 1 1, 2 2)', 'tata')");
        h2gis.execute("INSERT INTO test_h2gis VALUES (3, 'POINT(4 5)', 'titi')");

        Map<String, String> pgisProps = new HashMap<>();
        pgisProps.put("databaseName", "orbisgis_db");
        pgisProps.put("user", "orbisgis");
        pgisProps.put("password", "orbisgis");
        pgisProps.put("url", "jdbc:postgresql://localhost:5432/");
        postgis = POSTGIS.open(pgisProps);
        postgis.execute("DROP TABLE IF EXISTS test_postgis");
        postgis.execute("CREATE TABLE test_postgis(id int, the_geom GEOMETRY, text varchar)");
        postgis.execute("INSERT INTO test_postgis VALUES (1, 'POINT(0 0)', 'toto')");
        postgis.execute("INSERT INTO test_postgis VALUES (2, 'LINESTRING(0 0, 1 1, 2 2)', 'tata')");
        postgis.execute("INSERT INTO test_postgis VALUES (3, 'POINT(4 5)', 'titi')");
    }

    /**
     * Test the {@link JdbcDataSource#getConnection()} method.
     */
    @Test
    void testGetConnection() {
        assertNotNull(h2gis.getConnection());
        assertNotNull(postgis.getConnection());
    }

    /**
     * Test the {@link JdbcDataSource#getDataBaseType()} method.
     */
    @Test
    void testGetDataBaseType() {
        assertEquals(DBTypes.H2GIS, h2gis.getDataBaseType());
        assertEquals(DBTypes.POSTGIS, postgis.getDataBaseType());
    }

    /**
     * Test the {@link JdbcDataSource#execute(GString)} method.
     */
    @Test
    void testExecute() throws SQLException {
        GString gstring1 = new GStringImpl(new String[]{"test_h2gis"}, new String[]{"SELECT * FROM "});
        GString gstring2 = new GStringImpl(new String[]{"test_h2gis"}, new String[]{"UPDATE ", " SET text='titi' WHERE id=3"});
        GString gstring3 = new GStringImpl(new String[]{}, new String[]{"SELECT * FROM test_h2gis"});

        assertTrue(h2gis.execute(gstring1.toString()));
        assertFalse(h2gis.execute(gstring2.toString()));
        assertTrue(h2gis.execute(gstring3.toString()));

        gstring1 = new GStringImpl(new String[]{"test_postgis"}, new String[]{"SELECT * FROM "});
        gstring2 = new GStringImpl(new String[]{"test_postgis"}, new String[]{"UPDATE ", " SET text='titi' WHERE id=3"});
        gstring3 = new GStringImpl(new String[]{}, new String[]{"SELECT * FROM test_postgis"});

        assertTrue(postgis.execute(gstring1.toString()));
        assertFalse(postgis.execute(gstring2.toString()));
        assertTrue(postgis.execute(gstring3.toString()));
    }

    /**
     * Test the {@link JdbcDataSource#firstRow(GString)} method.
     */
    @Test
    void testFirstRow() throws SQLException {
        GString gstring1_h2gis = new GStringImpl(new String[]{"test_h2gis"}, new String[]{"SELECT * FROM "});
        GString gstring3_h2gis = new GStringImpl(new String[]{}, new String[]{"SELECT * FROM test_h2gis"});
        GString gstring1_postgis = new GStringImpl(new String[]{"test_postgis"}, new String[]{"SELECT * FROM "});
        GString gstring3_postgis = new GStringImpl(new String[]{}, new String[]{"SELECT * FROM test_postgis"});


        assertFalse(h2gis.firstRow(gstring1_h2gis).isEmpty());
        assertEquals("{ID=1, THE_GEOM=POINT (0 0), TEXT=toto}", h2gis.firstRow(gstring1_h2gis).toString());
        assertFalse(postgis.firstRow(gstring1_postgis).isEmpty());
        assertEquals("{id=1, the_geom=POINT (0 0), text=toto}", postgis.firstRow(gstring1_postgis).toString());

        assertFalse(h2gis.firstRow(gstring3_h2gis).isEmpty());
        assertEquals("{ID=1, THE_GEOM=POINT (0 0), TEXT=toto}", h2gis.firstRow(gstring3_h2gis).toString());
        assertFalse(postgis.firstRow(gstring3_postgis).isEmpty());
        assertEquals("{id=1, the_geom=POINT (0 0), text=toto}", postgis.firstRow(gstring3_postgis).toString());
    }

    /**
     * Test the {@link JdbcDataSource#rows(GString)} method.
     */
    @Test
    void testRows() throws SQLException {
        GString gstring1_h2gis = new GStringImpl(new String[]{"test_h2gis"}, new String[]{"SELECT * FROM "});
        GString gstring3_h2gis = new GStringImpl(new String[]{}, new String[]{"SELECT * FROM test_h2gis"});
        GString gstring1_postgis = new GStringImpl(new String[]{"test_postgis"}, new String[]{"SELECT * FROM "});
        GString gstring3_postgis = new GStringImpl(new String[]{}, new String[]{"SELECT * FROM test_postgis"});

        List<GroovyRowResult> list = h2gis.rows(gstring1_h2gis);
        assertFalse(list.isEmpty());
        assertEquals(3, list.size());
        assertEquals("{ID=1, THE_GEOM=POINT (0 0), TEXT=toto}", list.get(0).toString());
        assertEquals("{ID=2, THE_GEOM=LINESTRING (0 0, 1 1, 2 2), TEXT=tata}", list.get(1).toString());
        assertEquals("{ID=3, THE_GEOM=POINT (4 5), TEXT=titi}", list.get(2).toString());

        list = postgis.rows(gstring1_postgis);
        assertFalse(list.isEmpty());
        assertEquals(3, list.size());
        assertEquals("{id=1, the_geom=POINT (0 0), text=toto}", list.get(0).toString());
        assertEquals("{id=2, the_geom=LINESTRING (0 0, 1 1, 2 2), text=tata}", list.get(1).toString());
        assertEquals("{id=3, the_geom=POINT (4 5), text=titi}", list.get(2).toString());

        list = h2gis.rows(gstring3_h2gis);
        assertFalse(list.isEmpty());
        assertEquals(3, list.size());
        assertEquals("{ID=1, THE_GEOM=POINT (0 0), TEXT=toto}", list.get(0).toString());
        assertEquals("{ID=2, THE_GEOM=LINESTRING (0 0, 1 1, 2 2), TEXT=tata}", list.get(1).toString());
        assertEquals("{ID=3, THE_GEOM=POINT (4 5), TEXT=titi}", list.get(2).toString());

        list = postgis.rows(gstring3_postgis);
        assertFalse(list.isEmpty());
        assertEquals(3, list.size());
        assertEquals("{id=1, the_geom=POINT (0 0), text=toto}", list.get(0).toString());
        assertEquals("{id=2, the_geom=LINESTRING (0 0, 1 1, 2 2), text=tata}", list.get(1).toString());
        assertEquals("{id=3, the_geom=POINT (4 5), text=titi}", list.get(2).toString());
    }

    /**
     * Test the {@link JdbcDataSource#eachRow(GString, Closure)} method.
     */
    @Test
    void testEachRow() throws SQLException {
        GString gstring1_h2gis = new GStringImpl(new String[]{"test_h2gis"}, new String[]{"SELECT * FROM "});
        GString gstring3_h2gis = new GStringImpl(new String[]{}, new String[]{"SELECT * FROM test_h2gis"});
        GString gstring1_postgis = new GStringImpl(new String[]{"test_postgis"}, new String[]{"SELECT * FROM "});
        GString gstring3_postgis = new GStringImpl(new String[]{}, new String[]{"SELECT * FROM test_postgis"});

        final String[] collect = {""};
        Closure cl = new Closure(this) {
            @Override
            public Object call(Object arguments) {
                collect[0] += arguments.toString() + "\n";
                return collect[0];
            }
        };

        collect[0] = "";
        h2gis.eachRow(gstring1_h2gis, cl);
        assertEquals("[ID:1, THE_GEOM:POINT (0 0), TEXT:toto]\n" +
                "[ID:2, THE_GEOM:LINESTRING (0 0, 1 1, 2 2), TEXT:tata]\n" +
                "[ID:3, THE_GEOM:POINT (4 5), TEXT:titi]\n", collect[0]);
        collect[0] = "";
        postgis.eachRow(gstring1_postgis, cl);
        assertEquals("[id:1, the_geom:POINT (0 0), text:toto]\n" +
                "[id:2, the_geom:LINESTRING (0 0, 1 1, 2 2), text:tata]\n" +
                "[id:3, the_geom:POINT (4 5), text:titi]\n", collect[0]);
        collect[0] = "";

        collect[0] = "";
        h2gis.eachRow(gstring3_h2gis, cl);
        assertEquals("[ID:1, THE_GEOM:POINT (0 0), TEXT:toto]\n" +
                "[ID:2, THE_GEOM:LINESTRING (0 0, 1 1, 2 2), TEXT:tata]\n" +
                "[ID:3, THE_GEOM:POINT (4 5), TEXT:titi]\n", collect[0]);
        collect[0] = "";
        postgis.eachRow(gstring3_postgis, cl);
        assertEquals("[id:1, the_geom:POINT (0 0), text:toto]\n" +
                "[id:2, the_geom:LINESTRING (0 0, 1 1, 2 2), text:tata]\n" +
                "[id:3, the_geom:POINT (4 5), text:titi]\n", collect[0]);
        collect[0] = "";
    }

    /**
     * Test the {@link JdbcDataSource#executeScript(String, Map)} method.
     */
    @Test
    void testExecuteMethod1() throws URISyntaxException, SQLException {
        Map<String, String> map = new HashMap<>();
        map.put("intArg", "51");
        URL url = this.getClass().getResource("simpleWithArgs.sql");
        File file = new File(url.toURI());

        assertTrue(h2gis.executeScript(file.getAbsolutePath(), map));
        assertFalse(h2gis.executeScript("toto", map));
        String str = h2gis.rows("SELECT * FROM script").stream().map(Objects::toString).collect(Collectors.joining("\n"));
        assertEquals("{ID=1}\n{ID=11}\n{ID=51}", str);
        assertTrue(h2gis.executeScript(url.toURI().toString(), map));
        str = h2gis.rows("SELECT * FROM script").stream().map(Objects::toString).collect(Collectors.joining("\n"));
        assertEquals("{ID=1}\n{ID=11}\n{ID=51}", str);
        assertTrue(h2gis.executeScript(url.toString(), map));
        str = h2gis.rows("SELECT * FROM script").stream().map(Objects::toString).collect(Collectors.joining("\n"));
        assertEquals("{ID=1}\n{ID=11}\n{ID=51}", str);

        assertTrue(postgis.executeScript(file.getAbsolutePath(), map));
        assertFalse(postgis.executeScript("toto", map));
        str = postgis.rows("SELECT * FROM script").stream().map(Objects::toString).collect(Collectors.joining("\n"));
        assertEquals("{id=1}\n{id=11}\n{id=51}", str);
        assertTrue(postgis.executeScript(url.toURI().toString(), map));
        str = postgis.rows("SELECT * FROM script").stream().map(Objects::toString).collect(Collectors.joining("\n"));
        assertEquals("{id=1}\n{id=11}\n{id=51}", str);
        assertTrue(postgis.executeScript(url.toString(), map));
        str = postgis.rows("SELECT * FROM script").stream().map(Objects::toString).collect(Collectors.joining("\n"));
        assertEquals("{id=1}\n{id=11}\n{id=51}", str);

        assertFalse(h2gis.executeScript("notAFile.sql"));
        assertFalse(postgis.executeScript("notAFile.sql"));
    }

    /**
     * Test the {@link JdbcDataSource#executeScript(InputStream, Map)} method.
     */
    @Test
    void testExecuteMethod2() throws SQLException {
        Map<String, String> map = new HashMap<>();
        map.put("intArg", "51");

        assertTrue(h2gis.executeScript(this.getClass().getResourceAsStream("simpleWithArgs.sql"), map));
        String str = h2gis.rows("SELECT * FROM script").stream().map(Objects::toString).collect(Collectors.joining("\n"));
        assertEquals("{ID=1}\n{ID=11}\n{ID=51}", str);

        assertTrue(postgis.executeScript(this.getClass().getResourceAsStream("simpleWithArgs.sql"), map));
        str = postgis.rows("SELECT * FROM script").stream().map(Objects::toString).collect(Collectors.joining("\n"));
        assertEquals("{id=1}\n{id=11}\n{id=51}", str);

        assertFalse(h2gis.executeScript(this.getClass().getResourceAsStream("badSql.sql"), map));
        assertFalse(postgis.executeScript(this.getClass().getResourceAsStream("badSql.sql"), map));

        h2gis.execute("DROP TABLE IF EXISTS script");
    }

    /**
     * Test the {@link JdbcDataSource#setMetaClass(MetaClass)} and {@link JdbcDataSource#getMetaClass()} methods.
     */
    @Test
    void testMetaClassMethods() {
        assertEquals(InvokerHelper.getMetaClass(H2GIS.class), h2gis.getMetaClass());
        h2gis.setMetaClass(InvokerHelper.getMetaClass(this));
        assertEquals(InvokerHelper.getMetaClass(this), h2gis.getMetaClass());

        assertEquals(InvokerHelper.getMetaClass(POSTGIS.class), postgis.getMetaClass());
        postgis.setMetaClass(InvokerHelper.getMetaClass(this));
        assertEquals(InvokerHelper.getMetaClass(this), postgis.getMetaClass());
    }

    /**
     * Test the save methods.
     */
    @Test
    void testSave() throws SQLException, MalformedURLException {
        h2gis.execute("DROP TABLE IF EXISTS load");
        postgis.execute("DROP TABLE IF EXISTS load");

        assertTrue(h2gis.save("test_h2gis", "target/save_path_ds1.geojson", true));
        assertTrue(new File("target/save_path_ds1.geojson").exists());

        new File("target/save_path_enc_ds1.geojson").delete();
        assertTrue(h2gis.save("test_h2gis", "target/save_path_enc_ds1.geojson", "UTF8"));
        assertTrue(new File("target/save_path_enc_ds1.geojson").exists());

        new File("target/save_url_ds1.geojson").delete();
        assertTrue(h2gis.save("test_h2gis", new File("target/save_url_ds1.geojson").toURI().toURL()));
        assertTrue(new File("target/save_url_ds1.geojson").exists());

        new File("target/save_url_enc_ds1.geojson").delete();
        assertTrue(h2gis.save("test_h2gis", new File("target/save_url_enc_ds1.geojson").toURI().toURL(), "UTF8"));
        assertTrue(new File("target/save_url_enc_ds1.geojson").exists());

        new File("target/save_uri_ds1.geojson").delete();
        assertTrue(h2gis.save("test_h2gis", new File("target/save_uri_ds1.geojson").toURI()));
        assertTrue(new File("target/save_uri_ds1.geojson").exists());

        new File("target/save_uri_enc_ds1.geojson").delete();
        assertTrue(h2gis.save("test_h2gis", new File("target/save_uri_enc_ds1.geojson").toURI(), "UTF8"));
        assertTrue(new File("target/save_uri_enc_ds1.geojson").exists());

        new File("target/save_file_ds1.geojson").delete();
        assertTrue(h2gis.save("test_h2gis", new File("target/save_file_ds1.geojson")));
        assertTrue(new File("target/save_file_ds1.geojson").exists());

        new File("target/save_file_enc_ds1.geojson").delete();
        assertTrue(h2gis.save("test_h2gis", new File("target/save_file_enc_ds1.geojson"), "UTF8"));
        assertTrue(new File("target/save_file_enc_ds1.geojson").exists());
    }

    /**
     * Test the link methods.
     */
    @Test
    void testLink() throws SQLException, URISyntaxException, MalformedURLException {
        URL url = this.getClass().getResource("linkTable.dbf");
        URI uri = url.toURI();
        File file = new File(uri);
        String path = file.getAbsolutePath();


        h2gis.execute("DROP TABLE IF EXISTS LINKTABLE");
        ITable table = h2gis.getTable(h2gis.link(path));
        assertNotNull(table);
        assertEquals("LINKTABLE", table.getName());
        postgis.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertEquals("linktable", postgis.link(path));

        h2gis.execute("DROP TABLE IF EXISTS LINKTABLE");
        table = h2gis.getTable(h2gis.link(path, true));
        assertNotNull(table);
        assertEquals("LINKTABLE", table.getName());
        postgis.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertEquals("linktable", postgis.link(path, true));


        h2gis.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertNotNull(h2gis.link(path, "LINKTABLE"));
        postgis.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertEquals("linktable", postgis.link(path, "LINKTABLE"));

        h2gis.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertNotNull(h2gis.link(path, "LINKTABLE", true));
        postgis.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertEquals("linktable", postgis.link(path, "LINKTABLE", true));

        h2gis.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertNull(h2gis.link("$toto", true));
        postgis.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertNull(postgis.link("$toto", true));


        h2gis.execute("DROP TABLE IF EXISTS LINKTABLE");
        table = h2gis.getTable(h2gis.link(url));
        assertNotNull(table);
        assertEquals("LINKTABLE", table.getName());
        postgis.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertEquals("linktable", postgis.link(url));

        h2gis.execute("DROP TABLE IF EXISTS LINKTABLE");
        table = h2gis.getTable(h2gis.link(url, true));
        assertNotNull(table);
        assertEquals("LINKTABLE", table.getName());
        postgis.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertEquals("linktable", postgis.link(url, true));


        h2gis.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertNotNull(h2gis.link(url, "LINKTABLE"));
        postgis.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertEquals("linktable", postgis.link(url, "LINKTABLE"));

        h2gis.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertNotNull(h2gis.link(url, "LINKTABLE", true));
        postgis.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertEquals("linktable", postgis.link(url, "LINKTABLE", true));


        h2gis.execute("DROP TABLE IF EXISTS LINKTABLE");
        table = h2gis.getTable(h2gis.link(uri));
        assertNotNull(table);
        assertEquals("LINKTABLE", table.getName());
        postgis.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertEquals("linktable", postgis.link(uri));

        h2gis.execute("DROP TABLE IF EXISTS LINKTABLE");
        table = h2gis.getTable(h2gis.link(uri, true));
        assertNotNull(table);
        assertEquals("LINKTABLE", table.getName());
        postgis.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertEquals("linktable", postgis.link(uri, true));


        h2gis.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertNotNull(h2gis.link(uri, "LINKTABLE"));
        postgis.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertEquals("linktable", postgis.link(uri, "LINKTABLE"));

        h2gis.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertNotNull(h2gis.link(uri, "LINKTABLE", true));
        postgis.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertEquals("linktable", postgis.link(uri, "LINKTABLE", true));


        h2gis.execute("DROP TABLE IF EXISTS LINKTABLE");
        table = h2gis.getTable(h2gis.link(file));
        assertNotNull(table);
        assertEquals("LINKTABLE", table.getName());
        postgis.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertEquals("linktable", postgis.link(file));

        h2gis.execute("DROP TABLE IF EXISTS LINKTABLE");
        table = h2gis.getTable(h2gis.link(file, true));
        assertNotNull(table);
        assertEquals("LINKTABLE", table.getName());
        postgis.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertEquals("linktable", postgis.link(file, true));


        h2gis.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertNotNull(h2gis.link(file, "LINKTABLE"));
        postgis.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertEquals("linktable", postgis.link(file, "LINKTABLE"));

        h2gis.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertNotNull(h2gis.link(file, "LINKTABLE", true));
        postgis.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertEquals("linktable", postgis.link(file, "LINKTABLE", true));
    }

    /**
     * Test the load from an existing database methods.
     */
    @Disabled
    @Test
    void loadFromDB() throws SQLException {
        String tableNameDS1 = "test_h2gis";
        String tableNameDS2 = "test_postgis";

        h2gis.execute("DROP TABLE IF EXISTS " + tableNameDS2);
        ITable table = h2gis.getTable(h2gis.load(postgis, tableNameDS2));
        assertNotNull(table);
        assertEquals(tableNameDS2.toUpperCase(), table.getName());
        postgis.execute("DROP TABLE IF EXISTS " + tableNameDS1);
        String loadedTable = postgis.load(h2gis, tableNameDS1.toUpperCase());
        assertEquals(tableNameDS1, loadedTable);
        table = postgis.getTable(loadedTable);
        assertNotNull(table);
        assertEquals(tableNameDS1, table.getName());

        assertNull(h2gis.load(postgis, tableNameDS2, false));
        assertNull(postgis.load(h2gis, tableNameDS1, false));

        String tableNameDS1_new = "test_h2gis_imported";
        String tableNameDS2_new = "test_postgis_imported";

        h2gis.execute("DROP TABLE IF EXISTS " + tableNameDS2_new);
        table = h2gis.getTable(h2gis.load(postgis, tableNameDS2, tableNameDS2_new));
        assertNotNull(table);
        assertEquals(tableNameDS2_new.toUpperCase(), table.getName());
        postgis.execute("DROP TABLE IF EXISTS " + tableNameDS1_new);
        table = postgis.getTable(postgis.load(h2gis, tableNameDS1, tableNameDS1_new));
        assertNotNull(table);
        assertEquals(tableNameDS1_new, table.getName());

        table = h2gis.getTable(h2gis.load(postgis, tableNameDS2, tableNameDS2_new, true));
        assertNotNull(table);
        assertEquals(tableNameDS2_new.toUpperCase(), table.getName());
        table =  postgis.getTable(postgis.load(h2gis, tableNameDS1, tableNameDS1_new, true));
        assertNotNull(table);
        assertEquals(tableNameDS1_new, table.getName());
    }


    /**
     * Test the load file path methods.
     */
    @Test
    void testLoadPath() throws SQLException, URISyntaxException {
        URL url = this.getClass().getResource("loadTable.dbf");
        URI uri = url.toURI();
        File file = new File(uri);
        String path = file.getAbsolutePath();
        String name = "NAME";

        //Test path
        h2gis.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        ITable table = h2gis.getTable(h2gis.load(path));
        assertNotNull(table);
        assertEquals("LOADTABLE", table.getName());
        h2gis.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = h2gis.getTable(h2gis.load(path, name));
        assertNotNull(table);
        assertEquals(name, table.getName());
        h2gis.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = h2gis.getTable(h2gis.load(path, name, true));
        assertNotNull(table);
        assertEquals(name, table.getName());
        h2gis.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = h2gis.getTable(h2gis.load(path, name, "UTF8", true));
        assertNotNull(table);
        assertEquals(name, table.getName());

        postgis.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = postgis.getTable(postgis.load(path));
        assertNotNull(table);
        assertEquals("loadtable", table.getName());
        postgis.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = postgis.getTable(postgis.load(path, name));
        assertNotNull(table);
        assertEquals(name.toLowerCase(), table.getName());
        postgis.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = postgis.getTable(postgis.load(path, name, true));
        assertNotNull(table);
        assertEquals(name.toLowerCase(), table.getName());
        postgis.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = postgis.getTable(postgis.load(path, name, "UTF8", true));
        assertNotNull(table);
        assertEquals(name.toLowerCase(), table.getName());
        postgis.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = postgis.getTable(postgis.load(path, name.toLowerCase(), "UTF8", true));
        assertNotNull(table);
        assertEquals(name.toLowerCase(), table.getName());

        //Test URL
        h2gis.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = h2gis.getTable(h2gis.load(url));
        assertNotNull(table);
        assertEquals("LOADTABLE", table.getName());
        h2gis.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = h2gis.getTable(h2gis.load(url, name));
        assertNotNull(table);
        assertEquals(name, table.getName());
        h2gis.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = h2gis.getTable(h2gis.load(url, name, true));
        assertNotNull(table);
        assertEquals(name, table.getName());
        h2gis.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = h2gis.getTable(h2gis.load(url, true));
        assertNotNull(table);
        assertEquals("LOADTABLE", table.getName());
        h2gis.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = h2gis.getTable(h2gis.load(url, name, "UTF8", true));
        assertNotNull(table);
        assertEquals(name, table.getName());

        postgis.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = postgis.getTable(postgis.load(url));
        assertNotNull(table);
        assertEquals("loadtable", table.getName());
        postgis.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = postgis.getTable(postgis.load(url, name));
        assertNotNull(table);
        assertEquals(name.toLowerCase(), table.getName());
        postgis.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = postgis.getTable(postgis.load(url, true));
        assertNotNull(table);
        assertEquals("loadtable", table.getName());
        postgis.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = postgis.getTable(postgis.load(url, name, true));
        assertNotNull(table);
        assertEquals(name.toLowerCase(), table.getName());
        postgis.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = postgis.getTable(postgis.load(url, name, "UTF8", true));
        assertNotNull(table);
        assertEquals(name.toLowerCase(), table.getName());
        postgis.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = postgis.getTable(postgis.load(url, name.toLowerCase(), "UTF8", true));
        assertNotNull(table);
        assertEquals(name.toLowerCase(), table.getName());

        //Test URI
        h2gis.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = h2gis.getTable(h2gis.load(uri));
        assertNotNull(table);
        assertEquals("LOADTABLE", table.getName());
        h2gis.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = h2gis.getTable(h2gis.load(uri, name));
        assertNotNull(table);
        assertEquals(name, table.getName());
        h2gis.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = h2gis.getTable(h2gis.load(uri, name, true));
        assertNotNull(table);
        assertEquals(name, table.getName());
        h2gis.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = h2gis.getTable(h2gis.load(uri, name, "UTF8", true));
        assertNotNull(table);
        assertEquals(name, table.getName());

        postgis.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = postgis.getTable(postgis.load(uri));
        assertNotNull(table);
        assertEquals("loadtable", table.getName());
        postgis.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = postgis.getTable(postgis.load(uri, name));
        assertNotNull(table);
        assertEquals(name.toLowerCase(), table.getName());
        postgis.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = postgis.getTable(postgis.load(uri, name, true));
        assertNotNull(table);
        assertEquals(name.toLowerCase(), table.getName());
        postgis.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = postgis.getTable(postgis.load(uri, name, "UTF8", true));
        assertNotNull(table);
        assertEquals(name.toLowerCase(), table.getName());

        //Test File
        h2gis.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = h2gis.getTable(h2gis.load(file));
        assertNotNull(table);
        assertEquals("LOADTABLE", table.getName());
        h2gis.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = h2gis.getTable(h2gis.load(file, name));
        assertNotNull(table);
        assertEquals(name, table.getName());
        h2gis.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = h2gis.getTable(h2gis.load(file, name, true));
        assertNotNull(table);
        assertEquals(name, table.getName());
        h2gis.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = h2gis.getTable(h2gis.load(file, name, "UTF8", true));
        assertNotNull(table);
        assertEquals(name, table.getName());

        postgis.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = postgis.getTable(postgis.load(file));
        assertNotNull(table);
        assertEquals("loadtable", table.getName());
        postgis.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = postgis.getTable(postgis.load(file, name));
        assertNotNull(table);
        assertEquals(name.toLowerCase(), table.getName());
        postgis.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = postgis.getTable(postgis.load(file, name, true));
        assertNotNull(table);
        assertEquals(name.toLowerCase(), table.getName());
        postgis.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = postgis.getTable(postgis.load(file, name, "UTF8", true));
        assertNotNull(table);
        assertEquals(name.toLowerCase(), table.getName());

        //Test bad name
        h2gis.load("4file.dbf");

        h2gis.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
    }

    /**
     * Test the {@link JdbcDataSource#getLocation()} method.
     */
    @Test
    void testGetLocation() {
        assertNotNull(h2gis.getLocation());
        assertEquals(DB_NAME, h2gis.getLocation().toString());
        assertNotNull(postgis.getLocation());
        assertTrue(postgis.getLocation().toString().startsWith("5432/"));
    }

    /**
     * Test the {@link JdbcDataSource#getTableNames()} method.
     */
    @Test
    void testGetTableNames() {
        Collection<String> names = h2gis.getTableNames();
        assertNotNull(names);
        assertTrue(names.size()>0);
        assertTrue(names.contains("H2GISDATASOURCETEST.PUBLIC.GEOMETRY_COLUMNS"));
        assertTrue(names.contains("H2GISDATASOURCETEST.PUBLIC.TEST_H2GIS"));
        assertTrue(names.contains("H2GISDATASOURCETEST.PUBLIC.SPATIAL_REF_SYS"));

        names = postgis.getTableNames();
        assertNotNull(names);
        assertTrue(names.size()>0);
        assertTrue(names.contains("public.test_postgis"));
        assertTrue(names.contains("public.spatial_ref_sys"));
    }

    /**
     * Test the {@link JdbcDataSource#getColumnNames(String)} method.
     */
    @Test
    void testGetColumnNames() {
        Collection<String> names = h2gis.getColumnNames("H2GISDATASOURCETEST.PUBLIC.TEST_H2GIS");
        assertNotNull(names);
        assertEquals(3, names.size());
        assertTrue(names.contains("ID"));
        assertTrue(names.contains("THE_GEOM"));
        assertTrue(names.contains("TEXT"));
        names = h2gis.getColumnNames("PUBLIC.TEST_H2GIS");
        assertNotNull(names);
        assertEquals(3, names.size());
        assertTrue(names.contains("ID"));
        assertTrue(names.contains("THE_GEOM"));
        assertTrue(names.contains("TEXT"));
        names = h2gis.getColumnNames("TEST_H2GIS");
        assertNotNull(names);
        assertEquals(3, names.size());
        assertTrue(names.contains("ID"));
        assertTrue(names.contains("THE_GEOM"));
        assertTrue(names.contains("TEXT"));

        names = postgis.getColumnNames("public.test_postgis");
        assertTrue(names.contains("id"));
        assertTrue(names.contains("the_geom"));
        assertTrue(names.contains("text"));
        /*names = postgis.getColumnNames("public.test_postgis");
        assertTrue(names.contains("id"));
        assertTrue(names.contains("the_geom"));
        assertTrue(names.contains("text"));
        names = postgis.getColumnNames("test_postgis");
        assertTrue(names.contains("id"));
        assertTrue(names.contains("the_geom"));
        assertTrue(names.contains("text"));*/
    }

    /**
     * Test the {@link JdbcDataSource#hasTable(String)} method.
     */
    @Test
    void testHasTable() {
        Collection<String> names = h2gis.getTableNames();
        assertNotNull(names);
        assertTrue(names.size()>0);
        assertTrue(h2gis.hasTable("GEOMETRY_COLUMNS"));
        assertTrue(h2gis.hasTable("TEST_H2GIS"));
        assertTrue(h2gis.hasTable("H2GISDATASOURCETEST.PUBLIC.SPATIAL_REF_SYS"));
    }

    /**
     * Test the {@link JdbcDataSource#getDataSet(String)} method.
     */
    @Test
    void testGetDataSet() {
        Object dataset = h2gis.getDataSet("TEST_H2GIS");
        assertTrue(dataset instanceof ISpatialTable);
        dataset = h2gis.getDataSet("GEOMETRY_COLUMNS");
        assertNotNull(dataset);

        dataset = postgis.getDataSet("test_postgis");
        assertTrue(dataset instanceof ISpatialTable);
        dataset = postgis.getDataSet("geometry_columns");
        assertNotNull(dataset);
    }

    /**
     * Test the {@link JdbcDataSource#forwardOnly()}, {@link JdbcDataSource#scrollInsensitive()},
     * {@link JdbcDataSource#scrollSensitive()} ()}, {@link JdbcDataSource#updatable()},
     * {@link JdbcDataSource#readOnly()}, {@link JdbcDataSource#holdCursorOverCommit()},
     * {@link JdbcDataSource#closeCursorAtCommit()}, {@link JdbcDataSource#fetchForward()},
     * {@link JdbcDataSource#fetchReverse()}, {@link JdbcDataSource#fetchUnknown()},
     * {@link JdbcDataSource#fetchSize(int)}, {@link JdbcDataSource#timeout(int)},
     * {@link JdbcDataSource# maxRow(int)}, {@link JdbcDataSource#cursorName(String)},
     * {@link JdbcDataSource#poolable()}, {@link JdbcDataSource#maxFieldSize(int)}france methods.
     */
    @Test
    void testResultSetBuilder() {
        ITable<?,?> table = h2gis.forwardOnly().readOnly().holdCursorOverCommit().fetchForward()
                .fetchSize(SIZE).timeout(TIMEOUT).maxRow(MAX_ROW).cursorName("name").poolable()
                .maxFieldSize(FIELD_SIZE).getTable("TEST_H2GIS");
        assertNotNull(table);
        assertArrayEquals(new int[]{3, 3}, table.getSize());
        assertTrue(table instanceof H2gisSpatialTable);
        IResultSetProperties rsp = ((H2gisSpatialTable)table).getResultSetProperties();
        assertEquals(ResultSet.TYPE_FORWARD_ONLY, rsp.getType());
        assertEquals(ResultSet.CONCUR_READ_ONLY, rsp.getConcurrency());
        assertEquals(ResultSet.HOLD_CURSORS_OVER_COMMIT, rsp.getHoldability());
        assertEquals(ResultSet.FETCH_FORWARD, rsp.getFetchDirection());
        assertEquals(SIZE, rsp.getFetchSize());
        assertEquals(TIMEOUT, rsp.getTimeout());
        assertEquals(MAX_ROW, rsp.getMaxRows());
        assertEquals("name", rsp.getCursorName());
        assertTrue(rsp.isPoolable());
        assertEquals(FIELD_SIZE, rsp.getMaxFieldSize());

        table = postgis.scrollInsensitive().readOnly().holdCursorOverCommit().fetchReverse()
                .fetchSize(SIZE).timeout(TIMEOUT).maxRow(MAX_ROW).cursorName("name").poolable()
                .maxFieldSize(FIELD_SIZE).getTable("test_postgis");
        assertNotNull(table);
        assertArrayEquals(new int[]{3, 3}, table.getSize());
        assertTrue(table instanceof PostgisSpatialTable);
        rsp = ((PostgisSpatialTable)table).getResultSetProperties();
        assertEquals(ResultSet.TYPE_SCROLL_INSENSITIVE, rsp.getType());
        assertEquals(ResultSet.CONCUR_READ_ONLY, rsp.getConcurrency());
        assertEquals(ResultSet.HOLD_CURSORS_OVER_COMMIT, rsp.getHoldability());
        assertEquals(ResultSet.FETCH_REVERSE, rsp.getFetchDirection());
        assertEquals(SIZE, rsp.getFetchSize());
        assertEquals(TIMEOUT, rsp.getTimeout());
        assertEquals(MAX_ROW, rsp.getMaxRows());
        assertEquals("name", rsp.getCursorName());
        assertTrue(rsp.isPoolable());
        assertEquals(FIELD_SIZE, rsp.getMaxFieldSize());
    }
}
