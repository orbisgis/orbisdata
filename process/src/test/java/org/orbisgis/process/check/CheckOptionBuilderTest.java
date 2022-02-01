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
package org.orbisgis.process.check;

import org.junit.jupiter.api.Test;
import org.orbisgis.process.api.check.IProcessCheck;
import org.orbisgis.process.check.CheckOptionBuilder;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class dedicated to {@link CheckOptionBuilder} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019-2020)
 */
public class CheckOptionBuilderTest {

    /**
     * Test the {@link CheckOptionBuilder#stopOnFail()}, {@link CheckOptionBuilder#stopOnFail(String)},
     * {@link CheckOptionBuilder#continueOnFail()}, {@link CheckOptionBuilder#continueOnFail(String)}methods.
     */
    @Test
    public void onFailTest() {
        String failMessage = "Fail Message";
        String successMessage = "Success Message";

        DummyProcessCheck dummy = new DummyProcessCheck(null);
        CheckOptionBuilder builder = new CheckOptionBuilder(dummy);
        assertNotNull(builder.stopOnFail());
        assertEquals(IProcessCheck.Action.STOP, dummy.failAction);
        assertNull(dummy.failMessage);

        dummy = new DummyProcessCheck(null);
        builder = new CheckOptionBuilder(dummy);
        assertNotNull(builder.stopOnFail(failMessage));
        assertEquals(IProcessCheck.Action.STOP, dummy.failAction);
        assertEquals(failMessage, dummy.failMessage);

        dummy = new DummyProcessCheck(null);
        builder = new CheckOptionBuilder(dummy);
        assertNotNull(builder.stopOnFail());
        assertEquals(IProcessCheck.Action.STOP, dummy.failAction);
        assertNull(dummy.failMessage);

        dummy = new DummyProcessCheck(null);
        builder = new CheckOptionBuilder(dummy);
        assertNotNull(builder.stopOnFail(null));
        assertEquals(IProcessCheck.Action.STOP, dummy.failAction);
        assertNull(dummy.failMessage);

        dummy = new DummyProcessCheck(null);
        builder = new CheckOptionBuilder(dummy);
        assertNotNull(builder.stopOnFail());
        assertEquals(IProcessCheck.Action.STOP, dummy.failAction);
        assertNull(dummy.failMessage);

        dummy = new DummyProcessCheck(null);
        builder = new CheckOptionBuilder(dummy);
        assertNotNull(builder.continueOnFail(successMessage));
        assertEquals(IProcessCheck.Action.CONTINUE, dummy.failAction);
        assertEquals(successMessage, dummy.failMessage);

        dummy = new DummyProcessCheck(null);
        builder = new CheckOptionBuilder(dummy);
        assertNotNull(builder.continueOnFail());
        assertEquals(IProcessCheck.Action.CONTINUE, dummy.failAction);
        assertNull(dummy.failMessage);

        dummy = new DummyProcessCheck(null);
        builder = new CheckOptionBuilder(dummy);
        assertNotNull(builder.continueOnFail(null));
        assertEquals(IProcessCheck.Action.CONTINUE, dummy.failAction);
        assertNull(dummy.failMessage);

        dummy = new DummyProcessCheck(null);
        builder = new CheckOptionBuilder(dummy);
        assertNotNull(builder.continueOnFail());
        assertEquals(IProcessCheck.Action.CONTINUE, dummy.failAction);
        assertNull(dummy.failMessage);
    }

    /**
     * Test the {@link CheckOptionBuilder#stopOnSuccess()}, {@link CheckOptionBuilder#stopOnSuccess(String)},
     * {@link CheckOptionBuilder#continueOnSuccess()}, {@link CheckOptionBuilder#continueOnSuccess(String)}methods.
     */
    @Test
    public void onSuccessTest() {
        String failMessage = "Fail Message";
        String successMessage = "Success Message";

        DummyProcessCheck dummy = new DummyProcessCheck(null);
        CheckOptionBuilder builder = new CheckOptionBuilder(dummy);
        assertNotNull(builder.stopOnSuccess());
        assertEquals(IProcessCheck.Action.STOP, dummy.successAction);
        assertNull(dummy.successMessage);

        dummy = new DummyProcessCheck(null);
        builder = new CheckOptionBuilder(dummy);
        assertNotNull(builder.stopOnSuccess(failMessage));
        assertEquals(IProcessCheck.Action.STOP, dummy.successAction);
        assertEquals(failMessage, dummy.successMessage);

        dummy = new DummyProcessCheck(null);
        builder = new CheckOptionBuilder(dummy);
        assertNotNull(builder.stopOnSuccess());
        assertEquals(IProcessCheck.Action.STOP, dummy.successAction);
        assertNull(dummy.successMessage);

        dummy = new DummyProcessCheck(null);
        builder = new CheckOptionBuilder(dummy);
        assertNotNull(builder.stopOnSuccess(null));
        assertEquals(IProcessCheck.Action.STOP, dummy.successAction);
        assertNull(dummy.successMessage);

        dummy = new DummyProcessCheck(null);
        builder = new CheckOptionBuilder(dummy);
        assertNotNull(builder.stopOnSuccess());
        assertEquals(IProcessCheck.Action.STOP, dummy.successAction);
        assertNull(dummy.successMessage);

        dummy = new DummyProcessCheck(null);
        builder = new CheckOptionBuilder(dummy);
        assertNotNull(builder.continueOnSuccess(successMessage));
        assertEquals(IProcessCheck.Action.CONTINUE, dummy.successAction);
        assertEquals(successMessage, dummy.successMessage);

        dummy = new DummyProcessCheck(null);
        builder = new CheckOptionBuilder(dummy);
        assertNotNull(builder.continueOnSuccess());
        assertEquals(IProcessCheck.Action.CONTINUE, dummy.successAction);
        assertNull(dummy.successMessage);

        dummy = new DummyProcessCheck(null);
        builder = new CheckOptionBuilder(dummy);
        assertNotNull(builder.continueOnSuccess(null));
        assertEquals(IProcessCheck.Action.CONTINUE, dummy.successAction);
        assertNull(dummy.successMessage);

        dummy = new DummyProcessCheck(null);
        builder = new CheckOptionBuilder(dummy);
        assertNotNull(builder.continueOnSuccess());
        assertEquals(IProcessCheck.Action.CONTINUE, dummy.successAction);
        assertNull(dummy.successMessage);
    }
}
