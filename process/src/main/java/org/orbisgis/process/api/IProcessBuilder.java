/*
 * Bundle Process is part of the OrbisGIS platform
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
 * Process is distributed under LGPL 3 license.
 *
 * Copyright (C) 2018 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * Process is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Process is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Process. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.process.api;

import groovy.lang.Closure;
import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.commons.annotations.Nullable;

import java.util.LinkedHashMap;

/**
 * Interface declaring all the methods for the building of a {@link IProcess}.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public interface IProcessBuilder {

    /**
     * Set the identifier of the process.
     *
     * @param id Identifier of the process.
     * @return A {@link IProcessBuilder} to continue the building.
     */
    @NotNull
    IProcessBuilder id(@Nullable String id);

    /**
     * Set the title of the process.
     *
     * @param title Title of the process.
     * @return A {@link IProcessBuilder} to continue the building.
     */
    @NotNull
    IProcessBuilder title(@Nullable String title);

    /**
     * Set the description of the process.
     *
     * @param description Human readable description of the process.
     * @return A {@link IProcessBuilder} to continue the building.
     */
    @NotNull
    IProcessBuilder description(@Nullable String description);

    /**
     * Set the keywords of the process.
     *
     * @param keywords List of simple keyword (one word) of the process.
     * @return A {@link IProcessBuilder} to continue the building.
     */
    @NotNull
    IProcessBuilder keywords(@Nullable String[] keywords);

    /**
     * Set the inputs of the process.
     *
     * @param inputs {@link LinkedHashMap} of inputs with the name as key and the input Object as value.
     *               The names will be used  to link the closure parameters with the execution
     *               input data map.
     * @return A {@link IProcessBuilder} to continue the building.
     */
    @NotNull
    IProcessBuilder inputs(@Nullable LinkedHashMap<String, Object> inputs);

    /**
     * Set the outputs of the process.
     *
     * @param outputs {@link LinkedHashMap} of outputs with the name as key and the output Object as value.
     *                Those names will be used to generate the LinkedHashMap of the
     *                getResults Method.
     * @return A {@link IProcessBuilder} to continue the building.
     */
    @NotNull
    IProcessBuilder outputs(@Nullable LinkedHashMap<String, Object> outputs);

    /**
     * Set the version of the process.
     *
     * @param version Process version.
     * @return A {@link IProcessBuilder} to continue the building.
     */
    @NotNull
    IProcessBuilder version(@Nullable String version);

    /**
     * Set the closure of the process.
     *
     * @param closure {@link Closure} containing the code to execute on the process execution.
     * @return A {@link IProcessBuilder} to continue the building.
     */
    @NotNull
    IProcessBuilder run(@Nullable Closure<?> closure);

    /**
     * Build and return the process.
     *
     * @return The built {@link IProcess}.
     */
    @NotNull
    IProcess getProcess();
}
