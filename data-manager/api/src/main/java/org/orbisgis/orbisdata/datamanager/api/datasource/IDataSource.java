/*
 * Bundle DataManager API is part of the OrbisGIS platform
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
 * DataManager API is distributed under LGPL 3 license.
 *
 * Copyright (C) 2019-2020 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * DataManager API is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * DataManager API is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * DataManager API. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.orbisdata.datamanager.api.datasource;

import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.commons.annotations.Nullable;
import org.orbisgis.orbisdata.datamanager.api.dataset.IDataSet;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.Map;

/**
 * Raw source of data.
 *
 * @author Erwan Bocher (CNRS, 2020-2021)
 * @author Sylvain PALOMINOS (Lab-STICC UBS 2018-2019)
 */
public interface IDataSource<T> {

    /**
     * Return the {@link IDataSet} implementation corresponding to the given name or null if not {@link IDataSet found}.
     *
     * @param name Name of the {@link IDataSet}.
     * @return The implementation of {@link IDataSet} corresponding to the given name or null.
     */
    @Nullable
    IDataSet<T, T> getDataSet(@NotNull String name);

    /**
     * Return the location of the {@link IDataSourceLocation}
     *
     * @return The location of the {@link IDataSourceLocation}
     */
    @Nullable
    IDataSourceLocation getLocation();

    /**
     * Convert the current object into another with the given class.
     *
     * @param clazz New class of the result.
     * @return The current object into an other class.
     */
    @Nullable
    Object asType(@NotNull Class<?> clazz);

    @Override
    @NotNull
    String toString();

}
