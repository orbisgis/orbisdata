package org.orbisgis.datamanager;

import groovy.lang.Closure;
import org.h2gis.utilities.TableLocation;
import org.h2gis.utilities.wrapper.SpatialResultSetImpl;
import org.h2gis.utilities.wrapper.StatementWrapper;
import org.locationtech.jts.geom.Geometry;
import org.orbisgis.datamanagerapi.dataset.ISpatialTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

public class SpatialTable extends SpatialResultSetImpl implements ISpatialTable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpatialTable.class);

    private DataBase dataBase;
    private TableLocation tableLocation;

    public SpatialTable(TableLocation tableLocation, ResultSet resultSet, StatementWrapper statement, DataBase dataBase) {
        super(resultSet, statement);
        try {
            resultSet.beforeFirst();
        } catch (SQLException e) {
            LOGGER.error("Unable to go before the first ResultSet row.\n" + e.getLocalizedMessage());
        }
        this.dataBase = dataBase;
        this.tableLocation = tableLocation;
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

    @Override
    public Geometry getGeometry(int columnIndex){
        try {
            return super.getGeometry(columnIndex);
        } catch (SQLException e) {
            LOGGER.error("Unable to get the geometry at '" + columnIndex + "'.\n" + e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public Geometry getGeometry(String columnLabel){
        try {
            return super.getGeometry(columnLabel);
        } catch (SQLException e) {
            LOGGER.error("Unable to get the geometry of '" + columnLabel + "'.\n" + e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public Geometry getGeometry(){
        try {
            return super.getGeometry();
        } catch (SQLException e) {
            LOGGER.error("Unable to get the geometry.\n" + e.getLocalizedMessage());
        }
        return null;
    }
}
