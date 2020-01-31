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
 * ProcessManager is distributed under LGPL 3 license.
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
package org.orbisgis.orbisdata.processmanager.process;

import org.orbisgis.orbisdata.processmanager.api.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the {@link IProgressMonitor} interface.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (Lab-STICC UBS 2020)
 */
public class ProgressMonitor implements IProgressMonitor {

    private Logger logger;
    private int step;
    private int maximum;
    private List<IProgressMonitor> children;
    private boolean autoLog;
    private String name;
    private IProgressMonitor parent;

    public ProgressMonitor(String taskName, int maximum, boolean autoLog){
        this.name = taskName;
        this.maximum = maximum;
        this.step = 0;
        this.children = new ArrayList<>();
        this.autoLog = autoLog;
        this.logger = LoggerFactory.getLogger(ProgressMonitor.class);
        this.parent = null;
    }

    public ProgressMonitor(String taskName, int maximum){
        this(taskName, maximum, false);
    }

    private ProgressMonitor(IProgressMonitor parent, String taskName, int maximum, boolean autoLog){
        this(taskName, maximum, autoLog);
        this.parent = parent;
    }

    @Override
    public void incrementStep() {
        this.step++;
        if(autoLog) {
            if(parent != null) {
                parent.log();
            }
            else {
                log();
            }
        }
    }

    @Override
    public double getProgress() {
        double tot = 100.0 / getMaxStep() *(step + children.stream().map(IProgressMonitor::getProgress).reduce(0.0, Double::sum)/100);
        if(tot > 100){
            return 100;
        }
        return tot;
    }

    @Override
    public int getMaxStep() {
        return maximum;
    }

    @Override
    public IProgressMonitor getSubProgress(String taskName, int maximum) {
        IProgressMonitor pm = new ProgressMonitor(this, taskName, maximum, autoLog);
        children.add(pm);
        return pm;
    }

    @Override
    public void log() {
        if(parent == null) {
            logger.info((name != null ? name + " : " : "") + String.format("%.2f", getProgress()) + "/100.00");
        }
        else {
            parent.log();
        }
    }
}
