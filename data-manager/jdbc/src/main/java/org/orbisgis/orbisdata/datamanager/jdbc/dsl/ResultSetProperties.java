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
 * DataManager is distributed under LGPL 3 license.
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
package org.orbisgis.orbisdata.datamanager.jdbc.dsl;

import org.orbisgis.orbisdata.datamanager.api.dsl.IResultSetBuilder;
import org.orbisgis.orbisdata.datamanager.api.dsl.IResultSetProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;

/**
 * Implementation of the {@link IResultSetBuilder} interface.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC / Chaire GEOTERA 2020)
 */
public class ResultSetProperties implements IResultSetProperties {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ResultSetProperties.class);

    /** Type of the {@link java.sql.ResultSet}. */
    private int type = -1;
    /** {@link ResultSet} concurrency. */
    private int concurrency = -1;
    /** {@link ResultSet} holdability. */
    private int holdability = -1;
    /** {@link ResultSet} fetch direction. */
    private int fetchDirection = -1;
    /** {@link ResultSet} fetch size. */
    private int fetchSize = 100;
    /** {@link java.sql.Statement} query timeout. */
    private int timeout = -1;
    /** {@link java.sql.Statement} max row count. */
    private int maxRows = -1;
    /** {@link java.sql.Statement} cursor name. */
    private String cursorName = null;
    /** {@link java.sql.Statement} poolable status. */
    private boolean poolable = false;
    /** {@link java.sql.Statement} maximum field size. */
    private int maxFieldSize = -1;

    @Override
    public void setType(int type) {
        if(type != ResultSet.TYPE_FORWARD_ONLY &&
                type != ResultSet.TYPE_SCROLL_INSENSITIVE &&
                type != ResultSet.TYPE_SCROLL_SENSITIVE &&
                type != -1) {
            LOGGER.warn("ResultSet type should be 'TYPE_FORWARD_ONLY' or 'TYPE_SCROLL_INSENSITIVE' or 'TYPE_SCROLL_SENSITIVE'.");
            this.type = -1;
        }
        else {
            this.type = type;
        }
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public void setConcurrency(int concurrency) {
        if(concurrency != ResultSet.CONCUR_READ_ONLY &&
                concurrency != ResultSet.CONCUR_UPDATABLE &&
                concurrency != -1) {
                LOGGER.warn("ResultSet concurrency should be 'CONCUR_READ_ONLY' or 'CONCUR_UPDATABLE'.");
            this.concurrency = -1;
        }
        else {
            this.concurrency = concurrency;
        }
    }

    @Override
    public int getConcurrency() {
        return concurrency;
    }

    @Override
    public void setHoldability(int holdability) {
        if(holdability != ResultSet.HOLD_CURSORS_OVER_COMMIT &&
                holdability != ResultSet.CLOSE_CURSORS_AT_COMMIT &&
                holdability != -1) {
            LOGGER.warn("ResultSet holdability should be 'HOLD_CURSORS_OVER_COMMIT' or 'CLOSE_CURSORS_AT_COMMIT'.");
            this.holdability = -1;
        }
        else {
            this.holdability = holdability;
        }
    }

    @Override
    public int getHoldability() {
        return holdability;
    }

    @Override
    public void setFetchDirection(int fetchDirection) {
        if(fetchDirection != ResultSet.FETCH_FORWARD &&
                fetchDirection != ResultSet.FETCH_REVERSE &&
                fetchDirection != ResultSet.FETCH_UNKNOWN &&
                fetchDirection != -1) {
            LOGGER.warn("ResultSet holdability should be 'FETCH_FORWARD' or 'FETCH_REVERSE' or 'FETCH_UNKNOWN'.");
            this.fetchDirection = -1;
        }
        else {
            this.fetchDirection = fetchDirection;
        }
    }

    @Override
    public int getFetchDirection() {
        return fetchDirection;
    }

    @Override
    public void setFetchSize(int fetchSize) {
        if(fetchSize < 0) {
            LOGGER.warn("The fetch size cannot be under 0 so it will be disabled.");
            this.fetchSize = 100;
        }
        else {
            this.fetchSize = fetchSize;
        }
    }

    @Override
    public int getFetchSize() {
        return fetchSize;
    }

    @Override
    public void setTimeout(int timeout) {
        if(timeout < -1) {
            LOGGER.warn("The timeout cannot be under 0 so it will be disabled.");
            this.timeout = -1;
        }
        else {
            this.timeout = timeout;
        }
    }

    @Override
    public int getTimeout() {
        return timeout;
    }

    @Override
    public void setMaxRows(int maxRows) {
        if(maxRows < -1) {
            LOGGER.warn("The max rows count cannot be under 0 so it will be disabled.");
            this.maxRows = -1;
        }
        else {
            this.maxRows = maxRows;
        }
    }

    @Override
    public int getMaxRows() {
        return maxRows;
    }

    @Override
    public void setCursorName(String cursorName) {
        this.cursorName = cursorName;
    }

    @Override
    public String getCursorName() {
        return cursorName;
    }

    @Override
    public void setPoolable(boolean poolable) {
        this.poolable = poolable;
    }

    @Override
    public boolean isPoolable() {
        return poolable;
    }

    @Override
    public void setMaxFieldSize(int maxFieldSize) {
        if(maxFieldSize < -1) {
            LOGGER.warn("The max field size cannot be under 0 so it will be disabled.");
            this.maxFieldSize = -1;
        }
        else {
            this.maxFieldSize = maxFieldSize;
        }
    }

    @Override
    public int getMaxFieldSize() {
        return maxFieldSize;
    }

    @Override
    public IResultSetProperties copy() {
        ResultSetProperties rsp = new ResultSetProperties();
        rsp.type = this.type;
        rsp.concurrency = this.concurrency;
        rsp.holdability = this.holdability;
        rsp.fetchDirection = this.fetchDirection;
        rsp.fetchSize = this.fetchSize;
        rsp.timeout = this.timeout;
        rsp.maxRows = this.maxRows;
        rsp.cursorName = this.cursorName;
        rsp.poolable = this.poolable;
        rsp.maxFieldSize = this.maxFieldSize;
        return rsp;
    }
}
