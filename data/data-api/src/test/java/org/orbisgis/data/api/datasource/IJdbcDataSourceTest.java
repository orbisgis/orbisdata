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
package org.orbisgis.data.api.datasource;

import groovy.lang.GString;
import groovy.lang.MetaClass;
import groovy.lang.MissingMethodException;
import groovy.lang.MissingPropertyException;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.InvokerInvocationException;
import org.h2gis.utilities.dbtypes.DBTypes;
import org.junit.jupiter.api.Test;
import org.orbisgis.data.api.dataset.IJdbcSpatialTable;
import org.orbisgis.data.api.dataset.IJdbcTable;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
import java.sql.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class dedicated to {@link IJdbcDataSource} interface.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (Lab-STICC UBS 2019)
 */
public class IJdbcDataSourceTest {

    /**
     * Test the {@link IJdbcDataSource#invokeMethod(String, Object)} method.
     */
    @Test
    public void testInvokeMethod() {
        IJdbcDataSource ds = new IJdbcDataSourceTest.DummyDataSource();
        ds.setProperty("prop1", "value1");
        assertEquals("value1", ds.invokeMethod("getProperty", "prop1"));
        assertTrue((Boolean) ds.invokeMethod("noArg", null));
        assertTrue((Boolean) ds.invokeMethod("getNoArg", null));
        assertArrayEquals(new Object[]{"string", 0.2}, (Object[]) ds.invokeMethod("arrayMethod", new Object[]{"string", 0.2}));
        assertArrayEquals(new Object[]{"string", 0.2}, (Object[]) ds.invokeMethod("getArrayMethod", new Object[]{"string", 0.2}));
        assertArrayEquals(new Object[]{"string", 0.2}, (Object[]) ds.invokeMethod("getParametersMethod", new Object[]{"string", 0.2}));
        assertArrayEquals(new Object[]{"string", 0.2}, (Object[]) ds.invokeMethod("parametersMethod", new Object[]{"string", 0.2}));
        assertArrayEquals(new Object[]{"string", "0.2"}, (Object[]) ds.invokeMethod("getParametersMethod", new Object[]{"string", "0.2"}));
        assertArrayEquals(new Object[]{"string", "0.2"}, (Object[]) ds.invokeMethod("parametersMethod", new Object[]{"string", "0.2"}));
        assertEquals("string", ds.invokeMethod("getParameterMethod", new Object[]{"string"}));
        assertEquals("string", ds.invokeMethod("getParameterMethod", "string"));
        assertEquals("string", ds.invokeMethod("parameterMethod", new Object[]{"string"}));
        assertEquals("string", ds.invokeMethod("parameterMethod", "string"));

        assertThrows(MissingMethodException.class, () -> ds.invokeMethod("setProperty", new String[]{"tata"}));
        assertNull(ds.invokeMethod("getProperty", null));
        assertThrows(MissingMethodException.class, () -> ds.invokeMethod("notAMethod", null));
    }

    /**
     * Test the {@link IJdbcDataSource#executeScript(String)} and {@link IJdbcDataSource#executeScript(InputStream)}
     * methods.
     */
    @Test
    public void testExecuteScript() {
        DummyDataSource ds = new IJdbcDataSourceTest.DummyDataSource();

        assertFalse(ds.isFileScript());
        assertTrue(ds.executeScript((String) null));
        assertTrue(ds.isFileScript());

        assertFalse(ds.isStreamScript());
        assertTrue(ds.executeScript((InputStream) null));
        assertTrue(ds.isStreamScript());
    }

    /**
     * Test the {@link IJdbcDataSource#getProperty(String)} and {@link IJdbcDataSource#setProperty(String, Object)}
     * methods.
     */
    @Test
    public void testGetProperty() {
        IJdbcDataSource ds = new IJdbcDataSourceTest.DummyDataSource();

        ds.setProperty("prop1", "value1");
        ds.setProperty("prop2", "value2");

        assertEquals("value1", ds.getProperty("prop1"));
        assertEquals("value2", ds.getProperty("prop2"));
        assertNull(ds.getProperty(null));
        assertThrows(MissingPropertyException.class, () -> ds.getProperty("databaseType"));
    }

