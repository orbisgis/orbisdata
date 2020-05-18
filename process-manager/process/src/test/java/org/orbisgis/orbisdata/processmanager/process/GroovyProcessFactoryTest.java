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
import org.orbisgis.commons.annotations.Nullable;
import org.orbisgis.orbisdata.processmanager.api.IProcess;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class dedicated to {@link GroovyProcessFactory} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC 2019- 2020)
 */
public class GroovyProcessFactoryTest {

    /**
     * Test the {@link ProcessFactory#ProcessFactory(boolean, boolean)}, {@link ProcessFactory#ProcessFactory()},
     * {@link ProcessFactory#isDefault()} and {@link ProcessFactory#isLocked()} methods.
     */
    @Test
    void testAttributes() {
        GroovyProcessFactory pf = new DummyFactory();
        assertFalse(pf.isDefault());
        assertFalse(pf.isLocked());
    }

    /**
     * Test the {@link ProcessFactory#registerProcess(IProcess)} and {@link ProcessFactory#getProcess(String)} methods.
     */
    @Test
    void testRegister() {
        IProcess process = new Process(null, null, null, null, null, null, null);

        DummyFactory pf = new DummyFactory();
        pf.registerProcess(process);
        assertTrue(pf.getProcess(process.getIdentifier()).isPresent());
    }

    /**
     * Test the {@link GroovyProcessFactory#create()} and {@link GroovyProcessFactory#create(Closure)} methods.
     */
    @Test
    void testCreate() {
        GroovyProcessFactory pf1 = new DummyFactory();
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
        Closure<?> cl = (Closure<?>) new GroovyShell().evaluate(string);
        Optional<IProcess> opt = pf1.create(cl);
        assertTrue(opt.isPresent());

        IProcess process = opt.get();
        assertEquals("simple process", process.getTitle());
        assertEquals("description", process.getDescription());
        assertEquals("version", process.getVersion());
        assertArrayEquals(new String[]{"key1", "key2"}, process.getKeywords());
        assertEquals(2, process.getInputs().size());
        assertEquals(1, process.getOutputs().size());
    }

    /**
     * Test the {@link ProcessFactory#invokeMethod(String, Object)} method.
     */
    @Test
    void propertyTest() {
        DummyFactory factory = new DummyFactory();
        assertNull(factory.invokeMethod(null, null));
        assertEquals(false, factory.invokeMethod("isLocked", null));
        assertNull(factory.invokeMethod("getProcess", null));
        factory.setMetaClass(null);
        assertNull(factory.invokeMethod(null, null));
        assertEquals(false, factory.invokeMethod("isLocked", null));
        assertNull(factory.invokeMethod("getProcess", null));
    }

    private static class DummyFactory extends GroovyProcessFactory {
        @Override
        @Nullable
        public Object run() {
            return null;
        }
    }
}
