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

import groovy.lang.MetaClass;
import groovy.lang.MissingMethodException;
import groovy.lang.MissingPropertyException;
import groovy.sql.Sql;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.InvokerInvocationException;
import org.h2.jdbc.JdbcResultSetMetaData;
import org.h2gis.functions.factory.H2GISDBFactory;
import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.wrapper.ConnectionWrapper;
import org.h2gis.utilities.wrapper.StatementWrapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.orbisgis.commons.printer.Ascii;
import org.orbisgis.commons.printer.Html;
import org.orbisgis.orbisdata.datamanager.api.dataset.*;
import org.orbisgis.orbisdata.datamanager.api.dsl.IOptionBuilder;
import org.orbisgis.orbisdata.datamanager.jdbc.h2gis.H2gisSpatialTable;
import org.orbisgis.orbisdata.datamanager.jdbc.h2gis.H2gisTable;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class dedicated to {@link JdbcTable} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
//TODO test with a postgis database
class JdbcTableTest {

    /**
     * Database connection
     */
    private static Connection connection;
    /**
     * Linked database connection
     */
    private static Connection connectionLinked;
    /**
     * Connection statement
     */
    private static Statement statement;
    /**
     * Dummy data source
     */
    private static DummyJdbcDataSource dataSource;
    /**
     * Table location
     */
    private static TableLocation tableLocation;
    /**
     * Linked table location
     */
    private static TableLocation linkedLocation;
    /**
     * Temporary table location
     */
    private static TableLocation tempLocation;
    /**
     * Empty table location
     */
    private static TableLocation emptyLocation;

    private static final String BASE_DATABASE = JdbcTableTest.class.getSimpleName();
    private static final String TABLE_NAME = "ORBISGIS";
    private static final String BASE_QUERY = "SELECT * FROM " + TABLE_NAME;
    private static final String TEMP_NAME = "TEMPTABLE";
    private static final String TEMP_QUERY = "SELECT * FROM " + TEMP_NAME;
    private static final String EMPTY_NAME = "ORBISGIS_EMPTY";
    private static final String EMPTY_QUERY = "SELECT * FROM " + EMPTY_NAME;
    private static final String LINKED_DATABASE = JdbcTableTest.class.getSimpleName() + "linked";
    private static final String LINKED_NAME = "LINKEDTABLE";
    private static final String LINKED_QUERY = "SELECT * FROM " + LINKED_NAME;

    private static final String COL_THE_GEOM = "THE_GEOM";
    private static final String COL_THE_GEOM2 = "the_geom2";
    private static final String COL_ID = "ID";
    private static final String COL_VALUE = "VALUE";
    private static final String COL_MEANING = "MEANING";

    /**
     * Initialization of the database.
     */
    @BeforeAll
    static void init() {
        try {
            connection = H2GISDBFactory.createSpatialDataBase(BASE_DATABASE);
            connectionLinked = H2GISDBFactory.createSpatialDataBase(LINKED_DATABASE);
        } catch (SQLException | ClassNotFoundException e) {
            fail(e);
        }
        Sql sql = new Sql(connection);
        dataSource = new DummyJdbcDataSource(sql, DataBaseType.H2GIS);
    }

    /**
     * Set the database with some data.
     */
    @BeforeEach
    void prepareDB() {
        try {
            Statement statementLinked = connectionLinked.createStatement();
            statementLinked.execute("DROP TABLE IF EXISTS " + TABLE_NAME + "," + TEMP_NAME);
            statementLinked.execute("CREATE TABLE " + TABLE_NAME + " (" + COL_THE_GEOM + " GEOMETRY, " + COL_THE_GEOM2 + " GEOMETRY(POINT Z)," +
                    COL_ID + " INTEGER, " + COL_VALUE + " FLOAT, " + COL_MEANING + " VARCHAR)");
            statementLinked.execute("INSERT INTO " + TABLE_NAME + " VALUES ('POINT(0 0)', 'POINT(1 1 0)', 1, 2.3, 'Simple points')");
            statementLinked.execute("INSERT INTO " + TABLE_NAME + " VALUES ('POINT(0 1 2)', 'POINT(10 11 12)', 2, 0.568, '3D point')");
            statementLinked.execute("CREATE TEMPORARY TABLE " + TEMP_NAME + " (" + COL_THE_GEOM + " GEOMETRY, " + COL_THE_GEOM2 + " GEOMETRY(POINT Z)," +
                    COL_ID + " INTEGER, " + COL_VALUE + " FLOAT, " + COL_MEANING + " VARCHAR)");

            statement = connection.createStatement();
            statement.execute("DROP TABLE IF EXISTS " + TABLE_NAME + "," + LINKED_NAME + "," + TEMP_NAME + "," + EMPTY_NAME);
            statement.execute("CREATE TABLE " + TABLE_NAME + " (" + COL_THE_GEOM + " GEOMETRY, " + COL_THE_GEOM2 + " GEOMETRY(POINT Z)," +
                    COL_ID + " INTEGER, " + COL_VALUE + " FLOAT, " + COL_MEANING + " VARCHAR)");
            statement.execute("INSERT INTO " + TABLE_NAME + " VALUES ('POINT(0 0)', 'POINT(1 1 0)', 1, 2.3, 'Simple points')");
            statement.execute("INSERT INTO " + TABLE_NAME + " VALUES ('POINT(0 1 2)', 'POINT(10 11 12)', 2, 0.568, '3D point')");
            statement.execute("CREATE LINKED TABLE " + LINKED_NAME + "('org.h2.Driver','jdbc:h2:./target/test-resources/dbH2" + LINKED_DATABASE +
                    "','sa','sa','" + TABLE_NAME + "')");
            statement.execute("CREATE TEMPORARY TABLE " + TEMP_NAME + " (" + COL_THE_GEOM + " GEOMETRY, " + COL_THE_GEOM2 + " GEOMETRY(POINT Z)," +
                    COL_ID + " INTEGER, " + COL_VALUE + " FLOAT, " + COL_MEANING + " VARCHAR)");
            statement.execute("CREATE TABLE " + EMPTY_NAME + " (" + COL_THE_GEOM + " GEOMETRY, " + COL_THE_GEOM2 + " GEOMETRY(POINT Z)," +
                    COL_ID + " INTEGER, " + COL_VALUE + " FLOAT, " + COL_MEANING + " VARCHAR)");

            tableLocation = new TableLocation(BASE_DATABASE, TABLE_NAME);
            linkedLocation = new TableLocation(BASE_DATABASE, LINKED_NAME);
            tempLocation = new TableLocation(BASE_DATABASE, TEMP_NAME);
            emptyLocation = new TableLocation(BASE_DATABASE, EMPTY_NAME);
        } catch (Exception e) {
            fail(e);
        }
    }

