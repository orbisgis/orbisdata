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

import org.orbisgis.processmanagerapi.IProcess;
import org.orbisgis.processmanagerapi.IProcessMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of IProcessMapper.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public class ProcessMapper implements IProcessMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessMapper.class);

    private List<IProcess> processList;
    private Map<ProcessOutput, ProcessInput> inOutMap;
    private Map<String, Class> inputs;
    private Map<String, Class> outputs;
    private List<List<IProcess>> executionTree;
    private Map<String, Object> results;
    private List<Link> linkingList;

    public ProcessMapper(){
        linkingList = new ArrayList<>();
    }

    private void link(){
        processList = new ArrayList<>();
        inOutMap = new HashMap<>();
        inputs = new HashMap<>();
        outputs = new HashMap<>();
        executionTree = new ArrayList<>();
        results = new HashMap<>();

        List<ProcessInput> availableIn = new ArrayList<>();

        //Build the map linking the inputs and outputs.
        linkingList.forEach(link -> {
            String output = link.getOutName();
            String input = link.getInName();
            IProcess outputProcess = link.getOutProcess();
            IProcess inputProcess = link.getInProcess();
            if (getProcess(outputProcess.getIdentifier()) == null) {
                processList.add(outputProcess);
            }
            if (getProcess(inputProcess.getIdentifier()) == null) {
                processList.add(inputProcess);
            }
            inOutMap.put(new ProcessOutput(output, outputProcess.getIdentifier()),
                    new ProcessInput(input, inputProcess.getIdentifier()));
        });

        //Get all th inputs and outputs of the processes.
        for (IProcess process : processList) {
            if(process.getInputs() != null) {
                process.getInputs().forEach((key, value) -> {
                    boolean isBetween = false;
                    for (Map.Entry<ProcessOutput, ProcessInput> entry : inOutMap.entrySet()) {
                        if (entry.getValue().getInput().equals(key) &&
                                entry.getValue().getProcessId().equals(process.getIdentifier())) {
                            isBetween = true;
                        }
                    }
                    if (!isBetween) {
                        inputs.put(key, value);
                        availableIn.add(new ProcessInput(key, process.getIdentifier()));
                    }
                });
            }
            process.getOutputs().forEach((key, value) -> {
                boolean isBetween = false;
                for(Map.Entry<ProcessOutput, ProcessInput> entry : inOutMap.entrySet()) {
                    if (entry.getKey().getOutput().equals(key) &&
                            entry.getKey().getProcessId().equals(process.getIdentifier())) {
                        isBetween = true;
                    }
                }
                if (!isBetween) {
                    outputs.put(key, value);
                }
            });
        }

        //Build the execution tree
        List<IProcess> processes = new ArrayList<>(processList);
        int i = 0;
        List<ProcessInput> newIn = new ArrayList<>();
        do {
            availableIn.addAll(newIn);
            newIn = new ArrayList<>();
            executionTree.add(new ArrayList<>());
            for (Iterator<IProcess> iterator = processes.iterator(); iterator.hasNext(); ) {
                IProcess process = iterator.next();
                boolean isAllInput = true;
                if(process.getInputs() != null) {
                    for (String input : process.getInputs().keySet()) {
                        boolean isInputAvailable = false;
                        for (ProcessInput processInput : availableIn) {
                            if (processInput.getInput().equals(input) &&
                                    processInput.getProcessId().equals(process.getIdentifier())) {
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
                        for (Map.Entry<ProcessOutput, ProcessInput> entry : inOutMap.entrySet()) {
                            if (entry.getKey().getOutput().equals(name) &&
                                    entry.getKey().getProcessId().equals(process.getIdentifier())) {
                                newIn.add(entry.getValue());
                            }
                        }
                    }
                    iterator.remove();
                }
            }
            i++;
        } while(!processes.isEmpty() && !executionTree.get(i-1).isEmpty());

        if(!processes.isEmpty()){
            LOGGER.error("Unable to link the processes '"+
                    processes.stream().map(IProcess::getTitle).collect(Collectors.joining(","))+"'");
        }
    }


    @Override
    public Set<String> getInputNames() {
        return getInputDefinitions().keySet();
    }

    @Override
    public Set<String> getOutputNames() {
        return getOutputDefinitions().keySet();
    }

    @Override
    public Map<String, Class> getInputDefinitions() {
        return inputs;
    }

    @Override
    public Map<String, Class> getOutputDefinitions() {
        return outputs;
    }

    @Override
    public boolean execute(Map<String, Object> inputDataMap) {
        link();

        Map<String, Object> dataMap = inputDataMap == null ?  new HashMap<>() : new HashMap<>(inputDataMap);
        /*if(inputs != null && dataMap.size() != inputs.size()){
            LOGGER.error("The number of the input data map and the number of process input are different.");
            return false;
        }*/
        for(List<IProcess> processes : executionTree){
            for(IProcess process : processes){
                Map<String, Object> processInData = new HashMap<>();
                if(process.getInputs() != null) {
                    for (String in : process.getInputs().keySet()) {
                        processInData.put(in, dataMap.get(in));
                    }
                }
                process.execute(processInData);
                for(String key : process.getResults().keySet()){
                    boolean isBetween = false;
                    for(Map.Entry<ProcessOutput, ProcessInput> entry : inOutMap.entrySet()){
                        if(entry.getKey().getOutput().equals(key) &&
                                entry.getKey().getProcessId().equals(process.getIdentifier())){
                            dataMap.put(entry.getValue().getInput(), process.getResults().get(key));
                            isBetween = true;
                        }
                    }
                    if(!isBetween){
                        results.put(key, process.getResults().get(key));
                    }
                }
            }
        }
        return true;
    }

    @Override
    public Map<String, Object> getResults() {
        return results;
    }

    @Override
    public void link(Map<String, IProcess> map) {
        if (map == null || map.isEmpty()) {
            LOGGER.error("The in/output is null or empty");
        }
        else if (map.size() != 2) {
            LOGGER.error("The in/output should contains two value");
        }
        else {
            Link link = new Link();
            linkingList.add(link);
            Iterator<Map.Entry<String, IProcess>> it = map.entrySet().iterator();
            Map.Entry<String, IProcess> entry = it.next();
            link.setOut(entry.getKey(), entry.getValue());
            entry = it.next();
            link.setIn(entry.getKey(), entry.getValue());
        }
    }

    private IProcess getProcess(String identifier){
        return processList.stream().filter(process -> process.getIdentifier().equals(identifier)).findFirst().orElse(null);
    }

    //TODO remove that as the process id and inoutput will be merged as string
    private class ProcessOutput{
        private String output;
        private String processId;

        ProcessOutput(String output, String processId){
            this.output = output;
            this.processId = processId;
        }

        String getProcessId(){
            return processId;
        }

        String getOutput(){
            return output;
        }

        @Override public String toString(){return output+":"+processId;}
    }

    private class ProcessInput{
        private String input;
        private String processId;

        ProcessInput(String input, String processId){
            this.input = input;
            this.processId = processId;
        }

        String getProcessId(){
            return processId;
        }

        String getInput(){
            return input;
        }

        @Override public String toString(){return input+":"+processId;}
    }

    private class Link {
        private String inName;
        private IProcess inProcess;
        private String outName;
        private IProcess outProcess;

        void setIn(String inName, IProcess inProcess){
            this.inName = inName;
            this.inProcess = inProcess;
        }

        void setOut(String outName, IProcess outProcess){
            this.outName = outName;
            this.outProcess = outProcess;
        }

        String getInName(){
            return inName;
        }

        String getOutName(){
            return outName;
        }

        IProcess getInProcess(){
            return inProcess;
        }

        IProcess getOutProcess(){
            return outProcess;
        }
    }
}
