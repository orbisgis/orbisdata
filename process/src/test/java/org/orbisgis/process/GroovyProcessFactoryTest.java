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
import groovy.lang.GroovyObject;
import groovy.lang.GroovyShell;
import groovy.lang.MetaClass;
import org.codehaus.groovy .runtime.InvokerHelper;
import org.codehaus.groovy .runtime.metaclass.MissingPropertyExceptionNoStack;
import org.junit.jupiter.api.Test;
import org.orbisgis.process.api.IProcess;
import org.orbisgis.process.api.IProcessManager;
import org.slf4j.Logger;
import org.slf4j.Marker;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class dedicated to {@link GroovyProcessFactory} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC 2019- 2020)
 */
public class GroovyProcessFactoryTest {

    /**
     * Test the {@link GroovyProcessFactory#isDefault()} and {@link GroovyProcessFactory#isLocked()} methods.
     */
    @Test
    void testAttributes() {
        GroovyProcessFactory pf = new DummyFactory();
        assertFalse(pf.isDefault());
        assertFalse(pf.isLocked());
    }

    /**
     * Test the {@link GroovyProcessFactory#registerProcess(IProcess)} and {@link GroovyProcessFactory#getProcess(String)} methods.
     */
    @Test
    void testRegister() {
        IProcess process = new Process(null, null, null, null, null, null, null);

        DummyFactory pf = new DummyFactory();
        pf.registerProcess(process);
        assertTrue(pf.getProcess(process.getIdentifier()).isPresent());
    }

    /**
     * Test the {@link GroovyProcessFactory#create()} and {@link GroovyProcessFactory#create(Closure)} methods.
     */
    @Test
    void testCreate() {
        GroovyProcessFactory pf1 = new DummyFactory();
        assertNotNull(pf1.create());
        Optional<IProcess> opt = pf1.create(null);
        assertFalse(opt.isPresent());

        String string = "({\n" +
                "            title \"simple process\"\n" +
                "            description \"description\"\n" +
                "            keywords \"key1\", \"key2\"\n" +
                "            inputs inputA: String, inputB: String\n" +
                "            outputs outputA: String\n" +
                "            version \"version\"\n" +
                "            run { inputA, inputB -> [outputA: inputA + inputB] }\n" +
                "        })";
        Closure<?> cl = (Closure<?>) new GroovyShell().evaluate(string);
        opt = pf1.create(cl);
        assertTrue(opt.isPresent());

        IProcess process = opt.get();
        assertTrue(process.getTitle().isPresent());
        assertEquals("simple process", process.getTitle().get());
        assertTrue(process.getDescription().isPresent());
        assertEquals("description", process.getDescription().get());
        assertTrue(process.getVersion().isPresent());
        assertEquals("version", process.getVersion().get());
        assertTrue(process.getKeywords().isPresent());
        assertArrayEquals(new String[]{"key1", "key2"}, process.getKeywords().get());
        assertEquals(2, process.getInputs().size());
        assertEquals(1, process.getOutputs().size());
    }

    /**
     * Test the {@link GroovyProcessFactory#invokeMethod(String, Object)} method.
     */
    @Test
    void invokeMethodTest() {
        DummyFactory factory = new DummyFactory();
        assertNull(factory.invokeMethod(null, null));
        assertEquals(false, factory.invokeMethod("isLocked", null));
        assertNull(factory.invokeMethod("getProcess", null));
        factory.setMetaClass(null);
        assertNull(factory.invokeMethod(null, null));
        assertEquals(false, factory.invokeMethod("isLocked", null));
        assertNull(factory.invokeMethod("getProcess", null));
        assertNull(factory.invokeMethod("create", new Object[]{null}));
    }

    /**
     * Test the {@link GroovyProcessFactory#getProcessManager()} and {@link GroovyProcessFactory#setProcessManager(IProcessManager)} method.
     */
    @Test
    void getProcessManagerTest() {
        DummyFactory factory = new DummyFactory();
        assertFalse(factory.getProcessManager().isPresent());
        ProcessManager pm = new ProcessManager();
        factory.setProcessManager(pm);
        assertTrue(factory.getProcessManager().isPresent());
        assertEquals(pm, factory.getProcessManager().get());
        factory.setProcessManager(null);
        assertFalse(factory.getProcessManager().isPresent());
    }

    /**
     * Test the {@link GroovyProcessFactory#prefix(String)}, {@link GroovyProcessFactory#prefix(String, String)},
     * {@link GroovyProcessFactory#postfix(String)}, {@link GroovyProcessFactory#postfix(String, String)} methods.
     */
    @Test
    void prePostFixTest() {
        assertEquals("tata_toto", GroovyProcessFactory.prefix("tata", "toto"));
        assertEquals("tata_null", GroovyProcessFactory.prefix("tata", null));
        assertEquals("toto", GroovyProcessFactory.prefix(null, "toto"));
        assertEquals("toto", GroovyProcessFactory.prefix("", "toto"));
        assertEquals("tata_toto", GroovyProcessFactory.postfix("tata", "toto"));
        assertEquals("null_toto", GroovyProcessFactory.postfix(null, "toto"));
        assertEquals("tata", GroovyProcessFactory.postfix("tata", null));
        assertEquals("tata", GroovyProcessFactory.postfix("tata", ""));
        assertTrue(GroovyProcessFactory.prefix("toto").endsWith("toto"));
        assertTrue(GroovyProcessFactory.postfix("toto").startsWith("toto"));
    }

