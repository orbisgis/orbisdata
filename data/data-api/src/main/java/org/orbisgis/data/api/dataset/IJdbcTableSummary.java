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
package org.orbisgis.data.api.dataset;

import org.h2gis.utilities.TableLocation;

/**
 * Extension of the {@link ISummary} interface dedicated to the {@link IJdbcTable}.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain Palominos (Lab-STICC UBS 2019)
 */
public interface IJdbcTableSummary extends ISummary {
    /**
     * Returns the {@link TableLocation} of the summarized {@link IJdbcTable}.
     *
     * @return The {@link TableLocation} of the summarized {@link IJdbcTable}.
     */
    TableLocation getLocation();

    /**
     * Returns the row count of the summarized {@link IJdbcTable}.
     *
     * @return The row count of the summarized {@link IJdbcTable}.
     */
    int getRowCount();

    /**
     * Returns the column count of the summarized {@link IJdbcTable}.
     *
     * @return The column count of the summarized {@link IJdbcTable}.
     */
    int getColumnCount();
}