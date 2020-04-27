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
package org.orbisgis.orbisdata.datamanager.api.datasource;

import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.commons.annotations.Nullable;
import org.orbisgis.orbisdata.datamanager.api.dataset.IDataSet;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.Map;

/**
 * Raw source of data.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (Lab-STICC UBS 2018-2019)
 */
public interface IDataSource<T> {

    /**
     * Return the {@link IDataSet} implementation corresponding to the given name or null if not {@link IDataSet found}.
     *
     * @param name Name of the {@link IDataSet}.
     * @return The implementation of {@link IDataSet} corresponding to the given name or null.
     */
    @Nullable
    IDataSet<T, T> getDataSet(@NotNull String name);

    /**
     * Return the location of the {@link IDataSourceLocation}
     *
     * @return The location of the {@link IDataSourceLocation}
     */
    @Nullable
    IDataSourceLocation getLocation();

    /**
     * Convert the current object into another with the given class.
     *
     * @param clazz New class of the result.
     * @return The current object into an other class.
     */
    @Nullable
    Object asType(@NotNull Class<?> clazz);

    @Override
    @NotNull
    String toString();


    /* ********************** */
    /*      Load methods      */
    /* ********************** */

    /**
     * Load a file into the {@link IDataSource}.
     *
     * @param filePath Path of the file.
     * @return The {@link IDataSet} containing the loaded data.
     */
    @Nullable
    IDataSet<?, ?> load(@NotNull String filePath);

    /**
     * Load a file into the {@link IDataSource}.
     *
     * @param filePath Path of the file.
     * @param delete   True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The {@link IDataSet} containing the loaded data.
     */
    @Nullable
    IDataSet<?, ?> load(@NotNull String filePath, boolean delete);

    /**
     * Load a file to the {@link IDataSource}.
     *
     * @param filePath  Path of the file.
     * @param dataSetId Identifier of the {@link IDataSet}.
     * @return The {@link IDataSet} containing the loaded data.
     */
    @Nullable
    IDataSet<?, ?> load(@NotNull String filePath, @NotNull String dataSetId);

    /**
     * Load a file to the {@link IDataSource}.
     *
     * @param filePath  Path of the file.
     * @param dataSetId Identifier of the {@link IDataSet}.
     * @param delete    True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The {@link IDataSet} containing the loaded data.
     */
    @Nullable
    IDataSet<?, ?> load(@NotNull String filePath, @NotNull String dataSetId, boolean delete);

    /**
     * Load a file to the {@link IDataSource}.
     *
     * @param filePath  Path of the file.
     * @param dataSetId Identifier of the {@link IDataSet}.
     * @param encoding  Encoding of the loaded file.
     * @param delete    True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The {@link IDataSet} containing the loaded data.
     */
    @Nullable
    IDataSet<?, ?> load(@NotNull String filePath, @NotNull String dataSetId, @Nullable String encoding, boolean delete);

    /**
     * Load a file into the {@link IDataSource}.
     *
     * @param url {@link URL} of the file.
     * @return The {@link IDataSet} containing the loaded data.
     */
    @Nullable
    IDataSet<?, ?> load(@NotNull URL url);

    /**
     * Load a file into the {@link IDataSource}.
     *
     * @param url    {@link URL} of the file.
     * @param delete True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The {@link IDataSet} containing the loaded data.
     */
    @Nullable
    IDataSet<?, ?> load(@NotNull URL url, boolean delete);

    /**
     * Load a file to the {@link IDataSource}.
     *
     * @param url       {@link URL} of the file.
     * @param dataSetId Name of the table.
     * @return The {@link IDataSet} containing the loaded data.
     */
    @Nullable
    IDataSet<?, ?> load(@NotNull URL url, @NotNull String dataSetId);

    /**
     * Load a file to the {@link IDataSource}.
     *
     * @param url       {@link URL} of the file.
     * @param dataSetId Name of the table.
     * @param delete    True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The {@link IDataSet} containing the loaded data.
     */
    @Nullable
    IDataSet<?, ?> load(@NotNull URL url, @NotNull String dataSetId, boolean delete);

