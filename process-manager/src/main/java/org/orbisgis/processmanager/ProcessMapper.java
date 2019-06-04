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

import org.orbisgis.processmanagerapi.*;
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

    private Map<String, Class> inputMap = new HashMap<>();
    private Map<String, Class> outputMap = new HashMap<>();
    private List<List<IProcess>> executionTree;
    private Map<String, Object> results;
    /** Map with the input as key and the output as value. */
    private Map<IProcessInOutPut, IProcessInOutPut> inputOutputMap;
    /** List of all the processes used in this mapper. */
    private List<IProcess> processList;
    /** Map of the aliases as key and the list of input/output as value. */
    private Map<String, List<IProcessInOutPut>> aliases;
    /** List of check to do before process execution */
    private List<IProcessCheck> beforeList = new ArrayList<>();
    /** List of check to do after process execution */
    private List<IProcessCheck> afterList = new ArrayList<>();

    /**
     * Main constructor.
     */
    public ProcessMapper(){
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
        for(Map.Entry<String, List<IProcessInOutPut>> list : aliases.entrySet()){
            for(IProcessInOutPut inOutPut : list.getValue()) {
                if (inOutPut.getName().equals(name) && inOutPut.getProcess().getIdentifier().equals(process.getIdentifier())) {
                    return list.getKey();
                }
            }
        }
        return null;
    }

    /**
     * Link the inputs, the outputs and the aliases to prepare the execution.
     */
    private void link(){
        executionTree = new ArrayList<>();
        results = new HashMap<>();
        processList = new ArrayList<>();

        List<IProcessInOutPut> availableIn = new ArrayList<>();

        //Build the map linking the inputs and outputs.
        inputOutputMap.forEach((input, output) -> {
            if (!processList.contains(input.getProcess())) {
                processList.add(input.getProcess());
            }
            if (!processList.contains(output.getProcess())) {
                processList.add(output.getProcess());
            }
        });

        //Get all the inputs and outputs of the processes.
        for (IProcess process : processList) {
            if(process.getInputs() != null) {
                process.getInputs().forEach((key, value) -> {
                    boolean isBetween = false;
                    for (Map.Entry<IProcessInOutPut, IProcessInOutPut> entry : inputOutputMap.entrySet()) {
                        if (entry.getKey().getName().equals(key) &&
                                entry.getKey().getProcess().getIdentifier().equals(process.getIdentifier())) {
                            isBetween = true;
                        }
                    }
                    if (!isBetween) {
                        inputMap.put(key, value);
                        availableIn.add(new ProcessInOutPut(process, key));
                    }
                });
            }
            process.getOutputs().forEach((key, value) -> {
                boolean isBetween = false;
                for(Map.Entry<IProcessInOutPut, IProcessInOutPut> entry : inputOutputMap.entrySet()) {
                    if (entry.getValue().getName().equals(key) &&
                            entry.getValue().getProcess().getIdentifier().equals(process.getIdentifier())) {
                        isBetween = true;
                    }
                }
                if (!isBetween) {
                    outputMap.put(key, value);
                }
            });
        }

        //Build the execution tree
        List<IProcess> iterableProcessList = new ArrayList<>(processList);
        int i = 0;
        List<IProcessInOutPut> newIn = new ArrayList<>();
        do {
            availableIn.addAll(newIn);
            newIn = new ArrayList<>();
            executionTree.add(new ArrayList<>());
            for (Iterator<IProcess> iterator = iterableProcessList.iterator(); iterator.hasNext(); ) {
                IProcess process = iterator.next();
                boolean isAllInput = true;
                if (process.getInputs() != null) {
                    for (String input : process.getInputs().keySet()) {
                        boolean isInputAvailable = false;
                        for (IProcessInOutPut processInput : availableIn) {
                            if (processInput.getName().equals(input) &&
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
                    for (String name : process.getOutputs().keySet()) {
                        for (Map.Entry<IProcessInOutPut, IProcessInOutPut> entry : inputOutputMap.entrySet()) {
                            if (entry.getValue().getName().equals(name) &&
                                    entry.getValue().getProcess().getIdentifier().equals(process.getIdentifier())) {
                                newIn.add(entry.getKey());
                            }
                        }
                    }
                    iterator.remove();
                }
            }
            i++;
        } while(!iterableProcessList.isEmpty() && !executionTree.get(i-1).isEmpty());

        if(!iterableProcessList.isEmpty()){
            LOGGER.error("Unable to link the processes '"+
                    iterableProcessList.stream().map(IProcess::getTitle).collect(Collectors.joining(","))+"'");
        }
    }

    @Override
    public Map<String, Class> getInputs() {
        return inputMap;
    }

    @Override
    public Map<String, Class> getOutputs() {
        return outputMap;
    }

    @Override
    public IProcess newInstance() {
        ProcessMapper mapper = new ProcessMapper();
        mapper.aliases = aliases;
        mapper.processList = new ArrayList<>(processList);
        mapper.inputOutputMap = new HashMap<>(inputOutputMap);
        mapper.executionTree = new ArrayList<>(executionTree);
        mapper.inputMap = new HashMap<>(inputMap);
        mapper.outputMap = new HashMap<>(outputMap);
        return mapper;
    }

    @Override
    public boolean execute(LinkedHashMap<String, Object> inputDataMap) {
        link();
        Map<String, Object> dataMap = inputDataMap == null ?  new HashMap<>() : new HashMap<>(inputDataMap);
        //Iterate over the execution tree
        for(List<IProcess> processes : executionTree){
            for(IProcess process : processes){
                LinkedHashMap<String, Object> processInData = new LinkedHashMap<>();
                if(process.getInputs() != null) {
                    for (String in : process.getInputs().keySet()) {
                        //Try to get the data directly from the out of a process
                        String alias = getAlias(in, process);
                        final Object[] data = new Object[1];
                        if(alias != null){
                            data[0] = dataMap.get(alias);
                        }
                        else {
                            data[0] = dataMap.get(in);
                        }
                        //Get the link between the input 'in' and a process output if exists
                        inputOutputMap.forEach((input, output) -> {
                            if(process.getIdentifier().equals(input.getProcess().getIdentifier()) &&
                                    input.getName().equals(in)){
                                //get the process with the output linked to 'in'
                                for(IProcess p : processList){
                                    if(p.getIdentifier().equals(output.getProcess().getIdentifier())){
                                        data[0] = p.getResults().get(output.getName());
                                    }
                                }
                            }
                        });
                        processInData.put(in, data[0]);
                    }
                }
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
            for(Map.Entry<IProcessInOutPut, IProcessInOutPut> entry : inputOutputMap.entrySet()){
                if(entry.getKey().getName().equals(key) &&
                        entry.getKey().getProcess().getIdentifier().equals(process.getIdentifier())){
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
        return null;
    }

    @Override
    public Map<String, Object> getResults() {
        return results;
    }

    @Override
    public ILinker link(IProcessInOutPut... inOutPuts) {
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
        private List<IProcessInOutPut> inputs = new ArrayList<>();
        /** Array of outputs */
        private List<IProcessInOutPut> outputs = new ArrayList<>();

        /**
         * Main constructor.
         *
         * @param inOutPuts Inputs or outputs to link.
         */
        private Linker(IProcessInOutPut... inOutPuts){
            if(inOutPuts.length == 0){
                LOGGER.error("The input/output list should not be empty.");
            }
            boolean allInputs = true;
            boolean allOutputs = true;
            for(IProcessInOutPut inOutPut : inOutPuts){
                if(!inOutPut.getProcess().getInputs().keySet().contains(inOutPut.getName())){
                    allInputs = false;
                }
                if(!inOutPut.getProcess().getOutputs().keySet().contains(inOutPut.getName())){
                    allOutputs = false;
                }
            }
            if(!allInputs && !allOutputs){
                LOGGER.error("Input and outputs should not be mixed.");
            }
            else if(allInputs){
                inputs.addAll(Arrays.asList(inOutPuts));
            }
            else{
                outputs.addAll(Arrays.asList(inOutPuts));
            }
        }

        @Override
        public void to(IProcessInOutPut... inOutPuts) {
            if(!inputs.isEmpty()) {
                boolean allOutputs = true;
                for(IProcessInOutPut inOutPut : inOutPuts){
                    if(!inOutPut.getProcess().getOutputs().keySet().contains(inOutPut.getName())){
                        allOutputs = false;
                    }
                }
                if(!allOutputs){
                    LOGGER.error("Inputs should be link to outputs.");
                    return;
                }
                outputs.addAll(Arrays.asList(inOutPuts));
            }
            if(!outputs.isEmpty()) {
                boolean allInputs = true;
                for(IProcessInOutPut inOutPut : inOutPuts){
                    if(!inOutPut.getProcess().getInputs().keySet().contains(inOutPut.getName())){
                        allInputs = false;
                    }
                }
                if(!allInputs){
                    LOGGER.error("Outputs should be link to inputs.");
                    return;
                }
                inputs.addAll(Arrays.asList(inOutPuts));
            }
            for (IProcessInOutPut input : inputs) {
                for(IProcessInOutPut output : outputs) {
                    inputOutputMap.put(input, output);
                }
            }
        }

        @Override
        public void to(String alias) {
            List<IProcessInOutPut> inOutPuts = new ArrayList<>();
            inOutPuts.addAll(inputs);
            inOutPuts.addAll(outputs);
            for (IProcessInOutPut inOutPut : inOutPuts) {
                if(aliases.get(alias) != null ) {
                    aliases.get(alias).add(inOutPut);
                }
                else {
                    aliases.put(alias, new ArrayList<>(inOutPuts));
                }
            }
            for (IProcessInOutPut inOutPut : inputs) {
                inputMap.put(alias, inOutPut.getProcess().getInputs().get(inOutPut.getName()));
            }
            for (IProcessInOutPut inOutPut : outputs) {
                outputMap.put(alias, inOutPut.getProcess().getOutputs().get(inOutPut.getName()));
            }
        }
    }

    @Deprecated
    public void link(LinkedHashMap<String, IProcess> map){
        Iterator<Map.Entry<String, IProcess>> it = map.entrySet().iterator();
        Map.Entry<String, IProcess> inputEntry = it.next();
        Map.Entry<String, IProcess> outputEntry = it.next();
        link(new ProcessInOutPut(inputEntry.getValue(), inputEntry.getKey()))
                .to(new ProcessInOutPut(outputEntry.getValue(), outputEntry.getKey()));
    }
}
