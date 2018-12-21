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
 * DataManager is distributed under GPL 3 license.
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
package org.orbisgis.datamanager;

import groovy.lang.Closure;
import org.junit.jupiter.api.Test;
import org.orbisgis.datamanager.h2gis.H2GIS;
import org.orbisgis.datamanagerapi.dataset.IJdbcSpatialTable;
import org.orbisgis.datamanagerapi.dataset.ISpatialTable;
import org.osgi.service.jdbc.DataSourceFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


/**
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2018)
 */
public class H2GISTests {
    
    
    @Test
    public void loadH2GIS() {
        Map<String, String> map = new HashMap<>();
        map.put(DataSourceFactory.JDBC_DATABASE_NAME, "../../target/loadH2GIS");
        H2GIS h2GIS = H2GIS.open(map);
        assertNotNull(h2GIS);
    }


    @Test
    public void queryH2GIS() throws SQLException {
        Map<String, String> map = new HashMap<>();
        map.put(DataSourceFactory.JDBC_DATABASE_NAME, "../../target/loadH2GIS");
        H2GIS h2GIS = H2GIS.open(map);
        h2GIS.execute("DROP TABLE IF EXISTS h2gis; CREATE TABLE h2gis (id int, the_geom point);insert into h2gis values (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);");
        ArrayList<String> values = new ArrayList<>();
        h2GIS.eachRow("SELECT THE_GEOM FROM h2gis", new Closure(null){
            @Override
            public Object call(Object argument) {
                values.add(argument.toString());
                return argument;
            }
        });
        assertEquals(2, values.size());
        assertTrue(values.contains("[THE_GEOM:POINT (10 10)]"));
        assertTrue(values.contains("[THE_GEOM:POINT (1 1)]"));
    }

    @Test
    public void querySpatialTable() throws SQLException {
        Map<String, String> map = new HashMap<>();
        map.put(DataSourceFactory.JDBC_DATABASE_NAME, "../../target/loadH2GIS");
        H2GIS h2GIS = H2GIS.open(map);
        h2GIS.execute("DROP TABLE IF EXISTS h2gis; CREATE TABLE h2gis (id int, the_geom point);insert into h2gis values (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);");

        ArrayList<String> values = new ArrayList<>();
        h2GIS.getSpatialTable("h2gis").eachRow(new Closure(null){
            @Override
            public Object call(Object argument) {
                values.add(((IJdbcSpatialTable)argument).getGeometry().toString());
                return argument;
            }
        });
        assertEquals(2,values.size());
        assertEquals("POINT (10 10)", values.get(0));
        assertEquals("POINT (1 1)", values.get(1));
    }

    @Test
    public void queryTableNames() throws SQLException {
        Map<String, String> map = new HashMap<>();
        map.put(DataSourceFactory.JDBC_DATABASE_NAME, "../../target/loadH2GIS");
        H2GIS h2GIS = H2GIS.open(map);
        h2GIS.execute("DROP TABLE IF EXISTS table1, table2; " +
                "CREATE TABLE table1 (id int, the_geom point);" +
                "CREATE TABLE table2 (id int, the_geom point);");

        Collection<String> values = h2GIS.getTableNames();
        assertTrue(values.contains("LOADH2GIS.PUBLIC.TABLE1"));
        assertTrue(values.contains("LOADH2GIS.PUBLIC.TABLE2"));
    }

    @Test
    public void updateSpatialTable() throws SQLException {
        Map<String, String> map = new HashMap<>();
        map.put(DataSourceFactory.JDBC_DATABASE_NAME, "./target/loadH2GIS");
        H2GIS h2GIS = H2GIS.open(map);
        h2GIS.execute("DROP TABLE IF EXISTS h2gis; CREATE TABLE h2gis (id int PRIMARY KEY, code int, the_geom point);insert into h2gis values (1,22, 'POINT(10 10)'::GEOMETRY), (2,56, 'POINT(1 1)'::GEOMETRY);");

        h2GIS.getSpatialTable("h2gis").eachRow(new Closure(null){
            @Override
            public Object call(Object argument) {
                ISpatialTable sp = ((ISpatialTable) argument);
                try {
                    sp.updateInt(2, 3);
                    sp.updateRow();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                return argument;
            }
        });

        ArrayList<Integer> values = new ArrayList<>();
        h2GIS.getSpatialTable("h2gis").eachRow(new Closure(null){
            @Override
            public Object call(Object argument) {
                try {
                    values.add(((ISpatialTable)argument).getInt(2));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return argument;
            }
        });
        assertEquals(2,values.size());
        assertEquals(3, (int) values.get(0));
        assertEquals(3, (int) values.get(1));
    }
}
