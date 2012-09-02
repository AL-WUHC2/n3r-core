package org.n3r.esql.config;


public abstract class EsqlConfig implements EsqlConfigable {
    private String sqlfromdb;

    @Override
    public String getSqlfromdb() {
        return sqlfromdb;
    }

    public void setSqlfromdb(String sqlfromdb) {
        this.sqlfromdb = sqlfromdb;
    }

}