    /**
     * Returns a {@link DummyJdbcTable} for test purpose.
     *
     * @return A {@link DummyJdbcTable} for test purpose.
     */
    private DummyJdbcTable getTable() {
        return new DummyJdbcTable(DataBaseType.H2GIS, dataSource, tableLocation, statement, BASE_QUERY);
    }

    /**
     * Returns a linked {@link DummyJdbcTable} for test purpose.
     *
     * @return A linked {@link DummyJdbcTable} for test purpose.
     */
    private DummyJdbcTable getLinkedTable() {
        return new DummyJdbcTable(DataBaseType.H2GIS, dataSource, linkedLocation, statement, LINKED_QUERY);
    }

    /**
     * Returns a temporary {@link DummyJdbcTable} for test purpose.
     *
     * @return A temporary {@link DummyJdbcTable} for test purpose.
     */
    private DummyJdbcTable getTempTable() {
        return new DummyJdbcTable(DataBaseType.H2GIS, dataSource, tempLocation, statement, TEMP_QUERY);
    }

    /**
     * Returns an empty {@link DummyJdbcTable} for test purpose.
     *
     * @return An empty {@link DummyJdbcTable} for test purpose.
     */
    private DummyJdbcTable getEmptyTable() {
        return new DummyJdbcTable(DataBaseType.H2GIS, dataSource, emptyLocation, statement, EMPTY_QUERY);
    }

    /**
     * Returns a {@link JdbcTable} for test purpose.
     *
     * @return A {@link JdbcTable} for test purpose.
     */
    private JdbcTable getBuiltTable() {
        return getTable().columns(COL_THE_GEOM, COL_THE_GEOM2, COL_ID, COL_VALUE, COL_MEANING);
    }

    /**
     * Test the {@link JdbcTable#JdbcTable(DataBaseType, JdbcDataSource, TableLocation, Statement, String)} constructor.
     */
    @Test
    void testConstructor() {
        assertNotNull(getTable());
        assertNotNull(getLinkedTable());
        assertNotNull(getTempTable());
        assertNotNull(getEmptyTable());
        assertNotNull(getBuiltTable());
    }


    /**
     * Test the {@link IJdbcTable#getLocation()} method.
     */
    @Test
    public void testGetLocation() throws SQLException {
        assertEquals("\"catalog\".\"schema\".\"table\"", new DummyJdbcTable(DataBaseType.POSTGIS, dataSource,
                new TableLocation(BASE_DATABASE, "catalog", "schema", "table"),
                dataSource.getConnection().createStatement(), "not a request").getLocation());
        assertEquals("\"catalog\".\"schema\".\"table\"", new DummyJdbcTable(DataBaseType.H2GIS, dataSource,
                new TableLocation(BASE_DATABASE, "catalog", "schema", "table"),
                dataSource.getConnection().createStatement(), "not a request").getLocation());
        assertEquals(BASE_DATABASE, new TableLocation(BASE_DATABASE, "catalog", "schema", "table").getDataSource());

        assertEquals("\"ORBISGIS\"", getTable().getLocation());
        assertEquals("\"LINKEDTABLE\"", getLinkedTable().getLocation());
        assertEquals("\"TEMPTABLE\"", getTempTable().getLocation());
        assertEquals("\"ORBISGIS_EMPTY\"", getEmptyTable().getLocation());
        assertEquals(IJdbcTable.QUERY_LOCATION, getBuiltTable().getLocation());
    }

    /**
     * Test the {@link JdbcTable#getResultSet()} constructor.
     */
    @Test
    void testGetResultSet() throws SQLException {
        assertNotNull(getTable().getResultSet());
        assertNotNull(getLinkedTable().getResultSet());
        assertNotNull(getTempTable().getResultSet());
        assertNotNull(getEmptyTable().getResultSet());
        assertNotNull(getBuiltTable().getResultSet());
        JdbcTable table = new DummyJdbcTable(null, dataSource, new TableLocation(BASE_DATABASE, "tab"),
                dataSource.getConnection().createStatement(), "not a request");
        assertNull(table.getResultSet());
    }

