package org.n3r.esql.parser;

import org.apache.commons.lang3.StringUtils;
import org.n3r.core.lang.RJavaScript;
import org.n3r.esql.ex.EsqlConfigException;
import org.n3r.esql.res.EsqlPart;

public abstract class EsqlCondParser implements EsqlCondParsable {
    protected EsqlPart esqlPart;

    protected void checkEnd(int index, boolean endReached) {
        if (endReached) return;

        throw new EsqlConfigException(EsqlSqlParser.getErr(index, ", syntax error: <end> not found"));
    }

    protected void checkAndCompileCondition(int index, String condionValue) {
        if (StringUtils.isEmpty(condionValue)) throw new EsqlConfigException(
                EsqlSqlParser.getErr(index, " syntax error "));

        try {
            RJavaScript.compile(condionValue);
        }
        catch (Exception e) {
            throw new EsqlConfigException(EsqlSqlParser.getErr(index, " syntax error "), e);
        }
    }

    @Override
    public EsqlPart getSqlPart() {
        return esqlPart;
    }

}
