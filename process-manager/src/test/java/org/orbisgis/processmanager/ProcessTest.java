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
 * ProcessManager is distributed under GPL 3 license.
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
package org.orbisgis.processmanager;

import groovy.lang.Closure;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyShell;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.orbisgis.processmanager.inoutput.Input;
import org.orbisgis.processmanager.inoutput.Output;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class dedicated to {@link Process} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public class ProcessTest {

    private static Process miniProcess;
    private static Process fullProcess;

    @BeforeAll
    static void beforeAll(){

        Closure cl1 = new Closure(null) {
            @Override
            public Object call() {
                return "minimalProcess";
            }
        };

        miniProcess = new Process(null, null, null, null, null, null, cl1);

        String str = "{in1, in2, in3, in4 -> Object o1 = \"$in1$in2$in3$in4\"\n" +
                "                Object o2 = 4\n" +
                "                return [out1:o1, out2:o2, out3:\"toto\"]}";
        Closure cl2 = (Closure)new GroovyShell().evaluate(new GroovyCodeSource(str, "script", ""));

        Input in3 = Input.call().setTitle("in title").setDescription("in description")
                .setKeywords(new String[]{"key1", "key2"}).setType(String[].class).mandatory();
        Input in4 = Input.call().setType(Double.class).optional(3.56D);

        LinkedHashMap<String, Object> inputs = new LinkedHashMap<>();
        inputs.put("in1", 5698);
        inputs.put("in2", String.class);
        inputs.put("in3", in3);
        inputs.put("in4", in4);

        Output out1 = Output.call().setTitle("out title").setDescription("out description")
                .setKeywords(new String[]{"key1", "key2"}).setType(String[].class);
        Output out2 = Output.call().setType(int.class);

        LinkedHashMap<String, Object> outputs = new LinkedHashMap<>();
        outputs.put("out1", out1);
        outputs.put("out2", out2);
        outputs.put("out3", String.class);

        fullProcess = new Process("title", "description", new String[]{"test", "process"}, inputs, outputs, "1.0.0", cl2);
    }

    @Test
    void testProcessProperties(){
        assertNull(miniProcess.getTitle());
        assertNull(miniProcess.getDescription());
        assertNull(miniProcess.getKeywords());
        assertTrue(miniProcess.getInputs().isEmpty());
        assertTrue(miniProcess.getOutputs().isEmpty());
        assertNull(miniProcess.getVersion());
        assertTrue(miniProcess.execute(null));


        assertEquals("title", fullProcess.getTitle());
        assertEquals("description", fullProcess.getDescription());
        assertNotNull(fullProcess.getKeywords());
        assertEquals(2, fullProcess.getKeywords().length);
        assertEquals("test", fullProcess.getKeywords()[0]);
        assertEquals("process", fullProcess.getKeywords()[1]);

        assertNotNull(fullProcess.getInputs());
        assertEquals(4, fullProcess.getInputs().size());

        assertEquals(5698, fullProcess.getInputs().get(0).getDefaultValue());
        assertEquals("in1", fullProcess.getInputs().get(0).getName());
        assertEquals(Integer.class, fullProcess.getInputs().get(0).getType());
        assertEquals(fullProcess.getIdentifier(), fullProcess.getInputs().get(0).getProcess().getIdentifier());

        assertNull(fullProcess.getInputs().get(1).getDefaultValue());
        assertEquals("in2", fullProcess.getInputs().get(1).getName());
        assertEquals(String.class, fullProcess.getInputs().get(1).getType());
        assertEquals(fullProcess.getIdentifier(), fullProcess.getInputs().get(1).getProcess().getIdentifier());

        assertNull(fullProcess.getInputs().get(2).getDefaultValue());
        assertEquals("in3", fullProcess.getInputs().get(2).getName());
        assertEquals(String[].class, fullProcess.getInputs().get(2).getType());
        assertEquals(fullProcess.getIdentifier(), fullProcess.getInputs().get(2).getProcess().getIdentifier());

        assertEquals(3.56D, fullProcess.getInputs().get(3).getDefaultValue());
        assertEquals("in4", fullProcess.getInputs().get(3).getName());
        assertEquals(Double.class, fullProcess.getInputs().get(3).getType());
        assertEquals(fullProcess.getIdentifier(), fullProcess.getInputs().get(3).getProcess().getIdentifier());

        assertNotNull(fullProcess.getOutputs());
        assertEquals(3, fullProcess.getOutputs().size());

        assertEquals("out1", fullProcess.getOutputs().get(0).getName());
        assertEquals(String[].class, fullProcess.getOutputs().get(0).getType());
        assertEquals(fullProcess.getIdentifier(), fullProcess.getOutputs().get(0).getProcess().getIdentifier());

        assertEquals("out2", fullProcess.getOutputs().get(1).getName());
        assertEquals(int.class, fullProcess.getOutputs().get(1).getType());
        assertEquals(fullProcess.getIdentifier(), fullProcess.getOutputs().get(1).getProcess().getIdentifier());

        assertEquals("out3", fullProcess.getOutputs().get(2).getName());
        assertEquals(String.class, fullProcess.getOutputs().get(2).getType());
        assertEquals(fullProcess.getIdentifier(), fullProcess.getOutputs().get(2).getProcess().getIdentifier());

        assertEquals("1.0.0", fullProcess.getVersion());

        LinkedHashMap<String, Object> data = new LinkedHashMap<>();
        data.put("in2", "toto");
        data.put("in3", new String[]{"t", "a", "t", "a"});
        assertTrue(fullProcess.execute(data));
        assertEquals(3, fullProcess.getResults().size());
        assertEquals("5698toto[t, a, t, a]3.56", fullProcess.getResults().get("out1").toString());
        assertEquals(4, fullProcess.getResults().get("out2"));
        assertEquals("toto", fullProcess.getResults().get("out3"));
    }
}
