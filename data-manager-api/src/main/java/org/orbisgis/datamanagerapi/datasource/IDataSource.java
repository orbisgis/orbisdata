package org.orbisgis.datamanagerapi.datasource;

import org.orbisgis.datamanagerapi.dataset.IDataSet;

/**
 * Raw source of data.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2018)
 */
public interface IDataSource {
    /**
     * Return the IDataSet implementation corresponding to the given name.
     *
     * @param name Name of the IDataSet.
     *
     * @return The implementation of IDataSet corresponding to the given name.
     */
    IDataSet getDataSet(String name);
}
