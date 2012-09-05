package org.n3r.esql.parser;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.n3r.core.lang.RStr;
import org.n3r.esql.ex.EsqlConfigException;
import org.n3r.esql.res.EsqlCondition;
import org.n3r.esql.res.EsqlPartLiteral;
import org.n3r.esql.res.EsqlPartSwitch;

public class EsqlSwitchParser extends EsqlCondParser {

    @Override
    public int parseCondition(List<String> sqlLines, int index, String conditionValue) {
        checkAndCompileCondition(index, conditionValue);

        EsqlPartSwitch sqlPart = new EsqlPartSwitch();
        this.esqlPart = sqlPart;

        sqlPart.setSwitchCondition(conditionValue);
        StringBuilder sql = new StringBuilder();
        int newIndex = index + 1;
        boolean defaultReached = false;
        boolean endReached = false;
        EsqlCondition sqlCondition = null;

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

            if ("case".equalsIgnoreCase(condition.getConditionKey())) {
                if (sqlCondition == null && newIndex != index + 1) throw new EsqlConfigException(
                        EsqlSqlParser.getErr(index, ", syntax error: <switch expr> must follow <case value...>"));

                if (defaultReached) throw new EsqlConfigException(
                        EsqlSqlParser.getErr(index, ", syntax error: <case value...> must before <default>"));

                sqlCondition = new EsqlCondition(condition.getConditionValue());
                sqlPart.addCase(sqlCondition);
                RStr.clear(sql);
            }
            else if ("default".equals(condition.getConditionKey())) {
                defaultReached = true;
                sqlCondition = new EsqlCondition(condition.getConditionValue());
                sqlPart.setDefaultCase(sqlCondition);
            }
            else if ("end".equals(condition.getConditionKey())) {
                endReached = true;
                break;
            }
            else if (sqlCondition != null) {
                EsqlCondParsable parser = EsqlCondFactory.getParser(newIndex, condition.getConditionKey());
                newIndex = parser.parseCondition(sqlLines, newIndex, condition.getConditionValue());
                sqlCondition.addSqlPart(parser.getSqlPart());
            }
        }

        checkEnd(index, endReached);

        return newIndex;
    }
}
