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
package org.orbisgis.orbisdata.processmanager.process;

import groovy.lang.Closure;
import groovy.lang.GroovyShell;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.orbisgis.orbisdata.processmanager.api.IProcess;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class dedicated to {@link ProcessFactory} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public class ProcessManagerTest {

    public static Closure cl;

    @BeforeAll
    public static void beforeAll(){
        String string = "({\n" +
                "            title \"simple process\"\n" +
                "            description \"description\"\n" +
                "            keywords \"key1\", \"key2\"\n" +
                "            inputs inputA: String, inputB: String\n" +
                "            outputs outputA: String\n" +
                "            version \"version\"\n" +
                "            run { inputA, inputB -> [outputA: inputA + inputB] }\n" +
                "        })";
        cl = (Closure)new GroovyShell().evaluate(string);
    }

    /**
     * Test the {@link ProcessManager#create()} and {@link ProcessManager#create(Closure)} methods.
     */
    @Test
    void testCreate(){
        ProcessManager pm = ProcessManager.getProcessManager();
        assertNotNull(pm.create());

        String string = "({\n" +
                "            title \"simple process\"\n" +
                "            description \"description\"\n" +
                "            keywords \"key1\", \"key2\"\n" +
                "            inputs inputA: String, inputB: String\n" +
                "            outputs outputA: String\n" +
                "            version \"version\"\n" +
                "            run { inputA, inputB -> [outputA: inputA + inputB] }\n" +
                "        })";
        Closure cl = (Closure)new GroovyShell().evaluate(string);
        IProcess process = pm.create(cl);

        assertNotNull(process);
        assertEquals("simple process", process.getTitle());
        assertEquals("description", process.getDescription());
        assertEquals("version", process.getVersion());
        assertArrayEquals(new String[]{"key1", "key2"}, process.getKeywords());
        assertEquals(2, process.getInputs().size());
        assertEquals(1, process.getOutputs().size());
    }

    /**
     * Test the {@link ProcessManager#factoryIds()},
     * {@link ProcessManager#factory(String)}, {@link ProcessManager#factory()},
     * {@link ProcessManager#createFactory(String)}, {@link ProcessManager#createFactory()} methods.
     */
    @Test
    void testFactories(){
        ProcessManager pm = ProcessManager.getProcessManager();
        assertNotNull(ProcessManager.createFactory());
        assertNotNull(ProcessManager.createFactory("Mayor_DeFacto_Ry"));

        assertNotNull(pm.factory());
        assertNotNull(pm.factory("orbisgis"));
        assertNotNull(pm.factory("Mayor_DeFacto_Ry"));

        assertTrue(pm.factoryIds().contains("orbisgis"));
        assertTrue(pm.factoryIds().contains("Mayor_DeFacto_Ry"));

    }

    /**
     * Test the {@link ProcessManager#process(String)} and {@link ProcessManager#process(String, String)} methods.
     */
    @Test
    void testProcess(){
        ProcessManager pm = ProcessManager.getProcessManager();
        assertNotNull(ProcessManager.createFactory("Mayor_DeFacto_Ry"));
        String id1 = pm.factory().create(cl).getIdentifier();
        String id2 = pm.factory("Mayor_DeFacto_Ry").create(cl).getIdentifier();

        assertNull(pm.process(id2));
        assertNotNull(pm.process(id1));

        assertNull(pm.process(id1, "Mayor_DeFacto_Ry"));
        assertNotNull(pm.process(id2, "Mayor_DeFacto_Ry"));
    }
}
