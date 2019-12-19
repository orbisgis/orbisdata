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

import org.orbisgis.orbisdata.datamanager.api.datasource.IJdbcDataSource;
import org.orbisgis.orbisdata.datamanager.api.dsl.IOptionBuilder;
import org.orbisgis.orbisdata.datamanager.jdbc.JdbcDataSource;

import java.util.Map;

/**
 * Implementation of {@link IOptionBuilder}.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public class OptionBuilder extends BuilderResult implements IOptionBuilder {

    private StringBuilder query;
    private JdbcDataSource dataSource;

    /**
     * Main constructor.
     *
     * @param request    String request coming from the ISelectBuilder.
     * @param dataSource {@link IJdbcDataSource} where the request will be executed.
     */
    public OptionBuilder(String request, JdbcDataSource dataSource) {
        query = new StringBuilder();
        query.append(request).append(" ");
        this.dataSource = dataSource;
    }

    @Override
    public IOptionBuilder groupBy(String... fields) {
        query.append("GROUP BY ");
        query.append(String.join(",", fields));
        return new OptionBuilder(query.toString(), dataSource);
    }

    @Override
    public IOptionBuilder orderBy(Map<String, Order> orderByMap) {
        query.append("ORDER BY ");
        orderByMap.forEach((key, value) -> query.append(key).append(" ").append(value.name()).append(", "));
        query.deleteCharAt(query.length()-2);
        return new OptionBuilder(query.toString(), dataSource);
    }

    @Override
    public IOptionBuilder orderBy(String field, Order order) {
        query.append("ORDER BY ").append(field).append(" ").append(order.name());
        return new OptionBuilder(query.toString(), dataSource);
    }

    @Override
    public IOptionBuilder orderBy(String field) {
        query.append("ORDER BY ").append(field);
        return new OptionBuilder(query.toString(), dataSource);
    }

    @Override
    public IOptionBuilder limit(int limitCount) {
        query.append("LIMIT ").append(limitCount);
        return new OptionBuilder(query.toString(), dataSource);
    }

    @Override
    protected JdbcDataSource getDataSource() {
        return dataSource;
    }

    @Override
    protected String getQuery() {
        return query.toString();
    }
}
