package org.orbisgis.processmanagerapi;

import groovy.lang.Closure;

import java.util.Map;

/**
 * This interface defines the methods dedicated to the process creation and managing.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public interface IProcessFactory {

    /**
     * Create a new Process with its title, description, keyword array, input map, output map, version
     * and the closure containing the code to execute on the process execution. The Closure will receive all the input
     * as parameter in the same order as the order of the input map. So the list of parameter of the closure, the
     * array of input and the map of input given on the execution should have the same size and meaning.
     *
     * @param title Title of the process.
     * @param description Human readable description of the process.
     * @param keywords List of simple keyword (one word) of the process.
     * @param inputs Map of inputs with the name as key and the input class as value. The names will be used  to link
     *               the closure parameters with the execution input data map.
     * @param outputs Map of outputs with the name as key and the output class as value. Those names will be used to
     *                    generate the Map of the getResults Method.
     * @param version Process version.
     * @param closure Closure containing the code to execute on the process execution.
     */
    IProcess create(String title, String description, String[] keywords, Map<String, Class> inputs,
                           Map<String, Class> outputs, String version, Closure closure);

    /**
     * Create a new Process with its title, description, keyword array, input map, output map
     * and the closure containing the code to execute on the process execution. The Closure will receive all the input
     * as parameter in the same order as the order of the input map. So the list of parameter of the closure, the
     * array of input and the map of input given on the execution should have the same size and meaning.
     *
     * @param title Title of the process.
     * @param description Human readable description of the process.
     * @param keywords List of simple keyword (one word) of the process.
     * @param inputs Map of inputs with the name as key and the input class as value. The names will be used  to link
     *               the closure parameters with the execution input data map.
     * @param outputs Map of outputs with the name as key and the output class as value. Those names will be used to
     *                    generate the Map of the getResults Method.
     * @param closure Closure containing the code to execute on the process execution.
     */
    IProcess create(String title, String description, String[] keywords, Map<String, Class> inputs,
                           Map<String, Class> outputs, Closure closure);

    /**
     * Create a new Process with its title, description, keyword array, output map, version
     * and the closure containing the code to execute on the process execution. The Closure will receive all the input
     * as parameter in the same order as the order of the input map. So the list of parameter of the closure, the
     * array of input and the map of input given on the execution should have the same size and meaning.
     *
     * @param title Title of the process.
     * @param description Human readable description of the process.
     * @param keywords List of simple keyword (one word) of the process.
     * @param outputs Map of outputs with the name as key and the output class as value. Those names will be used to
     *                    generate the Map of the getResults Method.
     * @param version Process version.
     * @param closure Closure containing the code to execute on the process execution.
     */
    IProcess create(String title, String description, String[] keywords, Map<String, Class> outputs,
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
     * @param outputs Map of outputs with the name as key and the output class as value. Those names will be used to
     *                    generate the Map of the getResults Method.
     * @param closure Closure containing the code to execute on the process execution.
     */
    IProcess create(String title, String description, String[] keywords, Map<String, Class> outputs,
                           Closure closure);

    /**
     * Create a new Process with its title, description, input map, output map, version
     * and the closure containing the code to execute on the process execution. The Closure will receive all the input
     * as parameter in the same order as the order of the input map. So the list of parameter of the closure, the
     * array of input and the map of input given on the execution should have the same size and meaning.
     *
     * @param title Title of the process.
     * @param description Human readable description of the process.
     * @param inputs Map of inputs with the name as key and the input class as value. The names will be used  to link
     *               the closure parameters with the execution input data map.
     * @param outputs Map of outputs with the name as key and the output class as value. Those names will be used to
     *                    generate the Map of the getResults Method.
     * @param version Process version.
     * @param closure Closure containing the code to execute on the process execution.
     */
    IProcess create(String title, String description, Map<String, Class> inputs, Map<String, Class> outputs,
                           String version, Closure closure);

    /**
     * Create a new Process with its title, description, input map, output map
     * and the closure containing the code to execute on the process execution. The Closure will receive all the input
     * as parameter in the same order as the order of the input map. So the list of parameter of the closure, the
     * array of input and the map of input given on the execution should have the same size and meaning.
     *
     * @param title Title of the process.
     * @param description Human readable description of the process.
     * @param inputs Map of inputs with the name as key and the input class as value. The names will be used  to link
     *               the closure parameters with the execution input data map.
     * @param outputs Map of outputs with the name as key and the output class as value. Those names will be used to
     *                    generate the Map of the getResults Method.
     * @param closure Closure containing the code to execute on the process execution.
     */
    IProcess create(String title, String description,  Map<String, Class> inputs, Map<String, Class> outputs,
                           Closure closure);

    /**
     * Create a new Process with its title, description, output map, version
     * and the closure containing the code to execute on the process execution. The Closure will receive all the input
     * as parameter in the same order as the order of the input map. So the list of parameter of the closure, the
     * array of input and the map of input given on the execution should have the same size and meaning.
     *
     * @param title Title of the process.
     * @param description Human readable description of the process.
     * @param outputs Map of outputs with the name as key and the output class as value. Those names will be used to
     *                    generate the Map of the getResults Method.
     * @param version Process version.
     * @param closure Closure containing the code to execute on the process execution.
     */
    IProcess create(String title, String description, Map<String, Class> outputs, String version,
                           Closure closure);

    /**
     * Create a new Process with its title, description, output map
     * and the closure containing the code to execute on the process execution. The Closure will receive all the input
     * as parameter in the same order as the order of the input map. So the list of parameter of the closure, the
     * array of input and the map of input given on the execution should have the same size and meaning.
     *
     * @param title Title of the process.
     * @param description Human readable description of the process.
     * @param outputs Map of outputs with the name as key and the output class as value. Those names will be used to
     *                    generate the Map of the getResults Method.
     * @param closure Closure containing the code to execute on the process execution.
     */
    IProcess create(String title, String description, Map<String, Class> outputs, Closure closure);

    /**
     * Create a new Process with its title, keyword array, input map, output map, version
     * and the closure containing the code to execute on the process execution. The Closure will receive all the input
     * as parameter in the same order as the order of the input map. So the list of parameter of the closure, the
     * array of input and the map of input given on the execution should have the same size and meaning.
     *
     * @param title Title of the process.
     * @param keywords List of simple keyword (one word) of the process.
     * @param inputs Map of inputs with the name as key and the input class as value. The names will be used  to link
     *               the closure parameters with the execution input data map.
     * @param outputs Map of outputs with the name as key and the output class as value. Those names will be used to
     *                    generate the Map of the getResults Method.
     * @param version Process version.
     * @param closure Closure containing the code to execute on the process execution.
     */
    IProcess create(String title, String[] keywords, Map<String, Class> inputs, Map<String, Class> outputs,
                           String version, Closure closure);

    /**
     * Create a new Process with its title, keyword array, input map, output map
     * and the closure containing the code to execute on the process execution. The Closure will receive all the input
     * as parameter in the same order as the order of the input map. So the list of parameter of the closure, the
     * array of input and the map of input given on the execution should have the same size and meaning.
     *
     * @param title Title of the process.
     * @param keywords List of simple keyword (one word) of the process.
     * @param inputs Map of inputs with the name as key and the input class as value. The names will be used  to link
     *               the closure parameters with the execution input data map.
     * @param outputs Map of outputs with the name as key and the output class as value. Those names will be used to
     *                    generate the Map of the getResults Method.
     * @param closure Closure containing the code to execute on the process execution.
     */
    IProcess create(String title, String[] keywords, Map<String, Class> inputs, Map<String, Class> outputs,
                           Closure closure);

    /**
     * Create a new Process with its title, keyword array, output map, version
     * and the closure containing the code to execute on the process execution. The Closure will receive all the input
     * as parameter in the same order as the order of the input map. So the list of parameter of the closure, the
     * array of input and the map of input given on the execution should have the same size and meaning.
     *
     * @param title Title of the process.
     * @param keywords List of simple keyword (one word) of the process.
     * @param outputs Map of outputs with the name as key and the output class as value. Those names will be used to
     *                    generate the Map of the getResults Method.
     * @param version Process version.
     * @param closure Closure containing the code to execute on the process execution.
     */
    IProcess create(String title, String[] keywords, Map<String, Class> outputs, String version,
                           Closure closure);

    /**
     * Create a new Process with its title, keyword array, output map
     * and the closure containing the code to execute on the process execution. The Closure will receive all the input
     * as parameter in the same order as the order of the input map. So the list of parameter of the closure, the
     * array of input and the map of input given on the execution should have the same size and meaning.
     *
     * @param title Title of the process.
     * @param keywords List of simple keyword (one word) of the process.
     * @param outputs Map of outputs with the name as key and the output class as value. Those names will be used to
     *                    generate the Map of the getResults Method.
     * @param closure Closure containing the code to execute on the process execution.
     */
    IProcess create(String title, String[] keywords, Map<String, Class> outputs, Closure closure);

    /**
     * Create a new Process with its title, input map, output map, version
     * and the closure containing the code to execute on the process execution. The Closure will receive all the input
     * as parameter in the same order as the order of the input map. So the list of parameter of the closure, the
     * array of input and the map of input given on the execution should have the same size and meaning.
     *
     * @param title Title of the process.
     * @param inputs Map of inputs with the name as key and the input class as value. The names will be used  to link
     *               the closure parameters with the execution input data map.
     * @param outputs Map of outputs with the name as key and the output class as value. Those names will be used to
     *                    generate the Map of the getResults Method.
     * @param version Process version.
     * @param closure Closure containing the code to execute on the process execution.
     */
    IProcess create(String title, Map<String, Class> inputs, Map<String, Class> outputs, String version,
                           Closure closure);

    /**
     * Create a new Process with its title, input map, output map
     * and the closure containing the code to execute on the process execution. The Closure will receive all the input
     * as parameter in the same order as the order of the input map. So the list of parameter of the closure, the
     * array of input and the map of input given on the execution should have the same size and meaning.
     *
     * @param title Title of the process.
     * @param inputs Map of inputs with the name as key and the input class as value. The names will be used  to link
     *               the closure parameters with the execution input data map.
     * @param outputs Map of outputs with the name as key and the output class as value. Those names will be used to
     *                    generate the Map of the getResults Method.
     * @param closure Closure containing the code to execute on the process execution.
     */
    IProcess create(String title, Map<String, Class> inputs, Map<String, Class> outputs, Closure closure);

    /**
     * Create a new Process with its title, output map, version
     * and the closure containing the code to execute on the process execution. The Closure will receive all the input
     * as parameter in the same order as the order of the input map. So the list of parameter of the closure, the
     * array of input and the map of input given on the execution should have the same size and meaning.
     *
     * @param title Title of the process.
     * @param outputs Map of outputs with the name as key and the output class as value. Those names will be used to
     *                    generate the Map of the getResults Method.
     * @param version Process version.
     * @param closure Closure containing the code to execute on the process execution.
     */
    IProcess create(String title, Map<String, Class> outputs, String version, Closure closure);

    /**
     * Create a new Process with its title, output map
     * and the closure containing the code to execute on the process execution. The Closure will receive all the input
     * as parameter in the same order as the order of the input map. So the list of parameter of the closure, the
     * array of input and the map of input given on the execution should have the same size and meaning.
     *
     * @param title Title of the process.
     * @param outputs Map of outputs with the name as key and the output class as value. Those names will be used to
     *                    generate the Map of the getResults Method.
     * @param closure Closure containing the code to execute on the process execution.
     */
    IProcess create(String title, Map<String, Class> outputs, Closure closure);
}
