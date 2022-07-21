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
 * Copyright (C) 2019-2020 CNRS (Lab-STICC UMR CNRS 6285)
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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.orbisgis.commons.printer.ICustomPrinter.CellPosition.*;

/**
 * Test class dedicated to the {@link Ascii} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (Lab-STICC UBS 2019-2020)
 */
class AsciiTest {

    /**
     * Test the building of a {@link Ascii} printer.
     */
    @Test
    void buildTest() {
        assertThrows(IllegalArgumentException.class,
                () -> new Ascii(null));

        StringBuilder sb = new StringBuilder();
        assertEquals("", sb.toString());

        Ascii ascii = new Ascii(sb);
        assertEquals("", ascii.toString());

        sb.append("##HEADER##");
        assertEquals("##HEADER##", sb.toString());

        ascii = new Ascii(sb);
        assertEquals("##HEADER##\n", ascii.toString());
    }

    /**
     * Test the {@link Ascii#startTable(int, int)} and {@link Ascii#endTable()} methods
     */
    @Test
    void startEndTableTest() {
        Ascii ascii = new Ascii();
        ascii.appendTableLineSeparator();
        assertEquals("", ascii.toString());

        ascii.startTable(3, 1);
        assertEquals("", ascii.toString());
        ascii.appendTableLineSeparator();
        assertEquals("+---+\n", ascii.toString());

        ascii.endTable();
        assertEquals("+---+\n", ascii.toString());
        ascii.appendTableLineSeparator();
        assertEquals("+---+\n", ascii.toString());
    }

    /**
     * Test the {@link Ascii#appendTableLineSeparator()} method.
     */
    @Test
    void appendTableLineSeparatorTest() {
        Ascii ascii = new Ascii();
        ascii.appendTableLineSeparator();
        assertEquals("", ascii.toString());

        ascii.startTable(2, 2);
        ascii.appendTableLineSeparator();
        assertEquals("+--+--+\n", ascii.toString());
    }

    /**
     * Test the {@link Ascii#appendTableValue(Object, ICustomPrinter.CellPosition)} and
     * {@link Ascii#appendTableValue(Object)} methods.
     */
    @Test
    void appendTableValueTest() {
        Ascii ascii = new Ascii();

        assertThrows(IllegalArgumentException.class,
                () -> ascii.appendTableValue("null", null));
        assertThrows(IllegalArgumentException.class,
                () -> ascii.appendTableValue(null, CENTER));
        assertThrows(IllegalArgumentException.class,
                () -> ascii.appendTableValue(null));

        ascii.appendTableValue("center", CENTER);
        ascii.appendTableValue("left", LEFT);
        ascii.appendTableValue("right", RIGHT);
        ascii.appendTableValue("default");
        assertEquals("", ascii.toString());

        ascii.startTable(10, 4);
        assertEquals("", ascii.toString());

        ascii.appendTableValue("center", CENTER);
        ascii.appendTableValue("left", LEFT);
        ascii.appendTableValue("right", RIGHT);
        assertEquals("|  center  |left      |     right", ascii.toString());

        ascii.appendTableValue("end too long", LEFT);
        assertEquals("|  center  |left      |     right|end too...|\n", ascii.toString());

        ascii.appendTableValue("center");
        ascii.appendTableValue("left");
        ascii.appendTableValue("right");
        ascii.appendTableValue("end too long");
        assertEquals("|  center  |left      |     right|end too...|\n" +
                "|center    |left      |right     |end too...|\n", ascii.toString());
    }

    /**
     * Test the {@link Ascii#appendTableHeaderValue(Object, ICustomPrinter.CellPosition)} method.
     */
    @Test
    void appendTableHeaderValueTest() {
        Ascii ascii = new Ascii();

        assertThrows(IllegalArgumentException.class,
                () -> ascii.appendTableHeaderValue("null", null));
        assertThrows(IllegalArgumentException.class,
                () -> ascii.appendTableHeaderValue(null, CENTER));

        ascii.appendTableHeaderValue("center", CENTER);
        ascii.appendTableHeaderValue("left", LEFT);
        ascii.appendTableHeaderValue("right", RIGHT);
        assertEquals("", ascii.toString());

        ascii.startTable(10, 4);
        ascii.appendTableHeaderValue("center", CENTER);
        ascii.appendTableHeaderValue("left", LEFT);
        ascii.appendTableHeaderValue("right", RIGHT);
        assertEquals("|  center  |left      |     right", ascii.toString());

        ascii.appendTableHeaderValue("left", LEFT);
        assertEquals("|  center  |left      |     right|left      |\n", ascii.toString());
    }

    /**
     * Test the {@link Ascii#appendTableTitle(Object)} method.
     */
    @Test
    void appendTableTitleTest() {
        Ascii ascii = new Ascii();

        assertThrows(IllegalArgumentException.class,
                () -> ascii.appendTableTitle(null));

        ascii.appendTableTitle("title");
        assertEquals("", ascii.toString());

        ascii.startTable(10, 4);
        ascii.appendTableTitle("title");
        assertEquals("+----------+\n" +
                "|  title   |\n", ascii.toString());

        ascii.appendTableTitle("too long title");
        assertEquals("+----------+\n" +
                "|  title   |\n" +
                "+----------+\n" +
                "|too lon...|\n", ascii.toString());
    }

    /**
     * Test the {@link Ascii#appendValue(Object)} method.
     */
    @Test
    void appendValueTest() {
        Ascii ascii = new Ascii();

        assertThrows(IllegalArgumentException.class,
                () -> ascii.appendValue(null));

        ascii.appendValue("value");
        assertEquals("value\n", ascii.toString());

        ascii.startTable(10, 1);
        ascii.appendValue("value");
        assertEquals("value\n|value     |\n", ascii.toString());
    }
}
