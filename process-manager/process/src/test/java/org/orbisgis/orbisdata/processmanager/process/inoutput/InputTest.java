package org.orbisgis.orbisdata.processmanager.process.inoutput;

import groovy.lang.MetaClass;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.junit.jupiter.api.Test;
import org.orbisgis.orbisdata.processmanager.api.IProcess;
import org.orbisgis.orbisdata.processmanager.process.ProcessManager;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class dedicated to {@link Input} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC 2020)
 */
public class InputTest {

    /**
     * Test the {@link Input#setName(String)} and {@link Input#getName()} method.
     */
    @Test
    void nameTest() {
        Input input = new Input();
        assertFalse(input.getName().isPresent());
        assertNotNull(input.name("name"));
        assertTrue(input.getName().isPresent());
        assertEquals("name", input.getName().get());
        input.setName(null);
        assertFalse(input.getName().isPresent());
    }

    /**
     * Test the {@link Input#setProcess(IProcess)} and {@link Input#getProcess()} method.
     */
    @Test
    void processTest() {
        IProcess process = ProcessManager.createFactory().create().getProcess();
        Input input = new Input();
        assertFalse(input.getProcess().isPresent());
        assertNotNull(input.process(process));
        assertTrue(input.getProcess().isPresent());
        assertEquals(process, input.getProcess().get());
        input.setProcess(null);
        assertFalse(input.getProcess().isPresent());
    }

    /**
     * Test the {@link Input#setTitle(String)} and {@link Input#getTitle()} method.
     */
    @Test
    void titleTest() {
        Input input = new Input();
        assertFalse(input.getTitle().isPresent());
        assertNotNull(input.title("title"));
        assertTrue(input.getTitle().isPresent());
        assertEquals("title", input.getTitle().get());
        input.setTitle(null);
        assertFalse(input.getTitle().isPresent());
    }

    /**
     * Test the {@link Input#setDescription(String)} and {@link Input#getDescription()} method.
     */
    @Test
    void descriptionTest() {
        Input input = new Input();
        assertFalse(input.getDescription().isPresent());
        assertNotNull(input.description("descr"));
        assertTrue(input.getDescription().isPresent());
        assertEquals("descr", input.getDescription().get());
        input.setDescription(null);
        assertFalse(input.getDescription().isPresent());
    }

    /**
     * Test the {@link Input#setKeywords(String[])} and {@link Input#getKeywords()} method.
     */
    @Test
    void keywordsTest() {
        Input input = new Input();
        assertFalse(input.getKeywords().isPresent());
        assertNotNull(input.keywords(new String[]{"key1", "key2"}));
        assertTrue(input.getKeywords().isPresent());
        assertArrayEquals(new String[]{"key1", "key2"}, input.getKeywords().get());
        input.setKeywords(null);
        assertFalse(input.getKeywords().isPresent());
    }

    /**
     * Test the {@link Input#setType(Class)} and {@link Input#getType()} method.
     */
    @Test
    void typeTest() {
        Input input = new Input();
        assertFalse(input.getType().isPresent());
        assertNotNull(input.type(Integer.class));
        assertTrue(input.getType().isPresent());
        assertEquals(Integer.class, input.getType().get());
        input.setType(null);
        assertFalse(input.getType().isPresent());
    }

    /**
     * Test the {@link Input#toString()} method.
     */
    @Test
    void toStringTest() {
        IProcess process = ProcessManager.createFactory().create().getProcess();
        Input input = new Input();
        assertTrue(input.toString().isEmpty());

        input.setName("name");
        assertEquals("name", input.toString());

        input.setProcess(process);
        assertEquals("name:" + process.getIdentifier(), input.toString());

        input.setName(null);
        assertEquals(":" + process.getIdentifier(), input.toString());
    }

    /**
     * Test the {@link Input#optional(Object)}, {@link Input#isOptional()}, {@link Input#getDefaultValue()} methods.
     */
    @Test
    void optionalTest() {
        Input input = new Input();
        assertFalse(input.getDefaultValue().isPresent());
        assertFalse(input.isOptional());
        assertTrue(input.isMandatory());
        assertNotNull(input.optional("toto"));
        assertTrue(input.getDefaultValue().isPresent());
        assertEquals("toto", input.getDefaultValue().get());
        assertTrue(input.isOptional());
        assertFalse(input.isMandatory());
    }

    /**
     * Test the {@link Input#mandatory()} and {@link Input#isMandatory()} methods.
     */
    @Test
    void mandatoryTest() {
        Input input = new Input();
        assertFalse(input.getDefaultValue().isPresent());
        assertFalse(input.isOptional());
        assertTrue(input.isMandatory());
        assertNotNull(input.mandatory());
        assertFalse(input.getDefaultValue().isPresent());
        assertFalse(input.isOptional());
        assertTrue(input.isMandatory());
    }

    /**
     * Test the {@link InOutPut#setMetaClass(MetaClass)} and {@link InOutPut#getMetaClass()} methods.
     */
    @Test
    void metaClassTest(){
        Input input = new Input();
        assertEquals(InvokerHelper.getMetaClass(Input.class), input.getMetaClass());
        input.setMetaClass(null);
        assertNotNull(input.getMetaClass());
        input.setMetaClass(InvokerHelper.getMetaClass(this.getClass()));
        assertEquals(InvokerHelper.getMetaClass(this.getClass()), input.getMetaClass());
    }

    /**
     * Test the {@link Input#equals(Object)} method.
     */
    @Test
    void equalsTest() {
        IProcess p1 = ProcessManager.createFactory().create().getProcess();
        IProcess p2 = ProcessManager.createFactory().create().getProcess();
        Input in1 = new Input().name("toto").process(p1);
        Input in2 = new Input().name("toto").process(p1);
        Input in3 = new Input().name("toto").process(p2);
        Input in4 = new Input().name("toto");
        Input in5 = new Input().name("tata").process(p1);
        Input in6 = new Input();

        assertEquals(in1, in2);

        assertNotEquals(in1, in3);
        assertNotEquals(in1, in4);
        assertNotEquals(in1, in5);
        assertNotEquals(in1, in6);
        assertNotEquals(in1, "in3");
        assertNotEquals(in1, null);
    }

    /**
     * Test the {@link Input#copy()} method.
     */
    @Test
    void copyTest() {
        IProcess p1 = ProcessManager.createFactory().create().getProcess();
        IProcess p2 = ProcessManager.createFactory().create().getProcess();
        Input in1 = new Input().name("toto").process(p1);
        Input in2 = new Input().name("toto").process(p1);
        Input in3 = new Input().name("toto").process(p2);
        Input in4 = new Input().name("toto");
        Input in5 = new Input().name("tata").process(p1);
        Input in6 = new Input();

        assertEquals(in1, in1.copy());
        assertNotSame(in1, in1.copy());

        assertEquals(in2, in2.copy());
        assertNotSame(in2, in2.copy());

        assertEquals(in3, in3.copy());
        assertNotSame(in3, in3.copy());

        assertEquals(in4, in4.copy());
        assertNotSame(in4, in4.copy());

        assertEquals(in5, in5.copy());
        assertNotSame(in5, in5.copy());

        assertEquals(in6, in6.copy());
        assertNotSame(in6, in6.copy());
    }
}
