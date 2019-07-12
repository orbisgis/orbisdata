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

import org.orbisgis.processmanager.check.CheckDataBuilder;
import org.orbisgis.processmanager.check.ProcessCheck;
import org.orbisgis.processmanager.inoutput.Input;
import org.orbisgis.processmanager.inoutput.Output;
import org.orbisgis.processmanagerapi.ILinker;
import org.orbisgis.processmanagerapi.IProcess;
import org.orbisgis.processmanagerapi.IProcessMapper;
import org.orbisgis.processmanagerapi.check.ICheckDataBuilder;
import org.orbisgis.processmanagerapi.check.IProcessCheck;
import org.orbisgis.processmanagerapi.inoutput.IInOutPut;
import org.orbisgis.processmanagerapi.inoutput.IInput;
import org.orbisgis.processmanagerapi.inoutput.IOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of IProcessMapper. This class should not be used will using a IProcess executing and mapping
 * processes is easier.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public class ProcessMapper implements IProcessMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessMapper.class);

    private LinkedList<IInput> inputs = new LinkedList<>();
    private LinkedList<IOutput> outputs = new LinkedList<>();
    private List<List<IProcess>> executionTree;
    private Map<String, Object> results;
    /** Map with the input as key and the output as value. */
    private Map<IInOutPut, IInOutPut> inputOutputMap;
    /** List of all the processes used in this mapper. */
    private List<IProcess> processList;
    /** Map of the aliases as key and the list of input/output as value. */
    private Map<String, List<IInOutPut>> aliases;
    /** List of check to do before process execution */
    private List<IProcessCheck> beforeList = new ArrayList<>();
    /** List of check to do after process execution */
    private List<IProcessCheck> afterList = new ArrayList<>();
    /** Title of the mapper. */
    private String title;

    /**
     * Main empty constructor.
     */
    public ProcessMapper(){
        this(null);
    }

    /**
     * Main constructor.
     */
    public ProcessMapper(String title){
        this.title = title;
        inputOutputMap = new HashMap<>();
        aliases = new HashMap<>();
    }

    /**
     * Return the alias of the given input or output.
     *
     * @param name Name of the alias.
     * @param process {@link IProcess} containing the input/output with the alias.
     *
     * @return The alias name if there is one, null otherwise.
     */
    private String getAlias(String name, IProcess process){
        for(Map.Entry<String, List<IInOutPut>> list : aliases.entrySet()){
            for(IInOutPut inOutPut : list.getValue()) {
                if (inOutPut.getName().equals(name) && inOutPut.getProcess().getIdentifier().equals(process.getIdentifier())) {
                    return list.getKey();
                }
            }
        }
        return null;
    }

    /**
     * Fill the process list.
     */
    private void fillProcessList(){
        inputOutputMap.forEach((input, output) -> {
            if (!processList.contains(input.getProcess())) {
                processList.add(input.getProcess());
            }
            if (!processList.contains(output.getProcess())) {
                processList.add(output.getProcess());
            }
        });
        aliases.forEach((alias, iInOutPuts) -> {
            iInOutPuts.forEach(iInOutPut -> {
                if (!processList.contains(iInOutPut.getProcess())) {
                    processList.add(iInOutPut.getProcess());
                }
            });
        });
    }

    private void collectInput(IInput input, IProcess process, List<IInOutPut> collection){
        boolean isBetween = false;
        for (Map.Entry<IInOutPut, IInOutPut> entry : inputOutputMap.entrySet()) {
            if (entry.getKey().getName().equals(input.getName()) &&
                    entry.getKey().getProcess().getIdentifier().equals(process.getIdentifier())) {
                isBetween = true;
            }
        }
        if (!isBetween) {
            if(inputs.stream().noneMatch(iInput -> iInput.getName().equals(input.getName()) &&
                    iInput.getProcess().getIdentifier().equals(input.getProcess().getIdentifier()))) {
                inputs.add(input);
            }
            collection.add(new Input(process, input.getName()));
        }
    }

    private void collectOutput(IOutput output, IProcess process){
        boolean isBetween = false;
        for(Map.Entry<IInOutPut, IInOutPut> entry : inputOutputMap.entrySet()) {
            if (entry.getValue().getName().equals(output.getName()) &&
                    entry.getValue().getProcess().getIdentifier().equals(process.getIdentifier())) {
                isBetween = true;
            }
        }
        if (!isBetween &&
                outputs.stream().noneMatch(iOutput -> iOutput.getName().equals(output.getName()) &&
                        iOutput.getProcess().getIdentifier().equals(output.getProcess().getIdentifier())) &&
                getAlias(output.getName(), process) == null) {
            outputs.add(output);
        }
    }

    /**
     * Collect all the not linked inputs/outputs to set the {@link ProcessMapper} inputs/outputs.
     *
     * @return The list of available inputs.
     */
    private List<IInOutPut> collectInputOutput(){
        List<IInOutPut> availableIn = new ArrayList<>();
        for (IProcess process : processList) {
            if(process.getInputs() != null) {
                process.getInputs().forEach(input -> collectInput(input, process, availableIn));
            }
            process.getOutputs().forEach(output -> collectOutput(output, process));
        }
        return availableIn;
    }

    private void buildExecutionTreeLevel(List<IProcess> iterableProcessList, List<IInOutPut> availableIn,
                                         int i, List<IInOutPut> newIn){
        for (Iterator<IProcess> iterator = iterableProcessList.iterator(); iterator.hasNext(); ) {
            IProcess process = iterator.next();
            boolean isAllInput = true;
            if (process.getInputs() != null) {
                for (IInput input : process.getInputs()) {
                    boolean isInputAvailable = false;
                    for (IInOutPut processInput : availableIn) {
                        if (processInput.getName().equals(input.getName()) &&
                                processInput.getProcess().getIdentifier().equals(process.getIdentifier())) {
                            isInputAvailable = true;
                            break;
                        }
                    }
                    if (!isInputAvailable) {
                        isAllInput = false;
                        break;
                    }
                }
            }
            if (isAllInput) {
                executionTree.get(i).add(process);
                for (IOutput name : process.getOutputs()) {
                    for (Map.Entry<IInOutPut, IInOutPut> entry : inputOutputMap.entrySet()) {
                        if (entry.getValue().getName().equals(name.getName()) &&
                                entry.getValue().getProcess().getIdentifier().equals(process.getIdentifier())) {
                            newIn.add(entry.getKey());
                        }
                    }
                }
                iterator.remove();
            }
        }
    }

    /**
     * Link the inputs, the outputs and the aliases to prepare the execution.
     */
    private boolean link(){
        executionTree = new ArrayList<>();
        results = new HashMap<>();
        processList = new ArrayList<>();

        fillProcessList();

        List<IInOutPut> availableIn = collectInputOutput();

        //Build the execution tree
        List<IProcess> iterableProcessList = new ArrayList<>(processList);
        int i = 0;
        List<IInOutPut> newIn = new ArrayList<>();
        do {
            availableIn.addAll(newIn);
            newIn = new ArrayList<>();
            executionTree.add(new ArrayList<>());
            buildExecutionTreeLevel(iterableProcessList, availableIn, i, newIn);
            i++;
        } while(!iterableProcessList.isEmpty() && !executionTree.get(i-1).isEmpty());

        if(!iterableProcessList.isEmpty()){
            LOGGER.error("Unable to link the processes '"+
                    iterableProcessList.stream().map(IProcess::getTitle).collect(Collectors.joining(","))+"'");
            return false;
        }
        return true;
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
    public IProcessMapper newInstance() {
        ProcessMapper mapper = new ProcessMapper();
        mapper.aliases = aliases;
        mapper.processList = new ArrayList<>(processList);
        mapper.inputOutputMap = new HashMap<>(inputOutputMap);
        mapper.executionTree = new ArrayList<>(executionTree);
        mapper.inputs = new LinkedList<>(inputs);
        mapper.outputs = new LinkedList<>(outputs);
        return mapper;
    }

    /**
     * Return a {@link LinkedHashMap} with the input data for the given {@link IProcess} execution.
     *
     * @param process {@link IProcess} which will executed.
     * @param dataMap {@link Map} with the {@link ProcessMapper} input data.
     *
     * @return A {@link LinkedHashMap} with the input data for the given {@link IProcess}.
     */
    private LinkedHashMap<String, Object> getInputDataMap(IProcess process, Map<String, Object> dataMap){
        LinkedHashMap<String, Object> processInData = new LinkedHashMap<>();
        if(process.getInputs() != null) {
            for (IInput in : process.getInputs()) {
                //Try to get the data directly from the out of a process
                String alias = getAlias(in.getName(), process);
                final Object[] data = new Object[1];
                if(alias != null){
                    data[0] = dataMap.get(alias);
                }
                else {
                    data[0] = dataMap.get(in.getName());
                }
                //Get the link between the input 'in' and a process output if exists
                inputOutputMap.forEach((input, output) -> {
                    if(process.getIdentifier().equals(input.getProcess().getIdentifier()) &&
                            input.getName().equals(in.getName())){
                        //get the process with the output linked to 'in'
                        for(IProcess p : processList){
                            if(p.getIdentifier().equals(output.getProcess().getIdentifier())){
                                data[0] = p.getResults().get(output.getName());
                            }
                        }
                    }
                });
                //Do not add null value for optional input
                if(in.isOptional() && data[0] == null) {
                    processInData.put(in.getName(), in.getDefaultValue());
                }
                else {
                    processInData.put(in.getName(), data[0]);
                }
            }
        }
        return processInData;
    }

    @Override
    public boolean execute(LinkedHashMap<String, Object> inputDataMap) {
        if(!link()){
            return false;
        }
        Map<String, Object> dataMap = inputDataMap == null ?  new HashMap<>() : new HashMap<>(inputDataMap);
        //Iterate over the execution tree
        for(List<IProcess> processes : executionTree){
            for(IProcess process : processes){
                LinkedHashMap<String, Object> processInData = getInputDataMap(process, dataMap);
                //Do the before check
                for(IProcessCheck check : beforeList){
                    if(check.getProcess().getIdentifier().equals(process.getIdentifier())){
                        check.run(processInData);
                    }
                }
                //Execute the process
                process.execute(processInData);
                storeResults(process);
                //Do the after check
                for(IProcessCheck check : afterList){
                    if(check.getProcess().getIdentifier().equals(process.getIdentifier())){
                        check.run(processInData);
                    }
                }
            }
        }
        return true;
    }

    /**
     * Store the result of the {@link IProcess} execution.
     *
     * @param process {@link IProcess} which results should be stored.
     */
    private void storeResults(IProcess process){
        for(String key : process.getResults().keySet()){
            boolean isBetween = false;
            for(Map.Entry<IInOutPut, IInOutPut> entry : inputOutputMap.entrySet()){
                if(entry.getValue().getName().equals(key) &&
                        entry.getValue().getProcess().getIdentifier().equals(process.getIdentifier())){
                    isBetween = true;
                }
            }
            String alias = getAlias(key, process);
            if(alias != null){
                results.put(alias, process.getResults().get(key));
            }
            else {
                if(!isBetween){
                    results.put(key, process.getResults().get(key));
                }
            }
        }
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Map<String, Object> getResults() {
        return results;
    }

    @Override
    public ILinker link(IInOutPut... inOutPuts) {
        return new Linker(inOutPuts);
    }

    @Override
    public ICheckDataBuilder before(IProcess process) {
        IProcessCheck processCheck = new ProcessCheck(process);
        beforeList.add(processCheck);
        return new CheckDataBuilder(processCheck);
    }

    @Override
    public ICheckDataBuilder after(IProcess process) {
        IProcessCheck processCheck = new ProcessCheck(process);
        afterList.add(processCheck);
        return new CheckDataBuilder(processCheck);
    }

    /**
     * This class manage the link between inputs, outputs and aliases.
     */
    private class Linker implements ILinker {
        /** Array of inputs */
        private List<IInOutPut> inputList = new ArrayList<>();
        /** Array of outputs */
        private List<IInOutPut> outputList = new ArrayList<>();

        /**
         * Main constructor.
         *
         * @param inOutPuts Inputs or outputs to link.
         */
        private Linker(IInOutPut... inOutPuts){
            if(inOutPuts.length == 0){
                LOGGER.error("The input/output list should not be empty.");
            }
            boolean allInputs = true;
            boolean allOutputs = true;
            for(IInOutPut inOutPut : inOutPuts){
                if(inOutPut.getProcess().getInputs().stream().noneMatch(input -> input.getName().equals(inOutPut.getName()))) {
                    allInputs = false;
                }
                if(inOutPut.getProcess().getOutputs().stream().noneMatch(output -> output.getName().equals(inOutPut.getName()))) {
                    allOutputs = false;
                }
            }
            if(!allInputs && !allOutputs){
                LOGGER.error("Input and outputs should not be mixed.");
            }
            else if(allInputs){
                inputList.addAll(Arrays.asList(inOutPuts));
            }
            else{
                outputList.addAll(Arrays.asList(inOutPuts));
            }
        }

        @Override
        public void to(IInOutPut... inOutPuts) {
            if(inOutPuts.length == 0){
                LOGGER.error("The input/output list should not be empty.");
                return;
            }
            if(Arrays.stream(inOutPuts).anyMatch(iInOutPut -> iInOutPut instanceof IInput) &&
                    Arrays.stream(inOutPuts).anyMatch(iInOutPut -> iInOutPut instanceof IOutput)){
                LOGGER.error("Input and outputs should not be mixed.");
                return;
            }
            if(!inputList.isEmpty()) {
                if(Arrays.stream(inOutPuts).noneMatch(iInOutPut -> iInOutPut instanceof IOutput)){
                    LOGGER.error("Inputs should be link to outputs.");
                    return;
                }
                outputList.addAll(Arrays.asList(inOutPuts));
            }
            else if(!outputList.isEmpty()) {
                if(Arrays.stream(inOutPuts).noneMatch(iInOutPut -> iInOutPut instanceof IInput)){
                    LOGGER.error("Outputs should be link to inputs.");
                    return;
                }
                inputList.addAll(Arrays.asList(inOutPuts));
            }
            for (IInOutPut input : inputList) {
                for(IInOutPut output : outputList) {
                    inputOutputMap.put(input, output);
                }
            }
        }

        @Override
        public void to(String alias) {
            List<IInOutPut> inOutPuts = new ArrayList<>();
            inOutPuts.addAll(inputList);
            inOutPuts.addAll(outputList);
            for (IInOutPut inOutPut : inOutPuts) {
                if(aliases.get(alias) != null ) {
                    aliases.get(alias).add(inOutPut);
                }
                else {
                    aliases.put(alias, new ArrayList<>(inOutPuts));
                }
            }
            if(!inputList.isEmpty() && inputList.stream().noneMatch(iInOutPut -> iInOutPut.getName().equals(alias))){
                inputs.add(new Input(null, alias));
            }
            if(!outputList.isEmpty() && outputList.stream().noneMatch(iInOutPut -> iInOutPut.getName().equals(alias))){
                outputs.add(new Output(null, alias));
            }
        }
    }

    @Override
    public boolean call(LinkedHashMap<String, Object> inputDataMap){
        return execute(inputDataMap);
    }
}