    /**
     * Load a file to the {@link IDataSource}.
     *
     * @param url       {@link URL} of the file.
     * @param dataSetId Name of the table
     * @param encoding  Encoding of the loaded file.
     * @param delete    True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The {@link IDataSet} containing the loaded data.
     */
    @Nullable
    IDataSet<?, ?> load(@NotNull URL url, @NotNull String dataSetId, @Nullable String encoding, boolean delete);

    /**
     * Load a file into the {@link IDataSource}.
     *
     * @param uri {@link URI} of the file.
     * @return The {@link IDataSet} containing the loaded data.
     */
    @Nullable
    IDataSet<?, ?> load(@NotNull URI uri);

    /**
     * Load a file into the {@link IDataSource}.
     *
     * @param uri    {@link URI} of the file.
     * @param delete True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The {@link IDataSet} containing the loaded data.
     */
    @Nullable
    IDataSet<?, ?> load(@NotNull URI uri, boolean delete);

    /**
     * Load a file to the {@link IDataSource}.
     *
     * @param uri       {@link URI} of the file.
     * @param dataSetId Name of the table.
     * @return The {@link IDataSet} containing the loaded data.
     */
    @Nullable
    IDataSet<?, ?> load(@NotNull URI uri, @NotNull String dataSetId);

    /**
     * Load a file to the {@link IDataSource}.
     *
     * @param uri       {@link URI} of the file.
     * @param dataSetId Name of the table.
     * @param delete    True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The {@link IDataSet} containing the loaded data.
     */
    @Nullable
    IDataSet<?, ?> load(@NotNull URI uri, @NotNull String dataSetId, boolean delete);

    /**
     * Load a file to the {@link IDataSource}.
     *
     * @param uri       {@link URI} of the file.
     * @param dataSetId Name of the table
     * @param encoding  Encoding of the loaded file.
     * @param delete    True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The {@link IDataSet} containing the loaded data.
     */
    @Nullable
    IDataSet<?, ?> load(@NotNull URI uri, @NotNull String dataSetId, @Nullable String encoding, boolean delete);

    /**
     * Load a file into the {@link IDataSource}.
     *
     * @param file {@link File}.
     * @return The {@link IDataSet} containing the loaded data.
     */
    @Nullable
    IDataSet<?, ?> load(@NotNull File file);

    /**
     * Load a file into the {@link IDataSource}.
     *
     * @param file   {@link File}.
     * @param delete True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The {@link IDataSet} containing the loaded data.
     */
    @Nullable
    IDataSet<?, ?> load(@NotNull File file, boolean delete);

    /**
     * Load a file to the {@link IDataSource}.
     *
     * @param file      {@link File}.
     * @param dataSetId Name of the table.
     * @return The {@link IDataSet} containing the loaded data.
     */
    @Nullable
    IDataSet<?, ?> load(@NotNull File file, @NotNull String dataSetId);

    /**
     * Load a file to the {@link IDataSource}.
     *
     * @param file      {@link File}.
     * @param dataSetId Name of the table.
     * @param delete    True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The {@link IDataSet} containing the loaded data.
     */
    @Nullable
    IDataSet<?, ?> load(@NotNull File file, @NotNull String dataSetId, boolean delete);

    /**
     * Load a file to the {@link IDataSource}.
     *
     * @param file      {@link File}.
     * @param dataSetId Name of the table
     * @param encoding  Encoding of the loaded file.
     * @param delete    True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The {@link IDataSet} containing the loaded data.
     */
    @Nullable
    IDataSet<?, ?> load(@NotNull File file, @NotNull String dataSetId, @Nullable String encoding, boolean delete);

    /**
     * Load a table from another {@link IDataSource}.
     *
     * @param properties     Properties used to connect to the database.
     * @param inputdataSetId Name of the table to import.
     * @return The {@link IDataSet} containing the loaded data.
     */
    @Nullable
    IDataSet<?, ?> load(@NotNull Map<String, String> properties, @NotNull String inputdataSetId);


