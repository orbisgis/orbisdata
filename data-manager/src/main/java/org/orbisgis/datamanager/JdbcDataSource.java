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

import groovy.sql.Sql;
import groovy.text.SimpleTemplateEngine;
import org.h2.util.ScriptReader;
import org.h2gis.functions.io.utility.FileUtil;
import org.h2gis.utilities.SFSUtilities;
import org.h2gis.utilities.TableLocation;
import org.h2gis.utilities.URIUtilities;
import org.h2gis.utilities.wrapper.StatementWrapper;
import org.orbisgis.datamanager.h2gis.H2gisSpatialTable;
import org.orbisgis.datamanager.h2gis.H2gisTable;
import org.orbisgis.datamanager.postgis.PostgisSpatialTable;
import org.orbisgis.datamanager.postgis.PostgisTable;
import org.orbisgis.datamanagerapi.dataset.Database;
import org.orbisgis.datamanagerapi.dataset.ITable;
import org.orbisgis.datamanagerapi.datasource.IJdbcDataSource;
import org.orbisgis.datamanagerapi.dsl.ISqlBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.UUID;

/**
 * Abstract class used to implements the request builder methods (select, from ...) in order to give a base to all the
 * JdbcDataSource implementations.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public abstract class JdbcDataSource extends Sql implements IJdbcDataSource, ISqlBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcDataSource.class);

    private StringBuilder query = new StringBuilder();
    private Database database;

    public JdbcDataSource(Sql parent, Database database) {
        super(parent);
        this.database = database;
    }

    public JdbcDataSource(DataSource dataSource, Database database) {
        super(dataSource);
        this.database = database;
    }

    public JdbcDataSource(Connection connection, Database database) {
        super(connection);
        this.database = database;
    }

    public Database getDataBase(){
        return database;
    }

    @Override
    public ISqlBuilder select(String... fields) {
        query = new StringBuilder();
        query.append("SELECT ");
        StringBuilder columns = new StringBuilder();
        if(fields.length > 0){
           for(String field : fields){
                columns.append(field).append(", ");
            }
            columns.deleteCharAt(columns.length()-2);
        }
        else{
            columns.append("* ");
        }
        query.append(columns);
        return this;
    }

    @Override
    public ISqlBuilder from(String... tables) {
        query.append("FROM ");
        for(String table : tables){
            query.append(table).append(", ");
        }
        query.deleteCharAt(query.length()-2);
        return this;
    }

    @Override
    public ISqlBuilder where(String condition) {
        query.append("WHERE ");
        query.append(condition);
        return this;
    }

    @Override
    public ISqlBuilder and(String condition) {
        query.append(" AND ");
        query.append(condition);
        return this;
    }

    @Override
    public ISqlBuilder or(String condition) {
        query.append(" OR ");
        query.append(condition);
        return this;
    }

    @Override
    public ISqlBuilder groupBy(String... fields) {
        query.append(" GROUP BY ");
        for(String field : fields){
            query.append(field).append(", ");
        }
        query.deleteCharAt(query.length()-2);
        return this;
    }

    @Override
    public ISqlBuilder orderBy(Map<String, Order> orderByMap) {
        query.append(" ORDER BY ");
        orderByMap.forEach((key, value) -> query.append(key).append(" ").append(value.name()).append(", "));
        query.deleteCharAt(query.length()-2);
        return this;
    }

    @Override
    public ISqlBuilder orderBy(String field, Order order) {
        query.append(" ORDER BY ").append(field).append(" ").append(order.name());
        return this;
    }

    @Override
    public ISqlBuilder orderBy(String field) {
        query.append(" ORDER BY ").append(field);
        return this;
    }

    @Override
    public ISqlBuilder limit(int limitCount) {
        query.append(" LIMIT ").append(limitCount);
        return this;
    }

    @Override
    public ITable execute() {
        Statement statement;
        try {
            statement = getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        } catch (SQLException e) {
            LOGGER.error("Unable to create the StatementWrapper.\n" + e.getLocalizedMessage());
            return null;
        }
        ResultSet resultSet;
        try {
            resultSet = statement.executeQuery(query.toString());
        } catch (SQLException e) {
            LOGGER.error("Unable to execute the query.\n" + e.getLocalizedMessage());
            return null;
        }
        String name = "TABLE_"+UUID.randomUUID().toString();
        switch(database) {
            case H2GIS:
                if(!(statement instanceof StatementWrapper)){
                    LOGGER.error("The statement class not compatible with the database.");
                    break;
                }
                try {
                    if(SFSUtilities.hasGeometryField(resultSet)) {
                        return new H2gisSpatialTable(new TableLocation(name), resultSet, (StatementWrapper) statement);
                    }
                    else{
                        return new H2gisTable(new TableLocation(name), resultSet, (StatementWrapper) statement);
                    }
                } catch (SQLException e) {
                    LOGGER.warn("Unable to detect if table '"+name+"' has a geometric field.\n"+e.getLocalizedMessage());
                    return new H2gisTable(new TableLocation(name), resultSet, (StatementWrapper) statement);
                }
            case POSTGIS:
                if(!(statement instanceof org.orbisgis.postgis_jts.StatementWrapper)){
                    LOGGER.error("The statement class not compatible with the database.");
                    break;
                }
                try {
                    if(SFSUtilities.hasGeometryField(resultSet)) {
                        return new PostgisSpatialTable(new TableLocation(name), resultSet, (org.orbisgis.postgis_jts.StatementWrapper)statement);
                    }
                    else{
                        return new PostgisTable(new TableLocation(name), resultSet, (org.orbisgis.postgis_jts.StatementWrapper)statement);
                    }
                } catch (SQLException e) {
                    LOGGER.warn("Unable to detect if table '"+name+"' has a geometric field.\n"+e.getLocalizedMessage());
                    return new PostgisTable(new TableLocation(name), resultSet, (org.orbisgis.postgis_jts.StatementWrapper)statement);
                }
        }
        return null;
    }

    /**
     * This method is used to execute a SQL file
     * @param fileName the sql file
     */
    public void executeScript(String fileName){
        executeScript(fileName,null);
    }


        /**
         * This method is used to execute a SQL file that contains parametrized text
         * Parametrized text must be expressed with $value or ${value}
         * @param fileName the sql file
         * @param bindings the map between parametrized text and its value.
         * eg. ["value", "myvalue"] to replace ${value} by myvalue
         */
    public void executeScript(String fileName, Map<String, String> bindings){
        File file = URIUtilities.fileFromString(fileName);
        try {
            if (FileUtil.isFileImportable(file, "sql")) {
                SimpleTemplateEngine engine =null;
                if(bindings!=null && !bindings.isEmpty()){
                    engine = new SimpleTemplateEngine();
                }
                ScriptReader scriptReader = new ScriptReader(new FileReader(file));
                scriptReader.setSkipRemarks(true);
                while (true) {
                    String commandSQL = scriptReader.readStatement();
                    if (commandSQL == null) {
                        break;
                    }
                    if (!commandSQL.isEmpty()) {
                        if(engine!=null) {
                            commandSQL = engine.createTemplate(commandSQL).make(bindings).toString();
                        }
                        execute(commandSQL);
                    }
                }
            }
        } catch (SQLException | IOException | ClassNotFoundException e) {
            LOGGER.error("Unable to read the SQL file.\n" + e.getLocalizedMessage());
        }
    }
}