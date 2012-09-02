package org.n3r.esql.ex;


public class EsqlExecuteException extends EsqlException {

    public EsqlExecuteException(String msg) {
        super(msg);
    }

    public EsqlExecuteException(Throwable e) {
        super(e);
    }

    public EsqlExecuteException(String msg, Throwable e) {
        super(msg, e);
    }

}
