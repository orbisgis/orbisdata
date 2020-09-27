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
package org.orbisgis.orbisdata.datamanager.dataframe

import org.junit.jupiter.api.Test
import org.orbisgis.orbisdata.datamanager.jdbc.h2gis.H2GIS
import smile.base.cart.SplitRule
import smile.classification.RandomForest
import smile.data.formula.Formula
import smile.data.measure.NominalScale
import smile.data.type.StructField
import smile.data.vector.BaseVector
import smile.data.vector.IntVector
import smile.data.vector.StringVector
import smile.validation.Validation

import java.util.stream.IntStream

import static org.junit.jupiter.api.Assertions.*

class GroovyDataFrameTest {

    private static def RANDOM_DS = { H2GIS.open("./target/" + UUID.randomUUID().toString().replaceAll("-", "_")) }

    @Test
    void testDataFrameFromSpatialTable() {
        def h2GIS = RANDOM_DS();
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
        DataFrame df = DataFrame.of(h2GIS.getTable("H2GIS"))
        assertNotNull df
        assertNotNull df.schema()
        assertEquals 9, df.schema().length()
        assertEquals 9, df.ncols()
        assertEquals 0, df.columnIndex("ID")
        assertEquals 1, df.columnIndex("THE_GEOM1")
        assertEquals 2, df.columnIndex("THE_GEOM2")
        assertEquals 3, df.columnIndex("THE_GEOM3")
        assertEquals 4, df.columnIndex("THE_GEOM4")
        assertEquals 5, df.columnIndex("THE_GEOM5")
        assertEquals 6, df.columnIndex("THE_GEOM6")
        assertEquals 7, df.columnIndex("THE_GEOM7")
        assertEquals 8, df.columnIndex("THE_GEOM8")
        assertEquals 2, df.vector(0).size()
        assertEquals 2, df.stringVector(1).size()
        assertEquals 2, df.stringVector(2).size()
        assertEquals 2, df.stringVector(3).size()
        assertEquals 2, df.stringVector(4).size()
        assertEquals 2, df.stringVector(5).size()
        assertEquals 2, df.stringVector(6).size()
        assertEquals 2, df.stringVector(7).size()
        assertEquals 2, df.stringVector(8).size()
    }

    @Test
    void testSaveLoadDFFromTable() {
        def h2GIS = RANDOM_DS();
        h2GIS.execute("""
                DROP TABLE IF EXISTS geotable;
                CREATE TABLE geotable (id int, the_geom geometry(point), type varchar,temperature int, baby_jeje_weight double);
                INSERT INTO geotable VALUES (1, 'POINT(10 10)'::GEOMETRY, 'grass', -12, 4.780), (2, 'POINT(1 1)'::GEOMETRY, 'corn', 22, 5.500);
        """)
        DataFrame df = DataFrame.of(h2GIS.getSpatialTable("GEOTABLE").filter("where type = 'grass'").getSpatialTable())
        assertNotNull df
        assertNotNull df.schema()
        assertEquals 5, df.ncols()
        assertEquals 0, df.columnIndex("ID")
        assertEquals 1, df.columnIndex("THE_GEOM")
        assertEquals 2, df.columnIndex("TYPE")
        assertEquals 3, df.columnIndex("TEMPERATURE")
        assertEquals 4, df.columnIndex("BABY_JEJE_WEIGHT")
        df.save(h2GIS, "output_dataframe", true)
        def table = h2GIS.getTable("OUTPUT_DATAFRAME")
        assertNotNull(table)
        assertEquals(5, table.getColumnCount())
        assertEquals(1, table.getRowCount())
        table.next();
        def row = table.firstRow()
        assertEquals(1, row.ID)
        assertEquals("POINT (10 10)", row.THE_GEOM)
        assertEquals("grass", row.TYPE)
        assertEquals(-12, row.TEMPERATURE)
        assertEquals(4.780, row.BABY_JEJE_WEIGHT)
    }

    @Test
    void testCreateDFFromTable() {
        def h2GIS = RANDOM_DS();
        h2GIS.execute("""
                DROP TABLE IF EXISTS geotable;
                CREATE TABLE geotable (id int,  type varchar,temperature int, baby_jeje_weight double, orbisgis boolean);
                INSERT INTO geotable VALUES (1,  'grass', -12, 4.780, false), (2,  'corn', 22, null, null);
        """)
        DataFrame df = DataFrame.of(h2GIS.getTable("geotable").filter("where type = 'corn'").getTable())
        assertNotNull df
        assertEquals(2, df.get(0, 0))
        assertEquals("corn", df.get(0, 1))
        assertEquals(22, df.get(0, 2))
        assertNull(df.get(0, 3))
        assertNull(df.get(0, 4))
    }


