/*
 * Bundle JDBC is part of the OrbisGIS platform
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
 * JDBC is distributed under LGPL 3 license.
 *
 * Copyright (C) 2018 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * JDBC is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * JDBC is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * JDBC. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.data.jdbc;

import groovy.lang.Closure;
import groovy.lang.GString;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.sql.GroovyRowResult;
import groovy.sql.Sql;
import groovy.text.SimpleTemplateEngine;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.SimpleType;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.h2.util.ScriptReader;
import org.h2gis.functions.io.utility.IOMethods;
import org.h2gis.utilities.*;
import org.h2gis.utilities.dbtypes.DBTypes;
import org.locationtech.jts.geom.*;
import org.orbisgis.commons.printer.Ascii;
import org.orbisgis.data.api.dataset.IJdbcTable;
import org.orbisgis.data.api.datasource.IDataSourceLocation;
import org.orbisgis.data.api.datasource.IJdbcDataSource;
import org.orbisgis.data.api.dsl.IResultSetBuilder;
import org.orbisgis.data.jdbc.dsl.ResultSetBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.*;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Date;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.orbisgis.commons.printer.ICustomPrinter.CellPosition.*;

/**
 * Abstract class used to implements the request builder methods (select, from ...) in order to give a base to all the
 * JdbcDataSource implementations.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC 2019 / Chaire GEOTERA 2020)
 */
public abstract class JdbcDataSource extends Sql implements IJdbcDataSource, IResultSetBuilder {

