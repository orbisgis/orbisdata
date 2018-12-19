package org.orbisgis.datamanagerapi.datasource;

import org.orbisgis.datamanagerapi.dataset.ISpatialTable;
import org.orbisgis.datamanagerapi.dataset.ITable;

import java.util.Collection;

/**
 * Extension of the IDataSource interface dedicated to the usage of a JDBC database.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2018)
 */
public interface IJdbcDataSource extends IDataSource {

    /**
     * Close the underlying datasource
     */
    void close();

    /**
     * Return a {@link ITable} by name
     * @param tableName Name of the table
     * @return
     */
    ITable getTable(String tableName);

    /**
     * Return a {@link ISpatialTable} by name
     * @param tableName Name of the table
     * @return
     */
    ISpatialTable getSpatialTable(String tableName);

    /**
     * Get all table names from the underlying datasource.
     * @return
     */
    Collection<String> getTableNames();
}
