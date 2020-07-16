package org.orbisgis.orbisdata.datamanager.jdbc.dsl;

import org.orbisgis.orbisdata.datamanager.api.datasource.IJdbcDataSource;
import org.orbisgis.orbisdata.datamanager.api.dsl.IBuilderResult;
import org.orbisgis.orbisdata.datamanager.api.dsl.IFilterBuilder;

/**
 * Implementation of {@link IFilterBuilder}.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC / Chaire GEOTERA 2020)
 */
public class FilterBuilder extends BuilderResult implements IFilterBuilder {

    private final StringBuilder query;
    private final IJdbcDataSource dataSource;

    public FilterBuilder(IJdbcDataSource dataSource, String query) {
        this.dataSource = dataSource;
        this.query = new StringBuilder(query == null ? "" : query);
    }

    @Override
    public IBuilderResult filter(String filter) {
        if(filter != null) {
            query.append(" ").append(filter);
        }
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
