package org.orbisgis.datamanagerapi.dataset;

import groovy.lang.GroovyObject;

/**
 * Raw set of data.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2018)
 */
public interface IDataSet extends Iterable<Object>, GroovyObject {
    String getLocation();
    String getName();
}
