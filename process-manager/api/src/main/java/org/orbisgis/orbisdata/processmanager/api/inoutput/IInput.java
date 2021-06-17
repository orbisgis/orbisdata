/*
 * Bundle ProcessManager API is part of the OrbisGIS platform
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
 * ProcessManager API is distributed under LGPL 3 license.
 *
 * Copyright (C) 2018 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * ProcessManager API is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * ProcessManager API is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * ProcessManager API. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.orbisdata.processmanager.api.inoutput;

import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.commons.annotations.Nullable;

import java.util.Optional;

/**
 * This interface defines the methods dedicated the wrapping of input.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC 2019-2020)
 */
public interface IInput extends IInOutPut {

    /**
     * Make the {@link IInput} optional.
     *
     * @param dfltValue Default value to use if no value is provided.
     * @return The {@link IInput} to continue its configuration.
     */
    IInOutPut optional(Object dfltValue);

    /**
     * Return true if the input is optional, false otherwise.
     *
     * @return True if the input is optional, false otherwise.
     */
    boolean isOptional();

    /**
     * Make the {@link IInput} mandatory.
     *
     * @return The {@link IInput} to continue its configuration.
     */
    IInOutPut mandatory();

    /**
     * Return true if the input is mandatory, false otherwise.
     *
     * @return True if the input is mandatory, false otherwise.
     */
    boolean isMandatory();

    /**
     * Return the default value of the input. If mandatory, return null.
     *
     * @return The default value of the input.
     */
    Optional<Object> getDefaultValue();

    /**
     * Returns a copy the the current object.
     *
     * @return A copy of the current object.
     */
    IInput copy();
}
