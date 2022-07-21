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

import groovy.lang.Closure;
import org.orbisgis.commons.utilities.CheckUtils;
import org.orbisgis.process.api.IProcess;
import org.orbisgis.process.api.IProcessBuilder;
import org.orbisgis.process.api.IProcessFactory;

import java.util.LinkedHashMap;

/**
 * Implementation of the interface {@link IProcessBuilder}.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC 2019-2020)
 */
public class ProcessBuilder implements IProcessBuilder {

    /**
     * {@link IProcessFactory} used to register the process.
     */
    private final IProcessFactory factory;
    /**
     * Delegate for the closure
     */
    private final Object delegate;
    /**
     * Identifier of the process.
     */
    private String id;
    /**
     * Title of the process.
     */
    private String title;
    /**
     * Human readable description of the process.
     */
    private String description;
    /**
     * List of simple keyword (one word) of the process.
     */
    private String[] keywords;
    /**
     * @link LinkedHashMap} of inputs with the name as key and the input Object as value.
     */
    private LinkedHashMap<String, Object> inputs;
    /**
     * {@link LinkedHashMap} of outputs with the name as key and the output Object as value.
     */
    private LinkedHashMap<String, Object> outputs;
    /**
     * Process version.
     */
    private String version;
    /**
     * {@link Closure} containing the code to execute on the process execution.
     */
    private Closure<?> closure;

    /**
     * Main constructor.
     *
     * @param factory  {@link IProcessFactory} used to register the process.
     * @param delegate Delegate for the closure.
     */
    public ProcessBuilder(IProcessFactory factory, Object delegate) {
        CheckUtils.checkNotNull(factory, "The ProcessFactory should not be null.");
        CheckUtils.checkNotNull(delegate, "The Closure delegate object should not be null.");
        this.factory = factory;
        this.delegate = delegate;
    }

    @Override
    public IProcessBuilder id(String id) {
        this.id = id;
        return this;
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
    public IProcessBuilder run(Closure<?> closure) {
        this.closure = closure;
        if (closure != null) {
            this.closure.setDelegate(delegate);
            this.closure.setResolveStrategy(Closure.DELEGATE_FIRST);
        }
        return this;
    }

    @Override
    public IProcess getProcess() {
        IProcess process = new Process(id, title, description, keywords, inputs, outputs, version, closure);
        factory.registerProcess(process);
        return process;
    }
}
