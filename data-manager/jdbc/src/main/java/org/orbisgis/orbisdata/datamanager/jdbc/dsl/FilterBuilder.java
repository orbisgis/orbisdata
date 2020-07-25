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
import org.orbisgis.orbisdata.datamanager.api.datasource.IJdbcDataSource;
import org.orbisgis.orbisdata.datamanager.api.dsl.IBuilderResult;
import org.orbisgis.orbisdata.datamanager.api.dsl.IFilterBuilder;

import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

/**
 * Implementation of {@link IFilterBuilder}.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC / Chaire GEOTERA 2020)
 */
public class FilterBuilder extends BuilderResult implements IFilterBuilder {

    private final StringBuilder query;
    private final List<Object> params;
    private final IJdbcDataSource dataSource;
    private Statement statement;

    public FilterBuilder(IJdbcDataSource dataSource, String query, List<Object> params) {
        this.dataSource = dataSource;
        this.query = new StringBuilder(query == null ? "" : query);
        if(query != null && query.startsWith("(") && query.endsWith(")")) {
            this.query.append(" as foo");
        }
        this.params = new LinkedList<>(params);
    }

    public FilterBuilder(IJdbcDataSource dataSource, String query) {
        this.dataSource = dataSource;
        this.query = new StringBuilder(query == null ? "" : query);
        if(query != null && query.startsWith("(") && query.endsWith(")")) {
            this.query.append(" as foo");
        }
        this.params = new LinkedList<>();
    }

    @Override
    public IBuilderResult filter(String filter) {
        if(filter != null) {
            query.append(" ").append(filter);
        }
        return this;
    }

    @Override
    public IBuilderResult filter(GString filter) {
        params.addAll(dataSource.getParameters(filter));
        query.append(" ").append(dataSource.asSql(filter, params));
        return this;
    }

    @Override
    public IBuilderResult filter(String filter, List<Object> params) {
        this.params.addAll(params);
        this.query.append(" ").append(filter);
        return this;
    }

    @Override
    protected IJdbcDataSource getDataSource() {
        return dataSource;
    }

    @Override
    protected String getQuery() {
        return query.toString();
    }

    @Override
    public String toString() {
        return "(" + getQuery() + ")";
    }

    @Override
    public List<Object> getParams() {
        return params;
    }

    @Override
    public Statement getStatement() {
        return statement;
    }

    @Override
    public void setStatement(Statement statement) {
        this.statement=statement;
    }
}
