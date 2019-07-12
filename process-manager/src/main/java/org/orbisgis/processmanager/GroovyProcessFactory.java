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
 * ProcessManager is distributed under GPL 3 license.
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
package org.orbisgis.processmanager;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.lang.Script;
import org.orbisgis.processmanagerapi.IProcess;
import org.orbisgis.processmanagerapi.IProcessBuilder;
import org.orbisgis.processmanagerapi.IProcessFactory;

import java.util.LinkedHashMap;

/**
 * Implementation of the {@link IProcessFactory} class dedicated to Groovy.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public abstract class GroovyProcessFactory extends Script implements IProcessFactory {
    private ProcessFactory factory = new ProcessFactory();

    @Override
    public void registerProcess(IProcess process) {
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
    public IProcess getProcess(String processId) {
        return factory.getProcess(processId);
    }

    @Override
    public IProcessBuilder create() {
        return new ProcessBuilder(factory, this);
    }

    @Override
    public IProcess create(@DelegatesTo(IProcessBuilder.class) Closure cl) {
        IProcessBuilder builder = new ProcessBuilder(factory, this);
        Closure code = cl.rehydrate(builder, this, this);
        code.setResolveStrategy(Closure.DELEGATE_FIRST);
        return ((IProcessBuilder)code.call()).getProcess();
    }

    @Deprecated
    @Override
    public IProcess create(String title, String description, String[] keywords, LinkedHashMap<String, Object> inputs, LinkedHashMap<String, Object> outputs, String version, Closure closure) {
        return factory.create(title, description, keywords, inputs, outputs, version, closure);
    }

    @Deprecated
    @Override
    public IProcess create(String title, String description, String[] keywords, LinkedHashMap<String, Object> inputs, LinkedHashMap<String, Object> outputs, Closure closure) {
        return factory.create(title, description, keywords, inputs, outputs, closure);
    }

    @Deprecated
    @Override
    public IProcess create(String title, String description, String[] keywords, LinkedHashMap<String, Object> outputs, String version, Closure closure) {
        return factory.create(title, description, keywords, outputs, version, closure);
    }

    @Deprecated
    @Override
    public IProcess create(String title, String description, String[] keywords, LinkedHashMap<String, Object> outputs, Closure closure) {
        return factory.create(title, description, keywords, outputs, closure);
    }

    @Deprecated
    @Override
    public IProcess create(String title, String description, LinkedHashMap<String, Object> inputs, LinkedHashMap<String, Object> outputs, String version, Closure closure) {
        return factory.create(title, description, inputs, outputs, version, closure);
    }

    @Deprecated
    @Override
    public IProcess create(String title, String description, LinkedHashMap<String, Object> inputs, LinkedHashMap<String, Object> outputs, Closure closure) {
        return factory.create(title, description, inputs, outputs, closure);
    }

    @Deprecated
    @Override
    public IProcess create(String title, String description, LinkedHashMap<String, Object> outputs, String version, Closure closure) {
        return factory.create(title, description, outputs, version, closure);
    }

    @Deprecated
    @Override
    public IProcess create(String title, String description, LinkedHashMap<String, Object> outputs, Closure closure) {
        return factory.create(title, description, outputs, closure);
    }

    @Deprecated
    @Override
    public IProcess create(String title, String[] keywords, LinkedHashMap<String, Object> inputs, LinkedHashMap<String, Object> outputs, String version, Closure closure) {
        return factory.create(title, keywords, inputs, outputs, version, closure);
    }

    @Deprecated
    @Override
    public IProcess create(String title, String[] keywords, LinkedHashMap<String, Object> inputs, LinkedHashMap<String, Object> outputs, Closure closure) {
        return factory.create(title, keywords, inputs, outputs, closure);
    }

    @Deprecated
    @Override
    public IProcess create(String title, String[] keywords, LinkedHashMap<String, Object> outputs, String version, Closure closure) {
        return factory.create(title, keywords, outputs, version, closure);
    }

    @Deprecated
    @Override
    public IProcess create(String title, String[] keywords, LinkedHashMap<String, Object> outputs, Closure closure) {
        return factory.create(title, keywords, outputs, closure);
    }

    @Deprecated
    @Override
    public IProcess create(String title, LinkedHashMap<String, Object> inputs, LinkedHashMap<String, Object> outputs, String version, Closure closure) {
        return factory.create(title, inputs, outputs, version, closure);
    }

    @Deprecated
    @Override
    public IProcess create(String title, LinkedHashMap<String, Object> inputs, LinkedHashMap<String, Object> outputs, Closure closure) {
        return factory.create(title, inputs, outputs, closure);
    }

    @Deprecated
    @Override
    public IProcess create(String title, LinkedHashMap<String, Object> outputs, String version, Closure closure) {
        return factory.create(title, outputs, version, closure);
    }

    @Deprecated
    @Override
    public IProcess create(String title, LinkedHashMap<String, Object> outputs, Closure closure) {
        return factory.create(title, outputs, closure);
    }
}
