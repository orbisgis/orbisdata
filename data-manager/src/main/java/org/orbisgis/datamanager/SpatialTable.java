package org.orbisgis.datamanager;

import org.h2gis.utilities.TableLocation;
import org.h2gis.utilities.wrapper.SpatialResultSetImpl;
import org.h2gis.utilities.wrapper.StatementWrapper;
import org.orbisgis.datamanagerapi.dataset.ISpatialTable;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class SpatialTable extends SpatialResultSetImpl implements ISpatialTable {

    private TableLocation tableLocation;

    public SpatialTable(TableLocation tableLocation, ResultSet resultSet, StatementWrapper statement) {
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
