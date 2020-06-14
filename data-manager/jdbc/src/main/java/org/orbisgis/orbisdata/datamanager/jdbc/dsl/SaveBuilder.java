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

import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.commons.annotations.Nullable;
import org.orbisgis.orbisdata.datamanager.api.dataset.ITable;
import org.orbisgis.orbisdata.datamanager.api.dsl.ISaveBuilder;
import org.orbisgis.orbisdata.datamanager.jdbc.io.IOMethods;

import java.io.File;
import java.sql.Connection;

/**
 * Implementation of the {@link ISaveBuilder} interface
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (Lab-STICC UBS, Chaire GEOTERA, 2020)
 */
public class SaveBuilder implements ISaveBuilder {

    /** Connection of the database. */
    private final Connection con;
    /** Table to save. */
    private final ITable<?, ?> table;
    /** Encoding of the saved files. */
    private String encoding = null;
    /** Compression of the saved files. */
    private String compression = null;
    /** Name of the saved files. */
    private String name = null;
    /** Extension of the saved files. */
    private String folder = null;
    /** True if the existing save files shoudl be deleted, false otherwise. */
    private boolean delete = false;

    public SaveBuilder(@Nullable Connection con, @Nullable ITable<?, ?> table) {
        this.con = con;
        this.table = table;
    }

    @Override
    public boolean save(@Nullable String extension) {
        if(con == null || table == null || extension == null) {
            return false;
        }
        String path = "";
        if(folder != null && !folder.isEmpty()) {
            path += folder + File.separator;
        }
        if(name != null && !name.isEmpty()) {
            path += name;
        }
        else {
            path += table.getName();
        }
        path += "." + extension;

        return IOMethods.saveAsFile(con, table.getName(), path, encoding, compression, delete);
    }

    @NotNull
    @Override
    public ISaveBuilder encoding(@Nullable String encoding) {
        this.encoding = encoding;
        return this;
    }

    @NotNull
    @Override
    public ISaveBuilder utf8() {
        this.encoding = "UTF8";
        return this;
    }

    @NotNull
    @Override
    public ISaveBuilder zip() {
        this.compression = "zip";
        return this;
    }

    @NotNull
    @Override
    public ISaveBuilder gz() {
        this.compression = "gz";
        return this;
    }

    @NotNull
    @Override
    public ISaveBuilder name(@Nullable String name) {
        this.name = name;
        return this;
    }

    @NotNull
    @Override
    public ISaveBuilder folder(@Nullable String folder) {
        this.folder = folder;
        return this;
    }

    @NotNull
    @Override
    public ISaveBuilder delete() {
        this.delete = true;
        return this;
    }
}
