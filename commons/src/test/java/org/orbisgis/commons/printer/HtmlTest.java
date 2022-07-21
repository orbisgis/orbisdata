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
import org.orbisgis.commons.printer.Html;
import org.orbisgis.commons.printer.ICustomPrinter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.orbisgis.commons.printer.ICustomPrinter.CellPosition.*;

/**
 * Test class dedicated to the {@link Html} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (Lab-STICC UBS 2020)
 */
class HtmlTest {

    /**
     * Test the building of a {@link Html} printer.
     */
    @Test
    void buildTest() {
        assertThrows(IllegalArgumentException.class,
                () -> new Html(null));

        StringBuilder sb = new StringBuilder();
        assertEquals("", sb.toString());

        Html html = new Html(sb);
        assertEquals("", html.toString());

        sb.append("##HEADER##");
        assertEquals("##HEADER##", sb.toString());

        html = new Html(sb);
        assertEquals("##HEADER##", html.toString());
    }

    /**
     * Test the {@link Html#startTable(int, int)} and {@link Html#endTable()} methods
     */
    @Test
    void startEndTableTest() {
        Html html = new Html();
        html.appendTableLineSeparator();
        assertEquals("", html.toString());

        html.startTable(3, 1);
        assertEquals("<table>\n", html.toString());
        html.appendTableLineSeparator();
        assertEquals("<table>\n" +
                "<tr></tr>\n", html.toString());

        html.endTable();
        assertEquals("<table>\n" +
                "<tr></tr>\n" +
                "</table>\n", html.toString());
        html.appendTableLineSeparator();
        assertEquals("<table>\n" +
                "<tr></tr>\n" +
                "</table>\n", html.toString());
    }

    /**
     * Test the {@link Html#appendTableLineSeparator()} method.
     */
    @Test
    void appendTableLineSeparatorTest() {
        Html html = new Html();
        html.appendTableLineSeparator();
        assertEquals("", html.toString());
        html.startTable(2, 2);
        html.appendTableLineSeparator();
        assertEquals("<table>\n" +
                "<tr></tr>\n", html.toString());
    }

    /**
     * Test the {@link Html#appendTableValue(Object, ICustomPrinter.CellPosition)} and
     * {@link Html#appendTableValue(Object)} methods.
     */
    @Test
    void appendTableValueTest() {
        Html html = new Html();

        assertThrows(IllegalArgumentException.class,
                () -> html.appendTableValue("null", null));
        assertThrows(IllegalArgumentException.class,
                () -> html.appendTableValue(null, CENTER));
        assertThrows(IllegalArgumentException.class,
                () -> html.appendTableValue(null));

        html.appendTableValue("center", CENTER);
        html.appendTableValue("left", LEFT);
        html.appendTableValue("right", RIGHT);
        html.appendTableValue("default");
        assertEquals("", html.toString());

        html.startTable(10, 4);
        assertEquals("<table>\n", html.toString());

        html.appendTableValue("center", CENTER);
        html.appendTableValue("left", LEFT);
        html.appendTableValue("right", RIGHT);
        assertEquals("<table>\n" +
                "<tr>\n" +
                "<td align=\"CENTER\">center</td>\n" +
                "<td align=\"LEFT\">left</td>\n" +
                "<td align=\"RIGHT\">right</td>\n", html.toString());

        html.appendTableValue("end too long", LEFT);
        assertEquals("<table>\n" +
                "<tr>\n" +
                "<td align=\"CENTER\">center</td>\n" +
                "<td align=\"LEFT\">left</td>\n" +
                "<td align=\"RIGHT\">right</td>\n" +
                "<td align=\"LEFT\">end too...</td>\n" +
                "</tr>\n", html.toString());

        html.appendTableValue("center");
        html.appendTableValue("left");
        html.appendTableValue("right");
        html.appendTableValue("end too long");
        assertEquals("<table>\n" +
                "<tr>\n" +
                "<td align=\"CENTER\">center</td>\n" +
                "<td align=\"LEFT\">left</td>\n" +
                "<td align=\"RIGHT\">right</td>\n" +
                "<td align=\"LEFT\">end too...</td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "<td align=\"LEFT\">center</td>\n" +
                "<td align=\"LEFT\">left</td>\n" +
                "<td align=\"LEFT\">right</td>\n" +
                "<td align=\"LEFT\">end too...</td>\n" +
                "</tr>\n", html.toString());
    }

    /**
     * Test the {@link Html#appendTableHeaderValue(Object, ICustomPrinter.CellPosition)} method.
     */
    @Test
    void appendTableHeaderValueTest() {
        Html html = new Html();

        assertThrows(IllegalArgumentException.class,
                () -> html.appendTableHeaderValue("null", null));
        assertThrows(IllegalArgumentException.class,
                () -> html.appendTableHeaderValue(null, CENTER));

        html.appendTableHeaderValue("center", CENTER);
        html.appendTableHeaderValue("left", LEFT);
        html.appendTableHeaderValue("right", RIGHT);
        assertEquals("", html.toString());

        html.startTable(10, 4);
        html.appendTableHeaderValue("center", CENTER);
        html.appendTableHeaderValue("left", LEFT);
        html.appendTableHeaderValue("right", RIGHT);
        assertEquals("<table>\n" +
                "<tr>\n" +
                "<th align=\"CENTER\">center</th>\n" +
                "<th align=\"LEFT\">left</th>\n" +
                "<th align=\"RIGHT\">right</th>\n", html.toString());

        html.appendTableHeaderValue("end too long", LEFT);
        assertEquals("<table>\n" +
                "<tr>\n" +
                "<th align=\"CENTER\">center</th>\n" +
                "<th align=\"LEFT\">left</th>\n" +
                "<th align=\"RIGHT\">right</th>\n" +
                "<th align=\"LEFT\">end too...</th>\n" +
                "</tr>\n", html.toString());
    }

    /**
     * Test the {@link Html#appendTableTitle(Object)} method.
     */
    @Test
    void appendTableTitleTest() {
        Html html = new Html();

        assertThrows(IllegalArgumentException.class,
                () -> html.appendTableTitle(null));

        html.appendTableTitle("title");
        assertEquals("", html.toString());

        html.startTable(10, 4);
        html.appendTableTitle("title");
        assertEquals("<table>\n" +
                "<caption>title</caption>\n", html.toString());

        html.appendTableTitle("too long title");
        assertEquals("<table>\n" +
                "<caption>title</caption>\n" +
                "<caption>too lon...</caption>\n", html.toString());
    }

    /**
     * Test the {@link Html#appendValue(Object)} method.
     */
    @Test
    void appendValueTest() {
        Html html = new Html();

        assertThrows(IllegalArgumentException.class,
                () -> html.appendValue(null));

        html.appendValue("value");
        assertEquals("<p>value</p>\n", html.toString());

        html.startTable(10, 1);
        html.appendValue("value");
        assertEquals("<p>value</p>\n<table>\n<tr>\n<td align=\"LEFT\">value</td>\n</tr>\n", html.toString());
    }
}
