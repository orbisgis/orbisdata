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
package org.orbisgis.process.inoutput;

import groovy.lang.MetaClass;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.junit.jupiter.api.Test;
import org.orbisgis.process.api.IProcess;
import org.orbisgis.process.ProcessManager;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class dedicated to {@link Output} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC 2020)
 */
public class OutputTest {

    /**
     * Test the {@link Output#setName(String)} and {@link Output#getName()} method.
     */
    @Test
    void nameTest() {
        Output output = new  Output();
        assertFalse(output.getName().isPresent());
        assertNotNull(output.name("name"));
        assertTrue(output.getName().isPresent());
        assertEquals("name", output.getName().get());
        output.setName(null);
        assertFalse(output.getName().isPresent());
    }

    /**
     * Test the {@link Output#setProcess(IProcess)} and {@link Output#getProcess()} method.
     */
    @Test
    void processTest() {
        IProcess process = ProcessManager.createFactory().create().getProcess();
        Output output = new  Output();
        assertFalse(output.getProcess().isPresent());
        assertNotNull(output.process(process));
        assertTrue(output.getProcess().isPresent());
        assertEquals(process, output.getProcess().get());
        output.setProcess(null);
        assertFalse(output.getProcess().isPresent());
    }

    /**
     * Test the {@link Output#setTitle(String)} and {@link Output#getTitle()} method.
     */
    @Test
    void titleTest() {
        Output output = new  Output();
        assertFalse(output.getTitle().isPresent());
        assertNotNull(output.title("title"));
        assertTrue(output.getTitle().isPresent());
        assertEquals("title", output.getTitle().get());
        output.setTitle(null);
        assertFalse(output.getTitle().isPresent());
    }

    /**
     * Test the {@link Output#setDescription(String)} and {@link Output#getDescription()} method.
     */
    @Test
    void descriptionTest() {
        Output output = new  Output();
        assertFalse(output.getDescription().isPresent());
        assertNotNull(output.description("descr"));
        assertTrue(output.getDescription().isPresent());
        assertEquals("descr", output.getDescription().get());
        output.setDescription(null);
        assertFalse(output.getDescription().isPresent());
    }

    /**
     * Test the {@link Output#setKeywords(String[])} and {@link Output#getKeywords()} method.
     */
    @Test
    void keywordsTest() {
        Output output = new  Output();
        assertFalse(output.getKeywords().isPresent());
        assertNotNull(output.keywords(new String[]{"key1", "key2"}));
        assertTrue(output.getKeywords().isPresent());
        assertArrayEquals(new String[]{"key1", "key2"}, output.getKeywords().get());
        output.setKeywords(null);
        assertFalse(output.getKeywords().isPresent());
    }

    /**
     * Test the {@link Output#setType(Class)} and {@link Output#getType()} method.
     */
    @Test
    void typeTest() {
        Output output = new  Output();
        assertFalse(output.getType().isPresent());
        assertNotNull(output.type(Integer.class));
        assertTrue(output.getType().isPresent());
        assertEquals(Integer.class, output.getType().get());
        output.setType(null);
        assertFalse(output.getType().isPresent());
    }

    /**
     * Test the {@link Output#toString()} method.
     */
    @Test
    void toStringTest() {
        IProcess process = ProcessManager.createFactory().create().getProcess();
        Output output = new  Output();
        assertTrue(output.toString().isEmpty());

        output.setName("name");
        assertEquals("name", output.toString());

        output.setProcess(process);
        assertEquals("name:" + process.getIdentifier(), output.toString());

        output.setName(null);
        assertEquals(":" + process.getIdentifier(), output.toString());
    }

    /**
     * Test the {@link InOutPut#setMetaClass(MetaClass)} and {@link InOutPut#getMetaClass()} methods.
     */
    @Test
    void metaClassTest(){
        Output output = new Output();
        assertEquals(InvokerHelper.getMetaClass(Output.class), output.getMetaClass());
        output.setMetaClass(null);
        assertNotNull(output.getMetaClass());
        output.setMetaClass(InvokerHelper.getMetaClass(this.getClass()));
        assertEquals(InvokerHelper.getMetaClass(this.getClass()), output.getMetaClass());
    }

    /**
     * Test the {@link Output#equals(Object)} method.
     */
    @Test
    void equalsTest() {
        IProcess p1 = ProcessManager.createFactory().create().getProcess();
        IProcess p2 = ProcessManager.createFactory().create().getProcess();
        Output out1 = new Output().name("toto").process(p1);
        Output out2 = new Output().name("toto").process(p1);
        Output out3 = new Output().name("toto").process(p2);
        Output out4 = new Output().name("toto");
        Output out5 = new Output().name("tata").process(p1);
        Output out6 = new Output();

        assertEquals(out1, out2);

        assertNotEquals(out1, out3);
        assertNotEquals(out1, out4);
        assertNotEquals(out1, out5);
        assertNotEquals(out1, out6);
        assertNotEquals(out1, "out3");
        assertNotEquals(out1, null);
    }

    /**
     * Test the {@link Output#copy()} method.
     */
    @Test
    void copyTest() {
        IProcess p1 = ProcessManager.createFactory().create().getProcess();
        IProcess p2 = ProcessManager.createFactory().create().getProcess();
        Output out1 = new Output().name("toto").process(p1);
        Output out2 = new Output().name("toto").process(p1);
        Output out3 = new Output().name("toto").process(p2);
        Output out4 = new Output().name("toto");
        Output out5 = new Output().name("tata").process(p1);
        Output out6 = new Output();

        assertEquals(out1, out1.copy());
        assertNotSame(out1, out1.copy());

        assertEquals(out2, out2.copy());
        assertNotSame(out2, out2.copy());

        assertEquals(out3, out3.copy());
        assertNotSame(out3, out3.copy());

        assertEquals(out4, out4.copy());
        assertNotSame(out4, out4.copy());

        assertEquals(out5, out5.copy());
        assertNotSame(out5, out5.copy());

        assertEquals(out6, out6.copy());
        assertNotSame(out6, out6.copy());
    }
}
