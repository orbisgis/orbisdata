/*
 * Bundle DataManager is part of the OrbisGIS platform
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
 * DataManager is distributed under GPL 3 license.
 *
 * Copyright (C) 2018 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * DataManager is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * DataManager is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * DataManager. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.datamanager;

import org.orbisgis.datamanagerapi.datasource.IDataSourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;

/**
 * Implementation of {@link IDataSourceLocation}
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2018-2019)
 */
public class DataSourceLocation implements IDataSourceLocation {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceLocation.class);

    private String location;

    /**
     * Main constructor.
     *
     * @param location Path of the {@link org.orbisgis.datamanagerapi.datasource.IDataSource}.
     */
    public DataSourceLocation(String location){
        this.location = location;
    }

    @Override
    public String toString(){
        return asType(String.class);
    }

    @Override
    public <T> T asType(Class<T> type) {

        switch(type.getCanonicalName()){
            case "java.io.File":
                return (T) new File(location);
            case "java.net.URL":
                try {
                    return (T) new File(location).toURI().toURL();
                } catch (MalformedURLException ignored) {}
                return null;
            case "java.lang.String":
                return (T) location;
            case "java.net.URI":
                return (T) new File(location).toURI();
            default:
                return null;
        }
    }
}
