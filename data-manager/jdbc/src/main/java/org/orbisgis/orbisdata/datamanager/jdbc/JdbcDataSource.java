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
import org.h2gis.functions.io.utility.FileUtil;
import org.h2gis.utilities.GeometryTableUtilities;
import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.TableLocation;
import org.h2gis.utilities.URIUtilities;
import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.commons.annotations.Nullable;
import org.orbisgis.orbisdata.datamanager.api.dataset.DataBaseType;
import org.orbisgis.orbisdata.datamanager.api.dataset.IJdbcTable;
import org.orbisgis.orbisdata.datamanager.api.datasource.IDataSourceLocation;
import org.orbisgis.orbisdata.datamanager.api.datasource.IJdbcDataSource;
import org.orbisgis.orbisdata.datamanager.api.dsl.IFromBuilder;
import org.orbisgis.orbisdata.datamanager.api.dsl.ISelectBuilder;
import org.orbisgis.orbisdata.datamanager.jdbc.dsl.FromBuilder;
import org.orbisgis.orbisdata.datamanager.jdbc.io.IOMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract class used to implements the request builder methods (select, from ...) in order to give a base to all the
 * JdbcDataSource implementations.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public abstract class JdbcDataSource extends Sql implements IJdbcDataSource, ISelectBuilder {
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
    public boolean execute(GString gstring) throws SQLException {
        try {
            return super.execute(gstring);
        } catch (SQLException e) {
            LOGGER.debug("Unable to execute the request as a GString.\n" + e.getLocalizedMessage());
        }
        return super.execute(gstring.toString());
    }

    @Override
    public GroovyRowResult firstRow(GString gstring) throws SQLException {
        try {
            return super.firstRow(gstring);
        } catch (SQLException e) {
            LOGGER.debug("Unable to execute the request as a GString.\n" + e.getLocalizedMessage());
        }
        return super.firstRow(gstring.toString());
    }

    @Override
    public List<GroovyRowResult> rows(GString gstring) throws SQLException {
        try {
            return super.rows(gstring);
        } catch (SQLException e) {
            LOGGER.debug("Unable to execute the request as a GString.\n" + e.getLocalizedMessage());
        }
        return super.rows(gstring.toString());
    }

    @Override
    public void eachRow(GString gstring,
                        @ClosureParams(value = SimpleType.class, options = "java.sql.ResultSet") Closure closure)
            throws SQLException {
        try {
            super.eachRow(gstring, closure);
        } catch (SQLException e) {
            LOGGER.debug("Unable to execute the request as a GString.\n" + e.getLocalizedMessage());
            super.eachRow(gstring.toString(), closure);
        }
    }

    @Override
    public IFromBuilder select(String... fields) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ");
        if (fields != null && fields.length > 0) {
            query.append(String.join(",", fields));
        } else {
            query.append("* ");
        }
        return new FromBuilder(query.toString(), this);
    }

    @Override
    public boolean executeScript(@NotNull String fileName, Map<String, String> bindings) {
        File file = URIUtilities.fileFromString(fileName);
        try {
            if (FileUtil.isExtensionWellFormated(file, "sql")) {
                return executeScript(new FileInputStream(file), bindings);
            }
        } catch (IOException e) {
            LOGGER.error("Unable to read the SQL file.\n" + e.getLocalizedMessage());
        }
        return false;
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
    public boolean save(@NotNull String tableName, @NotNull String filePath, @Nullable String encoding) {
        if(getConnection() == null){
            LOGGER.error("No connection, cannot save.");
            return false;
        }
        return IOMethods.saveAsFile(getConnection(), tableName, filePath, encoding, false);
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
        int start = filePath.lastIndexOf("/") + 1;
        int end = filePath.lastIndexOf(".");
        if (end == -1) {
            end = filePath.length();
        }
        if(databaseType == DataBaseType.H2GIS){
            return filePath.substring(start, end).toUpperCase();
        }
        return filePath.substring(start, end).toLowerCase();
    }

    @Override
    public String link(@NotNull String filePath, @NotNull String tableName, boolean delete) {
        String formatedTableName = TableLocation.parse(tableName, getDataBaseType() == DataBaseType.H2GIS).toString(getDataBaseType() == DataBaseType.H2GIS);
        IOMethods.link(filePath, formatedTableName, delete, this);
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
        if(IOMethods.loadFile(filePath, formatedTableName, encoding, delete, this)){
            return formatedTableName;
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
        String tableName = getTableNameFromPath(filePath);
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
            TableLocation sourceTableLocation = TableLocation.parse(inputTableName, dataSource.getDataBaseType() == DataBaseType.H2GIS);
            TableLocation targetTableLocation = TableLocation.parse(inputTableName, this.getDataBaseType() == DataBaseType.H2GIS);
            if (IOMethods.loadFromDB(this, dataSource,
                    targetTableLocation, sourceTableLocation,
                    deleteIfExists, 1000)) {
                return targetTableLocation.toString(this.getDataBaseType() == DataBaseType.H2GIS);
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
            TableLocation sourceTableLocation = TableLocation.parse(inputTableName, dataSource.getDataBaseType() == DataBaseType.H2GIS);
            TableLocation targetTableLocation = TableLocation.parse(inputTableName, this.getDataBaseType() == DataBaseType.H2GIS);
            if (IOMethods.loadFromDB(this, dataSource,
                    targetTableLocation, sourceTableLocation,
                    false, 1000)) {
                return targetTableLocation.toString(this.getDataBaseType() == DataBaseType.H2GIS);
            }
        }
        return null;
    }

    @Override
    public String load(@NotNull IJdbcDataSource dataSource, @NotNull String inputTableName, @NotNull String outputTableName, boolean deleteIfExists, int batchSize) {
        //The inputTableName can be query
        String regex = ".*(?i)\\b(select|from)\\b.*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(inputTableName);
        if (matcher.find()) {
            if (inputTableName.startsWith("(") && inputTableName.endsWith(")")) {
                TableLocation targetTableLocation = TableLocation.parse(outputTableName, this.getDataBaseType() == DataBaseType.H2GIS);
                if (IOMethods.loadFromDB(this, dataSource,
                        targetTableLocation, inputTableName,
                        deleteIfExists, batchSize)) {
                    return targetTableLocation.toString(this.getDataBaseType() == DataBaseType.H2GIS);
                }
            } else {
                LOGGER.error("The query must be enclosed in parenthesis: '(SELECT * FROM ORDERS)'.");
            }
        } else {
            TableLocation sourceTableLocation = TableLocation.parse(inputTableName, dataSource.getDataBaseType() == DataBaseType.H2GIS);
            TableLocation targetTableLocation = TableLocation.parse(outputTableName, this.getDataBaseType() == DataBaseType.H2GIS);
            if (IOMethods.loadFromDB(this, dataSource,
                    targetTableLocation, sourceTableLocation,
                    deleteIfExists, batchSize)) {
                return targetTableLocation.toString(this.getDataBaseType() == DataBaseType.H2GIS);
            }
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
            return JDBCUtilities.getColumnNames(getConnection(), TableLocation.parse(location, databaseType.equals(DataBaseType.H2GIS)));
        } catch (SQLException e) {
            LOGGER.error("Unable to get the column names of the table " + location + ".", e);
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
}
