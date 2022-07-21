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

import org.orbisgis.process.api.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of the {@link IProgressMonitor} interface.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC 2020)
 */
public class ProgressMonitor implements IProgressMonitor {

    /**
     * Actual step of the progression.
     */
    private int step;
    /**
     * Maximum step count.
     */
    private final int maximum;
    /**
     * True if the progression should be logged, false otherwise.
     */
    private final boolean autoLog;
    /**
     * {@link Logger} used to log progression.
     */
    private final Logger logger;
    /**
     * List of child.
     */
    private final List<IProgressMonitor> children;
    /**
     * Name of the task.
     */
    private final String name;
    /**
     * Reference to the parent {@link IProgressMonitor}
     */
    private final IProgressMonitor parent;
    /**
     * Indicates if the progression has end.
     */
    private boolean end;

    /**
     * Returns a child {@link IProgressMonitor} with the given name as task name, the given maximum as maximum
     * step count and logging the progression if autoLog to true.
     *
     * @param parent Parent {@link IProgressMonitor}.
     * @param taskName Name of the task.
     * @param maximum Maximum step number.
     * @param autoLog Log the progression if true, otherwise do not log.
     */
    public ProgressMonitor(IProgressMonitor parent, String taskName, int maximum, boolean autoLog){
        this.name = taskName == null ? "task_" + UUID.randomUUID() : taskName;
        this.maximum = maximum;
        this.step = 0;
        this.children = new ArrayList<>();
        this.autoLog = autoLog;
        this.logger = LoggerFactory.getLogger(ProgressMonitor.class);
        this.parent = parent;
        this.end = false;
    }

    /**
     * Creates a child {@link IProgressMonitor} with the given name as task name, the given maximum as maximum
     * step count and logging the progression if autoLog to true.
     *
     * @param taskName Name of the task.
     * @param maximum Maximum step number.
     * @param autoLog Log the progression if true, otherwise do not log.
     */
    public ProgressMonitor(String taskName, int maximum, boolean autoLog){
        this(null, taskName, maximum, autoLog);
    }

    /**
     * Creates a child {@link IProgressMonitor} with the given maximum as maximum step count and logging the
     * progression if autoLog to true.
     *
     * @param maximum Maximum step number.
     * @param autoLog Log the progression if true, otherwise do not log.
     */
    public ProgressMonitor(int maximum, boolean autoLog){
        this(null, null, maximum, autoLog);
    }

    /**
     * Creates a child {@link IProgressMonitor} with the given name as task name, without maximum step count and
     * logging the progression if autoLog to true.
     *
     * @param taskName Name of the task.
     * @param autoLog Log the progression if true, otherwise do not log.
     */
    public ProgressMonitor(String taskName, boolean autoLog){
        this(null, taskName, -1, autoLog);
    }

    /**
     * Creates a child {@link IProgressMonitor} with the given name as task name and the given maximum as maximum
     * step count.
     *
     * @param taskName Name of the task.
     * @param maximum Maximum step number.
     */
    public ProgressMonitor(String taskName, int maximum){
        this(null, taskName, maximum, false);
    }

    /**
     * Creates a child {@link IProgressMonitor} with the given name as task name, without maximum step count.
     *
     * @param taskName Name of the task.
     */
    public ProgressMonitor(String taskName){
        this(null, taskName, -1, false);
    }

    /**
     * Creates a child {@link IProgressMonitor} with the given maximum as maximum step count.
     *
     * @param maximum Maximum step number.
     */
    public ProgressMonitor(int maximum){
        this(null, null, maximum, false);
    }

    /**
     * Creates a child {@link IProgressMonitor} without maximum step count, logging the progression if autoLog to true.
     *
     * @param autoLog Log the progression if true, otherwise do not log.
     */
    public ProgressMonitor(boolean autoLog){
        this(null, null, -1, autoLog);
    }

    /**
     * Creates a child {@link IProgressMonitor} without maximum step count.
     */
    public ProgressMonitor(){
        this(null, null, -1, false);
    }

    @Override
    public void incrementStep() {
        if(step < maximum) {
            this.step++;
        }
        if(maximum != -1 && step >= maximum){
            end = true;
        }
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
        if(end){
            return 100;
        }
        if(maximum == -1){
            return -1;
        }
        if(children.stream().map(IProgressMonitor::getProgress).anyMatch(d -> d==-1)){
            return -1;
        }
        return 100.0 /
                (children.size() + maximum) *
                (children.stream().map(IProgressMonitor::getProgress).reduce(0.0, Double::sum)/100 +step);
    }

    @Override
    public int getMaxStep() {
        return maximum;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public IProgressMonitor getSubProgress(String taskName) {
        return getSubProgress(taskName, -1, autoLog);
    }

    @Override
    public IProgressMonitor getSubProgress(boolean autoLog) {
        return getSubProgress(null, -1, autoLog);
    }

    @Override
    public IProgressMonitor getSubProgress(int maximum) {
        return getSubProgress(null, maximum, autoLog);
    }

    @Override
    public IProgressMonitor getSubProgress(String taskName, int maximum) {
        return getSubProgress(taskName, maximum, autoLog);
    }

    @Override
    public IProgressMonitor getSubProgress(String taskName, boolean autoLog) {
        return getSubProgress(taskName, -1, autoLog);
    }

    @Override
    public IProgressMonitor getSubProgress(int maximum, boolean autoLog) {
        return getSubProgress(null, maximum, autoLog);
    }

    @Override
    public IProgressMonitor getSubProgress(String taskName, int maximum, boolean autoLog) {
        IProgressMonitor pm = new ProgressMonitor(this, taskName, maximum, autoLog);
        children.add(pm);
        return pm;
    }

    @Override
    public void log() {
        if(parent == null) {
            logger.info(name + " : " + String.format("%.2f", getProgress()) + "/100.00");
        }
        else {
            parent.log();
        }
    }

    @Override
    public void end() {
        end = true;
        if(autoLog){
            log();
        }
    }
}
