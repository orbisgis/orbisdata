/*
 * Bundle Process is part of the OrbisGIS platform
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
 * Process is distributed under LGPL 3 license.
 *
 * Copyright (C) 2018 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * Process is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Process is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Process. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.process;

import groovy.lang.Closure;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.orbisgis.process.api.IProcess;
import org.orbisgis.process.api.IProcessManager;
import org.orbisgis.process.api.IProcessMapper;
import org.orbisgis.process.api.inoutput.IInOutPut;
import org.orbisgis.process.inoutput.InOutPut;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class dedicated to {@link ProcessMapper} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public class ProcessMapperTest {

    private static final IProcessManager processManager = ProcessManager.getProcessManager();

    private static Process pA1;
    private static Process pA2;
    private static Process pB1;
    private static Process pB2;
    private static Process pC;

    @BeforeAll
    public static void init() {
        LinkedHashMap<String, Object> pAInputMap = new LinkedHashMap<>();
        pAInputMap.put("inA1", String.class);
        pAInputMap.put("inA2", String.class);
        LinkedHashMap<String, Object> pAOutputMap = new LinkedHashMap<>();
        pAOutputMap.put("outA1", String.class);
        pA1 = (Process) processManager.factory("map5").create().title("pA").inputs(pAInputMap).outputs(pAOutputMap)
                .run(new Closure<Object>(null) {
                    public int getMaximumNumberOfParameters() {
                        return 2;
                    }

                    @Override
                    public Object call(Object... arguments) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("outA1", arguments[0].toString() + arguments[1].toString());
                        return map;
                    }
                }).getProcess();

        pA2 = (Process) pA1.newInstance();


        LinkedHashMap<String, Object> pBInputMap = new LinkedHashMap<>();
        pBInputMap.put("inB1", String.class);
        pBInputMap.put("inB2", String.class);
        LinkedHashMap<String, Object> pBOutputMap = new LinkedHashMap<>();
        pBOutputMap.put("outB1", String.class);
        pB1 = (Process) processManager.factory("map5").create().title("pB").inputs(pBInputMap).outputs(pBOutputMap)
                .run(new Closure<Object>(null) {
                    public int getMaximumNumberOfParameters() {
                        return 2;
                    }

                    @Override
                    public Object call(Object... arguments) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("outB1", arguments[1].toString() + " or " + arguments[0].toString());
                        return map;
                    }
                }).getProcess();

        pB2 = (Process) pB1.newInstance();


        LinkedHashMap<String, Object> pCInputMap = new LinkedHashMap<>();
        pCInputMap.put("inC1", "D");
        pCInputMap.put("inC2", String.class);
        LinkedHashMap<String, Object> pCOutputMap = new LinkedHashMap<>();
        pCOutputMap.put("outC1", String.class);
        pCOutputMap.put("outC2", String.class);

        pC = (Process) processManager.factory("map2").create().title("pC").inputs(pCInputMap).outputs(pCOutputMap)
                .run(new Closure<Object>(null) {
                    public int getMaximumNumberOfParameters() {
                        return 2;
                    }

                    @Override
                    public Object call(Object... arguments) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("outC1", arguments[0].toString() + arguments[1].toString());
                        map.put("outC2", arguments[1].toString() + arguments[0].toString());
                        return map;
                    }
                }).getProcess();
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
    public void mapping1Test() {
        LinkedHashMap<String, Object> pAInputMap = new LinkedHashMap<>();
        pAInputMap.put("inA1", String.class);
        LinkedHashMap<String, Object> pAOutputMap = new LinkedHashMap<>();
        pAOutputMap.put("outA1", String.class);
        Process pA = (Process) processManager.factory("map2").create().title("pA").inputs(pAInputMap)
                .outputs(pAOutputMap).run(new Closure<Object>(this) {
                    public int getMaximumNumberOfParameters() {
                        return 1;
                    }

                    @Override
                    public Object call(Object... arguments) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("outA1", arguments[0].toString().toUpperCase());
                        return map;
                    }
                }).getProcess();

        IProcessMapper mapper = new ProcessMapper();
        mapper.link((IInOutPut) pA.getProperty("outA1")).to((IInOutPut) pB1.getProperty("inB1"));
        mapper.link((IInOutPut) pB1.getProperty("outB1")).to((IInOutPut) pC.getProperty("inC2"));
        mapper.link((IInOutPut) pA.getProperty("outA1")).to((IInOutPut) pC.getProperty("inC1"));

        Closure<?> cl = (Closure<?>) new GroovyShell().evaluate("({1+1 == 2})");

        mapper.after(pA).with().check(cl);
        mapper.before(pA).with().check(cl);

        LinkedHashMap<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("inA1", "a");
        dataMap.put("inB2", "b");
        assertTrue(mapper.execute(dataMap));
        assertEquals(2, mapper.getResults().size());
        assertEquals("Ab or A", mapper.getResults().get("outC1"));
        assertEquals("b or AA", mapper.getResults().get("outC2"));
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
    public void mapping2Test() {

        IProcessMapper mapper = new ProcessMapper();

        mapper.link((IInOutPut) pA2.getProperty("outA1")).to((IInOutPut) pB1.getProperty("inB1"));
        mapper.link((IInOutPut) pB2.getProperty("inB2")).to((IInOutPut) pA2.getProperty("outA1"));
        mapper.link((IInOutPut) pA1.getProperty("outA1")).to((IInOutPut) pB2.getProperty("inB1"), (IInOutPut) pB1.getProperty("inB2"));

        mapper.link((IInOutPut) pA1.getProperty("outA1")).to("interPA1OutA1");
        mapper.link((IInOutPut) pA2.getProperty("outA1")).to("interPA2OutA1");

        mapper.link((IInOutPut) pA1.getProperty("inA1"), (IInOutPut) pA2.getProperty("inA1")).to("commonInput");
        mapper.link((IInOutPut) pA1.getProperty("inA2")).to("inputD");
        mapper.link((IInOutPut) pA2.getProperty("inA2")).to("inputK");

        mapper.link((IInOutPut) pB1.getProperty("outB1")).to("outD");
        mapper.link((IInOutPut) pB2.getProperty("outB1")).to("outK");

        LinkedHashMap<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("inputD", "D");
        dataMap.put("inputK", "K");
        dataMap.put("commonInput", "common");
        assertTrue(mapper.call(dataMap));

        assertEquals(3, mapper.getInputs().size());
        assertTrue(mapper.getInputs().stream().map(IInOutPut::getName).filter(Optional::isPresent).map(Optional::get).anyMatch(input -> input.equals("inputD")));
        assertTrue(mapper.getInputs().stream().map(IInOutPut::getName).filter(Optional::isPresent).map(Optional::get).anyMatch(input -> input.equals("inputK")));
        assertTrue(mapper.getInputs().stream().map(IInOutPut::getName).filter(Optional::isPresent).map(Optional::get).anyMatch(input -> input.equals("commonInput")));

        assertEquals(4, mapper.getOutputs().size());
        assertTrue(mapper.getOutputs().stream().map(IInOutPut::getName).filter(Optional::isPresent).map(Optional::get).anyMatch(output -> output.equals("interPA2OutA1")));
        assertTrue(mapper.getOutputs().stream().map(IInOutPut::getName).filter(Optional::isPresent).map(Optional::get).anyMatch(output -> output.equals("interPA1OutA1")));
        assertTrue(mapper.getOutputs().stream().map(IInOutPut::getName).filter(Optional::isPresent).map(Optional::get).anyMatch(output -> output.equals("outD")));
        assertTrue(mapper.getOutputs().stream().map(IInOutPut::getName).filter(Optional::isPresent).map(Optional::get).anyMatch(output -> output.equals("outK")));

        assertFalse(mapper.getResults().containsKey("outB1"));
        assertEquals("commonD or commonK", mapper.getResults().get("outD"));
        assertEquals("commonK or commonD", mapper.getResults().get("outK"));
        assertEquals("commonK", mapper.getResults().get("interPA2OutA1"));
        assertEquals("commonD", mapper.getResults().get("interPA1OutA1"));
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
    public void mapping4Test() {

        IProcessMapper mapper = new ProcessMapper();

        mapper.link((IInOutPut) pA1.getProperty("inA1"), (IInOutPut) pA2.getProperty("inA1")).to("commonInput");
        mapper.link((IInOutPut) pA1.getProperty("inA2")).to("inputD");
        mapper.link((IInOutPut) pA2.getProperty("inA2")).to("inputK");

        LinkedHashMap<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("inputD", "D");
        dataMap.put("inputK", "K");
        dataMap.put("commonInput", "common");
        assertTrue(mapper.call(dataMap));
    }


    /**
     *
     */
    @Test
    public void mapping3Test() {
        IProcessMapper mapper = new ProcessMapper();

        mapper.link((IInOutPut) pC.getProperty("outC1")).to("out");

        LinkedHashMap<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("inC1", null);
        dataMap.put("inC2", "K");
        assertTrue(mapper.call(dataMap));

        assertEquals(2, mapper.getInputs().size());
        assertTrue(mapper.getInputs().stream().map(IInOutPut::getName).filter(Optional::isPresent).map(Optional::get).anyMatch(input -> input.equals("inC1")));
        assertTrue(mapper.getInputs().stream().map(IInOutPut::getName).filter(Optional::isPresent).map(Optional::get).anyMatch(input -> input.equals("inC2")));

        assertEquals(2, mapper.getOutputs().size());
        assertTrue(mapper.getOutputs().stream().map(IInOutPut::getName).filter(Optional::isPresent).map(Optional::get).anyMatch(output -> output.equals("out")));
        assertTrue(mapper.getOutputs().stream().map(IInOutPut::getName).filter(Optional::isPresent).map(Optional::get).anyMatch(output -> output.equals("outC2")));

        assertEquals(2, mapper.getResults().size());
        assertFalse(mapper.getResults().containsKey("outC1"));
        assertEquals("DK", mapper.getResults().get("out"));
        assertEquals("KD", mapper.getResults().get("outC2"));
    }

    /**
     * Test a mapper which processes can't be linked.
     */
    @Test
    public void noLinkeableTest() {

        IProcessMapper mapper = new ProcessMapper();

        mapper.link((IInOutPut) pC.getProperty("outC1")).to((IInOutPut) pC.getProperty("inC1"));

        LinkedHashMap<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("inC1", "D");
        dataMap.put("inC2", "K");
        assertFalse(mapper.call(dataMap));
    }

    /**
     * Test the methods {@link ProcessMapper#link(IInOutPut...)},
     * {@link org.orbisgis.process.api.ILinker#to(String)} and
     * {@link org.orbisgis.process.api.ILinker#to(IInOutPut...)} methods in case of bad linking.
     */
    @Test
    public void badMappingTest() {
        IProcessMapper mapper = new ProcessMapper();
        mapper.link((IInOutPut) pA1.getProperty("outA1")).to((IInOutPut) pB2.getProperty("outB1"));
        assertTrue(mapper.getInputs().isEmpty());
        assertTrue(mapper.getOutputs().isEmpty());

        mapper = new ProcessMapper();
        mapper.link((IInOutPut) pA1.getProperty("inA1")).to((IInOutPut) pB2.getProperty("inB1"));
        assertTrue(mapper.getInputs().isEmpty());
        assertTrue(mapper.getOutputs().isEmpty());

        mapper = new ProcessMapper();
        mapper.link((IInOutPut) pA1.getProperty("outA1"), (IInOutPut) pB2.getProperty("inB1"));
        assertTrue(mapper.getInputs().isEmpty());
        assertTrue(mapper.getOutputs().isEmpty());

        mapper = new ProcessMapper();
        mapper.link((IInOutPut) pA1.getProperty("inA1")).to((IInOutPut) pA1.getProperty("outA1"), (IInOutPut) pB2.getProperty("inB1"));
        assertTrue(mapper.getInputs().isEmpty());
        assertTrue(mapper.getOutputs().isEmpty());

        mapper = new ProcessMapper();
        mapper.link((IInOutPut) pA1.getProperty("inA1")).to();
        assertTrue(mapper.getInputs().isEmpty());
        assertTrue(mapper.getOutputs().isEmpty());

        mapper = new ProcessMapper();
        mapper.link().to();
        assertTrue(mapper.getInputs().isEmpty());
        assertTrue(mapper.getOutputs().isEmpty());
    }

    /**
     * Test the methods {@link ProcessMapper#getTitle()}, {@link ProcessMapper#getDescription()},
     * {@link ProcessMapper#getKeywords()}, {@link ProcessMapper#getVersion()} methods.
     */
    @Test
    public void getAttributesTest() {
        IProcessMapper mapper = new ProcessMapper();
        assertTrue(mapper.getTitle().isPresent());
        assertFalse(mapper.getDescription().isPresent());
        assertFalse(mapper.getKeywords().isPresent());
        assertFalse(mapper.getVersion().isPresent());

        mapper = new ProcessMapper("title");
        assertEquals("title", mapper.getTitle().get());
        assertFalse(mapper.getDescription().isPresent());
        assertFalse(mapper.getKeywords().isPresent());
        assertFalse(mapper.getVersion().isPresent());
    }

    /**
     * Test the methods {@link ProcessMapper#after(IProcess)}, {@link ProcessMapper#before(IProcess)}  methods.
     */
    @Test
    public void getCheckerTest() {
        IProcessMapper mapper = new ProcessMapper();
        assertNotNull(mapper.after(null));
        assertNotNull(mapper.before(null));
    }

    /**
     * Test the methods {@link ProcessMapper#newInstance()} method.
     */
    @Test
    void newInstanceTest() {
        IProcessMapper mapper = new ProcessMapper();
        mapper.link((InOutPut) pA1.getProperty("inA1")).to("in");
        mapper.link((InOutPut) pA1.getProperty("outA1")).to("out");
        mapper.execute(null);
        IProcessMapper mapper2 = mapper.newInstance();
        assertNotEquals(mapper.getTitle(), mapper2.getTitle());
        assertEquals(mapper.getDescription(), mapper2.getDescription());
        assertEquals(mapper.getKeywords(), mapper2.getKeywords());
        assertEquals(mapper.getInputs(), mapper2.getInputs());
        assertEquals(mapper.getOutputs(), mapper2.getOutputs());
        assertEquals(mapper.getVersion(), mapper2.getVersion());
        assertNotEquals(mapper.getIdentifier(), mapper2.getIdentifier());
    }

    /**
     * Test the methods {@link ProcessMapper#copy()} method.
     */
    @Test
    void copyTest() {
        IProcessMapper mapper = new ProcessMapper();
        mapper.link((InOutPut) pA1.getProperty("inA1")).to("in");
        mapper.link((InOutPut) pA1.getProperty("outA1")).to("out");
        mapper.execute(null);
        IProcessMapper mapper2 = mapper.copy();
        assertNotEquals(mapper.getTitle(), mapper2.getTitle());
        assertEquals(mapper.getDescription(), mapper2.getDescription());
        assertEquals(mapper.getKeywords(), mapper2.getKeywords());
        assertEquals(mapper.getInputs(), mapper2.getInputs());
        assertEquals(mapper.getOutputs(), mapper2.getOutputs());
        assertEquals(mapper.getVersion(), mapper2.getVersion());
        assertNotEquals(mapper.getIdentifier(), mapper2.getIdentifier());
    }

    /**
     * Test the {@link Process#invokeMethod(String, Object)} method.
     */
    @Test
    void invokeMethodTest() {
        ProcessMapper mapper = new ProcessMapper("title");
        assertEquals("title", mapper.invokeMethod("getTitle", null));

        assertNull(mapper.invokeMethod(null, null));
        assertNotNull(mapper.invokeMethod("getIdentifier", null));
    }

    /**
     * Test the {@link Process#getProperty(String)} method.
     */
    @Test
    void getPropertyTest() {
        ProcessMapper mapper = new ProcessMapper("title");
        assertEquals("title", mapper.getProperty("title"));

        assertNull(mapper.getProperty(null));
        assertNotNull(mapper.getProperty("identifier"));
    }

    /**
     * Test the {@link Process#getMetaClass()} method.
     */
    @Test
    void metaClassTest() {
        ProcessMapper mapper = new ProcessMapper();
        assertEquals(InvokerHelper.getMetaClass(ProcessMapper.class), mapper.getMetaClass());
        mapper.setMetaClass(InvokerHelper.getMetaClass(ProcessMapperTest.class));
        assertEquals(InvokerHelper.getMetaClass(ProcessMapperTest.class), mapper.getMetaClass());
        mapper.setMetaClass(null);
        assertEquals(InvokerHelper.getMetaClass(ProcessMapper.class), mapper.getMetaClass());
        mapper.setMetaClass(InvokerHelper.getMetaClass(ProcessMapper.class));
    }
}