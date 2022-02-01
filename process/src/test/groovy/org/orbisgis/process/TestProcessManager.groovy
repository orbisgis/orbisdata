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

/**
 * Test class dedicated to {@link ProcessFactory} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC 2019-2020)
 */
class TestProcessManager {

    /**
     * Test the {@link ProcessManager#getProcessManager()} method
     */
    @Test
    void getProcessManagerTest() {
        assert ProcessManager.getProcessManager()
    }

    /**
     * Test the {@link ProcessManager#create()} and {@link ProcessManager#create(Closure)} methods.
     */
    @Test
    void testCreate() {
        def pm = ProcessManager.getProcessManager()
        assert pm.create()
        assert pm.create().getProcess()

        def process = pm.create {
                    title "simple process"
                    description "description"
                    keywords "key1", "key2"
                    inputs inputA: String, inputB: String
                    outputs outputA: String
                    version "version"
                    run { inputA, inputB -> [outputA: inputA + inputB] }
            }
        assert process
        assert "simple process" == process.title
        assert "description", process.description
        assert "version", process.version
        assert ["key1", "key2"] == process.keywords
        assert 2, process.inputs.size
        assert 1 == process.outputs.size

        process = pm.create(null)
        assert !process
    }

    /**
     * Test the {@link ProcessManager#factoryIds()},
     * {@link ProcessManager#factory(String)}, {@link ProcessManager#factory()},
     * {@link ProcessManager#createFactory(String)}, {@link ProcessManager#createFactory()} methods.
     */
    @Test
    void testFactories() {
        def pm = ProcessManager.getProcessManager()
        assert ProcessManager.createFactory()
        assert ProcessManager.createFactory("Mayor_DeFacto_Ry")
        assert ProcessManager.createFactory(null)
        assert ProcessManager.createFactory("")

        assert pm.factory()
        assert pm.factory() == pm.factory(null)
        assert pm.factory() == pm.factory("Default")
        assert pm.factory("Mayor_DeFacto_Ry")
        assert pm.factory("Factorio")

        assert pm.factoryIds().contains("Default")
        assert pm.factoryIds().contains("Mayor_DeFacto_Ry")
        assert pm.factoryIds().contains("Factorio")
    }

    /**
     * Test the {@link ProcessManager#process(String)} and {@link ProcessManager#process(String, String)} methods.
     */
    @Test
    void testProcess() {
        def pm = ProcessManager.getProcessManager()
        assert ProcessManager.createFactory("Mayor_DeFacto_Ry")

        def p1 = pm.factory().create {
            title "simple process"
            description "description"
            keywords "key1", "key2"
            inputs inputA: String, inputB: String
            outputs outputA: String
            version "version"
            run { inputA, inputB -> [outputA: inputA + inputB] }
        }
        assert p1
        def id1 = p1.identifier

        def p2 = pm.factory("Mayor_DeFacto_Ry").create {
            title "simple process"
            description "description"
            keywords "key1", "key2"
            inputs inputA: String, inputB: String
            outputs outputA: String
            version "version"
            run { inputA, inputB -> [outputA: inputA + inputB] }
        }
        assert p2
        def id2 = p2.identifier

        assert pm.process(id1)
        assert !pm.process(id2)

        assert !pm.process(id1, "Mayor_DeFacto_Ry")
        assert pm.process(id2, "Mayor_DeFacto_Ry")

        assert !pm.process(null)
        assert !pm.process(null, "Mayor_DeFacto_Ry")
        assert !pm.process(id2, null)
        assert !pm.process(null, null)
    }
}
