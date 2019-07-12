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
package org.orbisgis.processmanager.check;

import groovy.lang.Closure;
import groovy.lang.GroovyShell;
import org.junit.jupiter.api.Test;
import org.orbisgis.processmanager.Process;
import org.orbisgis.processmanager.ProcessManager;
import org.orbisgis.processmanager.ProcessMapper;
import org.orbisgis.processmanagerapi.check.ICheckClosureBuilder;
import org.orbisgis.processmanagerapi.check.ICheckDataBuilder;
import org.orbisgis.processmanagerapi.check.ICheckOptionBuilder;
import org.orbisgis.processmanagerapi.check.IProcessCheck;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class dedicated to {@link CheckOptionBuilder} class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public class CheckOptionBuilderTest {

    /**
     * Test the building of the check options.
     */
    @Test
    public void buildingTest(){
        ICheckDataBuilder dataBuilder = new ProcessMapper().after(null);
        assertNotNull(dataBuilder);

        ICheckClosureBuilder closureBuilder = dataBuilder.with("toto", "tata");
        assertNotNull(closureBuilder);

        ICheckOptionBuilder optionBuilder = closureBuilder.check((Closure)new GroovyShell().evaluate("({true})"));
        assertNotNull(optionBuilder);

        optionBuilder = optionBuilder.continueOnFail("continue");
        assertNotNull(optionBuilder);

        optionBuilder = optionBuilder.continueOnSuccess("continue");
        assertNotNull(optionBuilder);

        optionBuilder = optionBuilder.stopOnFail("continue");
        assertNotNull(optionBuilder);

        optionBuilder = optionBuilder.stopOnSuccess("continue");
        assertNotNull(optionBuilder);
    }

    /**
     * Test the {@link ProcessCheck} class.
     */
    @Test
    public void processCheckTest(){
        ProcessCheck processCheck = new ProcessCheck(null);
        assertNull(processCheck.getProcess());

        processCheck.setClosure(null);
        ProcessCheck finalProcessCheck = processCheck;
        assertThrows(IllegalStateException.class, () -> finalProcessCheck.run(null));

        processCheck.setClosure((Closure)new GroovyShell().evaluate("({1+1})"));
        ProcessCheck finalProcessCheck2 = processCheck;
        assertThrows(IllegalStateException.class, () -> finalProcessCheck2.run(null));

        LinkedHashMap<String, Object> inputs = new LinkedHashMap<>();
        inputs.put("toto", String.class);
        LinkedHashMap<String, Object> outputs = new LinkedHashMap<>();
        outputs.put("tata", String.class);
        LinkedHashMap<String, Object> data = new LinkedHashMap<>();
        data.put("toto", "tata");
        data.put("tata", "toto");
        Process process = (Process)ProcessManager.createFactory().create().inputs(inputs).outputs(outputs).getProcess();
        processCheck = new ProcessCheck(process);
        assertEquals(process.getIdentifier(), processCheck.getProcess().getIdentifier());

        ProcessCheck finalProcessCheck1 = processCheck;
        processCheck.onFail(IProcessCheck.CONTINUE, "continue fail");
        processCheck.fail();
        processCheck.onFail(IProcessCheck.STOP, "stop fail");
        assertThrows(IllegalStateException.class, finalProcessCheck1::fail);
        processCheck.onSuccess(IProcessCheck.STOP, "continue success");
        assertThrows(IllegalStateException.class, finalProcessCheck1::success);
        processCheck.onSuccess(IProcessCheck.CONTINUE, "continue success");
        processCheck.success();

        processCheck.setInOutputs(process.getProperty("toto"), process.getProperty("tata"));
        processCheck.setClosure((Closure)new GroovyShell().evaluate("({a, b ->b == 'tata'})"));
        processCheck.run(data);

        processCheck = new ProcessCheck(process);
        processCheck.onFail(IProcessCheck.STOP, "stop fail");
        processCheck.onSuccess(IProcessCheck.CONTINUE, "continue success");
        processCheck.setInOutputs(process.getProperty("toto"), process.getProperty("tata"));
        processCheck.setClosure((Closure)new GroovyShell().evaluate("({a, b -> b != 'tata'})"));
        ProcessCheck finalProcessCheck3 = processCheck;
        assertThrows(IllegalStateException.class, () -> finalProcessCheck3.run(data));
    }
}
