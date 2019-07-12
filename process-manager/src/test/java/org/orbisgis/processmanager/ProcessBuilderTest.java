/*
 * Bundle ProcessManager is part of the OrbisGIS platform
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
 * ProcessManager is distributed under GPL 3 license.
 *
 * Copyright (C) 2018 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * ProcessManager is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * ProcessManager is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * ProcessManager. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.processmanager;

import groovy.lang.Closure;
import groovy.lang.GroovyShell;
import org.junit.jupiter.api.Test;
import org.orbisgis.processmanagerapi.IProcess;
import org.orbisgis.processmanagerapi.IProcessFactory;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class dedicated to {@link ProcessBuilder} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public class ProcessBuilderTest {

    @Test
    void testProcessBuilder(){

        LinkedHashMap<String, Object> inputs = new LinkedHashMap<>();
        inputs.put("in1", String.class);
        inputs.put("in2", String.class);
        inputs.put("in3", String.class);

        LinkedHashMap<String, Object> outputs = new LinkedHashMap<>();
        outputs.put("out1", String.class);
        outputs.put("out2", String.class);

        IProcessFactory factory = ProcessManager.createFactory();
        IProcess process = new ProcessBuilder(factory, factory).title("title").description("description")
                .keywords(new String[]{"key1", "key2"}).inputs(inputs).outputs(outputs).run(null)
                .version("version").getProcess();

        assertEquals("title", process.getTitle());
        assertEquals("description", process.getDescription());
        assertEquals("version", process.getVersion());
        assertArrayEquals(new String[]{"key1", "key2"}, process.getKeywords());
        assertEquals(3, process.getInputs().size());
        assertEquals(2, process.getOutputs().size());
    }
}
