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
 * terms of the GNU General License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * ProcessManager is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General License for more details.
 *
 * You should have received a copy of the GNU General License along with
 * ProcessManager. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.process.check

import org.codehaus.groovy.runtime.InvokerHelper
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.orbisgis.commons.annotations.Nullable
import org.orbisgis.process.api.IProcess
import org.orbisgis.process.api.check.IProcessCheck
import org.orbisgis.process.api.inoutput.IInOutPut
import org.orbisgis.process.ProcessManager
import org.orbisgis.process.check.ProcessCheck
import org.orbisgis.process.inoutput.Input
import org.orbisgis.process.inoutput.Output

import static org.orbisgis.process.api.check.IProcessCheck.Action.CONTINUE
import static org.orbisgis.process.api.check.IProcessCheck.Action.STOP

/**
 * Test class dedicated to {@link org.orbisgis.process.check.ProcessCheck} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2020)
 */
class TestProcessCheck {

    private static IProcess PROCESS

    @Nullable
    private static IProcess NULL_PROCESS

    @BeforeAll
    static void beforeAll() {
        PROCESS = ProcessManager.createFactory()
                .create {
                        outputs "out1": String
                        inputs "in1": String, "in2": String, "in3": String
                        run {in1, in2, in3 -> return [out1:in1+in2+in3]} }
        NULL_PROCESS = null
    }

    @Test
    void getProcessTest() {
        assert new ProcessCheck(PROCESS).process
        assert !new ProcessCheck(NULL_PROCESS).process
    }

    /**
     * Test the {@link ProcessCheck#fail()}, {@link ProcessCheck#success()},
     * {@link ProcessCheck#onFail(String)}, {@link ProcessCheck#onFail(IProcessCheck.Action, String)},
     * {@link ProcessCheck#onSuccess(String)}, {@link ProcessCheck#onSuccess(IProcessCheck.Action, String)} methods.
     */
    @Test
    void failSuccessTest() {
        def message = "message"
        def check = new ProcessCheck(PROCESS)

        assert check.fail()
        assert !check.success()

        check.onFail(null, message)
        assert check.fail()
        check.onFail(null, null)
        assert check.fail()
        check.onFail(STOP, message)
        assert check.fail()
        check.onFail(STOP, null)
        assert check.fail()
        check.onFail(CONTINUE, message)
        assert !check.fail()
        check.onFail(CONTINUE, null)
        assert !check.fail()
        check.onFail(message)
        assert check.fail()
        check.onFail(null)
        assert check.fail()

        check.onSuccess(null, message)
        assert check.success()
        check.onSuccess(null, null)
        assert check.success()
        check.onSuccess(STOP, message)
        assert check.success()
        check.onSuccess(STOP, null)
        assert check.success()
        check.onSuccess(CONTINUE, message)
        assert !check.success()
        check.onSuccess(CONTINUE, null)
        assert !check.success()
        check.onSuccess(message)
        assert !check.success()
        check.onSuccess(null)
        assert !check.success()
    }

