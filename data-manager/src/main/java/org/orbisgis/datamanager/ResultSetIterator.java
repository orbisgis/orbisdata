package org.orbisgis.datamanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

public class ResultSetIterator implements Iterator<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResultSetIterator.class);

    private ResultSet resultSet;
    private int rowCount = 0;

    public ResultSetIterator(ResultSet resultSet){
        this.resultSet = resultSet;
        try {
            this.resultSet.last();
        } catch (SQLException e) {
            LOGGER.error("Unable to go to the last ResultSet row.\n" + e.getLocalizedMessage());
            return;
        }
        try {
            rowCount = resultSet.getRow();
        } catch (SQLException e) {
            LOGGER.error("Unable to get ResultSet row.\n" + e.getLocalizedMessage());
            return;
        }
        try {
            this.resultSet.beforeFirst();
        } catch (SQLException e) {
            LOGGER.error("Unable to go before the first ResultSet row.\n" + e.getLocalizedMessage());
        }
    }

    @Override
    public boolean hasNext() {
        int row = 0;
        try {
            row = resultSet.getRow();
        } catch (SQLException e) {
            LOGGER.error("Unable to get ResultSet row.\n" + e.getLocalizedMessage());
        }
        return row < rowCount;
    }

    @Override
    public Object next() {
        try {
            resultSet.next();
        } catch (SQLException e) {
            LOGGER.error("Unable to get next row.\n" + e.getLocalizedMessage());
        }
        return resultSet;
    }
}
