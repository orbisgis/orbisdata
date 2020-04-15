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

import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.commons.annotations.Nullable;
import org.orbisgis.orbisdata.processmanager.api.IProcess;
import org.orbisgis.orbisdata.processmanager.api.inoutput.IOutput;

import java.util.UUID;

/**
 * Implementation of the {@link IOutput} interface.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019-2020)
 */
public class Output extends InOutPut implements IOutput {
    /**
     * Main constructor.
     *
     * @param process {@link IProcess} of the input/output.
     * @param name    Name of the input/output.
     */
    public Output(@Nullable IProcess process, @NotNull String name) {
        super(process, name);
    }

    /**
     * Empty constructor.
     */
    public Output() {
        super(null, "output_" + UUID.randomUUID().toString());
    }

    @NotNull
    public static Output call() {
        return new Output();
    }

    @Override
    @NotNull
    public Output setTitle(String title) {
        super.setTitle(title);
        return this;
    }

    @Override
    @NotNull
    public Output setDescription(String description) {
        super.setDescription(description);
        return this;
    }

    @Override
    @NotNull
    public Output setKeywords(String[] keywords) {
        super.setKeywords(keywords);
        return this;
    }

    @Override
    @NotNull
    public Output setType(@Nullable Class<?> type) {
        super.setType(type);
        return this;
    }
}
