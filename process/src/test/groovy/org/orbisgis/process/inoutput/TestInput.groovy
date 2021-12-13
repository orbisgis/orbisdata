package org.orbisgis.process.inoutput

import org.junit.jupiter.api.Test
import org.orbisgis.process.api.IProcess
import org.orbisgis.process.impl.inoutput.Input

import static org.orbisgis.process.ProcessManager.createFactory

/**
 * Test class dedicated to {@link Input} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC 2020)
 */
class TestInput {

    /**
     * Test the {@link Input#setName(String)} and {@link Input#getName()} method.
     */
    @Test
    void nameTest() {
        def input = new Input()
        assert !input.name
        assert input.name("name")
        assert input.name
        assert "name" == input.name
        input.name = null
        assert !input.name
    }

    /**
     * Test the {@link Input#setProcess(IProcess)} and {@link Input#getProcess()} method.
     */
    @Test
    void processTest() {
        def process = createFactory().create().getProcess()
        def input = new Input()
        assert !input.process
        assert input.process(process)
        assert input.process
        assert process == input.process
        input.process = null
        assert !input.process
    }

    /**
     * Test the {@link Input#setTitle(String)} and {@link Input#getTitle()} method.
     */
    @Test
    void titleTest() {
        def input = new Input()
        assert !input.title
        assert input.title("title")
        assert input.title
        assert "title", input.title
        input.title = null
        assert !input.title
    }

    /**
     * Test the {@link Input#setDescription(String)} and {@link Input#getDescription()} method.
     */
    @Test
    void descriptionTest() {
        def input = new Input()
        assert !input.description
        assert input.description("descr")
        assert input.description
        assert "descr" == input.description
        input.description = null
        assert !input.description
    }

    /**
     * Test the {@link Input#setKeywords(String[])} and {@link Input#getKeywords()} method.
     */
    @Test
    void keywordsTest() {
        def input = new Input()
        assert !input.keywords
        assert input.keywords(new String[]{"key1", "key2"})
        assert input.keywords
        assert new String[]{"key1", "key2"} == input.keywords
        input.keywords = null
        assert !input.keywords
    }

    /**
     * Test the {@link Input#setType(Class)} and {@link Input#getType()} method.
     */
    @Test
    void typeTest() {
        def input = new Input()
        assert !input.type
        assert input.type(Integer)
        assert input.type
        assert Integer, input.type
        input.type = null
        assert !input.type
    }

    /**
     * Test the {@link Input#toString()} method.
     */
    @Test
    void toStringTest() {
        def process = createFactory().create().process
        def input = new Input()
        assert !input.toString()

        input.name = "name"
        assert "name", input.toString()

        input.process = process
        assert "name:" + process.identifier == input.toString()

        input.name = null
        assert ":" + process.identifier == input.toString()
    }

    /**
     * Test the {@link Input#optional(Object)}, {@link Input#isOptional()}, {@link Input#getDefaultValue()} methods.
     */
    @Test
    void optionalTest() {
        def input = new Input()
        assert !input.defaultValue
        assert !input.optional
        assert input.mandatory
        assert input.optional("toto")
        assert input.defaultValue
        assert "toto" == input.defaultValue
        assert input.optional
        assert !input.mandatory
    }

    /**
     * Test the {@link Input#mandatory()} and {@link Input#isMandatory()} methods.
     */
    @Test
    void mandatoryTest() {
        def input = new Input()
        assert !input.defaultValue
        assert !input.optional
        assert input.mandatory
        assert input.mandatory()
        assert !input.defaultValue
        assert !input.optional
        assert input.mandatory
    }

    /**
     * Test the {@link Input#equals(Object)} method.
     */
    @Test
    void equalsTest() {
        def p1 = createFactory().create().process
        def p2 = createFactory().create().process
        def in1 = new Input().name("toto").process(p1)
        def in2 = new Input().name("toto").process(p1)
        def in3 = new Input().name("toto").process(p2)
        def in4 = new Input().name("toto")
        def in5 = new Input().name("tata").process(p1)
        def in6 = new Input()

        assert in1 == in2

        assert in1 != in3
        assert in1 != in4
        assert in1 != in5
        assert in1 != in6
        assert in1 != "in3"
        assert in1 != null
    }

    /**
     * Test the {@link Input#copy()} method.
     */
    @Test
    void copyTest() {
        def p1 = createFactory().create().process
        def p2 = createFactory().create().process
        def in1 = new Input().name("toto").process(p1)
        def in2 = new Input().name("toto").process(p1)
        def in3 = new Input().name("toto").process(p2)
        def in4 = new Input().name("toto")
        def in5 = new Input().name("tata").process(p1)
        def in6 = new Input()

        assert in1 == in1.copy()
        assert in1 !== in1.copy()

        assert in2 == in2.copy()
        assert in2 !== in2.copy()

        assert in3 == in3.copy()
        assert in3 !== in3.copy()

        assert in4 == in4.copy()
        assert in4 !== in4.copy()

        assert in5 == in5.copy()
        assert in5 !== in5.copy()

        assert in6 == in6.copy()
        assert in6 !== in6.copy()
    }
}
