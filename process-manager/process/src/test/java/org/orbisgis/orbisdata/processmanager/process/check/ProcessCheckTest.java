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
package org.orbisgis.orbisdata.processmanager.process.check;

import groovy.lang.Closure;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.orbisgis.commons.annotations.Nullable;
import org.orbisgis.orbisdata.processmanager.api.IProcess;
import org.orbisgis.orbisdata.processmanager.api.check.IProcessCheck;
import org.orbisgis.orbisdata.processmanager.api.inoutput.IInOutPut;
import org.orbisgis.orbisdata.processmanager.process.ProcessManager;
import org.orbisgis.orbisdata.processmanager.process.inoutput.Input;
import org.orbisgis.orbisdata.processmanager.process.inoutput.Output;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.orbisgis.orbisdata.processmanager.api.check.IProcessCheck.Action.CONTINUE;
import static org.orbisgis.orbisdata.processmanager.api.check.IProcessCheck.Action.STOP;

/**
 * Test class dedicated to {@link ProcessCheck} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2020)
 */
public class ProcessCheckTest {

    private static IProcess PROCESS;

    @Nullable
    private static IProcess NULL_PROCESS;

    @BeforeAll
    public static void beforeAll() {
        LinkedHashMap<String, Object> outputs = new LinkedHashMap<>();
        outputs.put("out1", new Output());

        LinkedHashMap<String, Object> inputs = new LinkedHashMap<>();
        inputs.put("in1", new Input());
        inputs.put("in2", new Input());
        inputs.put("in3", new Input());

        String str = "{in1, in2, in3 -> return [out1:in1+in2+in3]}";
        Closure<?> cl = (Closure<?>) new GroovyShell().evaluate(new GroovyCodeSource(str, "script", ""));

        PROCESS = ProcessManager.createFactory()
                .create()
                .outputs(outputs)
                .inputs(inputs)
                .run(cl)
                .getProcess();
        NULL_PROCESS = null;
    }

    /**
     * Test the {@link ProcessCheck#getProcess()} method.
     */
    @Test
    public void getProcessTest() {
        assertTrue(new ProcessCheck(PROCESS).getProcess().isPresent());
        assertFalse(new ProcessCheck(NULL_PROCESS).getProcess().isPresent());
    }

    /**
     * Test the {@link ProcessCheck#fail()}, {@link ProcessCheck#success()},
     * {@link ProcessCheck#onFail(String)}, {@link ProcessCheck#onFail(IProcessCheck.Action, String)},
     * {@link ProcessCheck#onSuccess(String)}, {@link ProcessCheck#onSuccess(IProcessCheck.Action, String)} methods.
     */
    @Test
    public void failSuccessTest() {
        String message = "message";
        ProcessCheck check = new ProcessCheck(PROCESS);

        assertTrue(check::fail);
        assertFalse(check::success);

        check.onFail(null, message);
        assertTrue(check::fail);
        check.onFail(null, null);
        assertTrue(check::fail);
        check.onFail(STOP, message);
        assertTrue(check::fail);
        check.onFail(STOP, null);
        assertTrue(check::fail);
        check.onFail(CONTINUE, message);
        assertFalse(check::fail);
        check.onFail(CONTINUE, null);
        assertFalse(check::fail);
        check.onFail(message);
        assertTrue(check::fail);
        check.onFail(null);
        assertTrue(check::fail);

        check.onSuccess(null, message);
        assertTrue(check::success);
        check.onSuccess(null, null);
        assertTrue(check::success);
        check.onSuccess(STOP, message);
        assertTrue(check::success);
        check.onSuccess(STOP, null);
        assertTrue(check::success);
        check.onSuccess(CONTINUE, message);
        assertFalse(check::success);
        check.onSuccess(CONTINUE, null);
        assertFalse(check::success);
        check.onSuccess(message);
        assertFalse(check::success);
        check.onSuccess(null);
        assertFalse(check::success);
    }

    /**
     * Test the {@link ProcessCheck#run(LinkedHashMap)}, {@link ProcessCheck#setClosure(Closure)},
     * {@link ProcessCheck#setInOutPuts(IInOutPut...)} methods.
     */
    @Test
    public void runTest() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("in1", "a");
        map.put("in2", "b");

        String str = "{in1, in2, in3 -> return in1+in2+in3 == 'abc'}";
        Closure<?> cl = (Closure<?>) new GroovyShell().evaluate(new GroovyCodeSource(str, "script", ""));

        IInOutPut[] emptyInOutPuts = new IInOutPut[]{};
        IInOutPut[] inOutPuts = new IInOutPut[]{
                new Input(PROCESS, "in1").optional("a"),
                new Input(PROCESS, "in2"),
                new Input(PROCESS, "in3").optional("c")};

