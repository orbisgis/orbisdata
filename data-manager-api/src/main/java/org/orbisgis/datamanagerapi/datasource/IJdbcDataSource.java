package org.orbisgis.datamanagerapi.datasource;

import org.orbisgis.datamanagerapi.dataset.ISpatialTable;
import org.orbisgis.datamanagerapi.dataset.ITable;

/**
 * Extension of the IDataSource interface dedicated to the usage of a JDBC database.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2018)
 */
public interface IJdbcDataSource extends IDataSource {

    void close();

    ITable getTable(String name);

    ISpatialTable getSpatialTable(String name);
}
