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
 * DataManager is distributed under GPL 3 license.
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
 * @author Sylvain PALOMINOS (UBS 2018)
 */
public class ResultSetIterator implements Iterator<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResultSetIterator.class);

    /** Iterated ResultSet */
    private ResultSet resultSet;
    /** Count of ResultSet row */
    private int rowCount = 0;

    /**
     * Main constructor.
     *
     * @param resultSet ResultSet to iterate.
     */
    public ResultSetIterator(ResultSet resultSet){
        this.resultSet = resultSet;
        try {
            this.resultSet.last();
        } catch (SQLException e) {
            LOGGER.error("Unable to go to the last ResultSet row.\n" + e.getLocalizedMessage());
            return;
        }
        try {
            rowCount = resultSet.getRow();
        } catch (SQLException e) {
            LOGGER.error("Unable to get ResultSet row.\n" + e.getLocalizedMessage());
            return;
        }
        try {
            this.resultSet.beforeFirst();
        } catch (SQLException e) {
            LOGGER.error("Unable to go before the first ResultSet row.\n" + e.getLocalizedMessage());
        }
    }

    @Override
    public boolean hasNext() {
        int row = 0;
        try {
            row = resultSet.getRow();
        } catch (SQLException e) {
            LOGGER.error("Unable to get ResultSet row.\n" + e.getLocalizedMessage());
        }
        return row < rowCount;
    }

    @Override
    public Object next() {
        try {
            if(!resultSet.next()){
                LOGGER.error("Unable to move to the next row.");
            }
        } catch (SQLException e) {
            LOGGER.error("Unable to get next row.\n" + e.getLocalizedMessage());
        }
        return resultSet;
    }
    
    
}
