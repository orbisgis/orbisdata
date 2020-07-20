package org.orbisgis.orbisdata.datamanager.jdbc.dsl;

import groovy.lang.GString;
import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.orbisdata.datamanager.api.datasource.IJdbcDataSource;
import org.orbisgis.orbisdata.datamanager.api.dsl.IBuilderResult;
import org.orbisgis.orbisdata.datamanager.api.dsl.IFilterBuilder;
import org.orbisgis.orbisdata.datamanager.api.dsl.IQueryBuilder;
import org.orbisgis.orbisdata.datamanager.jdbc.TableLocation;

import java.util.List;

import static org.orbisgis.orbisdata.datamanager.api.dataset.DataBaseType.H2GIS;

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

    public QueryBuilder(IJdbcDataSource dataSource, @NotNull String nameOrQuery) {
        this.dataSource = dataSource;
        if(nameOrQuery.startsWith("(") && nameOrQuery.endsWith(")")) {
            location = nameOrQuery;
        }
        else {
            boolean isH2 = H2GIS == dataSource.getDataBaseType();
            location = TableLocation.parse(nameOrQuery, isH2).toString(isH2);
        }
    }

    @Override
    public IBuilderResult filter(String filter) {
        IFilterBuilder filterBuilder = new FilterBuilder(dataSource, getQuery());
        if(filter != null) {
            return filterBuilder.filter(filter);
        }
        else {
            return filterBuilder;
        }
    }

    @Override
    public IBuilderResult filter(GString filter) {
        IFilterBuilder filterBuilder = new FilterBuilder(dataSource, getQuery());
        if(filter != null) {
            return filterBuilder.filter(filter);
        }
        else {
            return filterBuilder;
        }
    }

    @Override
    public IBuilderResult filter(String filter, List<Object> params) {
        IFilterBuilder filterBuilder = new FilterBuilder(dataSource, getQuery());
        if(filter != null) {
            return filterBuilder.filter(filter, params);
        }
        else {
            return filterBuilder;
        }
    }

    @Override
    public IFilterBuilder columns(String... columns) {
        this.columns = String.join(", ", columns);
        return new FilterBuilder(dataSource, getQuery());
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
}