    /**
     * Test the {@link JdbcTable#getMetaData()} constructor.
     */
    @Test
    void testGetMetadata() throws SQLException {
        assertNotNull(getTable().getMetaData());
        assertNotNull(getLinkedTable().getMetaData());
        assertNotNull(getTempTable().getMetaData());
        assertNotNull(getEmptyTable().getMetaData());
        assertNotNull(getBuiltTable().getMetaData());
        JdbcTable table = new DummyJdbcTable(null, dataSource, new TableLocation(BASE_DATABASE, "tab"),
                dataSource.getConnection().createStatement(), "not a request");
        assertNull(table.getMetaData());
    }

    /**
     * Test the {@link JdbcTable#getBaseQuery()} method.
     */
    @Test
    void testGetBaseQuery() {
        assertEquals(BASE_QUERY, getTable().getBaseQuery());
        assertEquals(LINKED_QUERY, getLinkedTable().getBaseQuery());
        assertEquals(TEMP_QUERY, getTempTable().getBaseQuery());
        assertEquals(EMPTY_QUERY, getEmptyTable().getBaseQuery());
        assertEquals("SELECT THE_GEOM, the_geom2, ID, VALUE, MEANING FROM ORBISGIS", getBuiltTable().getBaseQuery().trim());
    }

    /**
     * Test the {@link JdbcTable#getJdbcDataSource()} method.
     */
    @Test
    void testGetJdbcDataSource() {
        assertEquals(dataSource, getTable().getJdbcDataSource());
        assertEquals(dataSource, getLinkedTable().getJdbcDataSource());
        assertEquals(dataSource, getTempTable().getJdbcDataSource());
        assertEquals(dataSource, getEmptyTable().getJdbcDataSource());
        assertEquals(dataSource, getBuiltTable().getJdbcDataSource());
    }

    /**
     * Test the {@link JdbcTable#getTableLocation()} method.
     */
    @Test
    void testGetTableLocation() {
        assertEquals(tableLocation, getTable().getTableLocation());
        assertEquals(linkedLocation, getLinkedTable().getTableLocation());
        assertEquals(tempLocation, getTempTable().getTableLocation());
        assertEquals(emptyLocation, getEmptyTable().getTableLocation());
        assertNull(getBuiltTable().getTableLocation());
    }

    /**
     * Test the {@link JdbcTable#getDbType()} method.
     */
    @Test
    void testGetDbType() {
        assertEquals(DataBaseType.H2GIS, getTable().getDbType());
        assertEquals(DataBaseType.H2GIS, getLinkedTable().getDbType());
        assertEquals(DataBaseType.H2GIS, getTempTable().getDbType());
        assertEquals(DataBaseType.H2GIS, getEmptyTable().getDbType());
        assertEquals(DataBaseType.H2GIS, getBuiltTable().getDbType());
    }

    /**
     * Test the {@link JdbcTable#getMetaClass()} method.
     */
    @Test
    void testGetMetaClass() {
        assertEquals(InvokerHelper.getMetaClass(DummyJdbcTable.class), getTable().getMetaClass());
        assertEquals(InvokerHelper.getMetaClass(DummyJdbcTable.class), getLinkedTable().getMetaClass());
        assertEquals(InvokerHelper.getMetaClass(DummyJdbcTable.class), getTempTable().getMetaClass());
        assertEquals(InvokerHelper.getMetaClass(DummyJdbcTable.class), getEmptyTable().getMetaClass());
        assertEquals(InvokerHelper.getMetaClass(H2gisTable.class), getBuiltTable().getMetaClass());
    }

    /**
     * Test the {@link JdbcTable#setMetaClass(MetaClass)} method.
     */
    @Test
    void testSetMetaClass() {
        List<JdbcTable> tables = Arrays.asList(getTable(), getLinkedTable(), getTempTable(), getEmptyTable(), getBuiltTable());
        tables.forEach(table -> {
            MetaClass metaClass = InvokerHelper.getMetaClass(JdbcTableTest.class);
            table.setMetaClass(metaClass);
            assertEquals(metaClass, table.getMetaClass());
        });
    }

    /**
     * Test the {@link JdbcTable#isSpatial()} method.
     */
    @Test
    void testIsSpatial() {
        assertFalse(getTable().isSpatial());
        assertFalse(getLinkedTable().isSpatial());
        assertFalse(getTempTable().isSpatial());
        assertFalse(getEmptyTable().isSpatial());
        assertFalse(getBuiltTable().isSpatial());
    }

    /**
     * Test the {@link JdbcTable#isLinked()} method.
     */
    @Test
    void testIsLinked() {
        assertFalse(getTable().isLinked());
        assertTrue(getLinkedTable().isLinked());
        assertFalse(getTempTable().isLinked());
        assertFalse(getEmptyTable().isLinked());
        assertFalse(getBuiltTable().isLinked());
    }

    /**
     * Test the {@link JdbcTable#isTemporary()} ()} method.
     */
    @Test
    void testIsTemporary() {
        assertFalse(getTable().isTemporary());
        assertFalse(getLinkedTable().isTemporary());
        assertTrue(getTempTable().isTemporary());
        assertFalse(getEmptyTable().isTemporary());
        assertFalse(getBuiltTable().isTemporary());
    }

