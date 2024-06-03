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
import groovy.lang.MetaClass;
import groovy.lang.MissingMethodException;
import groovy.lang.MissingPropertyException;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.metaclass.MissingPropertyExceptionNoStack;
import org.h2.jdbc.JdbcResultSetMetaData;
import org.h2gis.functions.factory.H2GISDBFactory;
import org.h2gis.utilities.TableLocation;
import org.h2gis.utilities.dbtypes.DBTypes;
import org.h2gis.utilities.wrapper.SpatialResultSetMetaDataImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.orbisgis.commons.printer.Ascii;
import org.orbisgis.commons.printer.Html;
import org.orbisgis.data.api.dataset.IJdbcTable;
import org.orbisgis.data.api.dataset.ISpatialTable;
import org.orbisgis.data.api.dataset.ITable;
import org.orbisgis.data.api.datasource.IJdbcDataSource;
import org.orbisgis.data.jdbc.JdbcColumn;
import org.orbisgis.data.jdbc.JdbcSpatialTable;
import org.orbisgis.data.jdbc.JdbcTable;
import org.orbisgis.data.jdbc.ResultSetIterator;
import org.orbisgis.data.jdbc.resultset.StreamResultSet;
import org.orbisgis.data.jdbc.resultset.StreamSpatialResultSet;

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
 * Test class dedicated to the {@link H2gisTable} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public class H2gisTableTest {

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

    private static final String BASE_DATABASE = H2gisTableTest.class.getSimpleName();
    private static final String TABLE_NAME = "ORBISGIS_TABLE";
    private static final String BASE_QUERY = "SELECT * FROM " + TABLE_NAME;
    private static final String TEMP_NAME = "TEMPTABLE";
    private static final String TEMP_QUERY = "SELECT * FROM " + TEMP_NAME;
    private static final String EMPTY_NAME = "ORBISGIS_EMPTY";
    private static final String EMPTY_QUERY = "SELECT * FROM " + EMPTY_NAME;
    private static final String LINKED_DATABASE = H2gisTableTest.class.getSimpleName() + "linked";
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
    static void init() throws Exception {
        try {
            connection = H2GISDBFactory.createSpatialDataBase(BASE_DATABASE, true, ";AUTO_SERVER=TRUE");
            connectionLinked = H2GISDBFactory.createSpatialDataBase(LINKED_DATABASE, true,";AUTO_SERVER=TRUE");
        } catch (SQLException e) {
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
                    COL_ID + " INTEGER, " + COL_VALUE + " DOUBLE PRECISION, " +  COL_MEANING + " VARCHAR)");
            statementLinked.execute("INSERT INTO " + TABLE_NAME + " VALUES ('POINT(0 0)', 'POINTZ(1 1 0)', 1, 2.3, 'Simple points')");
            statementLinked.execute("INSERT INTO " + TABLE_NAME + " VALUES ('POINT(0 1)', 'POINTZ(10 11 12)', 2, 0.568, '3D point')");
            statementLinked.execute("CREATE TEMPORARY TABLE " + TEMP_NAME + " (" + COL_THE_GEOM + " GEOMETRY, " + COL_THE_GEOM2 + " GEOMETRY(POINT Z)," +
                    COL_ID + " INTEGER, " + COL_VALUE + " DOUBLE PRECISION, " + COL_MEANING + " VARCHAR)");

            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.execute("DROP TABLE IF EXISTS " + TABLE_NAME + "," + LINKED_NAME + "," + TEMP_NAME + "," + EMPTY_NAME);
            statement.execute("CREATE TABLE " + TABLE_NAME + " (" + COL_THE_GEOM + " GEOMETRY, " + COL_THE_GEOM2 + " GEOMETRY(POINT Z)," +
                    COL_ID + " INTEGER, " + COL_VALUE + " DOUBLE PRECISION, " +  COL_MEANING + " VARCHAR)");
            statement.execute("INSERT INTO " + TABLE_NAME + " VALUES ('POINT(0 0)', 'POINTZ(1 1 0)', 1, 2.3,  'Simple points')");
            statement.execute("INSERT INTO " + TABLE_NAME + " VALUES ('POINT(0 1 )', 'POINTZ(10 11 12)', 2, 0.568, '3D point')");
            statement.execute("INSERT INTO " + TABLE_NAME + " VALUES ('POINT(10 11)', 'POINTZ(20 21 22)', 3, 7.18,'3D point')");
            statement.execute("CREATE LINKED TABLE " + LINKED_NAME + "('org.h2.Driver','jdbc:h2:./target/test-resources/dbH2" + LINKED_DATABASE +
                    "','sa','sa','" + TABLE_NAME + "')");
            statement.execute("CREATE TEMPORARY TABLE " + TEMP_NAME + " (" + COL_THE_GEOM + " GEOMETRY, " + COL_THE_GEOM2 + " GEOMETRY(POINT Z)," +
                    COL_ID + " INTEGER, " + COL_VALUE + " DOUBLE PRECISION, " + COL_MEANING + " VARCHAR)");
            statement.execute("INSERT INTO " + TEMP_NAME + " VALUES ('POINT(0 1)', 'POINTZ(10 11 12)', 2, 0.568, '3D point')");

            statement.execute("CREATE TABLE " + EMPTY_NAME + " (" + COL_THE_GEOM + " GEOMETRY, " + COL_THE_GEOM2 + " GEOMETRY(POINT Z)," +
                    COL_ID + " INTEGER, " + COL_VALUE + " DOUBLE PRECISION, " + COL_MEANING + " VARCHAR)");

            tableLocation = TableLocation.parse(TABLE_NAME, DBTypes.H2);
            linkedLocation = TableLocation.parse(LINKED_NAME,DBTypes.H2);
            tempLocation = TableLocation.parse(TEMP_NAME,DBTypes.H2);
            emptyLocation = TableLocation.parse(EMPTY_NAME,DBTypes.H2);
        } catch (Exception e) {
            fail(e);
        }
    }

    /**
     * Returns a {@link IJdbcTable} for test purpose.
     *
     * @return A {@link IJdbcTable} for test purpose.
     */
    private JdbcTable<StreamSpatialResultSet> getTable() {
        return new H2gisSpatialTable(tableLocation, BASE_QUERY, statement, null, dataSource);
    }

    /**
     * Returns a linked {@link IJdbcTable} for test purpose.
     *
     * @return A linked {@link IJdbcTable} for test purpose.
     */
    private JdbcTable<StreamResultSet> getLinkedTable() {
        return new H2gisTable(linkedLocation, LINKED_QUERY, statement, null, dataSource);
    }

    /**
     * Returns a temporary {@link IJdbcTable} for test purpose.
     *
     * @return A temporary {@link IJdbcTable} for test purpose.
     */
    private JdbcTable<StreamResultSet> getTempTable() {
        return new H2gisTable(tempLocation, TEMP_QUERY, statement, null, dataSource);
    }

    /**
     * Returns an empty {@link IJdbcTable} for test purpose.
     *
     * @return An empty {@link IJdbcTable} for test purpose.
     */
    private JdbcTable<StreamResultSet> getEmptyTable() {
        return new H2gisTable(emptyLocation, EMPTY_QUERY, statement, null, dataSource);
    }

    /**
     * Returns a {@link JdbcTable} for test purpose.
     *
     * @return A {@link JdbcTable} for test purpose.
     */
    private JdbcTable<StreamResultSet> getBuiltTable() throws Exception {
        return (JdbcTable) getTable().columns(COL_THE_GEOM, COL_THE_GEOM2, COL_ID, COL_VALUE, COL_MEANING)
                .filter("LIMIT 2")
                .getSpatialTable();
    }

    /**
     * Test the {@link JdbcTable#JdbcTable(DBTypes, IJdbcDataSource, TableLocation, Statement, List, String)} constructor.
     */
    @Test
    void testConstructor() throws Exception {
        assertNotNull(getTable());
        assertNotNull(getLinkedTable());
        assertNotNull(getTempTable());
        assertNotNull(getEmptyTable());
        assertNotNull(getBuiltTable());
    }

    @Test
    void streamTest() throws Exception {
        String str = getTable().stream()
                .map(resultSet -> resultSet.getObject(COL_THE_GEOM).toString())
                .collect(Collectors.joining(" ; "));
        assertEquals("POINT (0 0) ; POINT (0 1) ; POINT (10 11)", str);

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

        assertTrue(getTable().stream().filter(row -> row.getInt(COL_ID) == 2).findAny().isPresent());
        assertEquals(0, getTable().getSpatialTable().stream().filter(row -> row.getInt(COL_ID) < 3 && row.getGeometry(2).getArea() > 2000).count());
        assertEquals(2, getTable().stream().collect(Collectors.groupingBy(row -> row.getString(COL_MEANING))).size());
    }

    @Test
    void firstRowTest() throws Exception {
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
    public void testGetLocation() throws SQLException, Exception {
        assertEquals("catalog.schema.table", new PostgisTable(
                new TableLocation("catalog", "schema", "table"),
                "not a request", dataSource.getConnection().createStatement(), null, dataSource).getLocation());
        assertEquals("catalog.schema.table", new H2gisTable(
                new TableLocation( "catalog", "schema", "table"),
                "not a request", dataSource.getConnection().createStatement(), null, dataSource).getLocation());

        assertEquals("ORBISGIS_TABLE", getTable().getLocation());
        assertEquals("LINKEDTABLE", getLinkedTable().getLocation());
        assertEquals("TEMPTABLE", getTempTable().getLocation());
        assertEquals("ORBISGIS_EMPTY", getEmptyTable().getLocation());
        assertEquals(IJdbcTable.QUERY_LOCATION, getBuiltTable().getLocation());
    }



    /**
     * Test the {@link JdbcTable#getMetaData()} constructor.
     */
    @Test
    void testGetMetadata() throws SQLException, Exception {
        assertNotNull(getTable().getMetaData());
        assertNotNull(getLinkedTable().getMetaData());
        assertNotNull(getTempTable().getMetaData());
        assertNotNull(getEmptyTable().getMetaData());
        assertNotNull(getBuiltTable().getMetaData());
        JdbcTable table = new H2gisTable(new TableLocation(BASE_DATABASE, "tab"), "not a request",
                dataSource.getConnection().createStatement(), null, dataSource);
        assertThrows(SQLException.class, ()->table.getMetaData());
    }

    /**
     * Test the {@link JdbcTable#getBaseQuery()} method.
     */
    @Test
    void testGetBaseQuery() throws Exception {
        assertEquals(BASE_QUERY, getTable().getBaseQuery());
        assertEquals(LINKED_QUERY, getLinkedTable().getBaseQuery());
        assertEquals(TEMP_QUERY, getTempTable().getBaseQuery());
        assertEquals(EMPTY_QUERY, getEmptyTable().getBaseQuery());
        assertEquals("(SELECT THE_GEOM, the_geom2, ID, VAL, MEANING FROM ORBISGIS_TABLE LIMIT 2)",
                getBuiltTable().getBaseQuery().trim());
        assertEquals("(SELECT geom as g, st_area(geom) as area FROM (SELECT the_geom AS geom FROM ORBISGIS_TABLE where id=1) as foo)",
                getTable().columns("the_geom AS geom").filter("where id=1").getTable().columns("geom as g", "st_area(geom) as area").toString());
    }

    /**
     * Test the {@link JdbcTable#getJdbcDataSource()} method.
     */
    @Test
    void testGetJdbcDataSource() throws Exception {
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
    void testGetTableLocation() throws Exception {
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
    void testGetDbType() throws Exception {
        assertEquals(DBTypes.H2GIS, getTable().getDbType());
        assertEquals(DBTypes.H2GIS, getLinkedTable().getDbType());
        assertEquals(DBTypes.H2GIS, getTempTable().getDbType());
        assertEquals(DBTypes.H2GIS, getEmptyTable().getDbType());
        assertEquals(DBTypes.H2GIS, getBuiltTable().getDbType());
    }

    /**
     * Test the {@link JdbcTable#getMetaClass()} method.
     */
    @Test
    void testGetMetaClass() throws Exception {
        assertEquals(InvokerHelper.getMetaClass(H2gisSpatialTable.class), getTable().getMetaClass());
        assertEquals(InvokerHelper.getMetaClass(H2gisTable.class), getLinkedTable().getMetaClass());
        assertEquals(InvokerHelper.getMetaClass(H2gisTable.class), getTempTable().getMetaClass());
        assertEquals(InvokerHelper.getMetaClass(H2gisTable.class), getEmptyTable().getMetaClass());
        assertEquals(InvokerHelper.getMetaClass(H2gisSpatialTable.class), getBuiltTable().getMetaClass());
    }

    /**
     * Test the {@link JdbcTable#setMetaClass(MetaClass)} method.
     */
    @Test
    void testSetMetaClass() throws Exception {
        List<JdbcTable> tables = Arrays.asList(getTable(), getLinkedTable(), getTempTable(), getEmptyTable(), getBuiltTable());
        tables.forEach(table -> {
            MetaClass metaClass = InvokerHelper.getMetaClass(H2gisTable.class);
            table.setMetaClass(metaClass);
            assertEquals(metaClass, table.getMetaClass());
        });
    }

    /**
     * Test the {@link JdbcTable#isSpatial()} method.
     */
    @Test
    void testIsSpatial() throws Exception {
        assertTrue(getTable().isSpatial());
        assertFalse(getLinkedTable().isSpatial());
        assertFalse(getTempTable().isSpatial());
        assertFalse(getEmptyTable().isSpatial());
        assertTrue(getBuiltTable().isSpatial());
    }

    /**
     * Test the {@link JdbcTable#isLinked()} method.
     */
    @Test
    void testIsLinked() throws Exception {
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
    void testIsTemporary() throws Exception {
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
    void testGetColumnNames() throws Exception {
        List<String> colList = new ArrayList<>();
        colList.add(TableLocation.capsIdentifier(COL_THE_GEOM, DBTypes.H2));
        colList.add(TableLocation.capsIdentifier(COL_THE_GEOM2, DBTypes.H2));
        colList.add(TableLocation.capsIdentifier(COL_ID, DBTypes.H2));
        colList.add(TableLocation.capsIdentifier(COL_VALUE, DBTypes.H2));
        colList.add(TableLocation.capsIdentifier(COL_MEANING, DBTypes.H2));
        assertEquals(colList, getTable().getColumns());
        assertEquals(colList, getLinkedTable().getColumns());
        assertEquals(colList, getTempTable().getColumns());
        assertEquals(colList, getEmptyTable().getColumns());
        assertEquals(colList, getBuiltTable().getColumns());
    }

    /**
     * Test the {@link JdbcTable#getRowCount()} method.
     */
    @Test
    void testGetRowCount() throws Exception {
        assertEquals(3, getTable().getRowCount());
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
        assertEquals(3, getTable().getUniqueValues(COL_THE_GEOM).size());

        assertEquals(2, getLinkedTable().getUniqueValues(COL_MEANING).size());
        assertTrue(getLinkedTable().getUniqueValues(COL_MEANING).contains("Simple points"));
        assertTrue(getLinkedTable().getUniqueValues(COL_MEANING).contains("3D point"));
        assertEquals(2, getLinkedTable().getUniqueValues(COL_THE_GEOM).size());
    }

    /**
     * Test the {@link JdbcTable#save(String, String)} and {@link JdbcTable#save(String)} methods.
     */
    @Test
    void testSave() throws Exception {
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
    void testGetFirstRow() throws Exception {
        List<JdbcTable> tables = Arrays.asList(getTable(), getLinkedTable(), getBuiltTable());
        tables.forEach(table -> {
            try {
                assertEquals(5, table.getFirstRow().size());
            assertEquals("POINT (0 0)", table.getFirstRow().get(0).toString());
            assertEquals("POINT (1 1)", table.getFirstRow().get(1).toString());
            assertEquals(1, table.getFirstRow().get(2));
            assertEquals(2.3, table.getFirstRow().get(3));
            assertEquals("Simple points", table.getFirstRow().get(4));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Test the {@link JdbcTable#getColumnType(String)} method.
     */
    @Test
    void testGetColumnsType() throws Exception {
        List<JdbcTable> tables = Arrays.asList(getTable(), getEmptyTable(), getTempTable(), getLinkedTable(), getBuiltTable());
        tables.forEach(table -> {
            try {
            assertEquals("GEOMETRY", getTable().getColumnType(COL_THE_GEOM));
            assertEquals("INTEGER", getTable().getColumnType(COL_ID));
            assertEquals("CHARACTER VARYING", getTable().getColumnType(COL_MEANING));
            assertNull(getTable().getColumnType("NOT_A_COLUMN"));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Test the {@link JdbcTable#getColumnsTypes()} method.
     */
    @Test
    void testGetColumns() throws Exception {
        List<JdbcTable> tables = Arrays.asList(getTable(), getEmptyTable(), getTempTable());
        tables.forEach(table -> {
            Map<String, String> map = null;
            try {
                map = table.getColumnsTypes();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            String[] keys = {COL_THE_GEOM, COL_THE_GEOM2.toUpperCase(), COL_ID, COL_VALUE, COL_MEANING};
            String[] values = {"GEOMETRY", "GEOMETRY(POINT Z)", "INTEGER", "DOUBLE PRECISION", "CHARACTER VARYING"};
            Arrays.sort(keys);
            String[] actual = map.keySet().toArray(new String[0]);
            Arrays.sort(actual);
            assertArrayEquals(keys,actual);
            Arrays.sort(values);
            String[] actualValues = map.values().toArray(new String[0]);
            Arrays.sort(actualValues);
            assertArrayEquals(values, actualValues);
        });

        JdbcTable table = getBuiltTable();
        Map<String, String> map = table.getColumnsTypes();
        String[] keys = {COL_THE_GEOM, COL_THE_GEOM2.toUpperCase(), COL_ID, COL_VALUE, COL_MEANING};
        String[] values =  {"GEOMETRY", "GEOMETRY(POINT Z)", "INTEGER", "DOUBLE PRECISION", "CHARACTER VARYING"};
        Arrays.sort(keys);
        String[] actual = map.keySet().toArray(new String[0]);
        Arrays.sort(actual);
        assertArrayEquals(keys,actual);
        Arrays.sort(values);
        String[] actualValues = map.values().toArray(new String[0]);
        Arrays.sort(actualValues);
        assertArrayEquals(values, actualValues);

        table = getLinkedTable();
        map = table.getColumnsTypes();
        keys = new String[]{COL_THE_GEOM, COL_THE_GEOM2.toUpperCase(), COL_ID, COL_VALUE, COL_MEANING};
        values = new String[] {"GEOMETRY", "GEOMETRY(POINT Z)", "INTEGER", "DOUBLE PRECISION", "CHARACTER VARYING"};
        Arrays.sort(keys);
        actual = map.keySet().toArray(new String[0]);
        Arrays.sort(actual);
        assertArrayEquals(keys,actual);
        Arrays.sort(values);
        actualValues = map.values().toArray(new String[0]);
        Arrays.sort(actualValues);
        assertArrayEquals(values, actualValues);
    }

    /**
     * Test the {@link JdbcTable#columns(String...)} methods.
     */
    @Test
    void testColumns() throws Exception {
        JdbcTable table = getTable();
        JdbcSpatialTable spatialTable = (JdbcSpatialTable) dataSource.getSpatialTable(TABLE_NAME);

        assertEquals("(SELECT TOTO, tata, TIti FROM ORBISGIS_TABLE WHERE toto)", table.columns("TOTO", "tata", "TIti").filter("WHERE toto").toString().trim());
        assertEquals("(SELECT TOTO, tata, TIti FROM ORBISGIS_TABLE WHERE toto)", spatialTable.columns("TOTO", "tata", "TIti").filter("WHERE toto").toString().trim());
    }

    /**
     * Test the {@link JdbcTable#getTable()} and {@link JdbcTable#getSpatialTable()} methods.
     */
    @Test
    void testGetTable() throws Exception {
        List<JdbcTable> tables = Arrays.asList(getEmptyTable(), getTempTable(), getLinkedTable());
        tables.forEach(table -> {
            try {
                assertNotNull(table.getTable());
            assertNull(table.getSpatialTable());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        assertNotNull(getBuiltTable().getTable());
        assertNotNull(getBuiltTable().getSpatialTable());
        assertNotNull(getTable().getTable());
        assertNotNull(getTable().getSpatialTable());
    }

    /**
     * Test the {@link JdbcTable#getTable()} method.
     */
    @Test
    public void testAsType() throws Exception {
        assertNotNull(getTable().asType(ITable.class));
        assertTrue(getTable().asType(ITable.class) instanceof ITable);
        assertNotNull(getTable().asType(ISpatialTable.class));
        assertEquals("+--------------------+\n" +
                        "|   ORBISGIS_TABLE   |\n" +
                        "+--------------------+--------------------+--------------------+--------------------+--------------------+\n" +
                        "|      THE_GEOM      |     THE_GEOM2      |         ID         |        VAL         |      MEANING       |\n" +
                        "+--------------------+--------------------+--------------------+--------------------+--------------------+\n" +
                        "|POINT (0 0)         |POINT (1 1)         |                   1|                 2.3|Simple points       |\n" +
                        "|POINT (0 1)         |POINT (10 11)       |                   2|               0.568|3D point            |\n" +
                        "|POINT (10 11)       |POINT (20 21)       |                   3|                7.18|3D point            |\n" +
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
                "<caption>ORBISGIS_TABLE</caption>\n" +
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
                "<tr>\n" +
                "<td align=\"LEFT\">POINT (10 11)</td>\n" +
                "<td align=\"LEFT\">POINT (20 21)</td>\n" +
                "<td align=\"RIGHT\">3</td>\n" +
                "<td align=\"RIGHT\">7.18</td>\n" +
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
    void testGetSummary() throws Exception {
        assertEquals("ORBISGIS_TABLE; row count : 3; column count : 5", getTable().getSummary().toString());
        assertEquals("ORBISGIS_TABLE", getTable().getSummary().getLocation().toString());
        assertEquals(5, getTable().getSummary().getColumnCount());
        assertEquals(3, getTable().getSummary().getRowCount());
        assertEquals(IJdbcTable.QUERY_LOCATION + "; row count : 2; column count : 5", getBuiltTable().getSummary().toString());
        assertNull(getBuiltTable().getSummary().getLocation());
        assertEquals(5, getBuiltTable().getSummary().getColumnCount());
        assertEquals(2, getBuiltTable().getSummary().getRowCount());

        assertEquals("ORBISGIS_EMPTY; row count : 0; column count : 5", getEmptyTable().getSummary().toString());
        assertEquals("ORBISGIS_EMPTY", getEmptyTable().getSummary().getLocation().toString());
        assertEquals(5, getEmptyTable().getSummary().getColumnCount());
        assertEquals(0, getEmptyTable().getSummary().getRowCount());

        assertEquals("TEMPTABLE; row count : 1; column count : 5", getTempTable().getSummary().toString());
        assertEquals("TEMPTABLE", getTempTable().getSummary().getLocation().toString());
        assertEquals(5, getTempTable().getSummary().getColumnCount());
        assertEquals(1, getTempTable().getSummary().getRowCount());

        assertEquals("LINKEDTABLE; row count : 2; column count : 5", getLinkedTable().getSummary().toString());
        assertEquals("LINKEDTABLE", getLinkedTable().getSummary().getLocation().toString());
        assertEquals(5, getLinkedTable().getSummary().getColumnCount());
        assertEquals(2, getLinkedTable().getSummary().getRowCount());
    }

    @Test
    public void filterTest() throws Exception {
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
        assertTrue(it.hasNext());
        assertEquals("POINT (10 11)", it.next().getObject(1).toString());
        assertFalse(it.hasNext());

        table = getTable();
        assertNotNull(table.iterator());
    }

    /**
     * Test the {@link IJdbcTable#eachRow(Closure)} method.
     */
    @Test
    public void testEachRow() throws Exception {
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
        assertEquals("POINT (0 0)POINT (0 1)POINT (10 11)", result[0]);
    }
}
