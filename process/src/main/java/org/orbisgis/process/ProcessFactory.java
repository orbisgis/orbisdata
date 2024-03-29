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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the {@link IProcessFactory}.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC 2019-2020)
 */
public class ProcessFactory implements IProcessFactory, GroovyObject, GroovyInterceptable {

    /**
     * Indicated if the process creation is allowed.
     */
    protected boolean isLock;
    /**
     * Indicates if the factory should be used as the default one.
     */
    protected boolean isDefault;
    /**
     * List of the processes created with this factory.
     */
    private final List<IProcess> processList;
    private IProcessManager processManager;
    /**
     * {@link MetaClass}
     */
    private MetaClass metaClass = InvokerHelper.getMetaClass(ProcessFactory.class);

    /**
     * Default empty constructor.
     */
    ProcessFactory() {
        this(false, false);
    }

    /**
     * Constructor allowing to configure the factory.
     *
     * @param lock If true, not process creation allowed.
     * @param dflt If true, it will be selected as the default factory.
     */
    ProcessFactory(boolean lock, boolean dflt) {
        isLock = lock;
        isDefault = dflt;
        processList = new ArrayList<>();
    }

    @Override
    public void registerProcess(IProcess process) {
        if (!isLock && process != null) {
            List<IProcess> list = new ArrayList<>();
            for (IProcess p : processList) {
                if (p.getIdentifier().equals(process.getIdentifier())) {
                    list.add(p);
                }
            }
            processList.removeAll(list);
            processList.add(process);
        }
    }

    @Override
    public boolean isLocked() {
        return isLock;
    }

    @Override
    public boolean isDefault() {
        return isDefault;
    }

    @Override
    public Optional<IProcess> getProcess(String processId) {
        return processList
                .stream()
                .filter(iProcess -> iProcess.getIdentifier().equals(processId))
                .findFirst()
                .map(IProcess::newInstance);
    }

    @Override
    public IProcessBuilder create() {
        return new ProcessBuilder(this, this);
    }

    @Override
    public Optional<IProcess> create(@DelegatesTo(IProcessBuilder.class) Closure<?> cl) {
        if(cl == null) {
            return Optional.empty();
        }
        else {
            IProcessBuilder builder = new ProcessBuilder(this, this);
            Closure<?> code = cl.rehydrate(builder, this, this);
            code.setResolveStrategy(Closure.DELEGATE_FIRST);
            return Optional.of(((IProcessBuilder) code.call()).getProcess());
        }
    }

    @Override
    public Optional<IProcessManager> getProcessManager() {
        return Optional.ofNullable(processManager);
    }

    @Override
    public void setProcessManager(IProcessManager processManager) {
        this.processManager = processManager;
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
