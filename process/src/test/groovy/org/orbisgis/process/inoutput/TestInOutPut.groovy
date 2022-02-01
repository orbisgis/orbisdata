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

import static org.orbisgis.process.ProcessManager.createFactory as factory

/**
 * Test class dedicated to {@link InOutPut} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC 2020)
 */
class TestInOutPut {

    /**
     * Test the {@link InOutPut#setName(String)} and {@link InOutPut#getName()} method.
     */
    @Test
    void nameTest() {
        def dummyInOutPut = new DummyInOutPut()
        assert !dummyInOutPut.name
        assert dummyInOutPut.name("name") instanceof InOutPut
        assert dummyInOutPut.name
        assert "name" == dummyInOutPut.name
        dummyInOutPut.name = null
        assert !dummyInOutPut.name
    }

    /**
     * Test the {@link InOutPut#setProcess(IProcess)} and {@link InOutPut#getProcess()} method.
     */
    @Test
    void processTest() {
        def process = factory().create().process
        def dummyInOutPut = new DummyInOutPut()
        assert !dummyInOutPut.process
        assert dummyInOutPut.process(process) instanceof InOutPut
        assert dummyInOutPut.process
        assert process == dummyInOutPut.process
        dummyInOutPut.process = null
        assert !dummyInOutPut.process
    }

    /**
     * Test the {@link InOutPut#setTitle(String)} and {@link InOutPut#getTitle()} method.
     */
    @Test
    void titleTest() {
        def dummyInOutPut = new DummyInOutPut()
        assert !dummyInOutPut.title
        assert dummyInOutPut.title("title") instanceof InOutPut
        assert dummyInOutPut.title
        assert "title" == dummyInOutPut.title
        dummyInOutPut.title = null
        assert !dummyInOutPut.title
    }

    /**
     * Test the {@link InOutPut#setDescription(String)} and {@link InOutPut#getDescription()} method.
     */
    @Test
    void descriptionTest() {
        def dummyInOutPut = new DummyInOutPut()
        assert !dummyInOutPut.description
        assert dummyInOutPut.description("descr") instanceof InOutPut
        assert dummyInOutPut.description
        assert "descr", dummyInOutPut.description
        dummyInOutPut.description = null
        assert !dummyInOutPut.description
    }

    /**
     * Test the {@link InOutPut#setKeywords(String[])} and {@link InOutPut#getKeywords()} method.
     */
    @Test
    void keywordsTest() {
        def dummyInOutPut = new DummyInOutPut()
        assert !dummyInOutPut.keywords
        assert dummyInOutPut.keywords(new String[]{"key1", "key2"}) instanceof InOutPut
        assert dummyInOutPut.keywords
        assert new String[]{"key1", "key2"} == dummyInOutPut.keywords
        dummyInOutPut.keywords = null
        assert !dummyInOutPut.keywords
    }

    /**
     * Test the {@link InOutPut#setType(Class)} and {@link InOutPut#getType()} method.
     */
    @Test
    void typeTest() {
        def dummyInOutPut = new DummyInOutPut()
        assert !dummyInOutPut.type
        assert dummyInOutPut.type(Integer.class) instanceof InOutPut
        assert dummyInOutPut.type
        assert Integer.class, dummyInOutPut.type
        dummyInOutPut.type = null
        assert !dummyInOutPut.type
    }

    /**
     * Test the {@link InOutPut#toString()} method.
     */
    @Test
    void toStringTest() {
        def process = factory().create().getProcess()
        def dummyInOutPut = new DummyInOutPut()
        assert !dummyInOutPut.toString()

        dummyInOutPut.name "name"
        assert "name", dummyInOutPut.toString()

        dummyInOutPut.process = process
        assert "name:" + process.getIdentifier() == dummyInOutPut.toString()

        dummyInOutPut.name = null
        assert ":" + process.getIdentifier() == dummyInOutPut.toString()
    }
}
