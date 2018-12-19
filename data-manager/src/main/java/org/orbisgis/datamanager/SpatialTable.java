package org.orbisgis.datamanager;

import groovy.lang.Closure;
import groovy.lang.MetaClass;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.h2gis.utilities.TableLocation;
import org.h2gis.utilities.wrapper.SpatialResultSetImpl;
import org.h2gis.utilities.wrapper.StatementWrapper;
import org.locationtech.jts.geom.Geometry;
import org.orbisgis.datamanagerapi.dataset.ISpatialTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SpatialTable extends SpatialResultSetImpl implements ISpatialTable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpatialTable.class);

    private Database dataBase;
    private TableLocation tableLocation;
    private MetaClass metaClass;
    private Map<String, Object> propertyMap;

    public SpatialTable(TableLocation tableLocation, ResultSet resultSet, StatementWrapper statement, Database dataBase) {
        super(resultSet, statement);
        try {
            resultSet.beforeFirst();
        } catch (SQLException e) {
            LOGGER.error("Unable to go before the first ResultSet row.\n" + e.getLocalizedMessage());
        }
        this.dataBase = dataBase;
        this.tableLocation = tableLocation;
        this.metaClass = InvokerHelper.getMetaClass(getClass());
        this.propertyMap = new HashMap<>();
    }

    @Override
    public String getLocation() {
        return tableLocation.toString(dataBase.equals(Database.H2GIS));
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

    public Object invokeMethod(String name, Object args) {
        return null;
    }

    public Object getProperty(String propertyName) {
        try {
            return getObject(propertyName);
        } catch (SQLException e) {
            LOGGER.error("Unable to find the column '" + propertyName + "'.\n" + e.getLocalizedMessage());
        }
        return propertyMap.get(propertyName);
    }

    @Override
    public void setProperty(String propertyName, Object newValue) {
        propertyMap.put(propertyName, newValue);
    }

    public MetaClass getMetaClass() {
        return metaClass;
    }

    @Override
    public void setMetaClass(MetaClass metaClass) {
        this.metaClass = metaClass;
    }
}
