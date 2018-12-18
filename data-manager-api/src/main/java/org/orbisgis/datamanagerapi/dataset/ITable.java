package org.orbisgis.datamanagerapi.dataset;

import groovy.lang.Closure;

import java.sql.ResultSet;

/**
 * Implementation of the IDataSet interface. A table is a 2D (column/line) representation of data.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2018)
 */
public interface ITable extends IDataSet, ResultSet {

    void eachRow(Closure closure);
}