    /**
     * Test the {@link JdbcTable#getColumns()} method.
     */
    @Test
    void testGetColumnNames() {
        List<String> colList = new ArrayList<>();
        colList.add(TableLocation.capsIdentifier(COL_THE_GEOM, true));
        colList.add(TableLocation.capsIdentifier(COL_THE_GEOM2, true));
        colList.add(TableLocation.capsIdentifier(COL_ID, true));
        colList.add(TableLocation.capsIdentifier(COL_VALUE, true));
        colList.add(TableLocation.capsIdentifier(COL_MEANING, true));
        assertEquals(colList, getTable().getColumns());
        assertEquals(colList, getLinkedTable().getColumns());
        assertEquals(colList, getTempTable().getColumns());
        assertEquals(colList, getEmptyTable().getColumns());
        assertEquals(colList, getBuiltTable().getColumns());
    }

    /**
     * Test the {@link JdbcTable#hasColumn(String)} method.
     */
    @Test
    void testHasColumn() {
        List<JdbcTable> tables = Arrays.asList(getTable(), getLinkedTable(), getTempTable(), getEmptyTable(), getBuiltTable());
        tables.forEach(t -> {
            assertTrue(t.hasColumn(COL_THE_GEOM.toUpperCase()));
            assertTrue(t.hasColumn(COL_THE_GEOM.toLowerCase()));
            assertTrue(t.hasColumn(COL_THE_GEOM2));
            assertFalse(t.hasColumn("the_geom3"));
            assertTrue(t.hasColumn(COL_ID));
            assertTrue(t.hasColumn(COL_VALUE));
            assertTrue(t.hasColumn(COL_MEANING));
        });
    }

    /**
     * Test the {@link JdbcTable#hasColumn(String, Class)} method.
     */
    @Test
    void testHasColumnWithClass() {
        List<JdbcTable> tables = Arrays.asList(getTable(), getTempTable(), getEmptyTable());
        tables.forEach(t -> {
            assertTrue(t.hasColumn(COL_THE_GEOM.toUpperCase(), Geometry.class));
            assertTrue(t.hasColumn(COL_THE_GEOM.toLowerCase(), Geometry.class));
            assertFalse(t.hasColumn(COL_THE_GEOM2, Geometry.class));
            assertTrue(t.hasColumn(COL_THE_GEOM2, Point.class));
            assertTrue(t.hasColumn(COL_ID, Integer.class));
            assertFalse(t.hasColumn(COL_ID, Long.class));
            assertTrue(t.hasColumn(COL_VALUE, Float.class));
            assertTrue(t.hasColumn(COL_VALUE, Double.class));
            assertFalse(t.hasColumn(COL_VALUE, Integer.class));
            assertTrue(t.hasColumn(COL_MEANING, String.class));
            assertFalse(t.hasColumn("not_a_col", String.class));
        });

        JdbcTable t = getBuiltTable();
        assertTrue(t.hasColumn(COL_THE_GEOM.toUpperCase(), Point.class));
        assertTrue(t.hasColumn(COL_THE_GEOM.toLowerCase(), Point.class));
        assertFalse(t.hasColumn(COL_THE_GEOM2, Geometry.class));
        assertTrue(t.hasColumn(COL_THE_GEOM2, Point.class));
        assertTrue(t.hasColumn(COL_ID, Integer.class));
        assertFalse(t.hasColumn(COL_ID, Long.class));
        assertTrue(t.hasColumn(COL_VALUE, Float.class));
        assertTrue(t.hasColumn(COL_VALUE, Double.class));
        assertFalse(t.hasColumn(COL_VALUE, Integer.class));
        assertTrue(t.hasColumn(COL_MEANING, String.class));
        assertFalse(t.hasColumn("not_a_col", String.class));

        t = getLinkedTable();
        assertTrue(t.hasColumn(COL_THE_GEOM.toUpperCase(), Geometry.class));
        assertTrue(t.hasColumn(COL_THE_GEOM.toLowerCase(), Geometry.class));
        assertTrue(t.hasColumn(COL_THE_GEOM2, Geometry.class));
        assertFalse(t.hasColumn(COL_THE_GEOM2, Point.class));
        assertTrue(t.hasColumn(COL_ID, Integer.class));
        assertFalse(t.hasColumn(COL_ID, Long.class));
        assertTrue(t.hasColumn(COL_VALUE, Float.class));
        assertTrue(t.hasColumn(COL_VALUE, Double.class));
        assertFalse(t.hasColumn(COL_VALUE, Integer.class));
        assertTrue(t.hasColumn(COL_MEANING, String.class));
        assertFalse(t.hasColumn("not_a_col", String.class));
    }

    /**
     * Test the {@link JdbcTable#getRowCount()} method.
     */
    @Test
    void testGetRowCount() {
        assertEquals(2, getTable().getRowCount());
        assertEquals(2, getLinkedTable().getRowCount());
        assertEquals(0, getTempTable().getRowCount());
        assertEquals(0, getEmptyTable().getRowCount());
        assertEquals(2, getBuiltTable().getRowCount());
    }

    /**
     * Test the {@link JdbcTable#getUniqueValues(String)} method.
     */
    @Test
    void testGetUniqueValues() {
        assertEquals(2, getTable().getUniqueValues(COL_MEANING).size());
        assertTrue(getTable().getUniqueValues(COL_MEANING).contains("Simple points"));
        assertTrue(getTable().getUniqueValues(COL_MEANING).contains("3D point"));
        assertEquals(2, getTable().getUniqueValues(COL_THE_GEOM).size());

        assertEquals(2, getLinkedTable().getUniqueValues(COL_MEANING).size());
        assertTrue(getLinkedTable().getUniqueValues(COL_MEANING).contains("Simple points"));
        assertTrue(getLinkedTable().getUniqueValues(COL_MEANING).contains("3D point"));
        assertEquals(2, getLinkedTable().getUniqueValues(COL_THE_GEOM).size());

        /*assertEquals(2, getBuiltTable().getUniqueValues(COL_MEANING).size());
        assertTrue(getBuiltTable().getUniqueValues(COL_MEANING).contains("Simple points"));
        assertTrue(getBuiltTable().getUniqueValues(COL_MEANING).contains("3D point"));
        assertEquals(2, getBuiltTable().getUniqueValues(COL_THE_GEOM).size());*/
    }

