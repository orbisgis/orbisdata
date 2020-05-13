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
 * terms of the GNU General License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * ProcessManager is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General License for more details.
 *
 * You should have received a copy of the GNU General License along with
 * ProcessManager. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.orbisdata.processmanager.process.check

import org.junit.jupiter.api.Test

import static org.orbisgis.orbisdata.processmanager.api.check.IProcessCheck.Action.CONTINUE
import static org.orbisgis.orbisdata.processmanager.api.check.IProcessCheck.Action.STOP

/**
 * Test class dedicated to {@link CheckOptionBuilder} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019-2020)
 */
class TestCheckOptionBuilder {

    /**
     * Test the {@link CheckOptionBuilder#stopOnFail()}, {@link CheckOptionBuilder#stopOnFail(String)},
     * {@link CheckOptionBuilder#continueOnFail()}, {@link CheckOptionBuilder#continueOnFail(String)}methods.
     */
    @Test
    void onFailTest() {
        def failMessage = "Fail Message"
        def successMessage = "Success Message"

        def dummy = new DummyProcessCheck()
        def builder = new CheckOptionBuilder(dummy)
        assert builder.stopOnFail()
        assert STOP == dummy.failAction
        assert !dummy.failMessage

        dummy = new DummyProcessCheck()
        builder = new CheckOptionBuilder(dummy)
        assert builder.stopOnFail(failMessage)
        assert STOP == dummy.failAction
        assert failMessage == dummy.failMessage

        dummy = new DummyProcessCheck()
        builder = new CheckOptionBuilder(dummy)
        assert builder.stopOnFail()
        assert STOP == dummy.failAction
        assert !dummy.failMessage

        dummy = new DummyProcessCheck()
        builder = new CheckOptionBuilder(dummy)
        assert builder.stopOnFail()
        assert STOP == dummy.failAction
        assert !dummy.failMessage

        dummy = new DummyProcessCheck()
        builder = new CheckOptionBuilder(dummy)
        assert builder.stopOnFail()
        assert STOP == dummy.failAction
        assert !dummy.failMessage

        dummy = new DummyProcessCheck()
        builder = new CheckOptionBuilder(dummy)
        assert builder.continueOnFail(successMessage)
        assert CONTINUE == dummy.failAction
        assert successMessage == dummy.failMessage

        dummy = new DummyProcessCheck()
        builder = new CheckOptionBuilder(dummy)
        assert builder.continueOnFail()
        assert CONTINUE == dummy.failAction
        assert !dummy.failMessage

        dummy = new DummyProcessCheck()
        builder = new CheckOptionBuilder(dummy)
        assert builder.continueOnFail()
        assert CONTINUE == dummy.failAction
        assert !dummy.failMessage

        dummy = new DummyProcessCheck()
        builder = new CheckOptionBuilder(dummy)
        assert builder.continueOnFail()
        assert CONTINUE == dummy.failAction
        assert !dummy.failMessage
    }

    /**
     * Test the {@link CheckOptionBuilder#stopOnSuccess()}, {@link CheckOptionBuilder#stopOnSuccess(String)},
     * {@link CheckOptionBuilder#continueOnSuccess()}, {@link CheckOptionBuilder#continueOnSuccess(String)}methods.
     */
    @Test
    void onSuccessTest() {
        def failMessage = "Fail Message"
        def successMessage = "Success Message"

        def dummy = new DummyProcessCheck()
        def builder = new CheckOptionBuilder(dummy)
        assert builder.stopOnSuccess()
        assert STOP == dummy.successAction
        assert !dummy.successMessage

        dummy = new DummyProcessCheck()
        builder = new CheckOptionBuilder(dummy)
        assert builder.stopOnSuccess(failMessage)
        assert STOP == dummy.successAction
        assert failMessage, dummy.successMessage

        dummy = new DummyProcessCheck()
        builder = new CheckOptionBuilder(dummy)
        assert builder.stopOnSuccess()
        assert STOP == dummy.successAction
        assert !dummy.successMessage

        dummy = new DummyProcessCheck()
        builder = new CheckOptionBuilder(dummy)
        assert builder.stopOnSuccess()
        assert STOP == dummy.successAction
        assert !dummy.successMessage

        dummy = new DummyProcessCheck()
        builder = new CheckOptionBuilder(dummy)
        assert builder.stopOnSuccess()
        assert STOP == dummy.successAction
        assert !dummy.successMessage

        dummy = new DummyProcessCheck()
        builder = new CheckOptionBuilder(dummy)
        assert builder.continueOnSuccess(successMessage)
        assert CONTINUE == dummy.successAction
        assert successMessage == dummy.successMessage

        dummy = new DummyProcessCheck()
        builder = new CheckOptionBuilder(dummy)
        assert builder.continueOnSuccess()
        assert CONTINUE == dummy.successAction
        assert !dummy.successMessage

        dummy = new DummyProcessCheck()
        builder = new CheckOptionBuilder(dummy)
        assert builder.continueOnSuccess()
        assert CONTINUE == dummy.successAction
        assert !dummy.successMessage

        dummy = new DummyProcessCheck()
        builder = new CheckOptionBuilder(dummy)
        assert builder.continueOnSuccess()
        assert CONTINUE == dummy.successAction
        assert !dummy.successMessage
    }
}
