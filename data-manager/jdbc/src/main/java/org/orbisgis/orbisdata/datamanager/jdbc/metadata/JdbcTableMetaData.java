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
package org.orbisgis.orbisdata.datamanager.jdbc.metadata;

import org.h2gis.utilities.GeometryTableUtilities;
import org.h2gis.utilities.SpatialResultSet;
import org.h2gis.utilities.SpatialResultSetMetaData;
import org.orbisgis.orbisdata.datamanager.api.datasource.IJdbcDataSource;
import org.orbisgis.orbisdata.datamanager.api.metadata.IJdbcTableMetaData;
import org.orbisgis.orbisdata.datamanager.jdbc.JdbcSpatialTable;
import org.orbisgis.orbisdata.datamanager.jdbc.TableLocation;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;

/**
 * Contains the metadata of a {@link JdbcSpatialTable}
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Chaire GEOTERA 2020)
 */
public class JdbcTableMetaData extends TableMetaData implements IJdbcTableMetaData {

    /**
     * Cached and wrapped {@link ResultSetMetaData}.
     */
    private ResultSetMetaData metaData;

    public JdbcTableMetaData(String location, String name, int rowCount, ResultSetMetaData metaData,
                             IJdbcDataSource dataSource, ResultSet rs)
            throws SQLException {
        super(location, name, gatherColumnsTypes(metaData, location, dataSource, rs), metaData.getColumnCount(), rowCount,
                metaData instanceof SpatialResultSetMetaData);
        this.metaData = metaData;
    }

    private static LinkedHashMap<String, String> gatherColumnsTypes(
            ResultSetMetaData metaData, String location, IJdbcDataSource dataSource, ResultSet rs)
            throws SQLException {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        for(int i=1; i<=metaData.getColumnCount(); i++) {
            String typeName = metaData.getColumnTypeName(i);
            if(typeName.equals("GEOMETRY")) {
                if(location != null) {
                    typeName = GeometryTableUtilities.getMetaData(dataSource.getConnection(),
                            TableLocation.parse(location), metaData.getColumnName(i)).geometryType;
                }
                else{
                    typeName = GeometryTableUtilities.getMetaData(rs).get(metaData.getColumnName(i)).geometryType;
                }
            }
            map.put(metaData.getColumnName(i), typeName);
        }
        return map;
    }

    @Override
    public boolean isAutoIncrement(int i) throws SQLException {
        return metaData.isAutoIncrement(i);
    }

    @Override
    public boolean isCaseSensitive(int i) throws SQLException {
        return metaData.isCaseSensitive(i);
    }

    @Override
    public boolean isSearchable(int i) throws SQLException {
        return metaData.isSearchable(i);
    }

    @Override
    public boolean isCurrency(int i) throws SQLException {
        return metaData.isCurrency(i);
    }

    @Override
    public int isNullable(int i) throws SQLException {
        return metaData.isNullable(i);
    }

    @Override
    public boolean isSigned(int i) throws SQLException {
        return metaData.isSigned(i);
    }

    @Override
    public int getColumnDisplaySize(int i) throws SQLException {
        return metaData.getColumnDisplaySize(i);
    }

    @Override
    public String getColumnLabel(int i) throws SQLException {
        return metaData.getColumnLabel(i);
    }

    @Override
    public String getColumnName(int i) throws SQLException {
        return metaData.getColumnName(i);
    }

    @Override
    public String getSchemaName(int i) throws SQLException {
        return metaData.getSchemaName(i);
    }

    @Override
    public int getPrecision(int i) throws SQLException {
        return metaData.getPrecision(i);
    }

    @Override
    public int getScale(int i) throws SQLException {
        return metaData.getScale(i);
    }

    @Override
    public String getTableName(int i) throws SQLException {
        return metaData.getTableName(i);
    }

    @Override
    public String getCatalogName(int i) throws SQLException {
        return metaData.getTableName(i);
    }

    @Override
    public int getColumnType(int i) throws SQLException {
        return metaData.getColumnType(i);
    }

    @Override
    public String getColumnTypeName(int i) throws SQLException {
        return metaData.getColumnTypeName(i);
    }

    @Override
    public boolean isReadOnly(int i) throws SQLException {
        return metaData.isReadOnly(i);
    }

    @Override
    public boolean isWritable(int i) throws SQLException {
        return metaData.isWritable(i);
    }

    @Override
    public boolean isDefinitelyWritable(int i) throws SQLException {
        return metaData.isDefinitelyWritable(i);
    }

    @Override
    public String getColumnClassName(int i) throws SQLException {
        return metaData.getColumnClassName(i);
    }

    @Override
    public <T> T unwrap(Class<T> aClass) throws SQLException {
        return metaData.unwrap(aClass);
    }

    @Override
    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        return metaData.isWrapperFor(aClass);
    }
}
