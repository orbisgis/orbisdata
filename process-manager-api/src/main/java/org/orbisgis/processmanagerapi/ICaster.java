package org.orbisgis.processmanagerapi;

/**
 * This interface define methods which are used to be able to cast process input/output data into the good type.
 * ICastElement can be dynamically add in order to extends the capabilities.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
public interface ICaster {

    /**
     * Add a ICastElement in order to extend the capabilities of the caster.
     *
     * @param clazz New class of the object.
     * @param castElement ICastElement able to cast new types.
     */
    void addCast(Class clazz, ICastElement castElement);

    /**
     *
     * Cast the given object into another one with the given class name.
     *
     * @param o Object to cast.
     * @param clazz New class of the object.
     *
     * @return The given object into another one with the given class name.
     */
    Object cast(Object o, Class clazz);
}
