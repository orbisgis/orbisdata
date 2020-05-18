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
import groovy.lang.MetaClass;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.junit.jupiter.api.Test;
import org.orbisgis.orbisdata.processmanager.api.IProcess;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class dedicated to {@link ProcessFactory} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC 2019- 2020)
 */
public class ProcessFactoryTest {

    /**
     * Test the {@link ProcessFactory#ProcessFactory(boolean, boolean)}, {@link ProcessFactory#ProcessFactory()},
     * {@link ProcessFactory#isDefault()} and {@link ProcessFactory#isLocked()} methods.
     */
    @Test
    void testAttributes() {
        ProcessFactory pf = new ProcessFactory();
        assertFalse(pf.isDefault());
        assertFalse(pf.isLocked());

        pf = new ProcessFactory(true, true);
        assertTrue(pf.isDefault());
        assertTrue(pf.isLocked());

        pf = new ProcessFactory(false, true);
        assertTrue(pf.isDefault());
        assertFalse(pf.isLocked());

        pf = new ProcessFactory(true, false);
        assertFalse(pf.isDefault());
        assertTrue(pf.isLocked());
    }

    /**
     * Test the {@link ProcessFactory#registerProcess(IProcess)} and {@link ProcessFactory#getProcess(String)} methods.
     */
    @Test
    void testRegister() {
        IProcess process = new Process(null, null, null, null, null, null, null);

        ProcessFactory pf = new ProcessFactory(false, false);
        pf.registerProcess(process);
        assertTrue(pf.getProcess(process.getIdentifier()).isPresent());
        pf = new ProcessFactory(false, false);
        pf.registerProcess(null);
        assertFalse(pf.getProcess(process.getIdentifier()).isPresent());

        pf = new ProcessFactory(true, true);
        pf.registerProcess(process);
        assertFalse(pf.getProcess(process.getIdentifier()).isPresent());
        pf = new ProcessFactory(true, true);
        pf.registerProcess(null);
        assertFalse(pf.getProcess(process.getIdentifier()).isPresent());
    }

    /**
     * Test the {@link ProcessFactory#create()} and {@link ProcessFactory#create(Closure)} methods.
     */
    @Test
    void testCreate() {
        ProcessFactory pf1 = new ProcessFactory();
        assertNotNull(pf1.create());

        assertFalse(pf1.create(null).isPresent());

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
        assertNotNull(process);
        assertEquals("simple process", process.getTitle());
        assertEquals("description", process.getDescription());
        assertEquals("version", process.getVersion());
        assertArrayEquals(new String[]{"key1", "key2"}, process.getKeywords());
        assertEquals(2, process.getInputs().size());
        assertEquals(1, process.getOutputs().size());
    }

    /**
     * Test the {@link ProcessFactory#setMetaClass(MetaClass)} and {@link ProcessFactory#getMetaClass()} methods.
     */
    @Test
    void metaClassTest(){
        ProcessFactory factory = new ProcessFactory();
        assertEquals(InvokerHelper.getMetaClass(ProcessFactory.class), factory.getMetaClass());
        factory.setMetaClass(null);
        assertEquals(InvokerHelper.getMetaClass(factory.getClass()).getClass(),
                factory.getMetaClass().getClass());
        factory.setMetaClass(InvokerHelper.getMetaClass(this.getClass()));
        assertEquals(InvokerHelper.getMetaClass(this.getClass()), factory.getMetaClass());
    }

    /**
     * Test the {@link ProcessFactory#invokeMethod(String, Object)} method.
     */
    @Test
    void propertyTest() {
        ProcessFactory factory = new ProcessFactory(false, true);
        assertNull(factory.invokeMethod(null, null));
        assertEquals(false, factory.invokeMethod("isLocked", null));
        assertNull(factory.invokeMethod("getProcess", null));
        factory.setMetaClass(null);
        assertNull(factory.invokeMethod(null, null));
        assertEquals(false, factory.invokeMethod("isLocked", null));
        assertNull(factory.invokeMethod("getProcess", null));
    }
}
