package org.n3r.core.beanutils;

/**
 * <p>A <strong>ConversionException</strong> indicates that a call to
 * <code>Converter.convert()</code> has failed to complete successfully.
 *
 * @author Craig McClanahan
 * @author Paulo Gaspar
 * @since 1.3
 */

public class ConversionException extends RuntimeException {

    // ----------------------------------------------------------- Constructors

    /**
     * Construct a new exception with the specified message.
     *
     * @param message The message describing this exception
     */
    public ConversionException(String message) {

        super(message);

    }

    /**
     * Construct a new exception with the specified message and root cause.
     *
     * @param message The message describing this exception
     * @param cause The root cause of this exception
     */
    public ConversionException(String message, Throwable cause) {

        super(message);
        this.cause = cause;

    }

    /**
     * Construct a new exception with the specified root cause.
     *
     * @param cause The root cause of this exception
     */
    public ConversionException(Throwable cause) {

        super(cause.getMessage());
        this.cause = cause;

    }

    // ------------------------------------------------------------- Properties

    /**
     * The root cause of this <code>ConversionException</code>, compatible with
     * JDK 1.4's extensions to <code>java.lang.Throwable</code>.
     */
    protected Throwable cause = null;

    /**
     * Return the root cause of this conversion exception.
     * @return the root cause of this conversion exception
     */
    @Override
    public Throwable getCause() {
        return cause;
    }

}
