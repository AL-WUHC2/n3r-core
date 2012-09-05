package org.n3r.esql.res;

import java.util.List;

import org.n3r.esql.param.EsqlParamPlaceholder;
import org.n3r.esql.param.EsqlPlaceholderType;

public class EsqlDynamic {
    private List<String> sqlPieces;
    private EsqlPlaceholderType placeholdertype;
    private EsqlParamPlaceholder[] placeholders;

    public void setSqlPieces(List<String> sqlPieces) {
        this.sqlPieces = sqlPieces;
    }

    public List<String> getSqlPieces() {
        return sqlPieces;
    }

    public EsqlPlaceholderType getPlaceholdertype() {
        return placeholdertype;
    }

    public void setPlaceholdertype(EsqlPlaceholderType placeholdertype) {
        this.placeholdertype = placeholdertype;
    }

    public EsqlParamPlaceholder[] getPlaceholders() {
        return placeholders;
    }

    public void setPlaceholders(EsqlParamPlaceholder[] placeholders) {
        this.placeholders = placeholders;
    }

}
