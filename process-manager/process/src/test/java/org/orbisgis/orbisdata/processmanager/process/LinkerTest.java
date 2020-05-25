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

import org.junit.jupiter.api.Test;
import org.orbisgis.orbisdata.processmanager.api.inoutput.IInOutPut;
import org.orbisgis.orbisdata.processmanager.api.inoutput.IInput;
import org.orbisgis.orbisdata.processmanager.api.inoutput.IOutput;
import org.orbisgis.orbisdata.processmanager.process.inoutput.Input;
import org.orbisgis.orbisdata.processmanager.process.inoutput.Output;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class dedicated to {@link Linker} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC 2020)
 */
public class LinkerTest {

    /**
     * Test the {@link Linker#Linker(IInOutPut...)} method.
     */
    @Test
    void linkerTest() {
        Linker[] linker = new Linker[1];
        linker[0] = new Linker((IInOutPut[]) null);
        assertTrue(linker[0].getAliases().isEmpty());
        assertTrue(linker[0].getInputOutputMap().isEmpty());
        assertTrue(linker[0].getInputs().isEmpty());
        assertTrue(linker[0].getOutputs().isEmpty());

        linker[0] = new Linker();
        assertTrue(linker[0].getAliases().isEmpty());
        assertTrue(linker[0].getInputOutputMap().isEmpty());
        assertTrue(linker[0].getInputs().isEmpty());
        assertTrue(linker[0].getOutputs().isEmpty());

        IInput in1 = new Input();
        IInput in2 = new Input().name("in");
        IOutput out1 = new Output();
        IOutput out2 = new Output().name("out");

        LinkedHashMap<String, Object> inputs = new LinkedHashMap<>();
        inputs.put("in", in2);
        LinkedHashMap<String, Object> outputs = new LinkedHashMap<>();
        outputs.put("out", out2);
        new Process(null, null, null, inputs, outputs, null, null);

        linker[0] = new Linker(in1);
        assertTrue(linker[0].getAliases().isEmpty());
        assertTrue(linker[0].getInputOutputMap().isEmpty());
        assertTrue(linker[0].getInputs().isEmpty());
        assertTrue(linker[0].getOutputs().isEmpty());

        linker[0] = new Linker(out1);
        assertTrue(linker[0].getAliases().isEmpty());
        assertTrue(linker[0].getInputOutputMap().isEmpty());
        assertTrue(linker[0].getInputs().isEmpty());
        assertTrue(linker[0].getOutputs().isEmpty());

        linker[0] = new Linker(in2, out2);
        assertTrue(linker[0].getAliases().isEmpty());
        assertTrue(linker[0].getInputOutputMap().isEmpty());
        assertTrue(linker[0].getInputs().isEmpty());
        assertTrue(linker[0].getOutputs().isEmpty());

        linker[0] = new Linker(in2);
        assertTrue(linker[0].getAliases().isEmpty());
        assertTrue(linker[0].getInputOutputMap().isEmpty());
        assertTrue(linker[0].getInputs().isEmpty());
        assertTrue(linker[0].getOutputs().isEmpty());

        linker[0] = new Linker(out2);
        assertTrue(linker[0].getAliases().isEmpty());
        assertTrue(linker[0].getInputOutputMap().isEmpty());
        assertTrue(linker[0].getInputs().isEmpty());
        assertTrue(linker[0].getOutputs().isEmpty());
    }

