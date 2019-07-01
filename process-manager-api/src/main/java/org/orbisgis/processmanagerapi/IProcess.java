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

import org.orbisgis.processmanagerapi.inoutput.IInput;
import org.orbisgis.processmanagerapi.inoutput.IOutput;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Interface defining the main methods of a process.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public interface IProcess {

    /**
     * Return a new instance of the process.
     *
     * @return A process new instance.
     */
    IProcess newInstance();

    /**
     * Execute the process with the given inputs.
     *
     * @param inputDataMap Map of the inputs with the name as key and the input value as value.
     *
     * @return True if the execution is successful, false otherwise.
     */
    boolean execute(LinkedHashMap<String, Object> inputDataMap);

    /**
     * Return the title of the process.
     *
     * @return The title of the process.
     */
    String getTitle();

    /**
     * Return the process version.
     *
     * @return The process version.
     */
    String getVersion();

    /**
     * Return the human readable description of the process.
     *
     * @return The description of the process.
     */
    String getDescription();

    /**
     * Return the array of the process keywords.
     *
     * @return The array of the process keywords.
     */
    String[] getKeywords();

    /**
     * Return the results of the process.
     *
     * @return A Map of the results with the output name as key and the output value as value.
     */
    Map<String, Object> getResults();

    /**
     * Return the process identifier.
     *
     * @return The process identifier.
     */
    String getIdentifier();

    /**
     * Return a {@link Map} with the input name as key and its {@link Class} as value.
     *
     * @return A {@link Map} with the input name as key and its {@link Class} as value.
     */
    List<IInput> getInputs();

    /**
     * Return a {@link Map} with the output name as key and its {@link Class} as value.
     *
     * @return A {@link Map} with the output name as key and its {@link Class} as value.
     */
    List<IOutput> getOutputs();

    /**
     * Enumeration of the available WPS for conversion.
     */
    enum WpsType{GeoServer}

    /**
     * Return the WPS script of the given type.
     *
     * @param type Type of the WPS script to write.
     *
     * @return The Wps script.
     */
    String toWps(WpsType type);
}