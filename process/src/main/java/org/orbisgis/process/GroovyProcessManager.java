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
import org.orbisgis.process.api.IProcess;
import org.orbisgis.process.api.IProcessBuilder;
import org.orbisgis.process.api.IProcessFactory;
import org.orbisgis.process.api.IProcessManager;

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
    private final IProcessManager pm = new ProcessManager();
    /**
     * MetaClass use for groovy methods/properties binding
     */
    protected MetaClass metaClass = InvokerHelper.getMetaClass(GroovyProcessManager.class);

    @Override
    public IProcessBuilder create() {
        return pm.create();
    }

    @Override
    public Optional<IProcess> create(Closure<?> cl) {
        return pm.create(cl);
    }

    @Override
    public List<String> factoryIds() {
        return pm.factoryIds();
    }

    @Override
    public IProcessFactory factory(String identifier) {
        return pm.factory(identifier);
    }

    @Override
    public IProcessFactory factory() {
        return pm.factory();
    }

    @Override
    public Optional<IProcess> process(String processId) {
        return pm.process(processId);
    }

    @Override
    public Optional<IProcess> process(String processId, String factoryId) {
        return pm.process(processId, factoryId);
    }

    @Override
    public boolean registerFactory(String id, IProcessFactory factory) {
        boolean ret = pm.registerFactory(id, factory);
        factory.setProcessManager(this);
        return ret;
    }

    @Override
    public void register(Map<String, IProcessFactory> map) {
        pm.register(map);
        map.values().forEach(factory -> factory.setProcessManager(this));
    }

    public void register(List<Class<? extends GroovyProcessFactory>> list) throws IllegalAccessException, InstantiationException {
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

    @Override
    public Object invokeMethod(String name, Object args) {
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
    public Object getProperty(String name) {
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
    public MetaClass getMetaClass() {
        return metaClass;
    }

    @Override
    public void setMetaClass(MetaClass metaClass) {
        this.metaClass = metaClass == null ? InvokerHelper.getMetaClass(this.getClass()) : metaClass;
    }
}
