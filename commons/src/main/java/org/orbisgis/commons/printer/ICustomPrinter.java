/*
 * Bundle Commons is part of the OrbisGIS platform
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
 * Commons is distributed under LGPL 3 license.
 *
 * Copyright (C) 2018 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * Commons is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Commons is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Commons. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.commons.printer;

/**
 * Interface used for the customisation of the printing of Java Objects.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public interface ICustomPrinter {

    /** Cell positions.*/
    enum CellPosition {CENTER, RIGHT, LEFT}

    /**
     * Start the drawing of a table.
     *
     * @param columnWidth Width in character number of a single column.
     * @param columnCount Count of column.
     */
    void startTable(int columnWidth, int columnCount);

    /**
     * End the table drawing.
     */
    void endTable();

    /**
     * Append a line separator to the builder.
     */
    void appendTableLineSeparator();

    /**
     * Add a single value to the table. Linebreak are automatically generated once the column count is reached.
     *
     * @param value Value to add to the table.
     */
    void appendTableValue(Object value);

    /**
     * Add a single value to the table. Linebreak are automatically generated once the column count is reached.
     *
     * @param value Value to add to the table.
     */
    void appendTableValue(Object value, CellPosition position);

    /**
     * Add a header value to the table. Linebreak are automatically generated once the column count is reached.
     *
     * @param value Header value to add to the table.
     */
    void appendTableHeaderValue(Object value, CellPosition position);

    /**
     * Add a title to the table.
     *
     * @param title Title to add to the table.
     */
    void appendTableTitle(Object title);
}
