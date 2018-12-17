package org.orbisgis.datamanagerapi.datasource;

import org.orbisgis.datamanagerapi.dataset.IFeatureTable;
import org.orbisgis.datamanagerapi.dataset.ITable;

/**
 * Extension of the IDataSource interface dedicated to the usage of a JDBC database.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2018)
 */
public interface IJDBCDataSource extends IDataSource {

    void close();

    ITable getTable(String name);

    IFeatureTable getFeatureTable(String name);
}
