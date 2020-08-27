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
package org.orbisgis.orbisdata.datamanager.dataframe;

import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.commons.printer.Ascii;
import org.orbisgis.commons.printer.Html;
import org.orbisgis.commons.printer.ICustomPrinter;

import static org.orbisgis.commons.printer.ICustomPrinter.CellPosition.CENTER;
import static org.orbisgis.commons.printer.ICustomPrinter.CellPosition.RIGHT;

/**
 * {@link ISummary} implementation for the {@link DataFrame} object.
 *
 * @author Sylvain PALOMINOS (Lab-STICC UBS 2019)
 */
public class Summary extends DataFrame implements ISummary {

    /**
     * Main constructor wrapping a {@link DataFrame}
     *
     * @param dataFrame {@link DataFrame} wrapped as summary.
     */
    public Summary(smile.data.DataFrame dataFrame) {
        setInternalDataFrame(dataFrame);
    }

    @Override
    public Object asType(@NotNull Class<?> clazz) {
        if (Ascii.class.equals(clazz) || Html.class.equals(clazz)) {
            ICustomPrinter printer;
            if (Ascii.class.equals(clazz)) {
                printer = new Ascii();
            } else {
                printer = new Html();
            }
            int maxWidth = this.getName().length();
            for (String name : this.names()) {
                maxWidth = Math.max(maxWidth, name.length());
            }
            printer.startTable(maxWidth, this.getColumnCount());
            printer.appendTableTitle(this.getName());
            printer.appendTableLineSeparator();
            String[] titles = this.names();
            for (String title : titles) {
                printer.appendTableHeaderValue(title, CENTER);
            }
            printer.appendTableLineSeparator();
            for (int i = 0; i < this.size(); i++) {
                for (int j = 0; j < this.getColumnCount(); j++) {
                    printer.appendTableValue(this.get(i, j), RIGHT);
                }
                printer.appendTableLineSeparator();
            }
            printer.endTable();
            return printer;
        } else if (String.class.equals(clazz)){
            return toString();
        }
        return null;
    }
}
