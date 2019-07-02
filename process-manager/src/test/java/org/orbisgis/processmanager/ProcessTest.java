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
import org.codehaus.groovy.runtime.InvokerHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.orbisgis.processmanager.inoutput.Input;
import org.orbisgis.processmanager.inoutput.Output;
import org.orbisgis.processmanagerapi.IProcess;
import org.orbisgis.processmanagerapi.inoutput.IInput;
import org.orbisgis.processmanagerapi.inoutput.IOutput;

import java.util.Arrays;
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
    private static ProcessMapper mapper;

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

        Input in3 = Input.create().setTitle("in title").setDescription("in description")
                .setKeywords(new String[]{"key1", "key2"}).setType(String[].class).mandatory();
        Input in4 = Input.create().setType(Double.class).optional(3.56D);

        LinkedHashMap<String, Object> inputs = new LinkedHashMap<>();
        inputs.put("in1", 5698);
        inputs.put("in2", String.class);
        inputs.put("in3", in3);
        inputs.put("in4", in4);

        Output out1 = Output.create().setTitle("out title").setDescription("out description")
                .setKeywords(new String[]{"key1", "key2"}).setType(String[].class);
        Output out2 = Output.create().setType(int.class);

        LinkedHashMap<String, Object> outputs = new LinkedHashMap<>();
        outputs.put("out1", out1);
        outputs.put("out2", out2);
        outputs.put("out3", String.class);

        fullProcess = new Process("title", "description", new String[]{"test", "process"}, inputs, outputs, "1.0.0", cl2);

        mapper = new ProcessMapper();
        mapper.link(fullProcess.getInputs().get(0)).to("in1");
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



        assertTrue(mapper.execute(data));

        assertNotNull(mapper.getInputs());
        assertEquals(4, mapper.getInputs().size());

        assertEquals(5698, mapper.getInputs().get(0).getDefaultValue());
        assertEquals("in1", mapper.getInputs().get(0).getName());
        assertEquals(Integer.class, mapper.getInputs().get(0).getType());

        assertNull(mapper.getInputs().get(1).getDefaultValue());
        assertEquals("in2", mapper.getInputs().get(1).getName());
        assertEquals(String.class, mapper.getInputs().get(1).getType());

        assertNull(mapper.getInputs().get(2).getDefaultValue());
        assertEquals("in3", mapper.getInputs().get(2).getName());
        assertEquals(String[].class, mapper.getInputs().get(2).getType());

        assertEquals(3.56D, mapper.getInputs().get(3).getDefaultValue());
        assertEquals("in4", mapper.getInputs().get(3).getName());
        assertEquals(Double.class, mapper.getInputs().get(3).getType());

        assertNotNull(mapper.getOutputs());
        assertEquals(3, mapper.getOutputs().size());

        assertEquals("out1", mapper.getOutputs().get(0).getName());
        assertEquals(String[].class, mapper.getOutputs().get(0).getType());

        assertEquals("out2", mapper.getOutputs().get(1).getName());
        assertEquals(int.class, mapper.getOutputs().get(1).getType());

        assertEquals("out3", mapper.getOutputs().get(2).getName());
        assertEquals(String.class, mapper.getOutputs().get(2).getType());

        assertEquals(3, mapper.getResults().size());
        assertEquals("5698toto[t, a, t, a]3.56", mapper.getResults().get("out1").toString());
        assertEquals(4, mapper.getResults().get("out2"));
        assertEquals("toto", mapper.getResults().get("out3"));
    }

    @Test
    void testBadProcess(){
        assertFalse(new Process(null, null, null, new LinkedHashMap<>(), null, null, new Closure(null) {
            @Override
            public int getMaximumNumberOfParameters() {
                return 2;
            }
        }).execute(new LinkedHashMap<>()));

        assertFalse(new Process(null, null, null, new LinkedHashMap<>(), null, null, null).execute(null));


        LinkedHashMap<String, Object> inputs = new LinkedHashMap<>();
        inputs.put("in", String.class);
        LinkedHashMap<String, Object> outputs = new LinkedHashMap<>();
        outputs.put("out", String.class);
        Process p = new Process(null, null, null, inputs, outputs, null, new Closure(null) {
            @Override
            public int getMaximumNumberOfParameters() {
                return 1;
            }
            @Override
            public Object call(Object... args){
                Arrays.asList(args).forEach(Object::toString);
                return new LinkedHashMap<>();
            }
        });
        LinkedHashMap<String, Object> data = new LinkedHashMap<>();
        data.put("in", null);
        assertFalse(p.execute(data));

        inputs = new LinkedHashMap<>();
        inputs.put("in", String.class);
        outputs = new LinkedHashMap<>();
        outputs.put("out", String.class);
        p = new Process(null, null, null, inputs, outputs, null, new Closure(null) {
            @Override
            public int getMaximumNumberOfParameters() {
                return 1;
            }
            @Override
            public Object call(Object... args){
                return new LinkedHashMap<>();
            }
        });
        data = new LinkedHashMap<>();
        data.put("in", null);
        assertFalse(p.execute(data));

        fullProcess.execute(new LinkedHashMap<>());
        data = new LinkedHashMap<>();
        data.put("in3", "toto");
        data.put("in", "toto");
        assertFalse(fullProcess.execute(data));
    }

    @Test
    void testNewInstance(){
        IProcess p = fullProcess.newInstance();
        assertEquals(fullProcess.getTitle(), p.getTitle());
        assertEquals(fullProcess.getDescription(), p.getDescription());
        assertEquals(fullProcess.getKeywords(), p.getKeywords());
        assertEquals(fullProcess.getInputs(), p.getInputs());
        assertEquals(fullProcess.getOutputs(), p.getOutputs());
        assertEquals(fullProcess.getVersion(), p.getVersion());
        assertNotEquals(fullProcess.getIdentifier(), p.getIdentifier());
    }

    @Test
    void testInvokeMethod(){
        assertEquals("title", fullProcess.invokeMethod("getTitle", null));
        assertEquals("1.0.0", fullProcess.invokeMethod("getVersion", null));
    }

    @Test
    void testGetProperty(){
        assertTrue(fullProcess.getProperty("in1") instanceof IInput);
        assertTrue(fullProcess.getProperty("in2") instanceof IInput);
        assertTrue(fullProcess.getProperty("in3") instanceof IInput);
        assertTrue(fullProcess.getProperty("in4") instanceof IInput);
        assertTrue(fullProcess.getProperty("out1") instanceof IOutput);
        assertTrue(fullProcess.getProperty("out2") instanceof IOutput);
        assertTrue(fullProcess.getProperty("out3") instanceof IOutput);
        assertEquals("title", fullProcess.getProperty("title"));
    }

    @Test
    void testSetProperty(){
        assertEquals("title", fullProcess.getProperty("title"));
        fullProcess.setProperty("title", "toto");
        assertEquals("toto", fullProcess.getProperty("title"));
        fullProcess.setProperty("title", "title");
        assertEquals("title", fullProcess.getProperty("title"));
    }

    @Test
    void testMetaClass(){
        assertEquals(InvokerHelper.getMetaClass(Process.class), fullProcess.getMetaClass());
        fullProcess.setMetaClass(InvokerHelper.getMetaClass(ProcessTest.class));
        assertEquals(InvokerHelper.getMetaClass(ProcessTest.class), fullProcess.getMetaClass());
        fullProcess.setMetaClass(InvokerHelper.getMetaClass(Process.class));
        assertEquals(InvokerHelper.getMetaClass(Process.class), fullProcess.getMetaClass());
    }
}
