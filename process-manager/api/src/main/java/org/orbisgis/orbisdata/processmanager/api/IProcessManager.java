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
package org.orbisgis.orbisdata.processmanager.api;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.commons.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This interface defines the methods dedicated to the process and process factory managing.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public interface IProcessManager {

    /**
     * Return a {@link IProcessBuilder} to create a {@link IProcess}. Once the process created, it will be register
     * in the default {@link IProcessFactory} if set.
     *
     * @return A {@link IProcessBuilder} to create a {@link IProcess}.
     */
    @NotNull
    IProcessBuilder create();

    /**
     * Return a {@link IProcess} created from the given {@link Closure}. Once the process created, it will be register
     * in the default {@link IProcessFactory} if set.
     *
     * @param cl {@link Closure} delegated to {@link IProcessBuilder}.
     * @return A {@link IProcess}.
     */
    @NotNull
    Optional<IProcess> create(@Nullable @DelegatesTo(IProcessBuilder.class) Closure<?> cl);

    /**
     * Return the list of the factory identifiers.
     *
     * @return The list of the factory identifier.
     */
    @NotNull
    List<String> factoryIds();

    /**
     * Returns the process factory with the given identifier.
     *
     * @param identifier Identifier of the factory.
     * @return The process factory with the given identifier.
     */
    @NotNull
    IProcessFactory factory(@Nullable String identifier);

    /**
     * Returns the default process factory.
     *
     * @return The default process factory.
     */
    @NotNull
    IProcessFactory factory();

    /**
     * Returns the process with the given identifier from the default factory.
     *
     * @param processId Identifier of the process to get.
     * @return The process with the given identifier from the default factory.
     */
    @NotNull
    Optional<IProcess> process(@Nullable String processId);

    /**
     * Returns the process with the given identifier from the factory with the given identifier.
     *
     * @param processId Identifier of the process to get.
     * @param factoryId Identifier of the factory.
     * @return The process with the given identifier from the factory with the given identifier.
     */
    @NotNull
    Optional<IProcess> process(@Nullable String processId, @Nullable String factoryId);

    /**
     * Register the given factory with the given id.
     *
     * @param id      Identifier of the factory to register.
     * @param factory Factory to register.
     */
    boolean registerFactory(@Nullable String id, @Nullable IProcessFactory factory);

    /**
     * Register a map of factories with the id as key and the factory as value.
     *
     * @param map Map of the factories.
     */
    void register(@Nullable Map<String, IProcessFactory> map);
}
