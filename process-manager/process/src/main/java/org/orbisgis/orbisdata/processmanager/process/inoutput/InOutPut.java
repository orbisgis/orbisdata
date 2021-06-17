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
package org.orbisgis.orbisdata.processmanager.process.inoutput;

import groovy.lang.GroovyInterceptable;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.commons.annotations.Nullable;
import org.orbisgis.orbisdata.processmanager.api.IProcess;
import org.orbisgis.orbisdata.processmanager.api.inoutput.IInOutPut;

import java.util.Optional;

/**
 * Implementation of the {@link IInOutPut} interface.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC 2019-2020)
 */
public abstract class InOutPut implements IInOutPut, GroovyObject, GroovyInterceptable {

    /**
     * Groovy {@link MetaClass}.
     */
    protected MetaClass metaClass = InvokerHelper.getMetaClass(InOutPut.class);
    /**
     * {@link IProcess} of the input/output.
     */
    private IProcess process;
    /**
     * Name of the input/output.
     */
    private String name;
    /**
     * Type of the input/output.
     */
    private Class<?> type;
    /**
     * Title of the input/output.
     */
    private String title;
    /**
     * Description of the input/output.
     */
    private String description;
    /**
     * Keywords of the input/output.
     */
    private String[] keywords;
    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public IInOutPut name(String name) {
        this.name = name;
        return this;
    }
    public Optional<IProcess> getProcess() {
        return Optional.ofNullable(process);
    }

    @Override
    public void setProcess(IProcess process) {
        this.process = process;
    }

    @Override
    public IInOutPut process(IProcess process) {
        this.process = process;
        return this;
    }

    @Override
    public Optional<String> getTitle() {
        return Optional.ofNullable(title);
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public IInOutPut title(String title) {
        this.title = title;
        return this;
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public IInOutPut description(String description) {
        this.description = description;
        return this;
    }

    @Override
    public Optional<String[]> getKeywords() {
        return Optional.ofNullable(keywords);
    }

    @Override
    public void setKeywords(String[] keywords) {
        this.keywords = keywords;
    }
    @Override
    public IInOutPut keywords(String[] keywords) {
        this.keywords = keywords;
        return this;
    }

    @Override
    public Optional<Class<?>> getType() {
        return Optional.ofNullable(type);
    }

    @Override
    public void setType(Class<?> type) {
        this.type = type;
    }

    @Override
    public IInOutPut type(Class<?> type) {
        this.type = type;
        return this;
    }

    @Override
    public String toString() {
        String pId = process != null ? ":" + process.getIdentifier() : "";
        String str = name != null ? name : "";
        return str + pId;
    }

    @Override
    public void setProperty(String propertyName, Object newValue) {
        if(propertyName != null) {
            this.metaClass.setProperty(this, propertyName, newValue);
        }
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
