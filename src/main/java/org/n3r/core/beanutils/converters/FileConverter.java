package org.n3r.core.beanutils.converters;

import java.io.File;

/**
 * {@link org.apache.commons.beanutils.Converter} implementaion that handles conversion
 * to and from <b>java.io.File</b> objects.
 * <p>
 * Can be configured to either return a <i>default value</i> or throw a
 * <code>ConversionException</code> if a conversion error occurs.
 *
 * @author James Strachan
 * @version $Revision: 690380 $ $Date: 2008-08-29 21:04:38 +0100 (Fri, 29 Aug 2008) $
 * @since 1.6
 */
public final class FileConverter extends AbstractConverter {

    /**
     * Construct a <b>java.io.File</b> <i>Converter</i> that throws
     * a <code>ConversionException</code> if an error occurs.
     */
    public FileConverter() {
        super();
    }

    /**
     * Construct a <b>java.io.File</b> <i>Converter</i> that returns
     * a default value if an error occurs.
     *
     * @param defaultValue The default value to be returned
     * if the value to be converted is missing or an error
     * occurs converting the value.
     */
    public FileConverter(Object defaultValue) {
        super(defaultValue);
    }

    /**
     * Return the default type this <code>Converter</code> handles.
     *
     * @return The default type this <code>Converter</code> handles.
     * @since 1.8.0
     */
    @Override
    protected Class getDefaultType() {
        return File.class;
    }

    /**
     * <p>Convert the input object into a java.io.File.</p>
     *
     * @param type Data type to which this value should be converted.
     * @param value The input value to be converted.
     * @return The converted value.
     * @throws Throwable if an error occurs converting to the specified type
     * @since 1.8.0
     */
    @Override
    protected Object convertToType(Class type, Object value) throws Throwable {
        return new File(value.toString());
    }
}
