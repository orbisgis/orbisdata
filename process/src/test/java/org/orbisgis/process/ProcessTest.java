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
package org.orbisgis.process;

import groovy.lang.Closure;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.orbisgis.process.api.IProcess;
import org.orbisgis.process.api.inoutput.IInOutPut;
import org.orbisgis.process.api.inoutput.IInput;
import org.orbisgis.process.api.inoutput.IOutput;
import org.orbisgis.process.inoutput.Input;
import org.orbisgis.process.inoutput.Output;

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

    @BeforeAll
    static void beforeAll() {

        Closure<Object> cl1 = new Closure<Object>(null) {
           
            @Override
            public Object call() {
                return null;
            }
        };

        miniProcess = new Process(null, null, null, null, null, null, cl1);

        String str = "{in1, in2, in3, in4 -> Object o1 = \"$in1$in2$in3$in4\"\n" +
                "                Object o2 = 4\n" +
                "                return [out1:o1, out2:o2, out3:\"toto\"]}";
        Closure<?> cl2 = (Closure<?>) new GroovyShell().evaluate(new GroovyCodeSource(str, "script", ""));

        Input in3 = new Input().title("in title").description("in description")
                .keywords(new String[]{"key1", "key2"}).type(String[].class).mandatory();
        Input in4 = new Input().type(Double.class).optional(3.56D);

        LinkedHashMap<String, Object> inputs = new LinkedHashMap<>();
        inputs.put("in1", 5698);
        inputs.put("in2", String.class);
        inputs.put("in3", in3);
        inputs.put("in4", in4);

        Output out1 = new Output().title("out title").description("out description")
                .keywords(new String[]{"key1", "key2"}).type(String[].class);
        Output out2 = new Output().type(int.class);

        LinkedHashMap<String, Object> outputs = new LinkedHashMap<>();
        outputs.put("out1", out1);
        outputs.put("out2", out2);
        outputs.put("out3", String.class);

        fullProcess = new Process("title", "description", new String[]{"test", "process"}, inputs, outputs, "1.0.0", cl2);
    }

    @Test
    void nullResultTest() {
        Closure<Object> cl = new Closure<Object>(null) {
           
            @Override
            public Object call() {
                return null;
            }
        };

        LinkedHashMap<String, Object> outputs = new LinkedHashMap<>();
        outputs.put("out1", new Output().type(int.class));

        assertFalse(new Process("title", "description", new String[]{"test", "process"}, new LinkedHashMap<>(),
                outputs, "1.0.0", cl).execute(new LinkedHashMap<>()));
    }

    @Test
    void processPropertiesTest() {
        assertFalse(miniProcess.getTitle().isPresent());
        assertFalse(miniProcess.getDescription().isPresent());
        assertFalse(miniProcess.getKeywords().isPresent());
        assertTrue(miniProcess.getInputs().isEmpty());
        assertTrue(miniProcess.getOutputs().isEmpty());
        assertFalse(miniProcess.getVersion().isPresent());
        assertTrue(miniProcess.execute(null));

        assertTrue(fullProcess.getTitle().isPresent());
        assertEquals("title", fullProcess.getTitle().get());
        assertTrue(fullProcess.getDescription().isPresent());
        assertEquals("description", fullProcess.getDescription().get());
        assertTrue(fullProcess.getKeywords().isPresent());
        assertEquals(2, fullProcess.getKeywords().get().length);
        assertEquals("test", fullProcess.getKeywords().get()[0]);
        assertEquals("process", fullProcess.getKeywords().get()[1]);

        assertNotNull(fullProcess.getInputs());
        assertEquals(4, fullProcess.getInputs().size());

        assertTrue(fullProcess.getInputs().get(0).getDefaultValue().isPresent());
        assertEquals(5698, fullProcess.getInputs().get(0).getDefaultValue().get());
        assertTrue(fullProcess.getInputs().get(0).getName().isPresent());
        assertEquals("in1", fullProcess.getInputs().get(0).getName().get());
        assertTrue(fullProcess.getInputs().get(0).getType().isPresent());
        assertEquals(Integer.class, fullProcess.getInputs().get(0).getType().get());
        assertTrue(fullProcess.getInputs().get(0).getProcess().isPresent());
        assertEquals(fullProcess.getIdentifier(), fullProcess.getInputs().get(0).getProcess().get().getIdentifier());

        assertFalse(fullProcess.getInputs().get(1).getDefaultValue().isPresent());
        assertTrue(fullProcess.getInputs().get(1).getName().isPresent());
        assertEquals("in2", fullProcess.getInputs().get(1).getName().get());
        assertTrue(fullProcess.getInputs().get(1).getType().isPresent());
        assertEquals(String.class, fullProcess.getInputs().get(1).getType().get());
        assertTrue(fullProcess.getInputs().get(1).getProcess().isPresent());
        assertEquals(fullProcess.getIdentifier(), fullProcess.getInputs().get(1).getProcess().get().getIdentifier());

        assertFalse(fullProcess.getInputs().get(2).getDefaultValue().isPresent());
        assertTrue(fullProcess.getInputs().get(2).getName().isPresent());
        assertEquals("in3", fullProcess.getInputs().get(2).getName().get());
        assertTrue(fullProcess.getInputs().get(2).getType().isPresent());
        assertEquals(String[].class, fullProcess.getInputs().get(2).getType().get());
        assertTrue(fullProcess.getInputs().get(2).getProcess().isPresent());
        assertEquals(fullProcess.getIdentifier(), fullProcess.getInputs().get(2).getProcess().get().getIdentifier());

        assertTrue(fullProcess.getInputs().get(3).getDefaultValue().isPresent());
        assertEquals(3.56D, fullProcess.getInputs().get(3).getDefaultValue().get());
        assertTrue(fullProcess.getInputs().get(3).getName().isPresent());
        assertEquals("in4", fullProcess.getInputs().get(3).getName().get());
        assertTrue(fullProcess.getInputs().get(3).getType().isPresent());
        assertEquals(Double.class, fullProcess.getInputs().get(3).getType().get());
        assertTrue(fullProcess.getInputs().get(3).getProcess().isPresent());
        assertEquals(fullProcess.getIdentifier(), fullProcess.getInputs().get(3).getProcess().get().getIdentifier());

        assertNotNull(fullProcess.getOutputs());
        assertEquals(3, fullProcess.getOutputs().size());

        assertTrue(fullProcess.getOutputs().get(0).getName().isPresent());
        assertEquals("out1", fullProcess.getOutputs().get(0).getName().get());
        assertTrue(fullProcess.getOutputs().get(0).getType().isPresent());
        assertEquals(String[].class, fullProcess.getOutputs().get(0).getType().get());
        assertTrue(fullProcess.getOutputs().get(0).getProcess().isPresent());
        assertEquals(fullProcess.getIdentifier(), fullProcess.getOutputs().get(0).getProcess().get().getIdentifier());

        assertTrue(fullProcess.getOutputs().get(1).getName().isPresent());
        assertEquals("out2", fullProcess.getOutputs().get(1).getName().get());
        assertTrue(fullProcess.getOutputs().get(1).getType().isPresent());
        assertEquals(int.class, fullProcess.getOutputs().get(1).getType().get());
        assertTrue(fullProcess.getOutputs().get(1).getProcess().isPresent());
        assertEquals(fullProcess.getIdentifier(), fullProcess.getOutputs().get(1).getProcess().get().getIdentifier());

        assertTrue(fullProcess.getOutputs().get(2).getName().isPresent());
        assertEquals("out3", fullProcess.getOutputs().get(2).getName().get());
        assertTrue(fullProcess.getOutputs().get(2).getType().isPresent());
        assertEquals(String.class, fullProcess.getOutputs().get(2).getType().get());
        assertTrue(fullProcess.getOutputs().get(2).getProcess().isPresent());
        assertEquals(fullProcess.getIdentifier(), fullProcess.getOutputs().get(2).getProcess().get().getIdentifier());

        assertTrue(fullProcess.getVersion().isPresent());
        assertEquals("1.0.0", fullProcess.getVersion().get());

        LinkedHashMap<String, Object> data = new LinkedHashMap<>();
        data.put("in2", "toto");
        data.put("in3", new String[]{"t", "a", "t", "a"});
        assertTrue(fullProcess.execute(data));
        assertEquals(3, fullProcess.getResults().size());
        assertEquals("5698toto[t, a, t, a]3.56", fullProcess.getResults().get("out1").toString());
        assertEquals(4, fullProcess.getResults().get("out2"));
        assertEquals("toto", fullProcess.getResults().get("out3"));
    }

    @Test
    void badProcessTest() {
        //Test wrong constructor
        assertFalse(new Process(null, null, null, new LinkedHashMap<>(), null, null, new Closure<Object>(null) {
            @Override
            public int getMaximumNumberOfParameters() {
                return 2;
            }
        }).execute(new LinkedHashMap<>()));

        assertFalse(new Process(null, null, null, new LinkedHashMap<>(), null, null, null).execute(null));

        //Test execution with exception
        LinkedHashMap<String, Object> inputs = new LinkedHashMap<>();
        inputs.put("in", String.class);
        LinkedHashMap<String, Object> outputs = new LinkedHashMap<>();
        outputs.put("out", String.class);
        Process p = new Process(null, null, null, inputs, outputs, null, new Closure<Object>(null) {
            @Override
            public int getMaximumNumberOfParameters() {
                return 1;
            }

            @Override
            public Object call(Object... args) {
                Arrays.asList(args).forEach(System.out::println);
                return null;
            }
        });
        LinkedHashMap<String, Object> data = new LinkedHashMap<>();
        data.put("in", null);
        assertFalse(p.execute(data));

        //Test null input value
        inputs = new LinkedHashMap<>();
        inputs.put("in", String.class);
        outputs = new LinkedHashMap<>();
        outputs.put("out", String.class);
        p = new Process(null, null, null, inputs, outputs, null, new Closure<Object>(null) {
            @Override
            public int getMaximumNumberOfParameters() {
                return 1;
            }

            @Override
            public Object call(Object... args) {
                return null;
            }
        });
        data = new LinkedHashMap<>();
        data.put("in", null);
        assertFalse(p.execute(data));

        //Test no mandatory input
        data = new LinkedHashMap<>();
        data.put("in3", "toto");
        data.put("in", "toto");
        data.put(null, "toto");
        assertFalse(fullProcess.execute(data));

        //Test too much inputs
        data = new LinkedHashMap<>();
        data.put("in", "toto");
        data.put("in2", "toto");
        data.put("in3", "toto");
        data.put("in4", "toto");
        data.put("in5", "toto");
        data.put(null, "toto");
        assertFalse(fullProcess.call(data));

        //Test empty inputs
        assertFalse(fullProcess.execute(new LinkedHashMap<>()));
    }

    @Test
    void returnClosureTest() {
        String str = "{ o -> return 'toto' }";
        Closure<?> cl = (Closure<?>) new GroovyShell().evaluate(new GroovyCodeSource(str, "script", ""));
        LinkedHashMap<String, Object> inputs = new LinkedHashMap<>();
        inputs.put("o", String.class);
        LinkedHashMap<String, Object> outputs = new LinkedHashMap<>();
        outputs.put("o", String.class);

        IProcess process = new Process(null, null, null, inputs, null, null, cl);
        assertTrue(process.execute(inputs));
        assertTrue(process.getResults().containsKey("result"));
        assertEquals("toto", process.getResults().get("result"));

        process = new Process(null, null, null, inputs, outputs, null, cl);
        assertTrue(process.execute(inputs));
        assertTrue(process.getResults().containsKey("result"));
        assertEquals("toto", process.getResults().get("result"));

        str = "{ -> return [o1 : 'toto'] }";
        cl = (Closure<?>) new GroovyShell().evaluate(new GroovyCodeSource(str, "script", ""));
        process = new Process(null, null, null, null, outputs, null, cl);
        assertTrue(process.execute(null));
        assertTrue(process.getResults().containsKey("o1"));
        assertEquals("toto", process.getResults().get("o1"));

        outputs = new LinkedHashMap<>();
        outputs.put("o1", String.class);
        outputs.put("o2", String.class);
        str = "{ o1 -> return null }";
        cl = (Closure<?>) new GroovyShell().evaluate(new GroovyCodeSource(str, "script", ""));
        process = new Process(null, null, null, inputs, outputs, null, cl);
        assertFalse(process.execute(inputs));

        outputs = new LinkedHashMap<>();
        outputs.put("o1", String.class);
        outputs.put("o2", String.class);
        str = "{ o1 -> return 'yoyo' }";
        cl = (Closure<?>) new GroovyShell().evaluate(new GroovyCodeSource(str, "script", ""));
        process = new Process(null, null, null, inputs, outputs, null, cl);
        assertFalse(process.execute(inputs));

        outputs = new LinkedHashMap<>();
        outputs.put("o1", String.class);
        outputs.put("o2", String.class);
        str = "{ o1 -> return [o1 : 'otot', o3 : 'toto'] }";
        cl = (Closure<?>) new GroovyShell().evaluate(new GroovyCodeSource(str, "script", ""));
        process = new Process(null, null, null, inputs, outputs, null, cl);
        assertFalse(process.execute(inputs));
    }

    /**
     * Test the {@link Process#newInstance()} method.
     */
    @Test
    void newInstanceTest() {
        IProcess p = fullProcess.newInstance();
        assertEquals(fullProcess.getTitle(), p.getTitle());
        assertEquals(fullProcess.getDescription(), p.getDescription());
        assertEquals(fullProcess.getKeywords(), p.getKeywords());
        assertTrue(fullProcess.getInputs().stream().map(IInOutPut::getName)
                .allMatch(in -> p.getInputs().stream().map(IInOutPut::getName).anyMatch(in::equals)));
        assertTrue(fullProcess.getOutputs().stream().map(IInOutPut::getName)
                .allMatch(in -> p.getOutputs().stream().map(IInOutPut::getName).anyMatch(in::equals)));
        assertEquals(fullProcess.getVersion(), p.getVersion());
        assertNotEquals(fullProcess.getIdentifier(), p.getIdentifier());
    }

    /**
     * Test the {@link Process#copy()} method.
     */
    @Test
    void copyTest() {
        IProcess p = fullProcess.copy();
        assertEquals(fullProcess.getTitle(), p.getTitle());
        assertEquals(fullProcess.getDescription(), p.getDescription());
        assertEquals(fullProcess.getKeywords(), p.getKeywords());
        assertTrue(fullProcess.getInputs().stream().map(IInOutPut::getName)
                .allMatch(in -> p.getInputs().stream().map(IInOutPut::getName).anyMatch(in::equals)));
        assertTrue(fullProcess.getOutputs().stream().map(IInOutPut::getName)
                .allMatch(in -> p.getOutputs().stream().map(IInOutPut::getName).anyMatch(in::equals)));
        assertEquals(fullProcess.getVersion(), p.getVersion());
        assertEquals(fullProcess.getIdentifier(), p.getIdentifier());
    }

    /**
     * Test the {@link Process#invokeMethod(String, Object)} method.
     */
    @Test
    void invokeMethodTest() {
        assertEquals("title", fullProcess.invokeMethod("getTitle", null));
        assertEquals("1.0.0", fullProcess.invokeMethod("getVersion", null));

        assertNull(fullProcess.invokeMethod(null, null));
        assertNotNull(fullProcess.invokeMethod("getIdentifier", null));
    }

    /**
     * Test the {@link Process#getProperty(String)} method.
     */
    @Test
    void getPropertyTest() {
        assertTrue(fullProcess.getProperty("in1") instanceof IInput);
        assertTrue(fullProcess.getProperty("in2") instanceof IInput);
        assertTrue(fullProcess.getProperty("in3") instanceof IInput);
        assertTrue(fullProcess.getProperty("in4") instanceof IInput);
        assertTrue(fullProcess.getProperty("out1") instanceof IOutput);
        assertTrue(fullProcess.getProperty("out2") instanceof IOutput);
        assertTrue(fullProcess.getProperty("out3") instanceof IOutput);
        assertEquals("title", fullProcess.getProperty("title"));

        assertNull(fullProcess.getProperty(null));
        assertNotNull(fullProcess.getProperty("identifier"));
    }

    /**
     * Test the {@link Process#getProperty(String)} and {@link Process#setProperty(String, Object)} methods.
     */
    @Test
    void setPropertyTest() {
        assertEquals("title", fullProcess.getProperty("title"));
        fullProcess.setProperty("title", "toto");
        assertEquals("toto", fullProcess.getProperty("title"));
        fullProcess.setProperty("title", "title");
        assertEquals("title", fullProcess.getProperty("title"));
    }

    /**
     * Test the {@link Process#getMetaClass()} method.
     */
    @Test
    void metaClassTest() {
        assertEquals(InvokerHelper.getMetaClass(Process.class), fullProcess.getMetaClass());
        fullProcess.setMetaClass(InvokerHelper.getMetaClass(ProcessTest.class));
        assertEquals(InvokerHelper.getMetaClass(ProcessTest.class), fullProcess.getMetaClass());
        fullProcess.setMetaClass(null);
        assertEquals(InvokerHelper.getMetaClass(Process.class), fullProcess.getMetaClass());
        fullProcess.setMetaClass(InvokerHelper.getMetaClass(Process.class));
    }

    /**
     * Test the {@link Process#equals(Object)} method.
     */
    @Test
    void equalsTest() {
        assertNotEquals(fullProcess, "miniProcess");
        assertNotEquals(fullProcess, miniProcess);
        assertNotEquals(fullProcess, fullProcess.newInstance());
        assertNotEquals(fullProcess, null);
        assertNotEquals(miniProcess, miniProcess.newInstance());
        assertNotEquals(miniProcess, null);

        assertEquals(fullProcess, fullProcess);
        assertEquals(miniProcess, miniProcess);
    }
}
