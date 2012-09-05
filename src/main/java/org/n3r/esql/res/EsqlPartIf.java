package org.n3r.esql.res;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.n3r.core.lang.RBean;
import org.n3r.core.lang.RJavaScript;
import org.n3r.esql.ex.EsqlExecuteException;
import org.n3r.esql.util.EsqlUtils;

public class EsqlPartIf implements EsqlPart {
    public List<EsqlCondition> conditions = new ArrayList<EsqlCondition>();
    public EsqlCondition elsePart;

    public String getSqlPart(Object bean) {
        Map<String, Object> properties = RBean.beanToMap(bean);
        RJavaScript.createBinds(properties);
        for (EsqlCondition ifCondition : conditions) {
            Object ok = RJavaScript.eval(ifCondition.getCondition() );
            if (!(ok instanceof Boolean)) throw new EsqlExecuteException(ifCondition.getCondition()
                    + " is not a bool expr");
            if ((Boolean) ok) return EsqlUtils.buildSql(bean, ifCondition.getSqlParts());
        }

        return elsePart == null ? "" : EsqlUtils.buildSql(bean, elsePart.getSqlParts());
    }

    public void addCondition(EsqlCondition condition) {
        conditions.add(condition);
    }

    public void setElse(EsqlCondition esqlPart) {
        this.elsePart = esqlPart;
    }

}