    /**
     * Test the {@link IJdbcDataSource} methods with {@link Exception} thrown.
     */
    @Test
    public void testMethodThrowingException() {
        IJdbcDataSource ds = new IJdbcDataSourceTest.DummyDataSource();
        assertThrows(InvokerInvocationException.class, () -> ds.invokeMethod("dupMethod", null));
    }

    /**
     * Simple implementation of Exception
     */
    private class DummyException extends Exception {
    }


    /**
     * Simple implementation of the {@link IJdbcDataSource} interface.
     */
    private class DummyDataSource implements IJdbcDataSource {
        private Object prop1;
        private Object prop2;
        private boolean streamScript = false;
        private boolean fileScript = false;

        private boolean isStreamScript() {
            return streamScript;
        }

        private boolean isFileScript() {
            return fileScript;
        }

        private DummyDataSource() {
            prop1 = null;
            prop2 = null;
        }

        public boolean getNoArg() {
            return prop1 == null || prop2 == null || prop2.equals(prop1);
        }

        public Object[] getArrayMethod(Object[] array) {
            return array;
        }

        public Object[] getParametersMethod(String param1, Double param2) {
            return new Object[]{param1, param2};
        }

        public Object[] getParametersMethod(Object param1, Object param2) {
            return new Object[]{param1, param2};
        }

        public String getParameterMethod(String param1) {
            return param1;
        }

        public void dupMethod() throws DummyException {
            throw new DummyException();
        }

        @Override
        public void close() {/*Does nothing*/}

        @Override
        public IJdbcTable getTable(String tableName) {
            return null;
        }

       
        @Override
        public IJdbcTable getTable(GString nameOrQuery) {
            return null;
        }

       
        @Override
        public IJdbcTable getTable(String nameOrQuery, List<Object> params) {
            return null;
        }


        @Override
        public IJdbcTable getTable(String tableName, Statement statement) {
            return null;
        }

       
        @Override
        public IJdbcTable getTable(GString nameOrQuery, Statement statement) {
            return null;
        }

       
        @Override
        public IJdbcTable getTable(String nameOrQuery, List<Object> params, Statement statement) {
            return null;
        }

        @Override
        public IJdbcSpatialTable getSpatialTable(String tableName) {
            return null;
        }

       
        @Override
        public IJdbcSpatialTable getSpatialTable(GString nameOrQuery) {
            return null;
        }

       
        @Override
        public IJdbcSpatialTable getSpatialTable(String nameOrQuery, List<Object> params) {
            return null;
        }

       
        @Override
        public IJdbcSpatialTable getSpatialTable(String tableName, Statement statement) {
            return null;
        }

       
        @Override
        public IJdbcSpatialTable getSpatialTable(GString nameOrQuery, Statement statement) {
            return null;
        }

       
        @Override
        public IJdbcSpatialTable getSpatialTable(String nameOrQuery, List<Object> params, Statement statement) {
            return null;
        }


        @Override
        public Collection<String> getTableNames() {
            return null;
        }


        @Override
        public Collection<String> getTableNames(String namePattern) {
            return null;
        }


        @Override
        public Collection<String> getTableNames(String namePattern, TableType... types) {
            return null;
        }


        @Override
        public Collection<String> getTableNames(String schemaPattern, String namePattern) {
            return null;
        }

        @Override
        public Collection<String> getTableNames(String schemaPattern, String namePattern, TableType... types) {
            return null;
        }

        @Override
        public Collection<String> getTableNames(String catalogPattern, String schemaPattern, String namePattern) {
            return null;
        }

