package org.orbisgis.datamanager;

import org.h2gis.utilities.TableLocation;
import org.h2gis.utilities.wrapper.ResultSetWrapper;
import org.h2gis.utilities.wrapper.StatementWrapper;
import org.orbisgis.datamanagerapi.dataset.ITable;


import java.sql.ResultSet;

public class Table extends ResultSetWrapper implements ITable {

    private TableLocation tableLocation;

    public Table(TableLocation tableLocation, ResultSet resultSet, StatementWrapper statement) {
        super(resultSet, statement);
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
}
