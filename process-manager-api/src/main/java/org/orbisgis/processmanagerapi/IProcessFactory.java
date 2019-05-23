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
 * ProcessManager API is distributed under GPL 3 license.
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
package org.orbisgis.processmanagerapi;

import groovy.lang.Closure;

import java.util.LinkedHashMap;

/**
 * This interface defines the methods dedicated to the process creation and managing.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public interface IProcessFactory {

    /**
     * Return true if the process creation is locked, false otherwise.
     *
     * @return True if the process creation is locked, false otherwise.
     */
    boolean isLocked();

    /**
     * Return true if the factory if the default one, false otherwise.
     *
     * @return True if the factory if the default one, false otherwise.
     */
    boolean isDefault();

    /**
     * Returns the process with the given identifier.
     *
     * @param processId Identifier of the process to get.
     *
     * @return The process with the given identifier,
     */
    IProcess process(String processId);

    /**
     * Create a new Process with its title, description, keyword array, input map, output map, version
     * and the closure containing the code to execute on the process execution. The Closure will receive all the input
     * as parameter in the same order as the order of the input map. So the list of parameter of the closure, the
     * array of input and the map of input given on the execution should have the same size and meaning.
     *
     * @param title Title of the process.
     * @param description Human readable description of the process.
     * @param keywords List of simple keyword (one word) of the process.
     * @param inputs LinkedHashMap of inputs with the name as key and the input Object as value. The names will be used  to link
     *               the closure parameters with the execution input data map.
     * @param outputs LinkedHashMap of outputs with the name as key and the output Object as value. Those names will be used to
     *                    generate the LinkedHashMap of the getResults Method.
     * @param version Process version.
     * @param closure Closure containing the code to execute on the process execution.
     */
    IProcess create(String title, String description, String[] keywords, LinkedHashMap<String, Object> inputs,
                           LinkedHashMap<String, Object> outputs, String version, Closure closure);

    /**
     * Create a new Process with its title, description, keyword array, input map, output map
     * and the closure containing the code to execute on the process execution. The Closure will receive all the input
     * as parameter in the same order as the order of the input map. So the list of parameter of the closure, the
     * array of input and the map of input given on the execution should have the same size and meaning.
     *
     * @param title Title of the process.
     * @param description Human readable description of the process.
     * @param keywords List of simple keyword (one word) of the process.
     * @param inputs LinkedHashMap of inputs with the name as key and the input Object as value. The names will be used  to link
     *               the closure parameters with the execution input data map.
     * @param outputs LinkedHashMap of outputs with the name as key and the output Object as value. Those names will be used to
     *                    generate the LinkedHashMap of the getResults Method.
     * @param closure Closure containing the code to execute on the process execution.
     */
    IProcess create(String title, String description, String[] keywords, LinkedHashMap<String, Object> inputs,
                           LinkedHashMap<String, Object> outputs, Closure closure);

    /**
     * Create a new Process with its title, description, keyword array, output map, version
     * and the closure containing the code to execute on the process execution. The Closure will receive all the input
     * as parameter in the same order as the order of the input map. So the list of parameter of the closure, the
     * array of input and the map of input given on the execution should have the same size and meaning.
     *
     * @param title Title of the process.
     * @param description Human readable description of the process.
     * @param keywords List of simple keyword (one word) of the process.
     * @param outputs LinkedHashMap of outputs with the name as key and the output Object as value. Those names will be used to
     *                    generate the LinkedHashMap of the getResults Method.
     * @param version Process version.
     * @param closure Closure containing the code to execute on the process execution.
     */
    IProcess create(String title, String description, String[] keywords, LinkedHashMap<String, Object> outputs,
                           String version, Closure closure);

    /**
     * Create a new Process with its title, description, keyword array, output map
     * and the closure containing the code to execute on the process execution. The Closure will receive all the input
     * as parameter in the same order as the order of the input map. So the list of parameter of the closure, the
     * array of input and the map of input given on the execution should have the same size and meaning.
     *
     * @param title Title of the process.
     * @param description Human readable description of the process.
     * @param keywords List of simple keyword (one word) of the process.
     * @param outputs LinkedHashMap of outputs with the name as key and the output Object as value. Those names will be used to
     *                    generate the LinkedHashMap of the getResults Method.
     * @param closure Closure containing the code to execute on the process execution.
     */
    IProcess create(String title, String description, String[] keywords, LinkedHashMap<String, Object> outputs,
                           Closure closure);

    /**
     * Create a new Process with its title, description, input map, output map, version
     * and the closure containing the code to execute on the process execution. The Closure will receive all the input
     * as parameter in the same order as the order of the input map. So the list of parameter of the closure, the
     * array of input and the map of input given on the execution should have the same size and meaning.
     *
     * @param title Title of the process.
     * @param description Human readable description of the process.
     * @param inputs LinkedHashMap of inputs with the name as key and the input Object as value. The names will be used  to link
     *               the closure parameters with the execution input data map.
     * @param outputs LinkedHashMap of outputs with the name as key and the output Object as value. Those names will be used to
     *                    generate the LinkedHashMap of the getResults Method.
     * @param version Process version.
     * @param closure Closure containing the code to execute on the process execution.
     */
    IProcess create(String title, String description, LinkedHashMap<String, Object> inputs, LinkedHashMap<String, Object> outputs,
                           String version, Closure closure);

    /**
     * Create a new Process with its title, description, input map, output map
     * and the closure containing the code to execute on the process execution. The Closure will receive all the input
     * as parameter in the same order as the order of the input map. So the list of parameter of the closure, the
     * array of input and the map of input given on the execution should have the same size and meaning.
     *
     * @param title Title of the process.
     * @param description Human readable description of the process.
     * @param inputs LinkedHashMap of inputs with the name as key and the input Object as value. The names will be used  to link
     *               the closure parameters with the execution input data map.
     * @param outputs LinkedHashMap of outputs with the name as key and the output Object as value. Those names will be used to
     *                    generate the LinkedHashMap of the getResults Method.
     * @param closure Closure containing the code to execute on the process execution.
     */
    IProcess create(String title, String description,  LinkedHashMap<String, Object> inputs, LinkedHashMap<String, Object> outputs,
                           Closure closure);

    /**
     * Create a new Process with its title, description, output map, version
     * and the closure containing the code to execute on the process execution. The Closure will receive all the input
     * as parameter in the same order as the order of the input map. So the list of parameter of the closure, the
     * array of input and the map of input given on the execution should have the same size and meaning.
     *
     * @param title Title of the process.
     * @param description Human readable description of the process.
     * @param outputs LinkedHashMap of outputs with the name as key and the output Object as value. Those names will be used to
     *                    generate the LinkedHashMap of the getResults Method.
     * @param version Process version.
     * @param closure Closure containing the code to execute on the process execution.
     */
    IProcess create(String title, String description, LinkedHashMap<String, Object> outputs, String version,
                           Closure closure);

    /**
     * Create a new Process with its title, description, output map
     * and the closure containing the code to execute on the process execution. The Closure will receive all the input
     * as parameter in the same order as the order of the input map. So the list of parameter of the closure, the
     * array of input and the map of input given on the execution should have the same size and meaning.
     *
     * @param title Title of the process.
     * @param description Human readable description of the process.
     * @param outputs LinkedHashMap of outputs with the name as key and the output Object as value. Those names will be used to
     *                    generate the LinkedHashMap of the getResults Method.
     * @param closure Closure containing the code to execute on the process execution.
     */
    IProcess create(String title, String description, LinkedHashMap<String, Object> outputs, Closure closure);

    /**
     * Create a new Process with its title, keyword array, input map, output map, version
     * and the closure containing the code to execute on the process execution. The Closure will receive all the input
     * as parameter in the same order as the order of the input map. So the list of parameter of the closure, the
     * array of input and the map of input given on the execution should have the same size and meaning.
     *
     * @param title Title of the process.
     * @param keywords List of simple keyword (one word) of the process.
     * @param inputs LinkedHashMap of inputs with the name as key and the input Object as value. The names will be used  to link
     *               the closure parameters with the execution input data map.
     * @param outputs LinkedHashMap of outputs with the name as key and the output Object as value. Those names will be used to
     *                    generate the LinkedHashMap of the getResults Method.
     * @param version Process version.
     * @param closure Closure containing the code to execute on the process execution.
     */
    IProcess create(String title, String[] keywords, LinkedHashMap<String, Object> inputs, LinkedHashMap<String, Object> outputs,
                           String version, Closure closure);

    /**
     * Create a new Process with its title, keyword array, input map, output map
     * and the closure containing the code to execute on the process execution. The Closure will receive all the input
     * as parameter in the same order as the order of the input map. So the list of parameter of the closure, the
     * array of input and the map of input given on the execution should have the same size and meaning.
     *
     * @param title Title of the process.
     * @param keywords List of simple keyword (one word) of the process.
     * @param inputs LinkedHashMap of inputs with the name as key and the input Object as value. The names will be used  to link
     *               the closure parameters with the execution input data map.
     * @param outputs LinkedHashMap of outputs with the name as key and the output Object as value. Those names will be used to
     *                    generate the LinkedHashMap of the getResults Method.
     * @param closure Closure containing the code to execute on the process execution.
     */
    IProcess create(String title, String[] keywords, LinkedHashMap<String, Object> inputs, LinkedHashMap<String, Object> outputs,
                           Closure closure);

    /**
     * Create a new Process with its title, keyword array, output map, version
     * and the closure containing the code to execute on the process execution. The Closure will receive all the input
     * as parameter in the same order as the order of the input map. So the list of parameter of the closure, the
     * array of input and the map of input given on the execution should have the same size and meaning.
     *
     * @param title Title of the process.
     * @param keywords List of simple keyword (one word) of the process.
     * @param outputs LinkedHashMap of outputs with the name as key and the output Object as value. Those names will be used to
     *                    generate the LinkedHashMap of the getResults Method.
     * @param version Process version.
     * @param closure Closure containing the code to execute on the process execution.
     */
    IProcess create(String title, String[] keywords, LinkedHashMap<String, Object> outputs, String version,
                           Closure closure);

    /**
     * Create a new Process with its title, keyword array, output map
     * and the closure containing the code to execute on the process execution. The Closure will receive all the input
     * as parameter in the same order as the order of the input map. So the list of parameter of the closure, the
     * array of input and the map of input given on the execution should have the same size and meaning.
     *
     * @param title Title of the process.
     * @param keywords List of simple keyword (one word) of the process.
     * @param outputs LinkedHashMap of outputs with the name as key and the output Object as value. Those names will be used to
     *                    generate the LinkedHashMap of the getResults Method.
     * @param closure Closure containing the code to execute on the process execution.
     */
    IProcess create(String title, String[] keywords, LinkedHashMap<String, Object> outputs, Closure closure);

    /**
     * Create a new Process with its title, input map, output map, version
     * and the closure containing the code to execute on the process execution. The Closure will receive all the input
     * as parameter in the same order as the order of the input map. So the list of parameter of the closure, the
     * array of input and the map of input given on the execution should have the same size and meaning.
     *
     * @param title Title of the process.
     * @param inputs LinkedHashMap of inputs with the name as key and the input Object as value. The names will be used  to link
     *               the closure parameters with the execution input data map.
     * @param outputs LinkedHashMap of outputs with the name as key and the output Object as value. Those names will be used to
     *                    generate the LinkedHashMap of the getResults Method.
     * @param version Process version.
     * @param closure Closure containing the code to execute on the process execution.
     */
    IProcess create(String title, LinkedHashMap<String, Object> inputs, LinkedHashMap<String, Object> outputs, String version,
                           Closure closure);

    /**
     * Create a new Process with its title, input map, output map
     * and the closure containing the code to execute on the process execution. The Closure will receive all the input
     * as parameter in the same order as the order of the input map. So the list of parameter of the closure, the
     * array of input and the map of input given on the execution should have the same size and meaning.
     *
     * @param title Title of the process.
     * @param inputs LinkedHashMap of inputs with the name as key and the input Object as value. The names will be used  to link
     *               the closure parameters with the execution input data map.
     * @param outputs LinkedHashMap of outputs with the name as key and the output Object as value. Those names will be used to
     *                    generate the LinkedHashMap of the getResults Method.
     * @param closure Closure containing the code to execute on the process execution.
     */
    IProcess create(String title, LinkedHashMap<String, Object> inputs, LinkedHashMap<String, Object> outputs, Closure closure);

    /**
     * Create a new Process with its title, output map, version
     * and the closure containing the code to execute on the process execution. The Closure will receive all the input
     * as parameter in the same order as the order of the input map. So the list of parameter of the closure, the
     * array of input and the map of input given on the execution should have the same size and meaning.
     *
     * @param title Title of the process.
     * @param outputs LinkedHashMap of outputs with the name as key and the output Object as value. Those names will be used to
     *                    generate the LinkedHashMap of the getResults Method.
     * @param version Process version.
     * @param closure Closure containing the code to execute on the process execution.
     */
    IProcess create(String title, LinkedHashMap<String, Object> outputs, String version, Closure closure);

    /**
     * Create a new Process with its title, output map
     * and the closure containing the code to execute on the process execution. The Closure will receive all the input
     * as parameter in the same order as the order of the input map. So the list of parameter of the closure, the
     * array of input and the map of input given on the execution should have the same size and meaning.
     *
     * @param title Title of the process.
     * @param outputs LinkedHashMap of outputs with the name as key and the output Object as value. Those names will be used to
     *                    generate the LinkedHashMap of the getResults Method.
     * @param closure Closure containing the code to execute on the process execution.
     */
    IProcess create(String title, LinkedHashMap<String, Object> outputs, Closure closure);
}
