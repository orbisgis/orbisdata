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
package org.orbisgis.process

import org.junit.jupiter.api.Test
import org.orbisgis.commons.annotations.Nullable
import org.orbisgis.process.api.IProcess

/**
 * Test class dedicated to {@link GroovyProcessFactory} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC 2019- 2020)
 */
class TestGroovyProcessFactory {

    /**
     * Test the {@link ProcessFactory#ProcessFactory(boolean, boolean)}, {@link ProcessFactory#ProcessFactory()},
     * {@link ProcessFactory#isDefault()} and {@link ProcessFactory#isLocked()} methods.
     */
    @Test
    void testAttributes() {
        def pf = new DummyFactory()
        assert !pf.default
        assert !pf.locked
    }

    /**
     * Test the {@link ProcessFactory#registerProcess(IProcess)} and {@link ProcessFactory#getProcess(String)} methods.
     */
    @Test
    void testRegister() {
        def process = new Process(null, null, null, null, null, null, null)

        def pf = new DummyFactory()
        pf.registerProcess(process)
        assert pf.getProcess(process.identifier)
    }

    /**
     * Test the {@link GroovyProcessFactory#create()} and {@link GroovyProcessFactory#create(Closure)} methods.
     */
    @Test
    void testCreate() {
        def pf1 = new DummyFactory()
        assert pf1.create()

        def process = pf1.create {
            title "simple process"
            description "description"
            keywords "key1", "key2"
            inputs inputA: String, inputB: String
            outputs outputA: String
            version "version"
            run { inputA, inputB -> [outputA: inputA + inputB] }
        }

        assert "simple process" == process.title
        assert "description", process.description
        assert "version", process.version
        assert ["key1", "key2"] == process.keywords
        assert 2, process.inputs.size
        assert 1, process.outputs.size
    }

    private static class DummyFactory extends GroovyProcessFactory {
        @Override
        @Nullable
        Object run() {
            return null
        }

        @Override
        Object invokeMethod(@Nullable String name, @Nullable Object args) {
            super.invokeMethod(name, args)
        }
    }
}