        @Override
        public Collection<String> getTableNames(String catalogPattern, String schemaPattern, String namePattern, TableType... types) {
            return null;
        }

        @Override
        public String load(String filePath) {
            return null;
        }

        @Override
        public String load(String filePath, boolean delete) {
            return null;
        }

        @Override
        public String load(String filePath, String tableName) {
            return null;
        }

        @Override
        public String load(String filePath, String tableName, boolean delete) {
            return null;
        }

        @Override
        public String load(String filePath, String tableName, String encoding, boolean delete) {
            return null;
        }

        @Override
        public String load(URL url) {
            return null;
        }

        @Override
        public String load(URL url, boolean delete) {
            return null;
        }

        @Override
        public String load(URL url, String tableName) {
            return null;
        }

        @Override
        public String load(URL url, String tableName, boolean delete) {
            return null;
        }

        @Override
        public String load(URL url, String tableName, String encoding, boolean delete) {
            return null;
        }

        @Override
        public String load(URI uri) {
            return null;
        }

        @Override
        public String load(URI uri, boolean delete) {
            return null;
        }

        @Override
        public String load(URI uri, String tableName) {
            return null;
        }

        @Override
        public String load(URI uri, String tableName, boolean delete) {
            return null;
        }

        @Override
        public String load(URI uri, String tableName, String encoding, boolean delete) {
            return null;
        }

        @Override
        public String load(File file) {
            return null;
        }

        @Override
        public String load(File file, boolean delete) {
            return null;
        }

        @Override
        public String load(File file, String tableName) {
            return null;
        }

        @Override
        public String load(File file, String tableName, boolean delete) {
            return null;
        }

        @Override
        public String load(File file, String tableName, String encoding, boolean delete) {
            return null;
        }

        @Override
        public String load(IJdbcDataSource dataSource, String inputTableName) {
            return null;
        }

        @Override
        public String load(IJdbcDataSource dataSource, String inputTableName, boolean deleteIfExists) {
            return null;
        }

        @Override
        public String load(IJdbcDataSource dataSource, String inputTableName, String outputTableName) {
            return null;
        }

        @Override
        public String load(IJdbcDataSource dataSource, String inputTableName, String outputTableName, boolean deleteIfExists) {
            return null;
        }

        @Override
        public String load(IJdbcDataSource dataSource, String inputTableName, String outputTableName, boolean deleteIfExists, int batchSize) {
            return null;
        }

        @Override
        public boolean save(String tableName, String filePath) {
            return false;
        }

        @Override
        public boolean save(String tableName, String filePath, boolean delete) {
            return false;
        }

        @Override
        public boolean save(String tableName, String filePath, String encoding) {
            return false;
        }

        @Override
        public boolean save(String tableName, URI uri) {
            return false;
        }

        @Override
        public boolean save(String tableName, URI uri, String encoding) {
            return false;
        }

        @Override
        public boolean save(String tableName, URL url) {
            return false;
        }

        @Override
        public boolean save(String tableName, URL url, String encoding) {
            return false;
        }

        @Override
        public boolean save(String tableName, File file) {
            return false;
        }

        @Override
        public boolean save(String tableName, File file, String encoding) {
            return false;
        }

        @Override
        public String link(String filePath, String tableName, boolean delete) {
            return null;
        }

        @Override
        public String link(String filePath, String tableName) {
            return null;
        }

        @Override
        public String link(String filePath, boolean delete) {
            return null;
        }

        @Override
        public String link(String filePath) {
            return null;
        }

        @Override
        public String link(URI uri, String tableName, boolean delete) {
            return null;
        }

        @Override
        public String link(URI uri, String tableName) {
            return null;
        }

        @Override
        public String link(URI uri, boolean delete) {
            return null;
        }

        @Override
        public String link(URI uri) {
            return null;
        }

        @Override
        public String link(URL url, String tableName, boolean delete) {
            return null;
        }

