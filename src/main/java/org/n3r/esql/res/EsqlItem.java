package org.n3r.esql.res;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.n3r.core.lang.RClass;
import org.n3r.esql.parser.EsqlSubParser;
import org.n3r.esql.util.EsqlUtils;

public class EsqlItem {
    private String sqlId;
    private Map<String, String> sqlOptions = new HashMap<String, String>();
    private List<List<EsqlPart>> sqlRawSubs = new ArrayList<List<EsqlPart>>();
    private Class<?> returnType;
    private String onerr;
    private String split;

    public List<EsqlSub> createSqlSubs(Object bean) {
        return EsqlUtils.createSqlSubs(bean, sqlRawSubs, this);
    }

    public void addSqlParts(List<String> sqlLines) {
        this.sqlRawSubs.add(EsqlSubParser.parse(sqlLines));
    }

    public void setSqlId(String sqlId) {
        this.sqlId = sqlId;
    }

    public void setSqlOptions(Map<String, String> sqlOptions) {
        this.sqlOptions = sqlOptions;

        onerr = sqlOptions.get("onerr");
        returnType = RClass.findClass(sqlOptions.get("returnType"));
        if (returnType == null) returnType = RClass.findClass(sqlOptions.get("resultType"));

        split = sqlOptions.get("split");
        if (StringUtils.isEmpty(split)) split = ";";
    }

    public String getSqlId() {
        return sqlId;
    }

    public Map<String, String> getSqlOptions() {
        return sqlOptions;
    }

    public boolean isOnerrResume() {
        return StringUtils.equals("resume", onerr);
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public String getSqlSpitter() {
        return split;
    }
}
