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
package org.orbisgis.orbisdata.datamanager.jdbc;

import groovy.lang.Closure;
import groovy.lang.GString;
import groovy.lang.MetaClass;
import groovy.sql.GroovyRowResult;
import groovy.sql.Sql;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.h2gis.functions.factory.H2GISDBFactory;
import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.wrapper.ConnectionWrapper;
import org.h2gis.utilities.wrapper.StatementWrapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.commons.annotations.Nullable;
import org.orbisgis.orbisdata.datamanager.api.dataset.*;
import org.orbisgis.orbisdata.datamanager.api.dsl.IFromBuilder;
import org.orbisgis.orbisdata.datamanager.jdbc.h2gis.H2GIS;
import org.orbisgis.orbisdata.datamanager.jdbc.h2gis.H2gisSpatialTable;
import org.orbisgis.orbisdata.datamanager.jdbc.h2gis.H2gisTable;

import javax.sql.DataSource;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class dedicated to {@link JdbcDataSource} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
class JdbcDataSourceTest {

    private static final String DB_NAME = "target/JdbcDataSourceTest";
    private static final String DB_LINK_NAME = "./target/dbToLink";
    private static DummyJdbcDataSource ds1;
    private static DummyJdbcDataSource ds2;

    @BeforeAll
    static void beforeAll() throws SQLException {
        H2GIS h2gis = H2GIS.open(DB_LINK_NAME);
        h2gis.execute("DROP TABLE IF EXISTS linkedtable");
        h2gis.execute("CREATE TABLE linkedtable(id int, the_geom GEOMETRY, text varchar)");
        h2gis.execute("INSERT INTO linkedtable VALUES (1, 'POINT(0 0)', 'toto')");
    }

    /**
     * Initialize three {@link JdbcDataSource} with each constructors.
     *
     * @throws SQLException Sql exception.
     */
    @BeforeEach
    void init() throws SQLException {
        DataSource dataSource = H2GISDBFactory.createDataSource(new File(DB_NAME).toURI().toString(), true);
        Connection connection = dataSource.getConnection();

        ds1 = new DummyJdbcDataSource(dataSource, DataBaseType.H2GIS);
        ds2 = new DummyJdbcDataSource(connection, DataBaseType.POSTGIS);

        Statement st = dataSource.getConnection().createStatement();
        st.execute("DROP TABLE IF EXISTS test");
        st.execute("CREATE TABLE test(id int, the_geom GEOMETRY, text varchar)");
        st.execute("INSERT INTO test VALUES (1, 'POINT(0 0)', 'toto')");
        st.execute("INSERT INTO test VALUES (2, 'LINESTRING(0 0, 1 1, 2 2)', 'tata')");
        st.execute("INSERT INTO test VALUES (3, 'POINT(4 5)', 'titi')");
    }

    /**
     * Test the methods coming from {@link DataSource}.
     */
    @Test
    void testDataSourceMethods() throws SQLException {
        assertNotNull(ds1.getDataSource());
        assertNotNull(ds1.getConnection("sa", "sa"));
        assertEquals(ds1.getLogWriter(), ds1.getLogWriter());
        ds1.setLogWriter(null);
        assertNull(ds1.getLogWriter());
        assertEquals(ds1.getDataSource().getLoginTimeout(), ds1.getLoginTimeout());
        ds1.setLoginTimeout(123456);
        assertEquals(123456, ds1.getLoginTimeout());
        assertEquals(ds1.getDataSource().unwrap(DataSource.class), ds1.unwrap(DataSource.class));
        assertEquals(ds1.getDataSource().isWrapperFor(DataSource.class), ds1.isWrapperFor(DataSource.class));
        assertEquals(ds1.getDataSource().getParentLogger(), ds1.getParentLogger());

        assertNull(ds2.getDataSource());
        assertNull(ds2.getConnection("sa", "sa"));
        assertNull(ds2.getLogWriter());
        ds2.setLogWriter(null);
        assertNull(ds2.getLogWriter());
        assertEquals(-1, ds2.getLoginTimeout());
        ds2.setLoginTimeout(123456);
        assertEquals(-1, ds2.getLoginTimeout());
        assertNull(ds2.unwrap(DataSource.class));
        assertFalse(ds2.isWrapperFor(DataSource.class));
        assertNull(ds2.getParentLogger());
    }

