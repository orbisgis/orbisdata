/*
 * Bundle Process is part of the OrbisGIS platform
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
 * Process is distributed under LGPL 3 license.
 *
 * Copyright (C) 2018 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * Process is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Process is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Process. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.process;

import groovy.lang.GroovyInterceptable;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import org.codehaus.groovy .runtime.InvokerHelper;
import org.orbisgis.process.api.ILinker;
import org.orbisgis.process.api.IProcess;
import org.orbisgis.process.api.IProcessMapper;
import org.orbisgis.process.api.check.ICheckDataBuilder;
import org.orbisgis.process.api.check.IProcessCheck;
import org.orbisgis.process.api.inoutput.IInOutPut;
import org.orbisgis.process.api.inoutput.IInput;
import org.orbisgis.process.api.inoutput.IOutput;
import org.orbisgis.process.check.CheckDataBuilder;
import org.orbisgis.process.check.ProcessCheck;
import org.orbisgis.process.inoutput.Input;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of IProcessMapper. This class should not be used will using a IProcess executing and mapping
 * processes is easier.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC 2019-2020)
 */
public class ProcessMapper implements IProcessMapper, GroovyObject, GroovyInterceptable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessMapper.class);

    private LinkedList<IInput> inputs;
    private LinkedList<IOutput> outputs;
    private List<List<IProcess>> executionTree;
    private Map<String, Object> results;
    /**
     * List of all the processes used in this mapper.
     */
    private List<IProcess> processList;
    /**
     * List of check to do before process execution
     */
    private final List<IProcessCheck> beforeList;
    /**
     * List of check to do after process execution
     */
    private final List<IProcessCheck> afterList;
    /**
     * List of linker used to rebuild the execution.
     */
    private List<Linker> linkerList;
    /**
     * Map with the input as key and the output as value.
     */
    private final Map<IInOutPut, IInOutPut> inputOutputMap;
    /**
     * Map of the aliases as key and the list of input/output as value.
     */
    private final Map<String, List<IInOutPut>> aliases;
    /**
     * Title of the mapper.
     */
    private final String title;

    /**
     * Groovy {@link MetaClass}.
     */
    protected MetaClass metaClass = InvokerHelper.getMetaClass(ProcessMapper.class);

    /**
     * Main empty constructor.
     */
    public ProcessMapper() {
        this("mapper_" + UUID.randomUUID());
    }

    /**
     * Main constructor.
     */
    public ProcessMapper(String title) {
        this.title = title;
        beforeList = new ArrayList<>();
        afterList = new ArrayList<>();
        linkerList = new ArrayList<>();
        inputs = new LinkedList<>();
        outputs = new LinkedList<>();
        aliases = new HashMap<>();
        inputOutputMap = new HashMap<>();
    }

    /**
     * Return the alias of the given input or output.
     *
     * @param name    Name of the alias.
     * @param process {@link IProcess} containing the input/output with the alias.
     * @return The alias name if there is one, null otherwise.
     */
    private String getAlias(String name, IProcess process) {
        return aliases.entrySet()
                .stream()
                .filter(entry ->
                        entry.getValue().stream().map(IInOutPut::getName).map(Optional::get).anyMatch(n -> n.equals(name)) &&
                        entry.getValue().stream().map(IInOutPut::getProcess).map(Optional::get).anyMatch(p -> p.equals(process)))
                .findFirst()
                .orElse(new AbstractMap.SimpleEntry<>(null, null))
                .getKey();
    }

    /**
     * Fill the process list.
     */
    private void fillProcessList() {
        processList.addAll(
                inputOutputMap.keySet().stream()
                        .map(IInOutPut::getProcess)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList()));
        processList.addAll(
                inputOutputMap.values().stream()
                        .map(IInOutPut::getProcess)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList()));
        processList.addAll(
                aliases.values().stream()
                        .flatMap(Collection::stream)
                        .map(IInOutPut::getProcess)
                        .map(Optional::get)
                        .collect(Collectors.toList()));
        //Keep distinct processes
        processList = processList.stream().distinct().collect(Collectors.toList());
    }

    private Collection<IInOutPut> collectInput(IInput input, IProcess process) {
        Collection<IInOutPut> collection = new ArrayList<>();
        if (inputOutputMap.keySet().stream().noneMatch(in -> in.equals(input))) {
            if (getAlias(input.getName().orElse(null), process) == null) {
                inputs.add(input);
            }
            collection.add(new Input().process(process).name(input.getName().orElse(null)));
        }
        return collection;
    }

    private void collectOutput(IOutput output, IProcess process) {
        if (inputOutputMap.values().stream().noneMatch(out -> out.equals(output)) &&
                getAlias(output.getName().orElse(null), process) == null) {
            outputs.add(output);
        }
    }

    /**
     * Collect all the not linked inputs/outputs to set the {@link ProcessMapper} inputs/outputs.
     *
     * @return The list of available inputs.
     */
    private List<IInOutPut> collectInputOutput() {
        List<IInOutPut> availableIn = new ArrayList<>();
        for (IProcess process : processList) {
            process.getInputs().forEach(input -> availableIn.addAll(collectInput(input, process)));
            process.getOutputs().forEach(output -> collectOutput(output, process));
        }
        return availableIn;
    }

    private void buildExecutionTreeLevel(List<IProcess> iterableProcessList, List<IInOutPut> availableIn,
                                         int i, List<IInOutPut> newIn) {
        for (Iterator<IProcess> iterator = iterableProcessList.iterator(); iterator.hasNext(); ) {
            IProcess process = iterator.next();
            boolean isAllInput = true;
            for (IInput input : process.getInputs()) {
                boolean isInputAvailable = false;
                for (IInOutPut processInput : availableIn) {
                    if (processInput.equals(input)) {
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
                for (IOutput output : process.getOutputs()) {
                    for (Map.Entry<IInOutPut, IInOutPut> entry : inputOutputMap.entrySet()) {
                        if (entry.getValue().equals(output)) {
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
    private boolean link() {
        executionTree = new ArrayList<>();
        results = new HashMap<>();
        processList = new ArrayList<>();

        linkerList.stream().map(Linker::getAliases).forEach(aliases::putAll);
        linkerList.stream().map(Linker::getInputOutputMap).forEach(inputOutputMap::putAll);
        linkerList.stream().map(Linker::getInputs).forEach(inputs::addAll);
        linkerList.stream().map(Linker::getOutputs).forEach(outputs::addAll);

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
        } while (!iterableProcessList.isEmpty() && !executionTree.get(i - 1).isEmpty());

        if (!iterableProcessList.isEmpty()) {
            LOGGER.error("Unable to link the processes '" +
                    iterableProcessList.stream()
                            .map(IProcess::getTitle)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .collect(Collectors.joining(",")) + "'");
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
        mapper.processList = new ArrayList<>(processList);
        mapper.executionTree = new ArrayList<>(executionTree);
        mapper.linkerList = new ArrayList<>(linkerList);
        mapper.inputs = this.inputs.stream().map(IInput::copy).collect(Collectors.toCollection(LinkedList::new));
        mapper.outputs = this.outputs.stream().map(IOutput::copy).collect(Collectors.toCollection(LinkedList::new));
        return mapper;
    }

    @Override
    public IProcessMapper copy() {
        ProcessMapper mapper = new ProcessMapper();
        mapper.processList = new ArrayList<>(processList);
        mapper.executionTree = new ArrayList<>(executionTree);
        mapper.linkerList = new ArrayList<>(linkerList);
        mapper.inputs = this.inputs.stream().map(IInput::copy).collect(Collectors.toCollection(LinkedList::new));
        mapper.outputs = this.outputs.stream().map(IOutput::copy).collect(Collectors.toCollection(LinkedList::new));
        return mapper;
    }

    /**
     * Return a {@link LinkedHashMap} with the input data for the given {@link IProcess} execution.
     *
     * @param process {@link IProcess} which will executed.
     * @param dataMap {@link Map} with the {@link ProcessMapper} input data.
     * @return A {@link LinkedHashMap} with the input data for the given {@link IProcess}.
     */
    private LinkedHashMap<String, Object> getInputDataMap(IProcess process, Map<String, Object> dataMap) {
        LinkedHashMap<String, Object> processInData = new LinkedHashMap<>();
        for (IInput in : process.getInputs()) {
            //Try to get the data directly from the out of a process
            String alias = getAlias(in.getName().orElse(null), process);
            final Object[] data = new Object[1];
            if (alias != null) {
                data[0] = dataMap.get(alias);
            } else {
                data[0] = dataMap.get(in.getName().orElse(null));
            }
            //Get the link between the input 'in' and a process output if exists
            inputOutputMap.forEach((input, output) -> {
                if (process.equals(input.getProcess().orElse(null)) &&
                        input.getName().equals(in.getName())) {
                    //get the process with the output linked to 'in'
                    for (IProcess p : processList) {
                        if (p.equals(output.getProcess().orElse(null))) {
                            data[0] = p.getResults().get(output.getName().orElse(null));
                        }
                    }
                }
            });
            //Do not add null value for optional input
            if (in.isOptional() && data[0] == null) {
                processInData.put(in.getName().orElse(null), in.getDefaultValue().orElse(null));
            } else {
                processInData.put(in.getName().orElse(null), data[0]);
            }
        }
        return processInData;
    }

    @Override
    public boolean execute(LinkedHashMap<String, Object> inputDataMap) {
        if (!link()) {
            return false;
        }
        Map<String, Object> dataMap = inputDataMap == null ? new HashMap<>() : new HashMap<>(inputDataMap);
        //Iterate over the execution tree
        for (List<IProcess> processes : executionTree) {
            for (IProcess process : processes) {
                LinkedHashMap<String, Object> processInData = getInputDataMap(process, dataMap);
                //Do the before check
                beforeList.stream()
                        .filter(check -> check.getProcess().isPresent())
                        .filter(check -> check.getProcess().get().getIdentifier().equals(process.getIdentifier()))
                        .forEach(check -> check.run(processInData));
                //Execute the process
                process.execute(processInData);
                storeResults(process);
                //Do the after check
                afterList.stream()
                        .filter(check -> check.getProcess().isPresent())
                        .filter(check -> check.getProcess().get().getIdentifier().equals(process.getIdentifier()))
                        .forEach(check -> check.run(processInData));
            }
        }
        return true;
    }

    /**
     * Store the result of the {@link IProcess} execution.
     *
     * @param process {@link IProcess} which results should be stored.
     */
    private void storeResults(IProcess process) {
        for (String key : process.getResults().keySet()) {
            boolean isBetween = inputOutputMap.values().stream()
                    .anyMatch(value -> key.equals(value.getName().orElse(null)) &&
                            process.equals(value.getProcess().orElse(null)));
            String alias = getAlias(key, process);
            if (alias != null) {
                results.put(alias, process.getResults().get(key));
            } else {
                if (!isBetween) {
                    results.put(key, process.getResults().get(key));
                }
            }
        }
    }

    @Override
    public Optional<String> getTitle() {
        return Optional.ofNullable(title);
    }

    @Override
    public Map<String, Object> getResults() {
        return results;
    }

    @Override
    public ILinker link(IInOutPut... inOutPuts) {
        Linker linker = new Linker(inOutPuts);
        linkerList.add(linker);
        return linker;
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

    @Override
    public boolean call(LinkedHashMap<String, Object> inputDataMap) {
        return execute(inputDataMap);
    }

    @Override
    public Object getProperty(String propertyName){
        if(propertyName != null) {
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

    @Override
    public Object invokeMethod(String name, Object args) {
        if(name != null) {
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
    public MetaClass getMetaClass() {
        return metaClass;
    }

    @Override
    public void setMetaClass(MetaClass metaClass) {
        this.metaClass = metaClass == null ? InvokerHelper.getMetaClass(this.getClass()) : metaClass;
    }
}
