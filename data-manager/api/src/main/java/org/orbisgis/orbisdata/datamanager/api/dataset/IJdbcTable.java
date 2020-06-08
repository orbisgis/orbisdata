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

import groovy.lang.Closure;
import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.commons.annotations.Nullable;
import org.orbisgis.orbisdata.datamanager.api.dsl.IWhereBuilderOrOptionBuilder;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

/**
 * Extension of the {@link ITable} specially dedicated to the JDBC databases thanks to the extension of the
 * {@link ResultSet} interface. It also extends the {@link IWhereBuilderOrOptionBuilder} for the SQL requesting.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain Palominos (Lab-STICC UBS 2019)
 */
public interface IJdbcTable<T> extends ITable<ResultSet, T>, ResultSet, IWhereBuilderOrOptionBuilder {

    /**
     * Enumeration of the resultSet types.
     */
    enum RSType {TYPE_FORWARD_ONLY, TYPE_SCROLL_INSENSITIVE, TYPE_SCROLL_SENSITIVE}
    /**
     * Enumeration of the resultSet consurency.
     */
    enum RSConcurrency {CONCUR_READ_ONLY, CONCUR_UPDATABLE}
    /**
     * Enumeration of the resultSet holdability.
     */
    enum RSHoldability {HOLD_CURSORS_OVER_COMMIT, CLOSE_CURSORS_AT_COMMIT}

    /**
     * {@link String} location/name of the query built table
     */
    String QUERY_LOCATION = "query";

    /**
     * {@link String} name of the metadata property
     */
    String META_PROPERTY = "meta";

    /**
     * Return the {@link ITableLocation} of the {@link IJdbcTable}.
     *
     * @return The {@link ITableLocation}.
     */
    @Nullable
    ITableLocation getTableLocation();

    /**
     * Return the {@link DataBaseType} type of the {@link IJdbcTable}.
     *
     * @return The {@link DataBaseType} type
     */
    @NotNull
    DataBaseType getDbType();

    @Override
    @Nullable
    ResultSetMetaData getMetaData();

    /**
     * Return true if the {@link ITable} is a linked one.
     *
     * @return True if the {@link ITable} is a linked one.
     */
    boolean isLinked();

    /**
     * Return true if the {@link ITable} is a temporary one.
     *
     * @return True if the {@link ITable} is a temporary one.
     */
    boolean isTemporary();

    @Override
    default String getLocation() {
        ITableLocation location = getTableLocation();
        if (location == null || location.toString(getDbType()).isEmpty()) {
            return QUERY_LOCATION;
        } else {
            return location.toString(getDbType());
        }
    }

    @Override
    @NotNull
    default String getName() {
        ITableLocation location = getTableLocation();
        if (location == null || location.getTable().isEmpty()) {
            return QUERY_LOCATION;
        } else {
            return location.getTable();
        }
    }

    @Override
    @NotNull
    default ResultSetIterator iterator() {
        try {
            return new ResultSetIterator(this);
        } catch (SQLException e) {
            return new ResultSetIterator();
        }
    }

    @Override
    default void eachRow(@NotNull Closure<Object> closure) {
        this.forEach(closure::call);
    }

    @Override
    @NotNull
    IJdbcTableSummary getSummary();

    @Override
    @NotNull
    IJdbcTable columns(@NotNull String... columns);

    @Override
    @NotNull
    IJdbcTable columns(@NotNull List<String> columns);

    @Override
    IJdbcTable filter(String filter);
}
