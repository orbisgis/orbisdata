/*
 * Bundle DataManager is part of the OrbisGIS platform
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
 * DataManager is distributed under GPL 3 license.
 *
 * Copyright (C) 2018 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * DataManager is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * DataManager is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * DataManager. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.datamanager.h2gis;

import org.h2gis.utilities.SpatialResultSetMetaData;
import org.h2gis.utilities.TableLocation;
import org.h2gis.utilities.wrapper.ResultSetWrapper;
import org.h2gis.utilities.wrapper.SpatialResultSetImpl;
import org.h2gis.utilities.wrapper.StatementWrapper;
import org.orbisgis.datamanager.JdbcDataSource;
import org.orbisgis.datamanager.JdbcSpatialTable;
import org.orbisgis.datamanagerapi.dataset.DataBaseType;
import org.orbisgis.datamanagerapi.dataset.ISpatialTable;
import org.orbisgis.datamanagerapi.dataset.ITable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2018-2019)
 */
public class H2gisSpatialTable extends JdbcSpatialTable {

    private static final Logger LOGGER = LoggerFactory.getLogger(H2gisTable.class);

    /**
     * Main constructor.
     *
     * @param tableLocation TableLocation that identify the represented table.
     * @param baseQuery Query for the creation of the ResultSet
     * @param statement Statement used to request the database.
     * @param jdbcDataSource DataSource to use for the creation of the resultSet.
     */
    public H2gisSpatialTable(TableLocation tableLocation, String baseQuery, StatementWrapper statement,
                      JdbcDataSource jdbcDataSource) {
        super(DataBaseType.H2GIS, jdbcDataSource, tableLocation, statement, baseQuery);
    }

    @Override
    public SpatialResultSetMetaData getMetadata(){
        try {
            return getResultSet().getMetaData().unwrap(SpatialResultSetMetaData.class);
        } catch (SQLException e) {
            LOGGER.error("Unable to get the metadata.\n" + e.getLocalizedMessage());
            return null;
        }
    }

    @Override
    public Object asType(Class clazz) {
        if (clazz == ITable.class || clazz == H2gisTable.class) {
            return new H2gisTable(getTableLocation(), getBaseQuery(), (StatementWrapper)getStatement(),
                    getJdbcDataSource());
        } else if (clazz == ISpatialTable.class || clazz == H2gisSpatialTable.class) {
            return new H2gisSpatialTable(getTableLocation(), getBaseQuery(), (StatementWrapper)getStatement(),
                    getJdbcDataSource());
        } else {
            return null;
        }
    }
}
