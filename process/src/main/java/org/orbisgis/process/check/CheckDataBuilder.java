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
package org.orbisgis.process.check;

import org.orbisgis.process.api.check.ICheckClosureBuilder;
import org.orbisgis.process.api.check.ICheckDataBuilder;
import org.orbisgis.process.api.check.IProcessCheck;
import org.orbisgis.process.api.inoutput.IInOutPut;

import java.util.Arrays;
import java.util.Objects;

/**
 * Implementation of the {@link ICheckDataBuilder} interface.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019-2020)
 */
public class CheckDataBuilder implements ICheckDataBuilder {

    /**
     * {@link IProcessCheck} being built
     */
    private final IProcessCheck processCheck;

    /**
     * Default constructor.
     *
     * @param processCheck {@link IProcessCheck} to build.
     */
    public CheckDataBuilder(IProcessCheck processCheck) {
        this.processCheck = processCheck;
    }

    @Override
    public ICheckClosureBuilder with(IInOutPut... data) {
        if(data != null && (data.length == 0 || Arrays.stream(data).allMatch(Objects::isNull))) {
            processCheck.setInOutPuts((IInOutPut[]) null);
        }
        else {
            processCheck.setInOutPuts(data);
        }
        return new CheckClosureBuilder(processCheck);
    }
}
