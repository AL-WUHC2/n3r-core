package org.n3r.esql.param;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.n3r.core.lang.RBean;
import org.n3r.core.lang.RDate;
import org.n3r.core.lang.RStr;
import org.n3r.esql.ex.EsqlExecuteException;
import org.n3r.esql.param.EsqlParamPlaceholder.InOut;
import org.n3r.esql.res.EsqlSub;
import org.n3r.esql.res.EsqlSub.EsqlType;

public class EsqlParamsBinder {
    private EsqlSub subSql;
    private Object[] params;
    private StringBuilder boundParams;
    private PreparedStatement ps;

    public String bindParams(PreparedStatement ps, EsqlSub subSql, Object[] params) {
        this.subSql = subSql;
        this.params = params;
        this.boundParams = new StringBuilder();
        this.ps = ps;

        if (params != null && params.length > 0)
            switch (subSql.getPlaceHolderType()) {
            case AUTO_SEQ:
                for (int i = 0; i < subSql.getPlaceholderNum(); ++i)
                    setParam(i, getParamByIndex(i));
                break;
            case MANU_SEQ:
                for (int i = 0; i < subSql.getPlaceholderNum(); ++i)
                    setParam(i, findParamBySeq(i + 1));
                break;
            case VAR_NAME:
                for (int i = 0; i < subSql.getPlaceholderNum(); ++i)
                    setParam(i, findParamByName(subSql, i));
                break;
            default:
                break;
            }

        bindExtraParams();

        return boundParams.toString();
    }

    private void bindExtraParams() {
        Object[] extraBindParams = subSql.getExtraBindParams();
        if (extraBindParams == null) return;

        for (int i = subSql.getPlaceholderNum(); i < extraBindParams.length; ++i)
            setParam(i, extraBindParams[i]);
    }

    private void setParam(int index, Object value) {
        try {
            setParamEx(index, value);
        }
        catch (SQLException e) {
            throw new EsqlExecuteException("set parameters fail", e);
        }
    }

    private void setParamEx(int index, Object value) throws SQLException {
        if (regiesterOut(index)) return;

        if (value instanceof Date) {
            java.sql.Date date = new java.sql.Date(((Date) value).getTime());
            ps.setDate(index + 1, date);
            boundParams.append('[').append(RDate.toDateTimeStr(date)).append(']');
        }
        else {
            ps.setObject(index + 1, value);
            boundParams.append('[').append(value).append(']');
        }

        regiesterOut(index);
    }

    private boolean regiesterOut(int index) throws SQLException {
        EsqlParamPlaceholder[] placeHolders = subSql.getPlaceHolders();
        if (index >= placeHolders.length) return false;

        InOut inOut = placeHolders[index].getInOut();
        if (subSql.getSqlType() == EsqlType.CALL && inOut != InOut.IN)
            ((CallableStatement) ps).registerOutParameter(index + 1, Types.VARCHAR);

        return inOut == InOut.OUT;
    }

    private Object findParamByName(EsqlSub subSql, int index) {
        Object bean = params[0];

        String varName = subSql.getPlaceHolders()[index].getPlaceholder();
        String property = RBean.getPropertyQuietly(bean, varName);
        if (property != null) return property;

        String propertyName = RStr.convertUnderscoreNameToPropertyName(varName);
        if (!StringUtils.equals(propertyName, varName))
            property = RBean.getPropertyQuietly(bean, propertyName);

        return property;
    }

    private Object getParamByIndex(int index) {
        EsqlParamPlaceholder[] placeHolders = subSql.getPlaceHolders();
        if (index < placeHolders.length && subSql.getSqlType() == EsqlType.CALL
                && placeHolders[index].getInOut() == InOut.OUT) return null;

        if (index < params.length)
            return params[index];

        throw new EsqlExecuteException("[" + subSql.getSqlId() + "]执行过程中缺少参数");
    }

    private Object findParamBySeq(int index) {
        return getParamByIndex(subSql.getPlaceHolders()[index - 1].getSeq() - 1);
    }
}
