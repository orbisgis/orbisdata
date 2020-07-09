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
import org.orbisgis.orbisdata.datamanager.api.datasource.IJdbcDataSource;
import org.orbisgis.orbisdata.datamanager.api.datasource.IResultSetBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Stream;

/**
 * Implementation of the {@link IResultSetBuilder} interface.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC / Chaire GEOTERA 2020)
 */
public class ResultSetBuilder implements IResultSetBuilder {

    /**
     * {@link IJdbcDataSource} to use to get the {@link ResultSet}.
     */
    private IJdbcDataSource dataSource;
    /**
     * {@link ResultSet} type.
     */
    private int type = -1;
    /**
     * {@link ResultSet} concurrency.
     */
    private int concur = -1;
    /**
     * {@link ResultSet} holdability.
     */
    private int hold = -1;
    /**
     * {@link ResultSet} holdability.
     */
    private int direction = -1;
    /**
     * Maximum request size.
     */
    private int size = -1;
    /**
     * Request timeout.
     */
    private int timeout = -1;
    /**
     * Maximum row count of the request.
     */
    private int maxRow = -1;
    /**
     * Name of the cursor.
     */
    private String cursorName = null;
    /**
     * Request the {@link Statement} to be poolable.
     */
    private boolean poolable = false;
    /**
     * Maximum size of the fields.
     */
    private int maxFieldSize = -1;

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
        type = ResultSet.TYPE_FORWARD_ONLY;
        return this;
    }

    @Override
    public IResultSetBuilder scrollInsensitive() {
        type = ResultSet.TYPE_FORWARD_ONLY;
        return this;
    }

    @Override
    public IResultSetBuilder scrollSensitive() {
        type = ResultSet.TYPE_FORWARD_ONLY;
        return this;
    }

    @Override
    public IResultSetBuilder readOnly() {
        concur = ResultSet.CONCUR_READ_ONLY;
        return this;
    }

    @Override
    public IResultSetBuilder updatable() {
        concur = ResultSet.CONCUR_READ_ONLY;
        return this;
    }

    @Override
    public IResultSetBuilder holdCursorOverCommit() {
        hold = ResultSet.HOLD_CURSORS_OVER_COMMIT;
        return this;
    }

    @Override
    public IResultSetBuilder closeCursorAtCommit() {
        hold = ResultSet.CLOSE_CURSORS_AT_COMMIT;
        return this;
    }

    @Override
    public IResultSetBuilder fetchForward() {
        direction = ResultSet.FETCH_FORWARD;
        return this;
    }

    @Override
    public IResultSetBuilder fetchReverse() {
        direction = ResultSet.FETCH_REVERSE;
        return this;
    }

    @Override
    public IResultSetBuilder fetchUnknown() {
        direction = ResultSet.FETCH_UNKNOWN;
        return this;
    }

    @Override
    public IResultSetBuilder fetchSize(int size) {
        this.size = size;
        return this;
    }

    @Override
    public IResultSetBuilder timeout(int time) {
        this.timeout = timeout;
        return this;
    }

    @Override
    public IResultSetBuilder maxRow(int maxRow) {
        this.maxRow = maxRow;
        return this;
    }

    @Override
    public IResultSetBuilder cursorName(String name) {
        this.cursorName = name;
        return this;
    }

    @Override
    public IResultSetBuilder poolable() {
        this.poolable = poolable;
        return this;
    }

    @Override
    public IResultSetBuilder maxFieldSize(int size) {
        this.maxFieldSize = size;
        return this;
    }

    private Statement getStatement() throws SQLException {
        Statement st;
        if(type != -1 && concur != -1 && hold != -1) {
            st = dataSource.getConnection().createStatement(type, concur, hold);
        }
        else if(type != -1 && concur != -1) {
            st = dataSource.getConnection().createStatement(type, concur);
        }
        else if(type != -1) {
            st = dataSource.getConnection().createStatement(type, ResultSet.CONCUR_READ_ONLY);
        }
        else if(concur != -1) {
            st = dataSource.getConnection().createStatement(ResultSet.TYPE_FORWARD_ONLY, concur);
        }
        else {
            st = dataSource.getConnection().createStatement();
        }
        if(direction != -1) {
            st.setFetchDirection(direction);
        }
        if(size > -1) {
            st.setFetchSize(size);
        }
        if(timeout > -1) {
            st.setQueryTimeout(timeout);
        }
        if(maxRow > -1) {
            st.setMaxRows(maxRow);
        }
        if(cursorName != null) {
            st.setCursorName(cursorName);
        }
        if(poolable) {
            st.setPoolable(poolable);
        }
        if(maxFieldSize > -1) {
            st.setMaxFieldSize(maxFieldSize);
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
}
