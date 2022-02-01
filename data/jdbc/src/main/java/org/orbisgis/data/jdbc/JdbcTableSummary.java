/*
 * Bundle JDBC is part of the OrbisGIS platform
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
 * JDBC is distributed under LGPL 3 license.
 *
 * Copyright (C) 2018 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * JDBC is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * JDBC is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * JDBC. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.data.jdbc;

import org.h2gis.utilities.TableLocation;
import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.commons.annotations.Nullable;
import org.orbisgis.data.api.dataset.IJdbcTable;
import org.orbisgis.data.api.dataset.IJdbcTableSummary;
import org.orbisgis.data.api.dataset.ISummary;

/**
 * Implementation of the interface {@link ISummary} for the JDBC module.
 *
 * @author Sylvain Palominos
 */
public class JdbcTableSummary implements IJdbcTableSummary {

    /**
     * Count of column of the summarized {@link JdbcTable}
     */
    private final int columnCount;
    /**
     * Count of row of the summarized {@link JdbcTable}
     */
    private final int rowCount;
    /**
     * {@link TableLocation} of the summarized {@link JdbcTable}
     */
    private final TableLocation tableLocation;

    public JdbcTableSummary(@Nullable TableLocation tableLocation, int columnCount, int rowCount) {
        this.tableLocation = tableLocation;
        this.columnCount = columnCount;
        this.rowCount = rowCount;
    }

    /**
     * Returns the {@link TableLocation} of the summarized {@link JdbcTable}.
     *
     * @return The {@link TableLocation} of the summarized {@link JdbcTable}.
     */
    public TableLocation getLocation() {
        return tableLocation;
    }

    /**
     * Returns the row count of the summarized {@link JdbcTable}.
     *
     * @return The row count of the summarized {@link JdbcTable}.
     */
    public int getRowCount() {
        return rowCount;
    }

    /**
     * Returns the column count of the summarized {@link JdbcTable}.
     *
     * @return The column count of the summarized {@link JdbcTable}.
     */
    public int getColumnCount() {
        return columnCount;
    }

    @NotNull
    @Override
    public String toString() {
        return (tableLocation == null ? IJdbcTable.QUERY_LOCATION : tableLocation.toString()) + "; row count : " +
                rowCount + "; column count : " + columnCount;
    }

    @Override
    public Object asType(@NotNull Class<?> clazz) {
        return null;
    }
}