    /**
     * Load a table from another {@link IDataSource}.
     *
     * @param properties      Properties used to connect to the database.
     * @param inputdataSetId  Name of the table to import.
     * @param outputdataSetId Name of the imported table in the database.
     * @return The {@link IDataSet} containing the loaded data.
     */
    @Nullable
    IDataSet<?, ?> load(@NotNull Map<String, String> properties, @NotNull String inputdataSetId,
                    @NotNull String outputdataSetId);

    /**
     * Load a table from another {@link IDataSource}.
     *
     * @param properties     Properties used to connect to the database.
     * @param inputdataSetId Name of the table to import.
     * @param delete         True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The {@link IDataSet} containing the loaded data.
     */
    @Nullable
    IDataSet<?, ?> load(@NotNull Map<String, String> properties, @NotNull String inputdataSetId, boolean delete);

    /**
     * Load a table from another {@link IDataSource}.
     *
     * @param properties      Properties used to connect to the database.
     * @param inputdataSetId  Name of the table to import.
     * @param outputdataSetId Name of the imported table in the database.
     * @param delete          True to delete the {@link IDataSet} if exists, false otherwise.
     * @return The {@link IDataSet} containing the loaded data.
     */
    @Nullable
    IDataSet<?, ?> load(@NotNull Map<String, String> properties, @NotNull String inputdataSetId,
                    @NotNull String outputdataSetId, boolean delete);


    /* ********************** */
    /*      Save methods      */
    /* ********************** */

    /**
     * Save a table into a file.
     *
     * @param dataSetId Name of the table to save.
     * @param filePath  Path of the file where the table will be saved.
     * @return True if the file has been successfully saved, false otherwise.
     */
    boolean save(@NotNull String dataSetId, @NotNull String filePath);

    /**
     * Save a table into a file.
     *
     * @param dataSetId Name of the table to save.
     * @param filePath  Path of the file where the table will be saved.
     * @param encoding  Encoding of the file.
     * @return True if the file has been successfully saved, false otherwise.
     */
    boolean save(@NotNull String dataSetId, @NotNull String filePath, @Nullable String encoding);

    /**
     * Save a table into a file.
     *
     * @param dataSetId Name of the table to save.
     * @param uri       {@link URI} of the file where the table will be saved.
     * @return True if the file has been successfully saved, false otherwise.
     */
    boolean save(@NotNull String dataSetId, @NotNull URI uri);

    /**
     * Save a table into a file.
     *
     * @param dataSetId Name of the table to save.
     * @param uri       {@link URI} of the file where the table will be saved.
     * @param encoding  Encoding of the file.
     * @return True if the file has been successfully saved, false otherwise.
     */
    boolean save(@NotNull String dataSetId, @NotNull URI uri, @Nullable String encoding);

    /**
     * Save a table into a file.
     *
     * @param dataSetId Name of the table to save.
     * @param url       {@link URL} of the file where the table will be saved.
     * @return True if the file has been successfully saved, false otherwise.
     */
    boolean save(@NotNull String dataSetId, @NotNull URL url);

    /**
     * Save a table into a file.
     *
     * @param dataSetId Name of the table to save.
     * @param url       {@link URL} of the file where the table will be saved.
     * @param encoding  Encoding of the file.
     * @return True if the file has been successfully saved, false otherwise.
     */
    boolean save(@NotNull String dataSetId, @NotNull URL url, @Nullable String encoding);

    /**
     * Save a table into a file.
     *
     * @param dataSetId Name of the table to save.
     * @param file      {@link File} of the file where the table will be saved.
     * @return True if the file has been successfully saved, false otherwise.
     */
    boolean save(@NotNull String dataSetId, @NotNull File file);

    /**
     * Save a table into a file.
     *
     * @param dataSetId Name of the table to save.
     * @param file      {@link File} of the file where the table will be saved.
     * @param encoding  Encoding of the file.
     * @return True if the file has been successfully saved, false otherwise.
     */
    boolean save(@NotNull String dataSetId, @NotNull File file, @Nullable String encoding);


