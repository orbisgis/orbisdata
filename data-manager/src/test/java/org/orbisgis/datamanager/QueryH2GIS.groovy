package org.orbisgis.datamanager

H2GIS h2GIS = H2GIS.open([databaseName:'./target/loadH2GIS']);
//h2GIS.execute("DROP TABLE IF EXISTS h2gis; CREATE TABLE h2gis (id int, the_geom point);insert into h2gis values (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);");
h2GIS.execute("DROP TABLE IF EXISTS routes");
h2GIS.execute("CALL FILE_TABLE('/home/ebocher/Autres/data/DONNEES RENNES/Reseau_Rennes.shp', 'routes')");


h2GIS.getSpatialTable "routes" eachRow {rs ->
    System.out.println(rs.geometry)
}

