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
package org.orbisgis.processmanager.inoutput;

import org.orbisgis.processmanagerapi.IProcess;
import org.orbisgis.processmanagerapi.inoutput.IInOutPut;

/**
 * Implementation of the {@link IInOutPut} interface.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public class InOutPut implements IInOutPut {
    /** {@link IProcess} of the input/output. */
    private IProcess process;
    /** Name of the input/output. */
    private String name;
    private Class type;
    private String title;
    private String description;
    private String[] keywords;
    private String version;

    /**
     * Main constructor.
     *
     * @param process {@link IProcess} of the input/output.
     * @param name Name of the input/output.
     */
    public InOutPut(IProcess process, String name){
        this.process = process;
        this.name = name;
    }

    public String getName(){
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public IProcess getProcess() {
        return process;
    }

    @Override
    public void setProcess(IProcess process) {
        this.process = process;
    }

    @Override
    public IInOutPut setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public IInOutPut setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public IInOutPut setKeywords(String[] keywords) {
        this.keywords = keywords;
        return this;
    }

    @Override
    public IInOutPut setType(Class type) {
        this.type = type;
        return this;
    }

    @Override
    public Class getType() {
        return type;
    }

    @Override public String toString(){return name+":"+process.getIdentifier();}
}
