package org.n3r.esql.param;

public class EsqlParamPlaceholder {
    private String placeholder;
    private EsqlPlaceholderType placeholderType;
    private int seq;

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public EsqlPlaceholderType getPlaceholderType() {
        return placeholderType;
    }

    public void setPlaceholderType(EsqlPlaceholderType placeholderType) {
        this.placeholderType = placeholderType;
    }

}