    /**
     * Test the {@link GroovyProcessFactory#getProperty(String)} method.
     */
    @Test
    void getPropertyTest() {
        DummyFactory factory = new DummyFactory();
        factory.registerProcess(new Process("toto", null, null, null, null, null, null, null));
        assertNotNull(factory.getProperty("toto"));
        assertNull(factory.getProperty(null));
        assertNull(factory.getProperty("processManager"));
        assertNotNull(factory.getProperty("metaClass"));
        assertThrows(MissingPropertyExceptionNoStack.class, () -> factory.getProperty("pm"));
    }

    private static class DummyFactory extends GroovyProcessFactory {
        @Override
       
        public Object run() {
            return null;
        }
    }

    private static class DummyLogger implements Logger, GroovyObject {

        private MetaClass metaClass = InvokerHelper.getMetaClass(this.getClass());

        public String infoS;
        public Throwable infoE;
        public String debugS;
        public Throwable debugE;
        public String warnS;
        public Throwable warnE;
        public String errorS;
        public Throwable errorE;

        @Override
        public String getName() {
            return null;
        }

        @Override
        public boolean isTraceEnabled() {
            return false;
        }

        @Override
        public void trace(String s) {

        }

        @Override
        public void trace(String s, Object o) {

        }

        @Override
        public void trace(String s, Object o, Object o1) {

        }

        @Override
        public void trace(String s, Object... objects) {

        }

        @Override
        public void trace(String s, Throwable throwable) {

        }

        @Override
        public boolean isTraceEnabled(Marker marker) {
            return false;
        }

        @Override
        public void trace(Marker marker, String s) {

        }

        @Override
        public void trace(Marker marker, String s, Object o) {

        }

        @Override
        public void trace(Marker marker, String s, Object o, Object o1) {

        }

        @Override
        public void trace(Marker marker, String s, Object... objects) {

        }

        @Override
        public void trace(Marker marker, String s, Throwable throwable) {

        }

        @Override
        public boolean isDebugEnabled() {
            return false;
        }

        @Override
        public void debug(String s) {
            debugS = s;
        }

        @Override
        public void debug(String s, Object o) {
        }

        @Override
        public void debug(String s, Object o, Object o1) {

        }

        @Override
        public void debug(String s, Object... objects) {

        }

        @Override
        public void debug(String s, Throwable throwable) {
            debugS = s;
            debugE = throwable;
        }

        @Override
        public boolean isDebugEnabled(Marker marker) {
            return false;
        }

        @Override
        public void debug(Marker marker, String s) {

        }

        @Override
        public void debug(Marker marker, String s, Object o) {

        }

        @Override
        public void debug(Marker marker, String s, Object o, Object o1) {

        }

        @Override
        public void debug(Marker marker, String s, Object... objects) {

        }

        @Override
        public void debug(Marker marker, String s, Throwable throwable) {

        }

        @Override
        public boolean isInfoEnabled() {
            return false;
        }

        @Override
        public void info(String s) {
            infoS = s;
        }

        @Override
        public void info(String s, Object o) {

        }

        @Override
        public void info(String s, Object o, Object o1) {

        }

        @Override
        public void info(String s, Object... objects) {

        }

        @Override
        public void info(String s, Throwable throwable) {
            infoS = s;
            infoE = throwable;
        }

        @Override
        public boolean isInfoEnabled(Marker marker) {
            return false;
        }

        @Override
        public void info(Marker marker, String s) {

        }

        @Override
        public void info(Marker marker, String s, Object o) {

        }

        @Override
        public void info(Marker marker, String s, Object o, Object o1) {

        }

        @Override
        public void info(Marker marker, String s, Object... objects) {

        }

        @Override
        public void info(Marker marker, String s, Throwable throwable) {

        }

        @Override
        public boolean isWarnEnabled() {
            return false;
        }

        @Override
        public void warn(String s) {
            warnS = s;
        }

        @Override
        public void warn(String s, Object o) {

        }

        @Override
        public void warn(String s, Object... objects) {

        }

        @Override
        public void warn(String s, Object o, Object o1) {

        }

        @Override
        public void warn(String s, Throwable throwable) {
            warnS = s;
            warnE = throwable;
        }

        @Override
        public boolean isWarnEnabled(Marker marker) {
            return false;
        }

        @Override
        public void warn(Marker marker, String s) {

        }

        @Override
        public void warn(Marker marker, String s, Object o) {

        }

        @Override
        public void warn(Marker marker, String s, Object o, Object o1) {

        }

        @Override
        public void warn(Marker marker, String s, Object... objects) {

        }

        @Override
        public void warn(Marker marker, String s, Throwable throwable) {

        }

        @Override
        public boolean isErrorEnabled() {
            return false;
        }

        @Override
        public void error(String s) {
            errorS = s;
        }

        @Override
        public void error(String s, Object o) {

        }

        @Override
        public void error(String s, Object o, Object o1) {

        }

        @Override
        public void error(String s, Object... objects) {

        }

        @Override
        public void error(String s, Throwable throwable) {
            errorS = s;
            errorE = throwable;
        }

        @Override
        public boolean isErrorEnabled(Marker marker) {
            return false;
        }

        @Override
        public void error(Marker marker, String s) {

        }

        @Override
        public void error(Marker marker, String s, Object o) {

        }

        @Override
        public void error(Marker marker, String s, Object o, Object o1) {

        }

        @Override
        public void error(Marker marker, String s, Object... objects) {

        }

        @Override
        public void error(Marker marker, String s, Throwable throwable) {

        }

        @Override
        public MetaClass getMetaClass() {
            return metaClass;
        }

        @Override
        public void setMetaClass(MetaClass metaClass) {
            this.metaClass = metaClass != null ? metaClass : InvokerHelper.getMetaClass(this.getClass());
        }
    }
}
