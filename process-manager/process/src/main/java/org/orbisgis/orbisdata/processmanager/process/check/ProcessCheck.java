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
 * ProcessManager is distributed under LGPL 3 license.
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
package org.orbisgis.orbisdata.processmanager.process.check;

import groovy.lang.Closure;
import groovy.lang.GroovyInterceptable;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.commons.annotations.Nullable;
import org.orbisgis.orbisdata.processmanager.api.IProcess;
import org.orbisgis.orbisdata.processmanager.api.check.IProcessCheck;
import org.orbisgis.orbisdata.processmanager.api.inoutput.IInOutPut;
import org.orbisgis.orbisdata.processmanager.api.inoutput.IInput;
import org.orbisgis.orbisdata.processmanager.api.inoutput.IOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static org.orbisgis.orbisdata.processmanager.api.check.IProcessCheck.Action.CONTINUE;
import static org.orbisgis.orbisdata.processmanager.api.check.IProcessCheck.Action.STOP;

/**
 * Implementation of the {@link IProcessCheck} interface.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019-2020)
 */
public class ProcessCheck implements IProcessCheck, GroovyObject, GroovyInterceptable {

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessCheck.class);

    /**
     * {@link IProcess} concerned by the check.
     */
    @Nullable
    private final IProcess process;
    /**
     * Inputs/outputs to use for the check.
     */
    private final LinkedList<IInOutPut> inOutPuts = new LinkedList<>();
    /**
     * {@link Closure} to perform for the check.
     */
    @Nullable
    private Closure<?> cl;
    /**
     * Action to do on fail.
     */
    @NotNull
    private Action failAction = STOP;
    /**
     * Message to log on fail.
     */
    @Nullable
    private String failMessage = "Check failed";
    /**
     * Action to do on success.
     */
    @NotNull
    private Action successAction = CONTINUE;
    /**
     * Message to log on success.
     */
    @Nullable
    private String successMessage = "Check successful";
    /**
     * MetaClass use for groovy methods/properties binding
     */
    @Nullable
    private MetaClass metaClass = InvokerHelper.getMetaClass(getClass());

    /**
     * Default constructor. It the given {@link IProcess} is null, execute the check before any process execution.
     *
     * @param process {@link IProcess} concerned by the check.
     */
    public ProcessCheck(@Nullable IProcess process) {
        this.process = process;
    }

    /**
     * Return true if the Inputs are equals to given data, false otherwise.
     *
     * @param data {@link LinkedHashMap} containing the input data for the {@link IProcess} execution.
     * @return True if the process should continue, false otherwise.
     */
    private boolean runNoClosure(@NotNull LinkedHashMap<String, Object> data) {
        //First gather only the inputs as outputs can't be checked before any execution.
        List<IInput> inputs = inOutPuts.stream()
                .filter(inOutPut -> inOutPut instanceof IInput)
                .map(input -> (IInput)input)
                .collect(Collectors.toList());
        //Then check that the default values are the same as the data in the given map
        return inputs.stream().allMatch(in -> in.getName().isPresent() &&
                in.getDefaultValue().isPresent() &&
                data.containsKey(in.getName().get()) &&
                data.get(in.getName().get()).equals(in.getDefaultValue().get()));
    }

    /**
     * Execute the given closure with the given data.
     *
     * @param data {@link LinkedHashMap} containing the input data for the {@link IProcess} execution.
     * @param cl   {@link Closure} to execute for the check.
     * @return The {@link Closure} result or false if it can't be executed.
     */
    @Nullable
    private Object runWithClosure(@Nullable LinkedHashMap<String, Object> data, @NotNull Closure<?> cl) {
        LinkedList<Object> dataList = new LinkedList<>();
        //Gather all the data (process output or input data)
        for (IInOutPut inOutPut : inOutPuts) {
            if (inOutPut.getProcess().isPresent() &&
                    inOutPut.getProcess().get().getOutputs().stream()
                            .anyMatch(output -> output.getName().equals(inOutPut.getName()))) {
                dataList.add(inOutPut.getProcess().get().getResults().get(inOutPut.getName().get()));
            } else if (data != null && inOutPut.getName().isPresent()) {
                dataList.add(data.get(inOutPut.getName().get()));
            }
        }
        //Execute the Closure with the gathered data.
        try {
            return cl.call(dataList.toArray());
        } catch (Exception e) {
            String message = "";
            if(data == null || cl.getMaximumNumberOfParameters() > data.size()) {
                message = "\nIt can be an invalid number of data or closure input.";
            }
            LOGGER.error("Unable to run the process check with the given data." + message, e);
        }
        return false;
    }

    @Override
    public boolean run(@Nullable LinkedHashMap<String, Object> data) {
        if (cl == null && (inOutPuts.isEmpty() || data == null || data.isEmpty())) {
            LOGGER.warn("No closure set and no In/Outputs or data to check, no check to do.");
            return false;
        }

        Object result;
        if(cl == null){
            result = runNoClosure(data);
        }
        else {
            result = runWithClosure(data, cl);
        }
        if (!(result instanceof Boolean)) {
            LOGGER.error("The result of the check closure should be a boolean.");
            result = false;
        }
        if ((Boolean) result) {
            return success();
        } else {
            return fail();
        }
    }

    @Override
    public void onFail(@Nullable Action action, @Nullable String message) {
        failAction = action == null ? STOP : action;
        failMessage = message;
    }

    @Override
    public void onFail(@Nullable String message) {
        failAction = STOP;
        failMessage = message;
    }

    @Override
    public void onSuccess(@Nullable Action action, @Nullable String message) {
        successAction = action == null ? STOP : action;
        successMessage = message;
    }

    @Override
    public void onSuccess(@Nullable String message) {
        successAction = CONTINUE;
        successMessage = message;
    }

    @Override
    public void setInOutPuts(@Nullable IInOutPut... data) {
        if(data != null) {
            this.inOutPuts.clear();
            Collections.addAll(this.inOutPuts, data);
        }
    }

    @Override
    @NotNull
    public LinkedList<IInOutPut> getInOutPuts() {
        return inOutPuts;
    }

    @Override
    public void setClosure(@Nullable Closure<?> cl) {
        this.cl = cl;
    }

    @Override
    @NotNull
    public Optional<Closure<?>> getClosure() {
        return Optional.ofNullable(cl);
    }

    @Override
    public boolean fail() throws IllegalStateException {
        if(failMessage != null) {
            LOGGER.error(failMessage);
        }
        switch (failAction) {
            case CONTINUE:
                return false;
            case STOP:
            default:
                return true;
        }
    }

    @Override
    public boolean success() throws IllegalStateException {
        if(successMessage != null) {
            LOGGER.info(successMessage);
        }
        switch (successAction) {
            case CONTINUE:
            default:
                return false;
            case STOP:
                return true;
        }
    }

    @Override
    @NotNull
    public Optional<IProcess> getProcess() {
        return Optional.ofNullable(process);
    }

    @Override
    public void setProperty(@Nullable String propertyName, @Nullable Object newValue) {
        if(propertyName != null && metaClass != null) {
            this.metaClass.setProperty(this, propertyName, newValue);
        }
    }

    @Nullable
    @Override
    public Object getProperty(@Nullable String propertyName){
        if(metaClass != null) {
            Object obj = this.metaClass.getProperty(this, propertyName);
            if(obj instanceof Optional){
                return ((Optional<?>)obj).orElse(null);
            }
            else {
                return obj;
            }
        }
        else {
            return null;
        }
    }

    @Nullable
    @Override
    public Object invokeMethod(@Nullable String name, @Nullable Object args) {
        if(name != null && metaClass != null) {
            Object obj = this.metaClass.invokeMethod(this, name, args);
            if(obj instanceof Optional){
                return ((Optional<?>)obj).orElse(null);
            }
            else {
                return obj;
            }
        }
        else {
            return null;
        }
    }

    @Override
    @Nullable
    public MetaClass getMetaClass() {
        return metaClass;
    }

    @Override
    public void setMetaClass(@Nullable MetaClass metaClass) {
        this.metaClass = metaClass;
    }

    @Override
    @NotNull
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ProcessCheck : \n");
        if(!getClosure().isPresent() && getInOutPuts().isEmpty()){
            return "No check can be run as there is no closure nor in/outputs set.";
        }
        if(getProcess().isPresent()) {
            //If there is a process, get the name. If the name is null, use the process id.
            String processName = getProcess().get().getTitle() != null ?
                    getProcess().get().getTitle() :
                    getProcess().get().getIdentifier();
            builder.append("On process :")
                    .append(processName)
                    .append("\n");
        }
        if(!getInOutPuts().isEmpty()) {
            builder.append("Check that inputs :\n");
            inOutPuts.stream()
                    .filter(inOutPut -> inOutPut instanceof IInput)
                    .filter(inOutPut -> inOutPut.getName().isPresent())
                    .forEach(inOutPut -> builder.append("\t").append(inOutPut.getName().get()).append("\n"));

            if (getProcess().isPresent()) {
                builder.append("and output :\n");
                inOutPuts.stream()
                        .filter(inOutPut -> inOutPut instanceof IOutput)
                        .forEach(inOutPut -> builder.append("\t").append(inOutPut.getName().get()).append("\n"));
            }
        }
        if (getClosure().isPresent()) {
            builder.append("Verifies a closure");
        }
        else {
            builder.append("are equals to provided data.");
        }
        return builder.toString();
    }
}
