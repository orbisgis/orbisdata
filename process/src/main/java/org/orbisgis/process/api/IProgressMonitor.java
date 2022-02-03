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

import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.commons.annotations.Nullable;

/**
 * This interface declare the methods for a progress monitoring the running of a {@link IProcess} or a
 * {@link IProcessMapper}.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (Lab-STICC UBS 2020)
 */
public interface IProgressMonitor {

    /**
     * Increments the current step of the progression.
     */
    void incrementStep();

    /**
     * Returns the progression or -1 if the {@link IProgressMonitor} has not maximum step count.
     *
     * @return The progression or -1.
     */
    double getProgress();

    /**
     * Returns the maximum count of steps.
     *
     * @return The maximum count of steps.
     */
    int getMaxStep();

    /**
     * Log the progression.
     */
    void log();

    /**
     * Make the progression reach its end.
     */
    void end();

    /**
     * Returns the {@link IProgressMonitor} name.
     *
     * @return The {@link IProgressMonitor} name.
     */
    @NotNull
    String getName();

    /**
     * Returns a child {@link IProgressMonitor} with the given name as task name, without maximum step count.
     *
     * @param taskName Name of the task.
     * @return A child {@link IProgressMonitor}.
     */
    @NotNull
    IProgressMonitor getSubProgress(@Nullable String taskName);

    /**
     * Returns a child {@link IProgressMonitor} with the given maximum as maximum step count.
     *
     * @param maximum Maximum step number.
     * @return A child {@link IProgressMonitor}.
     */
    @NotNull
    IProgressMonitor getSubProgress(int maximum);

    /**
     * Returns a child {@link IProgressMonitor} without maximum step count, logging the progression if autoLog to true.
     *
     * @param autoLog Log the progression if true, otherwise do not log.
     * @return A child {@link IProgressMonitor}.
     */
    @NotNull
    IProgressMonitor getSubProgress(boolean autoLog);

    /**
     * Returns a child {@link IProgressMonitor} with the given name as task name and the given maximum as maximum
     * step count.
     *
     * @param taskName Name of the task.
     * @param maximum Maximum step number.
     * @return A child {@link IProgressMonitor}.
     */
    @NotNull
    IProgressMonitor getSubProgress(@Nullable String taskName, int maximum);

    /**
     * Returns a child {@link IProgressMonitor} with the given name as task name, without maximum step count and
     * logging the progression if autoLog to true.
     *
     * @param taskName Name of the task.
     * @param autoLog Log the progression if true, otherwise do not log.
     * @return A child {@link IProgressMonitor}.
     */
    @NotNull
    IProgressMonitor getSubProgress(@Nullable String taskName, boolean autoLog);

    /**
     * Returns a child {@link IProgressMonitor} with the given maximum as maximum step count and logging the
     * progression if autoLog to true.
     *
     * @param maximum Maximum step number.
     * @param autoLog Log the progression if true, otherwise do not log.
     * @return A child {@link IProgressMonitor}.
     */
    @NotNull
    IProgressMonitor getSubProgress(int maximum, boolean autoLog);

    /**
     * Returns a child {@link IProgressMonitor} with the given name as task name, the given maximum as maximum
     * step count and logging the progression if autoLog to true.
     *
     * @param taskName Name of the task.
     * @param maximum Maximum step number.
     * @param autoLog Log the progression if true, otherwise do not log.
     * @return A child {@link IProgressMonitor}.
     */
    @NotNull
    IProgressMonitor getSubProgress(@Nullable String taskName, int maximum, boolean autoLog);
}