    /**
     * Test the {@link JdbcTable#save(String, String)} and {@link JdbcTable#save(String)} methods.
     */
    @Test
    void testSave() {
        new File("./target/save1.json").delete();
        assertFalse(new File("./target/save1.json").exists());
        assertTrue(getTable().save("./target/save1.json"));
        assertTrue(new File("./target/save1.json").exists());

        new File("./target/save2.json").delete();
        assertFalse(new File("./target/save2.json").exists());
        assertTrue(getTable().save("./target/save2.json"), "UTF8");
        assertTrue(new File("./target/save2.json").exists());

        new File("./target/save3.json").delete();
        assertFalse(new File("./target/save3.json").exists());
        assertTrue(getTempTable().save("./target/save3.json"));
        assertTrue(new File("./target/save3.json").exists());

        new File("./target/save4.json").delete();
        assertFalse(new File("./target/save4.json").exists());
        assertTrue(getTempTable().save("./target/save4.json"), "UTF8");
        assertTrue(new File("./target/save4.json").exists());
    }

    /**
     * Test the {@link JdbcTable#getFirstRow()} method.
     */
    @Test
    void testGetFirstRow() {
        List<JdbcTable> tables = Arrays.asList(getTable(), getLinkedTable(), getBuiltTable());
        tables.forEach(table -> {
            assertEquals(5, table.getFirstRow().size());
            assertEquals("POINT (0 0)", table.getFirstRow().get(0).toString());
            assertEquals("POINT (1 1)", table.getFirstRow().get(1).toString());
            assertEquals(1, table.getFirstRow().get(2));
            assertEquals(2.3, table.getFirstRow().get(3));
            assertEquals("Simple points", table.getFirstRow().get(4));
        });
    }

    /**
     * Test the {@link JdbcTable#invokeMethod(String, Object)} method.
     */
    @Test
    void testInvokeMethod() {
        List<JdbcTable> tables = Arrays.asList(getTable(), getLinkedTable(), getEmptyTable(), getTempTable());
        tables.forEach(table -> {
            assertEquals(table.getLocation(), table.invokeMethod("getLocation", null));
            assertEquals(table.getLocation(), table.invokeMethod("location", null));
            assertArrayEquals(new Object[]{"string", 0.2}, (Object[]) table.invokeMethod("getArrayMethod", new Object[]{"string", 0.2}));
            assertArrayEquals(new Object[]{"string", 0.2}, (Object[]) table.invokeMethod("arrayMethod", new Object[]{"string", 0.2}));
            assertArrayEquals(new Object[]{"string", 0.2}, (Object[]) table.invokeMethod("getParametersMethod", new Object[]{"string", 0.2}));
            assertArrayEquals(new Object[]{"string", 0.2}, (Object[]) table.invokeMethod("parametersMethod", new Object[]{"string", 0.2}));
            assertArrayEquals(new Object[]{"string", "0.2"}, (Object[]) table.invokeMethod("getParametersMethod", new Object[]{"string", "0.2"}));
            assertArrayEquals(new Object[]{"string", "0.2"}, (Object[]) table.invokeMethod("parametersMethod", new Object[]{"string", "0.2"}));
            assertEquals("string", table.invokeMethod("getParameterMethod", new Object[]{"string"}));
            assertEquals("string", table.invokeMethod("getParameterMethod", "string"));
            assertEquals("string", table.invokeMethod("parameterMethod", new Object[]{"string"}));
            assertEquals("string", table.invokeMethod("parameterMethod", "string"));
            assertEquals(JdbcResultSetMetaData.class, table.invokeMethod("metaData", null).getClass());

            assertThrows(MissingMethodException.class, () -> table.invokeMethod("getLocation", new String[]{"tata", "toto"}));
            assertThrows(MissingMethodException.class, () -> table.invokeMethod("location", new String[]{"tata", "toto"}));
            assertNull(table.invokeMethod("getPrivateMethod", null));
            assertNull(table.invokeMethod("privateMethod", null));
        });
    }

    /**
     * Test the {@link JdbcTable#getProperty(String)} method.
     */
    @Test
    void testGetProperty() {
        List<JdbcTable> tables = Arrays.asList(getTable(), getEmptyTable(), getTempTable(), getLinkedTable());
        tables.forEach(table -> {
            assertThrows(MissingPropertyException.class, () -> table.getProperty("getLocation"));
            assertEquals(table.getLocation(), table.getProperty("location"));
            assertEquals(JdbcResultSetMetaData.class, table.getProperty("meta").getClass());
            assertArrayEquals(new Object[]{"string", 0.2}, (Object[]) table.getProperty("data"));
            assertEquals("tutu", table.getProperty("privateData"));
            assertNull(table.getProperty(null));
            assertTrue(table.getProperty("meaning") instanceof JdbcColumn);
            assertEquals("MEANING", ((JdbcColumn) table.getProperty("meaning")).getName());
            assertTrue(table.getProperty(COL_THE_GEOM.toUpperCase()) instanceof JdbcColumn);
            assertTrue(table.getProperty(COL_THE_GEOM.toLowerCase()) instanceof JdbcColumn);
        });
    }

