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
package org.orbisgis.orbisdata.datamanager.jdbc.dsl;

import groovy.lang.GString;
import org.h2gis.utilities.TableLocation;
import org.h2gis.utilities.dbtypes.DBTypes;
import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.orbisdata.datamanager.api.dataset.ISpatialTable;
import org.orbisgis.orbisdata.datamanager.api.dataset.ITable;
import org.orbisgis.orbisdata.datamanager.api.datasource.IJdbcDataSource;
import org.orbisgis.orbisdata.datamanager.api.dsl.IBuilderResult;
import org.orbisgis.orbisdata.datamanager.api.dsl.IFilterBuilder;
import org.orbisgis.orbisdata.datamanager.api.dsl.IQueryBuilder;
import org.orbisgis.orbisdata.datamanager.api.dsl.IResultSetProperties;

import java.util.Arrays;
import java.util.List;

/**
 * Implementation of {@link IQueryBuilder}.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC / Chaire GEOTERA 2020)
 */
public class QueryBuilder extends BuilderResult implements IQueryBuilder {

    private String columns = "*";
    private final String location;
    private final IJdbcDataSource dataSource;
    private final IResultSetProperties rsp;

    public QueryBuilder(IJdbcDataSource dataSource, @NotNull String nameOrQuery, IResultSetProperties properties) {
        this.dataSource = dataSource;
        if(nameOrQuery.startsWith("(") && nameOrQuery.endsWith(")")) {
            location = nameOrQuery + " as foo";
        }
        else {
            location = TableLocation.parse(nameOrQuery, dataSource.getDataBaseType()).toString();
        }
        rsp = properties.copy();
    }

    @Override
    public IBuilderResult filter(String filter) {
        IFilterBuilder filterBuilder = new FilterBuilder(dataSource, getQuery(), getResultSetProperties());
        if(filter != null) {
            return filterBuilder.filter(filter);
        }
        else {
            return filterBuilder;
        }
    }

    @Override
    public IBuilderResult filter(GString filter) {
        IFilterBuilder filterBuilder = new FilterBuilder(dataSource, getQuery(), getResultSetProperties());
        if(filter != null) {
            return filterBuilder.filter(filter);
        }
        else {
            return filterBuilder;
        }
    }

    @Override
    public IBuilderResult filter(String filter, List<Object> params) {
        IFilterBuilder filterBuilder = new FilterBuilder(dataSource, getQuery(), getResultSetProperties());
        if(filter != null) {
            return filterBuilder.filter(filter, params);
        }
        else {
            return filterBuilder;
        }
    }

    @Override
    public IFilterBuilder columns(String... columns) {
        if(columns == null || columns.length == 0 || Arrays.stream(columns).allMatch(s -> s == null || s.isEmpty())) {
            this.columns = "*";
        }
        else {
            this.columns = String.join(", ", columns);
        }
        return new FilterBuilder(dataSource, getQuery(), getResultSetProperties());
    }

    @Override
    protected IJdbcDataSource getDataSource() {
        return dataSource;
    }

    @Override
    protected String getQuery() {
        return "SELECT " + columns + " FROM " + location;
    }

    @Override
    public String toString() {
        return "(" + getQuery() + ")";
    }

    @Override
    public List<Object> getParams() {
        return null;
    }

    public IResultSetProperties getResultSetProperties(){
        return rsp;
    }

    @Override
    public ITable<?,?> getTable() {
        ResultSetBuilder rsb = new ResultSetBuilder(getDataSource(), rsp);
        return rsb.getTable(toString(), getParams());
    }

    @Override
    public ISpatialTable<?> getSpatialTable() {
        ResultSetBuilder rsb = new ResultSetBuilder(getDataSource(), rsp);
        return rsb.getSpatialTable(toString(), getParams());
    }
}
