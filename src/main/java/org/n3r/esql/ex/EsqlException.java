package org.n3r.esql.ex;

public class EsqlException extends RuntimeException {
    private static final long serialVersionUID = -8224740965898901169L;

    public EsqlException(String msg) {
        super(msg);
    }

    public EsqlException(Throwable e) {
        super(e);
    }

    public EsqlException(String msg, Throwable e) {
        super(msg, e);
    }

}
