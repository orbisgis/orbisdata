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

import java.util.Optional;

/**
 * This interface defines the methods dedicated to the process creation and managing.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC 2019-2020)
 */
public interface IProcessFactory {

    /**
     * Register a {@link IProcess}.
     *
     * @param process {@link IProcess} to register.
     */
    void registerProcess(@Nullable IProcess process);

    /**
     * Return true if the process creation is locked, false otherwise.
     *
     * @return True if the process creation is locked, false otherwise.
     */
    boolean isLocked();

    /**
     * Return true if the factory if the default one, false otherwise.
     *
     * @return True if the factory if the default one, false otherwise.
     */
    boolean isDefault();

    /**
     * Returns the process with the given identifier.
     *
     * @param processId Identifier of the process to get.
     * @return The process with the given identifier.
     */
    @Nullable
    Optional<IProcess> getProcess(@Nullable String processId);

    /**
     * Return a {@link IProcessBuilder} to create a {@link IProcess}. Once the process created, it will be register
     * in this {@link IProcessFactory}.
     *
     * @return A {@link IProcessBuilder} to create a {@link IProcess}.
     */
    @NotNull
    IProcessBuilder create();

    /**
     * Return a {@link IProcess} created from the given {@link Closure}. Once the process created, it will be register
     * in this {@link IProcessFactory}.
     *
     * @param cl {@link Closure} delegated to {@link IProcessBuilder}.
     * @return A {@link IProcess}.
     */
    @NotNull
    Optional<IProcess> create(@Nullable @DelegatesTo(IProcessBuilder.class) Closure<?> cl);
}
