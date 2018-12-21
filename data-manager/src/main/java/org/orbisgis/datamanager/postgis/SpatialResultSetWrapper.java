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
