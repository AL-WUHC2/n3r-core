package org.n3r.esql.res;

import java.util.ArrayList;
import java.util.List;

public class EsqlCondition {
    private String condition;
    private List<EsqlPart> sqlParts = new ArrayList<EsqlPart>();

    public EsqlCondition(String conditionValue) {
        this.condition = conditionValue;
    }

    public String getCondition() {
        return condition;
    }

    public List<EsqlPart> getSqlParts() {
        return sqlParts;
    }

    public void addSqlPart(EsqlPart sqlPart) {
        this.sqlParts.add(sqlPart);
    }
}
