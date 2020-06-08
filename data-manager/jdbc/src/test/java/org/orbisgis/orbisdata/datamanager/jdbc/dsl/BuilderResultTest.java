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
package org.orbisgis.orbisdata.datamanager.jdbc.dsl;

import groovy.sql.Sql;
import org.h2gis.functions.factory.H2GISDBFactory;
import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.wrapper.SpatialResultSetImpl;
import org.h2gis.utilities.wrapper.StatementWrapper;
import org.junit.jupiter.api.Test;
import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.commons.annotations.Nullable;
import org.orbisgis.commons.printer.Ascii;
import org.orbisgis.orbisdata.datamanager.api.dataset.*;
import org.orbisgis.orbisdata.datamanager.jdbc.JdbcDataSource;
import org.orbisgis.orbisdata.datamanager.jdbc.JdbcSpatialTable;
import org.orbisgis.orbisdata.datamanager.jdbc.TableLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class dedicated to the {@link BuilderResult} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public class BuilderResultTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(BuilderResultTest.class);

    private static Connection connection;

    @Test
    public void testToString() {
        assertEquals("A simple string", new DummyBuilderResult("A simple string", DataBaseType.H2GIS, false).toString());
    }

    @Test
    public void testAsType() {
        assertTrue(new DummyBuilderResult("Select *", DataBaseType.H2GIS, false).asType(Ascii.class) instanceof Ascii);
        assertTrue(new DummyBuilderResult("Select *", DataBaseType.H2GIS, false).asType(ITable.class) instanceof ITable);
        assertTrue(new DummyBuilderResult("Select *", DataBaseType.H2GIS, false).asType(ISpatialTable.class) instanceof ISpatialTable);
        assertNull(new DummyBuilderResult("Select *", DataBaseType.POSTGIS, false).asType(ITable.class));
        assertNull(new DummyBuilderResult("Select *", DataBaseType.H2GIS, false).asType(String.class));
        assertNotNull(new DummyBuilderResult("Select *", DataBaseType.H2GIS, false).getTable());
        assertNotNull(new DummyBuilderResult("Select *", DataBaseType.H2GIS, false).getSpatialTable());
        assertNull(new DummyBuilderResult("Select *", DataBaseType.H2GIS, true).getSpatialTable());
    }


    /**
     * Simple implementation of the {@link BuilderResult} class.
     */
    private static class DummyBuilderResult extends BuilderResult {

        private String query;
        private DummyJdbcDataSource dummyJdbcDataSource;

        public DummyBuilderResult(String query, DataBaseType dbtype, boolean close) {
            super();
            this.query = query;
            try {
                connection = JDBCUtilities.wrapConnection(H2GISDBFactory.createSpatialDataBase(BuilderResultTest.class.getSimpleName()));
            } catch (SQLException | ClassNotFoundException e) {
                fail(e);
            }
            if (close) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    fail(e);
                }
            }
            Sql sql = new Sql(connection);
            dummyJdbcDataSource = new DummyJdbcDataSource(sql, dbtype);
        }

        @Override
        protected JdbcDataSource getDataSource() {
            return dummyJdbcDataSource;
        }

        @Override
        protected String getQuery() {
            return query;
        }
    }


    /**
     * Simple instantiation of a {@link JdbcSpatialTable}.
     **/
    private static class DummyJdbcSpatialTable extends JdbcSpatialTable {

        /**
         * Main constructor.
         *
         * @param jdbcDataSource DataSource to use for the creation of the resultSet.
         * @param tableLocation  TableLocation that identify the represented table.
         * @param statement      Statement used to request the database.
         * @param baseQuery      Query for the creation of the ResultSet
         */
        public DummyJdbcSpatialTable(TableLocation tableLocation, String baseQuery, Statement statement,
                                     JdbcDataSource jdbcDataSource) {
            super(DataBaseType.H2GIS, jdbcDataSource, tableLocation, statement, baseQuery);
        }


        @Override
        protected ResultSet getResultSet() {
            if (resultSet == null) {
                try {
                    resultSet = getStatement().executeQuery(getBaseQuery());
                } catch (SQLException e) {
                    LOGGER.error("Unable to execute the query '" + getBaseQuery() + "'.\n" + e.getLocalizedMessage());
                    return null;
                }
                try {
                    resultSet.beforeFirst();
                } catch (SQLException e) {
                    LOGGER.error("Unable to go before the first ResultSet row.\n" + e.getLocalizedMessage());
                    return null;
                }
            }
            return new SpatialResultSetImpl(resultSet, (StatementWrapper) getStatement());
        }

        @NotNull
        @Override
        public ISpatialTable reproject(int srid) {
            throw new IllegalArgumentException("Unsupported operation");
        }
    }

    /**
     * Simple implementation of the {@link JdbcDataSource} abstract class for test purpose.
     */
    private static class DummyJdbcDataSource extends JdbcDataSource {

        private DummyJdbcDataSource(Sql parent, DataBaseType databaseType) {
            super(parent, databaseType);
        }

        @Override
        public IJdbcTable getTable(@NotNull String tableName) {
            return null;
        }

        @Override
        public IJdbcTable getTable(String tableName, IJdbcTable.RSType resultSetType, IJdbcTable.RSConcurrency resultSetConcurrency) {
            return null;
        }

        @Override
        public IJdbcTable getTable(String tableName, IJdbcTable.RSType resultSetType, IJdbcTable.RSConcurrency resultSetConcurrency, IJdbcTable.RSHoldability resultSetHoldability) {
            return null;
        }

        @Override
        public IJdbcSpatialTable getSpatialTable(@NotNull String tableName) {
            String name = TableLocation.parse(tableName, getDataBaseType().equals(DataBaseType.H2GIS)).toString(getDataBaseType().equals(DataBaseType.H2GIS));
            try {
                if (!JDBCUtilities.tableExists(connection, org.h2gis.utilities.TableLocation.parse(name, true))) {
                    return null;
                }
            } catch (SQLException e) {
                return null;
            }
            StatementWrapper statement;
            try {
                statement = (StatementWrapper) connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            } catch (SQLException e) {
                return null;
            }
            String query = String.format("SELECT * FROM %s", name);
            return new DummyJdbcSpatialTable(new TableLocation(null, name), query, statement, this);
        }

        @Override
        public IJdbcSpatialTable getSpatialTable(String tableName, IJdbcTable.RSType resultSetType, IJdbcTable.RSConcurrency resultSetConcurrency) {
            return null;
        }

        @Override
        public IJdbcSpatialTable getSpatialTable(String tableName, IJdbcTable.RSType resultSetType, IJdbcTable.RSConcurrency resultSetConcurrency, IJdbcTable.RSHoldability resultSetHoldability) {
            return null;
        }

        @NotNull
        @Override
        public Collection<String> getTableNames() {
            try {
                return JDBCUtilities.getTableNames(connection, null, null, null, null);
            } catch (SQLException ignored) {
            }
            return null;
        }

        @Override
        public IJdbcTable getDataSet(@NotNull String name) {
            return null;
        }

        @Nullable
        @Override
        public Object asType(@NotNull Class<?> clazz) {
            return null;
        }

        @Override
        public boolean hasTable(@NotNull String tableName) {
            try {
                return JDBCUtilities.tableExists(connection, org.h2gis.utilities.TableLocation.parse(tableName, true));
            } catch (SQLException ex) {
                return false;
            }
        }
    }
}