        ProcessCheck processCheck = new ProcessCheck(PROCESS);

        //Ensure the check is not run
        processCheck.onFail(STOP, null);
        processCheck.onSuccess(STOP, null);

        processCheck.setClosure(null);
        assertFalse(processCheck.getClosure().isPresent());
        processCheck.setInOutPuts((IInOutPut[]) null);
        assertTrue(processCheck.getInOutPuts().isPresent());
        assertTrue(processCheck.getInOutPuts().get().isEmpty());
        assertFalse(processCheck.run(null));
        assertFalse(processCheck.run(new LinkedHashMap<>()));
        assertFalse(processCheck.run(map));

        processCheck.setClosure(null);
        processCheck.setInOutPuts(emptyInOutPuts);
        assertFalse(processCheck.run(null));
        assertFalse(processCheck.run(new LinkedHashMap<>()));
        assertFalse(processCheck.run(map));

        processCheck.setClosure(null);
        processCheck.setInOutPuts(inOutPuts);
        assertFalse(processCheck.run(null));
        assertFalse(processCheck.run(new LinkedHashMap<>()));
        assertTrue(processCheck.run(map));

        processCheck.setClosure(cl);
        processCheck.setInOutPuts((IInOutPut[]) null);
        assertTrue(processCheck.run(null));
        assertTrue(processCheck.run(new LinkedHashMap<>()));
        assertTrue(processCheck.run(map));

        processCheck.setClosure(cl);
        processCheck.setInOutPuts(emptyInOutPuts);
        assertTrue(processCheck.run(null));
        assertTrue(processCheck.run(new LinkedHashMap<>()));
        assertTrue(processCheck.run(map));

        processCheck.setClosure(cl);
        assertTrue(processCheck.getClosure().isPresent());
        assertEquals(cl, processCheck.getClosure().get());
        processCheck.setInOutPuts(inOutPuts);
        assertTrue(processCheck.getInOutPuts().isPresent());
        assertEquals(3, processCheck.getInOutPuts().get().size());
        assertTrue(processCheck.run(null));
        assertTrue(processCheck.run(new LinkedHashMap<>()));
        assertTrue(processCheck.run(map));


        //Test that check without closure fail when required
        IInOutPut[] inOutPutsOpt = new IInOutPut[]{
                new Input(PROCESS, "in1").optional("a"),
                new Input(PROCESS, "in2").optional("b"),
                new Input(PROCESS, "in3").optional("c"),
                new Output(PROCESS, "out1")};
        IInOutPut[] noNameInOutPuts = new IInOutPut[]{
                new Input(PROCESS, "in1").optional("a"),
                new Input(PROCESS, null).optional("b"),
                new Input(PROCESS, null).optional("c"),
                new Output(PROCESS, "out1")};

        LinkedHashMap<String, Object> mapMissingInputs = new LinkedHashMap<>();
        mapMissingInputs.put("in1", "a");

        LinkedHashMap<String, Object> mapInvalidInputs = new LinkedHashMap<>();
        mapInvalidInputs.put("in1", "a");
        mapInvalidInputs.put("in2", "a");
        mapInvalidInputs.put("in3", "a");

        LinkedHashMap<String, Object> mapInputs = new LinkedHashMap<>();
        mapInputs.put("in1", "a");
        mapInputs.put("in2", "b");
        mapInputs.put("in3", "c");

        processCheck.onFail(STOP, null);
        processCheck.onSuccess(CONTINUE, null);

        processCheck.setClosure(null);
        processCheck.setInOutPuts(noNameInOutPuts);
        assertTrue(processCheck.run(map));

        processCheck.setClosure(null);
        processCheck.setInOutPuts(inOutPuts);
        assertTrue(processCheck.run(mapInputs));

        processCheck.setClosure(null);
        processCheck.setInOutPuts(inOutPutsOpt);
        assertTrue(processCheck.run(mapMissingInputs));

        processCheck.setClosure(null);
        processCheck.setInOutPuts(inOutPutsOpt);
        assertTrue(processCheck.run(mapInvalidInputs));


        //Test that check with closure fail when required
        IInOutPut[] inOutPutsNullProcess = new IInOutPut[]{
                new Input(PROCESS, "in1").optional("a"),
                new Input(null, "in2").optional("b"),
                new Input(PROCESS, "in3").optional("c"),
                new Output(PROCESS, "out1")};

        processCheck.setClosure(cl);
        processCheck.setInOutPuts(inOutPutsNullProcess);
        assertTrue(processCheck.run(mapInputs));

        processCheck.setClosure(cl);
        processCheck.setInOutPuts(noNameInOutPuts);
        assertTrue(processCheck.run(mapInputs));

