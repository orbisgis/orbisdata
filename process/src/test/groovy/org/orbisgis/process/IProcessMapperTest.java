/*
 * Bundle Process is part of the OrbisGIS platform
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
 * Process is distributed under LGPL 3 license.
 *
 * Copyright (C) 2018 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * Process is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Process is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Process. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.process;

import org.junit.jupiter.api.Test;
import org.orbisgis.process.api.ILinker;
import org.orbisgis.process.api.IProcess;
import org.orbisgis.process.api.IProcessMapper;
import org.orbisgis.process.api.check.ICheckDataBuilder;
import org.orbisgis.process.api.inoutput.IInOutPut;
import org.orbisgis.process.api.inoutput.IInput;
import org.orbisgis.process.api.inoutput.IOutput;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class dedicated to {@link IProcessMapper} interface.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public class IProcessMapperTest {

    /**
     * Test the {@link IProcessMapper#getVersion()} method.
     */
    @Test
    public void testGetVersion() {
        assertFalse(new DummyProcessMapper().getVersion().isPresent());
    }

    /**
     * Test the {@link IProcessMapper#getDescription()} method.
     */
    @Test
    public void testGetDescription() {
        assertFalse(new DummyProcessMapper().getDescription().isPresent());
    }

    /**
     * Test the {@link IProcessMapper#getKeywords()} method.
     */
    @Test
    public void testGetKeyword() {
        assertFalse(new DummyProcessMapper().getKeywords().isPresent());
    }

    /**
     * Test the {@link IProcessMapper#getIdentifier()} method.
     */
    @Test
    public void testGetIdentifier() {
        Pattern pattern = Pattern.compile("[\\da-f]{8}-[\\da-f]{4}-[\\da-f]{4}-[\\da-f]{4}-[\\da-f]{12}");
        Matcher matcher = pattern.matcher(new DummyProcessMapper().getIdentifier());
        assertTrue(matcher.find());
    }

    /**
     * Simple implementtion of the {@link IProcessMapper} interface.
     */
    private static class DummyProcessMapper implements IProcessMapper {


        @Override
        public ILinker link(IInOutPut... inOutPuts) {
            return null;
        }


        @Override
        public ICheckDataBuilder before(IProcess process) {
            return null;
        }


        @Override
        public ICheckDataBuilder after(IProcess process) {
            return null;
        }


        @Override
        public IProcessMapper newInstance() {
            return null;
        }

        @Override
        public boolean execute(LinkedHashMap<String, Object> inputDataMap) {
            return false;
        }


        @Override
        public Optional<String> getTitle() {
            return Optional.empty();
        }


        @Override
        public Map<String, Object> getResults() {
            return null;
        }


        @Override
        public List<IInput> getInputs() {
            return null;
        }


        @Override
        public List<IOutput> getOutputs() {
            return null;
        }

        @Override
        public boolean call(LinkedHashMap<String, Object> inputDataMap) {
            return false;
        }


        @Override
        public IProcessMapper copy() {
            return null;
        }
    }
}
