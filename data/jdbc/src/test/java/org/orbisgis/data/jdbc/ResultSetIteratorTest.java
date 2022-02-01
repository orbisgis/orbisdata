/*
 * Bundle JDBC API is part of the OrbisGIS platform
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
 * JDBC API is distributed under LGPL 3 license.
 *
 * Copyright (C) 2019-2020 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * JDBC API is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * JDBC API is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * JDBC API. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.data.jdbc;

import org.h2gis.functions.factory.H2GISDBFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class dedicated to {@link ResultSetIterator}.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (Lab-STICC UBS 2019)
 */
public class ResultSetIteratorTest {

    /**
     * Connection used to get a {@link ResultSet}.
     */
    private static Connection connection;

    /**
     * Initialize the database through the connection with some data.
     *
     * @throws SQLException           Exception thrown by SQL requests.
     * @throws ClassNotFoundException Exception get if the H2/H2GIS classes are not found.
     */
    @BeforeAll
    public static void initialisation() throws SQLException, ClassNotFoundException {
        connection = H2GISDBFactory.createSpatialDataBase("./target/resultsetiterator");
        connection.createStatement().execute("CREATE TABLE TEST (col1 int, col2 varchar, col3 double)");
        connection.createStatement().execute("INSERT INTO TEST VALUES(1, 'tata', 5.5689)");
        connection.createStatement().execute("INSERT INTO TEST VALUES(2, 'toto', 7.2635)");
        connection.createStatement().execute("INSERT INTO TEST VALUES(3, 'titi', 1.7362)");
        connection.createStatement().execute("INSERT INTO TEST VALUES(4, 'tutu', 9.2555)");
    }

    /**
     * Return a {@link ResultSet} containing data.
     *
     * @return A {@link ResultSet} containing data.
     * @throws SQLException Exception thrown by SQL requests.
     */
    private static ResultSet getResultSet() throws SQLException {
        return connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)
                .executeQuery("SELECT * FROM TEST");
    }

    /**
     * Test the instantiation of a {@link ResultSetIterator}.
     *
     * @throws SQLException Exception thrown by SQL requests.
     */
    @Test
    public void resultSetIteratorTest() throws SQLException {
        ResultSet obj;
        ResultSetIterator resultSetIterator = new ResultSetIterator(getResultSet());

        assertTrue(resultSetIterator.hasNext());
        obj = resultSetIterator.next();
        assertNotNull(obj);
        assertEquals(1, obj.getInt(1));
        assertEquals("tata", obj.getString(2));
        assertEquals(5.5689, obj.getDouble(3));

        assertTrue(resultSetIterator.hasNext());
        obj = resultSetIterator.next();
        assertNotNull(obj);
        assertEquals(2, obj.getInt(1));
        assertEquals("toto", obj.getString(2));
        assertEquals(7.2635, obj.getDouble(3));

        assertTrue(resultSetIterator.hasNext());
        obj = resultSetIterator.next();
        assertNotNull(obj);
        assertEquals(3, obj.getInt(1));
        assertEquals("titi", obj.getString(2));
        assertEquals(1.7362, obj.getDouble(3));

        assertTrue(resultSetIterator.hasNext());
        obj = resultSetIterator.next();
        assertNotNull(obj);
        assertEquals(4, obj.getInt(1));
        assertEquals("tutu", obj.getString(2));
        assertEquals(9.2555, obj.getDouble(3));

        assertNotNull(resultSetIterator.next());
    }

    @Test
    public void emptyResultSetIteratorTest() {
        ResultSetIterator resultSetIterator = new ResultSetIterator(null);
        assertFalse(resultSetIterator.hasNext());
        assertNull(resultSetIterator.next());
    }
}
