/*
 * Bundle JDBC is part of the OrbisGIS platform
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
 * JDBC is distributed under LGPL 3 license.
 *
 * Copyright (C) 2018 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * JDBC is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * JDBC is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * JDBC. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.data.jdbc.resultset;

import org.orbisgis.commons.utilities.CheckUtils;
import org.orbisgis.data.api.dataset.IStreamResultSet;
import org.orbisgis.data.api.dataset.ITable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * Wrapper of {@link ResultSet} used to simplified the usage of {@link ITable#stream()}, avoiding the usage of
 * try/catch.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC)
 */
public class StreamResultSet implements IStreamResultSet {

    /**
     * Logger used for exception logging.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(StreamResultSet.class);

    /**
     * Internal {@link ResultSet}.
     */
    private final ResultSet resultSet;
    
    public StreamResultSet(ResultSet resultSet){
        CheckUtils.checkNotNull(resultSet, "The given ResultSet should not be null.");
        this.resultSet = resultSet;
    }

    @Override
    public ResultSet getResultSet() {
        return resultSet;
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }
}
