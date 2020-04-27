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
 * DataManager is distributed under LGPL 3 license.
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
package org.orbisgis.orbisdata.datamanager.jdbc.postgis;

import org.h2gis.postgis_jts.StatementWrapper;
import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.commons.annotations.Nullable;
import org.orbisgis.orbisdata.datamanager.api.dataset.DataBaseType;
import org.orbisgis.orbisdata.datamanager.api.dataset.ISpatialTable;
import org.orbisgis.orbisdata.datamanager.api.dataset.ITable;
import org.orbisgis.orbisdata.datamanager.jdbc.JdbcDataSource;
import org.orbisgis.orbisdata.datamanager.jdbc.JdbcSpatialTable;
import org.orbisgis.orbisdata.datamanager.jdbc.TableLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Implementation of {@link ISpatialTable} for PostGIG.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2018-2019)
 */
public class PostgisSpatialTable extends JdbcSpatialTable {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostgisSpatialTable.class);

    /**
     * Main constructor.
     *
     * @param tableLocation  {@link TableLocation} that identify the represented table.
     * @param baseQuery      Query for the creation of the ResultSet
     * @param statement      Statement used to request the database.
     * @param jdbcDataSource DataSource to use for the creation of the resultSet.
     */
    public PostgisSpatialTable(@Nullable TableLocation tableLocation, @NotNull String baseQuery,
                               @NotNull StatementWrapper statement, @NotNull JdbcDataSource jdbcDataSource) {
        super(DataBaseType.H2GIS, jdbcDataSource, tableLocation, statement, baseQuery);
    }

    @Override
    protected ResultSet getResultSet() {
        if (resultSet == null) {
            try {
                resultSet = getStatement().executeQuery(getBaseQuery());
            } catch (SQLException e) {
                LOGGER.error("Unable to execute the query '" + getBaseQuery() + "'.\n" + e.getLocalizedMessage());
                return null;
            }
            try {
                resultSet.beforeFirst();
            } catch (SQLException e) {
                LOGGER.error("Unable to go before the first ResultSet row.\n" + e.getLocalizedMessage());
                return null;
            }
        }
        return new SpatialResultSetWrapper(resultSet, (StatementWrapper) getStatement());
    }

    @Override
    public Object asType(@NotNull Class<?> clazz) {
        if (ISpatialTable.class.isAssignableFrom(clazz)) {
            return new PostgisSpatialTable(getTableLocation(), getBaseQuery(), (StatementWrapper) getStatement(),
                    getJdbcDataSource());
        } else if (ITable.class.isAssignableFrom(clazz)) {
            return new PostgisTable(getTableLocation(), getBaseQuery(), (StatementWrapper) getStatement(),
                    getJdbcDataSource());
        } else {
            return super.asType(clazz);
        }
    }

    @Override
    public ISpatialTable reproject(int srid) {
        try {
            ResultSetMetaData meta = getMetaData();
            if(meta == null){
                LOGGER.error("Unable to get metadata for reprojection");
                return null;
            }
            int columnCount = meta.getColumnCount();
            String[] fieldNames = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                String columnName = meta.getColumnName(i);
                if (meta.getColumnTypeName(i).equalsIgnoreCase("geometry")) {
                    fieldNames[i - 1] = "ST_TRANSFORM(" + columnName + ", " + srid + ") AS " + columnName;
                } else {
                    fieldNames[i - 1] = columnName;
                }
            }
            TableLocation location = getTableLocation();
            if(location == null){
                LOGGER.error("Unable to get table location for reprojection");
                return null;
            }
            String query = "SELECT " + String.join(",", fieldNames) + " FROM " + location.toString(true);
            return new PostgisSpatialTable(null, query, (StatementWrapper) getStatement(), getJdbcDataSource());
        } catch (SQLException e) {
            LOGGER.error("Cannot reproject the table '" + getLocation() + "' in the SRID '" + srid + "'.\n", e);
            return null;
        }
    }
}