    /**
     * Test the {@link JdbcDataSource#getConnection()} method.
     */
    @Test
    void testGetConnection() {
        assertNotNull(ds1.getConnection());
        assertNotNull(ds2.getConnection());
    }

    /**
     * Test the {@link JdbcDataSource#getDataBaseType()} method.
     */
    @Test
    void testGetDataBaseType() {
        assertEquals(DataBaseType.H2GIS, ds1.getDataBaseType());
        assertEquals(DataBaseType.POSTGIS, ds2.getDataBaseType());
    }

    /**
     * Test the {@link JdbcDataSource#execute(GString)} method.
     */
    @Test
    void testExecute() throws SQLException {
        GString gstring1 = new GStringImpl(new String[]{"test"}, new String[]{"SELECT * FROM "});
        GString gstring2 = new GStringImpl(new String[]{"test"}, new String[]{"UPDATE ", " SET text='titi' WHERE id=3"});
        GString gstring3 = new GStringImpl(new String[]{}, new String[]{"SELECT * FROM test"});

        assertTrue(ds1.execute(gstring1));
        assertFalse(ds1.execute(gstring2));
        assertTrue(ds1.execute(gstring3));

        assertTrue(ds2.execute(gstring1));
        assertFalse(ds2.execute(gstring2));
        assertTrue(ds2.execute(gstring3));
    }

    /**
     * Test the {@link JdbcDataSource#firstRow(GString)} method.
     */
    @Test
    void testFirstRow() throws SQLException {
        GString gstring1 = new GStringImpl(new String[]{"test"}, new String[]{"SELECT * FROM "});
        GString gstring3 = new GStringImpl(new String[]{}, new String[]{"SELECT * FROM test"});

        assertFalse(ds1.firstRow(gstring1).isEmpty());
        assertEquals("{ID=1, THE_GEOM=POINT (0 0), TEXT=toto}", ds1.firstRow(gstring1).toString());
        assertFalse(ds2.firstRow(gstring1).isEmpty());
        assertEquals("{ID=1, THE_GEOM=POINT (0 0), TEXT=toto}", ds2.firstRow(gstring1).toString());

        assertFalse(ds1.firstRow(gstring3).isEmpty());
        assertEquals("{ID=1, THE_GEOM=POINT (0 0), TEXT=toto}", ds1.firstRow(gstring3).toString());
        assertFalse(ds2.firstRow(gstring3).isEmpty());
        assertEquals("{ID=1, THE_GEOM=POINT (0 0), TEXT=toto}", ds2.firstRow(gstring3).toString());
    }

    /**
     * Test the {@link JdbcDataSource#rows(GString)} method.
     */
    @Test
    void testRows() throws SQLException {
        GString gstring1 = new GStringImpl(new String[]{"test"}, new String[]{"SELECT * FROM "});
        GString gstring3 = new GStringImpl(new String[]{}, new String[]{"SELECT * FROM test"});

        testRows(ds1.rows(gstring1));
        testRows(ds2.rows(gstring1));

        testRows(ds1.rows(gstring3));
        testRows(ds2.rows(gstring3));
    }

    /**
     * Test all the rows from the given list.
     *
     * @param list List containing the rows to test.
     */
    private void testRows(List<GroovyRowResult> list) {
        assertFalse(list.isEmpty());
        assertEquals(3, list.size());
        assertEquals("{ID=1, THE_GEOM=POINT (0 0), TEXT=toto}", list.get(0).toString());
        assertEquals("{ID=2, THE_GEOM=LINESTRING (0 0, 1 1, 2 2), TEXT=tata}", list.get(1).toString());
        assertEquals("{ID=3, THE_GEOM=POINT (4 5), TEXT=titi}", list.get(2).toString());
    }

