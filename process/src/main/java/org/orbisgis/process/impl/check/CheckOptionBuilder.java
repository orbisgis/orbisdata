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
package org.orbisgis.process.impl.check;

import org.orbisgis.process.api.check.ICheckOptionBuilder;
import org.orbisgis.process.api.check.IProcessCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the {@link ICheckOptionBuilder} interface.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019-2020)
 */
public class CheckOptionBuilder implements ICheckOptionBuilder {

    /**
     * Class logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CheckOptionBuilder.class);

    /**
     * {@link IProcessCheck} being built
     */
    private final IProcessCheck processCheck;

    /**
     * Default constructor.
     *
     * @param processCheck {@link IProcessCheck} to build.
     */
    public CheckOptionBuilder(IProcessCheck processCheck) {
        this.processCheck = processCheck;
    }

    @Override
    public ICheckOptionBuilder stopOnFail(String message) {
        if(message == null) {
            LOGGER.warn("No message provided for the ProcessCheck stopOnFail.");
        }
        processCheck.onFail(IProcessCheck.Action.STOP, message);
        return this;
    }

    @Override
    public ICheckOptionBuilder stopOnFail() {
        processCheck.onFail(IProcessCheck.Action.STOP, null);
        return this;
    }

    @Override
    public ICheckOptionBuilder stopOnSuccess(String message) {
        if(message == null) {
            LOGGER.warn("No message provided for the ProcessCheck stopOnSuccess.");
        }
        processCheck.onSuccess(IProcessCheck.Action.STOP, message);
        return this;
    }

    @Override
    public ICheckOptionBuilder stopOnSuccess() {
        processCheck.onSuccess(IProcessCheck.Action.STOP, null);
        return this;
    }

    @Override
    public ICheckOptionBuilder continueOnFail(String message) {
        if(message == null) {
            LOGGER.warn("No message provided for the ProcessCheck continueOnFail.");
        }
        processCheck.onFail(IProcessCheck.Action.CONTINUE, message);
        return this;
    }

    @Override
    public ICheckOptionBuilder continueOnFail() {
        processCheck.onFail(IProcessCheck.Action.CONTINUE, null);
        return this;
    }

    @Override
    public ICheckOptionBuilder continueOnSuccess(String message) {
        if(message == null) {
            LOGGER.warn("No message provided for the ProcessCheck continueOnSuccess.");
        }
        processCheck.onSuccess(IProcessCheck.Action.CONTINUE, message);
        return this;
    }

    @Override
    public ICheckOptionBuilder continueOnSuccess() {
        processCheck.onSuccess(IProcessCheck.Action.CONTINUE, null);
        return this;
    }
}