    /**
     * Test the {@link JdbcTable#setProperty(String, Object)} method.
     */
    @Test
    void testSetProperty() {
        List<JdbcTable> tables = Arrays.asList(getTable(), getEmptyTable(), getTempTable(), getLinkedTable());
        tables.forEach(table -> {
            assertThrows(MissingPropertyException.class, () -> table.setProperty("getLocation", "tata"));
            table.setProperty("privateData", "toto");
            assertEquals("toto", table.getProperty("privateData"));
        });
    }

    /**
     * Test the {@link IJdbcTable} methods with {@link SQLException} thrown.
     */
    @Test
    void testSQLException() {
        List<JdbcTable> tables = Arrays.asList(getTable(), getEmptyTable(), getTempTable(), getLinkedTable());
        tables.forEach(table -> {
            assertNotNull(table.getProperty("data"));
            assertThrows(InvokerInvocationException.class, () -> table.invokeMethod("dupMethod", null));
        });
    }

    /**
     * Test the {@link JdbcTable#getColumnType(String)} method.
     */
    @Test
    void testGetColumnsType() {
        List<JdbcTable> tables = Arrays.asList(getTable(), getEmptyTable(), getTempTable(), getLinkedTable(), getBuiltTable());
        tables.forEach(table -> {
            assertEquals("GEOMETRY", getTable().getColumnType(COL_THE_GEOM));
            assertEquals("INTEGER", getTable().getColumnType(COL_ID));
            assertEquals("VARCHAR", getTable().getColumnType(COL_MEANING));
            assertNull(getTable().getColumnType("NOT_A_COLUMN"));
        });
    }

    /**
     * Test the {@link JdbcTable#getColumnsTypes()} method.
     */
    @Test
    void testGetColumns() {
        List<JdbcTable> tables = Arrays.asList(getTable(), getEmptyTable(), getTempTable());
        tables.forEach(table -> {
            Map<String, String> map = table.getColumnsTypes();
            String[] keys = {COL_THE_GEOM, COL_THE_GEOM2.toUpperCase(), COL_ID, COL_VALUE, COL_MEANING};
            String[] values = {"GEOMETRY", "POINTZ", "INTEGER", "DOUBLE", "VARCHAR"};
            assertArrayEquals(keys, map.keySet().toArray());
            assertArrayEquals(values, map.values().toArray());
        });

        JdbcTable table = getBuiltTable();
        Map<String, String> map = table.getColumnsTypes();
        String[] keys = {COL_THE_GEOM, COL_THE_GEOM2.toUpperCase(), COL_ID, COL_VALUE, COL_MEANING};
        String[] values = {"POINT", "POINT", "INTEGER", "DOUBLE", "VARCHAR"};
        assertArrayEquals(keys, map.keySet().toArray());
        assertArrayEquals(values, map.values().toArray());

        table = getLinkedTable();
        map = table.getColumnsTypes();
        keys = new String[]{COL_THE_GEOM, COL_THE_GEOM2.toUpperCase(), COL_ID, COL_VALUE, COL_MEANING};
        values = new String[]{"GEOMETRY", "GEOMETRY", "INTEGER", "DOUBLE", "VARCHAR"};
        assertArrayEquals(keys, map.keySet().toArray());
        assertArrayEquals(values, map.values().toArray());
    }

    /**
     * Test the sql building method.
     */
    @Test
    void testSqlBuilding() {
        Map<String, IOptionBuilder.Order> map = new HashMap<>();
        map.put("toto", IOptionBuilder.Order.ASC);
        map.put("tata", IOptionBuilder.Order.DESC);

        List<String> columns = new ArrayList<>();
        columns.add("TOTO");
        columns.add("tata");
        columns.add("TIti");

        assertEquals("SELECT TOTO, tata, TIti FROM ORBISGIS WHERE toto", getTable().columns("TOTO", "tata", "TIti").where("toto").toString().trim());
        assertEquals("SELECT TOTO, tata, TIti FROM ORBISGIS WHERE toto", getTable().columns(columns).where("toto").toString().trim());
        assertEquals("SELECT * FROM ORBISGIS WHERE toto", getTable().where("toto").toString().trim());
        assertEquals("SELECT * FROM ORBISGIS GROUP BY toto", getTable().groupBy("toto").toString().trim());
        assertEquals("SELECT * FROM ORBISGIS GROUP BY toto,tata", getTable().groupBy("toto", "tata").toString().trim());
        assertEquals("SELECT * FROM ORBISGIS ORDER BY toto", getTable().orderBy("toto").toString().trim());
        assertEquals("SELECT * FROM ORBISGIS ORDER BY toto ASC", getTable().orderBy("toto", IOptionBuilder.Order.ASC).toString().trim());
        assertEquals("SELECT * FROM ORBISGIS ORDER BY toto ASC, tata DESC", getTable().orderBy(map).toString().trim());
        assertEquals("SELECT * FROM ORBISGIS LIMIT 0", getTable().limit(0).toString().trim());

        assertEquals(2, getTable().columns("the_geom").getTable().getRowCount());
    }