    /**
     * Test the {@link JdbcDataSource#eachRow(GString, Closure)} method.
     */
    @Test
    void testEachRow() throws SQLException {
        GString gstring1 = new GStringImpl(new String[]{"test"}, new String[]{"SELECT * FROM "});
        GString gstring3 = new GStringImpl(new String[]{}, new String[]{"SELECT * FROM test"});
        final String[] collect = {""};
        Closure cl = new Closure(this) {
            @Override
            public Object call(Object arguments) {
                collect[0] += arguments.toString() + "\n";
                return collect[0];
            }
        };

        collect[0] = "";
        ds1.eachRow(gstring1, cl);
        assertEquals("[ID:1, THE_GEOM:POINT (0 0), TEXT:toto]\n" +
                "[ID:2, THE_GEOM:LINESTRING (0 0, 1 1, 2 2), TEXT:tata]\n" +
                "[ID:3, THE_GEOM:POINT (4 5), TEXT:titi]\n", collect[0]);
        collect[0] = "";
        ds2.eachRow(gstring1, cl);
        assertEquals("[ID:1, THE_GEOM:POINT (0 0), TEXT:toto]\n" +
                "[ID:2, THE_GEOM:LINESTRING (0 0, 1 1, 2 2), TEXT:tata]\n" +
                "[ID:3, THE_GEOM:POINT (4 5), TEXT:titi]\n", collect[0]);
        collect[0] = "";

        collect[0] = "";
        ds1.eachRow(gstring3, cl);
        assertEquals("[ID:1, THE_GEOM:POINT (0 0), TEXT:toto]\n" +
                "[ID:2, THE_GEOM:LINESTRING (0 0, 1 1, 2 2), TEXT:tata]\n" +
                "[ID:3, THE_GEOM:POINT (4 5), TEXT:titi]\n", collect[0]);
        collect[0] = "";
        ds2.eachRow(gstring3, cl);
        assertEquals("[ID:1, THE_GEOM:POINT (0 0), TEXT:toto]\n" +
                "[ID:2, THE_GEOM:LINESTRING (0 0, 1 1, 2 2), TEXT:tata]\n" +
                "[ID:3, THE_GEOM:POINT (4 5), TEXT:titi]\n", collect[0]);
        collect[0] = "";
    }

    /**
     * Test the {@link JdbcDataSource#select(String...)} method.
     */
    @Test
    void testSelect() throws NoSuchFieldException, IllegalAccessException {
        assertEquals("SELECT *  ", getQuery(ds1.select()));
        assertEquals("SELECT toto,tata ", getQuery(ds1.select("toto", "tata")));
        assertEquals("SELECT *  ", getQuery(ds1.select((String[]) null)));
        assertEquals("SELECT *  ", getQuery(ds1.select()));
        assertEquals("SELECT *  ", getQuery(ds2.select()));
        assertEquals("SELECT toto,tata ", getQuery(ds2.select("toto", "tata")));
        assertEquals("SELECT *  ", getQuery(ds2.select((String[]) null)));
        assertEquals("SELECT *  ", getQuery(ds2.select()));
    }

    /**
     * Return the string query from a {@link IFromBuilder}.
     *
     * @param builder {@link IFromBuilder}.
     * @return The string query.
     * @throws NoSuchFieldException   Exception thrown if the field doesn't exists.
     * @throws IllegalAccessException Exception thrown if the access is illegal.
     */
    private String getQuery(IFromBuilder builder) throws NoSuchFieldException, IllegalAccessException {
        Field f = builder.getClass().getDeclaredField("query");
        f.setAccessible(true);
        return f.get(builder).toString();
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

        assertTrue(ds1.executeScript(file.getAbsolutePath(), map));
        assertFalse(ds1.executeScript("toto", map));
        String str = ds1.rows("SELECT * FROM script").stream().map(Objects::toString).collect(Collectors.joining("\n"));
        assertEquals("{ID=1}\n{ID=11}\n{ID=51}", str);
        assertTrue(ds1.executeScript(url.toURI().toString(), map));
        str = ds1.rows("SELECT * FROM script").stream().map(Objects::toString).collect(Collectors.joining("\n"));
        assertEquals("{ID=1}\n{ID=11}\n{ID=51}", str);
        assertTrue(ds1.executeScript(url.toString(), map));
        str = ds1.rows("SELECT * FROM script").stream().map(Objects::toString).collect(Collectors.joining("\n"));
        assertEquals("{ID=1}\n{ID=11}\n{ID=51}", str);

        assertTrue(ds2.executeScript(file.getAbsolutePath(), map));
        assertFalse(ds2.executeScript("toto", map));
        str = ds2.rows("SELECT * FROM script").stream().map(Objects::toString).collect(Collectors.joining("\n"));
        assertEquals("{ID=1}\n{ID=11}\n{ID=51}", str);
        assertTrue(ds2.executeScript(url.toURI().toString(), map));
        str = ds2.rows("SELECT * FROM script").stream().map(Objects::toString).collect(Collectors.joining("\n"));
        assertEquals("{ID=1}\n{ID=11}\n{ID=51}", str);
        assertTrue(ds2.executeScript(url.toString(), map));
        str = ds2.rows("SELECT * FROM script").stream().map(Objects::toString).collect(Collectors.joining("\n"));
        assertEquals("{ID=1}\n{ID=11}\n{ID=51}", str);

        assertFalse(ds1.executeScript("notAFile.sql"));
        assertFalse(ds2.executeScript("notAFile.sql"));
    }

