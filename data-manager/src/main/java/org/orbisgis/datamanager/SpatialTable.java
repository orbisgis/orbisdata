package org.orbisgis.datamanager;

import groovy.lang.Closure;
import org.h2gis.utilities.TableLocation;
import org.h2gis.utilities.wrapper.SpatialResultSetImpl;
import org.h2gis.utilities.wrapper.StatementWrapper;
import org.orbisgis.datamanagerapi.dataset.ISpatialTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

public class SpatialTable extends SpatialResultSetImpl implements ISpatialTable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpatialTable.class);

    private TableLocation tableLocation;

    public SpatialTable(TableLocation tableLocation, ResultSet resultSet, StatementWrapper statement) {
        super(resultSet, statement);
        try {
            resultSet.beforeFirst();
        } catch (SQLException e) {
            LOGGER.error("Unable to go before the first ResultSet row.\n" + e.getLocalizedMessage());
        }
        this.tableLocation = tableLocation;
    }

    @Override
    public String getLocation() {
        return tableLocation.toString();
    }

    @Override
    public String getName() {
        return tableLocation.getTable();
    }

    @Override
    public Iterator iterator() {
        return new ResultSetIterator(this);
    }

    @Override
    public void eachRow(Closure<? extends ResultSet> closure){
        try {
            this.next();
        } catch (SQLException e) {
            LOGGER.error(("Unable to get the next row.\n" + e.getLocalizedMessage()));
        }
        closure.call(this);
    }
}
