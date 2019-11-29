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
package org.orbisgis.orbisdata.processmanager.process.inoutput;

import groovy.lang.MissingMethodException;
import org.junit.jupiter.api.Test;
import org.orbisgis.orbisdata.processmanager.process.ProcessManager;
import org.orbisgis.orbisdata.processmanager.api.IProcess;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class dedicated to {@link Input} and {@link Output} classes.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public class InputOutputTest {

    /**
     * Test the {@link Input} class.
     */
    @Test
    public void testInput(){
        Input input1 = Input.call();
        Input input2 = Input.call().optional("dflt")
                .setDescription("desc")
                .setKeywords(new String[]{"key1", "key2"})
                .setTitle("title")
                .setType(String.class);
        Input input3 = Input.call().mandatory();

        assertTrue(input1.isMandatory());
        assertFalse(input1.isOptional());
        assertNull(input1.getDefaultValue());
        assertNull(input1.getDescription());
        assertNull(input1.getKeywords());
        assertNull(input1.getName());
        assertNull(input1.getProcess());
        assertNull(input1.getTitle());
        assertNull(input1.getType());

        assertFalse(input2.isMandatory());
        assertTrue(input2.isOptional());
        assertEquals("dflt", input2.getDefaultValue());
        assertEquals("desc", input2.getDescription());
        assertArrayEquals(new String[]{"key1", "key2"}, input2.getKeywords());
        assertNull(input2.getName());
        assertNull(input2.getProcess());
        assertEquals("title", input2.getTitle());
        assertEquals(String.class, input2.getType());

        assertTrue(input3.isMandatory());
        assertFalse(input3.isOptional());
        assertNull(input3.getDefaultValue());
        assertNull(input3.getDescription());
        assertNull(input3.getKeywords());
        assertNull(input3.getName());
        assertNull(input3.getProcess());
        assertNull(input3.getTitle());
        assertNull(input3.getType());
    }

    /**
     * Test the {@link Output} class.
     */
    @Test
    public void testOutput(){
        Output output1 = Output.call();
        Output output2 = Output.call()
                .setDescription("desc")
                .setKeywords(new String[]{"key1", "key2"})
                .setTitle("title")
                .setType(String.class);

        assertNull(output1.getDescription());
        assertNull(output1.getKeywords());
        assertNull(output1.getName());
        assertNull(output1.getProcess());
        assertNull(output1.getTitle());
        assertNull(output1.getType());

        assertEquals("desc", output2.getDescription());
        assertArrayEquals(new String[]{"key1", "key2"}, output2.getKeywords());
        assertNull(output2.getName());
        assertNull(output2.getProcess());
        assertEquals("title", output2.getTitle());
        assertEquals(String.class, output2.getType());
    }

    /**
     * Test the {@link InOutPut#toString()} method.
     */
    @Test
    public void testToString(){
        IProcess process = ProcessManager.getProcessManager().create().getProcess();
        assertEquals("name:"+process.getIdentifier(), new Output(process, "name").toString());
    }

    /**
     * Test the {@link Output#methodMissing(String, Object)} and {@link Input#methodMissing(String, Object)} methods.
     */
    @Test
    public void testMissingMethodException(){
        Output output = new Output(null, null);

        assertThrows(MissingMethodException.class, () -> output.methodMissing("title", null));
        assertThrows(MissingMethodException.class, () -> output.methodMissing("title", new Object[]{}));
        assertThrows(MissingMethodException.class, () -> output.methodMissing("title", new Object[]{1}));
        assertThrows(MissingMethodException.class, () -> output.methodMissing("type", new Object[]{1}));
        assertThrows(MissingMethodException.class, () -> output.methodMissing("keywords", new Object[]{1}));
        assertThrows(MissingMethodException.class, () -> output.methodMissing("description", new Object[]{1}));
        assertThrows(MissingMethodException.class, () -> output.methodMissing("toto", new Object[]{1}));
    }
}
