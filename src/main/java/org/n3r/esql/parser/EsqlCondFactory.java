package org.n3r.esql.parser;

import org.n3r.esql.ex.EsqlConfigException;

public class EsqlCondFactory {

    public static EsqlCondParsable getParser(int index, String conditionKey) {
        if ("if".equalsIgnoreCase(conditionKey)) return new EsqlIfParser();
        if ("switch".equalsIgnoreCase(conditionKey)) return new EsqlSwitchParser();

        throw new EsqlConfigException(EsqlSqlParser.getErr(index, " syntax error "));
    }

}
