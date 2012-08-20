package org.n3r.core.beanutils.converters;

import java.util.Date;

/**
 * {@link DateTimeConverter} implementation that handles conversion to
 * and from <b>java.util.Date</b> objects.
 * <p>
 * This implementation can be configured to handle conversion either
 * by using a Locale's default format or by specifying a set of format
 * patterns (note, there is no default String conversion for Date).
 * See the {@link DateTimeConverter} documentation for further details.
 * <p>
 * Can be configured to either return a <i>default value</i> or throw a
 * <code>ConversionException</code> if a conversion error occurs.
 *
 * @version $Revision: 640131 $
 * @since 1.8.0
 */
public final class DateConverter extends DateTimeConverter {

    /**
     * Construct a <b>java.util.Date</b> <i>Converter</i> that throws
     * a <code>ConversionException</code> if an error occurs.
     */
    public DateConverter() {
        super();
    }

    /**
     * Construct a <b>java.util.Date</b> <i>Converter</i> that returns
     * a default value if an error occurs.
     *
     * @param defaultValue The default value to be returned
     * if the value to be converted is missing or an error
     * occurs converting the value.
     */
    public DateConverter(Object defaultValue) {
        super(defaultValue);
    }

    /**
     * Return the default type this <code>Converter</code> handles.
     *
     * @return The default type this <code>Converter</code> handles.
     */
    protected Class getDefaultType() {
        return Date.class;
    }

}
