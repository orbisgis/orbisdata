package org.orbisgis.orbisdata.processmanager.process.inoutput;

import groovy.lang.MetaClass;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.junit.jupiter.api.Test;
import org.orbisgis.orbisdata.processmanager.api.IProcess;
import org.orbisgis.orbisdata.processmanager.process.ProcessManager;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class dedicated to {@link Output} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC 2020)
 */
public class OutputTest {

    /**
     * Test the {@link Output#setName(String)} and {@link Output#getName()} method.
     */
    @Test
    void nameTest() {
        Output output = new  Output();
        assertFalse(output.getName().isPresent());
        assertNotNull(output.name("name"));
        assertTrue(output.getName().isPresent());
        assertEquals("name", output.getName().get());
        output.setName(null);
        assertFalse(output.getName().isPresent());
    }

    /**
     * Test the {@link Output#setProcess(IProcess)} and {@link Output#getProcess()} method.
     */
    @Test
    void processTest() {
        IProcess process = ProcessManager.createFactory().create().getProcess();
        Output output = new  Output();
        assertFalse(output.getProcess().isPresent());
        assertNotNull(output.process(process));
        assertTrue(output.getProcess().isPresent());
        assertEquals(process, output.getProcess().get());
        output.setProcess(null);
        assertFalse(output.getProcess().isPresent());
    }

    /**
     * Test the {@link Output#setTitle(String)} and {@link Output#getTitle()} method.
     */
    @Test
    void titleTest() {
        Output output = new  Output();
        assertFalse(output.getTitle().isPresent());
        assertNotNull(output.title("title"));
        assertTrue(output.getTitle().isPresent());
        assertEquals("title", output.getTitle().get());
        output.setTitle(null);
        assertFalse(output.getTitle().isPresent());
    }

    /**
     * Test the {@link Output#setDescription(String)} and {@link Output#getDescription()} method.
     */
    @Test
    void descriptionTest() {
        Output output = new  Output();
        assertFalse(output.getDescription().isPresent());
        assertNotNull(output.description("descr"));
        assertTrue(output.getDescription().isPresent());
        assertEquals("descr", output.getDescription().get());
        output.setDescription(null);
        assertFalse(output.getDescription().isPresent());
    }

    /**
     * Test the {@link Output#setKeywords(String[])} and {@link Output#getKeywords()} method.
     */
    @Test
    void keywordsTest() {
        Output output = new  Output();
        assertFalse(output.getKeywords().isPresent());
        assertNotNull(output.keywords(new String[]{"key1", "key2"}));
        assertTrue(output.getKeywords().isPresent());
        assertArrayEquals(new String[]{"key1", "key2"}, output.getKeywords().get());
        output.setKeywords(null);
        assertFalse(output.getKeywords().isPresent());
    }

    /**
     * Test the {@link Output#setType(Class)} and {@link Output#getType()} method.
     */
    @Test
    void typeTest() {
        Output output = new  Output();
        assertFalse(output.getType().isPresent());
        assertNotNull(output.type(Integer.class));
        assertTrue(output.getType().isPresent());
        assertEquals(Integer.class, output.getType().get());
        output.setType(null);
        assertFalse(output.getType().isPresent());
    }

    /**
     * Test the {@link Output#toString()} method.
     */
    @Test
    void toStringTest() {
        IProcess process = ProcessManager.createFactory().create().getProcess();
        Output output = new  Output();
        assertTrue(output.toString().isEmpty());

        output.setName("name");
        assertEquals("name", output.toString());

        output.setProcess(process);
        assertEquals("name:" + process.getIdentifier(), output.toString());

        output.setName(null);
        assertEquals(":" + process.getIdentifier(), output.toString());
    }

    /**
     * Test the {@link InOutPut#setMetaClass(MetaClass)} and {@link InOutPut#getMetaClass()} methods.
     */
    @Test
    void metaClassTest(){
        Output output = new Output();
        assertEquals(InvokerHelper.getMetaClass(Output.class), output.getMetaClass());
        output.setMetaClass(null);
        assertNotNull(output.getMetaClass());
        output.setMetaClass(InvokerHelper.getMetaClass(this.getClass()));
        assertEquals(InvokerHelper.getMetaClass(this.getClass()), output.getMetaClass());
    }
}
