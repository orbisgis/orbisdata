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

import groovy.lang.Closure;
import org.orbisgis.process.api.IProcess;
import org.orbisgis.process.api.check.IProcessCheck;
import org.orbisgis.process.api.inoutput.IInOutPut;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Optional;

/**
 * {@link IProcessCheck} implementation for test purpose.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2020)
 */
public class DummyProcessCheck implements IProcessCheck {

    public LinkedHashMap<String, Object> processInData;
    public Action failAction;
    public String failMessage;
    public Action successAction;
    public String successMessage;
    public IInOutPut[] inputOrOutput;
    public Closure<?> closure;
    public IProcess process;

    public DummyProcessCheck(IProcess process) {
        this.process = process;
    }

    @Override
    public boolean run(LinkedHashMap<String, Object> processInData) {
        this.processInData = processInData;
        return false;
    }

    @Override
    public void onFail(Action action, String message) {
        this.failAction = action;
        this.failMessage = message;
    }

    @Override
    public void onFail(String message) {
        this.failMessage = message;
    }

    @Override
    public void onSuccess(Action action, String message) {
        this.successAction = action;
        this.successMessage = message;
    }

    @Override
    public void onSuccess(String message) {
        this.successMessage = message;
    }

    @Override
    public void setInOutPuts(IInOutPut... inputOrOutput) {
        this.inputOrOutput = inputOrOutput;
    }

    @Override
    public LinkedList<IInOutPut> getInOutPuts() {
        return new LinkedList<>();
    }

    @Override
    public void setClosure(Closure<?> cl) {
        this.closure = cl;
    }

    @Override
    public Optional<Closure<?>> getClosure() {
        return Optional.ofNullable(closure);
    }

    @Override
    public boolean fail() {
        return false;
    }

    @Override
    public boolean success() {
        return false;
    }

    @Override
    public Optional<IProcess> getProcess() {
        return Optional.ofNullable(process);
    }
}
