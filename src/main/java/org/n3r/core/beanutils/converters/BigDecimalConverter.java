package org.n3r.core.beanutils.converters;

import java.math.BigDecimal;

/**
 * {@link NumberConverter} implementation that handles conversion to
 * and from <b>java.math.BigDecimal</b> objects.
 * <p>
 * This implementation can be configured to handle conversion either
 * by using BigDecimal's default String conversion, or by using a Locale's pattern
 * or by specifying a format pattern. See the {@link NumberConverter}
 * documentation for further details.
 * <p>
 * Can be configured to either return a <i>default value</i> or throw a
 * <code>ConversionException</code> if a conversion error occurs.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 690380 $ $Date: 2008-08-29 21:04:38 +0100 (Fri, 29 Aug 2008) $
 * @since 1.3
 */
public final class BigDecimalConverter extends NumberConverter {

    /**
     * Construct a <b>java.math.BigDecimal</b> <i>Converter</i> that throws
     * a <code>ConversionException</code> if an error occurs.
     */
    public BigDecimalConverter() {
        super(true);
    }

    /**
     * Construct a <b>java.math.BigDecimal</b> <i>Converter</i> that returns
     * a default value if an error occurs.
     *
     * @param defaultValue The default value to be returned
     * if the value to be converted is missing or an error
     * occurs converting the value.
     */
    public BigDecimalConverter(Object defaultValue) {
        super(true, defaultValue);
    }

    /**
     * Return the default type this <code>Converter</code> handles.
     *
     * @return The default type this <code>Converter</code> handles.
     * @since 1.8.0
     */
    @Override
    protected Class getDefaultType() {
        return BigDecimal.class;
    }

}
