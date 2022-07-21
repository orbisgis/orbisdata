/*
 * Bundle Commons is part of the OrbisGIS platform
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
 * Commons is distributed under LGPL 3 license.
 *
 * Copyright (C) 2019-2020 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * Commons is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Commons is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Commons. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.commons.utilities;

import org.junit.jupiter.api.Test;
import org.orbisgis.commons.utilities.CheckUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class dedicated to {@link CheckUtils} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (Lab-STICC UBS 2020)
 */
public class CheckUtilsTest {

    /**
     * Test the {@link CheckUtils#checkNotNull(Object)} and {@link CheckUtils#checkNotNull(Object, String)} methods.
     */
    @Test
    void checkNotNullTest() {
        Object notNullObj = "notNull";
        String nullMessage = null;
        String notNullMessage = "message";

        assertThrows(IllegalArgumentException.class, () -> CheckUtils.checkNotNull(null));
        assertThrows(IllegalArgumentException.class, () -> CheckUtils.checkNotNull(null, nullMessage));
        assertThrows(IllegalArgumentException.class, () -> CheckUtils.checkNotNull(null, notNullMessage));

        assertDoesNotThrow(() -> CheckUtils.checkNotNull(notNullObj));
        assertDoesNotThrow(() -> CheckUtils.checkNotNull(notNullObj, nullMessage));
        assertDoesNotThrow(() -> CheckUtils.checkNotNull(notNullObj, notNullMessage));
    }

    /**
     * Test the {@link CheckUtils#checkNotEmpty(Object[], String)}, {@link CheckUtils#checkNotEmpty(Collection)},
     * {@link CheckUtils#checkNotEmpty(Collection, String)}, {@link CheckUtils#checkNotEmpty(Object[])} methods.
     */
    @Test
    void checkNotEmptyTest() {
        Object[] notEmptyObj = {"notEmpty"};
        Object[] emptyObj = {};
        List<Object> notEmptyList = new ArrayList<>();
        notEmptyList.add("notEmpty");
        List<Object> emptyList = new ArrayList<>();
        String message = "message";

        assertThrows(IllegalArgumentException.class, () -> CheckUtils.checkNotEmpty((Collection<?>)null));
        assertThrows(IllegalArgumentException.class, () -> CheckUtils.checkNotEmpty((Collection<?>)null, message));
        assertThrows(IllegalArgumentException.class, () -> CheckUtils.checkNotEmpty((Collection<?>)null, null));
        assertThrows(IllegalArgumentException.class, () -> CheckUtils.checkNotEmpty((Object[])null));
        assertThrows(IllegalArgumentException.class, () -> CheckUtils.checkNotEmpty((Object[])null, message));
        assertThrows(IllegalArgumentException.class, () -> CheckUtils.checkNotEmpty((Object[])null, null));
        assertThrows(IllegalArgumentException.class, () -> CheckUtils.checkNotEmpty(emptyObj));
        assertThrows(IllegalArgumentException.class, () -> CheckUtils.checkNotEmpty(emptyObj, message));
        assertThrows(IllegalArgumentException.class, () -> CheckUtils.checkNotEmpty(emptyObj, null));
        assertThrows(IllegalArgumentException.class, () -> CheckUtils.checkNotEmpty(emptyList));
        assertThrows(IllegalArgumentException.class, () -> CheckUtils.checkNotEmpty(emptyList, message));
        assertThrows(IllegalArgumentException.class, () -> CheckUtils.checkNotEmpty(emptyList, null));

        assertDoesNotThrow(() -> CheckUtils.checkNotEmpty(notEmptyObj));
        assertDoesNotThrow(() -> CheckUtils.checkNotEmpty(notEmptyObj, message));
        assertDoesNotThrow(() -> CheckUtils.checkNotEmpty(notEmptyObj, null));
        assertDoesNotThrow(() -> CheckUtils.checkNotEmpty(notEmptyList));
        assertDoesNotThrow(() -> CheckUtils.checkNotEmpty(notEmptyList, message));
        assertDoesNotThrow(() -> CheckUtils.checkNotEmpty(notEmptyList, null));
    }
}
