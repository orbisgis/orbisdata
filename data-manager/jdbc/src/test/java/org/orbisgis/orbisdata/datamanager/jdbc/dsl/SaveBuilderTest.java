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
package org.orbisgis.orbisdata.datamanager.jdbc.dsl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.orbisgis.orbisdata.datamanager.jdbc.dsl.sql.BuilderResult;
import org.orbisgis.orbisdata.datamanager.jdbc.h2gis.H2GIS;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class dedicated to the {@link BuilderResult} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (Lab-STICC UBS, Chaire GEOTERA, 2020)
 */
public class SaveBuilderTest {

    private static H2GIS h2gis;

    @BeforeAll
    public static void beforeAll() throws SQLException {
        h2gis = H2GIS.open("./target/" + SaveBuilderTest.class.getSimpleName() + "_" + UUID.randomUUID().toString());
        h2gis.execute("DROP TABLE IF EXISTS data");
        h2gis.execute("CREATE TABLE data(id int, the_geom GEOMETRY, text varchar)");
        h2gis.execute("INSERT INTO data VALUES (1, 'POINT(0 0)', 'toto')");
    }

    @Test
    void test() {
        SaveBuilder builder = new SaveBuilder(h2gis.getConnection(), h2gis.getSpatialTable("data"));
        assertTrue(builder.folder("./target").name("toto").encoding("UTF8").save("json"));
        assertTrue(builder.delete().utf8().save("csv"));
    }
}
