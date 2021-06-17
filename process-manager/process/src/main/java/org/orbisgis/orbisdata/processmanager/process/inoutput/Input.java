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

import groovy.lang.MetaClass;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.commons.annotations.Nullable;
import org.orbisgis.orbisdata.processmanager.api.IProcess;
import org.orbisgis.orbisdata.processmanager.api.inoutput.IInput;

import java.util.Objects;
import java.util.Optional;

/**
 * Implementation of the {@link IInput} interface.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC 2019-2020)
 */
public class Input extends InOutPut implements IInput {
    private Object dfltValue;

    /**
     * Default constructor.
     */
    public Input() {
        this.metaClass = InvokerHelper.getMetaClass(Input.class);
    }

    @Override
    public Input optional(Object dfltValue) {
        this.dfltValue = dfltValue;
        return this;
    }

    @Override
    public boolean isOptional() {
        return dfltValue != null;
    }

    @Override
    public Optional<Object> getDefaultValue() {
        return Optional.ofNullable(dfltValue);
    }

    @Override
    public Input mandatory() {
        dfltValue = null;
        return this;
    }

    @Override
    public boolean isMandatory() {
        return dfltValue == null;
    }

    @Override
    public Input title(String title) {
        super.setTitle(title);
        return this;
    }

    @Override
    public Input description(String description) {
        super.setDescription(description);
        return this;
    }

    @Override
    public Input keywords(String[] keywords) {
        super.setKeywords(keywords);
        return this;
    }

    @Override
    public Input type(Class<?> type) {
        super.setType(type);
        return this;
    }

    @Override
    public Input name(String name) {
        super.setName(name);
        return this;
    }

    @Override
    public Input process(IProcess process) {
        super.setProcess(process);
        return this;
    }

    @Override
    public void setMetaClass(MetaClass metaClass) {
        this.metaClass = metaClass == null ? InvokerHelper.getMetaClass(this.getClass()) : metaClass;
    }

    @Override
    public boolean equals(Object obj){
        if(! (obj instanceof Input)){
            return false;
        }
        Input in = (Input)obj;
        return Objects.equals(this.getProcess(), in.getProcess()) &&
                Objects.equals(this.getName(), in.getName());
    }
    @Override
    public Input copy() {
        Input copy = new Input();
        copy.dfltValue = this.dfltValue;
        copy.setProcess(this.getProcess().orElse(null));
        copy.setName(this.getName().orElse(null));
        copy.setType(this.getType().orElse(null));
        copy.setTitle(this.getTitle().orElse(null));
        copy.setDescription(this.getDescription().orElse(null));
        copy.setKeywords(this.getKeywords().orElse(null));
        return copy;
    }
}
