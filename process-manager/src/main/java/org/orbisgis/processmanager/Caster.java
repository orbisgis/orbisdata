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

import org.orbisgis.processmanagerapi.ICastElement;
import org.orbisgis.processmanagerapi.ICaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Implementation of the interface ICaster.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public class Caster implements ICaster {

    private static final Logger LOGGER = LoggerFactory.getLogger(Caster.class);

    /** List of ICastElement available */
    private Map<Class, ICastElement> castElementMap;

    public Caster(){
        castElementMap = new HashMap<>();
    }

    @Override
    public void addCast(Class clazz, ICastElement castElement) {
        if(castElementMap.containsKey(clazz)){
            castElementMap.put(clazz, castElement);
        }
        else{
            LOGGER.error("There is already a cast element with the class name '"+clazz+"'.");
        }
    }

    @Override
    public Object cast(Object o, Class clazz) {
        return castElementMap.entrySet()
                .stream()
                .filter(element -> element.getKey().equals(clazz))
                .map(element -> element.getValue().cast(o))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(o);
    }
}
