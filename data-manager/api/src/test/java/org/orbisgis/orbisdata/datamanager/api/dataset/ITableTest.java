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
 * DataManager API is distributed under LGPL 3 license.
 *
 * Copyright (C) 2019-2020 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * DataManager API is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * DataManager API is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.orbisgis.orbisdata.datamanager.api.dataset;

import groovy.lang.Closure;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.orbisgis.commons.annotations.NotNull;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class dedicated to the test of the ITable default methods.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (Lab-STICC UBS 2019)
 */
public class ITableTest {

    /**
     * {@link ITable} to test.
     */
    private static ITable table;
    /**
     * Data written in the {@link ITable}.
     */
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
    public static void createTable() {
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
    public void testHasColumn() {
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
    public void testHasColumns() {
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
    public void testHasColumnWithClass() {
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
    public void testHasColumnsWithClass() {
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
    public void testGetColumnCount() {
        assertEquals(COLUMN_COUNT, table.getColumnCount());
        assertEquals(0, new DummyTable().getColumnCount());
    }

    /**
     * Test the {@link ITable#isEmpty()} method.
     */
    @Test
    public void testIsEmpty() {
        assertFalse(table.isEmpty());
        assertTrue(new DummyTable().isEmpty());
    }

    /**
     * Test the {@link ITable#save(String)} ()} method.
     */
    @Test
    public void testSave() {
        assertTrue(table.save("path"));
    }

    /**
     * Test the {@link ITable#getNDim()} method.
     */
    @Test
    public void testNDim() {
        assertEquals(2, table.getNDim());
    }

    /**
     * Test the {@link ITable#getSize()} method.
     */
    @Test
    public void testGetShape() {
        int[] shape = table.getSize();
        assertEquals(2, shape.length);
        assertEquals(5, shape[0]);
        assertEquals(4, shape[1]);
    }

    /**
     * Test the {@link ITable#eachRow(Closure)} method.
     */
    @Test
    public void testEachRow() {
        final String[] result = {""};
        Closure cl = new Closure(this) {
            @Override
            public Object call(Object argument) {
                result[0] += argument;
                return argument;
            }
        };
        table.eachRow(cl);
        assertEquals("12345678910", result[0]);
    }

    /**
     * Simple implementation of {@link ITable} for test purpose.
     */
    private static class DummyTable implements ITable<ResultSet, ResultSet> {

        /**
         * {@link List} of columns. A column is a list with the column name as first value.
         */
        private List<List<Object>> columns;

        /**
         * Main constructor with an empty column list.
         */
        private DummyTable() {
            columns = new ArrayList<>();
        }

        /**
         * Add a single column.
         *
         * @param columnName Name of the column.
         * @param values     Values of the column.
         */
        private void addColumn(String columnName, List<Object> values) {
            List<Object> list = new ArrayList<>();
            list.add(columnName);
            list.addAll(values);
            columns.add(list);
        }

        @NotNull
        @Override
        public Collection<String> getColumns() {
            return columns.stream().map(column -> column.get(0).toString()).collect(Collectors.toList());
        }

        @Override
        public boolean hasColumn(@NotNull String columnName, @NotNull Class clazz) {
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
                    .orElse(1) - 1;
        }

        @Override
        public int getRow() {
            return 0;
        }

        @Override
        public boolean next() {
            return false;
        }

        @Override
        public boolean previous() throws Exception {
            return false;
        }

        @Override
        public boolean first() throws Exception {
            return false;
        }

        @Override
        public boolean last() throws Exception {
            return false;
        }

        @Override
        public boolean isFirst() throws Exception {
            return false;
        }

        @Override
        public boolean isLast() throws Exception {
            return false;
        }

        @Override
        public Collection<String> getUniqueValues(@NotNull String column) {
            return null;
        }

        @Override
        public boolean save(@NotNull String filePath, String encoding) {
            return true;
        }

        @NotNull
        @Override
        public List<Object> getFirstRow() {
            return null;
        }

        @NotNull
        @Override
        public ITable columns(@NotNull String... columns) {
            return null;
        }

        @NotNull
        @Override
        public ITable columns(@NotNull List<String> columns) {
            return null;
        }

        @Override
        public boolean isSpatial() {
            return false;
        }

        @Override
        public String getString(int column) {
            return null;
        }

        @Override
        public boolean getBoolean(int column) {
            return false;
        }

        @Override
        public byte getByte(int column) {
            return 0;
        }

        @Override
        public short getShort(int column) {
            return 0;
        }

        @Override
        public int getInt(int column) {
            return 0;
        }

        @Override
        public long getLong(int column) {
            return 0;
        }

        @Override
        public float getFloat(int column) {
            return 0;
        }

        @Override
        public double getDouble(int column) {
            return 0;
        }

        @Override
        public byte[] getBytes(int column) {
            return new byte[0];
        }

        @Override
        public Date getDate(int column) {
            return null;
        }

        @Override
        public Time getTime(int column) {
            return null;
        }

        @Override
        public Timestamp getTimestamp(int column) {
            return null;
        }

        @Override
        public Object getObject(int column) {
            return null;
        }

        @Override
        public BigDecimal getBigDecimal(int column) {
            return null;
        }

        @Override
        public String getString(@NotNull String column) {
            return null;
        }

        @Override
        public boolean getBoolean(@NotNull String column) {
            return false;
        }

        @Override
        public byte getByte(@NotNull String column) {
            return 0;
        }

        @Override
        public short getShort(@NotNull String column) {
            return 0;
        }

        @Override
        public int getInt(@NotNull String column) {
            return 0;
        }

        @Override
        public long getLong(String column) {
            return 0;
        }

        @Override
        public float getFloat(@NotNull String column) {
            return 0;
        }

        @Override
        public double getDouble(@NotNull String column) {
            return 0;
        }

        @Override
        public byte[] getBytes(@NotNull String column) {
            return new byte[0];
        }

        @Override
        public Date getDate(@NotNull String column) {
            return null;
        }

        @Override
        public Time getTime(@NotNull String column) {
            return null;
        }

        @Override
        public Timestamp getTimestamp(@NotNull String column) {
            return null;
        }

        @Override
        public Object getObject(@NotNull String column) {
            return null;
        }

        @Override
        public BigDecimal getBigDecimal(@NotNull String column) {
            return null;
        }

        @Override
        public <U> U getObject(int column, @NotNull Class<U> clazz) throws Exception {
            return null;
        }

        @Override
        public <U> U getObject(@NotNull String column, @NotNull Class<U> clazz) throws Exception {
            return null;
        }

        @Override
        public Stream<ResultSet> stream() {
            return null;
        }

        @Override
        public ITable<ResultSet, ResultSet> filter(String filter) {
            return null;
        }

        @NotNull
        @Override
        public Map<String, Object> firstRow() {
            return null;
        }

        @Override
        public String getLocation() {
            return null;
        }

        @NotNull
        @Override
        public String getName() {
            return null;
        }

        @NotNull
        @Override
        public Object getMetaData() {
            return null;
        }

        @Override
        public Object asType(@NotNull Class clazz) {
            return null;
        }

        @NotNull
        @Override
        public ISummary getSummary() {
            return null;
        }

        @Override
        public boolean reload() {
            return false;
        }

        @Override
        public Iterator<ResultSet> iterator() {
            return new DummyIterator();
        }

        @NotNull
        @Override
        public Map<String, String> getColumnsTypes() {
            return null;
        }

        @Override
        public String getColumnType(@NotNull String columnName) {
            return null;
        }
    }

    private static class DummyIterator implements Iterator {

        private int index = 0;
        private int count = 10;

        @Override
        public boolean hasNext() {
            return index < count;
        }

        @Override
        public Object next() {
            return ++index;
        }
    }
}
