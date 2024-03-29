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
package org.orbisgis.process.inoutput

import org.junit.jupiter.api.Test
import org.orbisgis.process.api.IProcess

import static org.orbisgis.process.ProcessManager.createFactory

/**
 * Test class dedicated to {@link Output} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC 2020)
 */
class TestOutput {

    /**
     * Test the {@link Output#setName(String)} and {@link Output#getName()} method.
     */
    @Test
    void nameTest() {
        def output = new Output()
        assert !output.name
        assert output.name("name")
        assert output.name
        assert "name" == output.name
        output.name = null
        assert !output.name
    }

    /**
     * Test the {@link Output#setProcess(IProcess)} and {@link Output#getProcess()} method.
     */
    @Test
    void processTest() {
        def process = createFactory().create().getProcess()
        def output = new Output()
        assert !output.process
        assert output.process(process)
        assert output.process
        assert process == output.process
        output.process = null
        assert !output.process
    }

    /**
     * Test the {@link Output#setTitle(String)} and {@link Output#getTitle()} method.
     */
    @Test
    void titleTest() {
        def output = new Output()
        assert !output.title
        assert output.title("title")
        assert output.title
        assert "title" == output.title
        output.title = null
        assert !output.title
    }

    /**
     * Test the {@link Output#setDescription(String)} and {@link Output#getDescription()} method.
     */
    @Test
    void descriptionTest() {
        def output = new Output()
        assert !output.description
        assert output.description("descr")
        assert output.description
        assert "descr" == output.description
        output.description = null
        assert !output.description
    }

    /**
     * Test the {@link Output#setKeywords(String[])} and {@link Output#getKeywords()} method.
     */
    @Test
    void keywordsTest() {
        def output = new Output()
        assert !output.keywords
        assert output.keywords(new String[]{"key1", "key2"})
        assert output.keywords
        assert new String[]{"key1", "key2"} == output.keywords
        output.keywords = null
        assert !output.keywords
    }

    /**
     * Test the {@link Output#setType(Class)} and {@link Output#getType()} method.
     */
    @Test
    void typeTest() {
        def output = new Output()
        assert !output.type
        assert output.type(Integer)
        assert output.type
        assert Integer == output.type
        output.type = null
        assert !output.type
    }

    /**
     * Test the {@link Output#toString()} method.
     */
    @Test
    void toStringTest() {
        def process = createFactory().create().getProcess()
        def output = new Output()
        assert !output.toString()

        output.name = "name"
        assert "name" == output.toString()

        output.process = process
        assert "name:" + process.getIdentifier() == output.toString()

        output.name = null
        assert ":" + process.getIdentifier() == output.toString()
    }

    /**
     * Test the {@link Output#equals(Object)} method.
     */
    @Test
    void equalsTest() {
        def p1 = createFactory().create().process
        def p2 = createFactory().create().process
        def out1 = new Output().name("toto").process(p1)
        def out2 = new Output().name("toto").process(p1)
        def out3 = new Output().name("toto").process(p2)
        def out4 = new Output().name("toto")
        def out5 = new Output().name("tata").process(p1)
        def out6 = new Output()

        assert out1 == out2

        assert out1 != out3
        assert out1 != out4
        assert out1 != out5
        assert out1 != out6
        assert out1 != "out3"
        assert out1 != null
    }

    /**
     * Test the {@link Output#copy()} method.
     */
    @Test
    void copyTest() {
        def p1 = createFactory().create().process
        def p2 = createFactory().create().process
        def out1 = new Output().name("toto").process(p1)
        def out2 = new Output().name("toto").process(p1)
        def out3 = new Output().name("toto").process(p2)
        def out4 = new Output().name("toto")
        def out5 = new Output().name("tata").process(p1)
        def out6 = new Output()

        assert out1 == out1.copy()
        assert out1 !== out1.copy()

        assert out2 == out2.copy()
        assert out2 !== out2.copy()

        assert out3 == out3.copy()
        assert out3 !== out3.copy()

        assert out4 == out4.copy()
        assert out4 !== out4.copy()

        assert out5 == out5.copy()
        assert out5 !== out5.copy()

        assert out6 == out6.copy()
        assert out6 !== out6.copy()
    }
}
