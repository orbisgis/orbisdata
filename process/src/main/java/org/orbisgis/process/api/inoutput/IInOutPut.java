/*
 * Bundle ProcessManager API is part of the OrbisGIS platform
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
 * ProcessManager API is distributed under LGPL 3 license.
 *
 * Copyright (C) 2018 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * ProcessManager API is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * ProcessManager API is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * ProcessManager API. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.process.api.inoutput;

import org.orbisgis.process.api.IProcess;

import java.util.Optional;

/**
 * This interface defines the methods dedicated the wrapping of input/output.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC 2019-2020)
 */
public interface IInOutPut {

    /**
     * Return the input/output name.
     *
     * @return The input/output name.
     */
    Optional<String> getName();

    /**
     * Sets the name of the input/output.
     *
     * @param name Name of the input/output.
     */
    void setName(String name);

    /**
     * Sets the name of the input/output.
     *
     * @param name Name of the input/output.
     * @return Itself.
     */
    IInOutPut name(String name);

    /**
     * Return the {@link IProcess} of the input/output.
     *
     * @return The {@link IProcess} of the input/output.
     */
    Optional<IProcess> getProcess();

    /**
     * Sets the {@link IProcess} of the input/output.
     *
     * @param process The {@link IProcess} of the input/output.
     */
    void setProcess(IProcess process);

    /**
     * Sets the {@link IProcess} of the input/output.
     *
     * @param process The {@link IProcess} of the input/output.
     * @return Itself.
     */
    IInOutPut process(IProcess process);

    /**
     * Return the type of the input/output.
     *
     * @return The type of the input/output.
     */
    Optional<Class<?>> getType();

    /**
     * Sets the type of the input/output.
     *
     * @param type The type of the input/output.
     */
    void setType(Class<?> type);

    /**
     * Sets the type of the input/output.
     *
     * @param type The type of the input/output.
     * @return Itself.
     */
    IInOutPut type(Class<?> type);

    /**
     * Return the title or the input/output.
     *
     * @return The title of the input/output.
     */
    Optional<String> getTitle();

    /**
     * Sets the title of the input/output.
     *
     * @param title The title of the input/output.
     */
    void setTitle(String title);

    /**
     * Sets the title of the input/output.
     *
     * @param title The title of the input/output.
     * @return Itself.
     */
    IInOutPut title(String title);

    /**
     * Return the description of the input/output.
     *
     * @return The description of the input/output.
     */
    Optional<String> getDescription();

    /**
     * Sets the description of the input/output.
     *
     * @param description The description of the input/output.
     */
    void setDescription(String description);

    /**
     * Sets the description of the input/output.
     *
     * @param description The description of the input/output.
     * @return Itself.
     */
    IInOutPut description(String description);

    /**
     * Return the keywords of the input/output.
     *
     * @return The keywords of the input/output.
     */
    Optional<String[]> getKeywords();

    /**
     * Sets the keywords of the input/output.
     *
     * @param keywords The keywords of the input/output.
     */
    void setKeywords(String[] keywords);

    /**
     * Sets the keywords of the input/output.
     *
     * @param keywords The keywords of the input/output.
     * @return Itself.
     */
    IInOutPut keywords(String[] keywords);

    /**
     * Returns a copy the the current object.
     *
     * @return A copy of the current object.
     */
    IInOutPut copy();
}
