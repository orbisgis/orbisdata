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
import org.junit.jupiter.api.Test;
import org.orbisgis.orbisdata.processmanager.api.IProcess;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class dedicated to {@link ProcessFactory} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public class ProcessFactoryTest {

    /**
     * Test the {@link ProcessFactory#isDefault()} and {@link ProcessFactory#isLocked()} methods.
     */
    @Test
    void testAttributes(){
        ProcessFactory pf1 = new ProcessFactory();
        assertFalse(pf1.isDefault());
        assertFalse(pf1.isLocked());

        ProcessFactory pf2 = new ProcessFactory(true, true);
        assertTrue(pf2.isDefault());
        assertTrue(pf2.isLocked());
    }

    /**
     * Test the {@link ProcessFactory#registerProcess(IProcess)} method.
     */
    @Test
    void testRegister(){
        IProcess process = new Process(null, null, null, null, null, null, null);

        ProcessFactory pf1 = new ProcessFactory();
        pf1.registerProcess(process);
        assertNotNull(pf1.getProcess(process.getIdentifier()));

        ProcessFactory pf2 = new ProcessFactory(true, true);
        pf2.registerProcess(process);
        assertNull(pf2.getProcess(process.getIdentifier()));
    }

    /**
     * Test the {@link ProcessFactory#create()} and {@link ProcessFactory#create(Closure)} methods.
     */
    @Test
    void testCreate(){
        ProcessFactory pf1 = new ProcessFactory();
        assertNotNull(pf1.create());

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
        IProcess process = pf1.create(cl);

        assertNotNull(process);
        assertEquals("simple process", process.getTitle());
        assertEquals("description", process.getDescription());
        assertEquals("version", process.getVersion());
        assertArrayEquals(new String[]{"key1", "key2"}, process.getKeywords());
        assertEquals(2, process.getInputs().size());
        assertEquals(1, process.getOutputs().size());
    }
}
