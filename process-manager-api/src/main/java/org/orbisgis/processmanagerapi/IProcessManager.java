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
package org.orbisgis.processmanagerapi;

import java.util.List;

/**
 * This interface defines the methods dedicated to the process and process factory managing.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public interface IProcessManager {

    /**
     * Return the list of the factory identifiers.
     *
     * @return The list of the factory identifier.
     */
    List<String> factoryIds();

    /**
     * Returns the process factory with the given identifier.
     *
     * @param identifier Identifier of the factory.
     *
     * @return The process factory with the given identifier.
     */
    IProcessFactory factory(String identifier);

    /**
     * Returns the default process factory.
     *
     * @return The default process factory.
     */
    IProcessFactory factory();

    /**
     * Returns the process with the given identifier from the default factory.
     *
     * @param processId Identifier of the process to get.
     *
     * @return The process with the given identifier from the default factory.
     */
    IProcess process(String processId);

    /**
     * Returns the process with the given identifier from the factory with the given identifier.
     *
     * @param processId Identifier of the process to get.
     * @param factoryId Identifier of the factory.
     *
     * @return The process with the given identifier from the factory with the given identifier.
     */
    IProcess process(String processId, String factoryId);
}