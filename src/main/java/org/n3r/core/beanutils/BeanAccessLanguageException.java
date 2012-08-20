package org.n3r.core.beanutils;

/** 
 * Thrown to indicate that the <em>Bean Access Language</em> cannot execute query
 * against given bean. This is a runtime exception and access langauges are encouraged
 * to subclass to create custom exceptions whenever appropriate.
 *
 * @author Robert Burrell Donkin
 * @since 1.7
 */

public class BeanAccessLanguageException extends IllegalArgumentException {

    // --------------------------------------------------------- Constuctors

    /** 
     * Constructs a <code>BeanAccessLanguageException</code> without a detail message.
     */
    public BeanAccessLanguageException() {
        super();
    }

    /**
     * Constructs a <code>BeanAccessLanguageException</code> without a detail message.
     * 
     * @param message the detail message explaining this exception
     */
    public BeanAccessLanguageException(String message) {
        super(message);
    }
}
