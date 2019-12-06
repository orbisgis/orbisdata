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
package org.orbisgis.orbisdata.datamanager.dataframe


import org.junit.jupiter.api.Test
import org.orbisgis.orbisdata.datamanager.dataframe.DataFrame
import org.orbisgis.orbisdata.datamanager.jdbc.h2gis.H2GIS

import static org.junit.jupiter.api.Assertions.*


class GroovyDataFrameTest {


    @Test
    void createDataFrameFromSpatialTable() {
        def h2GIS = H2GIS.open([databaseName: './target/dataframe'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis;
                CREATE TABLE h2gis (id int, the_geom geometry(point));
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        DataFrame df = DataFrame.of(h2GIS.getTable("H2GIS"))
        assertNotNull(df);
        assertNotNull(df.schema());
        assertEquals(2, df.schema().length());
        assertEquals(2, df.ncols());
        assertEquals(0, df.columnIndex("ID"));
        assertEquals(1, df.columnIndex("THE_GEOM"));
        assertEquals(2, df.intVector(0).size());
        assertEquals(2, df.stringVector(1).size());
    }

    @Test
    void createDataFrameFromSpatialTable1() {
        def h2GIS = H2GIS.open([databaseName: './target/dataframe'])
        h2GIS.execute("""
                DROP TABLE IF EXISTS h2gis;
                CREATE TABLE h2gis (id int, the_geom geometry(GEOMETRY));
                INSERT INTO h2gis VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        DataFrame df = DataFrame.of(h2GIS.getTable("H2GIS"))
        assertNotNull(df);
        assertNotNull(df.schema());
        assertEquals(2, df.schema().length());
        assertEquals(2, df.ncols());
        assertEquals(0, df.columnIndex("ID"));
        assertEquals(1, df.columnIndex("THE_GEOM"));
        assertEquals(2, df.intVector(0).size());
        assertEquals(2, df.stringVector(1).size());
    }


}
