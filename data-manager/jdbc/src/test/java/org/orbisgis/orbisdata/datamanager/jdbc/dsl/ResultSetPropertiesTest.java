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

import org.junit.jupiter.api.Test;

import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class dedicated to {@link ResultSetProperties}.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC / Chaire GEOTERA 2020)
 */
public class ResultSetPropertiesTest {

    @Test
    void emptyProperties(){
        ResultSetProperties rsp = new ResultSetProperties();
        assertEquals(-1, rsp.getConcurrency());
        assertEquals(-1, rsp.getType());
        assertEquals(-1, rsp.getHoldability());
        assertEquals(-1, rsp.getFetchDirection());
        assertEquals(100, rsp.getFetchSize());
        assertEquals(-1, rsp.getTimeout());
        assertEquals(-1, rsp.getMaxRows());
        assertNull(rsp.getCursorName());
        assertFalse(rsp.isPoolable());
        assertEquals(-1, rsp.getMaxFieldSize());
    }

    @Test
    void badProperties(){
        ResultSetProperties rsp = new ResultSetProperties();
        rsp.setConcurrency(1);
        rsp.setType(2);
        rsp.setHoldability(3);
        rsp.setFetchDirection(4);
        rsp.setFetchSize(-5);
        rsp.setTimeout(-6);
        rsp.setMaxRows(-7);
        rsp.setMaxFieldSize(-8);

        assertEquals(-1, rsp.getConcurrency());
        assertEquals(-1, rsp.getType());
        assertEquals(-1, rsp.getHoldability());
        assertEquals(-1, rsp.getFetchDirection());
        assertEquals(100, rsp.getFetchSize());
        assertEquals(-1, rsp.getTimeout());
        assertEquals(-1, rsp.getMaxRows());
        assertNull(rsp.getCursorName());
        assertFalse(rsp.isPoolable());
        assertEquals(-1, rsp.getMaxFieldSize());
    }

    @Test
    void setProperties(){
        ResultSetProperties rsp = new ResultSetProperties();
        rsp.setConcurrency(ResultSet.CONCUR_UPDATABLE);
        rsp.setType(ResultSet.TYPE_SCROLL_SENSITIVE);
        rsp.setHoldability(ResultSet.HOLD_CURSORS_OVER_COMMIT);
        rsp.setFetchDirection(ResultSet.FETCH_FORWARD);
        rsp.setFetchSize(10);
        rsp.setTimeout(20);
        rsp.setMaxRows(30);
        rsp.setCursorName("toto");
        rsp.setPoolable(true);
        rsp.setMaxFieldSize(40);

        assertEquals(ResultSet.CONCUR_UPDATABLE, rsp.getConcurrency());
        assertEquals(ResultSet.TYPE_SCROLL_SENSITIVE, rsp.getType());
        assertEquals(ResultSet.HOLD_CURSORS_OVER_COMMIT, rsp.getHoldability());
        assertEquals(ResultSet.FETCH_FORWARD, rsp.getFetchDirection());
        assertEquals(10, rsp.getFetchSize());
        assertEquals(20, rsp.getTimeout());
        assertEquals(30, rsp.getMaxRows());
        assertEquals("toto", rsp.getCursorName());
        assertTrue(rsp.isPoolable());
        assertEquals(40, rsp.getMaxFieldSize());
    }
}
