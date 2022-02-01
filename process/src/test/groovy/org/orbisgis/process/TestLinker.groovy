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
package org.orbisgis.process

import org.junit.jupiter.api.Test
import org.orbisgis.process.api.inoutput.IInOutPut
import org.orbisgis.process.inoutput.Input
import org.orbisgis.process.inoutput.Output

/**
 * Test class dedicated to {@link Linker} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC 2020)
 */
class TestLinker {

    /**
     * Test the {@link Linker#Linker(IInOutPut...)} method.
     */
    @Test
    void linkerTest() {
        def linker = [new Linker((IInOutPut[]) null)]
        assert !linker[0].aliases
        assert !linker[0].inputOutputMap
        assert !linker[0].inputs
        assert !linker[0].outputs

        linker[0] = new Linker()
        assert !linker[0].aliases
        assert !linker[0].inputOutputMap
        assert !linker[0].inputs
        assert !linker[0].outputs

        def in1 = new Input()
        def in2 = new Input().name("in")
        def out1 = new Output()
        def out2 = new Output().name("out")

        def inputs = ["in": in2]
        def outputs = ["out": out2]
        new Process(null, null, null, inputs, outputs, null, null)

        linker[0] = new Linker(in1)
        assert !linker[0].aliases
        assert !linker[0].inputOutputMap
        assert !linker[0].inputs
        assert !linker[0].outputs

        linker[0] = new Linker(out1)
        assert !linker[0].aliases
        assert !linker[0].inputOutputMap
        assert !linker[0].inputs
        assert !linker[0].outputs

        linker[0] = new Linker(in2, out2)
        assert !linker[0].aliases
        assert !linker[0].inputOutputMap
        assert !linker[0].inputs
        assert !linker[0].outputs

        linker[0] = new Linker(in2)
        assert !linker[0].aliases
        assert !linker[0].inputOutputMap
        assert !linker[0].inputs
        assert !linker[0].outputs

        linker[0] = new Linker(out2)
        assert !linker[0].aliases
        assert !linker[0].inputOutputMap
        assert !linker[0].inputs
        assert !linker[0].outputs
    }

    /**
     * Test the {@link Linker#to(String)} method.
     */
    @Test
    void toAliasTest() {
        def in1 = new Input().name("in1")
        def in2 = new Input().name("in2")
        def out1 = new Output().name("out1")
        def out2 = new Output().name("out2")

        def inputs = ["in1": in1, "in2": in2]
        def outputs = ["out1": out1, "out2": out2]
        new Process(null, null, null, inputs, outputs, null, null)

        def linker = [new Linker(in1, in2)]
        linker[0].to("toto")
        assert 1 == linker[0].aliases.size()
        assert linker[0].aliases.containsKey("toto")
        assert !linker[0].inputOutputMap
        assert 1 == linker[0].inputs.size
        assert !linker[0].outputs

        linker[0] = new Linker(in1, in2)
        linker[0].to("in1")
        assert 1 == linker[0].aliases.size()
        assert linker[0].aliases.containsKey("in1")
        assert !linker[0].inputOutputMap
        assert !linker[0].inputs
        assert !linker[0].outputs

        linker[0] = new Linker(out1)
        linker[0].to("toto")
        assert 1 == linker[0].aliases.size()
        assert linker[0].aliases.containsKey("toto")
        assert !linker[0].inputOutputMap
        assert !linker[0].inputs
        assert 1 == linker[0].outputs.size

        linker[0] = new Linker(out1)
        linker[0].to("out1")
        assert 1 == linker[0].aliases.size()
        assert linker[0].aliases.containsKey("out1")
        assert !linker[0].inputOutputMap
        assert !linker[0].inputs
        assert !linker[0].outputs
    }

    /**
     * Test the {@link Linker#to(IInOutPut...)}  method.
     */
    @Test
    void toIInOutPutTest() {

        def in1 = new Input().name("in1")
        def in2 = new Input().name("in2")
        def out1 = new Output().name("out1")
        def out2 = new Output().name("out2")

        def linker = [new Linker(in1, in2)]
        linker[0].to((IInOutPut[]) null)
        assert !linker[0].aliases
        assert !linker[0].inputOutputMap
        assert !linker[0].inputs
        assert !linker[0].outputs

        linker[0] = new Linker()
        linker[0].to()
        assert !linker[0].aliases
        assert !linker[0].inputOutputMap
        assert !linker[0].inputs
        assert !linker[0].outputs

        def inputs = ["in1": in1, "in2": in2]
        def outputs = ["out1": out1, "out2": out2]
        new Process(null, null, null, inputs, outputs, null, null)

        linker[0] = new Linker(in1, in2)
        linker[0].to(out1, out2)
        assert !linker[0].aliases
        assert 2 == linker[0].inputOutputMap.size()
        assert !linker[0].inputs
        assert !linker[0].outputs

        linker[0] = new Linker(out1, out2)
        linker[0].to(in1, in2)
        assert !linker[0].aliases
        assert 2 == linker[0].inputOutputMap.size()
        assert !linker[0].inputs
        assert !linker[0].outputs

        linker[0] = new Linker(out1, out2)
        linker[0].to(in1, out2)
        assert !linker[0].aliases
        assert !linker[0].inputOutputMap
        assert !linker[0].inputs
        assert !linker[0].outputs

        linker[0] = new Linker(in1)
        linker[0].to(in2)
        assert !linker[0].aliases
        assert !linker[0].inputOutputMap
        assert !linker[0].inputs
        assert !linker[0].outputs

        linker[0] = new Linker(out1)
        linker[0].to(out2)
        assert !linker[0].aliases
        assert !linker[0].inputOutputMap
        assert !linker[0].inputs
        assert !linker[0].outputs

        linker[0] = new Linker()
        linker[0].to(out2)
        assert !linker[0].aliases
        assert !linker[0].inputOutputMap
        assert !linker[0].inputs
        assert !linker[0].outputs
    }
}
