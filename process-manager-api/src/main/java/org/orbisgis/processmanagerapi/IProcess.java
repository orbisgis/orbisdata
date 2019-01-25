package org.orbisgis.processmanagerapi;

import java.util.Map;

/**
 * Interface defining the main methods of a process.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public interface IProcess {

    /**
     * Execute the process with the given inputs.
     *
     * @param inputDataMap Map of the inputs with the name as key and the input value as value.
     *
     * @return True if the execution is successful, false otherwise.
     */
    boolean execute(Map<String, String> inputDataMap);

    /**
     * Return the title of the process.
     *
     * @return The title of the process.
     */
    String getTitle();

    /**
     * Return the process version.
     *
     * @return The process version.
     */
    String getVersion();

    /**
     * Return the human readable description of the process.
     *
     * @return The description of the process.
     */
    String getDescription();

    /**
     * Return the array of the process keywords.
     *
     * @return The array of the process keywords.
     */
    String[] getKeywords();

    /**
     * Return the results of the process.
     *
     * @return A Map of the results with the output name as key and the output value as value.
     */
    Map<String, Object> getResults();
}