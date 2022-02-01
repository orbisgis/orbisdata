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

import groovy.lang.*;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.commons.annotations.Nullable;
import org.orbisgis.process.api.IProcess;
import org.orbisgis.process.api.IProcessBuilder;
import org.orbisgis.process.api.IProcessFactory;
import org.orbisgis.process.api.IProcessManager;

import java.util.*;

/**
 * Implementation of IProcessManager as a singleton.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC 2019-2020)
 */
public class ProcessManager implements IProcessManager, GroovyObject, GroovyInterceptable {
    
    /**
     * Default factory name
     */
    private static final String DEFAULT_FACTORY_NAME = "Default";
    /**
     * Unique ProcessManager instance.
     */
    private static ProcessManager instance = null;

    /**
     * Map of the process factory and their identifier.
     */
    private final Map<String, IProcessFactory> processFactoryMap;
    /**
     * Default factory
     */
    private final IProcessFactory defaultFactory;
    /**
     * MetaClass use for groovy methods/properties binding
     */
    @NotNull
    protected MetaClass metaClass = InvokerHelper.getMetaClass(ProcessManager.class);

    /**
     * Private constructor in order to make it unique.
     */
    protected ProcessManager() {
        defaultFactory = new ProcessFactory(false, true);
        defaultFactory.setProcessManager(this);
        processFactoryMap = new HashMap<>();
        processFactoryMap.put(DEFAULT_FACTORY_NAME, defaultFactory);
    }

    /**
     * Return the unique instance of the ProcessManager.
     *
     * @return The unique instance of the ProcessManager.
     */
    @NotNull
    public static ProcessManager getProcessManager() {
        if (instance == null) {
            instance = new ProcessManager();
        }
        return instance;
    }

    @Override
    @NotNull
    public IProcessBuilder create() {
        return new ProcessBuilder(defaultFactory, defaultFactory);
    }

    @Override
    @NotNull
    public Optional<IProcess> create(@Nullable @DelegatesTo(IProcessBuilder.class) Closure<?> cl) {
        if(cl == null) {
            return Optional.empty();
        }
        IProcessBuilder builder = new ProcessBuilder(defaultFactory, defaultFactory);
        Closure<?> code = cl.rehydrate(builder, this, this);
        code.setResolveStrategy(Closure.DELEGATE_FIRST);
        code.call();
        Process p = (Process)builder.getProcess();
        return Optional.of(p);
    }

    @Override
    @NotNull
    public List<String> factoryIds() {
        return new ArrayList<>(processFactoryMap.keySet());
    }

    @Override
    @NotNull
    public IProcessFactory factory(@Nullable String identifier) {
        if(identifier == null) {
            return factory();
        }
        if (!processFactoryMap.containsKey(identifier)) {
            IProcessFactory factory = new ProcessFactory();
            factory.setProcessManager(this);
            processFactoryMap.put(identifier, factory);
        }
        return processFactoryMap.get(identifier);
    }

    @NotNull
    public static IProcessFactory createFactory(@Nullable String identifier) {
        return getProcessManager().factory(
                identifier != null && !identifier.isEmpty() ? identifier : "factory_"+UUID.randomUUID().toString());
    }

    @Override
    @NotNull
    public IProcessFactory factory() {
        return defaultFactory;
    }

    @NotNull
    public static IProcessFactory createFactory() {
        return getProcessManager().factory();
    }

    @Override
    @NotNull
    public Optional<IProcess> process(@Nullable String processId) {
        return factory().getProcess(processId);
    }

    @Override
    @NotNull
    public Optional<IProcess> process(@Nullable String processId, @Nullable String factoryId) {
        return factory(factoryId).getProcess(processId);
    }

    @Override
    public boolean registerFactory(@Nullable String id, @Nullable IProcessFactory factory) {
        if(factory == null || id == null || id.isEmpty() || processFactoryMap.containsKey(id)) {
            return false;
        }
        processFactoryMap.put(id, factory);
        factory.setProcessManager(this);
        return true;
    }

    @Override
    public void register(@Nullable Map<String, IProcessFactory> map) {
        if(map != null) {
            map.forEach(this::registerFactory);
        }
    }

    @Nullable
    @Override
    public Object invokeMethod(@Nullable String name, @Nullable Object args) {
        if(name != null) {
            Object obj = getMetaClass().invokeMethod(this, name, args);
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
    @NotNull
    public MetaClass getMetaClass() {
        return metaClass;
    }

    @Override
    public void setMetaClass(@Nullable MetaClass metaClass) {
        this.metaClass = metaClass == null ? InvokerHelper.getMetaClass(this.getClass()) : metaClass;
    }
}
