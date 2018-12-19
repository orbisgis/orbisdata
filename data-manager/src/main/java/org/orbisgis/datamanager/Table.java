package org.orbisgis.datamanager;

import groovy.lang.Closure;
import groovy.lang.MetaClass;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.h2gis.utilities.TableLocation;
import org.h2gis.utilities.wrapper.ResultSetWrapper;
import org.h2gis.utilities.wrapper.StatementWrapper;
import org.orbisgis.datamanagerapi.dataset.ITable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Table extends ResultSetWrapper implements ITable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Table.class);

    private Database dataBase;
    private TableLocation tableLocation;
    private MetaClass metaClass;
    private Map<String, Object> propertyMap;

    public Table(TableLocation tableLocation, ResultSet resultSet, StatementWrapper statement, Database dataBase) {
        super(resultSet, statement);
        try {
            resultSet.beforeFirst();
        } catch (SQLException e) {
            LOGGER.error("Unable to go before the first ResultSet row.\n" + e.getLocalizedMessage());
        }
        this.tableLocation = tableLocation;
        this.dataBase = dataBase;
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