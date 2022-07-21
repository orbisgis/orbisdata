/*
 * Bundle JDBC API is part of the OrbisGIS platform
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
 * JDBC API is distributed under LGPL 3 license.
 *
 * Copyright (C) 2019-2020 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * JDBC API is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * JDBC API is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * JDBC API. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.data.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

/**
 * Iterator dedicated to the iteration on a {@link T}.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (Lab-STICC UBS 2018-2019)
 */
public class ResultSetIterator<T extends ResultSet> implements Iterator<T> {

    /**
     * Class {@link Logger}
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ResultSetIterator.class);

    /**
     * Iterated {@link ResultSet}
     */
    private final T resultSet;

    private boolean nextDone = false;

    /**
     * Main constructor.
     *
     * @param resultSet {@link ResultSet} to iterate.
     */
    public ResultSetIterator(T resultSet) {
        this.resultSet = resultSet;
        if(this.resultSet == null) {
            LOGGER.warn("There is no ResultSet so there will no data.");
        }
    }

    @Override
    public boolean hasNext() {
        try {
            nextDone = true;
            return resultSet != null && resultSet.next();
        } catch (SQLException e) {
            LOGGER.error("Unable to get next row.\n", e);
            nextDone = false;
            return false;
        }
    }

    @Override
    public T next() {
        if(!nextDone) {
            if(!hasNext()) {
                return null;
            }
        }
        return resultSet;
    }

}
