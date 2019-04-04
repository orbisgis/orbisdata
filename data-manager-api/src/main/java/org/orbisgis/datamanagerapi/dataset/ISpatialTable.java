/*
 * Bundle DataManager API is part of the OrbisGIS platform
 *
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 *
 * DataManager API  is distributed under GPL 3 license.
 *
 * Copyright (C) 2019 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * DataManager API  is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * DataManager API  is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * DataManager API. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.datamanagerapi.dataset;

import org.h2gis.utilities.SpatialResultSet;
import org.h2gis.utilities.SpatialResultSetMetaData;
import org.locationtech.jts.geom.Geometry;

import java.util.Map;

/**
 * Extension of {@link ITable}. A {@link ISpatialTable} is a specialisation with at least one Geometry column.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2018-2019)
 */
public interface ISpatialTable extends IJdbcTable, SpatialResultSet {

    /**
     * Get the {@link SpatialResultSetMetaData} of the {@link ISpatialTable}.
     *
     * @return The metadata object.
     */
    @Override
    SpatialResultSetMetaData getMetadata();

    @Override
    Geometry getGeometry(int columnIndex);

    @Override
    Geometry getGeometry(String columnLabel);

    @Override
    Geometry getGeometry();

    /**
     * Returns a {@link Map} containing the field names as key and the SFS geometry type (well known name) as value.
     * @return The field names as key and geometry types as value.
     */
    Map<String, String> getGeometryTypes();
}
