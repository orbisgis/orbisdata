/*
 * Bundle DataManager API is part of the OrbisGIS platform
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
 * DataManager API  is distributed under GPL 3 license.
 *
 * Copyright (C) 2019 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * DataManager API  is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * DataManager API  is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * DataManager API. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.datamanagerapi.dataset;

import groovy.lang.Closure;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class dedicated to the test of the ITable default methods.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public class ITableTest {

    /** {@link ITable} to test. */
    private static ITable table;
    /** Data written in the {@link ITable}. */
    private static final String COL1_NAME = "Buildings";
    private static final List<Object> COL1_VALUES = Arrays.asList("build1", "build2", "build3");
    private static final String COL2_NAME = "Names";
    private static final List<Object> COL2_VALUES = Arrays.asList("Toto", "Tata", "Titi", "Tutu");
    private static final String COL3_NAME = "Data";
    private static final List<Object> COL3_VALUES = new ArrayList<>();
    private static final String COL4_NAME = "Null";
    private static final List<Object> COL4_VALUES = Arrays.asList(new String[]{null});
    private static final String COL5_NAME = "Numbers";
    private static final List<Object> COL5_VALUES = Arrays.asList(1, 25, 485, 1223333);

    private static final int COLUMN_COUNT = 5;

    /**
     * Initialize the {@link ITable} to test.
     */
    @BeforeAll
    public static void createTable(){
        DummyTable dummyTable = new DummyTable();
        dummyTable.addColumn(COL1_NAME, COL1_VALUES);
        dummyTable.addColumn(COL2_NAME, COL2_VALUES);
        dummyTable.addColumn(COL3_NAME, COL3_VALUES);
        dummyTable.addColumn(COL4_NAME, COL4_VALUES);
        dummyTable.addColumn(COL5_NAME, COL5_VALUES);
        table = dummyTable;
    }

    /**
     * Test the {@link ITable#hasColumn(String)} method.
     */
    @Test
    public void testHasColumn(){
        assertTrue(table.hasColumn(COL1_NAME));
        assertTrue(table.hasColumn(COL2_NAME));
        assertTrue(table.hasColumn(COL3_NAME));
        assertTrue(table.hasColumn(COL4_NAME));
        assertTrue(table.hasColumn(COL5_NAME));

        assertFalse(table.hasColumn("buildings"));
        assertFalse(table.hasColumn("names"));
        assertFalse(table.hasColumn("Datas"));
        assertFalse(table.hasColumn("null"));
        assertFalse(table.hasColumn("Number"));
    }

    /**
     * Test the {@link ITable#hasColumns(List)} method.
     */
    @Test
    public void testHasColumns(){
        List<String> list = new ArrayList<>();
        list.add(COL1_NAME);
        list.add(COL2_NAME);
        list.add(COL3_NAME);
        list.add(COL4_NAME);
        list.add(COL5_NAME);

        assertTrue(table.hasColumns(list));

        list = new ArrayList<>();
        list.add("buildings");
        assertFalse(table.hasColumns(list));
        list = new ArrayList<>();
        list.add("names");
        assertFalse(table.hasColumns(list));
        list = new ArrayList<>();
        list.add("Datas");
        assertFalse(table.hasColumns(list));
        list = new ArrayList<>();
        list.add("null");
        assertFalse(table.hasColumns(list));
        list = new ArrayList<>();
        list.add("Number");
        assertFalse(table.hasColumns(list));
    }

    /**
     * Test the {@link ITable#hasColumn(String, Class)} method.
     */
    @Test
    public void testHasColumnWithClass(){
        assertTrue(table.hasColumn(COL1_NAME, String.class));
        assertFalse(table.hasColumn(COL1_NAME, Object.class));
        assertFalse(table.hasColumn(COL1_NAME, Integer.class));

        assertTrue(table.hasColumn(COL2_NAME, String.class));
        assertFalse(table.hasColumn(COL2_NAME, Object.class));
        assertFalse(table.hasColumn(COL2_NAME, Integer.class));

        assertFalse(table.hasColumn(COL3_NAME, String.class));
        assertFalse(table.hasColumn(COL3_NAME, Object.class));
        assertFalse(table.hasColumn(COL3_NAME, Integer.class));

        assertFalse(table.hasColumn(COL4_NAME, String.class));
        assertFalse(table.hasColumn(COL4_NAME, Object.class));
        assertFalse(table.hasColumn(COL4_NAME, Integer.class));

        assertFalse(table.hasColumn(COL5_NAME, String.class));
        assertFalse(table.hasColumn(COL5_NAME, Object.class));
        assertTrue(table.hasColumn(COL5_NAME, Integer.class));
    }

    /**
     * Test the {@link ITable#hasColumns(Map)} method.
     */
    @Test
    public void testHasColumnsWithClass(){
        Map<String, Class> map = new HashMap<>();

        map.put(COL1_NAME, String.class);
        map.put(COL2_NAME, String.class);
        map.put(COL5_NAME, Integer.class);
        assertTrue(table.hasColumns(map));

        map = new HashMap<>();
        map.put(COL3_NAME, String.class);
        assertFalse(table.hasColumns(map));
        map = new HashMap<>();
        map.put(COL4_NAME, String.class);
        assertFalse(table.hasColumns(map));
        map = new HashMap<>();
        map.put(COL5_NAME, String.class);
        assertFalse(table.hasColumns(map));
        map = new HashMap<>();
        map.put(COL1_NAME, Object.class);
        assertFalse(table.hasColumns(map));
        map = new HashMap<>();
        map.put(COL2_NAME, Object.class);
        assertFalse(table.hasColumns(map));
        map = new HashMap<>();
        map.put(COL3_NAME, Object.class);
        assertFalse(table.hasColumns(map));
        map = new HashMap<>();
        map.put(COL4_NAME, Object.class);
        assertFalse(table.hasColumns(map));
        map = new HashMap<>();
        map.put(COL5_NAME, Object.class);
        assertFalse(table.hasColumns(map));
        map = new HashMap<>();
        map.put(COL1_NAME, Integer.class);
        assertFalse(table.hasColumns(map));
        map = new HashMap<>();
        map.put(COL2_NAME, Integer.class);
        assertFalse(table.hasColumns(map));
        map = new HashMap<>();
        map.put(COL3_NAME, Integer.class);
        assertFalse(table.hasColumns(map));
        map = new HashMap<>();
        map.put(COL4_NAME, Integer.class);
        assertFalse(table.hasColumns(map));
    }

    /**
     * Test the {@link ITable#getColumnCount()} method.
     */
    @Test
    public void testGetColumnCount(){
        assertEquals(COLUMN_COUNT, table.getColumnCount());
        assertEquals(0, new DummyTable().getColumnCount());
    }

    /**
     * Test the {@link ITable#isEmpty()} method.
     */
    @Test
    public void testIsEmpty(){
        assertFalse(table.isEmpty());
        assertTrue(new DummyTable().isEmpty());
    }

    /**
     * Test the {@link ITable#save(String)} ()} method.
     */
    @Test
    public void testSave(){
        assertTrue(table.save("path"));
    }

    /**
     * Simple implementation of {@link ITable} for test purpose.
     */
    private static class DummyTable implements ITable {

        /** {@link List} of columns. A column is a list with the column name as first value. */
        private List<List<Object>> columns;

        /**
         * Main constructor with an empty column list.
         */
        private DummyTable(){
            columns = new ArrayList<>();
        }

        /**
         * Add a single column.
         *
         * @param columnName Name of the column.
         * @param values Values of the column.
         */
        private void addColumn(String columnName, List<Object> values){
            List<Object> list = new ArrayList<>();
            list.add(columnName);
            list.addAll(values);
            columns.add(list);
        }

        @Override
        public Collection<String> getColumnNames() {
            return columns.stream().map(column -> column.get(0).toString()).collect(Collectors.toList());
        }

        @Override
        public boolean hasColumn(String columnName, Class clazz) {
            return columns
                    .stream()
                    .filter(column -> column.size() >= 2 &&
                            column.get(0).equals(columnName) &&
                            column.get(1) != null && column.get(1).getClass().equals(clazz))
                    .count() == 1;
        }

        @Override
        public int getRowCount() {
            return columns
                    .stream()
                    .mapToInt(List::size)
                    .max()
                    .orElse(1)-1;
        }

        @Override public void eachRow(Closure closure) {/*Does nothing*/}
        @Override public Collection<String> getUniqueValues(String column) {return null;}
        @Override public boolean save(String filePath, String encoding) {return true;}
        @Override public List<Object> getFirstRow() {return null;}
        @Override public String getLocation() {return null;}
        @Override public String getName() {return null;}
        @Override public Object getMetaData() {return null;}
        @Override public Object asType(Class clazz) {return null;}
        @Override public Iterator<Object> iterator() {return new ResultSetIterator();}
        @Override public Map<String, String> getColumns() {return null;}
        @Override public String getColumnsType(String columnName) {return null;}
    }
}
