package org.orbisgis.datamanager;

import groovy.lang.Closure;
import org.h2gis.utilities.TableLocation;
import org.h2gis.utilities.wrapper.ResultSetWrapper;
import org.h2gis.utilities.wrapper.StatementWrapper;
import org.orbisgis.datamanagerapi.dataset.ITable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

public class Table extends ResultSetWrapper implements ITable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Table.class);

    private DataBase dataBase;
    private TableLocation tableLocation;

    public Table(TableLocation tableLocation, ResultSet resultSet, StatementWrapper statement, DataBase dataBase) {
        super(resultSet, statement);
        try {
            resultSet.beforeFirst();
        } catch (SQLException e) {
            LOGGER.error("Unable to go before the first ResultSet row.\n" + e.getLocalizedMessage());
        }
        this.tableLocation = tableLocation;
        this.dataBase = dataBase;
    }

    @Override
    public String getLocation() {
        return tableLocation.toString(dataBase.equals(DataBase.H2GIS));
    }

    @Override
    public String getName() {
        return tableLocation.getTable();
    }

    @Override
    public Iterator<Object> iterator() {
        return new ResultSetIterator(this);
    }

    @Override
    public void eachRow(Closure closure){
        this.forEach(closure::call);
    }
}