package org.orbisgis.datamanagerapi.datasource;

import org.orbisgis.datamanagerapi.dataset.ISpatialTable;
import org.orbisgis.datamanagerapi.dataset.ITable;

import java.util.Collection;
import java.util.List;

/**
 * Extension of the IDataSource interface dedicated to the usage of a JDBC database.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2018)
 */
public interface IJdbcDataSource extends IDataSource {

    /**
     * Close the datasource
     */
    void close();

    /**
     * Return a {@link ITable}
     * @param name of the table
     * @return
     */
    ITable getTable(String name);

    /**
     * Return a {@link ISpatialTable}
     * @param name of the table
     * @return
     */
    ISpatialTable getSpatialTable(String name);

    /**
     * Get all table names from the underlying datasource.
     * @return
     */
    Collection<String> getTables();
}
