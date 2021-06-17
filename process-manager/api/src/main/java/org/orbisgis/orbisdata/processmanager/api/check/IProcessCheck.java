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
package org.orbisgis.orbisdata.processmanager.api.check;

import groovy.lang.Closure;
import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.commons.annotations.Nullable;
import org.orbisgis.orbisdata.processmanager.api.IProcess;
import org.orbisgis.orbisdata.processmanager.api.inoutput.IInOutPut;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Optional;

/**
 * Interface for the definition of getProcess check.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC 2019-2020)
 */
public interface IProcessCheck {

    /**
     * Enumeration of the action to do after a process check.
     */
    enum Action{CONTINUE, STOP}

    /**
     * Run the check with output data given with {@link #setInOutPuts(IInOutPut...)} and the input data from the given
     * {@link LinkedHashMap}. On fail, return false and apply the action set with {@link #onFail(Action, String)}.
     * On success, return true and apply the action set with {@link #onSuccess(Action, String)}.
     *
     * I not process have been provided, the default values of the mapper input are checked. The given map represents
     * the inputs to check as key and there default value as value.
     *
     * @param processInData {@link LinkedHashMap} containing the input data for the {@link IProcess} execution.
     * @return True if the process should continue, false otherwise
     */
    boolean run(LinkedHashMap<String, Object> processInData);

    /**
     * Set the action to do on check fail. If action is null, use {@link Action#STOP} by default.
     *
     * @param action  Action to do on fail.
     * @param message Message to log.
     */
    void onFail(Action action, String message);

    /**
     * Set the message on check fail. By default use {@link Action#STOP}.
     *
     * @param message Message to log.
     */
    void onFail(String message);

    /**
     * Set the action to do on check success. If action is null, use {@link Action#STOP} by default.
     *
     * @param action  Action to do on success.
     * @param message Message to log.
     */
    void onSuccess(Action action, String message);

    /**
     * Set the message on check success. By default use {@link Action#CONTINUE}.
     *
     * @param message Message to log.
     */
    void onSuccess(String message);

    /**
     * Sets the input and output to use inside the check.
     *
     * @param inputOrOutput Input or output list to use for the check.
     */
    void setInOutPuts(IInOutPut... inputOrOutput);

    /**
     * Returns the input and output to use inside the check.
     *
     * @return The input or output list to use for the check.
     */
    LinkedList<IInOutPut> getInOutPuts();

    /**
     * Set the {@link Closure} to call to execute the check.
     *
     * @param cl {@link Closure} to call to execute the check.
     */
    void setClosure(Closure<?> cl);

    /**
     * Returns the {@link Closure} to call to execute the check.
     *
     * @return The {@link Closure} to call to execute the check.
     */
    Optional<Closure<?>> getClosure();

    /**
     * Method executed on check fail. If process should stop, returns true, false otherwise.
     *
     * @return False if the process should stop, true otherwise.
     * @throws IllegalStateException Exception thrown when check should stop.
     */
    boolean fail() throws IllegalStateException;

    /**
     * Method executed on check success. If process should stop, returns true, false otherwise.
     *
     * @return False if the process should stop, true otherwise.
     * @throws IllegalStateException Exception thrown when check should stop.
     */
    boolean success() throws IllegalStateException;

    /**
     * Return the {@link IProcess} concerned by the check.
     *
     * @return The {@link IProcess} concerned by the check.
     */
    Optional<IProcess> getProcess();
}
