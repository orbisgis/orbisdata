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

    private Map<ProcessOutput, ProcessInput> inOutMap;
    private Map<String, Class> inputs;
    private Map<String, Class> outputs;
    private List<List<String>> executionTree;
    private Map<String, Object> results;
    private List<Link> linkingList;
    private Map<String, IProcess> processMap;

    public ProcessMapper(){
        linkingList = new ArrayList<>();
    }

    private String getUuidOfProcess(IProcess process){
        for(Map.Entry<String, IProcess> entry : processMap.entrySet()){
            if(entry.getValue().equals(process)){
                return entry.getKey();
            }
        }
        return null;
    }

    private void link(){
        inOutMap = new HashMap<>();
        inputs = new HashMap<>();
        outputs = new HashMap<>();
        executionTree = new ArrayList<>();
        results = new HashMap<>();
        processMap = new HashMap<>();

        List<ProcessInput> availableIn = new ArrayList<>();

        //Build the map linking the inputs and outputs.
        linkingList.forEach(link -> {
            String output = link.getOutName();
            String input = link.getInName();
            IProcess outputProcess = link.getOutProcess();
            IProcess inputProcess = link.getInProcess();
            if (!processMap.values().contains(outputProcess)) {
                processMap.put(UUID.randomUUID().toString(), outputProcess);
            }
            if (!processMap.values().contains(inputProcess)) {
                processMap.put(UUID.randomUUID().toString(), inputProcess);
            }
            inOutMap.put(new ProcessOutput(output, getUuidOfProcess(outputProcess)),
                    new ProcessInput(input, getUuidOfProcess(inputProcess)));
        });

        //Get all the inputs and outputs of the processes.
        for (String uuid : processMap.keySet()) {
            IProcess process = processMap.get(uuid);
            if(process.getInputs() != null) {
                process.getInputs().forEach((key, value) -> {
                    boolean isBetween = false;
                    for (Map.Entry<ProcessOutput, ProcessInput> entry : inOutMap.entrySet()) {
                        if (entry.getValue().getInput().equals(key) &&
                                entry.getValue().getProcessId().equals(uuid)) {
                            isBetween = true;
                        }
                    }
                    if (!isBetween) {
                        inputs.put(key, value);
                        availableIn.add(new ProcessInput(key, uuid));
                    }
                });
            }
            process.getOutputs().forEach((key, value) -> {
                boolean isBetween = false;
                for(Map.Entry<ProcessOutput, ProcessInput> entry : inOutMap.entrySet()) {
                    if (entry.getKey().getOutput().equals(key) &&
                            entry.getKey().getProcessId().equals(uuid)) {
                        isBetween = true;
                    }
                }
                if (!isBetween) {
                    outputs.put(key, value);
                }
            });
        }

        //Build the execution tree
        List<String> processes = new ArrayList<>(processMap.keySet());
        int i = 0;
        List<ProcessInput> newIn = new ArrayList<>();
        do {
            availableIn.addAll(newIn);
            newIn = new ArrayList<>();
            executionTree.add(new ArrayList<>());
            for (Iterator<String> iterator = processes.iterator(); iterator.hasNext(); ) {
                String uuid = iterator.next();
                IProcess process = processMap.get(uuid);
                boolean isAllInput = true;
                if(process.getInputs() != null) {
                    for (String input : process.getInputs().keySet()) {
                        boolean isInputAvailable = false;
                        for (ProcessInput processInput : availableIn) {
                            if (processInput.getInput().equals(input) &&
                                    processInput.getProcessId().equals(uuid)) {
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
                    executionTree.get(i).add(uuid);
                    for (String name : process.getOutputs().keySet()) {
                        for (Map.Entry<ProcessOutput, ProcessInput> entry : inOutMap.entrySet()) {
                            if (entry.getKey().getOutput().equals(name) &&
                                    entry.getKey().getProcessId().equals(uuid)) {
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
                    processes.stream().map(s -> processMap.get(s).getTitle()).collect(Collectors.joining(","))+"'");
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
        for(List<String> uuids : executionTree){
            for(String uuid : uuids){
                IProcess process = processMap.get(uuid);
                Map<String, Object> processInData = new HashMap<>();
                if(process.getInputs() != null) {
                    for (String in : process.getInputs().keySet()) {
                        //Try to get the data directly from the out of a process
                        Object data = dataMap.get(in);
                        //Get the link between the input 'in' and a process output if exists
                        for(Link link : linkingList){
                            if(uuid.equals(getUuidOfProcess(link.getInProcess())) &&
                                    link.getInName().equals(in)){
                                //get the process with the output linked to 'in'
                                for(String id : processMap.keySet()){
                                    if(id.equals(getUuidOfProcess(link.getOutProcess()))){
                                        data = processMap.get(id).getResults().get(link.getOutName());
                                    }
                                }
                            }
                        }
                        processInData.put(in, data);
                    }
                }
                process.execute(processInData);
                for(String key : process.getResults().keySet()){
                    boolean isBetween = false;
                    for(Map.Entry<ProcessOutput, ProcessInput> entry : inOutMap.entrySet()){
                        if(entry.getKey().getOutput().equals(key) &&
                                entry.getKey().getProcessId().equals(uuid)){
                            //dataMap.put(entry.getValue().getInput(), process.getResults().get(key));
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
        if (processMap.isEmpty()) {
            return null;
        }
        return processMap.values()
                .stream()
                .filter(iProcess -> iProcess.getIdentifier().equals(identifier))
                .findFirst()
                .orElse(null);

    }

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