    /**
     * Test the {@link JdbcDataSource#executeScript(InputStream, Map)} method.
     */
    @Test
    void testExecuteMethod2() throws SQLException {
        Map<String, String> map = new HashMap<>();
        map.put("intArg", "51");

        assertTrue(ds1.executeScript(this.getClass().getResourceAsStream("simpleWithArgs.sql"), map));
        String str = ds1.rows("SELECT * FROM script").stream().map(Objects::toString).collect(Collectors.joining("\n"));
        assertEquals("{ID=1}\n{ID=11}\n{ID=51}", str);

        assertTrue(ds2.executeScript(this.getClass().getResourceAsStream("simpleWithArgs.sql"), map));
        str = ds2.rows("SELECT * FROM script").stream().map(Objects::toString).collect(Collectors.joining("\n"));
        assertEquals("{ID=1}\n{ID=11}\n{ID=51}", str);

        assertFalse(ds1.executeScript(this.getClass().getResourceAsStream("badSql.sql"), map));
        assertFalse(ds2.executeScript(this.getClass().getResourceAsStream("badSql.sql"), map));

        ds1.execute("DROP TABLE IF EXISTS script");
    }

    /**
     * Test the {@link JdbcDataSource#setMetaClass(MetaClass)} and {@link JdbcDataSource#getMetaClass()} methods.
     */
    @Test
    void testMetaClassMethods() {
        assertEquals(InvokerHelper.getMetaClass(DummyJdbcDataSource.class), ds1.getMetaClass());
        ds1.setMetaClass(InvokerHelper.getMetaClass(this));
        assertEquals(InvokerHelper.getMetaClass(this), ds1.getMetaClass());

        assertEquals(InvokerHelper.getMetaClass(DummyJdbcDataSource.class), ds2.getMetaClass());
        ds2.setMetaClass(InvokerHelper.getMetaClass(this));
        assertEquals(InvokerHelper.getMetaClass(this), ds2.getMetaClass());
    }

