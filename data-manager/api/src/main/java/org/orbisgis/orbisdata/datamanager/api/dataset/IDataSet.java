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
package org.orbisgis.orbisdata.datamanager.api.dataset;

import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.commons.annotations.Nullable;

/**
 * Raw collection of data, no matter its structure.
 *
 * @param <T> The type of elements returned by the iterator.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2018-2019)
 */
public interface IDataSet<T> extends Iterable<T> {

    /**
     * Get the location of {@link IDataSet}.
     * The returned {@link String} can be anything to locate the data (URI, URL, file path ...)
     *
     * @return The location of the data.
     */
    String getLocation();

    /**
     * Get the human readable name of the {@link IDataSet}.
     *
     * @return The name of the {@link IDataSet}.
     */
    String getName();

    /**
     * Get the metadata object of the {@link IDataSet}.
     *
     * @return The metadata object.
     */
    Object getMetaData();

    /**
     * Convert the current object into another with the given class.
     *
     * @param clazz New class of the result.
     * @return The current object into an other class.
     */
    Object asType(Class<?> clazz);

    /**
     * Return true if the {@link IDataSet} is empty, false otherwise.
     *
     * @return True if the {@link IDataSet} is empty, false otherwise.
     */
    boolean isEmpty();

    /**
     * Return the {@link ISummary} of the {@link IDataSet}.
     *
     * @return The {@link ISummary} of the {@link IDataSet}.
     */
    ISummary getSummary();

    /**
     * Reload the source of the {@link IDataSet}.
     *
     * @return true if the reload has been done successfully, false otherwise.
     */
    boolean reload();

    @Override
    String toString();
}
