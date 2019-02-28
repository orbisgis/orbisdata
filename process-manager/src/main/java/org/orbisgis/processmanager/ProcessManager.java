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

import org.orbisgis.processmanagerapi.IProcess;
import org.orbisgis.processmanagerapi.IProcessFactory;
import org.orbisgis.processmanagerapi.IProcessManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * Implementation of IProcessManager as a singleton.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public class ProcessManager implements IProcessManager {

    /** Map of the process factory and their identifier. */
    private Map<String, IProcessFactory> processFactoryMap;
    /** Unique ProcessManager instance. */
    private static ProcessManager instance = null;

    /**
     * Private constructor in order to make it unique.
     */
    private ProcessManager(){
        processFactoryMap = new HashMap<>();
        processFactoryMap.put("orbisgis", new ProcessFactory(true, true));
    }

    /**
     * Return the unique instance of the ProcessManager.
     *
     * @return The unique instance of the ProcessManager.
     */
    public static ProcessManager getProcessManager(){
        if(instance == null){
            instance = new ProcessManager();
        }
        return instance;
    }

    @Override
    public List<String> factoryIds() {
        return processFactoryMap.entrySet().stream().map(Map.Entry::getKey).collect(toList());
    }

    @Override
    public IProcessFactory factory(String identifier){
        if(!processFactoryMap.containsKey(identifier)){
            processFactoryMap.put(identifier, new ProcessFactory());
        }
        return processFactoryMap.get(identifier);
    }

    @Override
    public IProcessFactory factory(){
        return processFactoryMap
                .entrySet()
                .stream()
                .map(Map.Entry::getValue)
                .filter(IProcessFactory::isDefault)
                .findFirst()
                .orElse(null);
    }

    @Override
    public IProcess process(String processId){
        IProcessFactory processFactory = factory();
        return processFactory == null ? null : processFactory.process(processId);
    }

    @Override
    public IProcess process(String processId, String factoryId){
        IProcessFactory processFactory = factory(factoryId);
        return processFactory == null ? null : processFactory.process(processId);
    }
}
