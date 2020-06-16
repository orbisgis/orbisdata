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
import org.orbisgis.orbisdata.datamanager.api.dsl.IConditionOrOptionBuilder;
import org.orbisgis.orbisdata.datamanager.jdbc.JdbcDataSource;

/**
 * Implementation of {@link IConditionOrOptionBuilder}.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public class ConditionOrOptionBuilder extends OptionBuilder implements IConditionOrOptionBuilder {

    private final StringBuilder query;
    private final JdbcDataSource dataSource;

    /**
     * Main constructor.
     *
     * @param request    String request coming from the ISelectBuilder.
     * @param dataSource {@link IJdbcDataSource} where the request will be executed.
     */
    public ConditionOrOptionBuilder(String request, JdbcDataSource dataSource) {
        super(request, dataSource);
        query = new StringBuilder();
        query.append(request).append(" ");
        this.dataSource = dataSource;
    }

    @Override
    public IConditionOrOptionBuilder and(String condition) {
        query.append("AND ");
        query.append(condition);
        return new ConditionOrOptionBuilder(query.toString(), dataSource);
    }

    @Override
    public IConditionOrOptionBuilder or(String condition) {
        query.append("OR ");
        query.append(condition);
        return new ConditionOrOptionBuilder(query.toString(), dataSource);
    }
}
