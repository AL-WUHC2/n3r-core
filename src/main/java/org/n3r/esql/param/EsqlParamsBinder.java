package org.n3r.esql.param;

import java.sql.PreparedStatement;

import org.apache.commons.lang3.StringUtils;
import org.n3r.core.lang.RBean;
import org.n3r.core.lang.RStr;
import org.n3r.esql.ex.EsqlExecuteException;
import org.n3r.esql.res.EsqlSub;
import org.n3r.esql.util.EsqlUtils;

public class EsqlParamsBinder {
    private EsqlSub subSql;
    private Object[] params;

    public String bindParams(PreparedStatement ps, EsqlSub subSql, Object[] params) {
        this.subSql = subSql;
        this.params = params;
        StringBuilder boundParams = new StringBuilder();

        if (params != null && params.length > 0)
            switch (subSql.getPlaceHolderType()) {
            case AUTO_SEQ:
                for (int i = 0; i < subSql.getPlaceholderNum(); ++i)
                    EsqlUtils.setParam(boundParams, ps, i + 1, getParamByIndex(i));

                break;
            case MANU_SEQ:
                for (int i = 0; i < subSql.getPlaceholderNum(); ++i)
                    EsqlUtils.setParam(boundParams, ps, i + 1, findParamBySeq(i + 1));
                break;
            case VAR_NAME:
                for (int i = 0; i < subSql.getPlaceholderNum(); ++i)
                    EsqlUtils.setParam(boundParams, ps, i + 1, findParamByName(subSql, i));
                break;
            default:
                break;
            }

        bindExtraParams(ps, subSql, boundParams);

        return boundParams.toString();
    }

    private void bindExtraParams(PreparedStatement ps, EsqlSub subSql, StringBuilder boundParams) {
        Object[] extraBindParams = subSql.getExtraBindParams();
        if (extraBindParams == null) return;

        for (int i = subSql.getPlaceholderNum(); i < extraBindParams.length; ++i)
            EsqlUtils.setParam(boundParams, ps, i + 1, extraBindParams[i]);
    }

    private Object findParamByName(EsqlSub subSql, int index) {
        Object bean = params[0];

        String varName = subSql.getPlaceHolders()[index].getPlaceholder();
        String property = RBean.getPropertyQuietly(bean, varName);
        if (property == null) {
            String propertyName = RStr.convertUnderscoreNameToPropertyName(varName);
            if (!StringUtils.equals(propertyName, varName))
                property = RBean.getPropertyQuietly(bean, propertyName);
        }
        return property;
    }

    private Object getParamByIndex(int index) {
        if (index < params.length)
            return params[index];

        throw new EsqlExecuteException("[" + subSql.getSqlId() + "]执行过程中缺少参数");
    }

    private Object findParamBySeq(int index) {
        return getParamByIndex(subSql.getPlaceHolders()[index - 1].getSeq() - 1);
    }
}
