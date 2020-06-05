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
package org.orbisgis.orbisdata.processmanager.process

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.orbisgis.orbisdata.processmanager.api.IProcessFactory

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow
import static org.junit.jupiter.api.Assertions.assertThrows

/**
 * Test class dedicated to {@link ProcessBuilder} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC 2019-2020)
 */
class TestProcessBuilder {

    private static final Object delegate = ""
    private static final IProcessFactory factory = new ProcessFactory()

    /**
     * Test the {@link ProcessBuilder#ProcessBuilder(IProcessFactory, Object)} method.
     */
    @Test
    void constructorTest() {
        assertDoesNotThrow((Executable){new ProcessBuilder(factory, delegate)})
        assertThrows(IllegalArgumentException) {new ProcessBuilder(null, delegate)}
        assertThrows(IllegalArgumentException) {new ProcessBuilder(null, null)}
        assertThrows(IllegalArgumentException) {new ProcessBuilder(factory, null)}
    }

    /**
     * Test the {@link ProcessBuilder#title(String)} method.
     */
    @Test
    void titleTest() {
        def processBuilder = new ProcessBuilder(factory, delegate)
        assert processBuilder == processBuilder.title("title")

        processBuilder = new ProcessBuilder(factory, delegate)
        assert processBuilder == processBuilder.title(null)
    }

    /**
     * Test the {@link ProcessBuilder#description(String)} method.
     */
    @Test
    void descriptionTest() {
        def processBuilder = new ProcessBuilder(factory, delegate)
        assert processBuilder == processBuilder.description("description")

        processBuilder = new ProcessBuilder(factory, delegate)
        assert processBuilder == processBuilder.description(null)
    }

    /**
     * Test the {@link ProcessBuilder#keywords(String[])} method.
     */
    @Test
    void keywordsTest() {
        def processBuilder = new ProcessBuilder(factory, delegate)
        assert processBuilder == processBuilder.keywords("key1", "key2")

        processBuilder = new ProcessBuilder(factory, delegate)
        assert processBuilder == processBuilder.keywords(null)
    }

    /**
     * Test the {@link ProcessBuilder#inputs(LinkedHashMap)} method.
     */
    @Test
    void inputsTest() {
        def inputs = [in1: String, in2: String, in3: String]

        def processBuilder = new ProcessBuilder(factory, delegate)
        assert processBuilder == processBuilder.inputs(inputs)

        processBuilder = new ProcessBuilder(factory, delegate)
        assert processBuilder == processBuilder.inputs(null)
    }

    /**
     * Test the {@link ProcessBuilder#outputs(LinkedHashMap)} method.
     */
    @Test
    void outputsTest() {
        def outputs = [out1: String, out2: String]

        def processBuilder = new ProcessBuilder(factory, delegate)
        assert processBuilder == processBuilder.outputs(outputs)

        processBuilder = new ProcessBuilder(factory, delegate)
        assert processBuilder == processBuilder.outputs(null)
    }

    /**
     * Test the {@link ProcessBuilder#version(String)} method.
     */
    @Test
    void versionTest() {
        def processBuilder = new ProcessBuilder(factory, delegate)
        assert processBuilder == processBuilder.version("version")
        processBuilder = new ProcessBuilder(factory, delegate)
        assert processBuilder == processBuilder.version(null)
    }

    /**
     * Test the {@link ProcessBuilder#run(Closure)} method.
     */
    @Test
    void runTest() {
        def processBuilder = new ProcessBuilder(factory, delegate)
        assert processBuilder == processBuilder.run(null)

        def cl = {in1, in2, in3 -> return [out1:in1+in2+in3]}
        processBuilder = new ProcessBuilder(factory, delegate)
        assert processBuilder == processBuilder.run(cl)
    }

    /**
     * Teste the whole building chain.
     */
    @Test
    void testProcessBuilder() {
        def inputs = [in1: String, in2: String, in3: String]
        def outputs = [out1: String, out2: String]
        def cl = {in1, in2, in3 -> return [out1:in1+in2+in3]}

        def builder = {factory, delegate -> new ProcessBuilder(factory, delegate)}

        def process = builder factory, delegate title "title" description "description" version "version" keywords "key1", "key2" inputs inputs outputs outputs run cl process

        assert "title" == process.title
        assert "description" == process.description
        assert "version" == process.version
        assert ["key1", "key2"] == process.keywords
        assert 3 == process.inputs.size
        assert 2 == process.outputs.size

        process = new ProcessBuilder(factory, delegate)
                .title(null)
                .description(null)
                .keywords(null)
                .inputs(null)
                .outputs(null)
                .run(null)
                .version(null)
                .process

        assert !process.title
        assert !process.description
        assert !process.version
        assert !process.keywords
        assert !process.inputs
        assert !process.outputs
    }
}
