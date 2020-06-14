/*
 * Bundle DataManager API is part of the OrbisGIS platform
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
 * DataManager API is distributed under LGPL 3 license.
 *
 * Copyright (C) 2019-2020 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * DataManager API is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * DataManager API is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * DataManager API. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.orbisdata.datamanager.api.dsl;

import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.commons.annotations.Nullable;
import org.orbisgis.orbisdata.datamanager.api.dataset.ITable;

/**
 * Defines the methods for the execution of the save.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (Lab-STICC UBS, Chaire GEOTERA, 2020)
 */
public interface ISaveBuilder {

    /**
     * Save the a {@link ITable} according to the data get through the builder. Returns true if the save has been done
     * with success, false otherwise.
     *
     * @param extension File extension of the save
     * @return True if the save has been done with success, false otherwise.
     */
    boolean save(@Nullable String extension);

    /**
     * Sets the encoding of the saved file. If not set or set to null, uses default encoding.
     *
     * @param encoding Encoding of the saved file.
     * @return The {@link ISaveBuilder} itself.
     */
    @NotNull
    ISaveBuilder encoding(@Nullable String encoding);

    /**
     * Sets the encoding to UTF-8.
     *
     * @return The {@link ISaveBuilder} itself.
     */
    @NotNull
    ISaveBuilder utf8();

    /**
     * Use ZIP compression for saved files.
     *
     * @return The {@link ISaveBuilder} itself.
     */
    @NotNull
    ISaveBuilder zip();

    /**
     * Use GZ compression for saved files.
     *
     * @return The {@link ISaveBuilder} itself.
     */
    @NotNull
    ISaveBuilder gz();

    /**
     * Sets the name of the saved files. It not set or set to null, uses the table name.
     *
     * @param name The saved files name.
     * @return The {@link ISaveBuilder} itself.
     */
    @NotNull
    ISaveBuilder name(@Nullable String name);

    /**
     * Sets the save folder. It not set or set to null, uses the current folder.
     *
     * @param folder The save folder.
     * @return The {@link ISaveBuilder} itself.
     */
    @NotNull
    ISaveBuilder folder(@Nullable String folder);

    /**
     * Delete the saved files if they already exists.
     *
     * @return The {@link ISaveBuilder} itself.
     */
    @NotNull
    ISaveBuilder delete();
}