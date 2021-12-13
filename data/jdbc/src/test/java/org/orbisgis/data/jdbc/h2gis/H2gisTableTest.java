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
package org.orbisgis.data.jdbc.h2gis;


import org.junit.jupiter.api.Test;
import org.orbisgis.data.api.dataset.ISpatialTable;
import org.orbisgis.data.api.dataset.ITable;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class dedicated to the {@link H2gisTable} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public class H2gisTableTest {

    /**
     * Test the {@link H2gisSpatialTable#asType(Class)} method.
     */
    @Test
    public void testAsType() {
        H2GIS h2gis = H2GIS.open("./target/test");
        try {
            h2gis.execute("DROP TABLE IF EXISTS NAME; CREATE TABLE name");
        } catch (SQLException e) {
            fail(e);
        }
        ITable table = h2gis.getTable("name");
        assertTrue(table.asType(ISpatialTable.class) instanceof ISpatialTable);
        assertTrue(table.asType(ITable.class) instanceof ITable);
        assertTrue(table.asType(H2gisSpatialTable.class) instanceof H2gisSpatialTable);
        assertTrue(table.asType(H2gisTable.class) instanceof H2gisTable);
        assertNull(table.asType(String.class));
    }
}
