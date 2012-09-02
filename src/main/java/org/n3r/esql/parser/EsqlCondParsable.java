package org.n3r.esql.parser;

import java.util.List;

import org.n3r.esql.res.EsqlPart;

public interface EsqlCondParsable {

    int parseCondition(List<String> sqlLines, int index, String conditionStart);

    EsqlPart getSqlPart();

}