        processCheck.setClosure(cl);
        processCheck.setInOutPuts(noNameInOutPuts);
        assertTrue(processCheck.run(null));

        processCheck.setClosure(cl);
        processCheck.setInOutPuts(inOutPuts);
        assertTrue(processCheck.run(mapInvalidInputs));

        String str2 = "{in1, in2, in3 -> return in1+in2+in3}";
        Closure<?> badCl = (Closure<?>) new GroovyShell().evaluate(new GroovyCodeSource(str2, "script", ""));

        processCheck.setClosure(badCl);
        processCheck.setInOutPuts(inOutPuts);
        assertTrue(processCheck.run(mapInputs));

        String str3 = "{in1, in2, in3 -> return false}";
        Closure<?> falseCl = (Closure<?>) new GroovyShell().evaluate(new GroovyCodeSource(str3, "script", ""));

        processCheck.setClosure(falseCl);
        processCheck.setInOutPuts(inOutPuts);
        assertTrue(processCheck.run(mapInputs));

        processCheck.setClosure(null);
        processCheck.setInOutPuts(
                new Input(PROCESS, "in1").optional("a"),
                new Input(PROCESS, "in2").optional("b"),
                new Input(PROCESS, "in3").optional("c"));
        assertFalse(processCheck.run(mapInputs));

        processCheck.setClosure(cl);
        processCheck.setInOutPuts(
                new Input(PROCESS, "in1"),
                new Input(PROCESS, "in2"),
                new Input(PROCESS, "in3"));
        assertFalse(processCheck.run(mapInputs));

        String str4 = "{out1 -> return out1== 'abc'}";
        Closure<?> outCl = (Closure<?>) new GroovyShell().evaluate(new GroovyCodeSource(str4, "script", ""));

        assertTrue(PROCESS.execute(mapInputs));
        processCheck.setClosure(outCl);
        processCheck.setInOutPuts(new Output(PROCESS, "out1"));
        assertFalse(processCheck.run(mapInputs));
    }

    /**
     * Test the {@link ProcessCheck#setProperty(String, Object)}, {@link ProcessCheck#getProperty(String)} methods.
     */
    @Test
    public void propertyTest() {
        ProcessCheck processCheck = new ProcessCheck(PROCESS);

        assertDoesNotThrow(() -> processCheck.setProperty(null, "value"));

        processCheck.setProperty("inOutPuts", null);
        Object obj = processCheck.getProperty("inOutPuts");
        assertFalse(obj instanceof Optional);
        assertTrue(obj instanceof LinkedList);
        assertTrue(((LinkedList<?>) obj).isEmpty());

        processCheck.setProperty("inOutPuts", new IInOutPut[]{new Input()});
        obj = processCheck.getProperty("inOutPuts");
        assertFalse(obj instanceof Optional);
        assertTrue(obj instanceof LinkedList);
        assertEquals(1, ((LinkedList<?>)obj).size());

        processCheck.setMetaClass(null);
        processCheck.setProperty("inOutPuts", null);
        obj = processCheck.getProperty("inOutPuts");
        assertNull(obj);

        processCheck.setMetaClass(InvokerHelper.getMetaClass(processCheck.getClass()));
        obj = processCheck.getProperty("inOutPuts");
        assertNotNull(obj);

        processCheck.setProperty("metaClass", InvokerHelper.getMetaClass(getClass()));
        obj = processCheck.getProperty("metaClass");
        assertNotNull(obj);
    }

    /**
     * Test the {@link ProcessCheck#invokeMethod(String, Object)} methods.
     */
    @Test
    public void invokeMethodTest() {
        ProcessCheck processCheck = new ProcessCheck(PROCESS);

        assertNull(processCheck.invokeMethod(null, "value"));
        assertNull(processCheck.invokeMethod(null, null));
        assertNull(processCheck.invokeMethod("setClosure", null));
        assertNull(processCheck.invokeMethod("getClosure", null));

        String str = "{in1, in2, in3 -> return in1+in2+in3 == 'abc'}";
        Closure<?> cl = (Closure<?>) new GroovyShell().evaluate(new GroovyCodeSource(str, "script", ""));

        assertNull(processCheck.invokeMethod("setClosure", cl));
        assertNotNull(processCheck.invokeMethod("getClosure", null));

        processCheck.setMetaClass(null);
        assertNull(processCheck.invokeMethod(null, "value"));
        assertNull(processCheck.invokeMethod(null, null));
        assertNull(processCheck.invokeMethod("setClosure", null));
        assertNull(processCheck.invokeMethod("getClosure", null));
        assertNull(processCheck.invokeMethod("setClosure", cl));
        assertNull(processCheck.invokeMethod("getClosure", null));
    }
}
