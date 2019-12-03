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
package org.orbisgis.orbisdata.processmanager.process;

import groovy.lang.Closure;
import org.orbisgis.orbisdata.processmanager.api.IProcess;
import org.orbisgis.orbisdata.processmanager.api.IProcessBuilder;
import org.orbisgis.orbisdata.processmanager.api.IProcessFactory;

import java.util.LinkedHashMap;

/**
 * Implementation of the interface {@link IProcessBuilder}.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public class ProcessBuilder implements IProcessBuilder {

    /** {@link IProcessFactory} used to register the process.*/
    private IProcessFactory factory;
    /** Delegate for the closure */
    private Object delegate;
    /** Title of the process.*/
    private String title;
    /** Human readable description of the process.*/
    private String description;
    /** List of simple keyword (one word) of the process.*/
    private String[] keywords;
    /** @link LinkedHashMap} of inputs with the name as key and the input Object as value.*/
    private LinkedHashMap<String, Object> inputs;
    /** {@link LinkedHashMap} of outputs with the name as key and the output Object as value.*/
    private LinkedHashMap<String, Object> outputs;
    /** Process version.*/
    private String version;
    /** {@link Closure} containing the code to execute on the process execution.*/
    private Closure closure;

    /**
     * Main constructor.
     *
     * @param factory {@link IProcessFactory} used to register the process.
     * @param delegate Delegate for the closure.
     */
    public ProcessBuilder(IProcessFactory factory, Object delegate){
        this.factory = factory;
        this.delegate = delegate;
    }

    @Override
    public IProcessBuilder title(String title) {
        this.title = title;
        return this;
    }

    @Override
    public IProcessBuilder description(String description) {
        this.description = description;
        return this;
    }

    @Override
    public IProcessBuilder keywords(String[] keywords) {
        this.keywords = keywords;
        return this;
    }

    @Override
    public IProcessBuilder inputs(LinkedHashMap<String, Object> inputs) {
        this.inputs = inputs;
        return this;
    }

    @Override
    public IProcessBuilder outputs(LinkedHashMap<String, Object> outputs) {
        this.outputs = outputs;
        return this;
    }

    @Override
    public IProcessBuilder version(String version) {
        this.version = version;
        return this;
    }

    @Override
    public IProcessBuilder run(Closure closure) {
        this.closure = closure;
        if(closure != null) {
            this.closure.setDelegate(delegate);
            this.closure.setResolveStrategy(Closure.DELEGATE_FIRST);
        }
        return this;
    }

    @Override
    public IProcess getProcess() {
        IProcess process = new Process(title, description, keywords, inputs, outputs, version, closure);
        factory.registerProcess(process);
        return process;
    }
}
