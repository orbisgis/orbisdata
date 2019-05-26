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
 * Commons is distributed under GPL 3 license.
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
 * Class for the printing of data in an Ascii style.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public class Ascii extends ICustomPrinter.CustomPrinter {

    public enum CellPosition {CENTER, RIGHT, LEFT}

    /** Width in character number of a single column. */
    private int columnWidth;
    /** Count of column. */
    private int columnCount;
    /** True of a table is currently drawn, false otherwise. */
    private boolean isDrawingTable;
    /** Current column index. */
    private int columnIndex;

    /**
     * Main constructor.
     *
     * @param builder StringBuilder used for building the string
     */
    public Ascii(StringBuilder builder) {
        super(builder);
    }

    /**
     * Start the drawing of a table.
     * @param columnWidth Width in character number of a single column.
     * @param columnCount Count of column.
     */
    public void startTable(int columnWidth, int columnCount){
        this.columnCount = columnCount;
        this.columnWidth = columnWidth;
        this.columnIndex = 0;
        this.isDrawingTable = true;
    }

    public void endTable(){
        this.columnCount = -1;
        this.columnWidth = -1;
        this.columnIndex = -1;
        this.isDrawingTable = false;
    }

    /**
     * Append a line separator to the builder.
     */
    public void appendTableLineSeparator(){
        if(isDrawingTable) {
            builder.append("+");
            for (int i = 0; i < columnCount; i++) {
                for (int j = 0; j < columnWidth - 1; j++) {
                    builder.append("-");
                }
                builder.append("+");
            }
            builder.append("\n");
        }
    }

    /**
     * Add a single value to the table. Linebreak are automatically generated once the column count is reached.
     *
     * @param value Value to add to the table.
     */
    public void appendTableValue(Object value){
        appendTableValue(value, CellPosition.LEFT);
    }

    /**
     * Add a single value to the table. Linebreak are automatically generated once the column count is reached.
     *
     * @param value Value to add to the table.
     */
    public void appendTableValue(Object value, CellPosition position){
        if(isDrawingTable){
            builder.append("|");
            String cut = value == null ? "null" : value.toString();
            if (cut.length() > columnWidth - 1) {
                cut = cut.substring(0, columnWidth - 4) + "...";
            }
            switch(position){
                case LEFT:
                    builder.append(cut);
                    for (int i = 0; i < (columnWidth - 1 - cut.length()); i++) {
                        builder.append(" ");
                    }
                    break;
                case RIGHT:
                    for (int i = 0; i < (columnWidth - 1 - cut.length()); i++) {
                        builder.append(" ");
                    }
                    builder.append(cut);
                    break;
                case CENTER:
                    for (int i = 0; i < (columnWidth - 1 - cut.length())/2; i++) {
                        builder.append(" ");
                    }
                    builder.append(cut);
                    for (int i = 0; i < (columnWidth - 1 - cut.length()) - (columnWidth - 1 - cut.length())/2; i++) {
                        builder.append(" ");
                    }
            }
            columnIndex ++;
            if(columnIndex == columnCount){
                columnIndex = 0;
                builder.append("|");
                builder.append("\n");
            }
        }
    }
}