    /**
     * Test the {@link Linker#to(String)} method.
     */
    @Test
    void toAliasTest() {
        Linker[] linker = new Linker[1];

        IInput in1 = new Input().name("in1");
        IInput in2 = new Input().name("in2");
        IOutput out1 = new Output().name("out1");
        IOutput out2 = new Output().name("out2");

        LinkedHashMap<String, Object> inputs = new LinkedHashMap<>();
        inputs.put("in1", in1);
        inputs.put("in2", in2);
        LinkedHashMap<String, Object> outputs = new LinkedHashMap<>();
        outputs.put("out1", out1);
        outputs.put("out2", out2);
        new Process(null, null, null, inputs, outputs, null, null);

        linker[0] = new Linker(in1, in2);
        linker[0].to("toto");
        assertEquals(1, linker[0].getAliases().size());
        assertTrue(linker[0].getAliases().containsKey("toto"));
        assertTrue(linker[0].getInputOutputMap().isEmpty());
        assertEquals(1, linker[0].getInputs().size());
        assertTrue(linker[0].getOutputs().isEmpty());

        linker[0] = new Linker(in1, in2);
        linker[0].to("in1");
        assertEquals(1, linker[0].getAliases().size());
        assertTrue(linker[0].getAliases().containsKey("in1"));
        assertTrue(linker[0].getInputOutputMap().isEmpty());
        assertTrue(linker[0].getInputs().isEmpty());
        assertTrue(linker[0].getOutputs().isEmpty());

        linker[0] = new Linker(out1);
        linker[0].to("toto");
        assertEquals(1, linker[0].getAliases().size());
        assertTrue(linker[0].getAliases().containsKey("toto"));
        assertTrue(linker[0].getInputOutputMap().isEmpty());
        assertTrue(linker[0].getInputs().isEmpty());
        assertEquals(1, linker[0].getOutputs().size());

        linker[0] = new Linker(out1);
        linker[0].to("out1");
        assertEquals(1, linker[0].getAliases().size());
        assertTrue(linker[0].getAliases().containsKey("out1"));
        assertTrue(linker[0].getInputOutputMap().isEmpty());
        assertTrue(linker[0].getInputs().isEmpty());
        assertTrue(linker[0].getOutputs().isEmpty());
    }

    /**
     * Test the {@link Linker#to(IInOutPut...)}  method.
     */
    @Test
    void toIInOutPutTest() {
        Linker[] linker = new Linker[1];

        IInput in1 = new Input().name("in1");
        IInput in2 = new Input().name("in2");
        IOutput out1 = new Output().name("out1");
        IOutput out2 = new Output().name("out2");

        linker[0] = new Linker(in1, in2);
        linker[0].to((IInOutPut[]) null);
        assertTrue(linker[0].getAliases().isEmpty());
        assertTrue(linker[0].getInputOutputMap().isEmpty());
        assertTrue(linker[0].getInputs().isEmpty());
        assertTrue(linker[0].getOutputs().isEmpty());

        linker[0] = new Linker();
        linker[0].to();
        assertTrue(linker[0].getAliases().isEmpty());
        assertTrue(linker[0].getInputOutputMap().isEmpty());
        assertTrue(linker[0].getInputs().isEmpty());
        assertTrue(linker[0].getOutputs().isEmpty());

        LinkedHashMap<String, Object> inputs = new LinkedHashMap<>();
        inputs.put("in1", in1);
        inputs.put("in2", in2);
        LinkedHashMap<String, Object> outputs = new LinkedHashMap<>();
        outputs.put("out1", out1);
        outputs.put("out2", out2);
        new Process(null, null, null, inputs, outputs, null, null);

        linker[0] = new Linker(in1, in2);
        linker[0].to(out1, out2);
        assertTrue(linker[0].getAliases().isEmpty());
        assertEquals(2, linker[0].getInputOutputMap().size());
        assertTrue(linker[0].getInputs().isEmpty());
        assertTrue(linker[0].getOutputs().isEmpty());

        linker[0] = new Linker(out1, out2);
        linker[0].to(in1, in2);
        assertTrue(linker[0].getAliases().isEmpty());
        assertEquals(2, linker[0].getInputOutputMap().size());
        assertTrue(linker[0].getInputs().isEmpty());
        assertTrue(linker[0].getOutputs().isEmpty());

        linker[0] = new Linker(out1, out2);
        linker[0].to(in1, out2);
        assertTrue(linker[0].getAliases().isEmpty());
        assertTrue(linker[0].getInputOutputMap().isEmpty());
        assertTrue(linker[0].getInputs().isEmpty());
        assertTrue(linker[0].getOutputs().isEmpty());

        linker[0] = new Linker(in1);
        linker[0].to(in2);
        assertTrue(linker[0].getAliases().isEmpty());
        assertTrue(linker[0].getInputOutputMap().isEmpty());
        assertTrue(linker[0].getInputs().isEmpty());
        assertTrue(linker[0].getOutputs().isEmpty());

        linker[0] = new Linker(out1);
        linker[0].to(out2);
        assertTrue(linker[0].getAliases().isEmpty());
        assertTrue(linker[0].getInputOutputMap().isEmpty());
        assertTrue(linker[0].getInputs().isEmpty());
        assertTrue(linker[0].getOutputs().isEmpty());

        linker[0] = new Linker();
        linker[0].to(out2);
        assertTrue(linker[0].getAliases().isEmpty());
        assertTrue(linker[0].getInputOutputMap().isEmpty());
        assertTrue(linker[0].getInputs().isEmpty());
        assertTrue(linker[0].getOutputs().isEmpty());
    }
}
