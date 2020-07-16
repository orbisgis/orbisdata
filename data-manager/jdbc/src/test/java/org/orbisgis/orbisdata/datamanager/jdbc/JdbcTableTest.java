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
import groovy.lang.MetaClass;
import groovy.lang.MissingMethodException;
import groovy.lang.MissingPropertyException;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.metaclass.MissingPropertyExceptionNoStack;
import org.h2.jdbc.JdbcResultSetMetaData;
import org.h2gis.functions.factory.H2GISDBFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.orbisgis.commons.printer.Ascii;
import org.orbisgis.commons.printer.Html;
import org.orbisgis.orbisdata.datamanager.api.dataset.DataBaseType;
import org.orbisgis.orbisdata.datamanager.api.dataset.IJdbcTable;
import org.orbisgis.orbisdata.datamanager.api.dataset.ISpatialTable;
import org.orbisgis.orbisdata.datamanager.api.dataset.ITable;
import org.orbisgis.orbisdata.datamanager.api.datasource.IJdbcDataSource;
import org.orbisgis.orbisdata.datamanager.jdbc.h2gis.H2GIS;
import org.orbisgis.orbisdata.datamanager.jdbc.h2gis.H2gisSpatialTable;
import org.orbisgis.orbisdata.datamanager.jdbc.h2gis.H2gisTable;
import org.orbisgis.orbisdata.datamanager.jdbc.postgis.PostgisTable;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private static H2GIS dataSource;
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
    private static final String COL_VALUE = "VAL";
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
        dataSource = H2GIS.open(connection);
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

            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.execute("DROP TABLE IF EXISTS " + TABLE_NAME + "," + LINKED_NAME + "," + TEMP_NAME + "," + EMPTY_NAME);
            statement.execute("CREATE TABLE " + TABLE_NAME + " (" + COL_THE_GEOM + " GEOMETRY, " + COL_THE_GEOM2 + " GEOMETRY(POINT Z)," +
                    COL_ID + " INTEGER, " + COL_VALUE + " FLOAT, " + COL_MEANING + " VARCHAR)");
            statement.execute("INSERT INTO " + TABLE_NAME + " VALUES ('POINT(0 0)', 'POINT(1 1 0)', 1, 2.3, 'Simple points')");
            statement.execute("INSERT INTO " + TABLE_NAME + " VALUES ('POINT(0 1 2)', 'POINT(10 11 12)', 2, 0.568, '3D point')");
            statement.execute("CREATE LINKED TABLE " + LINKED_NAME + "('org.h2.Driver','jdbc:h2:./target/test-resources/dbH2" + LINKED_DATABASE +
                    "','sa','sa','" + TABLE_NAME + "')");
            statement.execute("CREATE TEMPORARY TABLE " + TEMP_NAME + " (" + COL_THE_GEOM + " GEOMETRY, " + COL_THE_GEOM2 + " GEOMETRY(POINT Z)," +
                    COL_ID + " INTEGER, " + COL_VALUE + " FLOAT, " + COL_MEANING + " VARCHAR)");
            statement.execute("INSERT INTO " + TEMP_NAME + " VALUES ('POINT(0 1 2)', 'POINT(10 11 12)', 2, 0.568, '3D point')");

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
     * Returns a {@link IJdbcTable} for test purpose.
     *
     * @return A {@link IJdbcTable} for test purpose.
     */
    private JdbcTable getTable() {
        return new H2gisTable(tableLocation, BASE_QUERY, statement, dataSource);
    }

    /**
     * Returns a linked {@link IJdbcTable} for test purpose.
     *
     * @return A linked {@link IJdbcTable} for test purpose.
     */
    private JdbcTable getLinkedTable() {
        return new H2gisTable(linkedLocation, LINKED_QUERY, statement, dataSource);
    }

    /**
     * Returns a temporary {@link IJdbcTable} for test purpose.
     *
     * @return A temporary {@link IJdbcTable} for test purpose.
     */
    private JdbcTable getTempTable() {
        return new H2gisTable(tempLocation, TEMP_QUERY, statement, dataSource);
    }

    /**
     * Returns an empty {@link IJdbcTable} for test purpose.
     *
     * @return An empty {@link IJdbcTable} for test purpose.
     */
    private JdbcTable getEmptyTable() {
        return new H2gisTable(emptyLocation, EMPTY_QUERY, statement, dataSource);
    }

    /**
     * Returns a {@link JdbcTable} for test purpose.
     *
     * @return A {@link JdbcTable} for test purpose.
     */
    private JdbcTable getBuiltTable() {
        return (JdbcTable) getTable().columns(COL_THE_GEOM, COL_THE_GEOM2, COL_ID, COL_VALUE, COL_MEANING)
                .filter("LIMIT 2")
                .getSpatialTable();
    }

    /**
     * Test the {@link JdbcTable#JdbcTable(DataBaseType, IJdbcDataSource, TableLocation, Statement, String)} constructor.
     */
    @Test
    void testConstructor() {
        assertNotNull(getTable());
        assertNotNull(getLinkedTable());
        assertNotNull(getTempTable());
        assertNotNull(getEmptyTable());
        assertNotNull(getBuiltTable());
    }

    @Test
    void streamTest(){
        String str = getTable().stream()
                .map(resultSet -> resultSet.getObject(COL_THE_GEOM).toString())
                .collect(Collectors.joining(" ; "));
        assertEquals("POINT (0 0) ; POINT (0 1)", str);

        str = getTempTable().stream()
                .map(resultSet -> resultSet.getObject(COL_THE_GEOM).toString())
                .collect(Collectors.joining(" ; "));
        assertEquals("POINT (0 1)", str);

        str = getBuiltTable().stream()
                .map(resultSet -> resultSet.getObject(COL_THE_GEOM).toString())
                .collect(Collectors.joining(" ; "));
        assertEquals("POINT (0 0) ; POINT (0 1)", str);

        str = getEmptyTable().stream()
                .map(resultSet -> resultSet.getObject(COL_THE_GEOM).toString())
                .collect(Collectors.joining(" ; "));
        assertEquals("", str);

        str = getLinkedTable().stream()
                .map(resultSet -> resultSet.getObject(COL_THE_GEOM).toString())
                .collect(Collectors.joining(" ; "));
        assertEquals("POINT (0 0) ; POINT (0 1)", str);
    }

    @Test
    void firstRowTest(){
        Map<String, Object> map = getTable().firstRow();
        assertEquals(5, map.size());
        assertTrue(map.containsKey(COL_ID));
        assertEquals(1, map.get(COL_ID));
        assertTrue(map.containsKey(COL_THE_GEOM));
        assertEquals("POINT (0 0)", map.get(COL_THE_GEOM).toString());
        assertTrue(map.containsKey(COL_THE_GEOM2.toUpperCase()));
        assertEquals("POINT (1 1)", map.get(COL_THE_GEOM2.toUpperCase()).toString());
        assertTrue(map.containsKey(COL_VALUE));
        assertEquals("2.3", map.get(COL_VALUE).toString());
        assertTrue(map.containsKey(COL_MEANING));
        assertEquals("Simple points", map.get(COL_MEANING));

        map = getLinkedTable().firstRow();
        assertEquals(5, map.size());
        assertTrue(map.containsKey(COL_ID));
        assertEquals(1, map.get(COL_ID));
        assertTrue(map.containsKey(COL_THE_GEOM));
        assertEquals("POINT (0 0)", map.get(COL_THE_GEOM).toString());
        assertTrue(map.containsKey(COL_THE_GEOM2.toUpperCase()));
        assertEquals("POINT (1 1)", map.get(COL_THE_GEOM2.toUpperCase()).toString());
        assertTrue(map.containsKey(COL_VALUE));
        assertEquals("2.3", map.get(COL_VALUE).toString());
        assertTrue(map.containsKey(COL_MEANING));
        assertEquals("Simple points", map.get(COL_MEANING));

        map = getBuiltTable().firstRow();
        assertEquals(5, map.size());
        assertTrue(map.containsKey(COL_ID));
        assertEquals(1, map.get(COL_ID));
        assertTrue(map.containsKey(COL_THE_GEOM));
        assertEquals("POINT (0 0)", map.get(COL_THE_GEOM).toString());
        assertTrue(map.containsKey(COL_THE_GEOM2.toUpperCase()));
        assertEquals("POINT (1 1)", map.get(COL_THE_GEOM2.toUpperCase()).toString());
        assertTrue(map.containsKey(COL_VALUE));
        assertEquals("2.3", map.get(COL_VALUE).toString());
        assertTrue(map.containsKey(COL_MEANING));
        assertEquals("Simple points", map.get(COL_MEANING));

        map = getTempTable().firstRow();
        assertTrue(!map.isEmpty());

        map = getEmptyTable().firstRow();
        assertTrue(map.isEmpty());
    }


    /**
     * Test the {@link IJdbcTable#getLocation()} method.
     */
    @Test
    public void testGetLocation() throws SQLException {
        assertEquals("\"catalog\".\"schema\".\"table\"", new PostgisTable(
                new TableLocation(BASE_DATABASE, "catalog", "schema", "table"),
                "not a request", dataSource.getConnection().createStatement(), dataSource).getLocation());
        assertEquals("\"catalog\".\"schema\".\"table\"", new H2gisTable(
                new TableLocation(BASE_DATABASE, "catalog", "schema", "table"),
                "not a request", dataSource.getConnection().createStatement(), dataSource).getLocation());
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
        JdbcTable table = new H2gisTable(new TableLocation(BASE_DATABASE, "tab"), "not a request",
                dataSource.getConnection().createStatement(), dataSource);
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
        JdbcTable table = new H2gisTable(new TableLocation(BASE_DATABASE, "tab"), "not a request",
                dataSource.getConnection().createStatement(), dataSource);
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
        assertEquals("(SELECT THE_GEOM, the_geom2, ID, VAL, MEANING FROM ORBISGIS LIMIT 2)",
                getBuiltTable().getBaseQuery().trim());
        assertEquals("(SELECT geom as g, st_area(geom) as area FROM (SELECT the_geom AS geom FROM ORBISGIS where id=1))",
                getTable().columns("the_geom AS geom").filter("where id=1").getTable().columns("geom as g", "st_area(geom) as area").toString());
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
        assertEquals(InvokerHelper.getMetaClass(H2gisTable.class), getTable().getMetaClass());
        assertEquals(InvokerHelper.getMetaClass(H2gisTable.class), getLinkedTable().getMetaClass());
        assertEquals(InvokerHelper.getMetaClass(H2gisTable.class), getTempTable().getMetaClass());
        assertEquals(InvokerHelper.getMetaClass(H2gisTable.class), getEmptyTable().getMetaClass());
        assertEquals(InvokerHelper.getMetaClass(H2gisSpatialTable.class), getBuiltTable().getMetaClass());
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
        assertTrue(getBuiltTable().isSpatial());
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
        assertEquals(1, getTempTable().getRowCount());
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
    }

    /**
     * Test the {@link JdbcTable#save(String, String)} and {@link JdbcTable#save(String)} methods.
     */
    @Test
    void testSave() {
        new File("./target/save1.json").delete();
        assertFalse(new File("./target/save1.json").exists());
        assertEquals("./target/save1.json", getTable().save("./target/save1.json"));
        assertTrue(new File("./target/save1.json").exists());

        new File("./target/save2.json").delete();
        assertFalse(new File("./target/save2.json").exists());
        assertEquals("./target/save2.json",getTable().save("./target/save2.json"), "UTF8");
        assertTrue(new File("./target/save2.json").exists());

        new File("./target/save3.json").delete();
        assertFalse(new File("./target/save3.json").exists());
        assertEquals("./target/save3.json", getTempTable().save("./target/save3.json"));
        assertTrue(new File("./target/save3.json").exists());

        new File("./target/save4.json").delete();
        assertFalse(new File("./target/save4.json").exists());
        assertEquals("./target/save4.json",getTempTable().save("./target/save4.json"), "UTF8");
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
            assertEquals(JdbcResultSetMetaData.class, table.invokeMethod("metaData", null).getClass());

            assertThrows(MissingMethodException.class, () -> table.invokeMethod("getLocation", new String[]{"tata", "toto"}));
            assertThrows(MissingMethodException.class, () -> table.invokeMethod("location", new String[]{"tata", "toto"}));
        });
    }

    /**
     * Test the {@link JdbcTable#getProperty(String)} method.
     */
    @Test
    void testGetProperty() {
        List<JdbcTable> tables = Arrays.asList(getTable(), getEmptyTable(), getTempTable(), getLinkedTable());
        tables.forEach(table -> {
            assertThrows(MissingPropertyExceptionNoStack.class, () -> table.getProperty("getLocation"));
            assertEquals(table.getLocation(), table.getProperty("location"));
            assertEquals(JdbcResultSetMetaData.class, table.getProperty("meta").getClass());
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
            assertThrows(MissingPropertyExceptionNoStack.class, () -> table.setProperty("privateData", "toto"));
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
        String[] values = {"GEOMETRY", "GEOMETRY", "INTEGER", "DOUBLE", "VARCHAR"};
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

        assertEquals("(SELECT TOTO, tata, TIti FROM ORBISGIS WHERE toto)", table.columns("TOTO", "tata", "TIti").filter("WHERE toto").toString().trim());
        assertEquals("(SELECT TOTO, tata, TIti FROM ORBISGIS WHERE toto)", table.columns(columns).filter("WHERE toto").toString().trim());
        assertEquals("(SELECT TOTO, tata, TIti FROM ORBISGIS WHERE toto)", spatialTable.columns("TOTO", "tata", "TIti").filter("WHERE toto").toString().trim());
        assertEquals("(SELECT TOTO, tata, TIti FROM ORBISGIS WHERE toto)", spatialTable.columns(columns).filter("WHERE toto").toString().trim());
    }

    /**
     * Test the {@link JdbcTable#getTable()} and {@link JdbcTable#getSpatialTable()} methods.
     */
    @Test
    void testGetTable() {
        List<JdbcTable> tables = Arrays.asList(getTable(), getEmptyTable(), getTempTable(), getLinkedTable());
        tables.forEach(table -> {
            assertNotNull(table.getTable());
            assertNull(table.getSpatialTable());
        });
        assertNotNull(getBuiltTable().getTable());
        assertNotNull(getBuiltTable().getSpatialTable());
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
                        "|      THE_GEOM      |     THE_GEOM2      |         ID         |        VAL         |      MEANING       |\n" +
                        "+--------------------+--------------------+--------------------+--------------------+--------------------+\n" +
                        "|POINT (0 0)         |POINT (1 1)         |                   1|                 2.3|Simple points       |\n" +
                        "|POINT (0 1)         |POINT (10 11)       |                   2|               0.568|3D point            |\n" +
                        "+--------------------+--------------------+--------------------+--------------------+--------------------+\n",
                getTable().asType(Ascii.class).toString());
        assertEquals("+--------------------+\n" +
                        "|       " + IJdbcTable.QUERY_LOCATION + "        |\n" +
                        "+--------------------+--------------------+--------------------+--------------------+--------------------+\n" +
                        "|      THE_GEOM      |     THE_GEOM2      |         ID         |        VAL         |      MEANING       |\n" +
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
                "<th align=\"CENTER\">VAL</th>\n" +
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
                "<th align=\"CENTER\">VAL</th>\n" +
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

        assertEquals("\"TEMPTABLE\"; row count : 1; column count : 5", getTempTable().getSummary().toString());
        assertEquals("\"TEMPTABLE\"", getTempTable().getSummary().getLocation().toString());
        assertEquals(5, getTempTable().getSummary().getColumnCount());
        assertEquals(1, getTempTable().getSummary().getRowCount());

        assertEquals("\"LINKEDTABLE\"; row count : 2; column count : 5", getLinkedTable().getSummary().toString());
        assertEquals("\"LINKEDTABLE\"", getLinkedTable().getSummary().getLocation().toString());
        assertEquals(5, getLinkedTable().getSummary().getColumnCount());
        assertEquals(2, getLinkedTable().getSummary().getRowCount());
    }

    @Test
    public void filterTest(){
        assertArrayEquals(new int[]{5, 1}, getTable().filter("limit 1").getTable().getSize());
        assertArrayEquals(new int[]{5, 0}, getTable().filter("where ID=34 limit 1").getTable().getSize());
        assertArrayEquals(new int[]{5, 0}, getTable().filter("where ID=34").getTable().getSize());
        assertArrayEquals(new int[]{5, 1}, getTable().filter("where ID=1").getTable().getSize());
    }

    /**
     * Test the {@link IJdbcTable#iterator()} method.
     */
    @Test
    public void testIterator() throws SQLException {
        IJdbcTable table = getTable();
        ResultSetIterator it = (ResultSetIterator) table.iterator();
        assertNotNull(it);
        assertTrue(it.hasNext());
        assertEquals("POINT (0 0)", it.next().getObject(1).toString());
        assertTrue(it.hasNext());
        assertEquals("POINT (0 1)", it.next().getObject(1).toString());
        assertFalse(it.hasNext());

        table = getTable();
        assertNotNull(table.iterator());
    }

    /**
     * Test the {@link IJdbcTable#eachRow(Closure)} method.
     */
    @Test
    public void testEachRow() {
        IJdbcTable table = getTable();
        final String[] result = {""};
        table.eachRow(new Closure<Object>(this) {
            @Override
            public Object call(Object argument) {
                try {
                    result[0] += ((JdbcTable) argument).getObject(1).toString();
                } catch (SQLException e) {
                    fail(e);
                }
                return argument;
            }
        });
        assertEquals("POINT (0 0)POINT (0 1)", result[0]);
    }
}
