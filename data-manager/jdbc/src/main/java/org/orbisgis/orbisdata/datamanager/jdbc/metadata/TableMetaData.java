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
package org.orbisgis.orbisdata.datamanager.jdbc.metadata;

import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.orbisdata.datamanager.api.dataset.ITable;
import org.orbisgis.orbisdata.datamanager.api.metadata.ITableMetaData;

import java.util.LinkedHashMap;

/**
 * Contains the metadata of a {@link ITable}.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Chaire GEOTERA 2020)
 */
public class TableMetaData extends MatrixMetaData implements ITableMetaData {

    private LinkedHashMap<String, String> columnsTypes;
    private int columnCount;
    private int rowCount;
    private boolean isSpatial;

    public TableMetaData(String location, String name, LinkedHashMap<String, String> columnsTypes, int columnCount, int rowCount,
                         boolean isSpatial) {
        super(location, name, 2, new int[]{columnCount, rowCount});
        this.columnsTypes = columnsTypes;
        this.columnCount = columnCount;
        this.rowCount = rowCount;
        this.isSpatial = isSpatial;
    }

    @NotNull
    @Override
    public LinkedHashMap<String, String> getColumnsTypes() {
        return columnsTypes;
    }

    @Override
    public int getColumnCount() {
        return columnCount;
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }

    @Override
    public boolean isSpatial() {
        return isSpatial;
    }
}
