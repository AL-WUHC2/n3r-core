package org.n3r.esql.ex;

public class EsqlConfigException extends EsqlException {
    private static final long serialVersionUID = -654333785604100786L;

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
