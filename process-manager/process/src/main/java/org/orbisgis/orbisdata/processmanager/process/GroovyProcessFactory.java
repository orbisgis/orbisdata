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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the {@link IProcessFactory} class dedicated to Groovy.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC 2019-2020)
 */
public abstract class GroovyProcessFactory extends Script implements IProcessFactory, GroovyObject, GroovyInterceptable {
    private final ProcessFactory factory = new ProcessFactory();
    /**
     * MetaClass use for groovy methods/properties binding
     */
    @NotNull
    protected MetaClass metaClass = InvokerHelper.getMetaClass(ProcessManager.class);
    @Nullable
    private IProcessManager processManager;

    @Override
    public void registerProcess(@Nullable IProcess process) {
        factory.registerProcess(process);
    }

    @Override
    public boolean isLocked() {
        return factory.isLocked();
    }

    @Override
    public boolean isDefault() {
        return factory.isDefault();
    }

    @Override
    @NotNull
    public Optional<IProcess> getProcess(@Nullable String processId) {
        return factory.getProcess(processId);
    }

    @Override
    @NotNull
    public IProcessBuilder create() {
        return factory.create();
    }

    @Override
    @NotNull
    public Optional<IProcess> create(@Nullable @DelegatesTo(IProcessBuilder.class) Closure<?> cl) {
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
        Optional<IProcess> p = getProcess(name);
        if(p.isPresent()){
            return p.get().newInstance();
        }
        else{
            Object o = getMetaClass().getProperty(this, name);
            if(o instanceof Optional){
                return ((Optional) o).orElse(null);
            }
            return o;
        }
    }

    @NotNull
    @Override
    public Optional<IProcessManager> getProcessManager() {
        return Optional.ofNullable(processManager);
    }

    @Override
    public void setProcessManager(@Nullable IProcessManager processManager) {
        this.processManager = processManager;
    }

    /**
     * Prefix the given String with '_' and an UUID.
     *
     * @param name String to prefix
     * @return The prefixed String
     */
    public static String prefix(String name) {
        return UUID.randomUUID().toString().replaceAll("-", "_") + "_" + name;
    }

    /**
     * Prefix the given String with the given prefix.
     *
     * @param prefix Prefix
     * @param name String to prefix
     * @return The prefixed String
     */
    public static String prefix(String prefix, String name) {
        return prefix == null || prefix.isEmpty() ? name : prefix + "_" + name;
    }

    /**
     * Postfix the given String with '_' and an UUID..
     *
     * @param name String to postfix
     * @return The postfix String
     */
    public static String postfix(String name) {
        return name + "_" + UUID.randomUUID().toString().replaceAll("-", "_");
    }

    /**
     * Postfix the given String with the given postfix.
     *
     * @param postfix Postfix
     * @param name String to postfix
     * @return The postfix String
     */
    public static String postfix(String name, String postfix) {
        return postfix == null || postfix.isEmpty() ? name : name + "_" + postfix;
    }
}
