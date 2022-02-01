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
 * WARRANTY without even the implied warranty of MERCHANTABILITY or FITNESS FOR
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

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.orbisgis.process.api.IProcess
import org.orbisgis.process.api.IProcessManager
import org.orbisgis.process.api.IProcessMapper
import org.orbisgis.process.api.inoutput.IInOutPut

/**
 * Test class dedicated to {@link ProcessMapper} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
class TestProcessMapper {

    private static final IProcessManager processManager = ProcessManager.getProcessManager()

    private static Process pA1
    private static Process pA2
    private static Process pB1
    private static Process pB2
    private static Process pC

    @BeforeAll
    static void init() {
        def pAInputMap = [inA1: String, inA2: String]
        def pAOutputMap = [outA1: String]
        pA1 = processManager.factory("map5").create().title("pA").inputs(pAInputMap).outputs(pAOutputMap)
                .run({ in1, in2 -> ["outA1": "$in1$in2"] }).process

        pA2 = pA1.newInstance()

        def pBInputMap = [inB1: String, inB2: String]
        def pBOutputMap = [outB1: String]
        pB1 = processManager.factory("map5").create().title("pB").inputs(pBInputMap).outputs(pBOutputMap)
                .run({ in1, in2 -> [outB1: "$in2 or $in1"] }).process

        pB2 = pB1.newInstance()


        def pCInputMap = [inC1: "D", inC2: String]
        def pCOutputMap = [outC1: String, outC2: String]

        pC = processManager.factory("map2").create().title("pC").inputs(pCInputMap).outputs(pCOutputMap)
                .run{in1, in2 -> [outC1: "$in1$in2", outC2:"$in2$in1"]}.process
    }

    /**
     *  --> ----
     *     | pB | -----> ---- -->
     * |--> ----        | pC |
     * |            |--> ---- -->
     * |------------|
     *              |
     * --> ----     |
     *    | pA | ---|
     *     ----
     */
    @Test
    void mapping1Test() {
        def pAInputMap = [inA1: String]
        def pAOutputMap = [outA1: String]
        def pA = processManager.factory("map2").create().title("pA").inputs(pAInputMap)
                .outputs(pAOutputMap).run{in1 -> [outA1: "${in1.toUpperCase()}"]}.process

        def mapper = new ProcessMapper()
        mapper.link(pA.outA1).to(pB1.inB1)
        mapper.link(pB1.outB1).to(pC.inC2)
        mapper.link(pA.outA1).to(pC.inC1)

        mapper.after(pA).with().check{1+1 == 2}
        mapper.before(pA).with().check{1+1 == 2}

        def dataMap = [inA1: "a",inB2: "b"]
        assert mapper(dataMap)
        assert 2 == mapper.results.size()
        assert "Ab or A" == mapper.results.get("outC1")
        assert "b or AA" == mapper.results.get("outC2")
    }


    /**
     * --> -----  |-----> ----
     *    |  pA |-|      | pB |--->
     * --> -----  |-\ /-> ----
     *               X
     * --> -----  |-/ \-> ----
     *    |  pA |-|      | pB |--->
     * --> -----  |-----> ----
     */
    @Test
    void mapping2Test() {

        def mapper = new ProcessMapper()

        mapper.link(pA2.outA1).to(pB1.inB1)
        mapper.link(pB2.inB2).to(pA2.outA1)
        mapper.link(pA1.outA1).to(pB2.inB1, pB1.inB2)

        mapper.link(pA1.outA1).to("interPA1OutA1")
        mapper.link(pA2.outA1).to("interPA2OutA1")

        mapper.link(pA1.inA1, pA2.inA1).to("commonInput")
        mapper.link(pA1.inA2).to("inputD")
        mapper.link(pA2.inA2).to("inputK")

        mapper.link(pB1.outB1).to("outD")
        mapper.link(pB2.outB1).to("outK")

        def dataMap = [inputD: "D", inputK: "K", commonInput: "common"]
        assert mapper(dataMap)

        assert 3 == mapper.inputs.size

        assert 4 == mapper.outputs.size

        assert !mapper.results.containsKey("outB1")
        assert "commonD or commonK" == mapper.results.get("outD")
        assert "commonK or commonD" == mapper.results.get("outK")
        assert "commonK" == mapper.results.get("interPA2OutA1")
        assert "commonD" == mapper.results.get("interPA1OutA1")
    }


    /**
     * --> -----
     * |  pA |-->
     * --> -----
     * <p>
     * --> -----
     * |  pA |-->
     * --> -----
     */
    @Test
    void mapping4Test() {

        def mapper = new ProcessMapper()

        mapper.link(pA1.inA1, pA2.inA1).to("commonInput")
        mapper.link(pA1.inA2).to("inputD")
        mapper.link(pA2.inA2).to("inputK")

        def dataMap = [inputD: "D", inputK: "K", commonInput: "common"]
        assert mapper(dataMap)
    }


    /**
     *
     */
    @Test
    void mapping3Test() {
        def mapper = new ProcessMapper()

        mapper.link(pC.outC1).to("out")

        def dataMap = [inC1: null, inC2: "K"]
        assert mapper(dataMap)

        assert 2 == mapper.inputs.size

        assert 2 == mapper.outputs.size

        assert 2 == mapper.results.size()
        assert !mapper.results.containsKey("outC1")
        assert "DK" == mapper.results.get("out")
        assert "KD" == mapper.results.get("outC2")
    }

    /**
     * Test a mapper which processes can't be linked.
     */
    @Test
    void noLinkeableTest() {

        def mapper = new ProcessMapper()

        mapper.link(pC.outC1).to(pC.inC1)

        def dataMap = [inC1: "D", inC2: "K"]
        assert !mapper(dataMap)
    }

    /**
     * Test the methods {@link ProcessMapper#link(IInOutPut...)},
     * {@link org.orbisgis.process.api.ILinker#to(String)} and
     * {@link org.orbisgis.process.api.ILinker#to(IInOutPut...)} methods in case of bad linking.
     */
    @Test
    void badMappingTest() {
        IProcessMapper mapper = new ProcessMapper()
        mapper.link(pA1.outA1).to(pB2.outB1)
        assert !mapper.inputs
        assert !mapper.outputs

        mapper = new ProcessMapper()
        mapper.link(pA1.inA1).to(pB2.inB1)
        assert !mapper.inputs
        assert !mapper.outputs

        mapper = new ProcessMapper()
        mapper.link(pA1.outA1, pB2.inB1)
        assert !mapper.inputs
        assert !mapper.outputs

        mapper = new ProcessMapper()
        mapper.link(pA1.inA1).to(pA1.outA1, pB2.inB1)
        assert !mapper.inputs
        assert !mapper.outputs

        mapper = new ProcessMapper()
        mapper.link(pA1.inA1).to()
        assert !mapper.inputs
        assert !mapper.outputs

        mapper = new ProcessMapper()
        mapper.link()
        assert !mapper.inputs
        assert !mapper.outputs
    }

    /**
     * Test the methods {@link ProcessMapper#getTitle()}, {@link ProcessMapper#getDescription()},
     * {@link ProcessMapper#getKeywords()}, {@link ProcessMapper#getVersion()} methods.
     */
    @Test
    void getAttributesTest() {
        def mapper = new ProcessMapper()
        assert mapper.title
        assert !mapper.description
        assert !mapper.keywords
        assert !mapper.version

        mapper = new ProcessMapper("title")
        assert "title" == mapper.title
        assert !mapper.description
        assert !mapper.keywords
        assert !mapper.version
    }

    /**
     * Test the methods {@link ProcessMapper#after(IProcess)}, {@link ProcessMapper#before(IProcess)}  methods.
     */
    @Test
    void getCheckerTest() {
        def mapper = new ProcessMapper()
        assert mapper.after(null)
        assert mapper.before(null)
    }

    /**
     * Test the methods {@link ProcessMapper#newInstance()} method.
     */
    @Test
    void newInstanceTest() {
        def mapper = new ProcessMapper()
        mapper.link(pA1.inA1).to("in")
        mapper.link(pA1.outA1).to("out")
        mapper(null)
        def mapper2 = mapper.newInstance()
        assert mapper.getTitle() != mapper2.getTitle()
        assert mapper.getDescription() == mapper2.getDescription()
        assert mapper.getKeywords() == mapper2.getKeywords()
        assert mapper.inputs == mapper2.inputs
        assert mapper.outputs == mapper2.outputs
        assert mapper.getVersion() == mapper2.getVersion()
        assert mapper.getIdentifier() != mapper2.getIdentifier()
    }
}