    /**
     * Test the save methods.
     */
    @Test
    void testSave() throws SQLException, MalformedURLException {
        ds1.execute("DROP TABLE IF EXISTS load");
        ds2.execute("DROP TABLE IF EXISTS load");

        assertTrue(ds1.save("test", "target/save_path_ds1.geojson"));
        assertTrue(ds2.save("test", "target/save_path_ds2.geojson"));
        assertTrue(new File("target/save_path_ds1.geojson").exists());
        assertTrue(new File("target/save_path_ds2.geojson").exists());

        assertTrue(ds1.save("test", "target/save_path_enc_ds1.geojson", "UTF8"));
        assertTrue(ds2.save("test", "target/save_path_enc_ds2.geojson", "UTF8"));
        assertTrue(new File("target/save_path_enc_ds1.geojson").exists());
        assertTrue(new File("target/save_path_enc_ds2.geojson").exists());

        assertTrue(ds1.save("test", new File("target/save_url_ds1.geojson").toURI().toURL()));
        assertTrue(ds2.save("test", new File("target/save_url_ds2.geojson").toURI().toURL()));
        assertTrue(new File("target/save_url_ds1.geojson").exists());
        assertTrue(new File("target/save_url_ds2.geojson").exists());

        assertTrue(ds1.save("test", new File("target/save_url_enc_ds1.geojson").toURI().toURL(), "UTF8"));
        assertTrue(ds2.save("test", new File("target/save_url_enc_ds2.geojson").toURI().toURL(), "UTF8"));
        assertTrue(new File("target/save_url_enc_ds1.geojson").exists());
        assertTrue(new File("target/save_url_enc_ds2.geojson").exists());

        assertTrue(ds1.save("test", new File("target/save_uri_ds1.geojson").toURI()));
        assertTrue(ds2.save("test", new File("target/save_uri_ds2.geojson").toURI()));
        assertTrue(new File("target/save_uri_ds1.geojson").exists());
        assertTrue(new File("target/save_uri_ds2.geojson").exists());

        assertTrue(ds1.save("test", new File("target/save_uri_enc_ds1.geojson").toURI(), "UTF8"));
        assertTrue(ds2.save("test", new File("target/save_uri_enc_ds2.geojson").toURI(), "UTF8"));
        assertTrue(new File("target/save_uri_enc_ds1.geojson").exists());
        assertTrue(new File("target/save_uri_enc_ds2.geojson").exists());

        assertTrue(ds1.save("test", new File("target/save_file_ds1.geojson")));
        assertTrue(ds2.save("test", new File("target/save_file_ds2.geojson")));
        assertTrue(new File("target/save_file_ds1.geojson").exists());
        assertTrue(new File("target/save_file_ds2.geojson").exists());

        assertTrue(ds1.save("test", new File("target/save_file_enc_ds1.geojson"), "UTF8"));
        assertTrue(ds2.save("test", new File("target/save_file_enc_ds2.geojson"), "UTF8"));
        assertTrue(new File("target/save_file_enc_ds1.geojson").exists());
        assertTrue(new File("target/save_file_enc_ds2.geojson").exists());
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


        ds1.execute("DROP TABLE IF EXISTS LINKTABLE");
        ITable table = ds1.link(path);
        assertNotNull(table);
        assertEquals("LINKTABLE", table.getName());
        ds2.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertNull(ds2.link(path));

        ds1.execute("DROP TABLE IF EXISTS LINKTABLE");
        table = ds1.link(path, true);
        assertNotNull(table);
        assertEquals("LINKTABLE", table.getName());
        ds2.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertNull(ds2.link(path, true));


        ds1.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertNotNull(ds1.link(path, "LINKTABLE"));
        ds2.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertNull(ds2.link(path, "LINKTABLE"));

        ds1.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertNotNull(ds1.link(path, "LINKTABLE", true));
        ds2.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertNull(ds2.link(path, "LINKTABLE", true));

        ds1.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertNull(ds1.link("$toto", true));
        ds2.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertNull(ds2.link("$toto", true));


        ds1.execute("DROP TABLE IF EXISTS LINKTABLE");
        table = ds1.link(url);
        assertNotNull(table);
        assertEquals("LINKTABLE", table.getName());
        ds2.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertNull(ds2.link(url));

        ds1.execute("DROP TABLE IF EXISTS LINKTABLE");
        table = ds1.link(url, true);
        assertNotNull(table);
        assertEquals("LINKTABLE", table.getName());
        ds2.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertNull(ds2.link(url, true));


        ds1.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertNotNull(ds1.link(url, "LINKTABLE"));
        ds2.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertNull(ds2.link(url, "LINKTABLE"));

        ds1.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertNotNull(ds1.link(url, "LINKTABLE", true));
        ds2.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertNull(ds2.link(url, "LINKTABLE", true));


        ds1.execute("DROP TABLE IF EXISTS LINKTABLE");
        table = ds1.link(uri);
        assertNotNull(table);
        assertEquals("LINKTABLE", table.getName());
        ds2.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertNull(ds2.link(uri));

        ds1.execute("DROP TABLE IF EXISTS LINKTABLE");
        table = ds1.link(uri, true);
        assertNotNull(table);
        assertEquals("LINKTABLE", table.getName());
        ds2.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertNull(ds2.link(uri, true));


        ds1.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertNotNull(ds1.link(uri, "LINKTABLE"));
        ds2.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertNull(ds2.link(uri, "LINKTABLE"));

        ds1.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertNotNull(ds1.link(uri, "LINKTABLE", true));
        ds2.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertNull(ds2.link(uri, "LINKTABLE", true));


        ds1.execute("DROP TABLE IF EXISTS LINKTABLE");
        table = ds1.link(file);
        assertNotNull(table);
        assertEquals("LINKTABLE", table.getName());
        ds2.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertNull(ds2.link(file));

        ds1.execute("DROP TABLE IF EXISTS LINKTABLE");
        table = ds1.link(file, true);
        assertNotNull(table);
        assertEquals("LINKTABLE", table.getName());
        ds2.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertNull(ds2.link(file, true));


        ds1.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertNotNull(ds1.link(file, "LINKTABLE"));
        ds2.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertNull(ds2.link(file, "LINKTABLE"));

        ds1.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertNotNull(ds1.link(file, "LINKTABLE", true));
        ds2.execute("DROP TABLE IF EXISTS LINKTABLE");
        assertNull(ds2.link(file, "LINKTABLE", true));
    }

