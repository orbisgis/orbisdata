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
import org.h2gis.utilities.TableLocation;
import org.h2gis.utilities.*;
import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.commons.annotations.Nullable;
import org.orbisgis.orbisdata.datamanager.api.dataset.DataBaseType;
import org.orbisgis.orbisdata.datamanager.api.dataset.IJdbcTable;
import org.orbisgis.orbisdata.datamanager.api.datasource.IDataSourceLocation;
import org.orbisgis.orbisdata.datamanager.api.datasource.IJdbcDataSource;
import org.orbisgis.orbisdata.datamanager.api.dsl.IResultSetBuilder;
import org.orbisgis.orbisdata.datamanager.jdbc.dsl.ResultSetBuilder;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract class used to implements the request builder methods (select, from ...) in order to give a base to all the
 * JdbcDataSource implementations.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC 2019 / Chaire GEOTERA 2020)
 */
public abstract class JdbcDataSource extends Sql implements IJdbcDataSource, IResultSetBuilder {

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
    private final DataBaseType databaseType;
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
    public JdbcDataSource(@NotNull Sql parent, @NotNull DataBaseType databaseType) {
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
    public JdbcDataSource(@NotNull DataSource dataSource, @NotNull DataBaseType databaseType) {
        super(dataSource);
        this.dataSource = dataSource;
        this.metaClass = InvokerHelper.getMetaClass(getClass());
        this.databaseType = databaseType;
        LOG.setLevel(Level.OFF);
        try {
            getConnection().setAutoCommit(false);
        } catch (SQLException e) {
            LOGGER.warn("Unable to set the autocommit option of the connection to false.", e);
        }
    }

    /**
     * Constructor to create a {@link JdbcDataSource} from a {@link Connection} object.
     *
     * @param connection   Parent {@link Sql} object.
     * @param databaseType Type of the database
     */
    public JdbcDataSource(@NotNull Connection connection, @NotNull DataBaseType databaseType) {
        super(connection);
        this.dataSource = null;
        this.metaClass = InvokerHelper.getMetaClass(getClass());
        this.databaseType = databaseType;
        LOG.setLevel(Level.OFF);
        try {
            getConnection().setAutoCommit(false);
        } catch (SQLException e) {
            LOGGER.warn("Unable to set the autocommit option of the connection to false.", e);
        }
    }

    @Override
    @NotNull
    public Collection<String> getTableNames(@Nullable String namePattern){
        return getTableNames(null, null, namePattern, (TableType[]) null);
    }

    @Override
    @NotNull
    public Collection<String> getTableNames(@Nullable String namePattern, @Nullable TableType... types){
        return getTableNames(null, null, namePattern, types);
    }

    @Override
    @NotNull
    public Collection<String> getTableNames(@Nullable String schemaPattern, @Nullable String namePattern){
        return getTableNames(null, schemaPattern, namePattern, (TableType[]) null);
    }

    @Override
    @NotNull
    public Collection<String> getTableNames(@Nullable String schemaPattern, @Nullable String namePattern,
                                     @Nullable TableType... types){
        return getTableNames(null, schemaPattern, namePattern, types);
    }

    @Override
    @NotNull
    public Collection<String> getTableNames(@Nullable String catalogPattern, @Nullable String schemaPattern,
                                     @Nullable String namePattern){
        return getTableNames(catalogPattern, schemaPattern, namePattern, (TableType[]) null);
    }

    @Override
    @NotNull
    public Collection<String> getTableNames(@Nullable String catalogPattern, @Nullable String schemaPattern,
                                     @Nullable String namePattern, @Nullable TableType... types){
        String[] array = null;
        if(types != null){
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
    @Nullable
    public Connection getConnection(@NotNull String var1, @NotNull String var2) throws SQLException {
        if (this.dataSource != null) {
            return this.dataSource.getConnection(var1, var2);
        }
        LOGGER.error("Unable to get the DataSource.\n");
        return null;
    }

    @Nullable
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
    @Nullable
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
    @Nullable
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
    @Nullable
    public Connection getConnection() {
        Connection con = super.getConnection();
        if (con == null) {
            try {
                con = getDataSource().getConnection();
            } catch (SQLException e) {
                LOGGER.error("Unable to get the connection from the DataSource.\n" + e.getLocalizedMessage());
            }
        }
        return con;
    }

    @Override
    @NotNull
    public DataBaseType getDataBaseType() {
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
    public boolean execute(GString gstring) throws SQLException {
        boolean b;
        try {
            b = super.execute(gstring);
            if(!getConnection().getAutoCommit()){
                super.commit();
            }
            return b;
        } catch (SQLException e) {
            LOGGER.debug("Unable to execute the request as a GString.\n" + e.getLocalizedMessage());
            try {
                if(!getConnection().getAutoCommit()){
                    super.rollback();
                }
            } catch (SQLException e2) {
                LOGGER.error("Unable to rollback.", e2.getLocalizedMessage());
            }
        }
        b = super.execute(gstring.toString());
        if(!getConnection().getAutoCommit()){
            super.commit();
        }
        return b;
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
            if(!getConnection().getAutoCommit()){
                super.commit();
            }
            return row;
        } catch (SQLException e) {
            LOGGER.debug("Unable to execute the request as a GString.", e.getLocalizedMessage());
            try {
                if(!getConnection().getAutoCommit()){
                    super.rollback();
                }
            } catch (SQLException e2) {
                LOGGER.error("Unable to rollback.", e2.getLocalizedMessage());
            }
        }
        row = super.firstRow(gstring.toString());
        if(!getConnection().getAutoCommit()){
            super.commit();
        }
        return row;
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        try {
            boolean b = super.execute(sql);
            if(!getConnection().getAutoCommit()){
                super.commit();
            }
            return b;
        } catch (SQLException e) {
            try {
                if(!getConnection().getAutoCommit()){
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
            if(!getConnection().getAutoCommit()){
                super.commit();
            }
            return rows;
        } catch (SQLException e) {
            LOGGER.debug("Unable to execute the request as a GString.\n" + e.getLocalizedMessage());
            try {
                if(!getConnection().getAutoCommit()){
                    super.rollback();
                }
            } catch (SQLException e2) {
                LOGGER.error("Unable to rollback.", e2.getLocalizedMessage());
            }
        }
        rows = super.rows(gstring.toString());
        if(!getConnection().getAutoCommit()){
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
            if(!getConnection().getAutoCommit()){
                super.commit();
            }
        } catch (SQLException e) {
            LOGGER.debug("Unable to execute the request as a String.\n" + e.getLocalizedMessage());

            try {
                if(!getConnection().getAutoCommit()){
                    super.rollback();
                }
            } catch (SQLException e2) {
                LOGGER.error("Unable to rollback.", e2.getLocalizedMessage());
            }
            super.eachRow(sql.toString(), closure);
        }
    }

    @Override
    public void eachRow(GString gstring,
                        @ClosureParams(value = SimpleType.class, options = "java.sql.ResultSet") Closure closure)
            throws SQLException {
        try {
            super.eachRow(gstring, closure);
            if(!getConnection().getAutoCommit()){
                super.commit();
            }
        } catch (SQLException e) {
            LOGGER.debug("Unable to execute the request as a GString.\n" + e.getLocalizedMessage());

            try {
                if(!getConnection().getAutoCommit()){
                    super.rollback();
                }
            } catch (SQLException e2) {
                LOGGER.error("Unable to rollback.", e2.getLocalizedMessage());
            }
            super.eachRow(gstring.toString(), closure);
        }
    }

    @Override
    public boolean executeScript(@NotNull String fileName, Map<String, String> bindings) {
        File file = URIUtilities.fileFromString(fileName);
        boolean b = false;
        try {
            if (FileUtilities.isExtensionWellFormated(file, "sql")) {
                b = executeScript(new FileInputStream(file), bindings);
                if(!getConnection().getAutoCommit()){
                    super.commit();
                }
                return b;
            }
        } catch (IOException | SQLException e) {
            LOGGER.error("Unable to read the SQL file.", e.getLocalizedMessage());
            try {
                if(!getConnection().getAutoCommit()){
                    super.rollback();
                }
            } catch (SQLException e2) {
                LOGGER.error("Unable to rollback.", e2.getLocalizedMessage());
            }
        }
        return b;
    }

    @Override
    public boolean executeScript(@NotNull InputStream stream, Map<String, String> bindings) {
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
                        LOGGER.error("Unable to create the template for the Sql command '" + commandSQL + "'.\n" +
                                e.getLocalizedMessage());
                        return false;
                    }
                }
                try {
                    execute(commandSQL);
                    if(!getConnection().getAutoCommit()){
                        super.commit();
                    }
                } catch (SQLException e) {
                    LOGGER.error("Unable to execute the Sql command '" + commandSQL + "'.\n" + e.getLocalizedMessage());
                    return false;
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
    public boolean save(@NotNull String tableName, @NotNull String filePath) {
        return save(tableName, filePath, null);
    }

    @Override
    public boolean save(String tableName, String filePath, boolean delete) {
        if(getConnection() == null){
            LOGGER.error("No connection, cannot save.");
            return false;
        }
        try {
            if(ioMethods==null) {
                ioMethods = new IOMethods();
            }
            ioMethods.exportToFile(getConnection(), tableName,filePath, null, delete);
            return true;
        } catch (SQLException e) {
            LOGGER.error("Cannot import the file : "+ filePath);
        }
        return false;
    }

    @Override
    public boolean save(@NotNull String tableName, @NotNull String filePath, @Nullable String encoding) {
        if(getConnection() == null){
            LOGGER.error("No connection, cannot save.");
            return false;
        }
        try {
            if(ioMethods==null) {
                ioMethods = new IOMethods();
            }
            ioMethods.exportToFile(getConnection(), tableName,filePath, encoding, false);
            return true;
        } catch (SQLException e) {
            LOGGER.error("Cannot import the file : "+ filePath);
        }
        return false;
    }

    @Override
    public boolean save(@NotNull String tableName, @NotNull URL url) {
        return save(tableName, url, null);
    }

    @Override
    public boolean save(@NotNull String tableName, @NotNull URL url, @Nullable String encoding) {
        try {
            return save(tableName, url.toURI(), encoding);
        } catch (URISyntaxException e) {
            LOGGER.error("Unable to get the file from the URL '" + url.toString() + "'\n" + e.getLocalizedMessage());
        }
        return false;
    }

    @Override
    public boolean save(@NotNull String tableName, @NotNull URI uri) {
        return save(tableName, uri, null);
    }

    @Override
    public boolean save(@NotNull String tableName, @NotNull URI uri, @Nullable String encoding) {
        return save(tableName, new File(uri), encoding);
    }

    @Override
    public boolean save(@NotNull String tableName, @NotNull File file) {
        return save(tableName, file, null);
    }

    @Override
    public boolean save(@NotNull String tableName, @NotNull File file, @Nullable String encoding) {
        return save(tableName, file.getAbsolutePath(), encoding);
    }

    private String getTableNameFromPath(String filePath) {
        String name = URIUtilities.fileFromString(filePath).getName();
        if(name.contains(".")) {
            name = name.substring(0, name.lastIndexOf("."));
        }
        if(databaseType == DataBaseType.H2GIS){
            return name.toUpperCase();
        }
        return name.toLowerCase();
    }

    @Override
    public String link(@NotNull String filePath, @NotNull String tableName, boolean delete) {
        String formatedTableName = TableLocation.parse(tableName, getDataBaseType() == DataBaseType.H2GIS).toString(getDataBaseType() == DataBaseType.H2GIS);
        try {
            IOMethods.linkedFile(getConnection(), filePath, tableName, delete);
            return formatedTableName;
        } catch (SQLException e) {
            LOGGER.error("Cannot link the file : "+ filePath);
        }
        return formatedTableName;
    }

    @Override
    public String link(@NotNull String filePath, @NotNull String tableName) {
        return link(filePath, tableName, false);
    }

    @Override
    public String link(@NotNull String filePath, boolean delete) {
        String tableName = getTableNameFromPath(filePath);
        if (Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]*$").matcher(tableName).find()) {
            return link(filePath, tableName, delete);
        } else {
            LOGGER.error("The file name contains unsupported characters");
        }
        return null;
    }

    @Override
    public String link(@NotNull String filePath) {
        return link(filePath, false);
    }

    @Override
    public String link(@NotNull URL url, String tableName, boolean delete) {
        try {
            return link(url.toURI(), tableName, delete);
        } catch (URISyntaxException e) {
            LOGGER.error("Unable to get the file from the URL '" + url.toString() + "'\n" + e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public String link(@NotNull URL url, String tableName) {
        try {
            return link(url.toURI(), tableName);
        } catch (URISyntaxException e) {
            LOGGER.error("Unable to get the file from the URL '" + url.toString() + "'\n" + e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public String link(@NotNull URL url, boolean delete) {
        try {
            return link(url.toURI(), delete);
        } catch (URISyntaxException e) {
            LOGGER.error("Unable to get the file from the URL '" + url.toString() + "'\n" + e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public String link(@NotNull URL url) {
        try {
            return link(url.toURI());
        } catch (URISyntaxException e) {
            LOGGER.error("Unable to get the file from the URL '" + url.toString() + "'\n" + e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public String link(@NotNull URI uri, String tableName, boolean delete) {
        return link(new File(uri), tableName, delete);
    }

    @Override
    public String link(@NotNull URI uri, String tableName) {
        return link(new File(uri), tableName);
    }

    @Override
    public String link(@NotNull URI uri, boolean delete) {
        return link(new File(uri), delete);
    }

    @Override
    public String link(@NotNull URI uri) {
        return link(new File(uri));
    }

    @Override
    public String link(@NotNull File file, @NotNull String tableName, boolean delete) {
        return link(file.getAbsolutePath(), tableName, delete);
    }

    @Override
    public String link(@NotNull File file, @NotNull String tableName) {
        return link(file.getAbsolutePath(), tableName);
    }

    @Override
    public String link(@NotNull File file, boolean delete) {
        return link(file.getAbsolutePath(), delete);
    }

    @Override
    public String link(@NotNull File file) {
        return link(file.getAbsolutePath());
    }

    @Override
    public String load(@NotNull String filePath, @NotNull String tableName, @Nullable String encoding,
                           boolean delete) {
        String formatedTableName = TableLocation.parse(tableName, getDataBaseType() == DataBaseType.H2GIS).toString(getDataBaseType() == DataBaseType.H2GIS);
        try {
            if(ioMethods==null) {
                ioMethods = new IOMethods();
            }
            ioMethods.importFile(getConnection(), filePath, tableName, encoding, delete);
            return formatedTableName;
        } catch (SQLException e) {
            LOGGER.error("Cannot import the file : "+ filePath);
        }
        return null;
    }

    @Override
    public String load(@NotNull String filePath, @NotNull String tableName) {
        return load(filePath, tableName, null, false);
    }

    @Override
    public String load(@NotNull String filePath, @NotNull String tableName, boolean delete) {
        return load(filePath, tableName, null, delete);
    }

    @Override
    public String load(@NotNull String filePath) {
        return load(filePath, false);
    }

    @Override
    public String load(@NotNull String filePath, boolean delete) {
        String tableName = getTableNameFromPath(filePath).replace(".", "_");
        if (Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]*$").matcher(tableName).find()) {
            return load(filePath, tableName, null, delete);
        } else {
            LOGGER.error("Unsupported file characters");
        }
        return null;
    }

    @Override
    public String load(@NotNull URL url, @NotNull String tableName) {
        try {
            return load(url.toURI(), tableName, null, false);
        } catch (URISyntaxException e) {
            LOGGER.error("Unable to get the file from the URL '" + url.toString() + "'\n" + e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public String load(@NotNull URL url, @NotNull String tableName, boolean delete) {
        try {
            return load(url.toURI(), tableName, null, delete);
        } catch (URISyntaxException e) {
            LOGGER.error("Unable to get the file from the URL '" + url.toString() + "'\n" + e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public String load(@NotNull URL url) {
        try {
            return load(url.toURI(), false);
        } catch (URISyntaxException e) {
            LOGGER.error("Unable to get the file from the URL '" + url.toString() + "'\n" + e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public String load(@NotNull URL url, boolean delete) {
        try {
            return load(url.toURI(), delete);
        } catch (URISyntaxException e) {
            LOGGER.error("Unable to get the file from the URL '" + url.toString() + "'\n" + e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public String load(@NotNull URL url, @NotNull String tableName, @Nullable String encoding, boolean delete) {
        try {
            return load(url.toURI(), tableName, encoding, delete);
        } catch (URISyntaxException e) {
            LOGGER.error("Unable to get the file from the URL '" + url.toString() + "'\n" + e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public String load(@NotNull URI uri, @NotNull String tableName) {
        return load(new File(uri), tableName, null, false);
    }

    @Override
    public String load(@NotNull URI uri, @NotNull String tableName, boolean delete) {
        return load(new File(uri), tableName, null, delete);
    }

    @Override
    public String load(@NotNull URI uri) {
        return load(new File(uri), false);
    }

    @Override
    public String load(@NotNull URI uri, boolean delete) {
        return load(new File(uri), delete);
    }

    @Override
    public String load(@NotNull URI uri, @NotNull String tableName, @Nullable String encoding, boolean delete) {
        return load(new File(uri), tableName, encoding, delete);
    }

    @Override
    public String load(@NotNull File file, @NotNull String tableName) {
        return load(file.getAbsolutePath(), tableName, null, false);
    }

    @Override
    public String load(@NotNull File file, @NotNull String tableName, boolean delete) {
        return load(file.getAbsolutePath(), tableName, null, delete);
    }

    @Override
    public String load(@NotNull File file) {
        return load(file.getAbsolutePath(), false);
    }

    @Override
    public String load(@NotNull File file, boolean delete) {
        return load(file.getAbsolutePath(), delete);
    }

    @Override
    public String load(@NotNull File file, @NotNull String tableName, @Nullable String encoding, boolean delete) {
        return load(file.getAbsolutePath(), tableName, encoding, delete);
    }

    @Override
    public String load(@NotNull IJdbcDataSource dataSource, @NotNull String inputTableName, @NotNull String outputTableName, boolean deleteIfExists) {
        return load(dataSource, inputTableName, outputTableName, deleteIfExists, 1000);
    }

    @Override
    public String load(@NotNull IJdbcDataSource dataSource, @NotNull String inputTableName, @NotNull String outputTableName) {
        return load(dataSource, inputTableName, outputTableName, false, 1000);
    }

    @Override
    public String load(@NotNull IJdbcDataSource dataSource, @NotNull String inputTableName, boolean deleteIfExists) {
        //The inputTableName can be query
        String regex = ".*(?i)\\b(select|from)\\b.*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(inputTableName);
        if (matcher.find()) {
            LOGGER.error("This function doesn't support query as input data.");
        } else {
            try {
                IOMethods.exportToDataBase(dataSource.getConnection(), inputTableName, getConnection(), inputTableName, deleteIfExists?-1:0, 1000);
                TableLocation targetTableLocation = TableLocation.parse(inputTableName, this.getDataBaseType() == DataBaseType.H2GIS);
                return targetTableLocation.toString(this.getDataBaseType() == DataBaseType.H2GIS);
            } catch (SQLException e) {
                LOGGER.error("Unable to load the table "+inputTableName + " from " + dataSource.getLocation().toString());
            }
        }
        return null;
    }

    @Override
    public String load(@NotNull IJdbcDataSource dataSource, @NotNull String inputTableName) {
        //The inputTableName can be query
        String regex = ".*(?i)\\b(select|from)\\b.*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(inputTableName);
        if (matcher.find()) {
            LOGGER.error("This function doesn't support query as input data.");
        } else {
            try {
                IOMethods.exportToDataBase(dataSource.getConnection(), inputTableName, getConnection(), inputTableName, 0, 1000);
                TableLocation targetTableLocation = TableLocation.parse(inputTableName, this.getDataBaseType() == DataBaseType.H2GIS);
                return targetTableLocation.toString(this.getDataBaseType() == DataBaseType.H2GIS);
            } catch (SQLException e) {
                LOGGER.error("Unable to load the table "+inputTableName + " from " + dataSource.getLocation().toString());
            }
        }
        return null;
    }

    @Override
    public String load(@NotNull IJdbcDataSource dataSource, @NotNull String inputTableName, @NotNull String outputTableName, boolean deleteIfExists, int batchSize) {
        try {
            IOMethods.exportToDataBase(dataSource.getConnection(), inputTableName, getConnection(), outputTableName, deleteIfExists?-1:0, 1000);
            TableLocation targetTableLocation = TableLocation.parse(outputTableName, this.getDataBaseType() == DataBaseType.H2GIS);
            return targetTableLocation.toString(this.getDataBaseType() == DataBaseType.H2GIS);
        } catch (SQLException e) {
            LOGGER.error("Unable to load the table "+inputTableName + " from " + dataSource.getLocation().toString());
        }
        return null;
    }

    @Override
    public IDataSourceLocation getLocation() {
        try {
            Connection con = getConnection();
            if(con == null){
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


    @Override
    @NotNull
    public Collection<String> getTableNames() {
        try {
            Connection con = getConnection();
            if(con == null){
                LOGGER.error("Unable to get the connection.");
                return new ArrayList<>();
            }
            return JDBCUtilities.getTableNames(con, null, null, null, null);
        } catch (SQLException e) {
            LOGGER.error("Unable to get the database metadata.\n" + e.getLocalizedMessage());
            return new ArrayList<>();
        }
    }

    @Override
    @Nullable
    public Collection<String> getColumnNames(String location){
        try {
            Collection<String> cols = JDBCUtilities.getColumnNames(getConnection(), TableLocation.parse(location, databaseType.equals(DataBaseType.H2GIS)));
            if(!getConnection().getAutoCommit()) {
                getConnection().commit();
            }
            return cols;
        } catch (SQLException e) {
            LOGGER.error("Unable to get the column names of the table " + location + ".", e);
            try{
                if(!getConnection().getAutoCommit()) {
                    getConnection().rollback();
                }
            } catch (SQLException e2) {
                LOGGER.error("Unable to rollback.", e2);
            }
            return null;
        }
    }

    @Override
    public IJdbcTable getDataSet(@NotNull String dataSetName) {
        List<String> geomFields;
        try {
            Connection con = getConnection();
            if(con == null){
                LOGGER.error("Unable to get the connection.");
                return getTable(dataSetName);
            }
            geomFields = new ArrayList<>(GeometryTableUtilities.getGeometryColumnNames(con, new TableLocation(dataSetName)));
        } catch (SQLException e) {
            LOGGER.error("Unable to get the geometric fields.\n" + e.getLocalizedMessage());
            return getTable(dataSetName);
        }
        if (geomFields.size() >= 1) {
            return getSpatialTable(dataSetName);
        }
        return getTable(dataSetName);
    }

    @Nullable
    @Override
    public Object getProperty(String propertyName) {
        if (propertyName == null) {
            //LOGGER.error("Trying to get null property name.");
            return null;
        }
        IJdbcTable table = getTable(propertyName);
        if(table == null) {
            return getMetaClass().getProperty(this, propertyName);
        }
        else {
            return table;
        }
    }

    @Override
    public int call(GString gstring) throws Exception {
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
            Object param = params.get(i-1);
            if(param instanceof Array) {
                preparedStatement.setArray(i, (Array)param);
            }
            /*else if(param instanceof InputStream) {
                preparedStatement.setAsciiStream(i, (InputStream) param);
            }*/
            else if(param instanceof BigDecimal) {
                preparedStatement.setBigDecimal(i, (BigDecimal) param);
            }
            /*else if(param instanceof InputStream) {
                preparedStatement.set(i, (InputStream) param);
            }*/
            else if(param instanceof Blob) {
                preparedStatement.setBlob(i, (Blob) param);
            }
            /*else if(param instanceof InputStream) {
                preparedStatement.setBinaryStream(i, (InputStream) param);
            }*/
            else if(param instanceof Boolean) {
                preparedStatement.setBoolean(i, (Boolean) param);
            }
            else if(param instanceof Byte) {
                preparedStatement.setByte(i, (Byte) param);
            }
            else if(param instanceof byte[]) {
                preparedStatement.setBytes(i, (byte[]) param);
            }
            /*else if(param instanceof Reader) {
                preparedStatement.setCharacterStream(i, (Reader) param);
            }*/
            else if(param instanceof Clob) {
                preparedStatement.setClob(i, (Clob) param);
            }
            else if(param instanceof Date) {
                preparedStatement.setDate(i, (Date) param);
            }
            else if(param instanceof Double) {
                preparedStatement.setDouble(i, (Double) param);
            }
            else if(param instanceof Float) {
                preparedStatement.setFloat(i, (Float) param);
            }
            else if(param instanceof Integer) {
                preparedStatement.setInt(i, (Integer) param);
            }
            else if(param instanceof Long) {
                preparedStatement.setLong(i, (Long) param);
            }
            /*else if(param instanceof Reader) {
                preparedStatement.setNCharacterStream(i, (Reader) param);
            }*/
            else if(param instanceof NClob) {
                preparedStatement.setNClob(i, (NClob) param);
            }
            else if(param instanceof Ref) {
                preparedStatement.setRef(i, (Ref) param);
            }
            else if(param instanceof Short) {
                preparedStatement.setShort(i, (Short) param);
            }
            else if(param instanceof SQLXML) {
                preparedStatement.setSQLXML(i, (SQLXML) param);
            }
            else if(param instanceof String) {
                preparedStatement.setString(i, (String) param);
            }
            else if(param instanceof Time) {
                preparedStatement.setTime(i, (Time) param);
            }
            else if(param instanceof Timestamp) {
                preparedStatement.setTimestamp(i, (Timestamp) param);
            }
            else if(param instanceof URL) {
                preparedStatement.setURL(i, (URL) param);
            }
            else {
                preparedStatement.setObject(i, param);
            }
        }
    }

    @Override
    public JdbcDataSource autoCommit(boolean autoCommit) {
        try {
            Connection con = getConnection();
            if(con != null){
                con.setAutoCommit(autoCommit);
                return this;
            }
            else {
                LOGGER.error("Unable to get the connection.");
            }
        } catch (SQLException e) {
            LOGGER.error("Unable to set the auto-commit mode.\n" + e.getLocalizedMessage());
        }
        return this;
    }
}
