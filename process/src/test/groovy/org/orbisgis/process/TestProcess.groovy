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
 * ProcessManager is distributed under LGPL 3 license.
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
package org.orbisgis.process


import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.io.WKTReader
import org.orbisgis.data.api.dataset.ITable
import org.orbisgis.data.jdbc.h2gis.H2GIS
import org.orbisgis.process.api.IProcessManager
import org.orbisgis.process.impl.inoutput.Input
import org.orbisgis.process.impl.inoutput.Output

import static org.junit.jupiter.api.Assertions.*

class TestProcess {

    private static final IProcessManager processManager = ProcessManager.processManager

    void testProcessCreation() {

        assertFalse processManager.create({}).execute()
        assertTrue processManager.create({ run { 1 + 1 } }).execute()

        String[] arr = ["key1", "key2"]

        def process = processManager.create()
                .title("simple process")
                .description("description")
                .keywords("key1", "key2")
                .inputs([inputA: String, inputB: String])
                .outputs([outputA: String])
                .version("version")
                .run({ inputA, inputB -> [outputA: inputA + inputB] })
                .process
        process([inputA: "tata", inputB: "toto"])
        assertEquals "tatatoto", process.results.outputA
        assertEquals "simple process", process.title
        assertEquals "description", process.description
        assertArrayEquals arr, process.keywords
        assertEquals 2, process.inputs.size()
        assertEquals 1, process.outputs.size()
        assertEquals "version", process.version

        process = processManager.create({
            title "simple process"
            description "description"
            keywords "key1", "key2"
            inputs inputA: String, inputB: String
            outputs outputA: String
            version "version"
            run { inputA, inputB -> [outputA: inputA + inputB] }
        })
        process([inputA: "tata", inputB: "toto"])
        assertEquals "tatatoto", process.results.outputA
        assertEquals "simple process", process.title
        assertEquals "description", process.description
        assertArrayEquals arr, process.keywords
        assertEquals 2, process.inputs.size()
        assertEquals 1, process.outputs.size()
        assertEquals "version", process.version

        process = processManager.create({
            title "simple process"
            description "description"
            keywords "key1", "key2"
            inputs inputA: String, inputB: String
            outputs outputA: String
            version "version"
            run { inputA, inputB -> [outputA: inputA + inputB] }
        })

        process([inputA: "tata", inputB: "toto"])
        assertEquals "tatatoto", process.results.outputA
        assertEquals "simple process", process.title
        assertEquals "description", process.description
        assertArrayEquals arr, process.keywords
        assertEquals 2, process.inputs.size()
        assertEquals 1, process.outputs.size()
        assertEquals "version", process.version
    }

    void testNullResult() {
        def process = processManager.create()
                .title("simple process")
                .run({ return })
                .process
        assertTrue process()
        assertTrue process.getResults().keySet().contains("result")
    }

    void testSimpleProcess() {
        def process = processManager.create()
                .title("simple process")
                .inputs([inputA: String, inputB: String])
                .outputs([outputA: String])
                .run({ inputA, inputB -> [outputA: inputA + inputB] })
                .process
        process([inputA: "tata", inputB: "toto"])
        assertEquals "tatatoto", process.getResults().outputA
    }

    void testSimpleProcess2() {
        def p = processManager.create({
            title "OrbisGIS"
            inputs inputA: String
            outputs outputA: String
            run { inputA -> [outputA: inputA.replace("OrbisGIS", "Bretagne")] }
        })

        p([inputA: 'OrbisGIS is nice'])
        assertTrue(p.results.outputA.equals("Bretagne is nice"))
    }

    void testSimpleProcess3() {
        def h2GIS = H2GIS.open([databaseName: './target/loadH2GIS'])
        h2GIS("""
                DROP TABLE IF EXISTS h2gis, super;
                CREATE TABLE h2gis (id int, the_geom geometry(point));
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)

        def p = processManager.create({
            title "With database"
            inputs inputA: ITable
            outputs outputA: String
            run { inputA -> [outputA: inputA.columns] }
        })


        p([inputA: h2GIS.getSpatialTable("h2gis")])
        assertTrue(p.results.outputA.equals(["ID", "THE_GEOM"]))
    }

    void testSimpleProcess4() {

        def p = processManager.create({
            title "Create a buffer around a geometry"
            inputs inputA: Geometry, distance: double
            outputs outputA: Geometry
            run { inputA, distance -> [outputA: inputA.buffer(distance)] }
        })

        p([inputA: new WKTReader().read("POINT(1 1)"), distance: 10])
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
                "4.826834323650862, 10.807852804032311 2.9509032201612437, 11 1))").equalsExact((Geometry) p.results.outputA, 1e-6)
    }

    void testSimpleProcess5() {

        def p = processManager.create({
            title "Array"
            inputs inputA: String[]
            outputs outputA: String
            run { inputA -> [outputA: inputA[1]] }
        })


        p([inputA: ["A", "B", "C"]])
        assertEquals "B", p.getResults().outputA
    }

    void testProcessWithDefaultValue1() {

        def p = processManager.create({
            title "simple process"
            inputs inputA: String, inputB: "toto"
            outputs outputA: String
            run { inputA, inputB -> [outputA: inputA + inputB] }
        })

        assertTrue p([inputA: "tata"])
        assertEquals "tatatoto", p.getResults().outputA
    }

    void testProcessWithDefaultValue2() {
        def process = processManager.factory("test").create({
            title "simple process"
            inputs inputA: "tata", inputB: String
            outputs outputA: String
            run { inputA, inputB -> [outputA: inputA + inputB] }
        })
        assertTrue process([inputB: "toti"])
        assertEquals "tatatoti", process.getResults().outputA
    }

    void testProcessWithDefaultValue3() {
        def process = processManager.factory("test").create({
            title "simple process"
            inputs inputA: String, inputB: "tyty", inputC: 5.23d, inputD: Double
            outputs outputA: String
            run { inputA, inputB, inputC, inputD -> [outputA: inputA + inputB + inputC + inputD] }
        })
        assertTrue process([inputA: "tata", inputB: "toto", inputC: 1.0d, inputD: 2.1d])
        assertEquals "tatatoto1.02.1", process.getResults().outputA
        assertTrue process([inputA: "tata", inputC: 1.0d, inputD: 2.1d])
        assertEquals "tatatyty1.02.1", process.getResults().outputA
        assertTrue process([inputA: "tata", inputB: "toto", inputD: 2.1d])
        assertEquals "tatatoto5.232.1", process.getResults().outputA
        assertTrue process([inputA: "tata", inputD: 2.1d])
        assertEquals "tatatyty5.232.1", process.getResults().outputA
        assertFalse process([inputD: 2.1d])
        assertFalse process([inputA: "tata", inputB: "toto"])
    }

    void testBadTypeInput() {
        String[] arr = ["key1", "key2"]
        def process = processManager.create()
                .title("simple process")
                .description("description")
                .keywords("key1", "key2")
                .inputs([inputA: String[], inputB: "toto"])
                .outputs([outputA: String])
                .version("version")
                .run({ inputA, inputB -> [outputA: inputA] })
                .process
        assertFalse process.execute([inputB: 56D])
        assertTrue process.results.isEmpty()
        //assertFalse process.execute([inputA: arr])
        //assertTrue process.results.isEmpty()
        assertTrue process.execute([inputA: ["key1", "key2"]])
        assertFalse process.results.isEmpty()
    }
}