    /**
     * Test the {@link JdbcTable#columns(String...)} and {@link JdbcTable#columns(List)} methods.
     */
    @Test
    void testColumns() {
        JdbcTable table = getTable();
        JdbcSpatialTable spatialTable = (JdbcSpatialTable) dataSource.getSpatialTable(TABLE_NAME);

        List<String> columns = new ArrayList<>();
        columns.add("TOTO");
        columns.add("tata");
        columns.add("TIti");

        assertEquals("SELECT TOTO, tata, TIti FROM ORBISGIS WHERE toto", (table.columns("TOTO", "tata", "TIti")).where("toto").toString().trim());
        assertEquals("SELECT TOTO, tata, TIti FROM ORBISGIS WHERE toto", (table.columns(columns)).where("toto").toString().trim());
        assertEquals("SELECT TOTO, tata, TIti FROM ORBISGIS WHERE toto", (spatialTable.columns("TOTO", "tata", "TIti")).where("toto").toString().trim());
        assertEquals("SELECT TOTO, tata, TIti FROM ORBISGIS WHERE toto", (spatialTable.columns(columns)).where("toto").toString().trim());
    }

    /**
     * Test the {@link JdbcTable#getTable()} and {@link JdbcTable#getSpatialTable()} methods.
     */
    @Test
    void testGetTable() {
        List<JdbcTable> tables = Arrays.asList(getTable(), getEmptyTable(), getTempTable(), getLinkedTable(), getBuiltTable());
        tables.forEach(table -> {
            assertNotNull(table.getTable());
            assertNull(table.getSpatialTable());
        });
    }

    /**
     * Test the {@link JdbcTable#getTable()} method.
     */
    @Test
    public void testAsType() {
        assertNotNull(getTable().asType(ITable.class));
        assertTrue(getTable().asType(ITable.class) instanceof ITable);
        assertNotNull(getTable().asType(ISpatialTable.class));
        assertEquals("+--------------------+\n" +
                        "|      ORBISGIS      |\n" +
                        "+--------------------+--------------------+--------------------+--------------------+--------------------+\n" +
                        "|      THE_GEOM      |     THE_GEOM2      |         ID         |       VALUE        |      MEANING       |\n" +
                        "+--------------------+--------------------+--------------------+--------------------+--------------------+\n" +
                        "|POINT (0 0)         |POINT (1 1)         |                   1|                 2.3|Simple points       |\n" +
                        "|POINT (0 1)         |POINT (10 11)       |                   2|               0.568|3D point            |\n" +
                        "+--------------------+--------------------+--------------------+--------------------+--------------------+\n",
                getTable().asType(Ascii.class).toString());
        assertEquals("+--------------------+\n" +
                        "|       " + IJdbcTable.QUERY_LOCATION + "        |\n" +
                        "+--------------------+--------------------+--------------------+--------------------+--------------------+\n" +
                        "|      THE_GEOM      |     THE_GEOM2      |         ID         |       VALUE        |      MEANING       |\n" +
                        "+--------------------+--------------------+--------------------+--------------------+--------------------+\n" +
                        "|POINT (0 0)         |POINT (1 1)         |                   1|                 2.3|Simple points       |\n" +
                        "|POINT (0 1)         |POINT (10 11)       |                   2|               0.568|3D point            |\n" +
                        "+--------------------+--------------------+--------------------+--------------------+--------------------+\n",
                getBuiltTable().asType(Ascii.class).toString());
        assertEquals("<table>\n" +
                "<caption>ORBISGIS</caption>\n" +
                "<tr></tr>\n" +
                "<tr>\n" +
                "<th align=\"CENTER\">THE_GEOM</th>\n" +
                "<th align=\"CENTER\">THE_GEOM2</th>\n" +
                "<th align=\"CENTER\">ID</th>\n" +
                "<th align=\"CENTER\">VALUE</th>\n" +
                "<th align=\"CENTER\">MEANING</th>\n" +
                "</tr>\n" +
                "<tr></tr>\n" +
                "<tr>\n" +
                "<td align=\"LEFT\">POINT (0 0)</td>\n" +
                "<td align=\"LEFT\">POINT (1 1)</td>\n" +
                "<td align=\"RIGHT\">1</td>\n" +
                "<td align=\"RIGHT\">2.3</td>\n" +
                "<td align=\"LEFT\">Simple points</td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "<td align=\"LEFT\">POINT (0 1)</td>\n" +
                "<td align=\"LEFT\">POINT (10 11)</td>\n" +
                "<td align=\"RIGHT\">2</td>\n" +
                "<td align=\"RIGHT\">0.568</td>\n" +
                "<td align=\"LEFT\">3D point</td>\n" +
                "</tr>\n" +
                "<tr></tr>\n" +
                "</table>\n", getTable().asType(Html.class).toString());
        assertEquals("<table>\n" +
                "<caption>" + IJdbcTable.QUERY_LOCATION + "</caption>\n" +
                "<tr></tr>\n" +
                "<tr>\n" +
                "<th align=\"CENTER\">THE_GEOM</th>\n" +
                "<th align=\"CENTER\">THE_GEOM2</th>\n" +
                "<th align=\"CENTER\">ID</th>\n" +
                "<th align=\"CENTER\">VALUE</th>\n" +
                "<th align=\"CENTER\">MEANING</th>\n" +
                "</tr>\n" +
                "<tr></tr>\n" +
                "<tr>\n" +
                "<td align=\"LEFT\">POINT (0 0)</td>\n" +
                "<td align=\"LEFT\">POINT (1 1)</td>\n" +
                "<td align=\"RIGHT\">1</td>\n" +
                "<td align=\"RIGHT\">2.3</td>\n" +
                "<td align=\"LEFT\">Simple points</td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "<td align=\"LEFT\">POINT (0 1)</td>\n" +
                "<td align=\"LEFT\">POINT (10 11)</td>\n" +
                "<td align=\"RIGHT\">2</td>\n" +
                "<td align=\"RIGHT\">0.568</td>\n" +
                "<td align=\"LEFT\">3D point</td>\n" +
                "</tr>\n" +
                "<tr></tr>\n" +
                "</table>\n", getBuiltTable().asType(Html.class).toString());
    }

