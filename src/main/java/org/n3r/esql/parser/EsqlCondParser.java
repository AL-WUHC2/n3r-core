package org.n3r.esql.parser;

import org.apache.commons.lang3.StringUtils;
import org.n3r.esql.ex.EsqlConfigException;
import org.n3r.esql.res.EsqlPart;

import com.googlecode.aviator.AviatorEvaluator;

public abstract class EsqlCondParser implements EsqlCondParsable {
    protected EsqlPart esqlPart;
    private boolean endReached = false;

    protected void checkEnd(int index) {
        if (endReached) return;

        throw new EsqlConfigException(EsqlSqlParser.getErr(index, ", syntax error: <end> not found"));
    }

    protected void checkAndCompileCondition(int index, String condionValue) {
        if (StringUtils.isEmpty(condionValue))
            throw new EsqlConfigException(EsqlSqlParser.getErr(index, " syntax error "));

        try {
            AviatorEvaluator.compile(condionValue, true);
        } catch (Exception e) {
            throw new EsqlConfigException(EsqlSqlParser.getErr(index, " syntax error "), e);
        }
    }

    @Override
    public EsqlPart getSqlPart() {
        return esqlPart;
    }

    protected void setEndReached() {
        endReached = true;
    }

}
