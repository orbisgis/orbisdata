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
package org.orbisgis.datamanager;

import org.h2gis.utilities.SpatialResultSet;
import org.h2gis.utilities.TableLocation;
import org.locationtech.jts.geom.Geometry;
import org.orbisgis.datamanagerapi.dataset.DataBaseType;
import org.orbisgis.datamanagerapi.dataset.ISpatialTable;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * Contains the methods which are in common to all the IJdbcTable subclasses.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public abstract class JdbcSpatialTable extends JdbcTable implements ISpatialTable  {

    /**
     * Main constructor.
     *
     * @param dataBaseType Type of the DataBase where this table comes from.
     * @param tableLocation TableLocation that identify the represented table.
     * @param baseQuery Query for the creation of the ResultSet
     * @param statement Statement used to request the database.
     * @param jdbcDataSource DataSource to use for the creation of the resultSet.
     */
    public JdbcSpatialTable(DataBaseType dataBaseType, JdbcDataSource jdbcDataSource, TableLocation tableLocation,
                            Statement statement, String baseQuery) {
        super(dataBaseType, jdbcDataSource, tableLocation, statement, baseQuery);
    }

    @Override
    public Geometry getGeometry(int columnIndex){
        try {
            return ((SpatialResultSet)getResultSet()).getGeometry(columnIndex);
        } catch (SQLException e) {
            LOGGER.error("Unable to get the geometry at '" + columnIndex + "'.\n" + e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public Geometry getGeometry(String columnLabel){
        try {
            return ((SpatialResultSet)getResultSet()).getGeometry(columnLabel);
        } catch (SQLException e) {
            LOGGER.error("Unable to get the geometry of '" + columnLabel + "'.\n" + e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public Geometry getGeometry(){
        try {
            return ((SpatialResultSet)getResultSet()).getGeometry();
        } catch (SQLException e) {
            LOGGER.error("Unable to get the geometry.\n" + e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public void updateGeometry(int i, Geometry geometry) throws SQLException {
        ((SpatialResultSet)getResultSet()).updateGeometry(i, geometry);
    }

    @Override
    public void updateGeometry(String s, Geometry geometry) throws SQLException {
        ((SpatialResultSet)getResultSet()).updateGeometry(s, geometry);
    }
}