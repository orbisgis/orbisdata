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

import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.commons.annotations.Nullable;
import org.orbisgis.process.api.ILinker;
import org.orbisgis.process.api.inoutput.IInOutPut;
import org.orbisgis.process.api.inoutput.IInput;
import org.orbisgis.process.api.inoutput.IOutput;
import org.orbisgis.process.inoutput.Input;
import org.orbisgis.process.inoutput.Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * This class manage the link between inputs, outputs and aliases.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC 2019-2020)
 */
public class Linker implements ILinker {

    private static final Logger LOGGER = LoggerFactory.getLogger(Linker.class);
    /**
     * Array of inputs
     */
    private final List<IInOutPut> inputList = new ArrayList<>();
    /**
     * Array of outputs
     */
    private final List<IInOutPut> outputList = new ArrayList<>();
    /**
     * Map with the input as key and the output as value.
     */
    private final Map<IInOutPut, IInOutPut> inputOutputMap;
    /**
     * Map of the aliases as key and the list of input/output as value.
     */
    private final Map<String, List<IInOutPut>> aliases;
    private final LinkedList<IInput> inputs = new LinkedList<>();
    private final LinkedList<IOutput> outputs = new LinkedList<>();

    /**
     * Main constructor.
     *
     * @param inOutPuts Inputs or outputs to link.
     */
    protected Linker(@Nullable IInOutPut... inOutPuts) {
        inputOutputMap = new HashMap<>();
        aliases = new HashMap<>();
        if (inOutPuts == null || inOutPuts.length == 0) {
            LOGGER.error("The input/output list should not be empty.");
            return;
        }
        boolean allInputs = true;
        boolean allOutputs = true;
        for (IInOutPut inOutPut : inOutPuts) {
            if (!inOutPut.getProcess().isPresent()) {
                allOutputs = false;
                allInputs = false;
            }
            else {
                if (inOutPut.getProcess().get().getInputs().stream()
                        .noneMatch(input -> input.getName().equals(inOutPut.getName()))) {
                    allInputs = false;
                }
                if (inOutPut.getProcess().get().getOutputs().stream()
                        .noneMatch(output -> output.getName().equals(inOutPut.getName()))) {
                    allOutputs = false;
                }
            }
        }
        if (!allInputs && !allOutputs) {
            LOGGER.error("Input and outputs should not be mixed.");
        } else if (allInputs) {
            inputList.addAll(Arrays.asList(inOutPuts));
        } else {
            outputList.addAll(Arrays.asList(inOutPuts));
        }
    }

    @Override
    public void to(@Nullable IInOutPut... inOutPuts) {
        if (inOutPuts == null || inOutPuts.length == 0) {
            LOGGER.error("The input/output list should not be empty.");
            return;
        }
        if (Arrays.stream(inOutPuts).anyMatch(iInOutPut -> iInOutPut instanceof IInput) &&
                Arrays.stream(inOutPuts).anyMatch(iInOutPut -> iInOutPut instanceof IOutput)) {
            LOGGER.error("Input and outputs should not be mixed.");
            return;
        }
        if (!inputList.isEmpty()) {
            if (Arrays.stream(inOutPuts).noneMatch(iInOutPut -> iInOutPut instanceof IOutput)) {
                LOGGER.error("Inputs should be link to outputs.");
                return;
            }
            outputList.addAll(Arrays.asList(inOutPuts));
        } else if (!outputList.isEmpty()) {
            if (Arrays.stream(inOutPuts).noneMatch(iInOutPut -> iInOutPut instanceof IInput)) {
                LOGGER.error("Outputs should be link to inputs.");
                return;
            }
            inputList.addAll(Arrays.asList(inOutPuts));
        }
        for (IInOutPut input : inputList) {
            for (IInOutPut output : outputList) {
                inputOutputMap.put(input, output);
            }
        }
    }

    @Override
    public void to(@NotNull String alias) {
        List<IInOutPut> inOutPuts = new ArrayList<>();
        inOutPuts.addAll(inputList);
        inOutPuts.addAll(outputList);
        for (IInOutPut inOutPut : inOutPuts) {
            if (aliases.get(alias) != null) {
                aliases.get(alias).add(inOutPut);
            } else {
                aliases.put(alias, new ArrayList<>(inOutPuts));
            }
        }
        if (!inputList.isEmpty() &&
                inputList.stream().map(IInOutPut::getName).map(Optional::get)
                        .noneMatch(name -> name.equals(alias))) {
            inputs.add(new Input().name(alias));
        }
        if (!outputList.isEmpty() &&
                outputList.stream().map(IInOutPut::getName).map(Optional::get)
                        .noneMatch(name -> name.equals(alias))) {
            outputs.add(new Output().name(alias));
        }
    }

    public Map<String, List<IInOutPut>> getAliases() {
        return aliases;
    }

    public Map<IInOutPut, IInOutPut> getInputOutputMap() {
        return inputOutputMap;
    }

    public LinkedList<IInput> getInputs() {
        return inputs;
    }

    public LinkedList<IOutput> getOutputs() {
        return outputs;
    }
}
