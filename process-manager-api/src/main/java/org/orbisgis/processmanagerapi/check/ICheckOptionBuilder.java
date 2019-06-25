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
 * ProcessManager API is distributed under GPL 3 license.
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
package org.orbisgis.processmanagerapi.check;

/**
 * Interface for the definition of the getProcess check execution options.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public interface ICheckOptionBuilder {

    /**
     * Make the check log the given message and stop the program on fail .
     *
     * @param message Message to log.
     *
     * @return A {@link ICheckOptionBuilder} to continue the check building.
     */
    ICheckOptionBuilder stopOnFail(String message);

    /**
     * Make the check log the given message and stop the program on success .
     *
     * @param message Message to log.
     *
     * @return A {@link ICheckOptionBuilder} to continue the check building.
     */
    ICheckOptionBuilder stopOnSuccess(String message);

    /**
     * Make the check log the given message and continue the program on fail .
     *
     * @param message Message to log.
     *
     * @return A {@link ICheckOptionBuilder} to continue the check building.
     */
    ICheckOptionBuilder continueOnFail(String message);

    /**
     * Make the check log the given message and continue the program on success .
     *
     * @param message Message to log.
     *
     * @return A {@link ICheckOptionBuilder} to continue the check building.
     */
    ICheckOptionBuilder continueOnSuccess(String message);
}
