package org.orbisgis.process.inoutput;

import groovy.lang.MetaClass;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.junit.jupiter.api.Test;
import org.orbisgis.process.api.IProcess;
import org.orbisgis.process.ProcessManager;
import org.orbisgis.process.impl.inoutput.InOutPut;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class dedicated to {@link InOutPut} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC 2020)
 */
public class InOutPutTest {

    /**
     * Test the {@link InOutPut#setName(String)} and {@link InOutPut#getName()} method.
     */
    @Test
    void nameTest() {
        DummyInOutPut dummyInOutPut = new DummyInOutPut();
        assertFalse(dummyInOutPut.getName().isPresent());
        assertTrue(dummyInOutPut.name("name") instanceof InOutPut);
        assertTrue(dummyInOutPut.getName().isPresent());
        assertEquals("name", dummyInOutPut.getName().get());
        dummyInOutPut.setName(null);
        assertFalse(dummyInOutPut.getName().isPresent());
    }

    /**
     * Test the {@link InOutPut#setProcess(IProcess)} and {@link InOutPut#getProcess()} method.
     */
    @Test
    void processTest() {
        IProcess process = ProcessManager.createFactory().create().getProcess();
        DummyInOutPut dummyInOutPut = new DummyInOutPut();
        assertFalse(dummyInOutPut.getProcess().isPresent());
        assertTrue(dummyInOutPut.process(process) instanceof InOutPut);
        assertTrue(dummyInOutPut.getProcess().isPresent());
        assertEquals(process, dummyInOutPut.getProcess().get());
        dummyInOutPut.setProcess(null);
        assertFalse(dummyInOutPut.getProcess().isPresent());
    }

    /**
     * Test the {@link InOutPut#setTitle(String)} and {@link InOutPut#getTitle()} method.
     */
    @Test
    void titleTest() {
        DummyInOutPut dummyInOutPut = new DummyInOutPut();
        assertFalse(dummyInOutPut.getTitle().isPresent());
        assertTrue(dummyInOutPut.title("title") instanceof InOutPut);
        assertTrue(dummyInOutPut.getTitle().isPresent());
        assertEquals("title", dummyInOutPut.getTitle().get());
        dummyInOutPut.setTitle(null);
        assertFalse(dummyInOutPut.getTitle().isPresent());
    }

    /**
     * Test the {@link InOutPut#setDescription(String)} and {@link InOutPut#getDescription()} method.
     */
    @Test
    void descriptionTest() {
        DummyInOutPut dummyInOutPut = new DummyInOutPut();
        assertFalse(dummyInOutPut.getDescription().isPresent());
        assertTrue(dummyInOutPut.description("descr") instanceof InOutPut);
        assertTrue(dummyInOutPut.getDescription().isPresent());
        assertEquals("descr", dummyInOutPut.getDescription().get());
        dummyInOutPut.setDescription(null);
        assertFalse(dummyInOutPut.getDescription().isPresent());
    }

    /**
     * Test the {@link InOutPut#setKeywords(String[])} and {@link InOutPut#getKeywords()} method.
     */
    @Test
    void keywordsTest() {
        DummyInOutPut dummyInOutPut = new DummyInOutPut();
        assertFalse(dummyInOutPut.getKeywords().isPresent());
        assertTrue(dummyInOutPut.keywords(new String[]{"key1", "key2"}) instanceof InOutPut);
        assertTrue(dummyInOutPut.getKeywords().isPresent());
        assertArrayEquals(new String[]{"key1", "key2"}, dummyInOutPut.getKeywords().get());
        dummyInOutPut.setKeywords(null);
        assertFalse(dummyInOutPut.getKeywords().isPresent());
    }

    /**
     * Test the {@link InOutPut#setType(Class)} and {@link InOutPut#getType()} method.
     */
    @Test
    void typeTest() {
        DummyInOutPut dummyInOutPut = new DummyInOutPut();
        assertFalse(dummyInOutPut.getType().isPresent());
        assertTrue(dummyInOutPut.type(Integer.class) instanceof InOutPut);
        assertTrue(dummyInOutPut.getType().isPresent());
        assertEquals(Integer.class, dummyInOutPut.getType().get());
        dummyInOutPut.setType(null);
        assertFalse(dummyInOutPut.getType().isPresent());
    }

    /**
     * Test the {@link InOutPut#toString()} method.
     */
    @Test
    void toStringTest() {
        IProcess process = ProcessManager.createFactory().create().getProcess();
        DummyInOutPut dummyInOutPut = new DummyInOutPut();
        assertTrue(dummyInOutPut.toString().isEmpty());

        dummyInOutPut.setName("name");
        assertEquals("name", dummyInOutPut.toString());

        dummyInOutPut.setProcess(process);
        assertEquals("name:" + process.getIdentifier(), dummyInOutPut.toString());

        dummyInOutPut.setName(null);
        assertEquals(":" + process.getIdentifier(), dummyInOutPut.toString());
    }

    /**
     * Test the {@link InOutPut#setMetaClass(MetaClass)} and {@link InOutPut#getMetaClass()} methods.
     */
    @Test
    void metaClassTest() {
        DummyInOutPut dummyInOutPut = new DummyInOutPut();
        assertEquals(InvokerHelper.getMetaClass(DummyInOutPut.class), dummyInOutPut.getMetaClass());
        dummyInOutPut.setMetaClass(null);
        assertNotNull(dummyInOutPut.getMetaClass());
        dummyInOutPut.setMetaClass(InvokerHelper.getMetaClass(this.getClass()));
        assertEquals(InvokerHelper.getMetaClass(this.getClass()), dummyInOutPut.getMetaClass());
    }

    /**
     * Test the {@link InOutPut#setMetaClass(MetaClass)} and {@link InOutPut#getMetaClass()} methods.
     */
    @Test
    void propertyTest() {
        DummyInOutPut dummyInOutPut = new DummyInOutPut();
        dummyInOutPut.setProperty(null, null);
        dummyInOutPut.setName("toto");

        assertNull(dummyInOutPut.getProperty(null));
        assertTrue(dummyInOutPut.getName().isPresent());
        assertEquals("toto", dummyInOutPut.getName().get());

        dummyInOutPut.setProperty("name", null);
        assertFalse(dummyInOutPut.getName().isPresent());

        dummyInOutPut.setProperty("name", "tata");
        assertTrue(dummyInOutPut.getName().isPresent());
        assertEquals("tata", dummyInOutPut.getName().get());
        assertEquals("tata", dummyInOutPut.getProperty("name"));

        dummyInOutPut.setMetaClass(null);
        dummyInOutPut.setProperty("name", "tata");
        assertNull(dummyInOutPut.getProperty(null));
    }

    /**
     * Test the {@link InOutPut#invokeMethod(String, Object)} method.
     */
    @Test
    void invokeMethodTest() {
        DummyInOutPut dummyInOutPut = new DummyInOutPut();
        dummyInOutPut.setName("name");

        assertNull(dummyInOutPut.invokeMethod(null, null));
        assertEquals("name", dummyInOutPut.invokeMethod("getName", null));
        dummyInOutPut.setNotOptional("toto");
        assertEquals("toto", dummyInOutPut.invokeMethod("getNotOptional", null));
    }
}
