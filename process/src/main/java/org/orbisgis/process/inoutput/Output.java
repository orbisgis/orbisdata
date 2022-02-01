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
package org.orbisgis.process.inoutput;

import groovy.lang.MetaClass;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.commons.annotations.Nullable;
import org.orbisgis.process.api.IProcess;
import org.orbisgis.process.api.inoutput.IOutput;

import java.util.Objects;

/**
 * Implementation of the {@link IOutput} interface.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC 2019-2020)
 */
public class Output extends InOutPut implements IOutput {

    /**
     * Default constructor.
     */
    public Output() {
        this.metaClass = InvokerHelper.getMetaClass(Output.class);
    }

    @Override
    @NotNull
    public Output title(String title) {
        super.setTitle(title);
        return this;
    }

    @Override
    @NotNull
    public Output description(String description) {
        super.setDescription(description);
        return this;
    }

    @Override
    @NotNull
    public Output keywords(String[] keywords) {
        super.setKeywords(keywords);
        return this;
    }

    @Override
    @NotNull
    public Output type(@Nullable Class<?> type) {
        super.setType(type);
        return this;
    }

    @Override
    @NotNull
    public Output name(@Nullable String name) {
        super.setName(name);
        return this;
    }

    @Override
    @NotNull
    public Output process(@Nullable IProcess process) {
        super.setProcess(process);
        return this;
    }

    @Override
    public void setMetaClass(@Nullable MetaClass metaClass) {
        this.metaClass = metaClass == null ? InvokerHelper.getMetaClass(this.getClass()) : metaClass;
    }

    @Override
    public boolean equals(Object obj){
        if(! (obj instanceof Output)){
            return false;
        }
        Output out = (Output)obj;
        return Objects.equals(this.getProcess(), out.getProcess()) &&
                Objects.equals(this.getName(), out.getName());
    }

    @Nullable
    @Override
    public Output copy() {
        Output copy = new Output();
        copy.setProcess(this.getProcess().orElse(null));
        copy.setName(this.getName().orElse(null));
        copy.setType(this.getType().orElse(null));
        copy.setTitle(this.getTitle().orElse(null));
        copy.setDescription(this.getDescription().orElse(null));
        copy.setKeywords(this.getKeywords().orElse(null));
        return copy;
    }
}
