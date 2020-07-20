package org.orbisgis.orbisdata.datamanager.jdbc.dsl;

import groovy.lang.GString;
import org.orbisgis.orbisdata.datamanager.api.datasource.IJdbcDataSource;
import org.orbisgis.orbisdata.datamanager.api.dsl.IBuilderResult;
import org.orbisgis.orbisdata.datamanager.api.dsl.IFilterBuilder;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

    public FilterBuilder(IJdbcDataSource dataSource, String query) {
        this.dataSource = dataSource;
        this.query = new StringBuilder(query == null ? "" : query);
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
        params.addAll(params);
        query.append(" ").append(filter);
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
}
