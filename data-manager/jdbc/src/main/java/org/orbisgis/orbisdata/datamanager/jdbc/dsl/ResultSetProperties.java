package org.orbisgis.orbisdata.datamanager.jdbc.dsl;

import org.orbisgis.orbisdata.datamanager.api.dsl.IResultSetProperties;

import java.sql.ResultSet;

public class ResultSetProperties implements IResultSetProperties {

    /** Type of the {@link java.sql.ResultSet}. */
    private int type = -1;
    /** {@link ResultSet} concurrency. */
    private int concurrency = -1;
    /** {@link ResultSet} holdability. */
    private int holdability = -1;
    /** {@link ResultSet} fetch direction. */
    private int fetchDirection = -1;
    /** {@link ResultSet} fetch size. */
    private int fetchSize = -1;
    /** {@link java.sql.Statement} query timeout. */
    private int timeout = -1;
    /** {@link java.sql.Statement} max row count. */
    private int maxRows = -1;
    /** {@link java.sql.Statement} cursor name. */
    private String cursorName = null;
    /** {@link java.sql.Statement} poolable status. */
    private boolean poolable = false;
    /** {@link java.sql.Statement} maximum field size. */
    private int fieldSize = -1;

    @Override
    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public void setConcurrency(int concurrency) {
        this.concurrency = concurrency;
    }

    @Override
    public int getConcurrency() {
        return concurrency;
    }

    @Override
    public void setHoldability(int holdability) {
        this.holdability = holdability;
    }

    @Override
    public int getHoldability() {
        return holdability;
    }

    @Override
    public void setFetchDirection(int fetchDirection) {
        this.fetchDirection = fetchDirection;
    }

    @Override
    public int getFetchDirection() {
        return fetchDirection;
    }

    @Override
    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    @Override
    public int getFetchSize() {
        return fetchSize;
    }

    @Override
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public int getTimeout() {
        return timeout;
    }

    @Override
    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
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

    }

    @Override
    public boolean isPoolable() {
        return false;
    }

    @Override
    public void setMaxFieldSize(int maxFieldSize) {

    }

    @Override
    public int getMaxFieldSize() {
        return 0;
    }
}
