package org.orbisgis.process.inoutput

import org.junit.jupiter.api.Test
import org.orbisgis.process.api.IProcess
import org.orbisgis.process.impl.inoutput.InOutPut

import static org.orbisgis.process.ProcessManager.createFactory as factory

/**
 * Test class dedicated to {@link InOutPut} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC 2020)
 */
class TestInOutPut {

    /**
     * Test the {@link InOutPut#setName(String)} and {@link InOutPut#getName()} method.
     */
    @Test
    void nameTest() {
        def dummyInOutPut = new DummyInOutPut()
        assert !dummyInOutPut.name
        assert dummyInOutPut.name("name") instanceof InOutPut
        assert dummyInOutPut.name
        assert "name" == dummyInOutPut.name
        dummyInOutPut.name = null
        assert !dummyInOutPut.name
    }

    /**
     * Test the {@link InOutPut#setProcess(IProcess)} and {@link InOutPut#getProcess()} method.
     */
    @Test
    void processTest() {
        def process = factory().create().process
        def dummyInOutPut = new DummyInOutPut()
        assert !dummyInOutPut.process
        assert dummyInOutPut.process(process) instanceof InOutPut
        assert dummyInOutPut.process
        assert process == dummyInOutPut.process
        dummyInOutPut.process = null
        assert !dummyInOutPut.process
    }

    /**
     * Test the {@link InOutPut#setTitle(String)} and {@link InOutPut#getTitle()} method.
     */
    @Test
    void titleTest() {
        def dummyInOutPut = new DummyInOutPut()
        assert !dummyInOutPut.title
        assert dummyInOutPut.title("title") instanceof InOutPut
        assert dummyInOutPut.title
        assert "title" == dummyInOutPut.title
        dummyInOutPut.title = null
        assert !dummyInOutPut.title
    }

    /**
     * Test the {@link InOutPut#setDescription(String)} and {@link InOutPut#getDescription()} method.
     */
    @Test
    void descriptionTest() {
        def dummyInOutPut = new DummyInOutPut()
        assert !dummyInOutPut.description
        assert dummyInOutPut.description("descr") instanceof InOutPut
        assert dummyInOutPut.description
        assert "descr", dummyInOutPut.description
        dummyInOutPut.description = null
        assert !dummyInOutPut.description
    }

    /**
     * Test the {@link InOutPut#setKeywords(String[])} and {@link InOutPut#getKeywords()} method.
     */
    @Test
    void keywordsTest() {
        def dummyInOutPut = new DummyInOutPut()
        assert !dummyInOutPut.keywords
        assert dummyInOutPut.keywords(new String[]{"key1", "key2"}) instanceof InOutPut
        assert dummyInOutPut.keywords
        assert new String[]{"key1", "key2"} == dummyInOutPut.keywords
        dummyInOutPut.keywords = null
        assert !dummyInOutPut.keywords
    }

    /**
     * Test the {@link InOutPut#setType(Class)} and {@link InOutPut#getType()} method.
     */
    @Test
    void typeTest() {
        def dummyInOutPut = new DummyInOutPut()
        assert !dummyInOutPut.type
        assert dummyInOutPut.type(Integer.class) instanceof InOutPut
        assert dummyInOutPut.type
        assert Integer.class, dummyInOutPut.type
        dummyInOutPut.type = null
        assert !dummyInOutPut.type
    }

    /**
     * Test the {@link InOutPut#toString()} method.
     */
    @Test
    void toStringTest() {
        def process = factory().create().getProcess()
        def dummyInOutPut = new DummyInOutPut()
        assert !dummyInOutPut.toString()

        dummyInOutPut.name "name"
        assert "name", dummyInOutPut.toString()

        dummyInOutPut.process = process
        assert "name:" + process.getIdentifier() == dummyInOutPut.toString()

        dummyInOutPut.name = null
        assert ":" + process.getIdentifier() == dummyInOutPut.toString()
    }
}