    /**
     * Test the load from an existing database methods.
     */
    @Test
    void loadFromDB() throws SQLException {
        Map<String, String> map = new HashMap<>();
        map.put("url", "jdbc:h2:" + DB_LINK_NAME);
        String tableName = "LINKEDTABLE";
        String tableName2 = "LINKEDTABLE2";

        ds1.execute("DROP TABLE IF EXISTS " + tableName);
        ITable table = ds1.load(map, tableName);
        assertNotNull(table);
        assertEquals(tableName, table.getName());
        ds2.execute("DROP TABLE IF EXISTS " + tableName);
        assertNull(ds2.load(map, tableName));

        ds1.execute("DROP TABLE IF EXISTS " + tableName);
        table = ds1.load(map, tableName, true);
        assertNotNull(table);
        assertEquals(tableName, table.getName());
        ds2.execute("DROP TABLE IF EXISTS " + tableName);
        assertNull(ds2.load(map, tableName, true));

        ds1.execute("DROP TABLE IF EXISTS " + tableName2);
        table = ds1.load(map, tableName, tableName2);
        assertNotNull(table);
        assertEquals(tableName2, table.getName());
        ds2.execute("DROP TABLE IF EXISTS " + tableName2);
        assertNull(ds2.load(map, tableName, tableName2));

        ds1.execute("DROP TABLE IF EXISTS " + tableName2);
        table = ds1.load(map, tableName, tableName2, true);
        assertNotNull(table);
        assertEquals(tableName2, table.getName());
        ds2.execute("DROP TABLE IF EXISTS " + tableName2);
        assertNull(ds2.load(map, tableName, tableName2, true));
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
        ds1.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        ITable table = ds1.load(path);
        assertNotNull(table);
        assertEquals("LOADTABLE", table.getName());
        ds1.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = ds1.load(path, name);
        assertNotNull(table);
        assertEquals(name, table.getName());
        ds1.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = ds1.load(path, name, true);
        assertNotNull(table);
        assertEquals(name, table.getName());
        ds1.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = ds1.load(path, name, "UTF8", true);
        assertNotNull(table);
        assertEquals(name, table.getName());

        ds2.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = ds2.load(path);
        assertNotNull(table);
        assertEquals("LOADTABLE", table.getName());
        ds2.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = ds2.load(path, name);
        assertNotNull(table);
        assertEquals(name, table.getName());
        ds2.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = ds2.load(path, name, true);
        assertNotNull(table);
        assertEquals(name, table.getName());
        ds2.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = ds2.load(path, name, "UTF8", true);
        assertNotNull(table);
        assertEquals(name, table.getName());

        //Test URL
        ds1.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = ds1.load(url);
        assertNotNull(table);
        assertEquals("LOADTABLE", table.getName());
        ds1.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = ds1.load(url, name);
        assertNotNull(table);
        assertEquals(name, table.getName());
        ds1.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = ds1.load(url, name, true);
        assertNotNull(table);
        assertEquals(name, table.getName());
        ds1.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = ds1.load(url, true);
        assertNotNull(table);
        assertEquals("LOADTABLE", table.getName());
        ds1.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = ds1.load(url, name, "UTF8", true);
        assertNotNull(table);
        assertEquals(name, table.getName());

        ds2.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = ds2.load(url);
        assertNotNull(table);
        assertEquals("LOADTABLE", table.getName());
        ds2.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = ds2.load(url, name);
        assertNotNull(table);
        assertEquals(name, table.getName());
        ds2.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = ds2.load(url, true);
        assertNotNull(table);
        assertEquals("LOADTABLE", table.getName());
        ds2.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = ds2.load(url, name, true);
        assertNotNull(table);
        assertEquals(name, table.getName());
        ds2.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = ds2.load(url, name, "UTF8", true);
        assertNotNull(table);
        assertEquals(name, table.getName());

        //Test URI
        ds1.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = ds1.load(uri);
        assertNotNull(table);
        assertEquals("LOADTABLE", table.getName());
        ds1.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = ds1.load(uri, name);
        assertNotNull(table);
        assertEquals(name, table.getName());
        ds1.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = ds1.load(uri, name, true);
        assertNotNull(table);
        assertEquals(name, table.getName());
        ds1.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = ds1.load(uri, name, "UTF8", true);
        assertNotNull(table);
        assertEquals(name, table.getName());

        ds2.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = ds2.load(uri);
        assertNotNull(table);
        assertEquals("LOADTABLE", table.getName());
        ds2.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = ds2.load(uri, name);
        assertNotNull(table);
        assertEquals(name, table.getName());
        ds2.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = ds2.load(uri, name, true);
        assertNotNull(table);
        assertEquals(name, table.getName());
        ds2.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = ds2.load(uri, name, "UTF8", true);
        assertNotNull(table);
        assertEquals(name, table.getName());

        //Test File
        ds1.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = ds1.load(file);
        assertNotNull(table);
        assertEquals("LOADTABLE", table.getName());
        ds1.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = ds1.load(file, name);
        assertNotNull(table);
        assertEquals(name, table.getName());
        ds1.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = ds1.load(file, name, true);
        assertNotNull(table);
        assertEquals(name, table.getName());
        ds1.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = ds1.load(file, name, "UTF8", true);
        assertNotNull(table);
        assertEquals(name, table.getName());

        ds2.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = ds2.load(file);
        assertNotNull(table);
        assertEquals("LOADTABLE", table.getName());
        ds2.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = ds2.load(file, name);
        assertNotNull(table);
        assertEquals(name, table.getName());
        ds2.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = ds2.load(file, name, true);
        assertNotNull(table);
        assertEquals(name, table.getName());
        ds2.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
        table = ds2.load(file, name, "UTF8", true);
        assertNotNull(table);
        assertEquals(name, table.getName());

        //Test bad name
        ds1.load("4file.dbf");

        ds1.execute("DROP TABLE IF EXISTS LOADTABLE, NAME");
    }

