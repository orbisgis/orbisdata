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
package org.orbisgis.orbisdata.datamanager.jdbc;

import org.orbisgis.orbisdata.datamanager.api.dataset.DataBaseType;
import org.orbisgis.orbisdata.datamanager.api.dataset.ITableLocation;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implementation of the {@link ITableLocation}
 */
public class TableLocation extends org.h2gis.utilities.TableLocation implements ITableLocation {

    /**
     * The dataSource name.
     */
    private String dataSource;

    /**
     * @param dataSource DataSource name
     * @param rs         Result set obtained through {@link java.sql.DatabaseMetaData#getTables(String, String, String, String[])}
     * @throws SQLException
     */
    public TableLocation(String dataSource, ResultSet rs) throws SQLException {
        super(rs);
        this.dataSource = dataSource;
    }

    /**
     * @param dataSource DataSource name
     * @param catalog    Catalog name without quotes
     * @param schema     Schema name without quotes
     * @param table      Table name without quotes
     */
    public TableLocation(String dataSource, String catalog, String schema, String table) {
        super(catalog, schema, table);
        this.dataSource = dataSource;
    }

    /**
     * @param dataSource DataSource name
     * @param schema     Schema name without quotes
     * @param table      Table name without quotes
     */
    public TableLocation(String dataSource, String schema, String table) {
        super(schema, table);
        this.dataSource = dataSource;
    }

    /**
     * @param dataSource DataSource name
     * @param table      Table name without quotes
     */
    public TableLocation(String dataSource, String table) {
        super(table);
        this.dataSource = dataSource;
    }

    @Override
    public String getDataSource() {
        return dataSource;
    }

    @Override
    public String toString(DataBaseType type) {
        return super.toString();
    }
}
