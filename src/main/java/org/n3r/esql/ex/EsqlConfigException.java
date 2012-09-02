package org.n3r.esql.ex;


public class EsqlConfigException extends EsqlException {

    public EsqlConfigException(String msg) {
        super(msg);
    }

    public EsqlConfigException(String msg, Throwable e) {
        super(msg, e);
    }

    public EsqlConfigException(Throwable e) {
        super(e);
    }

}