    /**
     * Test the {@link JdbcDataSource#getLocation()} method.
     */
    @Test
    void testGetLocation() {
        assertNotNull(ds1.getLocation());
        assertEquals(new File(DB_NAME).getAbsolutePath(), ds1.getLocation().asType(String.class));
        assertNotNull(ds2.getLocation());
        assertEquals(new File(DB_NAME).getAbsolutePath(), ds2.getLocation().asType(String.class));
    }

    /**
     * Test the {@link JdbcDataSource#getTableNames()} method.
     */
    @Test
    void testGetTableNames() {
        Collection<String> names = ds1.getTableNames();
        assertNotNull(names);
        assertEquals(38, names.size());
        assertTrue(names.contains("JDBCDATASOURCETEST.PUBLIC.GEOMETRY_COLUMNS"));
        assertTrue(names.contains("JDBCDATASOURCETEST.PUBLIC.TEST"));
        assertTrue(names.contains("JDBCDATASOURCETEST.PUBLIC.SPATIAL_REF_SYS"));

        names = ds2.getTableNames();
        assertNotNull(names);
        assertEquals(38, names.size());
        assertTrue(names.contains("JDBCDATASOURCETEST.PUBLIC.GEOMETRY_COLUMNS"));
        assertTrue(names.contains("JDBCDATASOURCETEST.PUBLIC.TEST"));
        assertTrue(names.contains("JDBCDATASOURCETEST.PUBLIC.SPATIAL_REF_SYS"));
    }

    /**
     * Test the {@link JdbcDataSource#getColumnNames(String)} method.
     */
    @Test
    void testGetColumnNames() {
        Collection<String> names = ds1.getColumnNames("JDBCDATASOURCETEST.PUBLIC.TEST");
        assertNotNull(names);
        assertEquals(3, names.size());
        assertTrue(names.contains("ID"));
        assertTrue(names.contains("THE_GEOM"));
        assertTrue(names.contains("TEXT"));
        names = ds1.getColumnNames("PUBLIC.TEST");
        assertNotNull(names);
        assertEquals(3, names.size());
        assertTrue(names.contains("ID"));
        assertTrue(names.contains("THE_GEOM"));
        assertTrue(names.contains("TEXT"));
        names = ds1.getColumnNames("TEST");
        assertNotNull(names);
        assertEquals(3, names.size());
        assertTrue(names.contains("ID"));
        assertTrue(names.contains("THE_GEOM"));
        assertTrue(names.contains("TEXT"));

        names = ds2.getColumnNames("JDBCDATASOURCETEST.PUBLIC.TEST");
        assertNull(names);
        names = ds2.getColumnNames("PUBLIC.TEST");
        assertNull(names);
        names = ds2.getColumnNames("TEST");
        assertNull(names);
    }

