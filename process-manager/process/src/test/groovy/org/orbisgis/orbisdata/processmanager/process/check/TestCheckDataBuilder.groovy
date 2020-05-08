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
import org.orbisgis.orbisdata.processmanager.api.inoutput.IInOutPut
import org.orbisgis.orbisdata.processmanager.process.inoutput.Input
import org.orbisgis.orbisdata.processmanager.process.inoutput.Output

/**
 * Test class dedicated to {@link CheckClosureBuilder} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2020)
 */
class TestCheckDataBuilder {

    /**
     * Test the {@link CheckDataBuilder#with(IInOutPut...)} method.
     */
    @Test
    void withTest() {
        def (in1, in2) = [new Input(), new Input()]
        def (out1, out2) = [new Output(), new Output()]

        def dummy = new DummyProcessCheck()
        def builder = new CheckDataBuilder(dummy)
        assert builder.with(in1, in2, out1, out2)
        assert [in1, in2, out1, out2] == dummy.inputOrOutput

        dummy = new DummyProcessCheck()
        builder = new CheckDataBuilder(dummy)
        assert builder.with(out2)
        assert [out2] == dummy.inputOrOutput

        dummy = new DummyProcessCheck()
        builder = new CheckDataBuilder(dummy)
        assert builder.with()
        assert !dummy.inputOrOutput

        dummy = new DummyProcessCheck()
        builder = new CheckDataBuilder(dummy)
        assert builder.with()
        assert !dummy.inputOrOutput

        dummy = new DummyProcessCheck()
        builder = new CheckDataBuilder(dummy)
        assert builder.with()
        assert !dummy.inputOrOutput
    }
}