        @Override
        public String link(URL url, String tableName) {
            return null;
        }

        @Override
        public String link(URL url, boolean delete) {
            return null;
        }

        @Override
        public String link(URL url) {
            return null;
        }

        @Override
        public String link(File file, String tableName, boolean delete) {
            return null;
        }

        @Override
        public String link(File file, String tableName) {
            return null;
        }

        @Override
        public String link(File file, boolean delete) {
            return null;
        }

        @Override
        public String link(File file) {
            return null;
        }

        @Override
        public String link(Map dataSourceProperties, String tableName) {
            return null;
        }

        @Override
        public String link(Map dataSourceProperties, String sourceTableName, boolean delete) {
            return null;
        }

        @Override
        public String link(Map dataSourceProperties, String sourceTableName, String targetTableName, boolean delete) {
            return null;
        }

        @Override
        public boolean executeScript(String fileName, Map<String, String> bindings) {
            fileScript = true;
            return true;
        }

        @Override
        public boolean executeScript(InputStream stream, Map<String, String> bindings) {
            streamScript = true;
            return true;
        }

        @Override
        public DBTypes getDataBaseType() {
            return null;
        }

        @Override
        public MetaClass getMetaClass() {
            return InvokerHelper.getMetaClass(DummyDataSource.class);
        }

        @Override
        public void setMetaClass(MetaClass metaClass) {/*Does nothing*/}

        @Override
        public IJdbcTable getDataSet(String name) {
            return null;
        }

        @Override
        public List<Object> getParameters(GString gString) {
            return null;
        }

        @Override
        public String asSql(GString gString, List<Object> params) {
            return null;
        }

        @Override
        public IJdbcDataSource autoCommit(boolean autoCommit) {
            return this;
        }

        @Override
        public Class<?> typeNameToClass(String typeName) {
            return null;
        }


        @Override
        public IDataSourceLocation getLocation() {
            return null;
        }

       
        @Override
        public Object asType(Class<?> clazz) {
            return null;
        }

        @Override
        public boolean hasTable(String tableName) {
            return false;
        }

       
        @Override
        public Collection<String> getColumnNames(String location) {
            return null;
        }

        @Override
        public boolean createSpatialIndex(String tableName, String columnName) {
            return false;
        }

        @Override
        public boolean createIndex(String tableName, String columnName) {
            return false;
        }

        @Override
        public boolean hasGeometryColumn(String tableName) {
            return false;
        }

        @Override
        public List<String> getGeometryColumns(String tableName) {
            return null;
        }

        @Override
        public String getGeometryColumn(String tableName) {
            return null;
        }

        @Override
        public boolean isIndexed(String tableName, String columnName) {
            return false;
        }

        @Override
        public boolean isSpatialIndexed(String tableName, String columnName) {
            return false;
        }

        @Override
        public void dropIndex(String tableName, String columnName) {

        }

        @Override
        public void dropTable(String... tableName) {

        }

        @Override
        public void dropColumn(String tableName, String... columnName) {

        }

        @Override
        public boolean setSrid(String tableName, String columnName, int srid) {
            return false;
        }

        @Override
        public Connection getConnection() throws SQLException {
            return null;
        }

        @Override
        public Connection getConnection(String s, String s1) throws SQLException {
            return null;
        }

        @Override
        public PrintWriter getLogWriter() throws SQLException {
            return null;
        }

        @Override
        public void setLogWriter(PrintWriter printWriter) throws SQLException {
        }

        @Override
        public void setLoginTimeout(int i) throws SQLException {
        }

        @Override
        public int getLoginTimeout() throws SQLException {
            return 0;
        }

        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            return null;
        }

        @Override
        public <T> T unwrap(Class<T> aClass) throws SQLException {
            return null;
        }

        @Override
        public boolean isWrapperFor(Class<?> aClass) throws SQLException {
            return false;
        }
    }
}
