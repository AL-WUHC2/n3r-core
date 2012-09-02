package org.n3r.esql.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.n3r.core.lang.RStr;
import org.n3r.esql.res.EsqlPart;
import org.n3r.esql.res.EsqlPartLiteral;

public class EsqlSubParser {

    public static List<EsqlPart> parse(List<String> sqlLines) {
        StringBuilder sql = new StringBuilder();
        List<EsqlPart> sqlParts = new ArrayList<EsqlPart>();
        for (int i = 0, ii = sqlLines.size(); i < ii; ++i) {
            EsqlCondStr condition = new EsqlCondStr(sqlLines.get(i));

            if (!condition.isQuoted()) {
                sql.append(condition.getSqlLine()).append(' ');
                continue;
            }

            if (StringUtils.isNotBlank(sql)) {
                sqlParts.add(new EsqlPartLiteral(RStr.trimRight(sql.toString())));
                RStr.clear(sql);
            }

            EsqlCondParsable conditionParser = EsqlCondFactory.getParser(i, condition.getConditionKey());
            i = conditionParser.parseCondition(sqlLines, i, condition.getConditionValue());
            sqlParts.add(conditionParser.getSqlPart());
        }

        if (sql.length() > 0) sqlParts.add(new EsqlPartLiteral(sql.toString()));

        return sqlParts;
    }

}
