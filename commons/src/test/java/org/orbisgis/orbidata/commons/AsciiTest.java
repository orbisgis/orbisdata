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
 * Copyright (C) 2019 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.orbidata.commons;

import org.junit.jupiter.api.Test;
import org.orbisgis.orbisdata.commons.printer.Ascii;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.orbisgis.orbisdata.commons.printer.ICustomPrinter.CellPosition.*;

/**
 * Test class dedicated to the {@link Ascii} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (Lab-STICC UBS 2019)
 */
class AsciiTest {

    /**
     * Test the building of a {@link Ascii} printer.
     */
    @Test
    void asciiTest() {
        StringBuilder sb = new StringBuilder();
        sb.append("##HEADER##");
        assertEquals("##HEADER##", sb.toString());

        Ascii ascii = new Ascii(sb);
        assertEquals("##HEADER##\n", ascii.toString());

        ascii.startTable(4, 3);
        assertEquals("##HEADER##\n", ascii.toString());

        ascii.endTable();
        assertEquals("##HEADER##\n", ascii.toString());

        ascii.startTable(4, 3);
        assertEquals("##HEADER##\n", ascii.toString());

        ascii.appendTableLineSeparator();
        assertEquals("##HEADER##\n" +
                "+----+----+----+\n", ascii.toString());

        ascii.startTable(5, 2);
        ascii.appendTableLineSeparator();
        assertEquals("##HEADER##\n" +
                "+----+----+----+\n" +
                "+-----+-----+\n", ascii.toString());

        ascii.appendTableTitle("title");
        assertEquals("##HEADER##\n" +
                "+----+----+----+\n" +
                "+-----+-----+\n" +
                "+-----+\n" +
                "|title|\n", ascii.toString());

        ascii.appendTableTitle("title too long");
        assertEquals("##HEADER##\n" +
                "+----+----+----+\n" +
                "+-----+-----+\n" +
                "+-----+\n" +
                "|title|\n" +
                "+-----+\n" +
                "|ti...|\n", ascii.toString());

        ascii.startTable(5, 3);
        ascii.appendTableHeaderValue("#", RIGHT);
        ascii.appendTableHeaderValue("#", LEFT);
        ascii.appendTableHeaderValue("#", CENTER);
        assertEquals("##HEADER##\n" +
                "+----+----+----+\n" +
                "+-----+-----+\n" +
                "+-----+\n" +
                "|title|\n" +
                "+-----+\n" +
                "|ti...|\n" +
                "|    #|#    |  #  |\n", ascii.toString());

        ascii.startTable(5, 3);
        ascii.appendTableValue("#", RIGHT);
        ascii.appendTableValue("#", LEFT);
        ascii.appendTableValue("#", CENTER);
        ascii.appendTableValue("#");
        ascii.appendTableValue("#");
        ascii.appendTableValue("#");
        assertEquals("##HEADER##\n" +
                "+----+----+----+\n" +
                "+-----+-----+\n" +
                "+-----+\n" +
                "|title|\n" +
                "+-----+\n" +
                "|ti...|\n" +
                "|    #|#    |  #  |\n" +
                "|    #|#    |  #  |\n" +
                "|#    |#    |#    |\n", ascii.toString());
    }
}
