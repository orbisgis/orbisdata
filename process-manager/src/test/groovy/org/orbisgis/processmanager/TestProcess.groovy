/*
 * Bundle ProcessManager is part of the OrbisGIS platform
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
 * ProcessManager is distributed under GPL 3 license.
 *
 * Copyright (C) 2018 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * ProcessManager is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * ProcessManager is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * ProcessManager. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.processmanager

import org.junit.jupiter.api.Test
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.io.WKTReader
import org.orbisgis.datamanager.h2gis.H2GIS
import org.orbisgis.datamanagerapi.dataset.ITable
import org.orbisgis.processmanagerapi.IProcessManager

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue

class TestProcess {

    private static final IProcessManager processManager = ProcessManager.getProcessManager()


    @Test
    void testSimpleProcess(){
        def process = processManager.factory("test").create(
                "simple process",
                [inputA : String, inputB : String],
                [outputA : String],
                { inputA, inputB -> [outputA : inputA+inputB] }
        )
        process.execute([inputA : "tata", inputB : "toto"])
        assertEquals "tatatoto", process.getResults().outputA
    }

    @Test
    void testSimpleProcess2() {
        def p = processManager.factory("test").create(
                "OrbisGIS",
                [inputA: String],
                [outputA: String],
                { inputA -> [outputA: inputA.replace("OrbisGIS", "Bretagne")] }
        )
        p.execute([inputA: 'OrbisGIS is nice'])
        assertTrue(p.results.outputA.equals("Bretagne is nice"))
    }

    @Test
    void testSimpleProcess3(){
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis, super;
                CREATE TABLE h2gis (id int, the_geom point);
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)

        def p = processManager.factory("test").create(
                "With database",
                [inputA: ITable],
                [outputA: String],
                { inputA -> [outputA: inputA.columnNames] }
        )
        p.execute([inputA : h2GIS.getSpatialTable("h2gis")])
        assertTrue(p.results.outputA.equals(["ID", "THE_GEOM"]))
    }

    @Test
    void testSimpleProcess4(){
        def p = processManager.factory("test").create(
                "Create a buffer around a geometry",
                [inputA: Geometry, distance: double],
                [outputA: Geometry],
                { inputA, distance ->
                    [outputA: inputA.buffer(distance)]
                }
        )
        p.execute([inputA : new WKTReader().read("POINT(1 1)"), distance : 10] )
        assertTrue new WKTReader().read("POLYGON ((11 1, 10.807852804032304 " +
                "-0.9509032201612824, 10.238795325112868 -2.826834323650898, 9.314696123025453 -4.555702330196022, " +
                "8.071067811865476 -6.071067811865475, 6.555702330196023 -7.314696123025453, 4.826834323650898 " +
                "-8.238795325112868, 2.9509032201612833 -8.807852804032304, 1.0000000000000007 -9, -0.9509032201612819 " +
                "-8.807852804032304, -2.826834323650897 -8.238795325112868, -4.55570233019602 -7.314696123025454, " +
                "-6.071067811865475 -6.0710678118654755, -7.314696123025453 -4.555702330196022, -8.238795325112868 " +
                "-2.8268343236508944, -8.807852804032306 -0.9509032201612773, -9 1.0000000000000075, -8.807852804032303 " +
                "2.950903220161292, -8.238795325112862 4.826834323650909, -7.3146961230254455 6.555702330196034, " +
                "-6.071067811865463 8.071067811865486, -4.555702330196008 9.314696123025463, -2.826834323650879 " +
                "10.238795325112875, -0.9509032201612606 10.807852804032308, 1.0000000000000249 11, 2.950903220161309 " +
                "10.807852804032299, 4.826834323650925 10.238795325112857, 6.555702330196048 9.314696123025435, " +
                "8.071067811865499 8.07106781186545, 9.314696123025472 6.555702330195993, 10.238795325112882 " +
                "4.826834323650862, 10.807852804032311 2.9509032201612437, 11 1))").equalsExact((Geometry)p.results.outputA, 1e-6)
    }

    @Test
    void testSimpleProcess5(){
        def process = processManager.factory("test").create(
                "Array",
                [inputA : String[]],
                [outputA : String],
                { inputA -> [outputA : inputA[1]] }
        )
        process.execute([inputA :["A", "B", "C"]])
        assertEquals "B", process.getResults().outputA
    }

    /**
     *  --> -----      ----
     *     |  pA | -> | pB |-->
     *  --> -----      ----
     */
    @Test
    void testMapping(){
        def pA = processManager.factory("map1").create("pA", [inA1:String, inA2:String], [outA1:String], {inA1, inA2 ->[outA1:inA1+inA2]})
        def pB = processManager.factory("map1").create("pB", [inB1:String], [outB1:String], {inB1 ->[outB1:inB1+inB1]})

        def mapper = new ProcessMapper()
        mapper.link(outA1:pA, inB1:pB)
        assertTrue mapper.execute([inA1: "t", inA2: "a"])
        assertEquals "tata", mapper.getResults().outB1
    }

    /**
     *   --> ----
     *      | pB | -----> ---- -->
     *  |--> ----        | pC |
     *  |            |--> ---- -->
     *  |------------|
     *               |
     *  --> ----     |
     *     | pA | ---|
     *      ----
     */
    @Test
    void testMapping2(){
        def pA = processManager.factory("map2").create("pA", [inA1:String], [outA1:String], {inA1 ->[outA1:inA1.toUpperCase()]})
        def pB = processManager.factory("map2").create("pB", [inB1:String, inB2:String], [outB1:String], {inB1, inB2 ->[outB1:inB2+inB1]})
        def pC = processManager.factory("map2").create("pC", [inC1:String, inC2:String], [outC1:String, outC2:String],
                {inC1, inC2 ->[outC1:inC1+inC2, outC2:inC2+inC1]})

        def mapper = new ProcessMapper()
        mapper.link(outA1:pA,inB1:pB)
        mapper.link(outB1:pB,inC2:pC)
        mapper.link(outA1:pA,inC1:pC)

        assertTrue mapper.execute([inA1: "a", inB2: "b"])
        assertEquals "AbA", mapper.getResults().outC1
        assertEquals "bAA", mapper.getResults().outC2
    }

    /**
     *          --> ----
     *             | pC |
     * --> ---- --> ---- --> ---- -->
     *    | pA |            | pD |
     * --> ---- --> ---- --> ---- -->
     *             | pB |
     *          --> ----
     */
    @Test
    void testMapping3(){
        def pA = processManager.factory("map3").create("pA", [inA1:String], [outA1:String], {inA1 ->[outA1:inA1.toUpperCase()]})
        def pB = processManager.factory("map3").create("pB", [inB1:String, inB2:String], [outB1:String], {inB1, inB2 ->[outB1:inB2+inB1]})
        def pC = processManager.factory("map3").create("pC", [inC1:String, inC2:String], [outC1:String],
                {inC1, inC2 ->[outC1:inC1+inC2]})
        def pD = processManager.factory("map3").create("pD", [inD1:String, inD2:String], [outD1:String, outD2:String],
                {inD1, inD2 ->[outD1:inD1.toLowerCase(), outD2:inD2+inD1]})

        def mapper = new ProcessMapper()
        mapper.link(outA1: pA,inB1: pB)
        mapper.link(outA1: pA,inC1: pC)
        mapper.link(outB1: pB,inD1: pD)
        mapper.link(outC1: pC,inD2: pD)

        assertTrue mapper.execute([inA1: "a", inB2: "b", inC2: "c"])
        assertEquals "ba", mapper.getResults().outD1
        assertEquals "AcbA", mapper.getResults().outD2
    }

    /**
     *  --> -----  |--> ----  |--> ----
     *     |  pA |-|   | pA |-|   | pA |-->
     *  --> -----  |--> ----  |--> ----
     */
    @Test
    void testMapping4(){
        def pA1 = processManager.factory("map1").create("pA", [inA1:String, inA2:String], [outA1:String], {inA1, inA2 ->[outA1:inA1+inA2]})
        def pA2 = processManager.factory("map1").create("pA", [inA1:String, inA2:String], [outA1:String], {inA1, inA2 ->[outA1:inA1+inA2]})
        def pA3 = processManager.factory("map1").create("pA", [inA1:String, inA2:String], [outA1:String], {inA1, inA2 ->[outA1:inA1+inA2]})

        def mapper = new ProcessMapper()
        mapper.link(outA1:pA1, inA1:pA2)
        mapper.link(outA1:pA1, inA2:pA2)
        mapper.link(outA1:pA2, inA1:pA3)
        mapper.link(outA1:pA2, inA2:pA3)
        assertTrue mapper.execute([inA1: "t", inA2: "a"])
        assertEquals "tatatata", mapper.getResults().outA1
    }
}

