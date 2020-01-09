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

import org.junit.jupiter.api.Test;
import org.orbisgis.orbisdata.datamanager.jdbc.h2gis.H2GIS;
import smile.data.vector.BaseVector;
import smile.math.matrix.DenseMatrix;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.orbisgis.orbisdata.datamanager.jdbc.h2gis.H2GIS.open;

/**
 * Test class for {@link DataFrame}.
 *
 * @author Sylvain PALOMINOS (UBS LAB-STICC 2019)
 */
public class DataFrameTest {

    private static H2GIS RANDOM_DS() {
        return open("./target/" + UUID.randomUUID().toString().replaceAll("-", "_"));
    }

    @Test
    void testDataFrameFromTable() throws SQLException, IOException {
        H2GIS h2gis = RANDOM_DS();
        h2gis.execute("CREATE TABLE toto(col1 int, col2 varchar, col3 boolean, col4 char, col5 TINYINT, col6 SMALLINT, col7 INT8, col8 REAL, col9 double)");
        h2gis.execute("INSERT INTO toto VALUES (0, 'val0', true , 0, 0, 0, 0, 0.0, 0.0)");
        h2gis.execute("INSERT INTO toto VALUES (1, 'val1', false, 1, 1, 1, 1, 1.0, 1.0)");
        h2gis.execute("INSERT INTO toto VALUES (2, 'val2', true , 2, 2, 2, 2, 2.0, 2.0)");
        h2gis.execute("INSERT INTO toto VALUES (3, 'val3', false, 3, 3, 3, 3, 3.0, 3.0)");
        h2gis.execute("INSERT INTO toto VALUES (4, 'val4', true , 4, 4, 4, 4, 4.0, 4.0)");


        DataFrame df = DataFrame.of(h2gis.getTable("toto"));
        assertNotNull(df);
        assertNotNull(df.schema());
        assertEquals(9, df.schema().length());
        assertEquals(9, df.ncols());
        assertEquals(0, df.columnIndex("COL1"));
        assertEquals(5, df.column(0).size());
        assertEquals(5, df.vector(1).size());
        assertEquals(5, df.intVector(0).size());
        assertEquals(5, df.stringVector(1).size());
        assertEquals(5, df.booleanVector(2).size());
        //assertEquals(5, df.charVector(3).size());
        assertEquals(5, df.byteVector(4).size());
        assertEquals(5, df.shortVector(5).size());
        assertEquals(5, df.longVector(6).size());
        assertEquals(5, df.floatVector(7).size());
        assertEquals(5, df.doubleVector(8).size());

        DataFrame df01 = df.select(0, 1, 2, 3, 4);
        assertEquals(5, df01.ncols());
        assertArrayEquals(new String[]{"COL1", "COL2", "COL3", "COL4", "COL5"}, df01.names());

        String[] cols = new String[]{"COL6", "COL7", "COL8", "COL9"};
        DataFrame df02 = df.select(cols);
        assertEquals(4, df02.ncols());
        assertArrayEquals(cols, df02.names());

        DataFrame df03 = df01.merge(df02);
        assertEquals(9, df03.ncols());
        assertArrayEquals(new String[]{"COL1", "COL2", "COL3", "COL4", "COL5", "COL6", "COL7", "COL8", "COL9"}, df03.names());

        DataFrame df06 = df02.drop(0, 1, 2);
        assertEquals(1, df06.ncols());
        assertArrayEquals(new String[]{"COL9"}, df06.names());

        DataFrame df04 = df.select("COL9");
        assertEquals(1, df04.ncols());
        assertArrayEquals(new String[]{"COL9"}, df04.names());

        DataFrame df05 = df01.merge(df02.column(0), df02.column(1), df02.column(2)).merge(df04);
        assertEquals(9, df05.ncols());
        assertArrayEquals(new String[]{"COL1", "COL2", "COL3", "COL4", "COL5", "COL6", "COL7", "COL8", "COL9"}, df05.names());

        DataFrame df07 = df05.union(df03, df);
        assertEquals(9, df07.ncols());
        assertEquals(15, df07.nrows());
        assertArrayEquals(new String[]{"COL1", "COL2", "COL3", "COL4", "COL5", "COL6", "COL7", "COL8", "COL9"}, df07.names());

        assertEquals(5, df02.toArray().length);
        assertEquals(4, df02.toArray()[0].length);
        assertEquals(4, df02.toArray()[1].length);
        assertEquals(4, df02.toArray()[2].length);
        assertEquals(4, df02.toArray()[3].length);
        assertEquals(4, df02.toArray()[4].length);
        assertNotNull(df02.toMatrix());

        int i = 0;
        for (BaseVector baseVector : df) {
            i++;
            assertEquals(5, baseVector.size());
        }
        assertEquals(9, i);
        assertEquals(5, df.stream().count());

        assertFalse(df06.isEmpty());

        assertEquals(9, df.getColumns().size());
        assertTrue(df.getColumns().contains("COL1"));
        assertTrue(df.getColumns().contains("COL2"));
        assertTrue(df.getColumns().contains("COL3"));
        assertTrue(df.getColumns().contains("COL4"));
        assertTrue(df.getColumns().contains("COL5"));
        assertTrue(df.getColumns().contains("COL6"));
        assertTrue(df.getColumns().contains("COL7"));
        assertTrue(df.getColumns().contains("COL8"));
        assertTrue(df.getColumns().contains("COL9"));

        assertEquals(9, df.getColumnsTypes().size());
        assertEquals("int", df.getColumnType("COL1"));
        assertEquals("String", df.getColumnType("COL2"));
        assertEquals("boolean", df.getColumnType("COL3"));
        assertEquals("String", df.getColumnType("COL4"));
        assertEquals("byte", df.getColumnType("COL5"));
        assertEquals("short", df.getColumnType("COL6"));
        assertEquals("long", df.getColumnType("COL7"));
        assertEquals("float", df.getColumnType("COL8"));
        assertEquals("double", df.getColumnType("COL9"));
        assertEquals("int", df.getColumnType("COL1"));
        assertEquals("String", df.getColumnType("COL2"));
        assertEquals("boolean", df.getColumnType("COL3"));
        assertEquals("String", df.getColumnType("COL4"));
        assertEquals("byte", df.getColumnType("COL5"));
        assertEquals("short", df.getColumnType("COL6"));
        assertEquals("long", df.getColumnType("COL7"));
        assertEquals("float", df.getColumnType("COL8"));
        assertEquals("double", df.getColumnType("COL9"));
        assertTrue(df.hasColumn("COL9", double.class));
        assertFalse(df.hasColumn("COL9", float.class));

        assertEquals(df.nrows(), df.getRowCount());
        assertEquals(-1, df.getRow());

        assertEquals(2, df.getUniqueValues("COL3").size());
        assertEquals(9, df.getFirstRow().size());
        assertArrayEquals(new String[]{"COL1", "COL2", "COL3"}, df.columns("COL1", "COL2", "COL3").names());
        assertArrayEquals(new String[]{"COL1", "COL2", "COL3"}, df.columns(Arrays.asList("COL1", "COL2", "COL3")).names());

        assertFalse(df.isSpatial());
        assertEquals("smile.data.DataFrame", df.getLocation());
        assertEquals("DataFrame", df.getName());
        assertNotNull(df.summary());
        assertNotNull(df.getMetaData());
        assertEquals(df.summary().toString(), df.getMetaData().toString());
        assertNotNull(df.getSummary());
        assertEquals(df.summary().toString(), df.getSummary().toString());

        assertNotNull(df.asType(String.class));
        assertNotNull(df.asType(DenseMatrix.class));
        assertNotNull(df.asType(DataFrame.class));
        assertNull(df.asType(Float.class));

        String path = "./target/" + UUID.randomUUID().toString().replaceAll("-", "_") + ".csv";
        assertTrue(df.save(path, null));
        DataFrame df2 = DataFrame.of(new File(path));
        assertNotNull(df2);
        assertEquals(9, df2.schema().length());

        DataFrame df3 = DataFrame.of(path);
        assertNotNull(df3);
        assertEquals(9, df3.schema().length());

        assertNull(DataFrame.of("not/ a valid /path"));
        File notCsv = new File(path.substring(0, path.lastIndexOf('.')));
        assertTrue(notCsv.createNewFile());
        assertNull(DataFrame.of(notCsv));
    }

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
        assertEquals(2, df.intVector(0).size());
        assertEquals(2, df.stringVector(1).size());
        assertEquals(2, df.stringVector(2).size());
        assertEquals(2, df.stringVector(3).size());
        assertEquals(2, df.stringVector(4).size());
        assertEquals(2, df.stringVector(5).size());
        assertEquals(2, df.stringVector(6).size());
        assertEquals(2, df.stringVector(7).size());
        assertEquals(2, df.stringVector(8).size());
    }
}
