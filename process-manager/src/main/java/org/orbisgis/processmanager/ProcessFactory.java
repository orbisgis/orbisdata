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
import org.orbisgis.processmanagerapi.ICaster;
import org.orbisgis.processmanagerapi.IProcess;
import org.orbisgis.processmanagerapi.IProcessFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the IProcessManager.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public class ProcessFactory implements IProcessFactory {

    /** Caster use to cast the inputs. */
    private ICaster caster;
    /** Indicated if the process creation is allowed. */
    private boolean isLock;
    /** Indicates if the factory should be used as the default one. */
    private boolean isDefault;
    /** List of the processes created with this factory. */
    private List<IProcess> processList;

    /**
     * Default empty constructor.
     */
    ProcessFactory(){
        this(false, false);
    }

    /**
     * Constructor allowing to configure the factory.
     *
     * @param lock If true, not process creation allowed.
     * @param dflt If true, it will be selected as the default factory.
     */
    ProcessFactory(boolean lock, boolean dflt){
        isLock = lock;
        isDefault = dflt;
        caster = new Caster();
        caster.addCast(String.class, Object::toString);
        processList = new ArrayList<>();
    }

    @Override
    public boolean isLocked() {
        return isLock;
    }

    @Override
    public boolean isDefault() {
        return isDefault;
    }

    @Override
    public IProcess process(String processId) {
        return processList
                .stream()
                .filter(iProcess -> iProcess.getIdentifier().equals(processId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public IProcess create(String title, String description, String[] keywords, Map<String, Class> inputs,
                                  Map<String, Class> outputs, String version, Closure closure) {
        if(!isLocked()) {
            //TODO implements a isSame methods in IProcess
            IProcess process = null;
            /*IProcess process = processList.stream()
                    .filter(iProcess -> iProcess.getIdentifier().equals(title))
                    .findAny()
                    .orElse(null);*/
            if(process == null) {
                process = new Process(title, description, keywords, inputs, outputs, version, closure, caster);
                processList.add(process);
            }
            return process.newInstance();
        }
        else{
            return null;
        }
    }

    @Override
    public IProcess create(String title, String description, String[] keywords, Map<String, Class> inputs,
                                  Map<String, Class> outputs, Closure closure) {
        return create(title, description, keywords, inputs, outputs, null, closure);
    }

    @Override
    public IProcess create(String title, String description, String[] keywords, Map<String, Class> outputs,
                                  String version, Closure closure) {
        return create(title, description, keywords, null, outputs, version, closure);
    }

    @Override
    public IProcess create(String title, String description, String[] keywords, Map<String, Class> outputs,
                                  Closure closure) {
        return create(title, description, keywords, null, outputs, null, closure);
    }

    @Override
    public IProcess create(String title, String description, Map<String, Class> inputs,
                                  Map<String, Class> outputs, String version, Closure closure) {
        return create(title, description, null, inputs, outputs, version, closure);
    }

    @Override
    public IProcess create(String title, String description, Map<String, Class> inputs,
                                  Map<String, Class> outputs, Closure closure) {
        return create(title, description, null, inputs, outputs, null, closure);
    }

    @Override
    public IProcess create(String title, String description, Map<String, Class> outputs, String version,
                                  Closure closure) {
        return create(title, description, null, null, outputs, version, closure);
    }

    @Override
    public IProcess create(String title, String description, Map<String, Class> outputs, Closure closure) {
        return create(title, description, null, null, outputs, null, closure);
    }

    @Override
    public IProcess create(String title, String[] keywords, Map<String, Class> inputs,
                                  Map<String, Class> outputs, String version, Closure closure) {
        return create(title, null, keywords, inputs, outputs, version, closure);
    }

    @Override
    public IProcess create(String title, String[] keywords, Map<String, Class> inputs,
                                  Map<String, Class> outputs, Closure closure) {
        return create(title, null, keywords, inputs, outputs, null, closure);
    }

    @Override
    public IProcess create(String title, String[] keywords, Map<String, Class> outputs, String version,
                                  Closure closure) {
        return create(title, null, keywords, null, outputs, version, closure);
    }

    @Override
    public IProcess create(String title, String[] keywords, Map<String, Class> outputs, Closure closure) {
        return create(title, null, keywords, null, outputs, null, closure);
    }

    @Override
    public IProcess create(String title, Map<String, Class> inputs, Map<String, Class> outputs,
                                  String version, Closure closure) {
        return create(title, null, null, inputs, outputs, version, closure);
    }

    @Override
    public IProcess create(String title, Map<String, Class> inputs, Map<String, Class> outputs,
                                  Closure closure) {
        return create(title, null, null, inputs, outputs, null, closure);
    }

    @Override
    public IProcess create(String title, Map<String, Class> outputs, String version, Closure closure) {
        return create(title, null, null, null, outputs, version, closure);
    }

    @Override
    public IProcess create(String title, Map<String, Class> outputs, Closure closure) {
        return create(title, null, null, null, outputs, null, closure);
    }
}
