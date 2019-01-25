package org.orbisgis.processmanagerapi;

/**
 * This interface declare the cast methods dedicated to the casting of a specific class into another specific class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2019)
 */
@FunctionalInterface
public interface ICastElement {

    /**
     * Cast the given object into a new one in another class.
     *
     * @param object Object to cast.
     *
     * @return The new Object with another class if the cast has been successful, null otherwise.
     */
    Object cast(Object object);
}
