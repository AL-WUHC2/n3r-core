package org.n3r.esql.parser;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.n3r.core.lang.RStr;
import org.n3r.esql.ex.EsqlConfigException;
import org.n3r.esql.res.EsqlCondition;
import org.n3r.esql.res.EsqlPartIf;
import org.n3r.esql.res.EsqlPartLiteral;

public class EsqlIfParser extends EsqlCondParser {

    @Override
    public int parseCondition(List<String> sqlLines, int index, String conditionValue) {
        checkAndCompileCondition(index, conditionValue);

        EsqlPartIf esqlPart = new EsqlPartIf();
        super.esqlPart = esqlPart;

        EsqlCondition sqlCondition = new EsqlCondition(conditionValue);
        esqlPart.addCondition(sqlCondition);

        StringBuilder sql = new StringBuilder();
        int newIndex = index + 1;
        boolean elseReached = false;

        for (int lines = sqlLines.size(); newIndex < lines; ++newIndex) {
            EsqlCondStr condition = new EsqlCondStr(sqlLines.get(newIndex));

            if (!condition.isQuoted()) {
                sql.append(condition.getSqlLine()).append(' ');
                continue;
            }

            if (StringUtils.isNotBlank(sql)) {
                sqlCondition.addSqlPart(new EsqlPartLiteral(RStr.trimRight(sql.toString())));
                RStr.clear(sql);
            }

            if ("elseif".equalsIgnoreCase(condition.getConditionKey())) {
                checkElseIfState(index, elseReached);
                checkAndCompileCondition(newIndex, condition.getConditionValue());
                sqlCondition = new EsqlCondition(condition.getConditionValue());
            }
            else if ("else".equalsIgnoreCase(condition.getConditionKey())) {
                checkElseState(index, elseReached);
                elseReached = true;
                sqlCondition = new EsqlCondition(condition.getConditionValue());
            }
            else if ("end".equals(condition.getConditionKey())) {
                if (elseReached) esqlPart.setElse(sqlCondition);
                setEndReached();
                break;
            }
            else {
                EsqlCondParsable parser = EsqlCondFactory.getParser(newIndex, condition.getConditionKey());
                newIndex = parser.parseCondition(sqlLines, newIndex, condition.getConditionValue());
                sqlCondition.addSqlPart(parser.getSqlPart());
            }
        }

        checkEnd(index);

        return newIndex;
    }

    private void checkElseIfState(int index, boolean elseReached) {
        if (!elseReached) return;

        throw new EsqlConfigException(EsqlSqlParser.getErr(index, ", syntax error: <elseif> after <else>"));
    }

    private void checkElseState(int index, boolean elseReached) {
        if (!elseReached) return;

        throw new EsqlConfigException(EsqlSqlParser.getErr(index, ", syntax error: duplicate <else>"));
    }
}
