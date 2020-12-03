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

import groovy.lang.*;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.commons.annotations.Nullable;
import org.orbisgis.orbisdata.processmanager.api.IProcess;
import org.orbisgis.orbisdata.processmanager.api.IProcessBuilder;
import org.orbisgis.orbisdata.processmanager.api.IProcessFactory;
import org.orbisgis.orbisdata.processmanager.api.IProcessManager;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of the {@link IProcessManager} class dedicated to Groovy.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC 2020)
 */
public abstract class GroovyProcessManager extends Script implements IProcessManager, GroovyObject, GroovyInterceptable {

    /**
     * Internal ProcessManager.
     */
    private IProcessManager pm = new ProcessManager();
    /**
     * MetaClass use for groovy methods/properties binding
     */
    @NotNull
    protected MetaClass metaClass = InvokerHelper.getMetaClass(GroovyProcessManager.class);

    @NotNull
    @Override
    public IProcessBuilder create() {
        return pm.create();
    }

    @NotNull
    @Override
    public Optional<IProcess> create(@Nullable Closure<?> cl) {
        return pm.create(cl);
    }

    @NotNull
    @Override
    public List<String> factoryIds() {
        return pm.factoryIds();
    }

    @NotNull
    @Override
    public IProcessFactory factory(@Nullable String identifier) {
        return pm.factory(identifier);
    }

    @NotNull
    @Override
    public IProcessFactory factory() {
        return pm.factory();
    }

    @NotNull
    @Override
    public Optional<IProcess> process(@Nullable String processId) {
        return pm.process(processId);
    }

    @NotNull
    @Override
    public Optional<IProcess> process(@Nullable String processId, @Nullable String factoryId) {
        return pm.process(processId, factoryId);
    }

    @Override
    public boolean registerFactory(@Nullable String id, @Nullable IProcessFactory factory) {
        boolean ret = pm.registerFactory(id, factory);
        factory.setProcessManager(this);
        return ret;
    }

    @Override
    public void register(@Nullable Map<String, IProcessFactory> map) {
        pm.register(map);
        map.values().forEach(factory -> factory.setProcessManager(this));
    }

    public void register(@Nullable List<Class<? extends GroovyProcessFactory>> list) throws IllegalAccessException, InstantiationException {
        if(list != null) {
            for (Class<? extends GroovyProcessFactory> clazz : list) {
                GroovyProcessFactory gpf = clazz.newInstance();
                gpf.run();
                registerFactory(clazz.getSimpleName(), gpf);
                gpf.setProcessManager(this);
            }
        }
    }

    public static GroovyProcessManager load(Class<? extends GroovyProcessManager> clazz) throws IllegalAccessException, InstantiationException {
        GroovyProcessManager gpm = clazz.newInstance();
        gpm.run();
        return gpm;
    }

    public void setLogger(GroovyObject logger) {
        factoryIds().stream()
                .map(this::factory)
                .filter(pf -> pf instanceof GroovyProcessFactory)
                .map(pf -> (GroovyProcessFactory)pf);
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

    @Nullable
    @Override
    public Object getProperty(@Nullable String name) {
        if(name == null) {
            return null;
        }
        if(factoryIds().contains(name)){
            return factory(name);
        }
        else{
            return getMetaClass().getProperty(this, name);
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
