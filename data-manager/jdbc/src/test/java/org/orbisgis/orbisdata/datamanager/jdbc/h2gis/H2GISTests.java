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
package org.orbisgis.orbisdata.datamanager.jdbc.h2gis;

import groovy.lang.Closure;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.orbisgis.orbisdata.datamanager.api.dataset.IJdbcTable;
import org.orbisgis.orbisdata.datamanager.api.dataset.ISpatialTable;
import org.orbisgis.orbisdata.datamanager.api.dataset.ITable;
import org.osgi.service.jdbc.DataSourceFactory;

import java.io.File;
import java.sql.SQLException;
import java.sql.Time;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.orbisgis.orbisdata.datamanager.api.dataset.IJdbcTable.RSConcurrency.CONCUR_READ_ONLY;
import static org.orbisgis.orbisdata.datamanager.api.dataset.IJdbcTable.RSConcurrency.CONCUR_UPDATABLE;
import static org.orbisgis.orbisdata.datamanager.api.dataset.IJdbcTable.RSHoldability.CLOSE_CURSORS_AT_COMMIT;
import static org.orbisgis.orbisdata.datamanager.api.dataset.IJdbcTable.RSHoldability.HOLD_CURSORS_OVER_COMMIT;
import static org.orbisgis.orbisdata.datamanager.api.dataset.IJdbcTable.RSType.*;
import static org.orbisgis.orbisdata.datamanager.api.dsl.IOptionBuilder.Order.DESC;

/**
 * Test class dedicated to the {@link H2GIS} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2018-2019)
 */
public class H2GISTests {

    @Test
    public void openH2GIS() {
        assertNotNull(H2GIS.open("./target/openH2GIS1"));
        assertNotNull(H2GIS.open("./target/openH2GIS2", "sa", "sa"));
        assertNull(H2GIS.open(new File("file")));

        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("org.h2.Driver");
        ds.setUrl("jdbc:h2:mem:" + new File("./target/openH2GIS3").getAbsolutePath());
        ds.setUsername("sa");
        ds.setPassword("");
        assertNotNull(H2GIS.open(ds));
        assertNotNull(H2GIS.open(ds).getDataSource());
        assertFalse(H2GIS.open(ds).getTableNames().isEmpty());
    }

