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
package org.orbisgis.processmanager.inoutput;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.MissingMethodException;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.orbisgis.processmanagerapi.IProcess;
import org.orbisgis.processmanagerapi.inoutput.IInOutPut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the {@link IInOutPut} interface.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public abstract class InOutPut implements IInOutPut, GroovyObject {

    private static final Logger LOGGER = LoggerFactory.getLogger(InOutPut.class);

    /** {@link IProcess} of the input/output. */
    private IProcess process;
    /** Name of the input/output. */
    private String name;
    /** Type of the input/output. */
    private Class type;
    /** Title of the input/output. */
    private String title;
    /** Description of the input/output. */
    private String description;
    /** Keywords of the input/output. */
    private String[] keywords;
    /** Groovy metaclass */
    private MetaClass metaClass;

    /**
     * Main constructor.
     *
     * @param process {@link IProcess} of the input/output.
     * @param name Name of the input/output.
     */
    public InOutPut(IProcess process, String name){
        this.process = process;
        this.name = name;
        this.metaClass = InvokerHelper.getMetaClass(InOutPut.class);
    }

    public String getName(){
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public IProcess getProcess() {
        return process;
    }

    @Override
    public void setProcess(IProcess process) {
        this.process = process;
    }

    @Override
    public IInOutPut setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public IInOutPut setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public IInOutPut setKeywords(String[] keywords) {
        this.keywords = keywords;
        return this;
    }

    @Override
    public String[] getKeyWords() {
        return keywords;
    }

    @Override
    public IInOutPut setType(Class type) {
        this.type = type;
        return this;
    }

    @Override
    public Class getType() {
        return type;
    }

    @Override public String toString(){return name+":"+process.getIdentifier();}

    @Override
    public Object invokeMethod(String name, Object args) {
        try {
            return getMetaClass().invokeMethod(this, name, args);
        } catch (MissingMethodException e) {
            LOGGER.debug("Unable to find the '"+name+"' methods, trying with the getter");
            return getProperty(name);
        }
    }

    @Override
    public Object getProperty(String propertyName) {
        switch(propertyName.toLowerCase()){
            case "title":
                return getTitle();
            case "description":
                return getDescription();
            case "keywords":
                return getKeyWords();
            case "type":
                return getType();
            default:
                return metaClass.getProperty(this, propertyName);
        }
    }

    @Override
    public void setProperty(String propertyName, Object newValue) {
        switch(propertyName.toLowerCase()){
            case "title":
                setTitle(newValue.toString());
                break;
            case "description":
                setDescription(newValue.toString());
                break;
            case "keywords":
                if(newValue instanceof String[]) {
                    setKeywords((String[])newValue);
                }
                break;
            case "type":
                if(newValue instanceof Class) {
                    setType((Class)newValue);
                }
                break;
            default:
                metaClass.setProperty(this, propertyName, newValue);
                break;
        }
    }

    @Override
    public MetaClass getMetaClass() {
        return metaClass;
    }

    @Override
    public void setMetaClass(MetaClass metaClass) {
        this.metaClass = metaClass;
    }

    public Object methodMissing(String name, Object arg) {
        Object[] args = (Object[])arg;
        if(name.equalsIgnoreCase("title") && args.length == 1 && args[0] instanceof String){
            return setTitle(args[0].toString());
        }
        else if(name.equalsIgnoreCase("description") && args.length == 1 && args[0] instanceof String){
            return setDescription(args[0].toString());
        }
        else if(name.equalsIgnoreCase("keywords") && args.length == 1 && args[0] instanceof String[]){
            return setKeywords((String[])args[0]);
        }
        else if(name.equalsIgnoreCase("type") && args.length == 1 && args[0] instanceof Class){
            return setType((Class)args[0]);
        }
        return new MissingMethodException(name, InOutPut.class, args);
    }
}
