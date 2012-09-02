package org.n3r.esql.parser;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.n3r.core.lang.RBean;
import org.n3r.core.lang.RStr;
import org.n3r.esql.ex.EsqlExecuteException;
import org.n3r.esql.res.EsqlDynamic;
import org.n3r.esql.res.EsqlSub;

public class EsqlDynamicReplacer {
    public String repaceDynamics(EsqlSub subSql, Object[] dynamics) {
        if (dynamics != null && dynamics.length > 0 && subSql.getEsqlDynamic() == null)
            subSql.setEsqlDynamic(new EsqlDynamicParser().parseRawSql(subSql.getSql()));

        EsqlDynamic esqlDynamic = subSql.getEsqlDynamic();
        if (esqlDynamic == null) return subSql.getSql();

        List<String> sqlPieces = esqlDynamic.getSqlPieces();
        StringBuilder realSql = new StringBuilder(sqlPieces.get(0));

        switch (esqlDynamic.getPlaceholdertype()) {
        case AUTO_SEQ:
            for (int i = 1, ii = sqlPieces.size(); i < ii; ++i)
                realSql.append(getDynamicByIndex(dynamics, i - 1, subSql.getSqlId())).append(sqlPieces.get(i));
            break;
        case MANU_SEQ:
            for (int i = 1, ii = sqlPieces.size(); i < ii; ++i)
                realSql.append(findDynamicBySeq(dynamics, esqlDynamic, i - 1, subSql.getSqlId())).append(
                        sqlPieces.get(i));
            break;
        case VAR_NAME:
            for (int i = 1, ii = sqlPieces.size(); i < ii; ++i)
                realSql.append(findDynamicByName(dynamics, esqlDynamic, i - 1)).append(sqlPieces.get(i));
            break;
        default:
            break;
        }

        return realSql.toString();
    }

    private Object getDynamicByIndex(Object[] dynamics, int index, String sqlId) {
        if (index < dynamics.length)
            return dynamics[index];

        throw new EsqlExecuteException("[" + sqlId + "]执行过程中缺少动态替换参数");
    }

    private Object findDynamicBySeq(Object[] dynamics, EsqlDynamic esqlDynamic, int index, String sqlId) {
        return getDynamicByIndex(dynamics, esqlDynamic.getPlaceholders()[index - 1].getSeq() - 1, sqlId);
    }

    private Object findDynamicByName(Object[] dynamics, EsqlDynamic esqlDynamic, int index) {
        Object bean = dynamics[0];

        String varName = esqlDynamic.getPlaceholders()[index].getPlaceholder();
        String property = RBean.getPropertyQuietly(bean, varName);
        if (property == null) {
            String propertyName = RStr.convertUnderscoreNameToPropertyName(varName);
            if (!StringUtils.equals(propertyName, varName))
                property = RBean.getPropertyQuietly(bean, propertyName);
        }
        return property;
    }
}
