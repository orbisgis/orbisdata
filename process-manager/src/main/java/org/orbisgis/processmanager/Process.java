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
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.orbisgis.processmanager.inoutput.Input;
import org.orbisgis.processmanager.inoutput.Output;
import org.orbisgis.processmanagerapi.IProcess;
import org.orbisgis.processmanagerapi.inoutput.IInOutPut;
import org.orbisgis.processmanagerapi.inoutput.IInput;
import org.orbisgis.processmanagerapi.inoutput.IOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    /** List of inputs */
    private LinkedList<IInput> inputs;
    /** List of outputs */
    private LinkedList<IOutput> outputs;
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
        if(inputs != null && closure != null && closure.getMaximumNumberOfParameters() != inputs.size()){
            LOGGER.error("The number of the closure parameters and the number of process input names are different.");
            return;
        }
        this.title = title;
        this.version = version;
        this.description = description;
        this.keywords = keywords;
        this.inputs = new LinkedList<>();
        this.defaultValues = new HashMap<>();
        if(inputs != null) {
            for (Map.Entry<String, Object> entry : inputs.entrySet()) {
                if( entry.getValue() instanceof Class){
                    Input input = new Input(this, entry.getKey());
                    input.setType((Class)entry.getValue());
                    this.inputs.add(input);
                }
                else if(entry.getValue() instanceof Input) {
                    Input input = (Input)entry.getValue();
                    input.setProcess(this);
                    input.setName(entry.getKey());
                    this.inputs.add(input);
                    if(input.isOptional()) {
                        this.defaultValues.put(entry.getKey(), input.getDefaultValue());
                    }
                }
                else {
                    Input input = new Input(this, entry.getKey());
                    input.setType(entry.getValue().getClass());
                    input.optional(entry.getValue());
                    this.inputs.add(input);
                    this.defaultValues.put(entry.getKey(), entry.getValue());
                }
            }
        }
        this.outputs = new LinkedList<>();
        if(outputs != null) {
            for (Map.Entry<String, Object> entry : outputs.entrySet()) {
                if( entry.getValue() instanceof Class){
                    Output output = new Output(this, entry.getKey());
                    output.setType((Class)entry.getValue());
                    this.outputs.add(output);
                }
                else if(entry.getValue() instanceof Output) {
                    Output output = (Output)entry.getValue();
                    output.setProcess(this);
                    output.setName(entry.getKey());
                    this.outputs.add(output);
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
        Process process = new Process(title, description, keywords, null,  null,
                version, closure);
        process.inputs = this.inputs;
        process.outputs = this.outputs;
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
        for(IInput input : inputs) {
            if (!inputDataMap.containsKey(input.getName())) {
                if(defaultValues.get(input.getName()) == null){
                    LOGGER.error("The parameter " + input.getName() + " has no default value.");
                    return null;
                }
                cl = cl.ncurry(curryIndex, defaultValues.get(input.getName()));
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
                .stream()
                .map(IInOutPut::getName)
                .filter(inputDataMap::containsKey)
                .map(inputDataMap::get)
                .toArray();
    }

    private Closure getClosure(LinkedHashMap<String, Object> inputDataMap){
        if(inputs != null && inputs.size() != 0) {
            Closure cl = getClosureWithCurry(inputDataMap);
            if(cl != null){
                return cl;
            }
        }
        return closure;
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
                    " be between " + (closure.getMaximumNumberOfParameters()-defaultValues.size()) + " and " + closure.getMaximumNumberOfParameters() + ".");
            return false;
        }
        Object result;
        try {
            Closure cl = getClosure(inputDataMap);
            if(cl.getMaximumNumberOfParameters() > 0) {
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
        for(IOutput output : outputs) {
            isResultValid = map.containsKey(output.getName());
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
    public List<IInput> getInputs() {
        return inputs;
    }

    @Override
    public List<IOutput> getOutputs() {
        return outputs;
    }

    @Override
    public String toWps(String type) {
        return toWps(WpsType.valueOf(type.toUpperCase()));
    }

    @Override
    public String toWps(WpsType type) {
        switch(type){
            case GEOSERVER:
                return toGeoServer();
            default:
                return null;
        }
    }

    /**
     * Return the string representation of the process as a GeoServer WPS script.
     *
     * @return GeoServer WPS script.
     */
    private String toGeoServer(){
        ClassNode classNode = closure.getMetaClass().getClassNode();
        if(classNode == null){
            LOGGER.error("Unable to get the source code of the process closure." +
                    " Maybe the process should be written on its own groovy script file.");
            return null;
        }
        StringBuilder builder = new StringBuilder();
        if(title != null) {
            builder.append("title = '").append(this.title).append("'\n");
        }
        if(description != null) {
            builder.append("description = '").append(this.description).append("'\n");
        }
        builder.append("\n");
        if(!inputs.isEmpty()) {
            builder.append("inputs = [\n");
            for (int i = 0; i < inputs.size(); i++) {
                IInput input = inputs.get(i);
                builder.append("\t").append(input.getName()).append(": [name: '").append(input.getName()).append("'");
                if (input.getTitle() != null) {
                    builder.append(", title: '").append(input.getTitle()).append("'");
                }
                builder.append(", type: ").append(input.getType().getTypeName()).append(".class]");
                if (i < inputs.size() - 1) {
                    builder.append(",\n");
                }
            }
            builder.append("\n]\n");
        }
        if(!outputs.isEmpty()) {
            builder.append("outputs = [\n");
            for (int i = 0; i < outputs.size(); i++) {
                IOutput output = outputs.get(i);
                builder.append("\t").append(output.getName()).append(": [name: '").append(output.getName()).append("'");
                if (output.getTitle() != null) {
                    builder.append(", title: '").append(output.getTitle()).append("'");
                }
                builder.append(", type: ").append(output.getType().getTypeName()).append(".class]");
                if(i<outputs.size()-1) {
                    builder.append(",\n");
                }
            }
            builder.append("\n]\n");
        }
        builder.append("def run(input) {\n");
        final String[] code = {classNode.getDeclaredMethods("doCall").get(0).getCode().getText()};
        code[0] = code[0].substring(1, code[0].length()-1);
        inputs.stream().map(IInput::getName).forEach(name -> {
            Matcher matcher = Pattern.compile("[^\\d\\w](" + name + ")[^\\d\\w]").matcher(code[0]);
            while(matcher.find()) {
                code[0] = matcher.usePattern(Pattern.compile(name)).replaceAll("input." + name);
            }
        });
        builder.append(code[0]);
        builder.append("\n}");
        return builder.toString();
    }

    @Override
    public Object invokeMethod(String name, Object args) {
        return metaClass.invokeMethod(this, name, args);
    }

    @Override
    public Object getProperty(String propertyName) {
        if(inputs.stream().anyMatch(iInput -> iInput.getName().equals(propertyName))){
            return new Input(this, propertyName);
        }
        if(outputs.stream().anyMatch(iOutput -> iOutput.getName().equals(propertyName))){
            return new Output(this, propertyName);
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