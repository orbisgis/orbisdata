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
 * Copyright (C) 2018-2020 CNRS (Lab-STICC UMR CNRS 6285)
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


import java.util.Collection;

/**
 * Utility class for checking values, parameters ...
 *
 * @author Sylvain PALOMINOS (Lab-STICC UBS 2020)
 */
public class CheckUtils {

    /**
     * Check that the given value is not null. If null, throws an {@link IllegalArgumentException}.
     *
     * @param value Value to check.
     * @throws IllegalArgumentException Exception thrown in case of null value.
     */
    public static void checkNotNull(Object value) throws IllegalArgumentException {
        if (value == null) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Check that the given value is not null. If null, throws an {@link IllegalArgumentException} with the given
     * message.
     *
     * @param value   Value to check.
     * @param message Message to put in the {@link IllegalArgumentException}.
     * @throws IllegalArgumentException Exception thrown in case of null value with the given message.
     */
    public static void checkNotNull(Object value, String message) throws IllegalArgumentException {
        if (message == null) {
            checkNotNull(value);
        } else if (value == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Check that the given {@link Collection} is not empty. If empty, throws an {@link IllegalArgumentException}.
     *
     * @param value {@link Collection} to check.
     * @throws IllegalArgumentException Exception thrown in case of empty {@link Collection}.
     */
    public static void checkNotEmpty(Collection<?> value) throws IllegalArgumentException {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Check that the given {@link Collection} is not empty. If empty, throws an {@link IllegalArgumentException} with the given
     * message.
     *
     * @param value   {@link Collection} to check.
     * @param message Message to put in the {@link IllegalArgumentException}.
     * @throws IllegalArgumentException Exception thrown in case of empty {@link Collection} with the given message.
     */
    public static void checkNotEmpty(Collection<?> value,String message) throws IllegalArgumentException {
        if (message == null) {
            checkNotEmpty(value);
        } else if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Check that the given array is not empty. If empty, throws an {@link IllegalArgumentException}.
     *
     * @param array Array to check.
     * @throws IllegalArgumentException Exception thrown in case of empty array.
     */
    public static void checkNotEmpty(Object[] array) throws IllegalArgumentException {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Check that the given array is not empty. If empty, throws an {@link IllegalArgumentException} with the given
     * message.
     *
     * @param array   Array to check.
     * @param message Message to put in the {@link IllegalArgumentException}.
     * @throws IllegalArgumentException Exception thrown in case of empty array with the given message.
     */
    public static void checkNotEmpty(Object[] array, String message) throws IllegalArgumentException {
        if (message == null) {
            checkNotEmpty(array);
        } else if (array == null || array.length == 0) {
            throw new IllegalArgumentException(message);
        }
    }
}
