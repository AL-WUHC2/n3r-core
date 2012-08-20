package org.n3r.core.beanutils.converters;

import org.n3r.core.beanutils.Converter;

/**
 * Provides a facade for {@link Converter} implementations
 * preventing access to any public API in the implementation,
 * other than that specified by {@link Converter}.
 * <p />
 * This implementation can be used to prevent registered {@link Converter}
 * implementations that provide configuration options from being
 * retrieved and modified.
 *
 * @version $Revision: 552084 $ $Date: 2007-06-30 04:04:13 +0100 (Sat, 30 Jun 2007) $
 * @since 1.8.0
 */
public final class ConverterFacade implements Converter {

    private final Converter converter;

    /**
     * Construct a converter which delegates to the specified
     * {@link Converter} implementation.
     *
     * @param converter The converter to delegate to
     */
    public ConverterFacade(Converter converter) {
        if (converter == null) { throw new IllegalArgumentException("Converter is missing"); }
        this.converter = converter;
    }

    /**
     * Convert the input object into an output object of the
     * specified type by delegating to the underlying {@link Converter}
     * implementation.
     *
     * @param type Data type to which this value should be converted
     * @param value The input value to be converted
     * @return The converted value.
     */
    public Object convert(Class type, Object value) {
        return converter.convert(type, value);
    }

    /**
     * Provide a String representation of this facade implementation
     * sand the underlying {@link Converter} it delegates to.
     *
     * @return A String representation of this facade implementation
     * sand the underlying {@link Converter} it delegates to
     */
    @Override
    public String toString() {
        return "ConverterFacade[" + converter.toString() + "]";
    }

}
