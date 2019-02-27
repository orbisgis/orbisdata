/*
 * Bundle ProcessManager is part of the OrbisGIS platform
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
 * ProcessManager is distributed under GPL 3 license.
 *
 * Copyright (C) 2018 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * ProcessManager is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * ProcessManager is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * ProcessManager. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.processmanager;

import groovy.lang.Closure;
import org.orbisgis.processmanagerapi.ICaster;
import org.orbisgis.processmanagerapi.IProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Implementation of the IProcess interface dedicated to the local creation and execution of process (no link with
 * WPS process for now).
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public class Process implements IProcess {

    private static final Logger LOGGER = LoggerFactory.getLogger(Process.class);

    /** Title of the process */
    private String title;
    /** Process version */
    private String version;
    /** Human readable description of the process */
    private String description;
    /** List of simple keyword (one word) of the process */
    private String[] keywords;
    /** Map of inputs with the name as key and the input class as value */
    private Map<String, Class> inputs;
    /** Map of outputs with the name as key and the input class as value */
    private Map<String, Class> outputs;
    /** Closure containing the code to execute on the process execution */
    private Closure closure;
    /** Map of the process Result */
    private Map<String, Object> resultMap;
    /** Caster used to cast input object into the good class */
    private ICaster caster;
    /** Unique identifier */
    private String identifier;

    /**
     * Create a new Process with its title, description, keyword array, input map, output map, version
     * and the closure containing the code to execute on the process execution. The Closure will receive all the input
     * as parameter in the same order as the order of the input map. So the list of parameter of the closure, the
     * array of input and the map of input given on the execution should have the same size and meaning.
     *
     * @param title Title of the process.
     * @param description Human readable description of the process.
     * @param keywords List of simple keyword (one word) of the process.
     * @param inputs Map of inputs with the name as key and the input class as value. The names will be used  to link
     *               the closure parameters with the execution input data map.
     * @param outputs Map of outputs with the name as key and the output class as value. Those names will be used to
     *                    generate the Map of the getResults Method.
     * @param version Process version.
     * @param closure Closure containing the code to execute on the process execution.
     * @param caster Caster used to cast input object into the good class.
     */
    public Process(String title, String description, String[] keywords, Map<String, Class> inputs,
                   Map<String, Class> outputs, String version, Closure closure, ICaster caster){
        if(closure.getMaximumNumberOfParameters() != inputs.size()){
            LOGGER.error("The number of the closure parameters and the number of process input names are different.");
            return;
        }
        this.title = title;
        this.version = version;
        this.description = description;
        this.keywords = keywords;
        this.inputs = inputs;
        this.outputs = outputs;
        this.closure = closure;
        this.resultMap = new HashMap<>();
        this.caster = caster;
        this.identifier = title;
    }

    @Override
    public boolean execute(Map<String, Object> inputDataMap) {
        if(inputs != null && inputDataMap.size() != inputs.size()){
            LOGGER.error("The number of the input data map and the number of process input are different.");
            return false;
        }
        Object result;
        try {
            if(inputs != null) {
                result = closure.call(inputs.entrySet().stream().map(
                        entry -> caster.cast(inputDataMap.get(entry.getKey()), entry.getValue())).toArray()
                );
            }
            else {
                result = closure.call();
            }
        } catch (Exception e){
            LOGGER.error("Error while executing the process.\n"+e.getLocalizedMessage());
            return false;
        }
        if(!(result instanceof Map)){
            LOGGER.error("The process output should be a Map");
            return false;
        }
        Map<String, Object> map = (Map<String, Object>) result;
        boolean isResultValid = true;
        for (Map.Entry<String, Class> entry : outputs.entrySet()) {
            isResultValid = map.containsKey(entry.getKey());
        }
        if(!isResultValid){
            return false;
        }
        else {
            map.forEach((key, value) -> resultMap.put(key, caster.cast(value, outputs.get(key))));
            return true;
        }
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String[] getKeywords() {
        return keywords;
    }

    @Override
    public Map<String, Object> getResults() {
        return resultMap;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public Map<String, Class> getInputs() {
        return inputs;
    }

    @Override
    public Map<String, Class> getOutputs() {
        return outputs;
    }
}