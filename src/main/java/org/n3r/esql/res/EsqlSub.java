package org.n3r.esql.res;

import java.util.ArrayList;
import java.util.List;

import org.n3r.esql.param.EsqlParamPlaceholder;
import org.n3r.esql.param.EsqlPlaceholderType;

import com.google.common.base.Throwables;

public class EsqlSub implements Cloneable {
    public static enum EsqlType {
        SELECT, UPDATE, INSERT, MERGE, DROP, CREATE, TRUNCATE
    }

    private EsqlItem esqlItem;
    private String sql;
    private int placeholderNum;
    private EsqlParamPlaceholder[] placeHolders;
    private EsqlPlaceholderType placeHolderType;
    private List<EsqlPart> sqlParts = new ArrayList<EsqlPart>();
    private EsqlType sqlType;
    private boolean lastSelectSql;
    private boolean willReturnOnlyOneRow;
    private Object[] extraBindParams;
    private EsqlDynamic esqlDynamic;

    @Override
    public EsqlSub clone() {
        try {
            return (EsqlSub) super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw Throwables.propagate(e);
        }
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getSqlId() {
        return esqlItem != null ? esqlItem.getSqlId() : "<AUTO>";
    }

    public void setPlaceHolders(EsqlParamPlaceholder[] placeHolders) {
        this.placeHolders = placeHolders;
    }

    public String getSql() {
        return sql;
    }

    public EsqlParamPlaceholder[] getPlaceHolders() {
        return placeHolders;
    }

    public void setPlaceHolderType(EsqlPlaceholderType placeHolderType) {
        this.placeHolderType = placeHolderType;
    }

    public EsqlPlaceholderType getPlaceHolderType() {
        return placeHolderType;
    }

    public int getPlaceholderNum() {
        return placeholderNum;
    }

    public void setPlaceholderNum(int placeholderNum) {
        this.placeholderNum = placeholderNum;
    }

    public EsqlItem getEsqlItem() {
        return esqlItem;
    }

    public void setEsqlItem(EsqlItem esqlItem) {
        this.esqlItem = esqlItem;
    }

    public void addSqlPart(EsqlPart sqlPart) {
        sqlParts.add(sqlPart);
    }

    public EsqlType getSqlType() {
        return sqlType;
    }

    public void setSqlType(EsqlType sqlType) {
        this.sqlType = sqlType;
    }

    public void setLastSelectSql(boolean lastSelectSql) {
        this.lastSelectSql = lastSelectSql;
    }

    public boolean isLastSelectSql() {
        return this.lastSelectSql;
    }

    public boolean isWillReturnOnlyOneRow() {
        return willReturnOnlyOneRow;
    }

    public void setWillReturnOnlyOneRow(boolean willReturnOnlyOneRow) {
        this.willReturnOnlyOneRow = willReturnOnlyOneRow;
    }

    public Object[] getExtraBindParams() {
        return extraBindParams;
    }

    public void setExtraBindParams(Object... extraBindParams) {
        this.extraBindParams = extraBindParams;
    }

    public void setEsqlDynamic(EsqlDynamic esqlDynamic) {
        this.esqlDynamic = esqlDynamic;
    }

    public EsqlDynamic getEsqlDynamic() {
        return esqlDynamic;
    }

}
