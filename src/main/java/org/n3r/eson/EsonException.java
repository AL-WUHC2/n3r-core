package org.n3r.eson;

public class EsonException extends RuntimeException {
    private static final long serialVersionUID = -1953372501501896069L;

    public EsonException(String message, Throwable x) {
        super(message, x);
    }

    public EsonException(String message) {
        super(message);
    }

}