    @Test
    void testFactoryDF() {
        def h2GIS = RANDOM_DS();
        h2GIS.execute("""
                DROP TABLE IF EXISTS geotable;
                CREATE TABLE geotable (id int,  type varchar, orbisgis boolean);
                INSERT INTO geotable VALUES (1,  'grass', false), (2,  'corn', null), (3,  'corn', null)
                , (4,  'grass', null), (5,  'corn', null), (6,  'grass', null), (7,  'forest', null);
        """)
        DataFrame df = DataFrame.of(h2GIS.getTable("geotable"))
        assertNotNull df
        StructField field = df.schema().field(1);
        assertFalse(field.measure instanceof  NominalScale)
        def dfFactorized = df.factorize("TYPE")
        field = dfFactorized.schema().field(1);
        assertTrue(field.measure instanceof  NominalScale)
        assertEquals ("nominal[corn, forest, grass]", field.measure.toString())
    }

    @Test
    void testCreateDFFromArray() {
        def data = [
                [100, 5, 20],
                [50, 2.5, 10],
                [110, 6, 22]
        ]

        DataFrame df = DataFrame.of(data, "name", "age", "size")
        assertNotNull df
        assertEquals(100, df.get(0, 0))
        assertEquals(5, df.get(0, 1))
        assertEquals(20, df.get(0, 2))
        assertEquals(50, df.get(1, 0))
        assertEquals(2.5, df.get(1, 1))
        assertEquals(10, df.get(1, 2))

        data = [
                ["A", "B", "C", "D"],
                ["A1", "B1", "C1", "D1"],
                ["A2", "B2", "C2", "D2"],
        ]
        df = DataFrame.of(data, "name", "age", "size", "task")
        assertNotNull df
        assertEquals("A", df.get(0, 0))
        assertEquals("B", df.get(0, 1))
        assertEquals("C", df.get(0, 2))
        assertEquals("D", df.get(0, 3))
        assertEquals("A1", df.get(1, 0))
        assertEquals("B1", df.get(1, 1))
        assertEquals("C1", df.get(1, 2))

        data = [
                ["A", 5, true, "D"],
                ["A1", 6, false, "D1"],
                ["A2", 7, true, "D2"],
        ]
        df = DataFrame.of(data, "name", "age", "size", "task")
        assertNotNull df
        assertEquals("A", df.get(0, 0))
        assertEquals(5, df.get(0, 1))
        assertEquals(true, df.get(0, 2))
        assertEquals("D", df.get(0, 3))

        data = [
                ["A", 5, true, "D"],
                ["A1", "OrbisGIS", false, "D1"],
                ["A2", 7, true, "D2"],
        ]

        df = DataFrame.of(data, "name", "age", "size", "task")
        assertNotNull df
        assertEquals("A", df.get(0, 0))
        assertEquals(5, df.get(0, 1))
        assertEquals(true, df.get(0, 2))
        assertEquals("D", df.get(0, 3))
        assertEquals("A1", df.get(1, 0))
        assertEquals("OrbisGIS", df.get(1, 1))
        assertEquals(false, df.get(1, 2))
        assertEquals("D1", df.get(1, 3))

        data = [
                [100, 20],
                [50, 2.5, 10],
                [110, 6, 22]
        ]
        assertThrows(IllegalArgumentException.class,() ->  DataFrame.of(data, "name", "age", "size"));

        data = [
                [100, 12, 20],
                [50, 2.5, 10],
                [110, 6, 22]
        ]
        assertThrows(IllegalArgumentException.class,() ->  DataFrame.of(data, "age", "size"));
    }


    @Test
    void testRandomForestNominalValuesInResult() {
        // Dataset used
        def data = [
                [105,  'grass', 12, 20], [107,  'corn',12, 30], [106,  'corn',18, 20]
                , [105,  'grass',12, 30], [105,  'corn',16, 20], [106,  'grass',12, 20], [107,  'forest',12, 20],
                [106,  'grass',12, 20],[106,  'grass',16, 20],[106,  'grass',16, 20],[107,  'corn',16, 20],
                [107,  'corn',2, 20], [107,  'corn',16, 50],[107,  'corn',18, 40]
        ]
        DataFrame df = DataFrame.of(data, "LCZ", "TYPE","TEMPERATURE", "WIND");

        // Now we need to factorize the columns "LCZ" and "TYPE" in order to use the random forest
        DataFrame dfFactorized = df.factorize("TYPE");

        // Then we define the characteristics of the randomForest:
        Formula formula = Formula.lhs("LCZ");
        SplitRule splitRule = SplitRule.valueOf("GINI");
        int ntrees = 2;
        int mtry = 2;
        int maxDepth = 2;
        int maxNodes = 5;
        int nodeSize = 3;
        double subsample = 1.0;

        // At this point we have the "LCZ" column as predicted variable, defined as nominal. No problem here since we still have the correspondence between our "LCZ" values (105, 106, 107) and 0, 1, 2.
        // Then we create the randomForest
        RandomForest model = RandomForest.fit(formula, dfFactorized, ntrees, mtry, splitRule, maxDepth, maxNodes, nodeSize, subsample);

        // Finally, when we apply the random forest
        int[] prediction = Validation.test(model, dfFactorized);
        assertTrue(IntStream.of(prediction).filter(it -> it >100).toArray().length>0);
    }
}
