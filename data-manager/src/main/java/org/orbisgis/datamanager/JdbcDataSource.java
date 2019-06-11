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
import groovy.lang.GString;
import groovy.lang.MetaClass;
import groovy.sql.GroovyRowResult;
import groovy.sql.Sql;
import groovy.text.SimpleTemplateEngine;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.SimpleType;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.h2.util.ScriptReader;
import org.h2gis.functions.io.utility.FileUtil;
import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.SFSUtilities;
import org.h2gis.utilities.TableLocation;
import org.h2gis.utilities.URIUtilities;
import org.orbisgis.datamanager.dsl.FromBuilder;
import org.orbisgis.datamanager.io.IOMethods;
import org.orbisgis.datamanagerapi.dataset.DataBaseType;
import org.orbisgis.datamanagerapi.dataset.IDataSet;
import org.orbisgis.datamanagerapi.dataset.ITable;
import org.orbisgis.datamanagerapi.datasource.IDataSourceLocation;
import org.orbisgis.datamanagerapi.datasource.IJdbcDataSource;
import org.orbisgis.datamanagerapi.dsl.IFromBuilder;
import org.orbisgis.datamanagerapi.dsl.ISelectBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Abstract class used to implements the request builder methods (select, from ...) in order to give a base to all the
 * JdbcDataSource implementations.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public abstract class JdbcDataSource extends Sql implements IJdbcDataSource, ISelectBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcDataSource.class);

    private MetaClass metaClass;
    private DataBaseType databaseType;

    public JdbcDataSource(Sql parent, DataBaseType databaseType) {
        super(parent);
        this.metaClass = InvokerHelper.getMetaClass(getClass());
        this.databaseType = databaseType;
        LOG.setLevel(Level.OFF);
    }

    public JdbcDataSource(DataSource dataSource, DataBaseType databaseType) {
        super(dataSource);
        this.metaClass = InvokerHelper.getMetaClass(getClass());
        this.databaseType = databaseType;
        LOG.setLevel(Level.OFF);
    }

    public JdbcDataSource(Connection connection, DataBaseType databaseType) {
        super(connection);
        this.metaClass = InvokerHelper.getMetaClass(getClass());
        this.databaseType = databaseType;
        LOG.setLevel(Level.OFF);
    }

    public DataBaseType getDataBaseType(){
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
                      @ClosureParams(value= SimpleType.class, options="java.sql.ResultSet") Closure closure)
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
        StringBuilder columns = new StringBuilder();
        if(fields != null && fields.length > 0){
           for(String field : fields){
                columns.append(field).append(", ");
            }
            columns.deleteCharAt(columns.length()-2);
        }
        else{
            columns.append("* ");
        }
        query.append(columns);
        return new FromBuilder(query.toString(), this);
    }

    @Override
    public void executeScript(String fileName, Map<String, String> bindings) {
        File file = URIUtilities.fileFromString(fileName);
        try {
            if (FileUtil.isExtensionWellFormated(file, "sql")) {
                executeScript(new FileInputStream(file), bindings);
            }
        } catch (IOException e) {
            LOGGER.error("Unable to read the SQL file.\n" + e.getLocalizedMessage());
        }
    }

    @Override
    public void executeScript(InputStream stream, Map<String, String> bindings) {
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
                    }
                }
                try {
                    execute(commandSQL);
                } catch (SQLException e) {
                    LOGGER.error("Unable to execute the Sql command '" + commandSQL + "'.\n" + e.getLocalizedMessage());
                }
            }
        }
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
    public boolean save(String tableName, String filePath) {
        return save(tableName, filePath, null);
    }

    @Override
    public boolean save(String tableName, String filePath, String encoding) {
        return IOMethods.saveAsFile(getConnection(), tableName, filePath, encoding);
    }

    @Override
    public ITable link(String filePath, String tableName, boolean delete) {
        IOMethods.link(filePath, tableName, delete, this);
        return getTable(tableName);
    }

    @Override
    public ITable link(String filePath, String tableName) {
        return link(filePath, tableName, false);
    }

    @Override
    public ITable link(String filePath, boolean delete) {
        final String name = URIUtilities.fileFromString(filePath).getName();
        String tableName = name.substring(0, name.lastIndexOf(".")).toUpperCase();
        if ("^[a-zA-Z][a-zA-Z0-9_]*$".matches(tableName)) {
            return link(filePath, tableName, delete);
        } else {
            LOGGER.error("The file name contains unsupported characters");
        }
        return null;
    }

    @Override
    public ITable link(String filePath) {
        return link(filePath, false);
    }

    @Override
    public ITable load(String filePath, String tableName, String encoding, boolean delete) {
        IOMethods.loadFile(filePath, tableName, encoding, delete, this);
        return getTable(tableName);
    }

    @Override
    public ITable load(Map<String, String> properties, String inputTableName) {
        return load(properties, inputTableName, inputTableName, false);
    }

    @Override
    public ITable load(Map<String, String> properties, String inputTableName, boolean delete) {
        return load(properties, inputTableName, inputTableName, delete);
    }

    @Override
    public ITable load(Map<String, String> properties, String inputTableName, String outputTableName) {
        return load(properties, inputTableName, outputTableName, false);
    }

    @Override
    public ITable load(Map<String, String> properties, String inputTableName, String outputTableName, boolean delete) {
        IOMethods.loadTable(properties, inputTableName, outputTableName, delete, this);
        return getTable(outputTableName);

    }

    @Override
    public ITable load(String filePath, String tableName) {
        return load(filePath, tableName, null, false);
    }

    @Override
    public ITable load(String filePath, String tableName, boolean delete) {
        return load(filePath, tableName, null, delete);
    }

    @Override
    public ITable load(String filePath) {
        return load(filePath, false);
    }

    @Override
    public ITable load(String filePath, boolean delete) {
        final String name = URIUtilities.fileFromString(filePath).getName();
        String tableName = name.substring(0, name.lastIndexOf(".")).toUpperCase();
        if (tableName.matches("^[a-zA-Z][a-zA-Z0-9_]*$")) {
            return load(filePath,tableName, null, delete);
        } else {
            LOGGER.error("Unsupported file characters");
        }
        return null;
    }

    @Override
    public IDataSourceLocation getLocation(){
        try {
            String url = this.getConnection().getMetaData().getURL();
            return new DataSourceLocation(url.substring(url.lastIndexOf(":") + 1));
        } catch (SQLException e) {
            LOGGER.error("Unable to get the connection metadata.\n" + e.getLocalizedMessage());
        }
        return null;
    }


    @Override
    public Collection<String> getTableNames() {
        try {
            return JDBCUtilities.getTableNames(getConnection().getMetaData(), null, null, null, null);
        } catch (SQLException e) {
            LOGGER.error("Unable to get the database metadata.\n" + e.getLocalizedMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public IDataSet getDataSet(String dataSetName) {
        List<String> geomFields;
        try {
            geomFields = SFSUtilities.getGeometryFields(getConnection(), new TableLocation(dataSetName));
        } catch (SQLException e) {

            return getTable(dataSetName);
        }
        if (geomFields.size() >= 1) {
            return getSpatialTable(dataSetName);
        }
        return getTable(dataSetName);
    }
}
