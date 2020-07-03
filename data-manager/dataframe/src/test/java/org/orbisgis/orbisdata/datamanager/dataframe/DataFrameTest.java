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
package org.orbisgis.orbisdata.datamanager.dataframe;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.orbisgis.commons.printer.Ascii;
import org.orbisgis.commons.printer.Html;
import org.orbisgis.orbisdata.datamanager.api.dataset.IJdbcTable;
import org.orbisgis.orbisdata.datamanager.jdbc.h2gis.H2GIS;
import smile.data.formula.Formula;
import smile.data.type.DataType;
import smile.data.vector.BaseVector;
import smile.math.matrix.DenseMatrix;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.orbisgis.orbisdata.datamanager.dataframe.TestUtils.RANDOM_DS;
import static smile.data.formula.Terms.floor;

/**
 * Test class for {@link DataFrame}.
 *
 * @author Sylvain PALOMINOS (UBS LAB-STICC 2019)
 */
public class DataFrameTest {

    private DataFrame dataFrame;
    private static H2GIS h2gis;

    @BeforeAll
    public static void beforeAll() {
        h2gis = RANDOM_DS();
    }

    @BeforeEach
    public void beforeEach() {
        try {
            h2gis.execute("DROP TABLE IF EXISTS toto");
            h2gis.execute("CREATE TABLE toto(col1 int, col2 varchar, col3 boolean, col4 char, col5 TINYINT, col6 SMALLINT, " +
                    "col7 INT8, col8 REAL, col9 double, col10 time, col11 date, col12 timestamp, col13 DECIMAL(20, 2))");
            h2gis.execute("INSERT INTO toto VALUES (0, 'val0', true , 0, 0, 0, 0, 0.5, 0.0, '12:34:56', '2020-04-16', '2020-04-16 12:34:56.7', 0)");
            h2gis.execute("INSERT INTO toto VALUES (1, 'val1', false, 1, 1, 1, 1, 1.5, 1.0, '12:34:56', '2020-04-16', '2020-04-16 12:34:56.7', 1)");
            h2gis.execute("INSERT INTO toto VALUES (2, null, true , 2, 2, 2, 2, 2.5, 2.0, null, null, null, null)");
            h2gis.execute("INSERT INTO toto VALUES (3, 'val3', false, 3, 3, 3, 3, 3.5, 3.0, '12:34:56', '2020-04-16', '2020-04-16 12:34:56.7', 3)");
            h2gis.execute("INSERT INTO toto VALUES (4, 'val4', true , 4, 4, 4, 4, 4.5, 4.0, '12:34:56', '2020-04-16', '2020-04-16 12:34:56.7', 4)");
            dataFrame = DataFrame.of(h2gis.getTable("toto"));
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Tests the {@link DataFrame#apply(Formula)} method.
     */
    @Test
    void applyTest(){
        DataFrame df = null;
        Formula formula = Formula.rhs(floor("COL8"));

        try {
            IJdbcTable<?> table = h2gis.getTable("TOTO");
            assertNotNull(table);
            df = DataFrame.of(table);
            assertNotNull(df);
        } catch (SQLException e) {
            fail(e);
        }

        DataFrame floor = df.apply((Formula)null);
        assertSame(df, floor);

        floor = df.apply(formula);
        assertNotNull(floor);
        assertEquals("floor(COL8)", floor.column(0).name());
        assertTrue(DataType.isDouble(floor.column(0).type()));
        assertEquals(0.0, floor.get(0).get(0));
        assertEquals(1.0, floor.get(1).get(0));
        assertEquals(2.0, floor.get(2).get(0));
        assertEquals(3.0, floor.get(3).get(0));
        assertEquals(4.0, floor.get(4).get(0));
    }

    /**
     * Tests the {@link DataFrame#vector(int)}, {@link DataFrame#intVector(int)},
     * {@link DataFrame#stringVector(int)}, {@link DataFrame#booleanVector(int)}, {@link DataFrame#charVector(int)},
     * {@link DataFrame#byteVector(int)}, {@link DataFrame#shortVector(int)}, {@link DataFrame#longVector(int)},
     * {@link DataFrame#floatVector(int)}, {@link DataFrame#doubleVector(int)} methods test.
     */
    @Test
    void typeVectorTest(){
        assertEquals(5, dataFrame.vector(1).size());
        assertEquals(5, dataFrame.vector(0).size());
        assertEquals(5, dataFrame.stringVector(1).size());
        assertEquals(5, dataFrame.vector(2).size());
        assertEquals(5, dataFrame.stringVector(3).size());
        assertEquals(5, dataFrame.vector(4).size());
        assertEquals(5, dataFrame.vector(5).size());
        assertEquals(5, dataFrame.vector(6).size());
        assertEquals(5, dataFrame.vector(7).size());
        assertEquals(5, dataFrame.vector(8).size());
    }

    @Test
    void getTypeTest(){
        assertTrue(dataFrame.next());
        assertEquals(0, dataFrame.getInt(0));
        assertEquals("val0", dataFrame.getString(1));
        assertArrayEquals(new byte[]{'v', 'a', 'l', '0'}, dataFrame.getBytes(1));
        assertTrue((Boolean) dataFrame.getObject(2));
        assertEquals("0", dataFrame.getString(3));
        assertEquals(0, dataFrame.getByte(4));
        assertEquals(0, dataFrame.getShort(5));
        assertEquals(0, dataFrame.getLong(6));
        assertEquals(0.5, dataFrame.getFloat(7));
        assertEquals(0, dataFrame.getDouble(8));
        assertEquals("12:34:56", dataFrame.getTime(9).toString());
        assertEquals("2020-04-16", dataFrame.getDate(10).toString());
        assertEquals("2020-04-16 12:34:56.7", dataFrame.getTimestamp(11).toString());
        assertEquals(LocalDateTime.class, dataFrame.getObject(11).getClass());
        assertEquals("0.00", dataFrame.getBigDecimal(12).toString());

        assertTrue(dataFrame.next());
        assertEquals(1, dataFrame.getInt("COL1"));
        assertEquals("val1", dataFrame.getString("COL2"));
        assertArrayEquals(new byte[]{'v', 'a', 'l', '1'}, dataFrame.getBytes("COL2"));
        assertFalse(dataFrame.getBoolean("COL3"));
        assertEquals("1", dataFrame.getString("COL4"));
        assertEquals(1, dataFrame.getByte("COL5"));
        assertEquals(1, dataFrame.getShort("COL6"));
        assertEquals(1, dataFrame.getLong("COL7"));
        assertEquals(1.5, dataFrame.getFloat("COL8"));
        assertEquals(1, dataFrame.getDouble("COL9"));
        assertEquals("12:34:56", dataFrame.getTime("COL10").toString());
        assertEquals("2020-04-16", dataFrame.getDate("COL11").toString());
        assertEquals("2020-04-16 12:34:56.7", dataFrame.getTimestamp("COL12").toString());
        assertEquals(LocalDateTime.class, dataFrame.getObject("COL12").getClass());
        assertEquals("1.00", dataFrame.getBigDecimal("COL13").toString());

        assertTrue(dataFrame.next());
        assertNull(dataFrame.getString("COL2"));
        assertNull(dataFrame.getString(1));
        assertArrayEquals(new byte[]{}, dataFrame.getBytes("COL2"));
        assertArrayEquals(new byte[]{}, dataFrame.getBytes(1));
        assertNull(dataFrame.getTime("COL10"));
        assertNull(dataFrame.getTime(9));
        assertNull(dataFrame.getDate("COL11"));
        assertNull(dataFrame.getDate(10));
        assertNull(dataFrame.getTimestamp("COL12"));
        assertNull(dataFrame.getTimestamp(11));
        assertNull(dataFrame.getObject(12));
        assertNull(dataFrame.getObject(12, BigInteger.class));
        assertNull(dataFrame.getObject("COL13"));
        assertNull(dataFrame.getObject("COL13", BigInteger.class));

        assertTrue(dataFrame.next());
        assertEquals(3, dataFrame.getObject(0, int.class));
        assertEquals(3, dataFrame.getObject(0, Integer.class));
        assertEquals("val3", dataFrame.getObject(1, String.class));
        assertEquals(new String(new byte[]{'v', 'a', 'l', '3'}), new String((byte[])dataFrame.getObject(1, byte[].class)));
        assertEquals(false, dataFrame.getObject(2, Boolean.class));
        assertEquals("3", dataFrame.getObject(3, String.class));
        assertEquals((byte)3, dataFrame.getObject(4, byte.class));
        assertEquals((byte)3, dataFrame.getObject(4, Byte.class));
        assertEquals((short)3, dataFrame.getObject(5, short.class));
        assertEquals((short)3, dataFrame.getObject(5, Short.class));
        assertEquals(3l, dataFrame.getObject(6, long.class));
        assertEquals(3l, dataFrame.getObject(6, Long.class));
        assertEquals(3.5f, dataFrame.getObject(7, float.class));
        assertEquals(3.5f, dataFrame.getObject(7, Float.class));
        assertEquals(3d, dataFrame.getObject(8, double.class));
        assertEquals(3d, dataFrame.getObject(8, Double.class));
        assertEquals("12:34:56", dataFrame.getObject(9, Time.class).toString());
        assertEquals("2020-04-16", dataFrame.getObject(10, Date.class).toString());
        assertEquals("2020-04-16 12:34:56.7", dataFrame.getObject(11, Timestamp.class).toString());
        assertEquals(new BigDecimal("3.00"), dataFrame.getObject(12, BigDecimal.class));
        assertEquals(new BigInteger("3"), dataFrame.getObject(12, BigInteger.class));

        assertTrue(dataFrame.next());
        assertEquals(4, dataFrame.getObject("COL1", int.class));
        assertEquals(4, dataFrame.getObject("COL1", Integer.class));
        assertEquals("val4", dataFrame.getObject("COL2", String.class));
        assertEquals(new String(new byte[]{'v', 'a', 'l', '4'}), new String((byte[])dataFrame.getObject("COL2", byte[].class)));
        assertEquals(true, dataFrame.getObject("COL3", boolean.class));
        assertEquals(true, dataFrame.getObject("COL3", Boolean.class));
        assertEquals("4", dataFrame.getObject("COL4", String.class));
        assertEquals((byte)4, dataFrame.getObject("COL5", byte.class));
        assertEquals((byte)4, dataFrame.getObject("COL5", Byte.class));
        assertEquals((short)4, dataFrame.getObject("COL6", short.class));
        assertEquals((short)4, dataFrame.getObject("COL6", Short.class));
        assertEquals(4l, dataFrame.getObject("COL7", long.class));
        assertEquals(4l, dataFrame.getObject("COL7", Long.class));
        assertEquals(4.5f, dataFrame.getObject("COL8", float.class));
        assertEquals(4.5f, dataFrame.getObject("COL8", Float.class));
        assertEquals(4d, dataFrame.getObject("COL9", double.class));
        assertEquals(4d, dataFrame.getObject("COL9", Double.class));
        assertEquals("12:34:56", dataFrame.getObject("COL10", Time.class).toString());
        assertEquals("2020-04-16", dataFrame.getObject("COL11", Date.class).toString());
        assertEquals("2020-04-16 12:34:56.7", dataFrame.getObject("COL12", Timestamp.class).toString());
        assertEquals(new BigDecimal("4.00"), dataFrame.getObject("COL13", BigDecimal.class));
        assertEquals(new BigInteger("4"), dataFrame.getObject("COL13", BigInteger.class));

        assertThrows(UnsupportedOperationException.class, () -> dataFrame.getObject(0, Ascii.class));
        assertThrows(UnsupportedOperationException.class, () -> dataFrame.getObject("COL1", Ascii.class));
        assertThrows(UnsupportedOperationException.class, () -> dataFrame.getObject(0, UUID.class));
        assertThrows(UnsupportedOperationException.class, () -> dataFrame.getObject("COL1", UUID.class));

        assertFalse(dataFrame.next());
    }

    /**
     * Tests the {@link DataFrame#select(int...)}, {@link DataFrame#select(String...)},
     * {@link DataFrame#merge(smile.data.DataFrame...)}, {@link DataFrame#merge(BaseVector[])},
     * {@link DataFrame#drop(int...)}, {@link DataFrame#drop(String...)}
     */
    @Test
    void testSelect() {
        DataFrame df01 = dataFrame.select(0, 1, 2, 3, 4);
        assertEquals(5, df01.ncols());
        assertEquals(5, df01.nrows());
        assertArrayEquals(new String[]{"COL1", "COL2", "COL3", "COL4", "COL5"}, df01.names());

        String[] cols = new String[]{"COL6", "COL7", "COL8", "COL9"};
        DataFrame df02 = dataFrame.select(cols);
        assertEquals(4, df02.ncols());
        assertEquals(5, df02.nrows());
        assertArrayEquals(cols, df02.names());

        DataFrame df03 = df01.merge(df02);
        assertEquals(9, df03.ncols());
        assertEquals(5, df03.nrows());
        assertArrayEquals(new String[]{"COL1", "COL2", "COL3", "COL4", "COL5", "COL6", "COL7", "COL8", "COL9"}, df03.names());

        DataFrame df06 = df02.drop(0, 1, 2);
        assertEquals(1, df06.ncols());
        assertEquals(5, df06.nrows());
        assertArrayEquals(new String[]{"COL9"}, df06.names());

        DataFrame df04 = dataFrame.select("COL9");
        assertEquals(1, df04.ncols());
        assertEquals(5, df04.nrows());
        assertArrayEquals(new String[]{"COL9"}, df04.names());

        DataFrame df05 = df01.merge(df02.column(0), df02.column(1), df02.column(2)).merge(df04);
        assertEquals(9, df05.ncols());
        assertEquals(5, df05.nrows());
        assertArrayEquals(new String[]{"COL1", "COL2", "COL3", "COL4", "COL5", "COL6", "COL7", "COL8", "COL9"}, df05.names());

        DataFrame df07 = df05.union(df03, dataFrame.select(0, 1, 2, 3, 4, 5, 6, 7, 8));
        assertEquals(9, df07.ncols());
        assertEquals(15, df07.nrows());
        assertArrayEquals(new String[]{"COL1", "COL2", "COL3", "COL4", "COL5", "COL6", "COL7", "COL8", "COL9"}, df07.names());

        DataFrame df08 = df07.drop("COL1", "COL2", "COL4");
        assertEquals(6, df08.ncols());
        assertEquals(15, df08.nrows());
        assertArrayEquals(new String[]{"COL3", "COL5", "COL6", "COL7", "COL8", "COL9"}, df08.names());
    }

    /**
     * Tests the {@link DataFrame#getColumns()}, {@link DataFrame#getColumnsTypes()},
     * {@link DataFrame#getColumnType(String)}, {@link DataFrame#getColumnCount()}
     */
    @Test
    void columnsTest(){
        assertEquals(13, dataFrame.ncols());
        assertEquals(0, dataFrame.columnIndex("COL1"));
        assertEquals(5, dataFrame.column(0).size());

        assertArrayEquals(new String[]{"COL1", "COL2", "COL3"}, dataFrame.columns("COL1", "COL2", "COL3").names());
        assertArrayEquals(new String[]{"COL1", "COL2", "COL3"}, dataFrame.columns(Arrays.asList("COL1", "COL2", "COL3")).names());

        assertNull(dataFrame.getColumnType("COL"));

        assertEquals(13, dataFrame.getColumns().size());
        assertTrue(dataFrame.getColumns().contains("COL1"));
        assertTrue(dataFrame.getColumns().contains("COL2"));
        assertTrue(dataFrame.getColumns().contains("COL3"));
        assertTrue(dataFrame.getColumns().contains("COL4"));
        assertTrue(dataFrame.getColumns().contains("COL5"));
        assertTrue(dataFrame.getColumns().contains("COL6"));
        assertTrue(dataFrame.getColumns().contains("COL7"));
        assertTrue(dataFrame.getColumns().contains("COL8"));
        assertTrue(dataFrame.getColumns().contains("COL9"));
        assertTrue(dataFrame.getColumns().contains("COL10"));
        assertTrue(dataFrame.getColumns().contains("COL11"));
        assertTrue(dataFrame.getColumns().contains("COL12"));
        assertTrue(dataFrame.getColumns().contains("COL13"));

        assertEquals(13, dataFrame.getColumnsTypes().size());
        assertEquals("int", dataFrame.getColumnType("COL1"));
        assertEquals("String", dataFrame.getColumnType("COL2"));
        assertEquals("boolean", dataFrame.getColumnType("COL3"));
        assertEquals("String", dataFrame.getColumnType("COL4"));
        assertEquals("byte", dataFrame.getColumnType("COL5"));
        assertEquals("short", dataFrame.getColumnType("COL6"));
        assertEquals("long", dataFrame.getColumnType("COL7"));
        assertEquals("float", dataFrame.getColumnType("COL8"));
        assertEquals("double", dataFrame.getColumnType("COL9"));
        assertEquals("Time[HH:mm[:ss]]", dataFrame.getColumnType("COL10"));
        assertEquals("Date[uuuu-MM-dd]", dataFrame.getColumnType("COL11"));
        assertEquals("DateTime[uuuu-MM-dd'T'HH:mm[:ss]]", dataFrame.getColumnType("COL12"));
        assertEquals("Decimal", dataFrame.getColumnType("COL13"));
        assertTrue(dataFrame.hasColumn("COL9", double.class));
        assertFalse(dataFrame.hasColumn("COL9", float.class));

        DataFrame df = DataFrame.of(new int[][]{{0}});
        assertNull(df.getColumnType("COL"));
    }

    /**
     * Tests the {@link DataFrame#toArray()}, {@link DataFrame#toMatrix()} methods.
     */
    @Test
    void toArrayMatrixTest() {
        String[] cols = new String[]{"COL6", "COL7", "COL8", "COL9"};
        DataFrame df = dataFrame.select(cols);
        assertEquals(5, df.toArray().length);
        assertEquals(4, df.toArray()[0].length);
        assertEquals(4, df.toArray()[1].length);
        assertEquals(4, df.toArray()[2].length);
        assertEquals(4, df.toArray()[3].length);
        assertEquals(4, df.toArray()[4].length);
        assertNotNull(df.toMatrix());
    }

    /**
     * Tests the {@link DataFrame#isSpatial()}, {@link DataFrame#getLocation()}, {@link DataFrame#getName()},
     * {@link DataFrame#getMetaData()}, {@link DataFrame#summary()}, {@link DataFrame#getSummary()},
     * {@link DataFrame#isEmpty()}, {@link DataFrame#schema()} methods.
     */
    @Test
    void informationTest(){
        assertFalse(dataFrame.isEmpty());
        assertNotNull(dataFrame.schema());
        assertEquals(13, dataFrame.schema().length());
        assertFalse(dataFrame.isSpatial());
        assertEquals("smile.data.DataFrame", dataFrame.getLocation());
        assertEquals("DataFrame", dataFrame.getName());
        assertNotNull(dataFrame.summary());
        assertNotNull(dataFrame.getMetaData());
        assertEquals(dataFrame.summary().toString(), dataFrame.getMetaData().toString());
        assertNotNull(dataFrame.getSummary());
        assertEquals(dataFrame.summary().toString(), dataFrame.getSummary().toString());

    }

    /**
     * Tests the {@link DataFrame#stream()}, {@link DataFrame#nrows()}, {@link DataFrame#getRowCount()},
     * {@link DataFrame#getRow()}, {@link DataFrame#getUniqueValues(String)}, {@link DataFrame#getFirstRow()} methods
     * and for each iteration.
     */
    @Test
    void rowTest(){
        int i = 0;
        for (BaseVector baseVector : dataFrame) {
            i++;
            assertEquals(5, baseVector.size());
        }
        assertEquals(13, i);
        assertEquals(5, dataFrame.stream().count());

        assertEquals(dataFrame.nrows(), dataFrame.getRowCount());
        assertEquals(-1, dataFrame.getRow());

        assertEquals(2, dataFrame.getUniqueValues("COL3").size());
        assertEquals(13, dataFrame.getFirstRow().size());
    }

    /**
     * Tests the {@link DataFrame#asType(Class)} method.
     */
    @Test
    void asTypeTest(){
        assertNotNull(dataFrame.asType(String.class));
        assertNotNull(dataFrame.asType(DenseMatrix.class));
        assertNotNull(dataFrame.asType(DataFrame.class));
        assertNotNull(dataFrame.asType(Ascii.class));
        assertNotNull(dataFrame.asType(Html.class));
        assertNull(dataFrame.asType(Float.class));
    }

    /**
     * Tests the {@link DataFrame#save(String, String)}, {@link DataFrame#save(String)},
     * {@link DataFrame#of(File)}, {@link DataFrame#of(String)} methods.
     */
    @Test
    void saveLoadTest() throws IOException {
        String path = "./target/" + UUID.randomUUID().toString().replaceAll("-", "_") + ".csv";
        assertTrue(dataFrame.save(path, null));
        DataFrame df2 = DataFrame.of(new File(path));
        assertNotNull(df2);
        assertEquals(13, df2.schema().length());

        assertTrue(dataFrame.save(path));
        DataFrame df3 = DataFrame.of(path);
        assertNotNull(df3);
        assertEquals(13, df3.schema().length());

        assertNull(DataFrame.of("not/ a valid /path"));
        File notCsv = new File(path.substring(0, path.lastIndexOf('.')));
        assertTrue(notCsv.createNewFile());
        assertNull(DataFrame.of(notCsv));

        //Test save exception
        assertFalse(dataFrame.save("target/\u0000"));
        assertFalse(dataFrame.save("/"));
    }

    /**
     * Tests the wrapping of a spatial table into a {@link DataFrame}.
     *
     * @throws SQLException Exception thrown when a SQL error occurs.
     */
    @Test
    void testDataFrameFromSpatialTable() throws SQLException {
        H2GIS h2GIS = RANDOM_DS();
        h2GIS.execute("DROP TABLE IF EXISTS h2gis;" +
                "CREATE TABLE h2gis (id INT, the_geom1 GEOMETRY(GEOMETRY), the_geom2 GEOMETRY(GEOMETRYCOLLECTION), " +
                "the_geom3 GEOMETRY(MULTIPOLYGON), the_geom4 GEOMETRY(POLYGON), the_geom5 GEOMETRY(MULTILINESTRING)," +
                " the_geom6 GEOMETRY(LINESTRING), the_geom7 GEOMETRY(MULTIPOINT), the_geom8 GEOMETRY(POINT));" +
                "INSERT INTO h2gis VALUES " +
                "(1, 'POINT(10 10)'::GEOMETRY, 'GEOMETRYCOLLECTION (POINT(10 10), POINT(20 20))'::GEOMETRY, " +
                "'MULTIPOLYGON (((10 10,20 10,20 20,10 20, 10 10)),((50 50,60 50,60 60,50 60, 50 50)))'::GEOMETRY, " +
                "'POLYGON ((30 30,40 30,40 40,30 40,30 30))'::GEOMETRY, 'MULTILINESTRING((20 20,30 30,40 40), (50 50,60 60,70 70))'::GEOMETRY, " +
                "'LINESTRING(80 80,90 90,100 100)'::GEOMETRY, 'MULTIPOINT((20 20),(30 30))'::GEOMETRY, 'POINT(40 40)'::GEOMETRY);" +
                "INSERT INTO h2gis VALUES " +
                "(2, 'POINT(11 11)'::GEOMETRY, 'GEOMETRYCOLLECTION (POINT(11 11), POINT(21 21))'::GEOMETRY, " +
                "'MULTIPOLYGON (((11 11,21 11,21 21,11 21, 11 11)),((51 51,61 51,61 61,51 61, 51 51)))'::GEOMETRY, " +
                "'POLYGON ((31 31,41 31,41 41,31 41,31 31))'::GEOMETRY, 'MULTILINESTRING((21 21,31 31,41 41), (51 51,61 61,71 71))'::GEOMETRY, " +
                "'LINESTRING(81 81,91 91,111 111)'::GEOMETRY, 'MULTIPOINT((21 21),(31 31))'::GEOMETRY, 'POINT(41 41)'::GEOMETRY);");
        DataFrame df = DataFrame.of(h2GIS.getTable("H2GIS"));
        assertNotNull(df);
        assertNotNull(df.schema());
        assertEquals(9, df.schema().length());
        assertEquals(9, df.ncols());
        assertEquals(0, df.columnIndex("ID"));
        assertEquals(1, df.columnIndex("THE_GEOM1"));
        assertEquals(2, df.columnIndex("THE_GEOM2"));
        assertEquals(3, df.columnIndex("THE_GEOM3"));
        assertEquals(4, df.columnIndex("THE_GEOM4"));
        assertEquals(5, df.columnIndex("THE_GEOM5"));
        assertEquals(6, df.columnIndex("THE_GEOM6"));
        assertEquals(7, df.columnIndex("THE_GEOM7"));
        assertEquals(8, df.columnIndex("THE_GEOM8"));
        assertEquals(2, df.vector(0).size());
        assertEquals(2, df.stringVector(1).size());
        assertEquals(2, df.stringVector(2).size());
        assertEquals(2, df.stringVector(3).size());
        assertEquals(2, df.stringVector(4).size());
        assertEquals(2, df.stringVector(5).size());
        assertEquals(2, df.stringVector(6).size());
        assertEquals(2, df.stringVector(7).size());
        assertEquals(2, df.stringVector(8).size());

        df = DataFrame.of(h2GIS.getConnection().createStatement().executeQuery("SELECT id FROM h2gis"));
        assertNotNull(df);
        assertNotNull(df.schema());
    }

    /**
     * Tests {@link DataFrame#reload()} method.
     */
    @Test
    public void reloadTest(){
        assertFalse(dataFrame.reload());
    }

    /**
     * Tests {@link DataFrame#next()}, {@link DataFrame#previous()}, {@link DataFrame#last()},
     * {@link DataFrame#first()}, {@link DataFrame#isFirst()}, {@link DataFrame#isLast()}, {@link DataFrame#getRow()}
     * methods.
     */
    @Test
    public void cursorPositionTest(){
        assertFalse(dataFrame.isFirst());
        assertFalse(dataFrame.isLast());
        assertEquals(-1, dataFrame.getRow());

        assertTrue(dataFrame.first());
        assertTrue(dataFrame.isFirst());
        assertFalse(dataFrame.isLast());
        assertEquals(0, dataFrame.getRow());

        assertTrue(dataFrame.next());
        assertTrue(dataFrame.next());
        assertFalse(dataFrame.isFirst());
        assertFalse(dataFrame.isLast());
        assertEquals(2, dataFrame.getRow());

        assertTrue(dataFrame.previous());
        assertFalse(dataFrame.isFirst());
        assertFalse(dataFrame.isLast());
        assertEquals(1, dataFrame.getRow());

        assertTrue(dataFrame.first());
        assertTrue(dataFrame.isFirst());
        assertFalse(dataFrame.isLast());
        assertEquals(0, dataFrame.getRow());

        assertFalse(dataFrame.previous());
        assertTrue(dataFrame.isFirst());
        assertFalse(dataFrame.isLast());
        assertEquals(0, dataFrame.getRow());

        assertTrue(dataFrame.last());
        assertFalse(dataFrame.isFirst());
        assertTrue(dataFrame.isLast());
        assertEquals(4, dataFrame.getRow());

        assertFalse(dataFrame.next());
        assertFalse(dataFrame.isFirst());
        assertTrue(dataFrame.isLast());
        assertEquals(4, dataFrame.getRow());

        assertTrue(dataFrame.previous());
        assertTrue(dataFrame.previous());
        assertFalse(dataFrame.isFirst());
        assertFalse(dataFrame.isLast());
        assertEquals(2, dataFrame.getRow());
    }

    /**
     * Tests {@link DataFrame#filter(String)} method.
     */
    @Test
    public void filterTest(){
        assertThrows(UnsupportedOperationException.class, () -> dataFrame.filter("tata"));
    }
}
