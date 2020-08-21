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

import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Interface defining the properties used for the configuration of a {@link ResultSet}/{@link Statement} of a
 * {@link org.orbisgis.orbisdata.datamanager.api.dataset.IJdbcTable}
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC / Chaire GEOTERA 2020)
 */
public interface IResultSetProperties {

    /**
     * Sets the {@link ResultSet} type.
     *
     * @param type Type of the {@link ResultSet}, should be one of : {@link ResultSet#TYPE_FORWARD_ONLY},
     * {@link ResultSet#TYPE_SCROLL_SENSITIVE}, {@link ResultSet#TYPE_SCROLL_INSENSITIVE}.
     * @see Statement#getResultSetType()
     */
    void setType(int type);

    /**
     * Returns the {@link ResultSet} type.
     *
     * @return The type of the {@link ResultSet}, one of : {@link ResultSet#TYPE_FORWARD_ONLY},
     * {@link ResultSet#TYPE_SCROLL_SENSITIVE}, {@link ResultSet#TYPE_SCROLL_INSENSITIVE}.
     * @see Statement#getResultSetType()
     */
    int getType();

    /**
     * Sets the {@link ResultSet} concurrency.
     *
     * @param concurrency Concurrency of the {@link ResultSet}, should be one of : {@link ResultSet#CONCUR_UPDATABLE},
     * {@link ResultSet#CONCUR_READ_ONLY}.
     * @see Statement#getResultSetConcurrency()
     */
    void setConcurrency(int concurrency);

    /**
     * Returns the {@link ResultSet} concurrency.
     *
     * @return The concurrency of the {@link ResultSet}, one of : {@link ResultSet#CONCUR_UPDATABLE},
     * {@link ResultSet#CONCUR_READ_ONLY}.
     * @see Statement#getResultSetConcurrency()
     */
    int getConcurrency();

    /**
     * Sets the {@link ResultSet} holdability.
     *
     * @param holdability Holdability of the {@link ResultSet}, should be one of : {@link ResultSet#HOLD_CURSORS_OVER_COMMIT},
     * {@link ResultSet#CLOSE_CURSORS_AT_COMMIT}.
     * @see Statement#getResultSetHoldability()
     */
    void setHoldability(int holdability);

    /**
     * Returns the {@link ResultSet} holdability.
     *
     * @return The holdability of the {@link ResultSet}, one of : {@link ResultSet#HOLD_CURSORS_OVER_COMMIT},
     * {@link ResultSet#CLOSE_CURSORS_AT_COMMIT}.
     * @see Statement#getResultSetHoldability()
     */
    int getHoldability();

    /**
     * Sets the {@link ResultSet} fetch direction.
     *
     * @param fetchDirection Fetch direction of the {@link ResultSet}, should be one of :
     * {@link ResultSet#FETCH_UNKNOWN}, {@link ResultSet#FETCH_REVERSE}, {@link ResultSet#FETCH_FORWARD}.
     * @see Statement#setFetchDirection(int)
     */
    void setFetchDirection(int fetchDirection);

    /**
     * Returns the {@link ResultSet} fetch direction.
     *
     * @return The fetch direction of the {@link ResultSet}, one of :
     * {@link ResultSet#FETCH_UNKNOWN}, {@link ResultSet#FETCH_REVERSE}, {@link ResultSet#FETCH_FORWARD}.
     * @see Statement#getFetchDirection()
     */
    int getFetchDirection();

    /**
     * Sets the {@link ResultSet} fetch size.
     *
     * @param fetchSize The fetch size.
     * @see ResultSet#setFetchSize(int)
     */
    void setFetchSize(int fetchSize);

    /**
     * Returns the {@link ResultSet} fetch size.
     *
     * @return The fetch size.
     * @see ResultSet#getFetchSize()
     */
    int getFetchSize();

    /**
     * Sets the {@link Statement} query timeout.
     *
     * @param timeout The query timeout.
     * @see Statement#setQueryTimeout(int)
     */
    void setTimeout(int timeout);

    /**
     * Returns the {@link Statement} query timeout.
     *
     * @return The query timeout.
     * @see Statement#getQueryTimeout()
     */
    int getTimeout();

    /**
     * Sets the {@link Statement} max row count.
     *
     * @param maxRows Max row count.
     * @see Statement#setMaxRows(int)
     */
    void setMaxRows(int maxRows);

    /**
     * Returns the {@link Statement} max row count.
     *
     * @return The max row count.
     * @see Statement#getMaxRows()
     */
    int getMaxRows();

    /**
     * Sets the {@link Statement} cursor name.
     *
     * @param cursorName Name of the statement cursor.
     * @see Statement#setCursorName(String)
     */
    void setCursorName(String cursorName);

    /**
     * Returns the {@link Statement} cursor name.
     *
     * @return The cursor name.
     * @see Statement#setCursorName(String)
     */
    String getCursorName();

    /**
     * Sets the {@link Statement} poolable status.
     *
     * @param poolable True if the {@link Statement} is poolable, false otherwise.
     * @see Statement#setPoolable(boolean)
     */
    void setPoolable(boolean poolable);

    /**
     * Returns the {@link Statement} poolable status.
     *
     * @return True if the {@link Statement} is poolable, false otherwise.
     * @see Statement#isPoolable()
     */
    boolean isPoolable();

    /**
     * Sets the {@link Statement} maximum field size.
     *
     * @param maxFieldSize Maximum field size.
     * @see Statement#setMaxFieldSize(int)
     */
    void setMaxFieldSize(int maxFieldSize);

    /**
     * Return the maximum field size.
     *
     * @return The maximum field size.
     * @see Statement#getMaxFieldSize()
     */
    int getMaxFieldSize();

    /**
     * Return a deep copy of this object.
     *
     * @return A deep copy of this object.
     */
    IResultSetProperties copy();
}
