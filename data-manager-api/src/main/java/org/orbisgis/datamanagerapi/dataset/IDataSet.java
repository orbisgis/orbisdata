package org.orbisgis.datamanagerapi.dataset;

/**
 * Raw set of data.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2018)
 */
public interface IDataSet extends Iterable {

    String getLocation();
    String getName();
}
