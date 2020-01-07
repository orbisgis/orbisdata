/*
 * Bundle ProcessManager API is part of the OrbisGIS platform
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
 * ProcessManager API is distributed under LGPL 3 license.
 *
 * Copyright (C) 2018 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * ProcessManager API is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * ProcessManager API is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * ProcessManager API. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.orbisdata.processmanager.api;

import org.orbisgis.orbisdata.processmanager.api.check.ICheckDataBuilder;
import org.orbisgis.orbisdata.processmanager.api.check.IProcessCheck;
import org.orbisgis.orbisdata.processmanager.api.inoutput.IInOutPut;

import java.util.UUID;

/**
 * This class should not be used will using a IProcess executing and mapping processes is easier.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public interface IProcessMapper extends IProcess {

    /**
     * Return a new instance of the process.
     *
     * @return A process new instance.
     */
    IProcessMapper newInstance();

    /**
     * Start to link the given inputs/outputs.
     *
     * @param inOutPuts Inputs or outputs to link.
     * @return A {@link ILinker} object which will do the link.
     */
    ILinker link(IInOutPut... inOutPuts);

    /**
     * Add a {@link IProcessCheck} before the execution of the given {@link IProcess}.
     *
     * @param process {@link IProcess} before which the check should be done.
     * @return A {@link ICheckDataBuilder} to continue the {@link IProcessCheck} build.
     */
    ICheckDataBuilder before(IProcess process);


    /**
     * Add a {@link IProcessCheck} after the execution of the given {@link IProcess}.
     *
     * @param process {@link IProcess} after which the check should be done.
     * @return A {@link ICheckDataBuilder} to continue the {@link IProcessCheck} build.
     */
    ICheckDataBuilder after(IProcess process);

    @Override
    default String getVersion() {
        return null;
    }

    @Override
    default String getDescription() {
        return null;
    }

    @Override
    default String[] getKeywords() {
        return null;
    }

    @Override
    default String getIdentifier() {
        return UUID.randomUUID().toString();
    }
}
