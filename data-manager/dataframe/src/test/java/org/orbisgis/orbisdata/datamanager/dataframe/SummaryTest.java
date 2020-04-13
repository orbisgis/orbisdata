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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.orbisgis.commons.printer.Ascii;
import org.orbisgis.commons.printer.Html;
import org.orbisgis.orbisdata.datamanager.jdbc.h2gis.H2GIS;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.orbisgis.orbisdata.datamanager.dataframe.TestUtils.RANDOM_DS;

/**
 * Test class for {@link Summary}.
 *
 * @author Sylvain PALOMINOS (UBS LAB-STICC 2020)
 */
public class SummaryTest {

    /**
     * {@link Summary} instance for test purpose.
     */
    public static Summary summary;

    @BeforeAll
    public static void beforeAll() throws SQLException {
        H2GIS h2gis = RANDOM_DS();
        h2gis.execute("CREATE TABLE toto(col1 int, col2 varchar, col3 boolean, col4 char, col5 TINYINT, col6 SMALLINT, col7 INT8, col8 REAL, col9 double)");
        h2gis.execute("INSERT INTO toto VALUES (0, 'val0', true , 0, 0, 0, 0, 0.0, 0.0)");
        h2gis.execute("INSERT INTO toto VALUES (1, 'val1', false, 1, 1, 1, 1, 1.0, 1.0)");
        h2gis.execute("INSERT INTO toto VALUES (2, 'val2', true , 2, 2, 2, 2, 2.0, 2.0)");
        h2gis.execute("INSERT INTO toto VALUES (3, 'val3', false, 3, 3, 3, 3, 3.0, 3.0)");
        h2gis.execute("INSERT INTO toto VALUES (4, 'val4', true , 4, 4, 4, 4, 4.0, 4.0)");
        summary = DataFrame.of(h2gis.getTable("toto")).summary();
    }

    @Test
    public void asTypeTest(){
        String str = "+------+-----+---+---+---+\n" +
                "|column|count|min|avg|max|\n" +
                "+------+-----+---+---+---+\n" +
                "|  COL1|    5|  0|  2|  4|\n" +
                "|  COL5|    5|  0|  2|  4|\n" +
                "|  COL6|    5|  0|  2|  4|\n" +
                "|  COL7|    5|  0|  2|  4|\n" +
                "|  COL8|    5|  0|  2|  4|\n" +
                "|  COL9|    5|  0|  2|  4|\n" +
                "+------+-----+---+---+---+\n";
        assertEquals(str, summary.asType(String.class).toString());
        String ascii = "+---------+\n" +
                "|DataFrame|\n" +
                "+---------+---------+---------+---------+---------+\n" +
                "| column  |  count  |   min   |   avg   |   max   |\n" +
                "+---------+---------+---------+---------+---------+\n" +
                "|     COL1|        5|      0.0|      2.0|      4.0|\n" +
                "+---------+---------+---------+---------+---------+\n" +
                "|     COL5|        5|      0.0|      2.0|      4.0|\n" +
                "+---------+---------+---------+---------+---------+\n" +
                "|     COL6|        5|      0.0|      2.0|      4.0|\n" +
                "+---------+---------+---------+---------+---------+\n" +
                "|     COL7|        5|      0.0|      2.0|      4.0|\n" +
                "+---------+---------+---------+---------+---------+\n" +
                "|     COL8|        5|      0.0|      2.0|      4.0|\n" +
                "+---------+---------+---------+---------+---------+\n" +
                "|     COL9|        5|      0.0|      2.0|      4.0|\n" +
                "+---------+---------+---------+---------+---------+\n";
        assertEquals(ascii, summary.asType(Ascii.class).toString());
        String html = "<table>\n" +
                "<caption>DataFrame</caption>\n" +
                "<tr></tr>\n" +
                "<tr>\n" +
                "<th align=\"CENTER\">column</th>\n" +
                "<th align=\"CENTER\">count</th>\n" +
                "<th align=\"CENTER\">min</th>\n" +
                "<th align=\"CENTER\">avg</th>\n" +
                "<th align=\"CENTER\">max</th>\n" +
                "</tr>\n" +
                "<tr></tr>\n" +
                "<tr>\n" +
                "<td align=\"RIGHT\">COL1</td>\n" +
                "<td align=\"RIGHT\">5</td>\n" +
                "<td align=\"RIGHT\">0.0</td>\n" +
                "<td align=\"RIGHT\">2.0</td>\n" +
                "<td align=\"RIGHT\">4.0</td>\n" +
                "</tr>\n" +
                "<tr></tr>\n" +
                "<tr>\n" +
                "<td align=\"RIGHT\">COL5</td>\n" +
                "<td align=\"RIGHT\">5</td>\n" +
                "<td align=\"RIGHT\">0.0</td>\n" +
                "<td align=\"RIGHT\">2.0</td>\n" +
                "<td align=\"RIGHT\">4.0</td>\n" +
                "</tr>\n" +
                "<tr></tr>\n" +
                "<tr>\n" +
                "<td align=\"RIGHT\">COL6</td>\n" +
                "<td align=\"RIGHT\">5</td>\n" +
                "<td align=\"RIGHT\">0.0</td>\n" +
                "<td align=\"RIGHT\">2.0</td>\n" +
                "<td align=\"RIGHT\">4.0</td>\n" +
                "</tr>\n" +
                "<tr></tr>\n" +
                "<tr>\n" +
                "<td align=\"RIGHT\">COL7</td>\n" +
                "<td align=\"RIGHT\">5</td>\n" +
                "<td align=\"RIGHT\">0.0</td>\n" +
                "<td align=\"RIGHT\">2.0</td>\n" +
                "<td align=\"RIGHT\">4.0</td>\n" +
                "</tr>\n" +
                "<tr></tr>\n" +
                "<tr>\n" +
                "<td align=\"RIGHT\">COL8</td>\n" +
                "<td align=\"RIGHT\">5</td>\n" +
                "<td align=\"RIGHT\">0.0</td>\n" +
                "<td align=\"RIGHT\">2.0</td>\n" +
                "<td align=\"RIGHT\">4.0</td>\n" +
                "</tr>\n" +
                "<tr></tr>\n" +
                "<tr>\n" +
                "<td align=\"RIGHT\">COL9</td>\n" +
                "<td align=\"RIGHT\">5</td>\n" +
                "<td align=\"RIGHT\">0.0</td>\n" +
                "<td align=\"RIGHT\">2.0</td>\n" +
                "<td align=\"RIGHT\">4.0</td>\n" +
                "</tr>\n" +
                "<tr></tr>\n" +
                "</table>\n";
        assertEquals(html, summary.asType(Html.class).toString());
    }
}
