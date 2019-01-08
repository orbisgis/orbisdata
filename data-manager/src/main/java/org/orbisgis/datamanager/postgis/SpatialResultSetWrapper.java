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
package org.orbisgis.datamanager.postgis;

import org.h2gis.utilities.SpatialResultSet;
import org.h2gis.utilities.SpatialResultSetMetaData;
import org.locationtech.jts.geom.Geometry;
import org.orbisgis.postgis_jts.ResultSetWrapper;
import org.orbisgis.postgis_jts.StatementWrapper;

import java.sql.ResultSet;
import java.sql.SQLException;

//TODO move to the postgis-jts project.
public class SpatialResultSetWrapper extends ResultSetWrapper implements SpatialResultSet {
    private int firstGeometryFieldIndex = -1;

    public SpatialResultSetWrapper(ResultSet resultSet, StatementWrapper statement) {
        super(statement, resultSet);
    }

    private int getFirstGeometryFieldIndex() throws SQLException {
        if (this.firstGeometryFieldIndex == -1) {
            this.firstGeometryFieldIndex = this.getMetaData().unwrap(SpatialResultSetMetaData.class).getFirstGeometryFieldIndex();
        }

        return this.firstGeometryFieldIndex;
    }

    public Geometry getGeometry(int columnIndex) throws SQLException {
        Object field = this.getObject(columnIndex);
        if (field == null) {
            return (Geometry)field;
        } else if (field instanceof Geometry) {
            return (Geometry)field;
        } else {
            throw new SQLException("The column " + this.getMetaData().getColumnName(columnIndex) + " is not a Geometry");
        }
    }

    public Geometry getGeometry(String columnLabel) throws SQLException {
        Object field = this.getObject(columnLabel);
        if (field == null) {
            return (Geometry)field;
        } else if (field instanceof Geometry) {
            return (Geometry)field;
        } else {
            throw new SQLException("The column " + columnLabel + " is not a Geometry");
        }
    }

    public Geometry getGeometry() throws SQLException {
        return this.getGeometry(this.getFirstGeometryFieldIndex());
    }

    public void updateGeometry(int columnIndex, Geometry geometry) throws SQLException {
        this.updateObject(columnIndex, geometry);
    }

    public void updateGeometry(String columnLabel, Geometry geometry) throws SQLException {
        this.updateObject(columnLabel, geometry);
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            try {
                return iface.cast(this);
            } catch (ClassCastException var3) {
                throw new SQLException(var3);
            }
        } else {
            return super.unwrap(iface);
        }
    }
}
