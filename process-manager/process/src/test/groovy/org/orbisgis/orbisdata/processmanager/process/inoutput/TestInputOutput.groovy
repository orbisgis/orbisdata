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
package org.orbisgis.orbisdata.processmanager.process.inoutput

import org.junit.jupiter.api.Test
import org.orbisgis.orbisdata.processmanager.process.ProcessManager

import static org.junit.jupiter.api.Assertions.*

/**
 * Test class dedicated to {@link Input} and {@link Output} classes.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
class TestInputOutput {

    def Output = new Output(null, null)
    def Input = new Input(null, null)

    @Test
    void testInput(){
        Input input1 = Input()
        Input input2 = Input().optional("dflt")
                .description("desc")
                .keywords("key1", "key2")
                .title("title")
                .type(String.class)
        Input input3 = Input().mandatory()

        assertTrue(input1.mandatory)
        assertFalse(input1.optional)
        assertNull(input1.defaultValue)
        assertNull(input1.description)
        assertNull(input1.keywords)
        assertNull(input1.name)
        assertNull(input1.process)
        assertNull(input1.title)
        assertNull(input1.type)

        assertFalse(input2.mandatory)
        assertTrue(input2.optional)
        assertEquals("dflt", input2.defaultValue)
        assertEquals("desc", input2.description)
        assertArrayEquals((String[])['key1', 'key2'], input2.keywords)
        assertNull(input2.name)
        assertNull(input2.process)
        assertEquals("title", input2.title)
        assertEquals(String.class, input2.type)

        assertTrue(input3.mandatory)
        assertFalse(input3.optional)
        assertNull(input3.defaultValue)
        assertNull(input3.description)
        assertNull(input3.keywords)
        assertNull(input3.name)
        assertNull(input3.process)
        assertNull(input3.title)
        assertNull(input3.type)
    }

    @Test
    void testOutput(){
        def output1 = Output()
        def output2 = Output()
                .keywords('key1', 'key2')
                .description("desc")
                .title("title")
                .type(String.class)

        assertNull(output1.description)
        assertNull(output1.keywords)
        assertNull(output1.name)
        assertNull(output1.process)
        assertNull(output1.title)
        assertNull(output1.type)

        assertEquals("desc", output2.description)
        assertArrayEquals((String[])['key1', 'key2'], output2.keywords)
        assertNull(output2.name)
        assertNull(output2.process)
        assertEquals("title", output2.title)
        assertEquals(String, output2.type)
    }

    @Test
    void testToString(){
        def process = ProcessManager.processManager.create().process
        assertEquals("name:"+process.identifier, new Output(process, "name").toString())
    }
}
