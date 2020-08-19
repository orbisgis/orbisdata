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

import groovy.lang.GString;
import org.orbisgis.orbisdata.datamanager.api.dataset.IJdbcSpatialTable;
import org.orbisgis.orbisdata.datamanager.api.dataset.IJdbcTable;
import org.orbisgis.orbisdata.datamanager.api.dataset.ISpatialTable;
import org.orbisgis.orbisdata.datamanager.api.dataset.ITable;
import org.orbisgis.orbisdata.datamanager.api.datasource.IJdbcDataSource;
import org.orbisgis.orbisdata.datamanager.api.dsl.IResultSetBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Implementation of the {@link IResultSetBuilder} interface.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC / Chaire GEOTERA 2020)
 */
public class ResultSetBuilder implements IResultSetBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResultSetBuilder.class);

    /**
     * {@link IJdbcDataSource} to use to get the {@link ResultSet}.
     */
    private IJdbcDataSource dataSource;
    /**
     * {@link ResultSet} properties.
     */
    private ResultSetProperties rsp = new ResultSetProperties();

    /**
     * Main constructor.
     *
     * @param dataSource {@link IJdbcDataSource} used to get the {@link ResultSet}.
     */
    public ResultSetBuilder(IJdbcDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public IResultSetBuilder forwardOnly() {
        rsp.setType(ResultSet.TYPE_FORWARD_ONLY);
        return this;
    }

    @Override
    public IResultSetBuilder scrollInsensitive() {
        rsp.setType(ResultSet.TYPE_SCROLL_INSENSITIVE);
        return this;
    }

    @Override
    public IResultSetBuilder scrollSensitive() {
        rsp.setType(ResultSet.TYPE_SCROLL_SENSITIVE);
        return this;
    }

    @Override
    public IResultSetBuilder readOnly() {
        rsp.setConcurrency(ResultSet.CONCUR_READ_ONLY);
        return this;
    }

    @Override
    public IResultSetBuilder updatable() {
        rsp.setConcurrency(ResultSet.CONCUR_READ_ONLY);
        return this;
    }

    @Override
    public IResultSetBuilder holdCursorOverCommit() {
        rsp.setHoldability(ResultSet.HOLD_CURSORS_OVER_COMMIT);
        return this;
    }

    @Override
    public IResultSetBuilder closeCursorAtCommit() {
        rsp.setHoldability(ResultSet.CLOSE_CURSORS_AT_COMMIT);
        return this;
    }

    @Override
    public IResultSetBuilder fetchForward() {
        rsp.setFetchDirection(ResultSet.FETCH_FORWARD);
        return this;
    }

    @Override
    public IResultSetBuilder fetchReverse() {
        rsp.setFetchDirection(ResultSet.FETCH_REVERSE);
        return this;
    }

    @Override
    public IResultSetBuilder fetchUnknown() {
        rsp.setFetchDirection(ResultSet.FETCH_UNKNOWN);
        return this;
    }

    @Override
    public IResultSetBuilder fetchSize(int size) {
        rsp.setFetchSize(size);
        return this;
    }

    @Override
    public IResultSetBuilder timeout(int timeout) {
        rsp.setTimeout(timeout);
        return this;
    }

    @Override
    public IResultSetBuilder maxRow(int maxRow) {
        rsp.setMaxRows(maxRow);
        return this;
    }

    @Override
    public IResultSetBuilder cursorName(String name) {
        rsp.setCursorName(name);
        return this;
    }

    @Override
    public IResultSetBuilder poolable() {
        rsp.setPoolable(true);
        return this;
    }

    @Override
    public IResultSetBuilder maxFieldSize(int size) {
        rsp.setMaxFieldSize(size);
        return this;
    }

    private Statement getStatement() throws SQLException {
        Statement st;
        if(rsp.getType() != -1 && rsp.getConcurrency() != -1 && rsp.getHoldability() != -1) {
            st = dataSource.getConnection().createStatement(rsp.getType(), rsp.getConcurrency(), rsp.getHoldability());
        }
        else if(rsp.getType() != -1 && rsp.getConcurrency() != -1) {
            st = dataSource.getConnection().createStatement(rsp.getType(), rsp.getConcurrency());
        }
        else if(rsp.getType() != -1) {
            st = dataSource.getConnection().createStatement(rsp.getType(), ResultSet.CONCUR_READ_ONLY);
        }
        else if(rsp.getConcurrency() != -1) {
            st = dataSource.getConnection().createStatement(ResultSet.TYPE_FORWARD_ONLY, rsp.getConcurrency());
        }
        else {
            st = dataSource.getConnection().createStatement();
        }

        if(rsp.getFetchDirection() != -1) {
            st.setFetchDirection(rsp.getFetchDirection());
        }
        if(rsp.getFetchSize() > -1) {
            st.setFetchSize(rsp.getFetchSize());
        }
        if(rsp.getTimeout() > -1) {
            st.setQueryTimeout(rsp.getTimeout());
        }
        if(rsp.getMaxRows() > -1) {
            st.setMaxRows(rsp.getMaxRows());
        }
        if(rsp.getCursorName() != null) {
            st.setCursorName(rsp.getCursorName());
        }
        if(rsp.isPoolable()) {
            st.setPoolable(true);
        }
        if(rsp.getMaxFieldSize() > -1) {
            st.setMaxFieldSize(rsp.getMaxFieldSize());
        }
        return st;
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        return getStatement().execute(sql);
    }

    @Override
    public boolean execute(GString sql) throws SQLException {
        return getStatement().execute(sql.toString());
    }

    @Override
    public int[] executeBatch(String[] queries) throws SQLException {
        Statement st = getStatement();
        for (String query : queries) {
            st.addBatch(query);
        }
        return st.executeBatch();
    }

    @Override
    public int[] executeBatch(GString[] queries) throws SQLException {
        Statement st = getStatement();
        for (GString query : queries) {
            st.addBatch(query.toString());
        }
        return st.executeBatch();
    }

    @Override
    public long[] executeLargeBatch(String[] queries) throws SQLException {
        Statement st = getStatement();
        for (String query : queries) {
            st.addBatch(query);
        }
        return st.executeLargeBatch();
    }

    @Override
    public long[] executeLargeBatch(GString[] queries) throws SQLException {
        Statement st = getStatement();
        for (GString query : queries) {
            st.addBatch(query.toString());
        }
        return st.executeLargeBatch();
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        return getStatement().executeUpdate(sql);
    }

    @Override
    public int executeUpdate(GString sql) throws SQLException {
        return getStatement().executeUpdate(sql.toString());
    }

    @Override
    public long executeLargeUpdate(String sql) throws SQLException {
        return getStatement().executeLargeUpdate(sql);
    }

    @Override
    public long executeLargeUpdate(GString sql) throws SQLException {
        return getStatement().executeLargeUpdate(sql.toString());
    }

    @Override
    public IJdbcTable<?, ?> getTable(String nameOrQuery) {
        try {
            IJdbcTable<?, ?> table = dataSource.getTable(nameOrQuery, getStatement());
            table.setResultSetProperties(rsp);
            return table;
        } catch (SQLException e) {
            LOGGER.error("Unable to get the statement.", e);
            return dataSource.getTable(nameOrQuery);
        }
    }

    @Override
    public IJdbcSpatialTable<?> getSpatialTable(String nameOrQuery) {
        try {
            IJdbcSpatialTable<?> table = dataSource.getSpatialTable(nameOrQuery, getStatement());
            table.setResultSetProperties(rsp);
            return table;
        } catch (SQLException e) {
            LOGGER.error("Unable to get the statement.", e);
            return dataSource.getSpatialTable(nameOrQuery);
        }
    }

    @Override
    public ITable<?, ?> getTable(GString nameOrQuery) {
        try {
            IJdbcTable<?, ?> table = dataSource.getTable(nameOrQuery, getStatement());
            table.setResultSetProperties(rsp);
            return table;
        } catch (SQLException e) {
            LOGGER.error("Unable to get the statement.", e);
            return dataSource.getTable(nameOrQuery);
        }
    }

    @Override
    public ISpatialTable<?, ?> getSpatialTable(GString nameOrQuery) {
        try {
            IJdbcSpatialTable<?> table = dataSource.getSpatialTable(nameOrQuery, getStatement());
            table.setResultSetProperties(rsp);
            return table;
        } catch (SQLException e) {
            LOGGER.error("Unable to get the statement.", e);
            return dataSource.getSpatialTable(nameOrQuery);
        }
    }

    @Override
    public ITable<?, ?> getTable(String query, List<Object> params) {
        try {
            IJdbcTable<?, ?> table = dataSource.getTable(query, params, getStatement());
            table.setResultSetProperties(rsp);
            return table;
        } catch (SQLException e) {
            LOGGER.error("Unable to get the statement.", e);
            return dataSource.getTable(query, params);
        }
    }

    @Override
    public ISpatialTable<?, ?> getSpatialTable(String query, List<Object> params) {
        try {
            IJdbcSpatialTable<?> table = dataSource.getSpatialTable(query, params, getStatement());
            table.setResultSetProperties(rsp);
            return table;
        } catch (SQLException e) {
            LOGGER.error("Unable to get the statement.", e);
            return dataSource.getSpatialTable(query, params);
        }
    }
}
