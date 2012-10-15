package org.n3r.esql.parser;

import org.n3r.core.lang.RStr;

public class EsqlCondStr {

    private String sqlLine;
    private boolean quoted;
    private String condition;
    private String conditionKey;
    private String conditionValue;

    public EsqlCondStr(String sqlLine) {
        this.sqlLine = sqlLine;
    }

    public boolean isQuoted() {
        String trimedSqlLine = sqlLine.trim();
        quoted = RStr.isQuoted(trimedSqlLine, "<", ">");
        condition = trimedSqlLine;

        if (quoted) {
            condition = trimedSqlLine.substring(1, trimedSqlLine.length() - 1).trim();
            int posOfBlank = RStr.indexOfBlank(condition);
            if (posOfBlank > 0) {
                conditionKey = condition.substring(0, posOfBlank);
                conditionValue = condition.substring(posOfBlank).trim();
            }
            else {
                conditionKey = condition;
                conditionValue = "";
            }
        }

        return quoted;
    }

    public String getCondition() {
        return condition;
    }

    public String getSqlLine() {
        return sqlLine;
    }

    public String getConditionKey() {
        return conditionKey;
    }

    public String getConditionValue() {
        return conditionValue;
    }
}