    /**
     * Test the {@link JdbcDataSource#hasTable(String)} method.
     */
    @Test
    void testHasTable() {
        Collection<String> names = ds1.getTableNames();
        assertNotNull(names);
        assertEquals(38, names.size());
        assertTrue(ds1.hasTable("GEOMETRY_COLUMNS"));
        assertTrue(ds1.hasTable("TEST"));
        assertTrue(ds1.hasTable("JDBCDATASOURCETEST.PUBLIC.SPATIAL_REF_SYS"));
    }


    /**
     * Test the {@link JdbcDataSource#getDataSet(String)} method.
     */
    @Test
    void testGetDataSet() {
        Object dataset = ds1.getDataSet("TEST");
        assertTrue(dataset instanceof ISpatialTable);
        dataset = ds1.getDataSet("GEOMETRY_COLUMNS");
        assertTrue(dataset instanceof ITable);

        dataset = ds2.getDataSet("TEST");
        assertTrue(dataset instanceof ISpatialTable);
        dataset = ds2.getDataSet("GEOMETRY_COLUMNS");
        assertTrue(dataset instanceof ITable);
    }

    /**
     * Simple extension of {@link JdbcDataSource} for test purpose.
     */
    private static class DummyJdbcDataSource extends JdbcDataSource {

        DummyJdbcDataSource(Sql parent, DataBaseType databaseType) {
            super(parent, databaseType);
        }

        DummyJdbcDataSource(DataSource dataSource, DataBaseType databaseType) {
            super(dataSource, databaseType);
        }

        DummyJdbcDataSource(Connection connection, DataBaseType databaseType) {
            super(connection, databaseType);
        }

        @Override
        public IJdbcTable getTable(@NotNull String s) {
            ConnectionWrapper connectionWrapper = new ConnectionWrapper(this.getConnection());
            try {
                if (!JDBCUtilities.tableExists(connectionWrapper, org.h2gis.utilities.TableLocation.parse(s, true))) {
                    return null;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
            try {
                return new H2gisTable(
                        new TableLocation(null, s),
                        "SELECT * FROM " + s,
                        new StatementWrapper(this.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE), connectionWrapper),
                        this);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public IJdbcTable getTable(String tableName, IJdbcTable.RSType resultSetType, IJdbcTable.RSConcurrency resultSetConcurrency) {
            return null;
        }

        @Override
        public IJdbcTable getTable(String tableName, IJdbcTable.RSType resultSetType, IJdbcTable.RSConcurrency resultSetConcurrency, IJdbcTable.RSHoldability resultSetHoldability) {
            return null;
        }

        @Override
        public IJdbcSpatialTable getSpatialTable(@NotNull String s) {
            ConnectionWrapper connectionWrapper = new ConnectionWrapper(this.getConnection());
            try {
                if (!JDBCUtilities.tableExists(connectionWrapper, org.h2gis.utilities.TableLocation.parse(s, true))) {
                    return null;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
            try {
                return new H2gisSpatialTable(
                        new TableLocation(null, s),
                        "SELECT * FROM " + s,
                        new StatementWrapper(this.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE), connectionWrapper),
                        this);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public IJdbcSpatialTable getSpatialTable(String tableName, IJdbcTable.RSType resultSetType, IJdbcTable.RSConcurrency resultSetConcurrency) {
            return null;
        }

        @Override
        public IJdbcSpatialTable getSpatialTable(String tableName, IJdbcTable.RSType resultSetType, IJdbcTable.RSConcurrency resultSetConcurrency, IJdbcTable.RSHoldability resultSetHoldability) {
            return null;
        }

        @Override
        public boolean hasTable(@NotNull String tableName) {
            try {
                ConnectionWrapper connectionWrapper = new ConnectionWrapper(this.getConnection());
                return JDBCUtilities.tableExists(connectionWrapper, org.h2gis.utilities.TableLocation.parse(tableName, true));
            } catch (SQLException ex) {
                return false;
            }
        }

        @Nullable
        @Override
        public Object asType(@NotNull Class<?> clazz) {
            return null;
        }
    }
}