    /* ********************** */
    /*      Link methods      */
    /* ********************** */

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param filePath  Path of the file.
     * @param dataSetId Identifier of the {@link IDataSet}.
     * @param delete    True to delete the {@link IDataSet} if exists, false otherwise.
     * @return A {@link IDataSet} representing the linked file.
     */
    @Nullable
    IDataSet<?, ?> link(@NotNull String filePath, @NotNull String dataSetId, boolean delete);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param filePath  Path of the file.
     * @param dataSetId Identifier of the {@link IDataSet}.
     * @return A {@link IDataSet} representing the linked file.
     */
    @Nullable
    IDataSet<?, ?> link(@NotNull String filePath, @NotNull String dataSetId);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param filePath Path of the file.
     * @param delete   True to delete the {@link IDataSet} if exists, false otherwise.
     * @return A {@link IDataSet} representing the linked file.
     */
    @Nullable
    IDataSet<?, ?> link(@NotNull String filePath, boolean delete);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param filePath Path of the file to link.
     * @return A {@link IDataSet} representing the linked file.
     */
    @Nullable
    IDataSet<?, ?> link(@NotNull String filePath);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param uri       {@link URI} of the file.
     * @param dataSetId Identifier of the {@link IDataSet}.
     * @param delete    True to delete the {@link IDataSet} if exists, false otherwise.
     * @return A {@link IDataSet} representing the linked file.
     */
    @Nullable
    IDataSet<?, ?> link(@NotNull URI uri, @Nullable String dataSetId, boolean delete);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param uri       {@link URI} of the file.
     * @param dataSetId Identifier of the {@link IDataSet}.
     * @return A {@link IDataSet} representing the linked file.
     */
    @Nullable
    IDataSet<?, ?> link(@NotNull URI uri, @Nullable String dataSetId);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param uri    {@link URI} of the file.
     * @param delete True to delete the {@link IDataSet} if exists, false otherwise.
     * @return A {@link IDataSet} representing the linked file.
     */
    @Nullable
    IDataSet<?, ?> link(@NotNull URI uri, boolean delete);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param uri {@link URI} of the file.
     * @return A {@link IDataSet} representing the linked file.
     */
    @Nullable
    IDataSet<?, ?> link(@NotNull URI uri);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param url       {@link URL} of the file.
     * @param dataSetId Identifier of the {@link IDataSet}.
     * @param delete    True to delete the {@link IDataSet} if exists, false otherwise.
     * @return A {@link IDataSet} representing the linked file.
     */
    @Nullable
    IDataSet<?, ?> link(@NotNull URL url, @Nullable String dataSetId, boolean delete);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param url       {@link URI} of the file.
     * @param dataSetId Identifier of the {@link IDataSet}.
     * @return A {@link IDataSet} representing the linked file.
     */
    @Nullable
    IDataSet<?, ?> link(@NotNull URL url, @Nullable String dataSetId);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param url    {@link URI} of the file.
     * @param delete True to delete the {@link IDataSet} if exists, false otherwise.
     * @return A {@link IDataSet} representing the linked file.
     */
    @Nullable
    IDataSet<?, ?> link(@NotNull URL url, boolean delete);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param url {@link URI} of the file.
     * @return A {@link IDataSet} representing the linked file.
     */
    @Nullable
    IDataSet<?, ?> link(@NotNull URL url);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param file      {@link File}.
     * @param dataSetId Identifier of the {@link IDataSet}.
     * @param delete    True to delete the {@link IDataSet} if exists, false otherwise.
     * @return A {@link IDataSet} representing the linked file.
     */
    @Nullable
    IDataSet<?, ?> link(@NotNull File file, @NotNull String dataSetId, boolean delete);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param file      {@link File}.
     * @param dataSetId Identifier of the {@link IDataSet}.
     * @return A {@link IDataSet} representing the linked file.
     */
    @Nullable
    IDataSet<?, ?> link(@NotNull File file, @NotNull String dataSetId);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param file   {@link File}.
     * @param delete True to delete the {@link IDataSet} if exists, false otherwise.
     * @return A {@link IDataSet} representing the linked file.
     */
    @Nullable
    IDataSet<?, ?> link(@NotNull File file, boolean delete);

    /**
     * Link a file to the {@link IDataSource}.
     *
     * @param file {@link File}.
     * @return A {@link IDataSet} representing the linked file.
     */
    @Nullable
    IDataSet<?, ?> link(@NotNull File file);
}
