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
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyShell;
import org.junit.jupiter.api.Test;
import org.orbisgis.orbisdata.processmanager.api.IProcess;
import org.orbisgis.orbisdata.processmanager.api.IProcessFactory;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class dedicated to {@link ProcessBuilder} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC 2019-2020)
 */
public class ProcessBuilderTest {

    private static final Object delegate = "";
    private static final IProcessFactory factory = new ProcessFactory();

    /**
     * Test the {@link ProcessBuilder#ProcessBuilder(IProcessFactory, Object)} method.
     */
    @Test
    void constructorTest() {
        assertDoesNotThrow(() -> new ProcessBuilder(factory, delegate));
        assertThrows(IllegalArgumentException.class, () -> new ProcessBuilder(null, delegate));
        assertThrows(IllegalArgumentException.class, () -> new ProcessBuilder(null, null));
        assertThrows(IllegalArgumentException.class, () -> new ProcessBuilder(factory, null));
    }

    /**
     * Test the {@link ProcessBuilder#title(String)} method.
     */
    @Test
    void titleTest() {
        ProcessBuilder processBuilder = new ProcessBuilder(factory, delegate);
        assertEquals(processBuilder, processBuilder.title("title"));

        processBuilder = new ProcessBuilder(factory, delegate);
        assertEquals(processBuilder, processBuilder.title(null));
    }

    /**
     * Test the {@link ProcessBuilder#id(String)} method.
     */
    @Test
    void idTest() {
        ProcessBuilder processBuilder = new ProcessBuilder(factory, delegate);
        assertEquals(processBuilder, processBuilder.id("id"));

        processBuilder = new ProcessBuilder(factory, delegate);
        assertEquals(processBuilder, processBuilder.title(null));
    }

    /**
     * Test the {@link ProcessBuilder#description(String)} method.
     */
    @Test
    void descriptionTest() {
        ProcessBuilder processBuilder = new ProcessBuilder(factory, delegate);
        assertEquals(processBuilder, processBuilder.description("description"));

        processBuilder = new ProcessBuilder(factory, delegate);
        assertEquals(processBuilder, processBuilder.description(null));
    }

    /**
     * Test the {@link ProcessBuilder#keywords(String[])} method.
     */
    @Test
    void keywordsTest() {
        ProcessBuilder processBuilder = new ProcessBuilder(factory, delegate);
        assertEquals(processBuilder, processBuilder.keywords(new String[]{"key1", "key2"}));

        processBuilder = new ProcessBuilder(factory, delegate);
        assertEquals(processBuilder, processBuilder.keywords(null));
    }

    /**
     * Test the {@link ProcessBuilder#inputs(LinkedHashMap)} method.
     */
    @Test
    void inputsTest() {
        LinkedHashMap<String, Object> inputs = new LinkedHashMap<>();
        inputs.put("in1", String.class);
        inputs.put("in2", String.class);
        inputs.put("in3", String.class);

        ProcessBuilder processBuilder = new ProcessBuilder(factory, delegate);
        assertEquals(processBuilder, processBuilder.inputs(inputs));

        processBuilder = new ProcessBuilder(factory, delegate);
        assertEquals(processBuilder, processBuilder.inputs(null));
    }

    /**
     * Test the {@link ProcessBuilder#outputs(LinkedHashMap)} method.
     */
    @Test
    void outputsTest() {
        LinkedHashMap<String, Object> outputs = new LinkedHashMap<>();
        outputs.put("out1", String.class);
        outputs.put("out2", String.class);

        ProcessBuilder processBuilder = new ProcessBuilder(factory, delegate);
        assertEquals(processBuilder, processBuilder.outputs(outputs));

        processBuilder = new ProcessBuilder(factory, delegate);
        assertEquals(processBuilder, processBuilder.outputs(null));
    }

    /**
     * Test the {@link ProcessBuilder#version(String)} method.
     */
    @Test
    void versionTest() {
        ProcessBuilder processBuilder = new ProcessBuilder(factory, delegate);
        assertEquals(processBuilder, processBuilder.version("version"));
        processBuilder = new ProcessBuilder(factory, delegate);
        assertEquals(processBuilder, processBuilder.version(null));
    }

    /**
     * Test the {@link ProcessBuilder#run(Closure)} method.
     */
    @Test
    void runTest() {
        ProcessBuilder processBuilder = new ProcessBuilder(factory, delegate);
        assertEquals(processBuilder, processBuilder.run(null));

        String str = "{in1, in2, in3 -> return [out1:in1+in2+in3]}";
        Closure<?> cl = (Closure<?>) new GroovyShell().evaluate(new GroovyCodeSource(str, "script", ""));
        processBuilder = new ProcessBuilder(factory, delegate);
        assertEquals(processBuilder, processBuilder.run(cl));
    }

    /**
     * Teste the whole building chain.
     */
    @Test
    void testProcessBuilder() {
        LinkedHashMap<String, Object> inputs = new LinkedHashMap<>();
        inputs.put("in1", String.class);
        inputs.put("in2", String.class);
        inputs.put("in3", String.class);

        LinkedHashMap<String, Object> outputs = new LinkedHashMap<>();
        outputs.put("out1", String.class);
        outputs.put("out2", String.class);

        String str = "{in1, in2, in3 -> return [out1:in1+in2+in3]}";
        Closure<?> cl = (Closure<?>) new GroovyShell().evaluate(new GroovyCodeSource(str, "script", ""));

        IProcess process = new ProcessBuilder(factory, delegate)
                .title("title")
                .id("id")
                .description("description")
                .keywords(new String[]{"key1", "key2"})
                .inputs(inputs)
                .outputs(outputs)
                .run(cl)
                .version("version")
                .getProcess();

        assertTrue(process.getTitle().isPresent());
        assertEquals("title", process.getTitle().get());;
        assertEquals("id", process.getIdentifier());
        assertTrue(process.getDescription().isPresent());
        assertEquals("description", process.getDescription().get());
        assertTrue(process.getVersion().isPresent());
        assertEquals("version", process.getVersion().get());
        assertTrue(process.getKeywords().isPresent());
        assertArrayEquals(new String[]{"key1", "key2"}, process.getKeywords().get());
        assertEquals(3, process.getInputs().size());
        assertEquals(2, process.getOutputs().size());

        process = new ProcessBuilder(factory, delegate)
                .title("title")
                .description("description")
                .keywords(new String[]{"key1", "key2"})
                .inputs(inputs)
                .outputs(outputs)
                .run(cl)
                .version("version")
                .getProcess();

        assertTrue(process.getTitle().isPresent());
        assertEquals("title", process.getTitle().get());;
        assertNotNull(process.getIdentifier());
        assertTrue(process.getDescription().isPresent());
        assertEquals("description", process.getDescription().get());
        assertTrue(process.getVersion().isPresent());
        assertEquals("version", process.getVersion().get());
        assertTrue(process.getKeywords().isPresent());
        assertArrayEquals(new String[]{"key1", "key2"}, process.getKeywords().get());
        assertEquals(3, process.getInputs().size());
        assertEquals(2, process.getOutputs().size());

        process = new ProcessBuilder(factory, delegate)
                .title(null)
                .id(null)
                .description(null)
                .keywords(null)
                .inputs(null)
                .outputs(null)
                .run(null)
                .version(null)
                .getProcess();

        assertFalse(process.getTitle().isPresent());
        assertFalse(process.getDescription().isPresent());
        assertFalse(process.getVersion().isPresent());
        assertFalse(process.getKeywords().isPresent());
        assertTrue(process.getInputs().isEmpty());
        assertTrue(process.getOutputs().isEmpty());
    }
}
