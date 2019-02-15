package org.orbisgis.processmanager;

import org.orbisgis.processmanagerapi.IProcess;
import org.orbisgis.processmanagerapi.IProcessMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class ProcessMapper implements IProcessMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessMapper.class);

    private List<IProcess> processList;
    private Map<ProcessOutput, ProcessInput> inOutMap;
    private Map<String, Class> inputs;
    private Map<String, Class> outputs;
    private List<List<IProcess>> executionTree;
    private Map<String, Object> results;

    public ProcessMapper(List<Map<String, IProcess>> linkingMap){
        processList = new ArrayList<>();
        inOutMap = new HashMap<>();
        inputs = new HashMap<>();
        outputs = new HashMap<>();
        executionTree = new ArrayList<>();
        results = new HashMap<>();

        List<ProcessInput> availableIn = new ArrayList<>();

        //Build the map linking the inputs and outputs.
        linkingMap.forEach(map -> {
            if (map.size() != 2) {
                LOGGER.error("The linking map should contains pairs of <String, IProcess>");
                return;
            }
            Iterator<String> i1 = map.keySet().iterator();
            String output = i1.next();
            String input = i1.next();
            Iterator<IProcess> i2 = map.values().iterator();
            IProcess outputProcess = i2.next();
            IProcess inputProcess = i2.next();
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
            process.getInputs().forEach((key, value) -> {
                boolean isBetween = false;
                for(Map.Entry<ProcessOutput, ProcessInput> entry : inOutMap.entrySet()) {
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
        LOGGER.info("finished");
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
        Map<String, Object> dataMap = new HashMap<>(inputDataMap);
        if(inputs != null && dataMap.size() != inputs.size()){
            LOGGER.error("The number of the input data map and the number of process input are different.");
            return false;
        }
        for(List<IProcess> processes : executionTree){
            for(IProcess process : processes){
                Map<String, Object> processInData = new HashMap<>();
                for(String in : process.getInputs().keySet()){
                    processInData.put(in, dataMap.get(in));
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

    private IProcess getProcess(String identifier){
        return processList.stream().filter(process -> process.getIdentifier().equals(identifier)).findFirst().orElse(null);
    }

    //TODO remove that as the process and inoutput are merged as string
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
}