    /**
     * Test the {@link ProcessCheck#run(LinkedHashMap)}, {@link ProcessCheck#setClosure(Closure)},
     * {@link ProcessCheck#setInOutPuts(IInOutPut...)} methods.
     */
    @Test
    void runTest() {
        def map = [in1: "a", in2: "b"]
        def cl = {in1, in2, in3 -> return in1+in2+in3 == 'abc'}

        def emptyInOutPuts = []
        def inOutPuts = [
                new Input().process(PROCESS).name("in1").optional("a"),
                new Input().process(PROCESS).name("in2"),
                new Input().process(PROCESS).name("in3").optional("c")]

        def processCheck = new ProcessCheck(PROCESS)

        //Ensure the check is not run
        processCheck.onFail(STOP, null)
        processCheck.onSuccess(STOP, null)

        processCheck.closure = null
        processCheck.inOutPuts = null
        assert !processCheck.run()
        assert !processCheck.run([:])
        assert !processCheck.run(map)

        processCheck.closure = null
        processCheck.inOutPuts = emptyInOutPuts
        assert !processCheck.run()
        assert !processCheck.run([:])
        assert !processCheck.run(map)

        processCheck.closure = null
        processCheck.inOutPuts = inOutPuts
        assert !processCheck.run()
        assert !processCheck.run([:])
        assert processCheck.run(map)

        processCheck.closure = cl
        processCheck.inOutPuts = null
        assert processCheck.run()
        assert processCheck.run([:])
        assert processCheck.run(map)

        processCheck.closure = cl
        processCheck.inOutPuts = emptyInOutPuts
        assert processCheck.run()
        assert processCheck.run([:])
        assert processCheck.run(map)

        processCheck.closure = cl
        processCheck.inOutPuts = inOutPuts
        assert processCheck.run()
        assert processCheck.run([:])
        assert processCheck.run(map)


        //Test that check without closure fail when required
        def inOutPutsOpt = [
                new Input().process(PROCESS).name("in1").optional("a"),
                new Input().process(PROCESS).name("in2").optional("b"),
                new Input().process(PROCESS).name("in3").optional("c"),
                new Output().process(PROCESS).name("out1")]
        def noNameInOutPuts = [
                new Input().process(PROCESS).name("in1").optional("a"),
                new Input().process(PROCESS).optional("b"),
                new Input().process(PROCESS).optional("c"),
                new Output().process(PROCESS).name("out1")]

        def mapMissingInputs = [in1: "a"]
        def mapInvalidInputs = [in1: "a", in2: "a", in3: "a"]
        def mapInputs = [in1: "a", in2: "b", in3: "c"]

        processCheck.onFail(STOP, null)
        processCheck.onSuccess(CONTINUE, null)

        processCheck.closure = null
        processCheck.inOutPuts = noNameInOutPuts
        assert processCheck.run(map)

        processCheck.closure = null
        processCheck.inOutPuts = inOutPuts
        assert processCheck.run(mapInputs)

        processCheck.closure = null
        processCheck.inOutPuts = inOutPutsOpt
        assert processCheck.run(mapMissingInputs)

        processCheck.closure = null
        processCheck.inOutPuts = inOutPutsOpt
        assert processCheck.run(mapInvalidInputs)


        //Test that check with closure fail when required
        def inOutPutsNullProcess = [
                new Input().process(PROCESS).name("in1").optional("a"),
                new Input().name("in2").optional("b"),
                new Input().process(PROCESS).name("in3").optional("c"),
                new Output().process(PROCESS).name("out1")]

        processCheck.closure = cl
        processCheck.inOutPuts = inOutPutsNullProcess
        assert processCheck.run(mapInputs)

        processCheck.closure = cl
        processCheck.inOutPuts = noNameInOutPuts
        assert processCheck.run(mapInputs)

        processCheck.closure = cl
        processCheck.inOutPuts = noNameInOutPuts
        assert processCheck.run(null)

        processCheck.closure = cl
        processCheck.inOutPuts = inOutPuts
        assert processCheck.run(mapInvalidInputs)

        processCheck.closure = {in1, in2, in3 -> return in1+in2+in3}
        processCheck.inOutPuts = inOutPuts
        assert processCheck.run(mapInputs)

        processCheck.closure = {in1, in2, in3 -> return false}
        processCheck.inOutPuts = inOutPuts
        assert processCheck.run(mapInputs)

        processCheck.closure = null
        processCheck.inOutPuts = [
                new Input().process(PROCESS).name("in1").optional("a"),
                new Input().process(PROCESS).name("in2").optional("b"),
                new Input().process(PROCESS).name("in3").optional("c")]
        assert !processCheck.run(mapInputs)

        processCheck.closure = cl
        processCheck.inOutPuts = [
                new Input().process(PROCESS).name("in1"),
                new Input().process(PROCESS).name("in2"),
                new Input().process(PROCESS).name("in3")]
        assert !processCheck.run(mapInputs)

        assert PROCESS.execute(mapInputs)
        processCheck.closure = {out1 -> return out1 == 'abc'}
        processCheck.inOutPuts = [new Output().process(PROCESS).name("out1")]
        assert !processCheck.run(mapInputs)
    }

    /**
     * Test the {@link ProcessCheck#setProperty(String, Object)}, {@link ProcessCheck#getProperty(String)} methods.
     */
    @Test
    void propertyTest() {
        def processCheck = new ProcessCheck(PROCESS)

        processCheck.inOutPuts == null
        def obj = processCheck.inOutPuts
        assert  obj !instanceof Optional
        assert  obj instanceof LinkedList
        assert  obj.isEmpty()

        processCheck.inOutPuts = [new Input()]
        obj = processCheck.inOutPuts
        assert  obj !instanceof Optional
        assert  obj instanceof LinkedList
        assert 1 == obj.size()

        obj = processCheck.inOutPuts
        assert obj

        processCheck.metaClass = InvokerHelper.getMetaClass(getClass())
        obj = processCheck.metaClass
        assert obj

        processCheck.metaClass = null
        processCheck.inOutPuts = null
        obj = processCheck.inOutPuts
        assert obj
    }

    /**
     * Test the {@link ProcessCheck#invokeMethod(String, Object)} methods.
     */
    @Test
    void invokeMethodTest() {
        def processCheck = new ProcessCheck(PROCESS)

        assert !processCheck.invokeMethod(null, "value")
        assert !processCheck.invokeMethod(null, null)
        assert !processCheck.invokeMethod("setClosure", null)
        assert !processCheck.invokeMethod("getClosure", null)

        assert !processCheck.invokeMethod("setClosure", {in1, in2, in3 -> return in1+in2+in3 == 'abc'})
        assert processCheck.invokeMethod("getClosure", null)
    }
}
