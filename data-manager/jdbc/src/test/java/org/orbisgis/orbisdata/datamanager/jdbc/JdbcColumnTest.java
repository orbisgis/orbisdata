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
package org.orbisgis.orbisdata.datamanager.jdbc;

import groovy.lang.MetaClass;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.orbisgis.orbisdata.datamanager.jdbc.h2gis.H2GIS;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class dedicated to {@link JdbcColumn} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public class JdbcColumnTest {

    /** Default database name */
    private static final String BASE_DATABASE = "./target/" + JdbcColumnTest.class.getSimpleName();
    /** Database connection */
    private static JdbcDataSource dataSource;

    private static final String TABLE_NAME = "orbisgis";
    private static final String TEMP_NAME = "tempTable";
    private static final String LINKED_NAME = "linkedTable";

    private static final String COL_THE_GEOM = "the_geom";
    private static final String COL_THE_GEOM2 = "the_geom2";
    private static final String COL_ID = "id";
    private static final String COL_VALUE = "value";
    private static final String COL_MEANING = "meaning";

    private static final String COL_NO_COL = "COL_NO_COL";
    private static final String COL_NO_TAB = "COL_NO_TAB";

    /**
     * Initialization of the database.
     */
    @BeforeAll
    public static void init(){
        dataSource = H2GIS.open(BASE_DATABASE);
        try {
            dataSource.execute("DROP TABLE IF EXISTS "+TABLE_NAME+","+LINKED_NAME+","+TEMP_NAME);
        dataSource.execute("CREATE TABLE "+TABLE_NAME+" ("+COL_THE_GEOM+" GEOMETRY, "+COL_THE_GEOM2+" GEOMETRY(POINT Z)," +
                COL_ID+" INTEGER, "+COL_VALUE+" FLOAT, "+COL_MEANING+" VARCHAR)");
        dataSource.execute("INSERT INTO "+TABLE_NAME+" VALUES ('POINT(0 0)', 'POINT(1 1 0)', 1, 2.3, 'Simple points')");
        dataSource.execute("INSERT INTO "+TABLE_NAME+" VALUES ('POINT(0 1 2)', 'POINT(10 11 12)', 2, 0.568, '3D point')");
        dataSource.execute("INSERT INTO "+TABLE_NAME+" VALUES (null, 'POINT(110 111 112)', 2, null, '3D point')");
        } catch (SQLException e) {
            fail(e);
        }
    }

    /**
     * Return a simple instantiation of a {@link JdbcColumn}.
     *
     * @param name Name of the column
     *
     * @return A simple instantiation of a {@link JdbcColumn}.
     */
    private JdbcColumn getColumn(String name){
        if(COL_NO_TAB.equals(name)){
            return new DummyJdbcColumn(name, name, dataSource);
        }
        return new DummyJdbcColumn(name, TABLE_NAME, dataSource);
    }

    /**
     * Test the {@link JdbcColumn#getName()} method.
     */
    @Test
    public void testGetName(){
        assertEquals(COL_THE_GEOM.toUpperCase(), getColumn(COL_THE_GEOM).getName());
        assertEquals(COL_THE_GEOM2.toUpperCase(), getColumn(COL_THE_GEOM2).getName());
        assertEquals(COL_ID.toUpperCase(), getColumn(COL_ID).getName());
        assertEquals(COL_VALUE.toUpperCase(), getColumn(COL_VALUE).getName());
        assertEquals(COL_MEANING.toUpperCase(), getColumn(COL_MEANING).getName());
        assertEquals(COL_NO_COL.toUpperCase(), getColumn(COL_NO_COL).getName());
        assertEquals(COL_NO_TAB.toUpperCase(), getColumn(COL_NO_TAB).getName());
    }

    /**
     * Test the {@link JdbcColumn#getType()} method.
     */
    @Test
    public void testGetType(){
        assertEquals("GEOMETRY", getColumn(COL_THE_GEOM).getType());
        assertEquals("GEOMETRY", getColumn(COL_THE_GEOM2).getType());
        assertEquals("INTEGER", getColumn(COL_ID).getType());
        assertEquals("DOUBLE", getColumn(COL_VALUE).getType());
        assertEquals("VARCHAR", getColumn(COL_MEANING).getType());
        assertNull(getColumn(COL_NO_COL).getType());
        assertNull(getColumn(COL_NO_TAB).getType());
    }

    /**
     * Test the {@link JdbcColumn#getSize()} method.
     */
    @Test
    public void testGetSize(){
        assertEquals(2, getColumn(COL_THE_GEOM).getSize());
        assertEquals(3, getColumn(COL_THE_GEOM2).getSize());
        assertEquals(3, getColumn(COL_ID).getSize());
        assertEquals(2, getColumn(COL_VALUE).getSize());
        assertEquals(3, getColumn(COL_MEANING).getSize());
        assertEquals(-1, getColumn(COL_NO_COL).getSize());
        assertEquals(-1, getColumn(COL_NO_TAB).getSize());
    }

    /**
     * Test the {@link JdbcColumn#isSpatial()} method.
     */
    @Test
    public void testIsSpatial(){
        assertTrue(getColumn(COL_THE_GEOM).isSpatial());
        assertTrue(getColumn(COL_THE_GEOM2).isSpatial());
        assertFalse(getColumn(COL_ID).isSpatial());
        assertFalse(getColumn(COL_VALUE).isSpatial());
        assertFalse(getColumn(COL_MEANING).isSpatial());
        assertFalse(getColumn(COL_NO_COL).isSpatial());
        assertFalse(getColumn(COL_NO_TAB).isSpatial());
    }

    /**
     * Test that there is no spatial index
     */
    private void testNoSpatialIndexes(){
        //Test no spatial index
        assertFalse(getColumn(COL_THE_GEOM).isSpatialIndexed());
        assertFalse(getColumn(COL_THE_GEOM2).isSpatialIndexed());
        assertFalse(getColumn(COL_ID).isSpatialIndexed());
        assertFalse(getColumn(COL_VALUE).isSpatialIndexed());
        assertFalse(getColumn(COL_MEANING).isSpatialIndexed());
        assertFalse(getColumn(COL_NO_COL).isSpatialIndexed());
        assertFalse(getColumn(COL_NO_TAB).isSpatialIndexed());
    }

    /**
     * Test that there is no index
     */
    private void testNoIndexes(){
        //Test no index
        assertFalse(getColumn(COL_THE_GEOM).isIndexed());
        assertFalse(getColumn(COL_THE_GEOM2).isIndexed());
        assertFalse(getColumn(COL_ID).isIndexed());
        assertFalse(getColumn(COL_VALUE).isIndexed());
        assertFalse(getColumn(COL_MEANING).isIndexed());
        assertFalse(getColumn(COL_NO_COL).isIndexed());
        assertFalse(getColumn(COL_NO_TAB).isIndexed());
    }

    /**
     * Drop indexes on all columns
     */
    private void dropIndexes(){
        //Test drop index
        getColumn(COL_THE_GEOM).dropIndex();
        getColumn(COL_THE_GEOM2).dropIndex();
        getColumn(COL_ID).dropIndex();
        getColumn(COL_VALUE).dropIndex();
        getColumn(COL_MEANING).dropIndex();
        getColumn(COL_NO_COL).dropIndex();
        getColumn(COL_NO_TAB).dropIndex();
    }

    /**
     * Test the {@link JdbcColumn#isIndexed()}, {@link JdbcColumn#isSpatialIndexed()},
     * {@link JdbcColumn#createIndex()}, {@link JdbcColumn#createSpatialIndex()}, {@link JdbcColumn#dropIndex()}
     * methods.
     */
    @Test
    public void testIndexes(){
        dropIndexes();
        testNoSpatialIndexes();
        testNoIndexes();

        //Test standard index creation
        assertTrue(getColumn(COL_THE_GEOM).createIndex());
        assertTrue(getColumn(COL_THE_GEOM2).createIndex());
        assertTrue(getColumn(COL_ID).createIndex());
        assertTrue(getColumn(COL_VALUE).createIndex());
        assertTrue(getColumn(COL_MEANING).createIndex());
        assertFalse(getColumn(COL_NO_COL).createIndex());
        assertFalse(getColumn(COL_NO_TAB).createIndex());

        //Test no index re-creation
        assertFalse(getColumn(COL_THE_GEOM).createIndex());
        assertFalse(getColumn(COL_THE_GEOM2).createIndex());
        assertFalse(getColumn(COL_ID).createIndex());
        assertFalse(getColumn(COL_VALUE).createIndex());
        assertFalse(getColumn(COL_MEANING).createIndex());
        assertFalse(getColumn(COL_NO_COL).createIndex());
        assertFalse(getColumn(COL_NO_TAB).createIndex());

        //Test index
        assertTrue(getColumn(COL_THE_GEOM).isIndexed());
        assertTrue(getColumn(COL_THE_GEOM2).isIndexed());
        assertTrue(getColumn(COL_ID).isIndexed());
        assertTrue(getColumn(COL_VALUE).isIndexed());
        assertTrue(getColumn(COL_MEANING).isIndexed());
        assertFalse(getColumn(COL_NO_COL).isIndexed());
        assertFalse(getColumn(COL_NO_TAB).isIndexed());

        testNoSpatialIndexes();

        dropIndexes();
        testNoSpatialIndexes();
        testNoIndexes();

        //Test spatial index  creation
        assertTrue(getColumn(COL_THE_GEOM).createSpatialIndex());
        assertTrue(getColumn(COL_THE_GEOM2).createSpatialIndex());
        assertFalse(getColumn(COL_ID).createSpatialIndex());
        assertFalse(getColumn(COL_VALUE).createSpatialIndex());
        assertFalse(getColumn(COL_MEANING).createSpatialIndex());
        assertFalse(getColumn(COL_NO_COL).createSpatialIndex());
        assertFalse(getColumn(COL_NO_TAB).createSpatialIndex());

        //Test standard index
        assertTrue(getColumn(COL_THE_GEOM).isIndexed());
        assertTrue(getColumn(COL_THE_GEOM2).isIndexed());
        assertFalse(getColumn(COL_ID).isIndexed());
        assertFalse(getColumn(COL_VALUE).isIndexed());
        assertFalse(getColumn(COL_MEANING).isIndexed());
        assertFalse(getColumn(COL_NO_COL).isIndexed());
        assertFalse(getColumn(COL_NO_TAB).isIndexed());

        //Test spatial index
        assertTrue(getColumn(COL_THE_GEOM).isSpatialIndexed());
        assertTrue(getColumn(COL_THE_GEOM2).isSpatialIndexed());
        assertFalse(getColumn(COL_ID).isSpatialIndexed());
        assertFalse(getColumn(COL_VALUE).isSpatialIndexed());
        assertFalse(getColumn(COL_MEANING).isSpatialIndexed());
        assertFalse(getColumn(COL_NO_COL).isSpatialIndexed());
        assertFalse(getColumn(COL_NO_TAB).isIndexed());

        //Test no index re-creation
        assertFalse(getColumn(COL_THE_GEOM).createIndex());
        assertFalse(getColumn(COL_THE_GEOM2).createIndex());
        assertTrue(getColumn(COL_ID).createIndex());
        assertTrue(getColumn(COL_VALUE).createIndex());
        assertTrue(getColumn(COL_MEANING).createIndex());
        assertFalse(getColumn(COL_NO_COL).createIndex());
        assertFalse(getColumn(COL_NO_TAB).createIndex());
    }

    /**
     * Test the {@link JdbcColumn#getMetaClass()} and {@link JdbcColumn#setMetaClass(MetaClass)} methods.
     */
    @Test
    public void testMetaClass(){
        JdbcColumn column = getColumn(COL_THE_GEOM);
        assertEquals(InvokerHelper.getMetaClass(JdbcColumn.class), column.getMetaClass());
        column.setMetaClass(InvokerHelper.getMetaClass(this.getClass()));
        assertEquals(InvokerHelper.getMetaClass(this.getClass()), column.getMetaClass());
    }

    /**
     * Test the {@link JdbcColumn#invokeMethod(String, Object)} method.
     */
    @Test
    public void testInvokeMethod(){
        JdbcColumn column = getColumn(COL_THE_GEOM);
        column.invokeMethod("dropIndex", null);
        assertTrue((Boolean)column.invokeMethod("createIndex", null));
        assertTrue((Boolean)column.invokeMethod("indexed", null));
        assertTrue((Boolean)column.invokeMethod("isIndexed", null));
        assertEquals(COL_THE_GEOM.toUpperCase(), column.invokeMethod("getName", null));
        assertEquals(COL_THE_GEOM.toUpperCase(), column.invokeMethod("name", null));
    }

    /**
     * Test the {@link JdbcColumn#getProperty(String)}, {@link JdbcColumn#setProperty(String, Object)} method.
     */
    @Test
    public void testProperties(){
        JdbcColumn column = getColumn(COL_THE_GEOM);
        column.createIndex();
        assertTrue((Boolean)column.getProperty("indexed"));
        assertEquals(COL_THE_GEOM.toUpperCase(), column.getProperty("name"));
        column.setProperty("name", "toto");
        assertEquals("toto", column.getProperty("name"));
    }

    /**
     * Simple extension of the {@link JdbcColumn} class.
     */
    private class DummyJdbcColumn extends JdbcColumn{
        private DummyJdbcColumn(String name, String tableName, JdbcDataSource dataSource){
            super(name, tableName, dataSource);
        }
    }
}
