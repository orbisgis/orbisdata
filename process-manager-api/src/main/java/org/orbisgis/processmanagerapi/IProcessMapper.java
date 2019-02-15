package org.orbisgis.processmanagerapi;

import java.util.Map;
import java.util.Set;

public interface IProcessMapper {

    Set<String> getInputNames();
    Set<String> getOutputNames();
    Map<String, Class> getInputDefinitions();
    Map<String, Class> getOutputDefinitions();
    boolean execute(Map<String, Object> inputDataMap);
    Map<String, Object> getResults();
}