    static final Map<String, Class> TYPE_NAME_TO_CLASS =
            new HashMap<String, Class>() {
                {
                    put("GEOMETRY", Geometry.class);
                    put("GEOGRAPHY", Geometry.class);
                    put("POINT", Point.class);
                    put("POINTM", Point.class);
                    put("POINTZ", Point.class);
                    put("POINTZM", Point.class);
                    put("LINESTRING", LineString.class);
                    put("LINESTRINGM", LineString.class);
                    put("LINESTRINGZ", LineString.class);
                    put("LINESTRINGZM", LineString.class);
                    put("POLYGON", Polygon.class);
                    put("POLYGONM", Polygon.class);
                    put("POLYGONZ", Polygon.class);
                    put("POLYGONZM", Polygon.class);
                    put("MULTIPOINT", MultiPoint.class);
                    put("MULTIPOINTM", MultiPoint.class);
                    put("MULTIPOINTZ", MultiPoint.class);
                    put("MULTIPOINTZM", MultiPoint.class);
                    put("MULTILINESTRING", MultiLineString.class);
                    put("MULTILINESTRINGM", MultiLineString.class);
                    put("MULTILINESTRINGZ", MultiLineString.class);
                    put("MULTILINESTRINGZM", MultiLineString.class);
                    put("MULTIPOLYGON", MultiPolygon.class);
                    put("MULTIPOLYGONM", MultiPolygon.class);
                    put("MULTIPOLYGONZ", MultiPolygon.class);
                    put("MULTIPOLYGONZM", MultiPolygon.class);
                    put("GEOMETRYCOLLECTION", GeometryCollection.class);
                    put("GEOMETRYCOLLECTIONM", GeometryCollection.class);
                    put("BYTEA", byte[].class);
                    put("INT2", Short.class);
                    put("INT4", Integer.class);
                    put("INT8", Long.class);
                    put("INTEGER", Integer.class);
                    put("FLOAT4", Float.class);
                    put("FLOAT", Float.class);
                    put("REAL", Float.class);
                    put("DOUBLE", Double.class);
                    put("DOUBLE PRECISION", Double.class);
                    put("FLOAT8", Double.class);
                    put("BOOL", Boolean.class);
                    put("BOOLEAN", Boolean.class);
                    put("VARCHAR", String.class);
                    put("CHARACTER VARYING", String.class);
                    put("DATE", java.sql.Date.class);
                    put("TIME", java.sql.Time.class);
                    put("TIMESTAMP", java.sql.Timestamp.class);
                    put("TIMESTAMPZ", java.sql.Timestamp.class);
                    put("TIMESTAMPTZ", java.sql.Timestamp.class);
                    put("TINYINT", Byte.class);
                    put("SMALLINT", Short.class);
                    put("BIGINT", Long.class);
                }
            };
    private IOMethods ioMethods = null;
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcDataSource.class);
    /**
     * MetaClass used for the implementation of the {@link GroovyObject} methods
     */
    private MetaClass metaClass;
    /**
     * Type of the database
     */
    private final DBTypes databaseType;
    /**
     * Wrapped {@link DataSource}
     */
    private final DataSource dataSource;

    /**
     * Constructor to create a {@link JdbcDataSource} from a {@link Sql} object.
     *
     * @param parent       Parent {@link Sql} object.
     * @param databaseType Type of the database
     */
    public JdbcDataSource(Sql parent, DBTypes databaseType) {
        super(parent);
        this.dataSource = parent.getDataSource();
        this.metaClass = InvokerHelper.getMetaClass(getClass());
        this.databaseType = databaseType;
        LOG.setLevel(Level.OFF);
    }

    /**
     * Constructor to create a {@link JdbcDataSource} from a {@link DataSource} object.
     *
     * @param dataSource   Parent {@link DataSource} object.
     * @param databaseType Type of the database
     */
    public JdbcDataSource(DataSource dataSource, DBTypes databaseType) {
        super(dataSource);
        this.dataSource = dataSource;
        this.metaClass = InvokerHelper.getMetaClass(getClass());
        this.databaseType = databaseType;
        LOG.setLevel(Level.OFF);
    }

    /**
     * Constructor to create a {@link JdbcDataSource} from a {@link Connection} object.
     *
     * @param connection   Parent {@link Sql} object.
     * @param databaseType Type of the database
     */
    public JdbcDataSource(Connection connection, DBTypes databaseType) {
        super(connection);
        this.dataSource = null;
        this.metaClass = InvokerHelper.getMetaClass(getClass());
        this.databaseType = databaseType;
        LOG.setLevel(Level.OFF);
    }

    @Override
    public Collection<String> getTableNames(String namePattern) {
        return getTableNames(null, null, namePattern, (TableType[]) null);
    }

    @Override
    public Collection<String> getTableNames(String namePattern, TableType... types) {
        return getTableNames(null, null, namePattern, types);
    }

    @Override
    public Collection<String> getTableNames(String schemaPattern, String namePattern) {
        return getTableNames(null, schemaPattern, namePattern, (TableType[]) null);
    }

    @Override
    public Collection<String> getTableNames(String schemaPattern, String namePattern,
                                            TableType... types) {
        return getTableNames(null, schemaPattern, namePattern, types);
    }

    @Override
    public Collection<String> getTableNames(String catalogPattern, String schemaPattern,
                                            String namePattern) {
        return getTableNames(catalogPattern, schemaPattern, namePattern, (TableType[]) null);
    }

    @Override
    public Collection<String> getTableNames(String catalogPattern, String schemaPattern,
                                            String namePattern, TableType... types) {
        String[] array = null;
        if (types != null) {
            array = Arrays.stream(types).filter(Objects::nonNull).map(Enum::toString).toArray(String[]::new);
        }
        try {
            return JDBCUtilities.getTableNames(this.getConnection(), catalogPattern, schemaPattern, namePattern, array);
        } catch (SQLException e) {
            LOGGER.error("Unable to get the table names.", e);
        }
        return new ArrayList<>();
    }

    @Override
    public Connection getConnection(String var1, String var2) throws SQLException {
        if (this.dataSource != null) {
            return this.dataSource.getConnection(var1, var2);
        }
        LOGGER.error("Unable to get the DataSource.\n");
        return null;
    }

    public PrintWriter getLogWriter() throws SQLException {
        if (this.dataSource != null) {
            return this.dataSource.getLogWriter();
        }
        LOGGER.error("Unable to get the DataSource.\n");
        return null;
    }

    public void setLogWriter(PrintWriter writer) throws SQLException {
        if (this.dataSource != null) {
            this.dataSource.setLogWriter(writer);
        }
        LOGGER.error("Unable to get the DataSource.\n");
    }

    public void setLoginTimeout(int time) throws SQLException {
        if (this.dataSource != null) {
            this.dataSource.setLoginTimeout(time);
        }
        LOGGER.error("Unable to get the DataSource.\n");
    }

    public int getLoginTimeout() throws SQLException {
        if (this.dataSource != null) {
            return this.dataSource.getLoginTimeout();
        }
        LOGGER.error("Unable to get the DataSource.\n");
        return -1;
    }

    @Override
    public <T> T unwrap(Class<T> aClass) throws SQLException {
        if (this.dataSource != null) {
            return this.dataSource.unwrap(aClass);
        }
        LOGGER.error("Unable to get the DataSource.\n");
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        if (this.dataSource != null) {
            return this.dataSource.isWrapperFor(aClass);
        }
        LOGGER.error("Unable to get the DataSource.\n");
        return false;
    }

    @Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        if (this.dataSource != null) {
            return this.dataSource.getParentLogger();
        }
        LOGGER.error("Unable to get the DataSource.\n");
        return null;
    }

    @Override
    public DataSource getDataSource() {
        return this.dataSource;
    }

    @Override
    public Connection getConnection() {
        Connection con = super.getConnection();
        if (con == null) {
            try {
                con = getDataSource().getConnection();
            } catch (SQLException e) {
                throw new RuntimeException("Unable to get the connection from the DataSource.\n" + e.getLocalizedMessage());
            }
        }
        return con;
    }


    @Override
    public DBTypes getDataBaseType() {
        return databaseType;
    }

    @Override
    public IResultSetBuilder forwardOnly() {
        return new ResultSetBuilder(this).forwardOnly();
    }

    @Override
    public IResultSetBuilder scrollInsensitive() {
        return new ResultSetBuilder(this).scrollInsensitive();
    }

    @Override
    public IResultSetBuilder scrollSensitive() {
        return new ResultSetBuilder(this).scrollSensitive();
    }

    @Override
    public IResultSetBuilder readOnly() {
        return new ResultSetBuilder(this).readOnly();
    }

    @Override
    public IResultSetBuilder updatable() {
        return new ResultSetBuilder(this).updatable();
    }

    @Override
    public IResultSetBuilder holdCursorOverCommit() {
        return new ResultSetBuilder(this).holdCursorOverCommit();
    }

    @Override
    public IResultSetBuilder closeCursorAtCommit() {
        return new ResultSetBuilder(this).closeCursorAtCommit();
    }

    @Override
    public IResultSetBuilder fetchForward() {
        return new ResultSetBuilder(this).fetchForward();
    }

    @Override
    public IResultSetBuilder fetchReverse() {
        return new ResultSetBuilder(this).fetchReverse();
    }

    @Override
    public IResultSetBuilder fetchUnknown() {
        return new ResultSetBuilder(this).fetchUnknown();
    }

    @Override
    public IResultSetBuilder fetchSize(int size) {
        return new ResultSetBuilder(this).fetchSize(size);
    }

    @Override
    public IResultSetBuilder timeout(int time) {
        return new ResultSetBuilder(this).timeout(time);
    }

    @Override
    public IResultSetBuilder maxRow(int maxRow) {
        return new ResultSetBuilder(this).maxRow(maxRow);
    }

    @Override
    public IResultSetBuilder cursorName(String name) {
        return new ResultSetBuilder(this).cursorName(name);
    }

    @Override
    public IResultSetBuilder poolable() {
        return new ResultSetBuilder(this).poolable();
    }

    @Override
    public IResultSetBuilder maxFieldSize(int size) {
        return new ResultSetBuilder(this).maxFieldSize(size);
    }

    @Override
    public int[] executeBatch(String[] queries) throws SQLException {
        return new ResultSetBuilder(this).executeBatch(queries);
    }

    @Override
    public int[] executeBatch(GString[] queries) throws SQLException {
        return new ResultSetBuilder(this).executeBatch(queries);
    }

    @Override
    public long[] executeLargeBatch(String[] queries) throws SQLException {
        return new ResultSetBuilder(this).executeLargeBatch(queries);
    }

    @Override
    public long[] executeLargeBatch(GString[] queries) throws SQLException {
        return new ResultSetBuilder(this).executeLargeBatch(queries);
    }

    @Override
    public long executeLargeUpdate(String sql) throws SQLException {
        return new ResultSetBuilder(this).executeLargeUpdate(sql);
    }

    @Override
    public long executeLargeUpdate(GString sql) throws SQLException {
        return new ResultSetBuilder(this).executeLargeUpdate(sql);
    }

    @Override
    public GroovyRowResult firstRow(GString gstring) throws SQLException {
        GroovyRowResult row;
        try {
            row = super.firstRow(gstring);
            if (!getConnection().getAutoCommit()) {
                super.commit();
            }
            return row;
        } catch (SQLException e) {
            LOGGER.debug("Unable to execute the request as a GString.", e.getLocalizedMessage());
            try {
                if (!getConnection().getAutoCommit()) {
                    super.rollback();
                }
            } catch (SQLException e2) {
                LOGGER.error("Unable to rollback.", e2.getLocalizedMessage());
            }
        }
        row = super.firstRow(gstring.toString());
        if (!getConnection().getAutoCommit()) {
            super.commit();
        }
        return row;
    }

    @Override
    public boolean execute(GString gstring) throws SQLException {
        return execute(gstring.toString());
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        try {
            boolean b = super.execute(sql);
            if (!getConnection().getAutoCommit()) {
                super.commit();
            }
            return b;
        } catch (SQLException e) {
            try {
                if (!getConnection().getAutoCommit()) {
                    super.rollback();
                }
            } catch (SQLException e2) {
                LOGGER.error("Unable to rollback.", e2.getLocalizedMessage());
            }
            throw e;
        }
    }

    @Override
    public List<GroovyRowResult> rows(GString gstring) throws SQLException {
        List<GroovyRowResult> rows;
        try {
            rows = super.rows(gstring);
            if (!getConnection().getAutoCommit()) {
                super.commit();
            }
            return rows;
        } catch (SQLException e) {
            try {
                if (!getConnection().getAutoCommit()) {
                    super.rollback();
                }
            } catch (SQLException e2) {
                LOGGER.error("Unable to rollback.", e2.getLocalizedMessage());
            }
        }
        rows = super.rows(gstring.toString());
        if (!getConnection().getAutoCommit()) {
            super.commit();
        }
        return rows;
    }

    @Override
    public void eachRow(String sql,
                        @ClosureParams(value = SimpleType.class, options = "groovy.sql.GroovyResultSet") Closure closure)
            throws SQLException {
        try {
            super.eachRow(sql, closure);
            if (!getConnection().getAutoCommit()) {
                super.commit();
            }
        } catch (SQLException e) {
            LOGGER.debug("Unable to execute the request as a String.\n" + e.getLocalizedMessage());
            try {
                if (!getConnection().getAutoCommit()) {
                    super.rollback();
                }
            } catch (SQLException e2) {
                LOGGER.error("Unable to rollback.", e2.getLocalizedMessage());
            }
            super.eachRow(sql, closure);
        }
    }

    @Override
    public void eachRow(GString gstring,
                        @ClosureParams(value = SimpleType.class, options = "java.sql.ResultSet") Closure closure)
            throws SQLException {
        try {
            super.eachRow(gstring, closure);
            if (!getConnection().getAutoCommit()) {
                super.commit();
            }
        } catch (SQLException e) {
            LOGGER.debug("Unable to execute the request as a GString.\n" + e.getLocalizedMessage());

            try {
                if (!getConnection().getAutoCommit()) {
                    super.rollback();
                }
            } catch (SQLException e2) {
                LOGGER.error("Unable to rollback.", e2.getLocalizedMessage());
            }
            super.eachRow(gstring.toString(), closure);
        }
    }

    @Override
    public boolean executeScript(String fileName, Map<String, String> bindings) throws Exception {
        boolean b = false;
        try {
            File file = URIUtilities.fileFromString(fileName);
            if (FileUtilities.isExtensionWellFormated(file, "sql")) {
                b = executeScript(new FileInputStream(file), bindings);
                if (!getConnection().getAutoCommit()) {
                    super.commit();
                }
                return b;
            }
        } catch (IOException | SQLException e) {
            try {
                if (!getConnection().getAutoCommit()) {
                    super.rollback();
                }
            } catch (SQLException e2) {
                throw new SQLException("Unable to rollback.", e2);
            }
            throw e;
        }
        return b;
    }

    @Override
    public boolean executeScript(InputStream stream, Map<String, String> bindings) throws Exception {
        SimpleTemplateEngine engine = null;
        if (bindings != null && !bindings.isEmpty()) {
            engine = new SimpleTemplateEngine();
        }
        ScriptReader scriptReader = new ScriptReader(new InputStreamReader(stream));
        scriptReader.setSkipRemarks(true);
        while (true) {
            String commandSQL = scriptReader.readStatement();
            if (commandSQL == null) {
                break;
            }
            if (!commandSQL.isEmpty()) {
                if (engine != null) {
                    try {
                        commandSQL = engine.createTemplate(commandSQL).make(bindings).toString();
                    } catch (ClassNotFoundException | IOException e) {
                        throw new IllegalArgumentException("Unable to create the template for the Sql command '" + commandSQL + "'.\n" +
                                e.getLocalizedMessage());
                    }
                }
                try {
                    execute(commandSQL);
                    if (!getConnection().getAutoCommit()) {
                        super.commit();
                    }
                } catch (SQLException e) {
                    throw new SQLException("Unable to execute the Sql command '" + commandSQL + "'.\n" + e.getLocalizedMessage());
                }
            }
        }
        return true;
    }

    @Override
    public MetaClass getMetaClass() {
        return metaClass;
    }

    @Override
    public void setMetaClass(MetaClass metaClass) {
        this.metaClass = metaClass;
    }

    @Override
    public boolean save(String tableName, String filePath) throws Exception {
        return save(tableName, filePath, null);
    }

    @Override
    public boolean save(String tableName, String filePath, boolean delete) throws Exception {
        if (getConnection() == null) {
            throw new IllegalArgumentException("No connection, cannot save.");
        }
            if (ioMethods == null) {
                ioMethods = new IOMethods();
            }
            ioMethods.exportToFile(getConnection(), tableName, filePath, null, delete);
            return true;
    }

    @Override
    public boolean save(String tableName, String filePath, String encoding) throws Exception {
        if (getConnection() == null) {
            throw new IllegalArgumentException("No connection, cannot save.");
        }
        if (ioMethods == null) {
            ioMethods = new IOMethods();
        }
        ioMethods.exportToFile(getConnection(), tableName, filePath, encoding, false);
        return true;
    }

    @Override
    public boolean save(String tableName, URL url) throws Exception {
        return save(tableName, url, null);
    }

    @Override
    public boolean save(String tableName, URL url, String encoding) throws Exception {
            return save(tableName, url.toURI(), encoding);
    }

    @Override
    public boolean save(String tableName, URI uri) throws Exception {
        return save(tableName, uri, null);
    }

    @Override
    public boolean save(String tableName, URI uri, String encoding) throws Exception {
        return save(tableName, new File(uri), encoding);
    }

    @Override
    public boolean save(String tableName, File file) throws Exception {
        return save(tableName, file, null);
    }

    @Override
    public boolean save(String tableName, File file, String encoding) throws Exception {
        return save(tableName, file.getAbsolutePath(), encoding);
    }

    private String getTableNameFromPath(String filePath) {
        String name = URIUtilities.fileFromString(filePath).getName();
        if (name.contains(".")) {
            name = name.substring(0, name.lastIndexOf("."));
        }
        if (databaseType == DBTypes.H2GIS) {
            return name.toUpperCase();
        }
        return name.toLowerCase();
    }

    @Override
    public String link(String filePath, String tableName, boolean delete) throws Exception {
        String formatedTableName = TableLocation.parse(tableName, getDataBaseType()).toString();
            IOMethods.linkedFile(getConnection(), filePath, tableName, delete);
            return formatedTableName;
    }

    @Override
    public String link(String filePath, String tableName) throws Exception {
        return link(filePath, tableName, false);
    }

    @Override
    public String link(String filePath, boolean delete) throws Exception {
        String tableName = getTableNameFromPath(filePath);
        if (Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]*$").matcher(tableName).find()) {
            return link(filePath, tableName, delete);
        } else {
            throw new IllegalArgumentException("The file name contains unsupported characters");
        }
    }

    @Override
    public String link(String filePath) throws Exception {
        return link(filePath, false);
    }

    @Override
    public String link(URL url, String tableName, boolean delete) throws Exception {
            return link(url.toURI(), tableName, delete);
    }

    @Override
    public String link(URL url, String tableName) throws Exception {
            return link(url.toURI(), tableName);
    }

    @Override
    public String link(URL url, boolean delete) throws Exception {
            return link(url.toURI(), delete);
    }

    @Override
    public String link(URL url) throws Exception {
            return link(url.toURI());
    }

    @Override
    public String link(URI uri, String tableName, boolean delete) throws Exception {
        return link(new File(uri), tableName, delete);
    }

    @Override
    public String link(URI uri, String tableName) throws Exception {
        return link(new File(uri), tableName);
    }

    @Override
    public String link(URI uri, boolean delete) throws Exception {
        return link(new File(uri), delete);
    }

    @Override
    public String link(URI uri) throws Exception {
        return link(new File(uri));
    }

    @Override
    public String link(File file, String tableName, boolean delete) throws Exception {
        return link(file.getAbsolutePath(), tableName, delete);
    }

    @Override
    public String link(File file, String tableName) throws Exception {
        return link(file.getAbsolutePath(), tableName);
    }

    @Override
    public String link(File file, boolean delete) throws Exception {
        return link(file.getAbsolutePath(), delete);
    }

    @Override
    public String link(File file) throws Exception {
        return link(file.getAbsolutePath());
    }

    @Override
    public String load(String filePath, String tableName, String encoding,
                       boolean delete) throws Exception {
        String formatedTableName = TableLocation.parse(tableName, getDataBaseType()).toString();

            if (ioMethods == null) {
                ioMethods = new IOMethods();
            }
            ioMethods.importFile(getConnection(), filePath, tableName, encoding, delete);
            return formatedTableName;
    }

    @Override
    public String load(String filePath, String tableName) throws Exception {
        return load(filePath, tableName, null, false);
    }

    @Override
    public String load(String filePath, String tableName, boolean delete) throws Exception {
        return load(filePath, tableName, null, delete);
    }

    @Override
    public String load(String filePath) throws Exception {
        return load(filePath, false);
    }

    @Override
    public String load(String filePath, boolean delete) throws Exception {
        String tableName = getTableNameFromPath(filePath).replace(".", "_");
        if (Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]*$").matcher(tableName).find()) {
            return load(filePath, tableName, null, delete);
        } else {
            LOGGER.error("Unsupported file characters");
        }
        return null;
    }

    @Override
    public String load(URL url, String tableName) throws Exception {
            return load(url.toURI(), tableName, null, false);
    }

    @Override
    public String load(URL url, String tableName, boolean delete) throws Exception {
            return load(url.toURI(), tableName, null, delete);

    }

    @Override
    public String load(URL url) throws Exception {
            return load(url.toURI(), false);
    }

    @Override
    public String load(URL url, boolean delete) throws Exception {
            return load(url.toURI(), delete);
    }

    @Override
    public String load(URL url, String tableName, String encoding, boolean delete) throws Exception {
            return load(url.toURI(), tableName, encoding, delete);

    }

    @Override
    public String load(URI uri, String tableName) throws Exception {
        return load(new File(uri), tableName, null, false);
    }

    @Override
    public String load(URI uri, String tableName, boolean delete) throws Exception {
        return load(new File(uri), tableName, null, delete);
    }

    @Override
    public String load(URI uri) throws Exception {
        return load(new File(uri), false);
    }

    @Override
    public String load(URI uri, boolean delete) throws Exception {
        return load(new File(uri), delete);
    }

    @Override
    public String load(URI uri, String tableName, String encoding, boolean delete) throws Exception {
        return load(new File(uri), tableName, encoding, delete);
    }

    @Override
    public String load(File file, String tableName) throws Exception {
        return load(file.getAbsolutePath(), tableName, null, false);
    }

    @Override
    public String load(File file, String tableName, boolean delete) throws Exception {
        return load(file.getAbsolutePath(), tableName, null, delete);
    }

    @Override
    public String load(File file) throws Exception {
        return load(file.getAbsolutePath(), false);
    }

    @Override
    public String load(File file, boolean delete) throws Exception {
        return load(file.getAbsolutePath(), delete);
    }

    @Override
    public String load(File file, String tableName, String encoding, boolean delete) throws Exception {
        return load(file.getAbsolutePath(), tableName, encoding, delete);
    }

    @Override
    public String load(IJdbcDataSource dataSource, String inputTableName, String outputTableName, boolean deleteIfExists) throws Exception {
        return load(dataSource, inputTableName, outputTableName, deleteIfExists, 1000);
    }

    @Override
    public String load(IJdbcDataSource dataSource, String inputTableName, String outputTableName) throws Exception {
        return load(dataSource, inputTableName, outputTableName, false, 1000);
    }

    @Override
    public String load(IJdbcDataSource dataSource, String inputTableName, boolean deleteIfExists) throws Exception {
             IOMethods.exportToDataBase(dataSource.getConnection(), inputTableName, getConnection(), inputTableName, deleteIfExists ? -1 : 0, 1000);
            TableLocation targetTableLocation = TableLocation.parse(inputTableName, this.getDataBaseType());
            return targetTableLocation.toString();

    }

    @Override
    public String load(IJdbcDataSource dataSource, String inputTableName) throws Exception {

            return IOMethods.exportToDataBase(dataSource.getConnection(), inputTableName, getConnection(), inputTableName, 0, 1000);

    }

    @Override
    public String load(IJdbcDataSource dataSource, String inputTableName, String outputTableName, boolean deleteIfExists, int batchSize) throws Exception {
        return IOMethods.exportToDataBase(dataSource.getConnection(), inputTableName, getConnection(), outputTableName, deleteIfExists ? -1 : 0, 1000);
    }

    @Override
    public IDataSourceLocation getLocation() {
        try {
            Connection con = getConnection();
            if (con == null) {
                LOGGER.error("Unable to get the connection.");
                return null;
            }
            String url = con.getMetaData().getURL();
            return url == null ? null : new DataSourceLocation(url.substring(url.lastIndexOf(":") + 1));
        } catch (SQLException e) {
            LOGGER.error("Unable to get the connection metadata.\n" + e.getLocalizedMessage());
        }
        return null;
    }

    /**
     * Return the IOMethods used to load and save data
     *
     * @return
     */
    public IOMethods getIoMethods() {
        if (ioMethods == null) {
            ioMethods = new IOMethods();
        }
        return ioMethods;
    }

    @Override
    public Collection<String> getTableNames() throws Exception {
            return JDBCUtilities.getTableNames(getConnection(), null, null, null, null);

    }

    @Override
    public IJdbcTable getDataSet(String dataSetName) throws Exception {
        return getTable(dataSetName);
    }

    @Override
    public int call(GString gstring) throws SQLException {
        return super.call(gstring.toString());
    }

    @Override
    public List<Object> getParameters(GString gString) {
        return super.getParameters(gString);
    }

    @Override
    public String asSql(GString gString, List<Object> params) {
        return super.asSql(gString, params);
    }

    /**
     * Set the given {@link PreparedStatement} with the given parameters.
     *
     * @param preparedStatement {@link PreparedStatement} to set.
     * @param params            List of the parameters.
     * @throws SQLException Exception thrown when problem occurs on setting the parameters.
     */
    public void setStatementParameters(PreparedStatement preparedStatement, List<Object> params) throws SQLException {
        for (int i = 1; i <= params.size(); i++) {
            Object param = params.get(i - 1);
            if (param instanceof Array) {
                preparedStatement.setArray(i, (Array) param);
            }
            /*else if(param instanceof InputStream) {
                preparedStatement.setAsciiStream(i, (InputStream) param);
            }*/
            else if (param instanceof BigDecimal) {
                preparedStatement.setBigDecimal(i, (BigDecimal) param);
            }
            /*else if(param instanceof InputStream) {
                preparedStatement.set(i, (InputStream) param);
            }*/
            else if (param instanceof Blob) {
                preparedStatement.setBlob(i, (Blob) param);
            }
            /*else if(param instanceof InputStream) {
                preparedStatement.setBinaryStream(i, (InputStream) param);
            }*/
            else if (param instanceof Boolean) {
                preparedStatement.setBoolean(i, (Boolean) param);
            } else if (param instanceof Byte) {
                preparedStatement.setByte(i, (Byte) param);
            } else if (param instanceof byte[]) {
                preparedStatement.setBytes(i, (byte[]) param);
            }
            /*else if(param instanceof Reader) {
                preparedStatement.setCharacterStream(i, (Reader) param);
            }*/
            else if (param instanceof Clob) {
                preparedStatement.setClob(i, (Clob) param);
            } else if (param instanceof Date) {
                preparedStatement.setDate(i, (Date) param);
            } else if (param instanceof Double) {
                preparedStatement.setDouble(i, (Double) param);
            } else if (param instanceof Float) {
                preparedStatement.setFloat(i, (Float) param);
            } else if (param instanceof Integer) {
                preparedStatement.setInt(i, (Integer) param);
            } else if (param instanceof Long) {
                preparedStatement.setLong(i, (Long) param);
            }
            /*else if(param instanceof Reader) {
                preparedStatement.setNCharacterStream(i, (Reader) param);
            }*/
            else if (param instanceof NClob) {
                preparedStatement.setNClob(i, (NClob) param);
            } else if (param instanceof Ref) {
                preparedStatement.setRef(i, (Ref) param);
            } else if (param instanceof Short) {
                preparedStatement.setShort(i, (Short) param);
            } else if (param instanceof SQLXML) {
                preparedStatement.setSQLXML(i, (SQLXML) param);
            } else if (param instanceof String) {
                preparedStatement.setString(i, (String) param);
            } else if (param instanceof Time) {
                preparedStatement.setTime(i, (Time) param);
            } else if (param instanceof Timestamp) {
                preparedStatement.setTimestamp(i, (Timestamp) param);
            } else if (param instanceof URL) {
                preparedStatement.setURL(i, (URL) param);
            } else {
                preparedStatement.setObject(i, param);
            }
        }
    }

    @Override
    public JdbcDataSource autoCommit(boolean autoCommit) {
        try {
            Connection con = getConnection();
            if (con != null) {
                con.setAutoCommit(autoCommit);
                return this;
            } else {
                LOGGER.error("Unable to get the connection.");
            }
        } catch (SQLException e) {
            LOGGER.error("Unable to set the auto-commit mode.\n" + e.getLocalizedMessage());
        }
        return this;
    }

    @Override
    public Class<?> typeNameToClass(String typeName) {
        return TYPE_NAME_TO_CLASS.get(typeName);
    }

    @Override
    public boolean createSpatialIndex(String tableName, String columnName) {
        if (columnName == null || tableName == null) {
            LOGGER.error("Unable to create a spatial index");
            return false;
        }
        try {
            return JDBCUtilities.createSpatialIndex(getConnection(), TableLocation.parse(tableName, getDataBaseType()), columnName);
        } catch (SQLException e) {
            LOGGER.error("Unable to create a spatial index on the column '" + columnName + "' in the table '" + tableName + "'.\n" +
                    e.getLocalizedMessage());
        }
        return false;
    }

    @Override
    public boolean createSpatialIndex(String tableName) {
        if (tableName == null) {
            LOGGER.error("Unable to create a spatial index");
            return false;
        }
        try {
            TableLocation table = TableLocation.parse(tableName, getDataBaseType());
            String geomColumn = GeometryTableUtilities.getFirstGeometryColumnNameAndIndex(getConnection(), table).first();
            if (geomColumn == null || geomColumn.isEmpty()) {
                return false;
            }
            return JDBCUtilities.createSpatialIndex(getConnection(), table, geomColumn);
        } catch (SQLException e) {
            LOGGER.error("Unable to create a spatial index on the table '" + tableName + "'.\n" +
                    e.getLocalizedMessage());
        }
        return false;
    }

    @Override
    public boolean createIndex(String tableName, String columnName) {
        if (columnName == null || tableName == null) {
            LOGGER.error("Unable to create an index");
            return false;
        }
        try {
            return JDBCUtilities.createIndex(getConnection(), TableLocation.parse(tableName, getDataBaseType()), columnName);
        } catch (SQLException e) {
            LOGGER.error("Unable to create an index on the column '" + columnName + "' in the table '" + tableName + "'.\n" +
                    e.getLocalizedMessage());
        }
        return false;
    }

    @Override
    public boolean hasGeometryColumn(String tableName) {
        if (tableName == null) {
            LOGGER.error("Unable to get the table");
            return false;
        }
        try {
            return GeometryTableUtilities.hasGeometryColumn(getConnection(), TableLocation.parse(tableName, getDataBaseType()));
        } catch (SQLException e) {
            LOGGER.error("Unable to get the table.\n" +
                    e.getLocalizedMessage());
        }
        return false;
    }

    @Override
    public List<String> getGeometryColumns(String tableName) {
        if (tableName == null) {
            LOGGER.error("Unable to get the table");
            return null;
        }
        try {
            return GeometryTableUtilities.getGeometryColumnNames(getConnection(), TableLocation.parse(tableName, getDataBaseType()));
        } catch (SQLException e) {
            LOGGER.error("Unable to get the table.\n" +
                    e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public String getGeometryColumn(String tableName) {
        if (tableName == null) {
            LOGGER.error("Unable to get the table");
            return null;
        }
        try {
            return GeometryTableUtilities.getFirstGeometryColumnNameAndIndex(getConnection(), TableLocation.parse(tableName, getDataBaseType())).first();
        } catch (SQLException e) {
            LOGGER.error("Unable to get the table.\n" +
                    e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public boolean isIndexed(String tableName, String columnName) {
        try {
            return JDBCUtilities.isIndexed(getConnection(), TableLocation.parse(tableName, getDataBaseType()), columnName);
        } catch (SQLException e) {
            LOGGER.error("Unable to check if the column '" + columnName + "' from the table '" + tableName + "' is indexed.\n" +
                    e.getLocalizedMessage());
        }
        return false;
    }

    @Override
    public boolean isSpatialIndexed(String tableName, String columnName) throws Exception{
        return JDBCUtilities.isSpatialIndexed(getConnection(), TableLocation.parse(tableName, getDataBaseType()), columnName);
    }

    @Override
    public boolean isSpatialIndexed(String tableName) throws Exception{
            TableLocation table = TableLocation.parse(tableName, getDataBaseType());
            String geomColumn = GeometryTableUtilities.getFirstGeometryColumnNameAndIndex(getConnection(), table).first();
            if (geomColumn == null || geomColumn.isEmpty()) {
                return false;
            }
            return JDBCUtilities.isSpatialIndexed(getConnection(), tableName, geomColumn);
    }

    @Override
    public void dropIndex(String tableName, String columnName) throws Exception{
        if (columnName != null || tableName != null) {
            JDBCUtilities.dropIndex(getConnection(), TableLocation.parse(tableName, getDataBaseType()), columnName);
        }
    }

    @Override
    public void dropTable(String... tableName) throws Exception{
        if (tableName != null) {
            String query = Stream.of(tableName).filter(s -> s != null && !s.isEmpty())
                    .collect(Collectors.joining(" , "));
            try {
                if (!query.isEmpty()) {
                    execute("DROP TABLE IF EXISTS " + query);
                }
            } catch (SQLException e) {
                throw new SQLException("Unable to drop the tables '" + query + "'." , e);
            }
        }
    }

    @Override
    public void dropTable(List<String> tableNames) throws Exception{
        if (tableNames != null) {
            String query = tableNames.stream().filter(s -> s != null && !s.isEmpty())
                    .collect(Collectors.joining(","));
            try {
                if (!query.isEmpty()) {
                    execute("DROP TABLE IF EXISTS " + query);
                }
            } catch (SQLException e) {
                throw new SQLException("Unable to drop the tables '" + query + "'." ,
                        e);
            }
        }
    }
    @Override
    public boolean setSrid(String tableName, String columnName, int srid) throws Exception{
       return GeometryTableUtilities.alterSRID(getConnection(), TableLocation.parse(tableName, getDataBaseType()), columnName, srid);
   }

    @Override
    public int getSrid(String tableName) throws Exception{
        if (tableName == null) {
            throw new IllegalArgumentException("Unable to get the srid");
        }
        return GeometryTableUtilities.getSRID(getConnection(), TableLocation.parse(tableName, getDataBaseType()));
    }

    @Override
    public int getSrid(String tableName, String columnName) throws Exception{
        if (tableName == null) {
            throw new IllegalArgumentException("Unable to get the srid");
        }
        return GeometryTableUtilities.getSRID(getConnection(), TableLocation.parse(tableName, getDataBaseType()), columnName);
    }

    @Override
    public boolean setSrid(String tableName, int srid) throws Exception{
            String geomColumn = GeometryTableUtilities.getFirstGeometryColumnNameAndIndex(getConnection(), TableLocation.parse(tableName, getDataBaseType())).first();
            if (geomColumn == null || geomColumn.isEmpty()) {
                throw new IllegalArgumentException("Unable to get the srid");
            }
            return GeometryTableUtilities.alterSRID(getConnection(), tableName, geomColumn, srid);
    }


    @Override
    public boolean isEmpty(String tableName) throws Exception{
        try {
            GroovyRowResult row = firstRow("SELECT * FROM " + tableName + " LIMIT 1 ");
            if (row == null) {
                return true;
            }
            return row.isEmpty();
        } catch (SQLException e) {
            throw new SQLException("Unable to check if the table is empty.", e);
        }
    }

    @Override
    public void print(String tableName) throws Exception{
        print(tableName, 1000);
    }

    @Override
    public void print(String tableName, int numberOfRows) throws Exception{
        if (tableName == null || tableName.isEmpty()) {
            System.out.println("The table name is null or empty.");
        }
        Connection con = getConnection();
        if (con == null) {
            System.out.println("Unable to get the connection to the database");
        }
        try (Statement statement = con.createStatement()) {
            ResultSet rs = statement.executeQuery("SELECT * FROM " + tableName + " as foo");
            StringBuilder builder = new StringBuilder();
            Ascii printer = new Ascii(builder);
            List<String> columnNames = JDBCUtilities.getColumnNames(rs.getMetaData());
            if (columnNames == null) {
                printer.endTable();
                System.out.print(printer);
                return;
            }
            printer.startTable(20, columnNames.size());
            printer.appendTableTitle(tableName);
            printer.appendTableLineSeparator();
            for (String column : columnNames) {
                printer.appendTableHeaderValue(column, CENTER);
            }
            printer.appendTableLineSeparator();
            boolean tooManyRows = false;
            if (rs != null) {
                try {
                    int count = 0;
                    while (rs.next()) {
                        if (count > numberOfRows) {
                            tooManyRows = true;
                            break;
                        }
                        for (String column : columnNames) {
                            Object obj = rs.getObject(column);
                            if (obj instanceof Number) {
                                printer.appendTableValue(rs.getObject(column), RIGHT);
                            } else {
                                printer.appendTableValue(rs.getObject(column), LEFT);
                            }
                        }
                        count++;
                    }
                } catch (Exception e) {
                    throw new SQLException("Error while reading the table '" + tableName + "'.\n" + e.getLocalizedMessage());
                }
            }
            if (tooManyRows && numberOfRows>1) {
                printer.appendText("Note that the table contains more than " + numberOfRows + " rows");
            } else {
                printer.appendTableLineSeparator();
            }
            printer.endTable();
            System.out.println(printer);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, String> getColumnNamesTypes(String location) throws Exception {
        HashMap<String, String> fieldNameList = new HashMap<>();
        final Statement statement = getConnection().createStatement();
        try {
            final ResultSet resultSet = statement.executeQuery(
                    "SELECT * FROM " + location + " LIMIT 0;");
            try {
                ResultSetMetaData metadata = resultSet.getMetaData();
                int columnCount = metadata.getColumnCount();
                for (int columnId = 1; columnId <= columnCount; columnId++) {
                    fieldNameList.put(metadata.getColumnName(columnId), metadata.getColumnTypeName(columnId));
                }
            } finally {
                resultSet.close();
            }
        } finally {
            statement.close();
        }
        return fieldNameList;
    }

    @Override
    public Map<String, Class> getColumnNamesClasses(String location) throws Exception {
        HashMap<String, Class> columnsWithClass = new HashMap<>();
        Connection con = getConnection();
        final Statement statement = con.createStatement();
        try {
            final ResultSet resultSet = statement.executeQuery(
                    "SELECT * FROM " + location + " LIMIT 0;");
            try {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                for (int columnId = 1; columnId <= columnCount; columnId++) {
                    String columnName = metaData.getColumnName(columnId);
                    //Take into account the geometry type
                    String type = metaData.getColumnTypeName(columnId);
                    if(type.toLowerCase().startsWith("geometry")){
                            columnsWithClass.put(columnName, typeNameToClass(GeometryTableUtilities.getMetaData(con,
                                    location,
                                    TableLocation.capsIdentifier(columnName, getDataBaseType())
                            ).getGeometryType()));

                    }else{
                        columnsWithClass.put(columnName,  typeNameToClass(type));
                    }
                }
            } finally {
                resultSet.close();
            }
        } finally {
            statement.close();
        }
        return columnsWithClass;
    }
}
