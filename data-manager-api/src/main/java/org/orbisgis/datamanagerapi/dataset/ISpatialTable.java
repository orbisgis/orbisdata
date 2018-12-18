package org.orbisgis.datamanagerapi.dataset;

import org.h2gis.utilities.SpatialResultSet;

/**
 * Extension of ITable. A ISpatialTable is a specialisation of ITable with at least one Geometry column.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2018)
 */
public interface ISpatialTable extends ITable, SpatialResultSet {
}
