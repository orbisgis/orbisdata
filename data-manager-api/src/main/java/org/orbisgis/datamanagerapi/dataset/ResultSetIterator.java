/*
 * Bundle DataManager API is part of the OrbisGIS platform
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
 * DataManager API is distributed under GPL 3 license.
 *
 * Copyright (C) 2019 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * DataManager API is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * DataManager API is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * DataManager API. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.datamanagerapi.dataset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

/**
 * Iterator dedicated to the iteration on a {@link ResultSet}.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2018-2019)
 */
public class ResultSetIterator implements Iterator<Object> {

    /** Class {@link Logger} */
    private static final Logger LOGGER = LoggerFactory.getLogger(ResultSetIterator.class);

    /** Iterated {@link ResultSet} */
    private ResultSet resultSet;
    /** Count of {@link ResultSet} row */
    private int rowCount = 0;

    public ResultSetIterator(){
        this.resultSet = null;
        LOGGER.warn("There is no ResultSet so there will no data.");
    }

    /**
     * Main constructor.
     *
     * @param resultSet {@link ResultSet} to iterate.
     */
    public ResultSetIterator(ResultSet resultSet) throws SQLException {
        this.resultSet = resultSet;
        try {
            this.resultSet.last();
            rowCount = resultSet.getRow();
            this.resultSet.beforeFirst();
        } catch (SQLException e) {
            LOGGER.error("Unable to query the ResultSet.\n" + e.getLocalizedMessage());
            throw e;
        }
    }

    @Override
    public boolean hasNext() {
        if(resultSet == null) {
            return false;
        }
        int row;
        try {
            row = resultSet.getRow();
        } catch (SQLException e) {
            LOGGER.error("Unable to get ResultSet row.\n" + e.getLocalizedMessage());
            return false;
        }
        return row < rowCount;
    }

    @Override
    public ResultSet next() {
        if(resultSet == null) {
            return null;
        }
        try {
            if (!resultSet.next()) {
                LOGGER.error("Unable to move to the next row.");
            }
        } catch (SQLException e) {
            LOGGER.error("Unable to get next row.\n" + e.getLocalizedMessage());
        }
        return resultSet;
    }
    
    
}
