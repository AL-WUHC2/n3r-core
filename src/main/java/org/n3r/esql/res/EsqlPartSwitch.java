package org.n3r.esql.res;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.n3r.core.lang.RBean;
import org.n3r.core.lang.RStr;
import org.n3r.esql.util.EsqlUtils;

import com.googlecode.aviator.AviatorEvaluator;

public class EsqlPartSwitch implements EsqlPart {
    private String switchCondition;
    private List<EsqlCondition> cases = new ArrayList<EsqlCondition>();
    private EsqlCondition defaultCase;

    @Override
    public String getSqlPart(Object bean) {
        Map<String, Object> properties = RBean.beanToMap(bean);
        Object eval = AviatorEvaluator.execute(switchCondition, properties, true);
        String switchStr = "" + eval;

        for (EsqlCondition caseCond : cases)
            if (RStr.exists(switchStr, caseCond.getCondition()))
                return EsqlUtils.buildSql(bean, caseCond.getSqlParts());

        return defaultCase == null ? "" : EsqlUtils.buildSql(bean, defaultCase.getSqlParts());
    }

    public void setSwitchCondition(String condition) {
        switchCondition = condition;
    }

    public void addCase(EsqlCondition caseCondition) {
        cases.add(caseCondition);
    }

    public void setDefaultCase(EsqlCondition sqlCondition) {
        defaultCase = sqlCondition;
    }

}
