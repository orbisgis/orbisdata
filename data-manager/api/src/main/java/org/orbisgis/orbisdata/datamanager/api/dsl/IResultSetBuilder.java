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
package org.orbisgis.orbisdata.datamanager.api.dsl;

import groovy.lang.GString;
import org.orbisgis.orbisdata.datamanager.api.dataset.ISpatialTable;
import org.orbisgis.orbisdata.datamanager.api.dataset.ITable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Interface defining method use for the configuration and building of a {@link java.sql.ResultSet} which can be used
 * to execute queries or to be converted to a {@link org.orbisgis.orbisdata.datamanager.api.dataset.IJdbcTable}.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC / Chaire GEOTERA 2020)
 */
public interface IResultSetBuilder {

    /**
     * Set the {@link java.sql.ResultSet} type to {@link java.sql.ResultSet#TYPE_FORWARD_ONLY}.
     *
     * @return This builder.
     */
    IResultSetBuilder forwardOnly();

    /**
     * Set the {@link java.sql.ResultSet} type to {@link java.sql.ResultSet#TYPE_SCROLL_INSENSITIVE}.
     *
     * @return This builder.
     */
    IResultSetBuilder scrollInsensitive();

    /**
     * Set the {@link java.sql.ResultSet} type to {@link java.sql.ResultSet#TYPE_SCROLL_SENSITIVE}.
     *
     * @return This builder.
     */
    IResultSetBuilder scrollSensitive();

    /**
     * Set the {@link java.sql.ResultSet} concurrency to {@link java.sql.ResultSet#CONCUR_READ_ONLY}.
     *
     * @return This builder.
     */
    IResultSetBuilder readOnly();

    /**
     * Set the {@link java.sql.ResultSet} concurrency to {@link java.sql.ResultSet#CONCUR_UPDATABLE}.
     *
     * @return This builder.
     */
    IResultSetBuilder updatable();

    /**
     * Set the {@link java.sql.ResultSet} holdability to {@link java.sql.ResultSet#HOLD_CURSORS_OVER_COMMIT}.
     *
     * @return This builder.
     */
    IResultSetBuilder holdCursorOverCommit();

    /**
     * Set the {@link java.sql.ResultSet} holdability to {@link java.sql.ResultSet#CLOSE_CURSORS_AT_COMMIT}.
     *
     * @return This builder.
     */
    IResultSetBuilder closeCursorAtCommit();

    /**
     * Set the {@link java.sql.ResultSet} fetch direction to {@link java.sql.ResultSet#FETCH_FORWARD}.
     *
     * @return This builder.
     */
    IResultSetBuilder fetchForward();

    /**
     * Set the {@link java.sql.ResultSet} fetch direction to {@link java.sql.ResultSet#FETCH_REVERSE}.
     *
     * @return This builder.
     */
    IResultSetBuilder fetchReverse();

    /**
     * Set the {@link java.sql.ResultSet} fetch direction to {@link java.sql.ResultSet#FETCH_UNKNOWN}.
     *
     * @return This builder.
     */
    IResultSetBuilder fetchUnknown();

    /**
     * Set the {@link java.sql.ResultSet} fetch size.
     *
     * @param size Fetch size.
     * @return This builder.
     */
    IResultSetBuilder fetchSize(int size);

    /**
     * Set the {@link java.sql.ResultSet} timeout.
     *
     * @param time Timeout.
     * @return This builder.
     */
    IResultSetBuilder timeout(int time);

    /**
     * Set the {@link java.sql.ResultSet} max row count.
     *
     * @param maxRow Max row count.
     * @return This builder.
     */
    IResultSetBuilder maxRow(int maxRow);

    /**
     * Set the name of the cursor used for update and delete.
     *
     * @param name Name of the cursor.
     * @return This builder.
     */
    IResultSetBuilder cursorName(String name);

    /**
     * Request the {@link java.sql.Statement} to be poolable like {@link java.sql.PreparedStatement}.
     *
     * @return This builder.
     */
    IResultSetBuilder poolable();

    /**
     * Set the maximum size of a field.
     *
     * @param size Maximum size of a field.
     * @return This builder.
     */
    IResultSetBuilder maxFieldSize(int size);


    /**
     * Enables auto-commit mode, which means that each statement is once again
     * committed automatically when it is completed.
     * @param autoCommit false to disable auto-commit mode
     * @return  This builder.
     */
    IResultSetBuilder autoCommit(boolean autoCommit);

    /**
     * See {@link java.sql.Statement#execute(String)}
     */
    boolean execute(String sql) throws SQLException;

    /**
     * See {@link java.sql.Statement#execute(String)}
     */
    boolean execute(GString sql) throws SQLException;

    /**
     * See {@link Statement#executeBatch()}
     */
    int[] executeBatch(String[] queries) throws SQLException;

    /**
     * See {@link Statement#executeBatch()}
     */
    int[] executeBatch(GString[] queries) throws SQLException;

    /**
     * See {@link Statement#executeLargeBatch()}
     */
    long[] executeLargeBatch(String[] queries) throws SQLException;

    /**
     * See {@link Statement#executeLargeBatch()}
     */
    long[] executeLargeBatch(GString[] queries) throws SQLException;

    /**
     * See {@link Statement#executeUpdate(String)}
     */
    int executeUpdate(String sql) throws SQLException;

    /**
     * See {@link Statement#executeUpdate(String)}
     */
    int executeUpdate(GString sql) throws SQLException;

    /**
     * See {@link Statement#executeLargeUpdate(String)}
     */
    long executeLargeUpdate(String sql) throws SQLException;

    /**
     * See {@link Statement#executeLargeUpdate(String)}
     */
    long executeLargeUpdate(GString sql) throws SQLException;

    /**
     * Returns a {@link ITable} built from the {@link ResultSet}.
     *
     * @param nameOrQuery Name or query of the table to get.
     * @return A {@link ITable} built from the {@link ResultSet}.
     */
    ITable<?, ?> getTable(String nameOrQuery);

    /**
     * Returns a {@link ISpatialTable} built from the {@link ResultSet}.
     *
     * @param nameOrQuery Name or query of the table to get.
     * @return A {@link ISpatialTable} built from the {@link ResultSet}.
     */
    ISpatialTable<?, ?> getSpatialTable(String nameOrQuery);

    /**
     * Returns a {@link ITable} built from the {@link ResultSet}.
     *
     * @param nameOrQuery Name or query of the table to get.
     * @return A {@link ITable} built from the {@link ResultSet}.
     */
    ITable<?, ?> getTable(GString nameOrQuery);

    /**
     * Returns a {@link ISpatialTable} built from the {@link ResultSet}.
     *
     * @param nameOrQuery Name or query of the table to get.
     * @return A {@link ISpatialTable} built from the {@link ResultSet}.
     */
    ISpatialTable<?, ?> getSpatialTable(GString nameOrQuery);

    /**
     * Returns a {@link ITable} built from the {@link ResultSet}.
     *
     * @param query Query of the table to get.
     * @param params Parameters of the query.
     * @return A {@link ITable} built from the {@link ResultSet}.
     */
    ITable<?, ?> getTable(String query, List<Object> params);

    /**
     * Returns a {@link ISpatialTable} built from the {@link ResultSet}.
     *
     * @param query Query of the table to get.
     * @param params Parameters of the query.
     * @return A {@link ISpatialTable} built from the {@link ResultSet}.
     */
    ISpatialTable<?, ?> getSpatialTable(String query, List<Object> params);
}
