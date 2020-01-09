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
import org.orbisgis.orbisdata.processmanager.api.IProcess;

import java.util.LinkedHashMap;

/**
 * Interface for the definition of getProcess check.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public interface IProcessCheck {

    /**
     * Continue action.
     */
    String CONTINUE = "CONTINUE";
    /**
     * Stop action.
     */
    String STOP = "STOP";

    /**
     * Run the check with output data given with {@link #setInOutputs(Object...)} and the input data from the given
     * {@link LinkedHashMap}. On fail, exit the programme or apply the action set with {@link #onFail(String, String)}.
     * On success, continue the programme or apply the action set with {@link #onSuccess(String, String)}.
     *
     * @param processInData {@link LinkedHashMap} containing the input data for the {@link IProcess} execution.
     */
    void run(LinkedHashMap<String, Object> processInData);

    /**
     * Set the action to do on check fail.
     *
     * @param action  Action to do on fail.
     * @param message Message to log.
     */
    void onFail(String action, String message);

    /**
     * Set the action to do on check success.
     *
     * @param action  Action to do on success.
     * @param message Message to log.
     */
    void onSuccess(String action, String message);

    /**
     * Sets the input and output to use inside the check.
     *
     * @param inputOrOutput Input or output list to use for the check.
     */
    void setInOutputs(Object... inputOrOutput);

    /**
     * Set the {@link Closure} to call to execute the check.
     *
     * @param cl {@link Closure} to call to execute the check.
     */
    void setClosure(Closure cl);

    /**
     * Method executed on check fail.
     */
    void fail();

    /**
     * Method executed on check success.
     */
    void success();

    /**
     * Return the {@link IProcess} concerned by the check.
     *
     * @return The {@link IProcess} concerned by the check.
     */
    IProcess getProcess();
}
