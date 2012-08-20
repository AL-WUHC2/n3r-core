package org.n3r.core.beanutils;

/**
 * <p>General purpose data type converter that can be registered and used
 * within the BeanUtils package to manage the conversion of objects from
 * one type to another.</p>
 *
 * <p>Converter subclasses bundled with the BeanUtils library are required
 * to be thread-safe, as users of the library may call conversion methods
 * from more than one thread simultaneously.</p>
 *
 * <p>Custom converter subclasses created by users of the library can be
 * non-thread-safe if the application using them is single-threaded. However
 * it is recommended that they be written in a thread-safe manner anyway.</p> 
 *
 * @author Craig McClanahan
 * @author Paulo Gaspar
 * @version $Revision: 555824 $ $Date: 2007-07-13 01:27:15 +0100 (Fri, 13 Jul 2007) $
 * @since 1.3
 */

public interface Converter {

    /**
     * Convert the specified input object into an output object of the
     * specified type.
     *
     * @param type Data type to which this value should be converted
     * @param value The input value to be converted
     * @return The converted value
     *
     * @exception ConversionException if conversion cannot be performed
     *  successfully
     */
    public Object convert(Class type, Object value);

}