    /**
     * Test the {@link JdbcTable#getSummary()} method.
     */
    @Test
    void testGetSummary() {
        assertEquals("\"ORBISGIS\"; row count : 2; column count : 5", getTable().getSummary().toString());
        assertEquals("\"ORBISGIS\"", getTable().getSummary().getLocation().toString());
        assertEquals(5, getTable().getSummary().getColumnCount());
        assertEquals(2, getTable().getSummary().getRowCount());

        assertEquals(IJdbcTable.QUERY_LOCATION + "; row count : 2; column count : 5", getBuiltTable().getSummary().toString());
        assertNull(getBuiltTable().getSummary().getLocation());
        assertEquals(5, getBuiltTable().getSummary().getColumnCount());
        assertEquals(2, getBuiltTable().getSummary().getRowCount());

        assertEquals("\"ORBISGIS_EMPTY\"; row count : 0; column count : 5", getEmptyTable().getSummary().toString());
        assertEquals("\"ORBISGIS_EMPTY\"", getEmptyTable().getSummary().getLocation().toString());
        assertEquals(5, getEmptyTable().getSummary().getColumnCount());
        assertEquals(0, getEmptyTable().getSummary().getRowCount());

        assertEquals("\"TEMPTABLE\"; row count : 0; column count : 5", getTempTable().getSummary().toString());
        assertEquals("\"TEMPTABLE\"", getTempTable().getSummary().getLocation().toString());
        assertEquals(5, getTempTable().getSummary().getColumnCount());
        assertEquals(0, getTempTable().getSummary().getRowCount());

        assertEquals("\"LINKEDTABLE\"; row count : 2; column count : 5", getLinkedTable().getSummary().toString());
        assertEquals("\"LINKEDTABLE\"", getLinkedTable().getSummary().getLocation().toString());
        assertEquals(5, getLinkedTable().getSummary().getColumnCount());
        assertEquals(2, getLinkedTable().getSummary().getRowCount());
    }

    /**
     * Simple implementation of the {@link JdbcDataSource} abstract class for test purpose.
     */
    private static class DummyJdbcDataSource extends JdbcDataSource {

        private DummyJdbcDataSource(Sql parent, DataBaseType databaseType) {
            super(parent, databaseType);
        }

        @Override
        public IJdbcTable getTable(String tableName) {
            return null;
        }

        @Override
        public IJdbcSpatialTable getSpatialTable(String tableName) {
            try {
                if (!JDBCUtilities.tableExists(connection,
                        TableLocation.parse(tableName, getDataBaseType().equals(DataBaseType.H2GIS)).getTable())) {
                    return null;
                }
            } catch (SQLException e) {
                return null;
            }
            Statement statement;
            try {
                statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            } catch (SQLException e) {
                return null;
            }
            String query = String.format("SELECT * FROM %s", tableName);
            return new H2gisSpatialTable(new TableLocation(BASE_DATABASE, tableName), query,
                    new StatementWrapper(statement, new ConnectionWrapper(connection)), this);
        }

        @Override
        public Collection<String> getTableNames() {
            try {
                return JDBCUtilities.getTableNames(connection.getMetaData(), null, null, null, null);
            } catch (SQLException e) {

            }
            return null;
        }

        @Override
        public IJdbcTable getDataSet(String name) {
            return null;
        }

        @Override
        public boolean hasTable(String tableName) {
            try {
                return JDBCUtilities.tableExists(connection, tableName);
            } catch (SQLException ex) {
                return false;
            }
        }
    }


    /**
     * Simple implementation of the {@link IJdbcTable} interface.
     */
    private static class DummyJdbcTable extends JdbcTable {

        /**
         * Fake row index.
         */
        private int rowIndex = 0;
        /**
         * Fake data.
         */
        private Object[] data = new Object[]{"string", 0.2};
        /**
         * True if throws exception, false otherwise.
         */
        private boolean sqlException = false;
        /**
         * Private data.
         */
        private Object privateData;

        private DummyJdbcTable(DataBaseType dataBaseType, JdbcDataSource jdbcDataSource, TableLocation tableLocation,
                               Statement statement, String baseQuery) {
            super(dataBaseType, jdbcDataSource, tableLocation, statement, baseQuery);
            privateData = "tutu";
        }

        private void getPrivateMethod() {/*Does nothing*/}

        public Object[] getArrayMethod(Object[] array) {
            return array;
        }

        public Object[] getParametersMethod(String param1, Double param2) {
            return new Object[]{param1, param2};
        }

        public Object[] getParametersMethod(Object param1, Object param2) {
            return new Object[]{param1, param2};
        }

        public String getParameterMethod(String param1) {
            return param1;
        }

        public void dupMethod() throws IllegalAccessException {
            throw new IllegalAccessException();
        }

        @Override
        public boolean next() throws SQLException {
            if (!sqlException) {
                return rowIndex++ < data.length;
            } else {
                throw new SQLException();
            }
        }

        @Override
        public JdbcTable columns(String... cols) {
            return (JdbcTable) super.columns(cols);
        }

        @Override
        public JdbcTable columns(List<String> cols) {
            return (JdbcTable) super.columns(cols);
        }
    }
}
