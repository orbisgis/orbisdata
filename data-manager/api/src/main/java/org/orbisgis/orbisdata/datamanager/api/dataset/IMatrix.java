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
import org.orbisgis.orbisdata.datamanager.api.metadata.IMatrixMetaData;

/**
 * Multi-dimensional structured data.
 *
 * @param <T> The type of elements returned by the iterator.
 * @param <U> The type of elements streamed.
 *
 * @author Ewan Bocher (CNRS)
 * @author Sylvain PALOMINOS (Lab-STICC UBS 2019)
 */
public interface IMatrix<T, U> extends IDataSet<T, U> {

    /**
     * Return the number of dimensions of the {@link IMatrix}.
     *
     * @return The number of dimensions of the {@link IMatrix}.
     */
    default int getNDim() {
        return getMetaData().getNDim();
    }

    /**
     * Returns the size of the {@link IMatrix}. The returned array contains the size of each dimensions.
     *
     * @return The size of the {@link IMatrix} as an int array.
     */
    @NotNull
    default int[] getSize() {
        return getMetaData().getSize();
    }

    @Override
    @NotNull
    IMatrixMetaData getMetaData();
}