    @Test
    public void getTableTest() throws SQLException {
        H2GIS h2gis = H2GIS.open(
                "./target/openH2GIS2_"+ UUID.randomUUID().toString().replaceAll("-", "_"), "sa", "sa");
        h2gis.execute("DROP TABLE IF EXISTS h2gis; CREATE TABLE h2gis (id int, the_geom geometry(point), str VARCHAR);" +
                "insert into h2gis values (1, 'POINT(10 10)'::GEOMETRY, 'toto'), (2, 'POINT(1 1)'::GEOMETRY, 'tata');");

        IJdbcTable.RSType[] types = {TYPE_FORWARD_ONLY, TYPE_SCROLL_INSENSITIVE, TYPE_SCROLL_SENSITIVE};
        IJdbcTable.RSConcurrency[] concurs = {CONCUR_READ_ONLY, CONCUR_UPDATABLE};
        IJdbcTable.RSHoldability[] holds = {null, CLOSE_CURSORS_AT_COMMIT, HOLD_CURSORS_OVER_COMMIT};
        final ArrayList<String> values = new ArrayList<>();
        for(IJdbcTable.RSType type : types) {
            for (IJdbcTable.RSConcurrency concur : concurs) {
                for (IJdbcTable.RSHoldability hold : holds) {
                    values.clear();
                    h2gis.getSpatialTable("h2gis", type, concur, hold).eachRow(new Closure(null) {
                        @Override
                        public Object call(Object argument) {
                            values.add(((ISpatialTable) argument).getGeometry().toString());
                            return argument;
                        }
                    });
                    assertEquals(2, values.size());
                    assertEquals("POINT (10 10)", values.get(0));
                    assertEquals("POINT (1 1)", values.get(1));
                }
            }
        }

        for(IJdbcTable.RSType type : types) {
            for (IJdbcTable.RSConcurrency concur : concurs) {
                for (IJdbcTable.RSHoldability hold : holds) {
                    values.clear();
                    h2gis.getSpatialTable("h2gis", type, concur, hold).eachRow(new Closure(null) {
                        @Override
                        public Object call(Object argument) {
                            try {
                                values.add(((ITable)argument).getString("str").toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return argument;
                        }
                    });
                    assertEquals(2, values.size());
                    assertEquals("toto", values.get(0));
                    assertEquals("tata", values.get(1));
                }
            }
        }
    }

    @Test
    public void testColumnsType() throws SQLException {
        Map<String, String> map = new HashMap<>();
        map.put(DataSourceFactory.JDBC_DATABASE_NAME, "./target/loadH2GIS");
        H2GIS h2GIS = H2GIS.open(map);
        assertNotNull(h2GIS);
        h2GIS.execute("DROP TABLE IF EXISTS TYPES");
        h2GIS.execute("CREATE TABLE TYPES (colint INT, colreal REAL, colint2 MEDIUMINT, coltime TIME, " +
                "colvarchar VARCHAR2, colbool boolean, coltiny tinyint, colpoint GEOMETRY(POINT), colgeom GEOMETRY)");
        assertTrue(h2GIS.getTable("TYPES").hasColumn("colint", Integer.class));
        assertFalse(h2GIS.getTable("TYPES").hasColumn("colint", Short.class));
        Map<String, Class<?>> columns = new HashMap<>();
        columns.put("colint", Integer.class);
        columns.put("colreal", Float.class);
        columns.put("colint2", Integer.class);
        columns.put("coltime", Time.class);
        columns.put("colvarchar", String.class);
        columns.put("colbool", Boolean.class);
        columns.put("coltiny", Byte.class);
        columns.put("colpoint", Point.class);
        columns.put("colgeom", Geometry.class);
        assertTrue(h2GIS.getTable("TYPES").hasColumns(columns));
    }


    @Test
    public void loadH2GIS() {
        Map<String, String> map = new HashMap<>();
        map.put(DataSourceFactory.JDBC_DATABASE_NAME, "./target/loadH2GIS");
        H2GIS h2GIS = H2GIS.open(map);
        assertNotNull(h2GIS);
    }


    @Test
    public void queryH2GIS() throws SQLException {
        Map<String, String> map = new HashMap<>();
        map.put(DataSourceFactory.JDBC_DATABASE_NAME, "./target/loadH2GIS2");
        H2GIS h2GIS = H2GIS.open(map);
        h2GIS.execute("DROP TABLE IF EXISTS h2gis; CREATE TABLE h2gis (id int, the_geom geometry(point));" +
                "insert into h2gis values (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);");
        ArrayList<String> values = new ArrayList<>();
        h2GIS.eachRow("SELECT THE_GEOM FROM h2gis", new Closure(null) {
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
        map.put(DataSourceFactory.JDBC_DATABASE_NAME, "./target/loadH2GIS2");
        H2GIS h2GIS = H2GIS.open(map);
        h2GIS.execute("DROP TABLE IF EXISTS h2gis, noGeom; CREATE TABLE h2gis (id int, the_geom geometry(point));" +
                "insert into h2gis values (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);" +
                "CREATE TABLE noGeom (id int);");

        ArrayList<String> values = new ArrayList<>();
        h2GIS.getSpatialTable("h2gis").eachRow(new Closure(null) {
            @Override
            public Object call(Object argument) {
                values.add(((ISpatialTable) argument).getGeometry().toString());
                return argument;
            }
        });
        assertEquals(2, values.size());
        assertEquals("POINT (10 10)", values.get(0));
        assertEquals("POINT (1 1)", values.get(1));

        assertNull(h2GIS.getSpatialTable("noGeom"));
    }

    @Test
    public void querySpatialTableWhere() throws SQLException {
        Map<String, String> map = new HashMap<>();
        map.put(DataSourceFactory.JDBC_DATABASE_NAME, "./target/loadH2GIS2");
        H2GIS h2GIS = H2GIS.open(map);
        h2GIS.execute("DROP TABLE IF EXISTS h2gis; CREATE TABLE h2gis (id int, the_geom geometry(point));" +
                "insert into h2gis values (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);");

        ArrayList<String> values = new ArrayList<>();
        ((IJdbcTable) h2GIS.getSpatialTable("h2gis")).where("id=2").eachRow(new Closure(null) {
            @Override
            public Object call(Object argument) {
                values.add(((ISpatialTable) argument).getGeometry().toString());
                return argument;
            }
        });
        assertEquals(1, values.size());
        assertEquals("POINT (1 1)", values.get(0));
    }

    @Test
    public void queryTableNames() throws SQLException {
        Map<String, String> map = new HashMap<>();
        map.put(DataSourceFactory.JDBC_DATABASE_NAME, "./target/loadH2GIS");
        H2GIS h2GIS = H2GIS.open(map);
        h2GIS.execute("DROP TABLE IF EXISTS table1, table2; " +
                "CREATE TABLE table1 (id int, the_geom geometry(point));" +
                "CREATE TABLE table2 (id int, the_geom geometry(point));");

        Collection<String> values = h2GIS.getTableNames();
        assertTrue(values.contains("LOADH2GIS.PUBLIC.TABLE1"));
        assertTrue(values.contains("LOADH2GIS.PUBLIC.TABLE2"));
    }

    @Test
    public void updateSpatialTable() throws SQLException {
        Map<String, String> map = new HashMap<>();
        map.put(DataSourceFactory.JDBC_DATABASE_NAME, "./target/loadH2GIS");
        H2GIS h2GIS = H2GIS.open(map);
        h2GIS.execute("DROP TABLE IF EXISTS h2gis;" +
                " CREATE TABLE h2gis (id int PRIMARY KEY, code int, the_geom geometry(point));" +
                "insert into h2gis values (1,22, 'POINT(10 10)'::GEOMETRY), (2,56, 'POINT(1 1)'::GEOMETRY);");

        h2GIS.getSpatialTable("h2gis").eachRow(new Closure(null) {
            @Override
            public Object call(Object argument) {
                IJdbcTable sp = ((IJdbcTable) argument);
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
        h2GIS.getSpatialTable("h2gis").eachRow(new Closure(null) {
            @Override
            public Object call(Object argument) {
                try {
                    values.add(((IJdbcTable) argument).getInt(2));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return argument;
            }
        });
        assertEquals(2, values.size());
        assertEquals(3, (int) values.get(0));
        assertEquals(3, (int) values.get(1));
    }

    @Test
    public void request() throws SQLException {
        Map<String, String> map = new HashMap<>();
        map.put(DataSourceFactory.JDBC_DATABASE_NAME, "./target/loadH2GIS2");
        H2GIS h2GIS = H2GIS.open(map);
        h2GIS.execute("DROP TABLE IF EXISTS h2gis; " +
                "CREATE TABLE h2gis (id int PRIMARY KEY, code int, the_geom geometry(point));" +
                "insert into h2gis values (1,22, 'POINT(10 10)'::GEOMETRY);" +
                "insert into h2gis values (2,56, 'POINT(20 20)'::GEOMETRY);" +
                "insert into h2gis values (3,22, 'POINT(10 10)'::GEOMETRY);" +
                "insert into h2gis values (4,22, 'POINT(10 10)'::GEOMETRY);" +
                "insert into h2gis values (5,22, 'POINT(20 10)'::GEOMETRY);");

        ITable table = ((ITable) h2GIS
                .select("COUNT(id)", "code", "the_geom")
                .from("h2gis")
                .where("code=22")
                .and("id<5")
                .groupBy("code")
                .asType(ITable.class));

        ArrayList<Integer> values = new ArrayList<>();
        table.eachRow(new Closure(null) {
            @Override
            public Object call(Object argument) {
                try {
                    values.add(((IJdbcTable) argument).getInt(1));
                    values.add(((IJdbcTable) argument).getInt(2));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return argument;
            }
        });
        assertEquals(2, values.size());
        assertEquals(3, (int) values.get(0));
        assertEquals(22, (int) values.get(1));

        table = ((ITable) h2GIS
                .select("*")
                .from("h2gis")
                .where("code=22")
                .or("code=56")
                .orderBy("id", DESC)
                .asType(ITable.class));

        ArrayList<Integer> values2 = new ArrayList<>();
        table.eachRow(new Closure(null) {
            @Override
            public Object call(Object argument) {
                try {
                    values2.add(((IJdbcTable) argument).getInt(1));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return argument;
            }
        });
        assertEquals(5, values2.size());
        assertEquals(5, (int) values2.get(0));
        assertEquals(4, (int) values2.get(1));
        assertEquals(3, (int) values2.get(2));
        assertEquals(2, (int) values2.get(3));
        assertEquals(1, (int) values2.get(4));

        table = ((ITable) h2GIS
                .select("*")
                .from("h2gis")
                .orderBy("id", DESC)
                .asType(ITable.class));

        ArrayList<Integer> values3 = new ArrayList<>();
        table.eachRow(new Closure(null) {
            @Override
            public Object call(Object argument) {
                try {
                    values3.add(((IJdbcTable) argument).getInt(1));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return argument;
            }
        });
        assertEquals(5, values3.size());
        assertEquals(5, (int) values3.get(0));
        assertEquals(4, (int) values3.get(1));
        assertEquals(3, (int) values3.get(2));
        assertEquals(2, (int) values3.get(3));
        assertEquals(1, (int) values3.get(4));
    }

    @Test
    public void hasTable() throws SQLException {
        Map<String, String> map = new HashMap<>();
        map.put(DataSourceFactory.JDBC_DATABASE_NAME, "./target/loadH2GIS");
        H2GIS h2GIS = H2GIS.open(map);
        h2GIS.execute("DROP TABLE IF EXISTS table1, table2; " +
                "CREATE TABLE table1 (id int, the_geom geometry(point));" +
                "CREATE TABLE table2 (id int, the_geom geometry(point));");

        assertTrue(h2GIS.hasTable("TABLE1"));
        assertTrue(h2GIS.hasTable("TABLE2"));
        assertTrue(h2GIS.hasTable("table1"));
        assertFalse(h2GIS.hasTable("OrbisGIS"));
    }

    @Test
    void testGetTableOnEmptyTable() throws SQLException {
        Map<String, String> map = new HashMap<>();
        map.put(DataSourceFactory.JDBC_DATABASE_NAME, "./target/loadH2GIS");
        H2GIS h2GIS = H2GIS.open(map);
        h2GIS.execute("DROP TABLE IF EXISTS table1, table2; " +
                "CREATE TABLE table1 (id int, the_geom geometry(point));" +
                "CREATE TABLE table2 (id int, val varchar);");
        assertNotNull(h2GIS.getSpatialTable("table1"));
        assertNotNull(h2GIS.getSpatialTable("table1").getGeometricColumns());
        assertNotNull(h2GIS.getTable("table1"));
    }
}
