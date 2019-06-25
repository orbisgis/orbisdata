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
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.orbisgis.processmanager.inoutput.ProcessInOutPut;
import org.orbisgis.processmanagerapi.IProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Implementation of the {@link IProcess} interface dedicated to the local creation and execution of process (no link with
 * WPS process for now).
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public class Process implements IProcess, GroovyObject {

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
    private LinkedHashMap<String, Class> inputs;
    /** Map of outputs with the name as key and the input class as value */
    private LinkedHashMap<String, Class> outputs;
    /** Closure containing the code to execute on the process execution */
    private Closure closure;
    /** Map of the process Result */
    private Map<String, Object> resultMap;
    /** Unique identifier */
    private String identifier;
    /** MetaClass use for groovy methods/properties binding */
    private MetaClass metaClass;
    /** Map of the defaults values */
    private Map<String, Object> defaultValues;

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
     */
    Process(String title, String description, String[] keywords, LinkedHashMap<String, Object> inputs,
            LinkedHashMap<String, Object> outputs, String version, Closure closure){
        if(inputs != null && closure.getMaximumNumberOfParameters() != inputs.size()){
            LOGGER.error("The number of the closure parameters and the number of process input names are different.");
            return;
        }
        this.title = title;
        this.version = version;
        this.description = description;
        this.keywords = keywords;
        this.inputs = new LinkedHashMap<>();
        this.defaultValues = new HashMap<>();
        if(inputs != null) {
            for (Map.Entry<String, Object> entry : inputs.entrySet()) {
                if( entry.getValue() instanceof Class){
                    this.inputs.put(entry.getKey(),(Class) entry.getValue());
                }
                else {
                    this.inputs.put(entry.getKey(), entry.getValue().getClass());
                    this.defaultValues.put(entry.getKey(), entry.getValue());
                }
            }
        }
        this.outputs = new LinkedHashMap<>();
        if(inputs != null) {
            for (Map.Entry<String, Object> entry : outputs.entrySet()) {
                if( entry.getValue() instanceof Class){
                    this.outputs.put(entry.getKey(),(Class) entry.getValue());
                }
                else {
                    this.outputs.put(entry.getKey(), entry.getValue().getClass());
                }
            }
        }
        this.closure = closure;
        this.resultMap = new HashMap<>();
        this.identifier = UUID.randomUUID().toString();
        this.metaClass = InvokerHelper.getMetaClass(getClass());
    }

    @Override
    public IProcess newInstance() {
        Process process = new Process(title, description, keywords, new LinkedHashMap<>(inputs),  new LinkedHashMap<>(outputs),
                version, closure);
        process.defaultValues = this.defaultValues;
        return process;
    }

    /**
     * Return the curry closure taking into account the optional arguments.
     *
     * @param inputDataMap Map containing the data for the execution of the closure. This map may not contains several
     *                     inputs.
     *
     * @return The closure if the missing inputs are all optional, false otherwise.
     */
    private Closure getClosureWithCurry(LinkedHashMap<String, Object> inputDataMap){
        Closure cl = closure;
        int curryIndex = 0;
        for (Map.Entry<String, Class> entry : inputs.entrySet()) {
            if (!inputDataMap.containsKey(entry.getKey())) {
                if(defaultValues.get(entry.getKey()) == null){
                    LOGGER.error("The parameter " + entry.getKey() + " has no default value.");
                    return null;
                }
                cl = cl.ncurry(curryIndex, defaultValues.get(entry.getKey()));
                curryIndex--;
            }
            curryIndex++;
        }
        return cl;
    }

    /**
     * Returns the input data as an Object array.
     *
     * @param inputDataMap Map containing the data for the execution of the closure.
     *
     * @return The casted input data as an Object array.
     */
    private Object[] getClosureArgs(LinkedHashMap<String, Object> inputDataMap){
        return inputs
                .keySet()
                .stream()
                .filter(inputDataMap::containsKey)
                .map(inputDataMap::get)
                .toArray();
    }

    @Override
    public boolean execute(LinkedHashMap<String, Object> inputDataMap) {
        if(closure == null){
            LOGGER.error("The process should have a Closure defined.");
            return false;
        }
        LOGGER.debug("Starting the execution of '" + this.getTitle() + "'.");
        if(inputs != null && inputDataMap != null && (inputs.size() < inputDataMap.size() || inputs.size()-defaultValues.size() > inputDataMap.size())){
            LOGGER.error("The number of the input data map and the number of process input are different, should" +
                    " be between " + (inputDataMap.size()-defaultValues.size()) + " and " + inputDataMap.size() + ".");
            return false;
        }
        Object result;
        try {
            if(inputs != null && inputs.size() != 0) {
                Closure cl = getClosureWithCurry(inputDataMap);
                if(cl == null){
                    return false;
                }
                result = cl.call(getClosureArgs(inputDataMap));
            }
            else {
                result = closure.call();
            }
        } catch (Exception e){
            LOGGER.error("Error while executing the process.\n"+e.getLocalizedMessage());
            return false;
        }
        if(!(result instanceof Map)){
            HashMap<String, Object> map = new HashMap<>();
            map.put("result", result);
            result = map;
        }
        Map<String, Object> map = (Map<String, Object>) result;
        boolean isResultValid = true;
        for (Map.Entry<String, Class> entry : outputs.entrySet()) {
            isResultValid = map.containsKey(entry.getKey());
        }
        LOGGER.debug("End of the execution of '" + this.getTitle() + "'.");
        if(!isResultValid){
            return false;
        }
        else {
            map.forEach((key, value) -> resultMap.put(key, value));
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

    @Override
    public Object invokeMethod(String name, Object args) {
        return metaClass.invokeMethod(this, name, args);
    }

    @Override
    public Object getProperty(String propertyName) {
        if(inputs.keySet().contains(propertyName) || outputs.keySet().contains(propertyName)){
            return new ProcessInOutPut(this, propertyName);
        }
        return metaClass.getProperty(this, propertyName);
    }

    @Override
    public void setProperty(String propertyName, Object newValue) {
        this.metaClass.setProperty(this, propertyName, newValue);
    }

    @Override
    public MetaClass getMetaClass() {
        return metaClass;
    }

    @Override
    public void setMetaClass(MetaClass metaClass) {
        this.metaClass = metaClass;
    }
}