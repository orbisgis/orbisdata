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
 * Copyright (C) 2018-2020 CNRS (Lab-STICC UMR CNRS 6285)
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


import static org.orbisgis.commons.utilities.CheckUtils.checkNotNull;

/**
 * Root implementation of {@link ICustomPrinter} for the custom printers.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019-2020)
 */
public abstract class CustomPrinter implements ICustomPrinter {

    /**
     * Not null {@link StringBuilder} used for the string building
     */
    protected StringBuilder builder;
    /**
     * Width in character number of a single column
     */
    protected int columnWidth;
    /**
     * Count of column
     */
    protected int columnCount;
    /**
     * True of a table is currently drawn, false otherwise
     */
    protected boolean isDrawingTable;
    /**
     * Current column index
     */
    protected int columnIndex;

    /**
     * Main constructor.
     *
     * @param builder {@link StringBuilder} used for the string building.
     */
    protected CustomPrinter(StringBuilder builder) {
        checkNotNull(builder, "The builder should not be null.");
        this.builder = builder;
    }

    @Override
    public String toString() {
        return builder.toString();
    }

    @Override
    public void startTable(int columnWidth, int columnCount) {
        this.columnCount = columnCount;
        this.columnWidth = columnWidth;
        this.columnIndex = 0;
        this.isDrawingTable = true;
    }

    @Override
    public void endTable() {
        this.columnCount = -1;
        this.columnWidth = -1;
        this.columnIndex = -1;
        this.isDrawingTable = false;
    }

    @Override
    public void appendTableValue(Object value) {
        checkNotNull(value, "The value to append should not be null.");
        appendTableValue(value, CellPosition.LEFT);
    }

    @Override
    public void appendText(String text) {
        if (isDrawingTable) {
            builder.append(text);
        }
    }
}
