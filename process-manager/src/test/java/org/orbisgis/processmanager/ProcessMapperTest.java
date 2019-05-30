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
package org.orbisgis.processmanager;

import groovy.lang.Closure;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.orbisgis.processmanagerapi.IProcessInOutPut;
import org.orbisgis.processmanagerapi.IProcessManager;
import org.orbisgis.processmanagerapi.IProcessMapper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class dedicated to {@link ProcessMapper} interface.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public class ProcessMapperTest {

    private static Process pA1;
    private static Process pA2;
    private static Process pB1;
    private static Process pB2;

    @BeforeAll
    public static void init(){
        LinkedHashMap<String, Object> pAInputMap = new LinkedHashMap<>();
        pAInputMap.put("inA1", String.class);
        pAInputMap.put("inA2", String.class);
        LinkedHashMap<String, Object> pAOutputMap = new LinkedHashMap<>();
        pAOutputMap.put("outA1", String.class);
        pA1 = (Process)processManager.factory("map5").create("pA", pAInputMap, pAOutputMap, new Closure(null) {
            public int getMaximumNumberOfParameters() {
                return 2;
            }
            @Override
            public Object call(Object... arguments) {
                Map<String, Object> map = new HashMap<>();
                map.put("outA1", arguments[0].toString() + arguments[1].toString());
                return map;
            }
        });

        pA2 = (Process)processManager.factory("map5").create("pA", pAInputMap, pAOutputMap, new Closure(null) {
            public int getMaximumNumberOfParameters() {
                return 2;
            }
            @Override
            public Object call(Object... arguments) {
                Map<String, Object> map = new HashMap<>();
                map.put("outA1", arguments[0].toString() + arguments[1].toString());
                return map;
            }
        });


        LinkedHashMap<String, Object> pBInputMap = new LinkedHashMap<>();
        pBInputMap.put("inB1", String.class);
        pBInputMap.put("inB2", String.class);
        LinkedHashMap<String, Object> pBOutputMap = new LinkedHashMap<>();
        pBOutputMap.put("outB1", String.class);
        pB1 = (Process)processManager.factory("map5").create("pB", pBInputMap, pBOutputMap, new Closure(null) {
            public int getMaximumNumberOfParameters() {
                return 2;
            }
            @Override
            public Object call(Object... arguments) {
                Map<String, Object> map = new HashMap<>();
                map.put("outB1", arguments[1].toString() + " or " + arguments[0].toString());
                return map;
            }
        });

        pB2 = (Process)processManager.factory("map5").create("pB", pBInputMap, pBOutputMap, new Closure(null) {
            public int getMaximumNumberOfParameters() {
                return 2;
            }
            @Override
            public Object call(Object... arguments) {
                Map<String, Object> map = new HashMap<>();
                map.put("outB1", arguments[1].toString() + " or " + arguments[0].toString());
                return map;
            }
        });
    }

    private static final IProcessManager processManager = ProcessManager.getProcessManager();

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
    void testMapping1(){

        LinkedHashMap<String, Object> pAInputMap = new LinkedHashMap<>();
        pAInputMap.put("inA1", String.class);
        LinkedHashMap<String, Object> pAOutputMap = new LinkedHashMap<>();
        pAOutputMap.put("outA1", String.class);
        Process pA = (Process)processManager.factory("map2").create("pA", pAInputMap, pAOutputMap, new Closure(this) {
            public int getMaximumNumberOfParameters() {
                return 1;
            }
            @Override
            public Object call(Object... arguments) {
                Map<String, Object> map = new HashMap<>();
                map.put("outA1", arguments[0].toString().toUpperCase());
                return map;
            }
        });

        LinkedHashMap<String, Object> pCInputMap = new LinkedHashMap<>();
        pCInputMap.put("inC1", String.class);
        pCInputMap.put("inC2", String.class);
        LinkedHashMap<String, Object> pCOutputMap = new LinkedHashMap<>();
        pCOutputMap.put("outC1", String.class);
        pCOutputMap.put("outC2", String.class);
        Process pC = (Process)processManager.factory("map2").create("pC", pCInputMap, pCOutputMap, new Closure(this) {
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
        });

        IProcessMapper mapper = new ProcessMapper();
        mapper.link((IProcessInOutPut)pA.getProperty("outA1")).to((IProcessInOutPut)pB1.getProperty("inB1"));
        mapper.link((IProcessInOutPut)pB1.getProperty("outB1")).to((IProcessInOutPut)pC.getProperty("inC2"));
        mapper.link((IProcessInOutPut)pA.getProperty("outA1")).to((IProcessInOutPut)pC.getProperty("inC1"));

        LinkedHashMap<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("inA1", "a");
        dataMap.put("inB2", "b");
        assertTrue(mapper.execute(dataMap));
        assertEquals("AbA", mapper.getResults().get("outC1"));
        assertEquals("bAA", mapper.getResults().get("outC2"));
    }


    /**
     *  --> -----  |--> ----
     *     |  pA |-|   | pB |--->
     *  --> -----  |--> ----
     *
     *  --> -----  |--> ----
     *     |  pA |-|   | pB |--->
     *  --> -----  |--> ----
     */
    @Test
    public void testMapping2(){

        IProcessMapper mapper = new ProcessMapper();

        mapper.link((IProcessInOutPut)pA1.getProperty("outA1")).to((IProcessInOutPut)pB1.getProperty("inB1"));
        mapper.link((IProcessInOutPut)pA1.getProperty("outA1")).to((IProcessInOutPut)pB1.getProperty("inB2"));
        mapper.link((IProcessInOutPut)pA2.getProperty("outA1")).to((IProcessInOutPut)pB2.getProperty("inB1"), (IProcessInOutPut)pB2.getProperty("inB2"));

        mapper.link((IProcessInOutPut)pA1.getProperty("outA1")).to("interPA1OutA1");
        mapper.link((IProcessInOutPut)pA2.getProperty("outA1")).to("interPA2OutA1");

        mapper.link((IProcessInOutPut)pA1.getProperty("inA1"), (IProcessInOutPut)pA2.getProperty("inA1")).to("commonInput");
        mapper.link((IProcessInOutPut)pA1.getProperty("inA2")).to("inputD");
        mapper.link((IProcessInOutPut)pA2.getProperty("inA2")).to("inputK");

        mapper.link((IProcessInOutPut)pB1.getProperty("outB1")).to("outD");
        mapper.link((IProcessInOutPut)pB2.getProperty("outB1")).to("outK");

        LinkedHashMap<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("inputD", "D");
        dataMap.put("inputK", "K");
        dataMap.put("commonInput", "common");
        assertTrue(mapper.execute(dataMap));

        assertEquals(5, mapper.getInputs().size());
        assertTrue(mapper.getInputs().containsKey("inA1"));
        assertEquals(String.class, mapper.getInputs().get("inA1"));
        assertTrue(mapper.getInputs().containsKey("inA2"));
        assertEquals(String.class, mapper.getInputs().get("inA2"));
        assertTrue(mapper.getInputs().containsKey("inputD"));
        assertEquals(String.class, mapper.getInputs().get("inputD"));
        assertTrue(mapper.getInputs().containsKey("inputK"));
        assertEquals(String.class, mapper.getInputs().get("inputK"));
        assertTrue(mapper.getInputs().containsKey("commonInput"));
        assertEquals(String.class, mapper.getInputs().get("commonInput"));

        assertEquals(5, mapper.getOutputs().size());
        assertTrue(mapper.getOutputs().containsKey("interPA2OutA1"));
        assertEquals(String.class, mapper.getOutputs().get("interPA2OutA1"));
        assertTrue(mapper.getOutputs().containsKey("interPA1OutA1"));
        assertEquals(String.class, mapper.getOutputs().get("interPA1OutA1"));
        assertTrue(mapper.getOutputs().containsKey("outB1"));
        assertEquals(String.class, mapper.getOutputs().get("outB1"));
        assertTrue(mapper.getOutputs().containsKey("outD"));
        assertEquals(String.class, mapper.getOutputs().get("outD"));
        assertTrue(mapper.getOutputs().containsKey("outK"));
        assertEquals(String.class, mapper.getOutputs().get("outK"));

        assertFalse(mapper.getResults().containsKey("outB1"));
        assertEquals("commonD or commonD", mapper.getResults().get("outD"));
        assertEquals("commonK or commonK", mapper.getResults().get("outK"));
        assertEquals("commonK", mapper.getResults().get("interPA2OutA1"));
        assertEquals("commonD", mapper.getResults().get("interPA1OutA1"));
    }
}