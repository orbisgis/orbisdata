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

import java.util.Map;

/**
 * Cached metadata of a {@link ITable}.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Chaire GEOTERA 2020)
 */
public interface ITableMetaData extends IMatrixMetaData {

    @Override
    default int getNDim() {
        return 2;
    }

    @Override
    @NotNull
    default int[] getSize(){
        return new int[] {getColumnCount(), getRowCount()};
    }

    /**
     * Get all column information from the underlying table.
     *
     * @return A {@link Map} containing the information of the column.
     */
    @NotNull
    Map<String, String> getColumnsTypes();

    /**
     * Return the count of columns.
     *
     * @return The count of columns.
     */
    int getColumnCount();

    /**
     * Return the count of lines or -1 if not able to find the {@link ITable}.
     *
     * @return The count of lines or -1 if not able to find the {@link ITable}.
     */
    int getRowCount();

    /**
     * Return true if the {@link ITable} is spatial.
     *
     * @return True if the {@link ITable} is spatial.
     */
    boolean isSpatial();
}
