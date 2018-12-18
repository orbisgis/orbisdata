package org.orbisgis.datamanager;

import groovy.lang.Closure;
import org.h2gis.utilities.SpatialResultSet;
import org.junit.jupiter.api.Test;
import org.osgi.service.jdbc.DataSourceFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2018)
 */
public class H2GISTests {
    
    
    @Test
    public void loadH2GIS() throws SQLException {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DataSourceFactory.JDBC_DATABASE_NAME, "./target/loadH2GIS");
        H2GIS h2GIS = H2GIS.open(map);
        assertNotNull(h2GIS);
    }


    @Test
    public void queryH2GIS() throws SQLException {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DataSourceFactory.JDBC_DATABASE_NAME, "./target/loadH2GIS");
        H2GIS h2GIS = H2GIS.open(map);
        h2GIS.execute("DROP TABLE IF EXISTS h2gis; CREATE TABLE h2gis (id int, the_geom point);insert into h2gis values (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);");
        ArrayList<String> values = new ArrayList<>();
        h2GIS.eachRow("SELECT THE_GEOM FROM h2gis", new Closure(null){
            @Override
            public Object call(Object argument) {
                values.add(argument.toString());
                return argument;
            }
        });
        assertTrue(values.size()==2);
        assertTrue(values.contains("[THE_GEOM:POINT (10 10)]"));
        assertTrue(values.contains("[THE_GEOM:POINT (1 1)]"));
    }

    @Test
    public void querySpatialTable() throws SQLException {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DataSourceFactory.JDBC_DATABASE_NAME, "./target/loadH2GIS");
        H2GIS h2GIS = H2GIS.open(map);
        h2GIS.execute("DROP TABLE IF EXISTS h2gis; CREATE TABLE h2gis (id int, the_geom point);insert into h2gis values (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);");

        ArrayList<String> values = new ArrayList<>();
        h2GIS.getSpatialTable("h2gis").eachRow(new Closure(null){
            @Override
            public Object call(Object argument) {
                try {
                    values.add(((SpatialResultSet)argument).getGeometry().toString());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return argument;
            }
        });
        assertEquals(2,values.size());
        assertTrue(values.contains("POINT (10 10)]"));
        assertTrue(values.contains("POINT (1 1)]"));
    }
}
